package org.bok.mk.sukela.data.backup;

import android.annotation.SuppressLint;
import android.util.Log;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;

import org.bok.mk.sukela.BuildConfig;
import org.bok.mk.sukela.data.model.Contract;
import org.bok.mk.sukela.data.model.Entry;
import org.bok.mk.sukela.data.model.EntryList;
import org.bok.mk.sukela.data.model.SozlukEnum;
import org.bok.mk.sukela.data.source.sozluk.eksi.EksiFactory;
import org.bok.mk.sukela.data.source.sozluk.eksi.EksiSozluk;
import org.bok.mk.sukela.data.source.sozluk.instela.Instela;
import org.bok.mk.sukela.data.source.sozluk.instela.InstelaFactory;
import org.bok.mk.sukela.data.source.sozluk.uludag.Uludag;
import org.bok.mk.sukela.data.source.sozluk.uludag.UludagFactory;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import static org.bok.mk.sukela.data.source.sozluk.eksi.data.EksiContract.EKSI_ENTRY_BASE_PATH;
import static org.bok.mk.sukela.data.source.sozluk.instela.data.InstelaContract.INSTELA_ENTRY_BASE_PATH;
import static org.bok.mk.sukela.data.source.sozluk.uludag.data.UludagContract.ULUDAG_BASE_ENTRY_PATH;

/**
 * DO NOT TRY TO OPTIMIZE / CHANGE / EDIT / DELETE THIS CLASS
 */
public class BackupManager
{
    private static final String LOG_TAG = BackupManager.class.getSimpleName();

    public static boolean saveEntriesToSdCard(EntryList entryList, final String path) {
        for (Entry e : entryList) {
            String entryBody = e.getBody();
            entryBody = entryBody.replaceAll("&lt;", "<");
            entryBody = entryBody.replaceAll("&gt;", ">");
            entryBody = entryBody.replaceAll("&amp;", "&");
            e.setBody(entryBody);
        }

        String backUpFileName = generateNameForBackUpFile();

        File f = new File(path + File.separator + backUpFileName);

        return createXmlFile(entryList, f);
    }

    private static boolean createXmlFile(EntryList entryList, File file) {
        try {
            DocumentBuilderFactory dbFac = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbFac.newDocumentBuilder();
            org.w3c.dom.Document doc = docBuilder.newDocument();

            // Root element - use full name it may interfere with jericho.Element
            org.w3c.dom.Element root = doc.createElement("EntryList");
            doc.appendChild(root);

            org.w3c.dom.Element version = doc.createElement("version");
            String versionNAme = BuildConfig.VERSION_NAME;
            version.appendChild(doc.createTextNode(versionNAme));
            root.appendChild(version);

            org.w3c.dom.Element ele;
            org.w3c.dom.Element entryElement;

            for (Entry e : entryList) {
                entryElement = doc.createElement("Entry");

                ele = doc.createElement("sozluk");
                ele.appendChild(doc.createTextNode(e.getSozluk().getName()));
                entryElement.appendChild(ele);

                ele = doc.createElement("title");
                ele.appendChild(doc.createTextNode(e.getTitle()));
                entryElement.appendChild(ele);

                ele = doc.createElement("body");
                ele.appendChild(doc.createTextNode(e.getBody()));
                entryElement.appendChild(ele);

                ele = doc.createElement("user");
                ele.appendChild(doc.createTextNode(e.getUser()));
                entryElement.appendChild(ele);

                ele = doc.createElement("entryNo");
                ele.appendChild(doc.createTextNode(String.valueOf(e.getEntryNo())));
                entryElement.appendChild(ele);

                ele = doc.createElement("date_time");
                ele.appendChild(doc.createTextNode(e.getDateTime()));
                entryElement.appendChild(ele);

                ele = doc.createElement("entry_url");
                ele.appendChild(doc.createTextNode(e.getEntryUrl()));
                entryElement.appendChild(ele);

                ele = doc.createElement("title_url");
                ele.appendChild(doc.createTextNode(e.getTitleUrl()));
                entryElement.appendChild(ele);

                ele = doc.createElement("tag");
                ele.appendChild(doc.createTextNode(e.getTag()));
                entryElement.appendChild(ele);

                root.appendChild(entryElement);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(file);
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.transform(source, result);

            return true;
        }
        catch (Exception ex) {
            Log.e(LOG_TAG, "XML oluşturma başarısız", ex);
            return false;
        }
    }

    private static String generateNameForBackUpFile() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyyMMddHHmmss");
        Date date = new Date();
        return dateFormat.format(date) + ".xml";
    }

    public static EntryList restoreEntriesFromBackUpFile(File file) {
        Source source = null;
        try {
            String sourceUrlString = "file:///" + file.getAbsolutePath();
            source = new Source(new URL(null, sourceUrlString));
        }
        catch (Exception ex) {
            Log.e("_MK", "Xml okunamadı", ex);
        }

        Element version = source.getFirstElement("version");
        if (version == null) {
            // this means we have an old backup file
            return restoreLegacyBackUpFile(source);
        }
        else if (version.getContent().toString().equals("4.1.0")) {
            return restore410backup(source);
        }

        // 4.1.1 ve sonrası
        EntryList restoredEntries = new EntryList();

        List<Element> list = source.getAllElements("Entry");
        for (Element e : list) {
            Entry entry = new Entry();

            String sozluk = e.getAllElements("sozluk").get(0).getContent().toString();
            if (sozluk.equals("Inci Sözlük")) {
                continue; //inci no more
            }

            String title = e.getAllElements("title").get(0).getContent().toString();
            String body = e.getAllElements("body").get(0).getContent().toString();
            String user = e.getAllElements("user").get(0).getContent().toString();
            String entryNo = e.getAllElements("entryNo").get(0).getContent().toString();
            String date = e.getAllElements("date_time").get(0).getContent().toString();
            String entryUrl = e.getAllElements("entry_url").get(0).getContent().toString();
            String titleUrl = e.getAllElements("title_url").get(0).getContent().toString();
            String tag = e.getAllElements("tag").get(0).getContent().toString();

            entry.setTitle(title);
            entry.setBody(body);
            entry.setUser(user);
            entry.setDateTime(date);
            entry.setEntryNo(Integer.parseInt(entryNo));
            entry.setTitleUrl(titleUrl);
            entry.setSozluk(SozlukEnum.getSozlukEnum(sozluk));
            entry.setTag(tag);
            entry.setEntryUrl(titleUrl);
            entry.setEntryUrl(entryUrl);

            restoredEntries.add(entry);
        }

        return restoredEntries;
    }

    private static EntryList restore410backup(Source source) {
        EntryList restoredEntries = new EntryList();
        EksiSozluk eksi = EksiFactory.getInstance();
        Instela instela = InstelaFactory.getInstance();
        Uludag uludag = UludagFactory.getInstance();

        List<Element> list = source.getAllElements("Entry");
        for (Element e : list) {
            Entry entry = new Entry();

            String tag = e.getAllElements("tag").get(0).getContent().toString();
            String entryNo = e.getAllElements("entryNo").get(0).getContent().toString();
            String title = e.getAllElements("title").get(0).getContent().toString();
            String title_id;
            try {
                title_id = e.getAllElements("title_id").get(0).getContent().toString();
            }
            catch (Exception ex) {
                title_id = "0";
            }

            String user = e.getAllElements("user").get(0).getContent().toString();
            String body = e.getAllElements("body").get(0).getContent().toString();
            String date = e.getAllElements("date_time").get(0).getContent().toString();
            String sozluk = e.getAllElements("sozluk").get(0).getContent().toString();
            if (sozluk.equals("Inci Sözlük")) {
                continue; //inci no more
            }

            String titleUrl = "";
            String entryUrl = "";
            if (sozluk.equals(SozlukEnum.EKSI.getName())) {
                titleUrl = eksi.createTitleLink(title, Integer.parseInt(title_id));
                entryUrl = EKSI_ENTRY_BASE_PATH + entryNo;
            }
            else if (sozluk.equals(SozlukEnum.INSTELA.getName())) {
                titleUrl = instela.createTitleLink(title, Integer.parseInt(title_id));
                entryUrl = INSTELA_ENTRY_BASE_PATH + entryNo;
            }
            else if (sozluk.equals(SozlukEnum.ULUDAG.getName())) {
                titleUrl = uludag.createTitleLink(title);
                entryUrl = ULUDAG_BASE_ENTRY_PATH + entryNo;
            }


            entry.setTitle(title);
            entry.setBody(body);
            entry.setUser(user);
            entry.setDateTime(date);
            entry.setEntryNo(Integer.parseInt(entryNo));
            entry.setTitleUrl(titleUrl);
            entry.setSozluk(SozlukEnum.getSozlukEnum(sozluk));
            entry.setTag(tag);
            entry.setEntryUrl(entryUrl);

            restoredEntries.add(entry);
        }

        return restoredEntries;
    }

    private static EntryList restoreLegacyBackUpFile(Source source) {
        EntryList restoredEntries = new EntryList();
        try {
            List<Element> list = source.getAllElements("Entry");
            for (Element e : list) {
                Entry entry = new Entry();
                String entryNo = e.getAllElements("entryNo").get(
                        0).getContent().toString().substring(1);
                String title = e.getAllElements("baslik").get(0).getContent().toString();
                String title_id;
                try {
                    title_id = e.getAllElements("title_id").get(0).getContent().toString();
                }
                catch (Exception ex) {
                    title_id = "0";
                }

                String author = e.getAllElements("yazar").get(0).getContent().toString();
                String body = e.getAllElements("body").get(0).getContent().toString();
                String date = e.getAllElements("date").get(0).getContent().toString();

                String sozluk;
                String titleUrl;
                String entryUrl;
                try {
                    sozluk = e.getAllElements("sozluk").get(0).getContent().toString();
                    switch (sozluk) {
                        case "0":
                            sozluk = SozlukEnum.EKSI.getName();
                            titleUrl = EksiFactory.getInstance().createTitleLink(title,
                                    Integer.parseInt(title_id));
                            entryUrl = EKSI_ENTRY_BASE_PATH + entryNo;
                            break;
                        case "1":
                            sozluk = SozlukEnum.INSTELA.getName();
                            titleUrl = InstelaFactory.getInstance().createTitleLink(title,
                                    Integer.parseInt(title_id));
                            entryUrl = INSTELA_ENTRY_BASE_PATH + entryNo;
                            break;
                        case "2":
                            sozluk = SozlukEnum.ULUDAG.getName();
                            titleUrl = UludagFactory.getInstance().createTitleLink(title);
                            entryUrl = ULUDAG_BASE_ENTRY_PATH + entryNo;
                            break;
                        default:
                            throw new IllegalStateException();
                    }
                }
                catch (Exception ex) {
                    // eski yedek bu
                    sozluk = SozlukEnum.EKSI.getName();
                    titleUrl = EksiFactory.getInstance().createTitleLink(title,
                            Integer.parseInt(title_id));
                    entryUrl = EKSI_ENTRY_BASE_PATH + entryNo;
                }

                entry.setTitle(title);
                entry.setBody(body);
                entry.setUser(author);
                entry.setDateTime(date);
                entry.setEntryNo(Integer.parseInt(entryNo));
                entry.setTitleUrl(titleUrl);
                entry.setSozluk(SozlukEnum.getSozlukEnum(sozluk));
                entry.setTag(Contract.TAG_SAVE_FOR_GOOD); // this backup is before save_for_later
                entry.setEntryUrl(titleUrl); // what can i do sometimes
                entry.setEntryUrl(entryUrl);
                restoredEntries.add(entry);
            }
        }
        catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage(), ex);
        }
        return restoredEntries;
    }
}
