package org.bok.mk.sukela.source;

import android.content.Context;
import android.util.Log;

import net.htmlparser.jericho.*;

import org.bok.mk.sukela.entry.Entry;
import org.bok.mk.sukela.entry.EntryList;
import org.bok.mk.sukela.entry.EntryManager;
import org.bok.mk.sukela.helper.Meta;
import org.bok.mk.sukela.helper.SozlukEnum;
import org.bok.mk.sukela.helper.TextOperations;
import org.bok.mk.sukela.helper.exception.JerichoFileReadException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Created by mk on 25.12.2016.
 */

public class Instela extends Sozluk implements SinglePageSource
{
    public static final String BASE_ENTRY_PATH = "https://www.itusozluk.com/goster.php/%40";

    private Meta META;

    public Instela(Context c, Meta META)
    {
        this.mContext = c;
        this.entryManager = EntryManager.getManager(mContext);
        this.META = META;
    }

    public static String getTitleLink(Entry entry)
    {
        String title = entry.getTitle().replaceAll("&amp;", "&");
        String url = "https://tr.instela.com/";
        title = TextOperations.removeTurkishChars(title);
        title = title.replaceAll("'", "");
        url = url + title.replaceAll(" ", "-");
        url = url + "--" + entry.getTitleID();
        return url;
    }

    @Override
    public String getBaseEntryPath()
    {
        return BASE_ENTRY_PATH;
    }

    @Override
    public Entry getEntryFromUrl(String url, String id) throws IOException
    {
        net.htmlparser.jericho.Source entrySource = null;
        try
        {
            entrySource = new net.htmlparser.jericho.Source(new URL(url));
            Element article = entrySource.getAllElements(HTMLElementName.ARTICLE).get(0);

            String body = article.getAllElementsByClass("entry_text edit-placeholder").get(0).getContent().toString().trim();
            if (!body.startsWith("http"))
            {
                body = body.replaceAll("href=\"/", "href=\"https://tr.instela.com/");
            }
            String user = article.getAllElementsByClass("hop").get(1).getRenderer().toString().trim();
            String entryNo = article.getAttributeValue("id").substring(1).trim();
            String date = article.getAllElements(HTMLElementName.TIME).get(0).getRenderer().toString().trim();
            Element h1 = entrySource.getAllElements(HTMLElementName.H1).get(0);
            String title = h1.getRenderer().toString().trim();
            String titleUri = h1.getURIAttributes().get(0).toString();
            String titleID = titleUri.substring(titleUri.indexOf("--") + 2, titleUri.length() - 1);
            if (titleID.contains("-"))
            {
                titleID = titleID.substring(0, titleID.indexOf("-"));
            }

            Entry e = Entry.createEntryWithTag(META.getTag());
            e.setBody(body);
            e.setTitleID(Integer.parseInt(titleID));
            e.setTitle(title);
            e.setDateTime(date);
            e.setUser(user);
            e.setEntryNo(Integer.parseInt(entryNo));
            e.setSozluk(SozlukEnum.INSTELA);

            return e;
        }
        catch (IOException e)
        {
            throw new JerichoFileReadException("İndirilen HTML dosyası okunamadı. Lütfen tekrar deneyin.");
        }
    }

    @Override
    public EntryList readEntriesFromHtmlFile(File htmlFile) throws JerichoFileReadException
    {
        EntryList entryList = new EntryList();
        String sourceUrlString = "file:" + htmlFile;
        net.htmlparser.jericho.Source entrySource = null;
        try
        {
            entrySource = new net.htmlparser.jericho.Source(new URL(null, sourceUrlString));
            List<Element> articles = entrySource.getAllElements(HTMLElementName.ARTICLE);
            for (Element article : articles)
            {
                String body = article.getAllElementsByClass("entry_text edit-placeholder").get(0).getContent().toString().trim();
                if (!body.startsWith("http"))
                {
                    body = body.replaceAll("href=\"/", "href=\"https://tr.instela.com/");
                }
                String user = article.getAllElementsByClass("hop").get(1).getRenderer().toString().trim();
                String entryNo = article.getAttributeValue("id").substring(1).trim();
                String date = article.getAllElements(HTMLElementName.TIME).get(0).getRenderer().toString().trim();
                Element h2 = article.getAllElements(HTMLElementName.H2).get(0);
                String title = h2.getRenderer().toString().trim();
                String titleUri = h2.getURIAttributes().get(0).toString();
                String titleID = titleUri.substring(titleUri.indexOf("--") + 2, titleUri.length() - 1);
                if (titleID.contains("-"))
                {
                    titleID = titleID.substring(0, titleID.indexOf("-"));
                }

                Entry e = Entry.createEntryWithTag(META.getTag());
                e.setBody(body);
                e.setTitleID(Integer.parseInt(titleID));
                e.setTitle(title);
                e.setDateTime(date);
                e.setUser(user);
                e.setEntryNo(Integer.parseInt(entryNo));
                e.setSozluk(SozlukEnum.INSTELA);

                entryList.add(e);
            }
            return entryList;
        }
        catch (IOException e)
        {
            Log.e("_MK", "Jericho could not read file: " + htmlFile, e);
            throw new JerichoFileReadException("İndirilen HTML dosyası okunamadı. Lütfen tekrar deneyin.");
        }
    }

    //private Entry extractEntryFromInstelaArticleTag()
}
