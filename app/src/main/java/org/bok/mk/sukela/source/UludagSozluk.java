package org.bok.mk.sukela.source;

import android.content.Context;
import android.util.Log;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import org.bok.mk.sukela.entry.Entry;
import org.bok.mk.sukela.entry.EntryList;
import org.bok.mk.sukela.entry.EntryManager;
import org.bok.mk.sukela.helper.callbacks.MultiFileDownloadCallback;
import org.bok.mk.sukela.helper.Meta;
import org.bok.mk.sukela.helper.SozlukEnum;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mk on 25.12.2016.
 */

public class UludagSozluk extends Sozluk implements MultiPageSource
{
    public static final String BASE_ENTRY_PATH = "https://m.uludagsozluk.com/e/";

    private Meta META;

    public UludagSozluk(Context c, Meta META)
    {
        this.mContext = c;
        this.entryManager = EntryManager.getManager(mContext);
        this.META = META;
    }

    public static String getTitleLink(Entry entry)
    {
        String entryTitle = entry.getTitle();
        entryTitle = entryTitle.replaceAll(" ", "-");
        return "https://m.uludagsozluk.com/k/" + entryTitle;
    }

    @Override
    public String getBaseEntryPath()
    {
        return BASE_ENTRY_PATH;
    }

    @Override
    public List<String> getEntryNumbersFromUrl(String url) throws IOException
    {
        List<String> entryIds = new ArrayList<>();
        Source source = new Source(new URL(url));

        Element table = source.getAllElements(HTMLElementName.TABLE).get(0);
        List<Element> trElements = table.getAllElements(HTMLElementName.TR);
        for (Element tr : trElements)
        {
            String row = tr.getAllElements(HTMLElementName.A).get(0).getContent().toString().trim();
            String number = row.substring(row.lastIndexOf('#') + 1);
            entryIds.add(number);
        }

        return entryIds;
    }

    @Override
    public EntryList downloadEntries(List<String> entryIds, MultiFileDownloadCallback feedback) throws IOException
    {
        EntryList entryList = new EntryList();
        for (int i = 0; i < entryIds.size(); i++)
        {
            if (feedback.isTaskCancelled())
            {
                break;
            }

            String id = entryIds.get(i);
            feedback.updateProgress(i);
            String url = BASE_ENTRY_PATH + id;
            try
            {
                Entry entry = getEntryFromUrl(url, id);
                if (entry != null)
                {
                    entryList.add(entry);
                }
            }
            catch (IOException e)
            {
                Log.d("_MK", e.getMessage(), e);
            }
        }

        if (entryList.size() == 0)
        {
            throw new IOException("Uludağ sözlüğe bağlanılamadı.");
        }
        return entryList;
    }

    @Override
    public Entry getEntryFromUrl(String url, String id) throws IOException
    {
        Entry e = Entry.createEntryWithTag(META.getTag());
        e.setSozluk(SozlukEnum.ULUDAG);

        Source source = new Source(new URL(url));
        Element bodyDiv = source.getElementById("li" + id);
        if (bodyDiv == null)
        {
            return null;
        }

        String body = bodyDiv.getAllElementsByClass("ent").get(0).getContent().toString().trim();
        if (!body.startsWith("http"))
        {
            body = body.replaceAll("href=\"/k/", "href=\"http://www.uludagsozluk.com/k/");
            body = body.replaceAll("href=\"/e/", "href=\"http://www.uludagsozluk.com/e/");
        }
        body = body.replaceAll("(?s)<style>.*?</style>", "");
        body = body.replaceAll("(?s)<script>.*?</script>", "");
        //body = TextOperations.restoreTurkishChars(body);

        String title = source.getAllElements(HTMLElementName.H1).get(0).getRenderer().toString().trim();
        String user = source.getAllElementsByClass("kuladi").get(0).getRenderer().toString().trim();
        String date = source.getAllElementsByClass("tarih").get(0).getRenderer().toString().trim();
        if (date.contains("-"))
        {
            date = date.substring(date.indexOf('-') + 1);
        }
        int titleID = 0;
        int entryNo = Integer.parseInt(id);

        e.setBody(body);
        e.setUser(user);
        e.setEntryNo(entryNo);
        e.setDateTime(date);
        e.setTitle(title);
        e.setTitleID(titleID);

        return e;
    }
}
