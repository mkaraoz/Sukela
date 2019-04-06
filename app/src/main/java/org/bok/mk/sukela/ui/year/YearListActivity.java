package org.bok.mk.sukela.ui.year;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.bok.mk.sukela.R;
import org.bok.mk.sukela.data.model.Contract;
import org.bok.mk.sukela.data.model.pack.Pack;
import org.bok.mk.sukela.data.model.pack.eksi.EksiYearPack;
import org.bok.mk.sukela.ui.entryscreen.a.EntryScreenActivity;

public class YearListActivity extends AppCompatActivity implements YearAdapter.OnItemClickListener, YearContract.View
{
    private YearPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_year_list);

        RecyclerView mRecyclerView = findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new YearAdapter(this));
        mRecyclerView.addItemDecoration(getHorizontalLine());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter = new YearPresenter();
        mPresenter.attachView(this);
    }

    private DividerItemDecoration getHorizontalLine() {
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                YearListActivity.this, LinearLayoutManager.VERTICAL);
        dividerItemDecoration.setDrawable(
                getResources().getDrawable(R.drawable.divider_light_color));

        return dividerItemDecoration;
    }

    @Override
    public void onItemClick(String year) {
        mPresenter.onYearListClick(EksiYearPack.Year.getYearEnum(year));
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.detachView();
    }

    @Override
    public void startEntryScreen(Pack pack) {
        Intent i = new Intent(this, EntryScreenActivity.class);
        i.putExtra(Contract.PACK, pack);
        startActivity(i);
    }
}
