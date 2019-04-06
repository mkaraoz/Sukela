package org.bok.mk.sukela.data.model.pack.sakla;

import org.bok.mk.sukela.data.model.Contract;
import org.bok.mk.sukela.data.model.Entry;

public class SingleEntryPack extends SaklaPack
{
    private Entry e;

    @Override
    public String getTitle() {
        return "Aranan Entry";
    }

    @Override
    public String getTag() {
        return Contract.TAG_SINGLE_ENTRY;
    }

    public void setEntry(Entry e) {
        this.e = e;
    }

    public Entry getEntry() {
        return e;
    }
}
