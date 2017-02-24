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

public class InciSozluk extends Sozluk implements MultiPageSource {
    public static final String BASE_ENTRY_PATH = "http://www.incisozluk.com.tr/e/";

    private Meta META;

    public InciSozluk(Context c, Meta META) {
        this.mContext = c;
        this.entryManager = EntryManager.getManager(mContext);
        this.META = META;
    }

    public static String getTitleLink(Entry e) {
        String title = e.getTitle().replace(" ", "-");
        title = "http://www.incisozluk.com.tr/w/" + title;
        return title;
    }

    @Override
    public String getBaseEntryPath() {
        return BASE_ENTRY_PATH;
    }

    @Override
    public List<String> getEntryNumbersFromUrl(String url) throws IOException {
        List<String> entryIds = new ArrayList<>();
        Source source = new Source(new URL(url));

        Element table = source.getAllElementsByClass("table table-bordered table-striped").get(0);
        List<Element> trElements = table.getAllElements(HTMLElementName.TR);
        for (Element tr : trElements) {
            Element e = tr.getAllElements(HTMLElementName.TD).get(1);
            String content = e.getRenderer().toString().trim();
            String entryNo = content.substring(content.indexOf("/#") + 2);
            entryIds.add(entryNo);
        }
        return entryIds;
    }

    @Override
    public EntryList downloadEntries(List<String> entryIds, MultiFileDownloadCallback feedback) throws IOException {
        EntryList entryList = new EntryList();
        for (int i = 0; i < entryIds.size(); i++) {
            if (feedback.isTaskCancelled()) {
                break;
            }

            String id = entryIds.get(i);
            feedback.updateProgress(i);
            String url = BASE_ENTRY_PATH + id;
            try {
                Entry entry = getEntryFromUrl(url, id);
                if (entry != null) {
                    entryList.add(entry);
                }
            } catch (IOException e) {
                Log.d("_MK", e.getMessage(), e);
            }
        }

        if (entryList.size() == 0) {
            throw new IOException("İnci sözlüğe bağlanılamadı.");
        }
        return entryList;
    }

    @Override
    public Entry getEntryFromUrl(String url, String id) throws IOException {
        Entry e = Entry.createEntryWithTag(META.getTag());
        e.setSozluk(SozlukEnum.INCI);
        Source source = new Source(new URL(url));

        List<Element> containerTags = source.getAllElementsByClass("entry-text-wrap");
        if (containerTags == null || containerTags.isEmpty()) {
            return null;
        }

        String body = source.getAllElementsByClass("entry-text-wrap").get(0).getContent().toString().trim();
        String user = source.getAllElementsByClass("username").get(0).getContent().toString().trim();
        String entryNo = id;
        String date = source.getAllElementsByClass("entry-tarih").get(0).getRenderer().toString().trim();
        Element h1 = source.getAllElementsByClass("title").get(0);
        String title = h1.getRenderer().toString();
        String titleID = h1.getAttributeValue("id");
        titleID = titleID.substring(titleID.indexOf('_') + 1);

        e.setBody(body);
        e.setUser(user);
        e.setEntryNo(Integer.parseInt(entryNo));
        e.setDateTime(date);
        e.setTitle(title);
        e.setTitleID(Integer.parseInt(titleID));

        return e;
    }


}
