package org.bok.mk.sukela.data.source.sozluk.uludag;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import org.bok.mk.sukela.data.source.sozluk.uludag.data.UludagEntry;
import org.bok.mk.sukela.util.TextOps;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.bok.mk.sukela.data.source.sozluk.uludag.data.UludagContract.ULUDAG_BASE_ENTRY_PATH;
import static org.bok.mk.sukela.data.source.sozluk.uludag.data.UludagContract.ULUDAG_BASE_TITLE_PATH;
import static org.bok.mk.sukela.data.source.sozluk.uludag.data.UludagContract.ULUDAG_DEBE_LIST_URL;
import static org.bok.mk.sukela.data.source.sozluk.uludag.data.UludagContract.ULUDAG_HEBE_LIST_URL;

class UludagRunner implements Uludag
{
    @Override
    public UludagEntry getEntryByNumber(int entryNo) throws IOException {
        String url = ULUDAG_BASE_ENTRY_PATH + entryNo;
        Source source = new Source(new URL(url));
        Element entryDiv = source.getElementById("li_" + entryNo);
        if (entryDiv == null) {
            return null;
        }

        String body = entryDiv.getAllElementsByClass("entry-p").get(
                0).getContent().toString().trim();
        if (!body.startsWith("http")) {
            body = body.replaceAll("href=\"/k/", "href=\"http://www.uludagsozluk.com/k/");
            body = body.replaceAll("href=\"/e/", "href=\"http://www.uludagsozluk.com/e/");
        }
        body = body.replace("src=\"//", "src=\"http://");
        body = body.replaceAll("(?s)<style>.*?</style>", "");
        body = body.replaceAll("(?s)<script>.*?</script>", "");
        body = TextOps.restoreTurkishChars(body);

        String title = source.getAllElements(HTMLElementName.H1).get(
                0).getRenderer().toString().trim();
        String user = source.getAllElementsByClass("alt-u yazar").get(
                0).getRenderer().toString().trim();
        String date = source.getAllElementsByClass("date-u").get(0).getRenderer().toString().trim();
        if (date.contains("-")) {
            date = date.substring(date.indexOf('-') + 1);
        }

        UludagEntry e = new UludagEntry();
        e.setEntryNo(entryNo);
        e.setBody(body);
        e.setUser(user);
        e.setDateTime(date);
        e.setTitle(title);
        e.setEntryUrl(url);

        return e;
    }

    @Override
    public List<Integer> getHebeIds() throws IOException {
        return getEntryNumbersFromUrl(ULUDAG_HEBE_LIST_URL);
    }

    @Override
    public List<Integer> getDebeIds() throws IOException {
        return getEntryNumbersFromUrl(ULUDAG_DEBE_LIST_URL);
    }

    @Override
    public String createTitleLink(String title) {
        title = title.replaceAll(" ", "-");
        return ULUDAG_BASE_TITLE_PATH + title;
    }

    private List<Integer> getEntryNumbersFromUrl(String url) throws IOException {
        List<Integer> entryIds = new ArrayList<>();
        Source source = new Source(new URL(url));

        Element table = source.getAllElements(HTMLElementName.TABLE).get(0);
        List<Element> trElements = table.getAllElements(HTMLElementName.TR);
        for (Element tr : trElements) {
            String row = tr.getAllElements(HTMLElementName.A).get(0).getContent().toString().trim();
            String number = row.substring(row.lastIndexOf('#') + 1);
            entryIds.add(Integer.parseInt(number));
        }

        return entryIds;
    }

}
