package org.bok.mk.sukela.data.source.sozluk.instela;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import org.bok.mk.sukela.data.source.sozluk.instela.data.InstelaEntry;
import org.bok.mk.sukela.util.TextOps;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.bok.mk.sukela.data.source.sozluk.instela.data.InstelaContract.INSTELA_BASE_URL;
import static org.bok.mk.sukela.data.source.sozluk.instela.data.InstelaContract.INSTELA_ENTRY_BASE_PATH;
import static org.bok.mk.sukela.data.source.sozluk.instela.data.InstelaContract.INSTELA_MONTH_URL;
import static org.bok.mk.sukela.data.source.sozluk.instela.data.InstelaContract.INSTELA_TOP_20_URL;
import static org.bok.mk.sukela.data.source.sozluk.instela.data.InstelaContract.INSTELA_WEEK_URL;
import static org.bok.mk.sukela.data.source.sozluk.instela.data.InstelaContract.INSTELA_YESTERDAY_URL;

class InstelaRunner implements Instela
{
    @Override
    public List<InstelaEntry> getBestOfMonth() throws IOException {
        return pullEntriesFromInstela(INSTELA_MONTH_URL);
    }

    @Override
    public List<InstelaEntry> getBestOfWeek() throws IOException {
        return pullEntriesFromInstela(INSTELA_WEEK_URL);
    }

    @Override
    public List<InstelaEntry> getBestOfYesterday() throws IOException {
        return pullEntriesFromInstela(INSTELA_YESTERDAY_URL);
    }

    @Override
    public List<InstelaEntry> getTop20() throws IOException {
        return pullEntriesFromInstela(INSTELA_TOP_20_URL);
    }

    @Override
    public String createTitleLink(String title, int titleId) {
        title = title.replaceAll("&amp;", "&");
        String url = INSTELA_BASE_URL;
        title = TextOps.removeTurkishChars(title);
        title = title.replaceAll("'", "");
        url = url + title.replaceAll(" ", "-");
        url = url + "--" + titleId;
        return url;
    }

    @Override
    public InstelaEntry getEntryByNumber(int entryNo) throws IOException {
        String url = INSTELA_ENTRY_BASE_PATH + entryNo;
        Source entrySource = new Source(new URL(url));
        Element article = entrySource.getAllElements(HTMLElementName.ARTICLE).get(0);

        String body = article.getAllElementsByClass("entry_text edit-placeholder").get(0).getContent().toString().trim();
        if (!body.startsWith("http")) {
            body = body.replaceAll("href=\"/", "href=\"https://tr.instela.com/");
        }
        String user = article.getAllElementsByClass("hop").get(1).getRenderer().toString().trim();
        String date = article.getAllElements(HTMLElementName.TIME).get(0).getRenderer().toString().trim();
        Element h1 = entrySource.getAllElements(HTMLElementName.H1).get(0);
        String title = h1.getRenderer().toString().trim();
        String titleUri = h1.getURIAttributes().get(0).toString();
        String titleID = titleUri.substring(titleUri.indexOf("--") + 2, titleUri.length() - 1);
        if (titleID.contains("-")) {
            titleID = titleID.substring(0, titleID.indexOf("-"));
        }

        InstelaEntry e = new InstelaEntry();
        e.setBody(body);
        e.setTitleID(Integer.parseInt(titleID));
        e.setTitle(title);
        e.setDateTime(date);
        e.setUser(user);
        e.setEntryNo(entryNo);
        e.setEntryUrl(INSTELA_ENTRY_BASE_PATH + entryNo);

        return e;
    }

    private List<InstelaEntry> pullEntriesFromInstela(final String url) throws IOException {
        List<InstelaEntry> entryList = new ArrayList<>();

        Source source = new Source(new URL(url));
        List<Element> articles = source.getAllElements(HTMLElementName.ARTICLE);
        for (Element article : articles) {
            String body = article.getAllElementsByClass("entry_text edit-placeholder").get(
                    0).getContent().toString().trim();
            if (!body.startsWith("http")) {
                body = body.replaceAll("href=\"/", "href=\"https://tr.instela.com/");
            }
            String user = article.getAllElementsByClass("hop").get(
                    1).getRenderer().toString().trim();
            String entryNo = article.getAttributeValue("id").substring(1).trim();
            String date = article.getAllElements(HTMLElementName.TIME).get(
                    0).getRenderer().toString().trim();
            Element h2 = article.getAllElements(HTMLElementName.H2).get(0);
            String title = h2.getRenderer().toString().trim();
            String titleUri = h2.getURIAttributes().get(0).toString();
            String titleID = titleUri.substring(titleUri.indexOf("--") + 2, titleUri.length() - 1);
            if (titleID.contains("-")) {
                titleID = titleID.substring(0, titleID.indexOf("-"));
            }

            InstelaEntry e = new InstelaEntry();
            e.setBody(body);
            e.setTitleID(Integer.parseInt(titleID));
            e.setTitle(title);
            e.setDateTime(date);
            e.setUser(user);
            e.setEntryNo(Integer.parseInt(entryNo));
            e.setEntryUrl(INSTELA_ENTRY_BASE_PATH + entryNo);

            entryList.add(e);
        }
        return entryList;
    }
}
