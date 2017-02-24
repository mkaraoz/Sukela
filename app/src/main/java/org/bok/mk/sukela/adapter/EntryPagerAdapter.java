package org.bok.mk.sukela.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.bok.mk.sukela.entry.Entry;
import org.bok.mk.sukela.entry.EntryList;
import org.bok.mk.sukela.helper.Contract;
import org.bok.mk.sukela.helper.Meta;
import org.bok.mk.sukela.ui.entryscreen.EntryScreenFragment;

/**
 * Created by mk on 13.06.2015.
 */
public class EntryPagerAdapter extends FragmentStatePagerAdapter {
    private Meta meta;
    private EntryList mEntryList;

    public EntryPagerAdapter(FragmentManager fm, Meta meta) {
        super(fm);
        this.meta = meta;
    }

    public void setData(EntryList list) {
        this.mEntryList = list;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return mEntryList.size();
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = new EntryScreenFragment();

        if (mEntryList.size() > 0) {
            Bundle args = new Bundle();
            args.putSerializable(Contract.ENTRY, mEntryList.get(position));
            args.putSerializable(Contract.META, meta);
            fragment.setArguments(args);
        }
        return fragment;
    }

    public Entry getEntry(final int position) {
        return mEntryList.get(position);
    }
}
