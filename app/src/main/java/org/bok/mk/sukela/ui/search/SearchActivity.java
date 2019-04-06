package org.bok.mk.sukela.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;

import org.bok.mk.sukela.Injection;
import org.bok.mk.sukela.R;
import org.bok.mk.sukela.data.model.Contract;
import org.bok.mk.sukela.data.model.Entry;
import org.bok.mk.sukela.data.model.pack.sakla.SingleEntryPack;
import org.bok.mk.sukela.ui.entryscreen.a.EntryScreenActivity;

public class SearchActivity extends AppCompatActivity implements SearchActivityContract.View
{
    private EditText mQueryBox;
    private ListView mSearchResultList;
    private CheckBox mTitleBox, mEntryBox, mUserBox;
    private Parcelable state;

    private SearchActivityPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // create and attach presenter
        mPresenter = new SearchActivityPresenter(Injection.provideEntryRepository(this));
        mPresenter.attachView(this);

        // set toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Bul");

        // create checkboxes
        mTitleBox = findViewById(R.id.checkBoxTitle);
        mEntryBox = findViewById(R.id.checkBoxBody);
        mUserBox = findViewById(R.id.checkBoxUser);

        // set listeners
        mTitleBox.setOnCheckedChangeListener(new CheckBoxListener());
        mEntryBox.setOnCheckedChangeListener(new CheckBoxListener());
        mUserBox.setOnCheckedChangeListener(new CheckBoxListener());

        // list view and listener
        mSearchResultList = findViewById(R.id.resultList);
        mSearchResultList.setOnItemClickListener((adapterView, view, position, id) -> mPresenter.entryClicked(position));

        // search box and listener
        mQueryBox = findViewById(R.id.queryBox);

        mQueryBox.addTextChangedListener(new QueryListener()
        {
            @Override
            public void afterTextChanged(Editable text) {
                mPresenter.searchQueryChanged(text.toString(), mTitleBox.isChecked(),
                        mEntryBox.isChecked(), mUserBox.isChecked());
            }
        });

        // restore values
        if (savedInstanceState != null) {
            mTitleBox.setChecked(savedInstanceState.getBoolean("title"));
            mEntryBox.setChecked(savedInstanceState.getBoolean("entry"));
            mUserBox.setChecked(savedInstanceState.getBoolean("user"));
            state = savedInstanceState.getParcelable("state");
        }

        // will fill the list view
        mPresenter.uiCreated(mQueryBox.getText().toString(), mTitleBox.isChecked(),
                mEntryBox.isChecked(), mUserBox.isChecked());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("title", mTitleBox.isChecked());
        outState.putBoolean("entry", mEntryBox.isChecked());
        outState.putBoolean("user", mUserBox.isChecked());
        int index = mSearchResultList.getFirstVisiblePosition();
        View v = mSearchResultList.getChildAt(0);
        int top = (v == null) ? 0 : (v.getTop() - mSearchResultList.getPaddingTop());
        outState.putInt("index", index);
        outState.putInt("top", top);
        state = mSearchResultList.onSaveInstanceState();
        outState.putParcelable("state", state);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setAdapter(String[] titles) {
        this.runOnUiThread(() -> {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(SearchActivity.this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, titles);
            mSearchResultList.setAdapter(adapter);
            if (state != null) {
                mSearchResultList.onRestoreInstanceState(state);
            }
        });

    }

    private class CheckBoxListener implements CompoundButton.OnCheckedChangeListener
    {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            mPresenter.checkChanged(mQueryBox.getText().toString(), mTitleBox.isChecked(),
                    mEntryBox.isChecked(), mUserBox.isChecked());
        }
    }

    @Override
    public void startEntryScreenActivity(Entry e) {
        Intent i = new Intent(this, EntryScreenActivity.class);
        SingleEntryPack pack = new SingleEntryPack();
        i.putExtra(Contract.PACK, pack);
        pack.setEntry(e);
        startActivity(i);
    }
}
