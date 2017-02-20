package org.bok.mk.sukela.ui;

import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.bok.mk.sukela.R;
import org.bok.mk.sukela.entry.EntryList;
import org.bok.mk.sukela.entry.EntryManager;
import org.bok.mk.sukela.helper.IntentHelper;
import org.bok.mk.sukela.helper.Meta;
import org.bok.mk.sukela.helper.T;
import org.bok.mk.sukela.ui.entryscreen.SavedEntryScreenActivity;

public class SearchResultsActivity extends AppCompatActivity
{
    private EntryManager mEntryManager = EntryManager.getManager(this);
    private int mHeadIndex;

    public static final String INITIAL_INDEX = "INITIAL_INDEX";
    public static final String END_OF_SEARCH = "END_OF_SEARCH";
    public static final String JUMP_ENTRY_ID = "JUMP_ENTRY_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        handleIntent(getIntent());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent)
    {
        String query = intent.getStringExtra(Intent.ACTION_SEARCH);
        mHeadIndex = intent.getIntExtra(INITIAL_INDEX, 0);
        showResults(query);
    }

    private void showResults(String query)
    {
        if (query.length() < 3)
        {
            return;
        }

        final EntryList foundEntries = mEntryManager.searchSavedEntries(query);

        if (foundEntries.size() == 0)
        {
            T.toast(this, "Hiçbir şey bulamadım.");
            finish();
            return;
        }

        final ListView listView = (ListView) findViewById(R.id.list);

        String[] titles = new String[foundEntries.size()];
        for (int i = 0; i < foundEntries.size(); i++)
            titles[i] = foundEntries.get(i).getTitle();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, titles);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
            {
                int entryID = foundEntries.get(position).getEntryNo();
                Intent intent = IntentHelper.createIntentWithType(SavedEntryScreenActivity.class, Meta.savedForGoodMeta(SearchResultsActivity.this), SearchResultsActivity.this);
                intent.putExtra(JUMP_ENTRY_ID, entryID);
                intent.putExtra(END_OF_SEARCH, false);
                startActivity(intent);
                // finish();
            }
        });
    }

    @Override
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        // tell saved entry screen that search is over
        Intent intent = IntentHelper.createIntentWithType(SavedEntryScreenActivity.class, Meta.savedForGoodMeta(SearchResultsActivity.this), SearchResultsActivity.this);
        intent.putExtra(END_OF_SEARCH, true);
        intent.putExtra(INITIAL_INDEX, mHeadIndex);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onSearchRequested() {
        Log.d("_MK", "onSearchRequested");
        return super.onSearchRequested();
    }
}
