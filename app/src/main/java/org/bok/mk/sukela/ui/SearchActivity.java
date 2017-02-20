package org.bok.mk.sukela.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;

import org.bok.mk.sukela.R;
import org.bok.mk.sukela.entry.EntryList;
import org.bok.mk.sukela.entry.EntryManager;
import org.bok.mk.sukela.helper.IntentHelper;
import org.bok.mk.sukela.helper.Meta;
import org.bok.mk.sukela.ui.entryscreen.SingleEntryActivity;

public class SearchActivity extends AppCompatActivity
{
    public static final String EXTRA_ENTRY = "EXTRA_ENTRY";
    private EditText mQueryBox;
    private ListView mSearchResultList;
    private EntryManager mEntryManager = EntryManager.getManager(this);
    private CheckBox mTitleBox, mEntryBox, mUserBox;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setUserScreenTheme();
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(getString(R.string.title_search));

        mTitleBox = (CheckBox) findViewById(R.id.checkBoxTitle);
        mEntryBox = (CheckBox) findViewById(R.id.checkBoxBody);
        mUserBox = (CheckBox) findViewById(R.id.checkBoxUser);

        mTitleBox.setOnCheckedChangeListener(new CheckBoxListener());
        mEntryBox.setOnCheckedChangeListener(new CheckBoxListener());
        mUserBox.setOnCheckedChangeListener(new CheckBoxListener());

        mSearchResultList = (ListView) findViewById(R.id.resultList);
        mQueryBox = (EditText) findViewById(R.id.queryBox);
        mQueryBox.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void afterTextChanged(Editable text)
            {
                search(text.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }
        });
    }

    private void search(final String query)
    {
        if (query.length() >= 3)
        {
            showResults(query);
        }
        else
        {
            mSearchResultList.setAdapter(null);
        }
    }

    private class CheckBoxListener implements CompoundButton.OnCheckedChangeListener
    {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
        {
            if (!mUserBox.isChecked() && !mEntryBox.isChecked() && !mTitleBox.isChecked())
            {
                mSearchResultList.setAdapter(null);
            }
            else
            {
                String query = mQueryBox.getText().toString();
                search(query);
            }
        }
    }

    private void showResults(String query)
    {

        StringBuilder sb = new StringBuilder();
        if (mTitleBox.isChecked())
        {
            sb.append("1");
        }
        else
        {
            sb.append("0");
        }
        if (mEntryBox.isChecked())
        {
            sb.append("1");
        }
        else
        {
            sb.append("0");
        }
        if (mUserBox.isChecked())
        {
            sb.append("1");
        }
        else
        {
            sb.append("0");
        }

        final EntryList foundEntries = mEntryManager.searchSavedEntries(query, sb.toString());

        if (foundEntries.size() == 0)
        {
            mSearchResultList.setAdapter(null);
            return;
        }

        String[] titles = new String[foundEntries.size()];
        for (int i = 0; i < foundEntries.size(); i++)
            titles[i] = foundEntries.get(i).getTitle();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, titles);
        mSearchResultList.setAdapter(adapter);

        mSearchResultList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
            {
                Intent intent = IntentHelper.createIntentWithType(SingleEntryActivity.class, Meta.singleEntryMeta(SearchActivity.this), SearchActivity.this);
                intent.putExtra(EXTRA_ENTRY, foundEntries.get(position));
                startActivity(intent);
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == android.R.id.home)
        {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setUserScreenTheme()
    {
        setTheme(R.style.SearchActivityTheme);
        // Status bar is painted to black
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.BLACK);
        }
    }
}
