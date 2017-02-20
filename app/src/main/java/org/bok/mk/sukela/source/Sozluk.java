package org.bok.mk.sukela.source;

import android.content.Context;
import android.net.Uri;

import org.bok.mk.sukela.entry.Entry;
import org.bok.mk.sukela.entry.EntryList;
import org.bok.mk.sukela.entry.EntryManager;
import org.bok.mk.sukela.helper.SozlukEnum;

import java.io.IOException;
import java.util.List;

/**
 * Created by mk on 20.12.2016.
 */

public abstract class Sozluk
{
    protected Context mContext;
    protected EntryManager entryManager;

    public abstract Entry getEntryFromUrl(String url, String id) throws IOException;

    public int countEntries(Uri dataUri)
    {
        return entryManager.countEntries(dataUri);
    }

    public EntryList getEntriesFromDb(final Uri uri)
    {
        return entryManager.getStoredEntries(uri);
    }

    public int saveEntriesToLocalStorage(List<Entry> entries)
    {
        return entryManager.insertEntriesToDb(entries);
    }

    public static String getEntryLink(Entry e)
    {
        String entryNo = String.valueOf(e.getEntryNo());
        SozlukEnum sozlukenum = e.getSozluk();
        String url;
        switch (sozlukenum)
        {
            case EKSI:
                url = EksiSozluk.BASE_ENTRY_PATH + entryNo;
                break;
            case INSTELA:
                //                String title_url = Instela.getTitleLink(e);
                //                url = title_url.substring(0, title_url.indexOf("--"));
                //                url = url + "---" + entryNo;
                url = Instela.BASE_ENTRY_PATH + entryNo;
                break;
            case ULUDAG:
                url = UludagSozluk.BASE_ENTRY_PATH + entryNo;
                break;
            case INCI:
                url = InciSozluk.BASE_ENTRY_PATH + entryNo;
                break;
            default:
                throw new IllegalArgumentException();
        }

        return url;
    }

    public static String getTitleLink(final Entry e)
    {
        SozlukEnum sozlukenum = e.getSozluk();
        switch (sozlukenum)
        {
            case EKSI:
                return EksiSozluk.getTitleLink(e);
            case INSTELA:
                return Instela.getTitleLink(e);
            case ULUDAG:
                return UludagSozluk.getTitleLink(e);
            case INCI:
                return InciSozluk.getTitleLink(e);
            default:
                throw new IllegalArgumentException();
        }
    }

    public abstract String getBaseEntryPath();

}
