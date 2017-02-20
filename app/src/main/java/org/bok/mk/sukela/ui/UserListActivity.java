package org.bok.mk.sukela.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import org.bok.mk.sukela.R;
import org.bok.mk.sukela.entry.EntryList;
import org.bok.mk.sukela.helper.CommonOps;
import org.bok.mk.sukela.helper.Contract;
import org.bok.mk.sukela.helper.Meta;
import org.bok.mk.sukela.helper.ReturnCodes;
import org.bok.mk.sukela.helper.T;
import org.bok.mk.sukela.helper.network.NetworkManager;
import org.bok.mk.sukela.source.EksiSozluk;
import org.bok.mk.sukela.source.UserManager;
import org.bok.mk.sukela.ui.entryscreen.asyncdownloader.MultiplePageDownloadTask;

import java.io.IOException;
import java.util.List;

public class UserListActivity extends AppCompatActivity implements UserListFragment.UserDeleteListener
{
    private UserListFragment mFragment;
    private Meta META;
    private TextView mNoUserWaning;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Bundle b = getIntent().getExtras();
        this.META = (Meta) b.getSerializable(Contract.META);
        setUserScreenTheme();
        setContentView(R.layout.activity_user_list);

        mFragment = (UserListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);

        setTitle(getResources().getString(R.string.yazarlar));
        initComponents();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private void setUserScreenTheme()
    {
        setTheme(R.style.UserListTheme);
        // Status bar is painted to black
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.BLACK);
        }
    }

    private void initComponents()
    {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.bacground1_light)));
        fab.setRippleColor(ContextCompat.getColor(this, R.color.blue));
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                displayAddUserDialog();

            }
        });

        mNoUserWaning = (TextView) findViewById(R.id.no_user);
        if (mFragment.getCurrentUserCount() > 0)
        {
            mNoUserWaning.setVisibility(View.GONE);
        }
    }

    private void displayAddUserDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Yazar adı");
        final EditText input = new EditText(this);
        input.setGravity(Gravity.CENTER);
        builder.setView(input);
        builder.setPositiveButton("Tamam", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                CommonOps.hideSoftKeyboard(input, UserListActivity.this);
                String name = input.getText().toString().trim();
                addUser(name);
                dialog.cancel();
            }
        });
        builder.setNegativeButton("İptal", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                dialog.cancel();
                CommonOps.hideSoftKeyboard(input, UserListActivity.this);
            }
        });

        builder.create().show();

        CommonOps.showSoftKeyboard(UserListActivity.this);
    }

    private void addUser(String name)
    {
        boolean alreadyAdded = mFragment.checkIfUserExists(name);
        if (alreadyAdded)
        {
            T.toast(this, "Bu yazar zaten eklenmiş.");
            return;
        }

        UserDownloader userDownloader = new UserDownloader(name, this);
        userDownloader.execute();
    }

    @Override
    public void userDeleted(String deleteUserName, int remainingUserCount)
    {
        if (remainingUserCount == 0)
        {
            mNoUserWaning.setVisibility(View.VISIBLE);
        }
    }

    protected class UserDownloader extends MultiplePageDownloadTask
    {
        final String mUserName;
        private EksiSozluk sozluk = new EksiSozluk(UserListActivity.this, META);

        UserDownloader(String userName, Activity a)
        {
            super(a);
            super.init(sozluk, META);
            this.mUserName = userName;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Void... args)
        {
            EksiSozluk sozluk = new EksiSozluk(UserListActivity.this, META);
            updateProgressBarMessage(mUserName + " aranıyor...");

            // internet var mı kontrol et
            boolean isOnline = NetworkManager.isOnline(UserListActivity.this);
            if (!isOnline)
            {
                return ReturnCodes.OFFLINE;
            }

            List<String> entryIds;
            try
            {
                entryIds = UserManager.getEntryNumbersFromUserPage(META, mUserName);
            }
            catch (IOException e)
            {
                return ReturnCodes.ENTRY_LIST_DOWNLOAD_FAILED;
            }

            mHorizontalProgressDialog.setMax(entryIds.size());

            try
            {
                if (entryIds.isEmpty())
                {
                    return ReturnCodes.UNPOPULAR_USER;
                }

                EntryList entryList = sozluk.downloadEntries(entryIds, this);
                publishProgress(entryIds.size());
                updateProgressBarMessage("Entriler kaydediliyor...");
                mSavedEntryCount = sozluk.saveEntriesToLocalStorage(entryList.unmodifiableList());
                return ReturnCodes.SUCCESS_DOWNLOAD;
            }
            catch (IOException e)
            {
                return ReturnCodes.ENTRY_DOWNLOAD_FAILED;
            }
        }

        @Override
        protected void onPostExecute(Integer result)
        {
            super.onPostExecute(result);

            if (result == ReturnCodes.SUCCESS_DOWNLOAD)
            {
                mNoUserWaning.setVisibility(View.GONE);
                mFragment.updateUiForUser(mUserName);
                T.toast(UserListActivity.this, mSavedEntryCount + " adet entry kaydedildi.");
            }
            else if (result == ReturnCodes.ENTRY_LIST_DOWNLOAD_FAILED)
            {
                T.toast(UserListActivity.this, "Entry listesi okunamadı.");
            }
            else if (result == ReturnCodes.UNPOPULAR_USER)
            {
                T.toast(UserListActivity.this, "Laf aramızda bu yazar pek sevilmiyor.");
            }
            else if (result == ReturnCodes.ENTRY_DOWNLOAD_FAILED)
            {
                T.toast(UserListActivity.this, "Entryler indirilemedi. Ekşi sözlük erişimi kısıtlanmış olabilir.");
            }

            mHorizontalProgressDialog.dismiss();
        }
    }
}
