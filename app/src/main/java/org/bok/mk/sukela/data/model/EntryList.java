package org.bok.mk.sukela.data.model;

import android.support.annotation.NonNull;

import org.bok.mk.sukela.data.source.sozluk.eksi.data.EksiEntry;
import org.bok.mk.sukela.data.source.sozluk.instela.data.InstelaEntry;
import org.bok.mk.sukela.data.source.sozluk.sozlock.data.SozlockEntry;
import org.bok.mk.sukela.data.source.sozluk.uludag.data.UludagEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class EntryList implements Iterable<Entry>
{
    private final List<Entry> entryList = new ArrayList<>();

    public EntryList() {
    }

    public EntryList (EntryList list) {
        EntryList entries = new EntryList();
        entries.addAll(list);
    }

    public static EntryList fromList(List<Entry> entries) {
        EntryList entryList = new EntryList();
        for (Entry e : entries)
        {
            entryList.add(e);
        }
        return entryList;
    }

    public static EntryList fromSozlockList(List<SozlockEntry> entries, String tag) {
        EntryList entryList = new EntryList();
        for (SozlockEntry se : entries)
        {
            Entry e = EntryConverter.convert(se, tag);
            entryList.add(e);
        }
        return entryList;
    }

    public static EntryList fromEksiList(List<EksiEntry> entries, String tag) {
        EntryList entryList = new EntryList();
        for (EksiEntry ee : entries)
        {
            Entry e = EntryConverter.convert(ee, tag);
            entryList.add(e);
        }
        return entryList;
    }

    public static EntryList fromUludagList(List<UludagEntry> entries, String tag) {
        EntryList entryList = new EntryList();
        for (UludagEntry ue : entries)
        {
            Entry e = EntryConverter.convert(ue, tag);
            entryList.add(e);
        }
        return entryList;
    }

    public static EntryList fromInstelaList(List<InstelaEntry> entries, String tag) {
        EntryList entryList = new EntryList();
        for (InstelaEntry ie : entries)
        {
            Entry e = EntryConverter.convert(ie, tag);
            entryList.add(e);
        }
        return entryList;
    }

    @NonNull
    @Override
    public Iterator<Entry> iterator() {
        return Collections.unmodifiableList(entryList).iterator();
    }

    public int size() {
        return entryList.size();
    }

    public boolean isEmpty() {
        return entryList.isEmpty();
    }

    public boolean contains(Entry e) {
        return entryList.contains(e);
    }

    public int indexOf(Entry e) {
        return entryList.indexOf(e);
    }

    public boolean add(Entry e) {
        return entryList.add(e);
    }

    public boolean remove(Entry e) {
        return entryList.remove(e);
    }

    public void clear() {
        entryList.clear();
    }

    public Entry get(int index) {
        return entryList.get(index);
    }

    public boolean addAll(EntryList list) {
        return entryList.addAll(list.entryList);
    }

    public static EntryList emptyList() {
        return new EntryList();
    }

    public List<Entry> unmodifiableList() {
        return Collections.unmodifiableList(entryList);
    }

    public List<String> getTitles() {
        List<String> titleList = new ArrayList<>();
        for (Entry e : entryList) {
            titleList.add(e.getTitle());
        }
        return titleList;
    }

    public void sort() {
        Collections.sort(entryList, (e1, e2) -> e1.getTitle().compareTo(e2.getTitle()));
    }
}
