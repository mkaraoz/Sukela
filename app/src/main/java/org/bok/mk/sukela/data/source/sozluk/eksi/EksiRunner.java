package org.bok.mk.sukela.data.source.sozluk.eksi;

import android.util.Log;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import org.bok.mk.sukela.data.source.sozluk.eksi.data.EksiContract;
import org.bok.mk.sukela.data.source.sozluk.eksi.data.EksiEntry;
import org.bok.mk.sukela.data.model.pack.eksi.EksiYearPack;
import org.bok.mk.sukela.util.TextOps;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.bok.mk.sukela.data.source.sozluk.eksi.data.EksiContract.EKSI_BASE_URL;
import static org.bok.mk.sukela.data.source.sozluk.eksi.data.EksiContract.EKSI_ENTRY_BASE_PATH;
import static org.bok.mk.sukela.data.source.sozluk.eksi.data.EksiContract.EKSI_HEBE_LIST_URL;

class EksiRunner implements EksiSozluk
{
    @Override
    public EksiEntry getEntryByNumber(int entryNo) throws IOException {
        String url = EKSI_ENTRY_BASE_PATH + entryNo;
        return getFirstEntryOnPage(url);
    }

    @Override
    public EksiEntry getBestOfPage(final String url) throws IOException {
        return getFirstEntryOnPage(EksiContract.EKSI_BASE_URL + url);
    }

    @Override
    public List<Integer> getHebeIds() throws IOException {
        List<Integer> entryIds = new ArrayList<>();

        Source source = new Source(new URL(EKSI_HEBE_LIST_URL));
        List<Element> htmlElements = source.getAllElements(HTMLElementName.OL);
        Element olElement;
        if (htmlElements.size() > 1) {
            olElement = htmlElements.get(1);
        }
        else {
            olElement = htmlElements.get(0);
        }
        List<Element> liElements = olElement.getChildElements();
        for (Element e : liElements) {
            Element ATag = (e.getAllElements(HTMLElementName.A)).get(0);
            String elements = ATag.getAttributeValue("href");
            String entryAndNumber = elements.substring(elements.lastIndexOf("f%23") + 4);
            entryIds.add(Integer.valueOf(entryAndNumber));
        }

        return entryIds;
    }

    /**
     * https://eksisozluk.com/basliklar/istatistik/USER_NAME/en-begenilenleri adresine giderek
     * yazarın en beğenilen entrilerinden ilk 50 tanesinin entry numaralarını alıp, dönüyor.
     *
     * @param username Yazar adı
     * @return
     * @throws IOException
     */
    @Override
    public List<Integer> getUserBestOfIds(String username) throws IOException {
        final String url = "https://eksisozluk.com/basliklar/istatistik/" + username + "/en-begenilenleri";

        List<Integer> entryNumbers = new ArrayList<>();
        Source source = new Source(new URL(url));
        Element content = source.getElementById("content");

        // Eğer url hatalıysa content null geliyor
        if (content == null) { return new ArrayList<>(); }

        // Adamın hiç entrisi yoksa ul boş geliyor. Onun için gerekli bir kontrol
        List<Element> ulElems = content.getAllElements(HTMLElementName.UL);
        if (ulElems.size() <= 0) { return entryNumbers; }

        Element ul = ulElems.get(0);
        for (Element li : ul.getAllElements(HTMLElementName.LI)) {
            String entryID = li.getAllElementsByClass("detail with-parentheses").get(
                    0).getRenderer().toString().substring(1);
            entryNumbers.add(Integer.parseInt(entryID));
        }
        return entryNumbers;
    }

    @Override
    public List<String> getGundemTitles() throws IOException {
        List<String> gundemTitles = new ArrayList<>();
        Source source = new Source(new URL(EKSI_BASE_URL));

        List<Element> liElements = source.getFirstElementByClass(
                "topic-list partial").getAllElements(HTMLElementName.LI);
        for (Element e : liElements) {
            try {
                String link = e.getFirstStartTag(HTMLElementName.A).getAttributeValue("href");
                link = link.substring(1, link.indexOf("?a=") + 3);
                link = link + "dailynice";
                gundemTitles.add(link);
            }
            catch (java.lang.NullPointerException npe) {
                Log.e("_MK", "Eksi Gündem: " + npe.getMessage());
            }
        }
        return gundemTitles;
    }

    @Override
    public List<EksiEntry> getBestOfYear(int year) throws IOException {
        List<EksiEntry> entryList = new ArrayList<>();
        Source source = new Source(new URL(EksiYearPack.getDownloadLink(year)));
        List<Element> list = source.getAllElements("Entry");

        for (Element e : list) {
            String entryNo = e.getAllElements("entryNo").get(0).getContent().toString();
            String title = e.getAllElements("title").get(0).getContent().toString();
            String titleId = e.getAllElements("title_id").get(0).getContent().toString();
            String body = e.getAllElements("body").get(0).getContent().toString();
            String user = e.getAllElements("user").get(0).getContent().toString();
            String dateTime = e.getAllElements("date_time").get(0).getContent().toString();
            String tag = e.getAllElements("tag").get(0).getContent().toString();

            EksiEntry entry = new EksiEntry();

            entry.setEntryNo(Integer.parseInt(entryNo));
            entry.setTitle(title);
            entry.setTitleID(Integer.parseInt(titleId));
            entry.setBody(body);
            entry.setUser(user);
            entry.setDateTime(dateTime);
            entry.setEntryUrl(EksiContract.EKSI_BASE_URL + "entry/" + entryNo);

            entryList.add(entry);
        }
        return entryList;
    }

    @Override
    public String createTitleLink(String title, int titleId) {
        StringBuilder sb = new StringBuilder();
        sb.append(EKSI_BASE_URL);

        title = title.replaceAll("&amp;", "&");
        title = TextOps.removeTurkishChars(title);

        if (titleId == 0) {
            sb.append(title);
            return sb.toString();
        }

        title = title.replace("'", "");
        title = title.replace(" ", "-");
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

    private EksiEntry getFirstEntryOnPage(final String url) throws IOException {
        Source source = new Source(new URL(url));
        Element ul = source.getElementById("entry-item-list");
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
                String supTitle = sup.getFirstElement(HTMLElementName.A).getAttributeValue(
                        "title").trim();
                supTitle = "(" + supTitle.substring(supTitle.indexOf(":") + 1);
                body = body.replaceFirst(">\\*<", ">*" + supTitle + "<");
            }
        }

        String user = ul.getAllElementsByClass("entry-author").get(
                0).getRenderer().toString().trim();
        String entryNo = ul.getAllElements(HTMLElementName.LI).get(0).getAttributeValue(
                "data-id").trim();
        String date = ul.getAllElementsByClass("entry-date").get(0).getRenderer().toString().trim();
        Element h1 = source.getElementById("title");
        String title = h1.getRenderer().toString().trim();
        String titleID = h1.getAttributeValue("data-id").trim();

        EksiEntry e = new EksiEntry();
        e.setBody(body);
        e.setUser(user);
        e.setEntryNo(Integer.parseInt(entryNo));
        e.setDateTime(date);
        e.setTitle(title);
        e.setTitleID(Integer.parseInt(titleID));
        e.setEntryUrl(EksiContract.EKSI_BASE_URL + "entry/" + entryNo);

        return e;
    }

}
