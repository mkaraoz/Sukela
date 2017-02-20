package org.bok.mk.sukela.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.bok.mk.sukela.R;
import org.bok.mk.sukela.adapter.CircularRowRvAdapter;
import org.bok.mk.sukela.data.LocalDbManager;
import org.bok.mk.sukela.helper.CircularRowData;
import org.bok.mk.sukela.helper.CommonOps;
import org.bok.mk.sukela.helper.Contract;
import org.bok.mk.sukela.helper.IntentHelper;
import org.bok.mk.sukela.helper.Meta;
import org.bok.mk.sukela.helper.T;
import org.bok.mk.sukela.helper.callbacks.CircularRowClickCallback;
import org.bok.mk.sukela.source.UserManager;
import org.bok.mk.sukela.ui.entryscreen.UserEntryScreenActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInRightAnimator;

/**
 * Created by mk on 04.09.2016.
 */
public class UserListFragment extends Fragment implements CircularRowClickCallback
{
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<CircularRowData> mCircularRowData;
    private CircularRowRvAdapter mAdapter;
    List<String> mCurrentUsers;
    private Meta META;

    public UserListFragment()
    {
    }

    public int getCurrentUserCount()
    {
        return mCurrentUsers.size();
    }

    private void initDataset()
    {
        mCurrentUsers = UserManager.getSavedUsers(getActivity(), META.getDataUri());
        mCircularRowData = new ArrayList<>();
        String color = CommonOps.getColorString(R.color.blue, getActivity());
        for (String userName : mCurrentUsers)
        {
            CircularRowData row = CircularRowData.createSameColor(color, userName.substring(0, 1).toUpperCase(), userName, Contract.TAG_USER);
            mCircularRowData.add(row);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.user_list_fragment, container, false);
        Bundle args = getActivity().getIntent().getExtras();
        if (args != null)
        {
            this.META = (Meta) args.getSerializable(Contract.META);
            initDataset();
            mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv);
            mRecyclerView.setItemAnimator(new SlideInRightAnimator());
            mLayoutManager = new LinearLayoutManager(getActivity());
            setRecyclerViewLayoutManager();
            mAdapter = new CircularRowRvAdapter(mCircularRowData, this);
            mRecyclerView.setAdapter(mAdapter);
        }
        return rootView;
    }

    public void setRecyclerViewLayoutManager()
    {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (mRecyclerView.getLayoutManager() != null)
        {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        }

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
    }

    @Override
    public void rowClicked(String tag, String user)
    {
        Intent intent = IntentHelper.createIntentWithType(UserEntryScreenActivity.class, META, getActivity());
        intent.putExtra(Contract.TAG_USER, user);
        startActivity(intent);
    }

    @Override
    public void rowLongClicked(String tag, final String userName)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Sil");
        builder.setMessage(getString(R.string.delete_user, userName));

        builder.setPositiveButton("Sil", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                deleteUser(userName);
            }
        });

        builder.setNegativeButton("Yok", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                T.toast(getActivity(), "Peki.");
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteUser(String userName)
    {
        LocalDbManager manager = new LocalDbManager(getActivity());

        Uri uri = META.getDataUri();
        if (META.getTag().equals(Contract.TAG_USER))
        {
            uri = Uri.withAppendedPath(uri, userName);
        }

        int deletedEntryCount = manager.deleteEntries(uri);
        int index = mCurrentUsers.indexOf(userName);
        mCurrentUsers.remove(index);
        mAdapter.removeItem(index);
        T.toast(getActivity(), userName + " ve " + deletedEntryCount + " adet entrisi silindi.");

        mUserDeleteListener.userDeleted(userName, mCurrentUsers.size());
    }

    public boolean checkIfUserExists(String name)
    {
        return mCurrentUsers.contains(name);
    }

    public void updateUiForUser(String userName)
    {
        mCurrentUsers.add(userName);
        Collections.sort(mCurrentUsers);
        int index = mCurrentUsers.indexOf(userName);
        String color = CommonOps.getColorString(R.color.blue, getActivity());
        CircularRowData row = CircularRowData.createSameColor(color, userName.substring(0, 1).toUpperCase(), userName, Contract.TAG_USER);
        mCircularRowData.add(index, row);
        mAdapter.notifyItemInserted(index);
        mRecyclerView.getLayoutManager().scrollToPosition(index);
    }

    public interface UserDeleteListener
    {
        void userDeleted(String deleteUserName, int remainingUserCount);
    }

    private static final UserDeleteListener sDummyCallbacks = new UserDeleteListener()
    {
        @Override
        public void userDeleted(String deleteUserName, int remainingUserCount)
        {
        }
    };

    @Override
    public void onAttach(Context activity)
    {
        super.onAttach(activity);
        try
        {
            mUserDeleteListener = (UserDeleteListener) activity;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString() + " must implement EntryListFragmentListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mUserDeleteListener = sDummyCallbacks;
    }

    private UserDeleteListener mUserDeleteListener = sDummyCallbacks;
}
