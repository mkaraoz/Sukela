package org.bok.mk.sukela.data.source.sozluk.sozlock;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import org.bok.mk.sukela.data.source.sozluk.sozlock.data.SozlockEntry;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.bok.mk.sukela.data.source.sozluk.eksi.data.EksiContract.EKSI_BASE_URL;
import static org.bok.mk.sukela.data.source.sozluk.eksi.data.EksiContract.EKSI_ENTRY_BASE_PATH;
import static org.bok.mk.sukela.data.source.sozluk.sozlock.data.SozlockContract.SOZLOCK_DAILY_DEBE;

class SozlockRunner implements Sozlock
{
    @Override
    public List<SozlockEntry> getDebe() throws IOException {
        return getEntriesFromSozlock(SOZLOCK_DAILY_DEBE);
    }

    @Override
    public List<SozlockEntry> getDebeByDate(String yyyymmdd) throws IOException {
        String url = SOZLOCK_DAILY_DEBE + "?date=" + yyyymmdd;
        return getEntriesFromSozlock(url);
    }

    private List<SozlockEntry> getEntriesFromSozlock(final String url) throws IOException {
        List<SozlockEntry> entryList = new ArrayList<>();
        Source source = new Source(new URL(url));
        Element element = source.getFirstElementByClass("listnone entrylist");
        List<Element> liElements = element.getAllElements(HTMLElementName.LI);
        for (Element li : liElements) {
            Element h3 = li.getFirstElement(HTMLElementName.H3);
            if (h3 != null) {
                String title = h3.getRenderer().toString();
                title = title.substring(title.indexOf('.') + 1).trim();

                String body = li.getFirstElementByClass("entrytxt").getContent().toString();
                // sozlock entry numarası ile verilen entry linklerini olduğu gibi alıyor, kendiside
                // bizde sözlüğe gidemiyoruz tıklayınca. Başına ekşi sözlüğü eklemek gerekli
                body = body.replace("href=\"/entry/", "href=\"https://www.eksisozluk.com/entry/");
                body = body.replace("href=\"/?q=", "href=\"https://www.eksisozluk.com/?q=");

                String entryNo = li.getFirstElementByClass("basliklogo").getFirstElement(
                        HTMLElementName.A).getAttributeValue("href");
                entryNo = entryNo.substring(entryNo.lastIndexOf('/') + 1);

                String user = li.getFirstElementByClass("yazar").getRenderer().toString().trim();

                String dateTime = li.getFirstElementByClass(
                        "entrytime small").getRenderer().toString().trim();

                List<Element> sups = li.getAllElements(HTMLElementName.SUP);
                if (sups.size() > 0) {
                    for (Element sup : sups) {
                        String supTitle = sup.getFirstElement(HTMLElementName.A).getAttributeValue("title").trim();
                        supTitle = "(" + supTitle.substring(supTitle.indexOf(":") + 1);
                        body = body.replaceFirst(">\\*<", ">*" + supTitle + "<");
                    }
                }

                SozlockEntry e = new SozlockEntry();
                e.setTitle(title);
                e.setBody(body);
                e.setUser(user);
                e.setDate(dateTime);
                e.setEntryNo(Integer.parseInt(entryNo));
                e.setEksiLink(EKSI_ENTRY_BASE_PATH + entryNo);
                e.setTitleUrl(EKSI_BASE_URL + title);
                e.setEntryUrl(EKSI_BASE_URL + entryNo);
                entryList.add(e);
            }
        }
        return entryList;
    }
}
