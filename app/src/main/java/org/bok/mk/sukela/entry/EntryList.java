package org.bok.mk.sukela.entry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by mk on 06.06.2015.
 */
public class EntryList implements Iterable<Entry> {
    private final List<Entry> entryList = new ArrayList<>();

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
}
