package org.bok.mk.sukela.ui.entryscreen.a;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.bok.mk.sukela.data.model.Contract;
import org.bok.mk.sukela.data.model.EntryList;
import org.bok.mk.sukela.data.model.pack.Pack;
import org.bok.mk.sukela.ui.entryscreen.f.EntryScreenFragment;

class EntryPagerAdapter extends FragmentStatePagerAdapter
{
    private final Pack pack;
    private EntryList mEntryList;

    EntryPagerAdapter(FragmentManager fm, Pack pack) {
        super(fm);
        this.pack = pack;
    }

    public void setData(EntryList list) {
        this.mEntryList = list;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
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
            args.putSerializable(Contract.PACK, pack);
            fragment.setArguments(args);
        }
        return fragment;
    }
}