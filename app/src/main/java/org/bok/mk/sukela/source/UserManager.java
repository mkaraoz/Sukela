package org.bok.mk.sukela.source;

import android.content.Context;
import android.net.Uri;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import org.bok.mk.sukela.data.LocalDbManager;
import org.bok.mk.sukela.helper.Meta;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by mk on 05.01.2017.
 */

public class UserManager {
    public static List<String> getSavedUsers(final Context c, final Uri uri) {
        LocalDbManager manager = new LocalDbManager(c);
        List<String> users = manager.getSavedUsers(uri);
        Collections.sort(users);
        return (users);
    }

    public static List<String> getEntryNumbersFromUserPage(Meta META, String userName) throws IOException {
        List<String> entryIds = new ArrayList<>();
        String url = META.getListUrl() + userName.trim().replaceAll(" ", "-") + "/en-begenilenleri";
        Source source = new Source(new URL(url));
        Element content = source.getElementById("content");

        // Adamın hiç entrisi yoksa ul boş geliyor. Onun için gerekli bir kontrol
        List<Element> ulElems = content.getAllElements(HTMLElementName.UL);
        if (ulElems.size() <= 0)
            return entryIds;

        Element ul = ulElems.get(0);
        for (Element li : ul.getAllElements(HTMLElementName.LI)) {
            String entryID = li.getAllElementsByClass("detail with-parentheses").get(0).getRenderer().toString().substring(1);
            entryIds.add(entryID);
        }
        return entryIds;
    }
}
