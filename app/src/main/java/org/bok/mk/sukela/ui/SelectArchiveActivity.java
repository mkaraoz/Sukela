/*
package org.bok.mk.sukela.ui;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.bok.mk.sukela.R;
import org.bok.mk.sukela.adapter.CircularRowRvAdapter;
import org.bok.mk.sukela.helper.CircularRowData;
import org.bok.mk.sukela.helper.callbacks.CircularRowClickCallback;
import org.bok.mk.sukela.helper.CommonOps;
import org.bok.mk.sukela.helper.Contract;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInRightAnimator;

public class SelectArchiveActivity extends AppCompatActivity implements CircularRowClickCallback
{

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<CircularRowData> mCircularRowData;
    private CircularRowRvAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_base_archive);

        initDataset();

        mRecyclerView = (RecyclerView) findViewById(R.id.rv);
        mRecyclerView.setItemAnimator(new SlideInRightAnimator());
        mLayoutManager = new LinearLayoutManager(this);
        setRecyclerViewLayoutManager();
        mAdapter = new CircularRowRvAdapter(mCircularRowData, this);
        mRecyclerView.setAdapter(mAdapter);

        setTitle(getResources().getString(R.string.arsiv));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public void setRecyclerViewLayoutManager()
    {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (mRecyclerView.getLayoutManager() != null)
        {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        }

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
    }

    private void initDataset()
    {
        mCircularRowData = new ArrayList<>();

        String color_days = CommonOps.getColorString(R.color.pink_500, this);
        CircularRowData row = CircularRowData.createSameColor(color_days, "G", "Geçmiş Günler", Contract.TAG_ARCHIVE_DAY);
        mCircularRowData.add(row);

        String color_weeks = CommonOps.getColorString(R.color.blue_500, this);
        row = CircularRowData.createSameColor(color_weeks, "H", "Geçmiş Haftalar", Contract.TAG_ARCHIVE_WEEK);
        mCircularRowData.add(row);
    }

    @Override
    public void rowClicked(String tag, String data)
    {
        Intent i;
        if (tag.equals(Contract.TAG_ARCHIVE_DAY))
        {
            i = new Intent(this, ArchivePhoneActivity.class);
            i.putExtra(Contract.TAG, Contract.TAG_ARCHIVE_DAY);
        }
        else if (tag.equals(Contract.TAG_ARCHIVE_WEEK))
        {
            i = new Intent(this, ArchivePhoneActivity.class);
            i.putExtra(Contract.TAG, Contract.TAG_ARCHIVE_WEEK);
        }
        else
        {
            throw new RuntimeException("Unexpected TAG: " + tag);
        }
        startActivity(i);
    }

    @Override
    public void rowLongClicked(String tag, String data)
    {
        // ignore
    }
}
*/
