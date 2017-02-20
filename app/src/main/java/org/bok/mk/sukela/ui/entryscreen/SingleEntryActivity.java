package org.bok.mk.sukela.ui.entryscreen;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.View;

import com.github.clans.fab.FloatingActionButton;

import org.bok.mk.sukela.R;
import org.bok.mk.sukela.entry.Entry;

import static org.bok.mk.sukela.ui.SearchActivity.EXTRA_ENTRY;

public class SingleEntryActivity extends EntryScreenActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        createShareFabMenu();
    }

    private void createShareFabMenu()
    {
        mFabMenu.setVisibility(View.GONE);
        FloatingActionButton shareButton = (FloatingActionButton) findViewById(R.id.fab_share_for_single_entry_screen);
        shareButton.setVisibility(View.VISIBLE);
        int normalColorResID = ContextCompat.getColor(this, R.color.colorSingleEntryShareNormal);
        int pressedColorResID = ContextCompat.getColor(this, R.color.colorSingleEntrySharePressed);
        int rippleColorResID = ContextCompat.getColor(this, R.color.colorSingleEntryShareRipple);
        shareButton.setColorNormal(normalColorResID);
        shareButton.setColorPressed(pressedColorResID);
        shareButton.setColorRipple(rippleColorResID);
        shareButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (mEntryList.size() > 0)
                {
                    Entry e = mEntryList.get(mCurrentIndex);
                    shareEntry(e);
                }
            }
        });
    }

    @Override
    protected void getEntriesFromInternet()
    {
        return;
    }

    @Override
    protected void fillEntryList()
    {
        Entry e = (Entry) getIntent().getExtras().getSerializable(EXTRA_ENTRY);
        mEntryList.clear();
        mEntryList.add(e);
        updateViewPager();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        return true;
    }
}
