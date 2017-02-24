package org.bok.mk.sukela.source;

import android.content.Context;
import android.util.Log;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import org.bok.mk.sukela.entry.Entry;
import org.bok.mk.sukela.entry.EntryList;
import org.bok.mk.sukela.entry.EntryManager;
import org.bok.mk.sukela.helper.BackupManager;
import org.bok.mk.sukela.helper.Contract;
import org.bok.mk.sukela.helper.Meta;
import org.bok.mk.sukela.helper.SozlukEnum;
import org.bok.mk.sukela.helper.TextOperations;
import org.bok.mk.sukela.helper.callbacks.MultiFileDownloadCallback;
import org.bok.mk.sukela.helper.exception.JerichoFileReadException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mk on 20.12.2016.
 */

public class EksiSozluk extends Sozluk implements MultiPageSource, SinglePageSource {
    public static final String BASE_ENTRY_PATH = "https://eksisozluk.com/entry/";
    public static final String EKSI_BASE_URL = "https://eksisozluk.com/";
    public static final String SOZLOCK_BASE_URL = "http://sozlock.com/";
    public static final String DEBE_OKU_BASE_URL = "http://debeoku.com/";

    private Meta META;

    public EksiSozluk(Context c, Meta META) {
        this.mContext = c;
        this.entryManager = EntryManager.getManager(mContext);
        this.META = META;
    }

    public static String getTitleLink(final Entry e) {
        StringBuilder sb = new StringBuilder();
        sb.append(EKSI_BASE_URL);

        String title = e.getTitle().replaceAll("&amp;", "&");
        title = TextOperations.removeTurkishChars(title);
        int titleId = e.getTitleID();

        if (titleId == 0) {
            sb.append(title);
            return sb.toString();
        }

        title = title.replace("'", "");
        title = title.replace(" ", "-");
        title = title.replaceFirst("#", "");
        title = title.replace("#", "-");
        title = title.replace("&", "-");
        title = title.replace("$", "-");
        title = title.replace("=", "-");
        title = title.replace("/", "-");
        title = title.replace(".", "-");
        title = title.replace("+", "-");
        title = title.replace("^", "-");
        title = title.replace("*", "-");
        title = title.replace("---", "-");

        sb.append(title);
        sb.append("--");
        sb.append(titleId);
        return sb.toString();

    }

    @Override
    public String getBaseEntryPath() {
        return BASE_ENTRY_PATH;
    }

    @Override
    public List<String> getEntryNumbersFromUrl(String url) throws IOException {
        if (url.equals(EKSI_BASE_URL)) {
            return getGundemEntryNumbers();
        }

        if (url.equals(DEBE_OKU_BASE_URL)) {
            return debeOkuComEntryNumbers();
        }

        List<String> entryIds = new ArrayList<>();
        Source source = new Source(new URL(url));

        List<Element> htmlElements = source.getAllElements(HTMLElementName.OL);
        Element olElement;
        if (htmlElements.size() > 1) {
            olElement = htmlElements.get(1);
        } else {
            olElement = htmlElements.get(0);
        }
        List<Element> liElements = olElement.getChildElements();
        for (Element e : liElements) {
            Element ATag = (e.getAllElements(HTMLElementName.A)).get(0);
            String elements = ATag.getAttributeValue("href");
            String entryAndNumber = elements.substring(elements.lastIndexOf("f%23") + 4);
            entryIds.add(entryAndNumber);
        }

        return entryIds;
    }

    private List<String> debeOkuComEntryNumbers() throws IOException {
        List<String> entryIds = new ArrayList<>();
        Source source = new Source(new URL(DEBE_OKU_BASE_URL));

        List<Element> aElements = source.getFirstElement(HTMLElementName.UL).getAllElements(HTMLElementName.A);
        for (Element a : aElements) {
            try {
                if (a.getAttributeValue("target") != null) {
                    String link = a.getAttributeValue("href");
                    String number = link.substring(link.lastIndexOf('/') + 1);
                    entryIds.add(number);
                }
            } catch (java.lang.NullPointerException npe) {
                Log.d("_MK", "reklamlar: " + npe.getMessage());
            }
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
            String url;
            if (META.getTag().equals(Contract.TAG_EKSI_GNDM)) {
                url = EKSI_BASE_URL + id;
            } else
                url = BASE_ENTRY_PATH + id;
            try {
                Entry entry = getEntryFromUrl(url, id);
                entryList.add(entry);
            } catch (IOException e) {
                Log.d("_MK", e.getMessage(), e);
            }
        }

        if (entryList.size() == 0) {
            throw new IOException("Ekşi sözlüğe bağlanılamadı.");
        }
        return entryList;
    }

    @Override
    public Entry getEntryFromUrl(String url, String id) throws IOException {
        Entry e = Entry.createEntryWithTag(META.getTag());
        e.setSozluk(SozlukEnum.EKSI);
        Source source = new Source(new URL(url));

        Element ul = source.getElementById("entry-list");


        Element contentEle = ul.getAllElementsByClass("content").get(0);

        String body = contentEle.getContent().toString().trim();
        if (!body.startsWith("http")) {
            body = body.replaceAll("href=\"/\\?q=", "href=\"https://eksisozluk.com/?q=");
            body = body.replaceAll("href=\"/entry/", "href=\"https://eksisozluk.com/entry/");
        }
        body = body.replace("www.eksisozluk.com", "eksisozluk.com");

        List<Element> sups = contentEle.getAllElements(HTMLElementName.SUP);
        if (sups.size() > 0) {
            for (Element sup : sups) {
                String supTitle = sup.getFirstElement(HTMLElementName.A).getAttributeValue("title").trim();
                supTitle = "(" + supTitle.substring(supTitle.indexOf(":") + 1);
                body = body.replaceFirst(">\\*<", ">*" + supTitle + "<");
            }
        }

        String user = ul.getAllElementsByClass("entry-author").get(0).getRenderer().toString().trim();
        String entryNo = ul.getAllElements(HTMLElementName.LI).get(0).getAttributeValue("data-id").trim();
        String date = ul.getAllElementsByClass("entry-date").get(0).getRenderer().toString().trim();
        Element h1 = source.getElementById("title");
        String title = h1.getRenderer().toString().trim();
        String titleID = h1.getAttributeValue("data-id").trim();

        e.setBody(body);
        e.setUser(user);
        e.setEntryNo(Integer.parseInt(entryNo));
        e.setDateTime(date);
        e.setTitle(title);
        e.setTitleID(Integer.parseInt(titleID));

        return e;
    }

    public int countUserEntries(String user) {
        return entryManager.countUserEntries(user);
    }

    public EntryList readEntriesFromHtmlFile(File file) throws JerichoFileReadException {
        if (META.getTag().equals(Contract.TAG_EKSI_DEBE)) {
            return readSozlockDebeEntries(file);
        } else {
            return BackupManager.readEntriesFromXmlFile(file);
        }
    }

    private EntryList readSozlockDebeEntries(final File file) throws JerichoFileReadException {
        EntryList entryList = new EntryList();

        String sourceUrlString = "file:" + file;
        net.htmlparser.jericho.Source entrySource = null;
        try {
            entrySource = new net.htmlparser.jericho.Source(new URL(null, sourceUrlString));
        } catch (IOException e) {
            Log.e("_MK", "Jericho could not read file: " + file, e);
            throw new JerichoFileReadException("İndirilen HTML dosyası okunamadı. Lütfen tekrar deneyin.");
        }

        Element element = entrySource.getFirstElementByClass("listnone entrylist");
        List<Element> liElements = element.getAllElements(HTMLElementName.LI);
        for (Element li : liElements) {
            Element h3 = li.getFirstElement(HTMLElementName.H3);
            if (h3 != null) {
                String title = h3.getRenderer().toString();
                title = title.substring(title.indexOf('.') + 1).trim();

                String body = li.getFirstElementByClass("entrytxt").getContent().toString();

                String entryNo = li.getFirstElementByClass("basliklogo").getFirstElement(HTMLElementName.A).getAttributeValue("href");
                entryNo = entryNo.substring(entryNo.lastIndexOf('/') + 1);

                String user = li.getFirstElementByClass("yazar").getRenderer().toString().trim();

                String dateTime = li.getFirstElementByClass("entrytime small").getRenderer().toString().trim();

                Entry e = Entry.createEntryWithTag(Contract.TAG_EKSI_DEBE);
                e.setSozluk(SozlukEnum.EKSI);
                e.setBody(body);
                e.setDateTime(dateTime);
                e.setEntryNo(Integer.parseInt(entryNo));
                e.setTitle(title);
                e.setTitleID(0);
                e.setUser(user);
                entryList.add(e);
            }
        }
        return entryList;
    }

    private List<String> getGundemEntryNumbers() throws IOException {
        List<String> entryIds = new ArrayList<>();
        Source source = new Source(new URL(EKSI_BASE_URL));

        List<Element> liElements = source.getFirstElementByClass("topic-list partial").getAllElements(HTMLElementName.LI);
        for (Element e : liElements) {
            try {
                String link = e.getFirstStartTag(HTMLElementName.A).getAttributeValue("href");
                link = link.substring(1, link.indexOf("?a=") + 3);
                link = link + "dailynice";
                entryIds.add(link);
            } catch (java.lang.NullPointerException npe) {
                Log.e("_MK", "Eksi Gündem: " + npe.getMessage());
            }
        }
        return entryIds;
    }
}