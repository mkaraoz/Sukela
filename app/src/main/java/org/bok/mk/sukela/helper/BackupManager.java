package org.bok.mk.sukela.helper;

import android.util.Log;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;

import org.bok.mk.sukela.BuildConfig;
import org.bok.mk.sukela.entry.Entry;
import org.bok.mk.sukela.entry.EntryList;
import org.bok.mk.sukela.helper.exception.JerichoFileReadException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by mk on 15.01.2017.
 */

public class BackupManager
{
    public static EntryList restoreLegacyBackUpFile(final Source source)
    {
        EntryList restoredEntries = new EntryList();
        try
        {
            List<Element> list = source.getAllElements("Entry");
            for (Element e : list)
            {
                Entry entry = Entry.createEntryWithTag(Contract.TAG_SAVE_FOR_GOOD);
                String entryNo = e.getAllElements("entryNo").get(0).getContent().toString().substring(1);
                String title = e.getAllElements("baslik").get(0).getContent().toString();
                String title_id;
                try
                {
                    title_id = e.getAllElements("title_id").get(0).getContent().toString();
                }
                catch (Exception ex)
                {
                    title_id = "0";
                }

                String author = e.getAllElements("yazar").get(0).getContent().toString();
                String body = e.getAllElements("body").get(0).getContent().toString();
                String date = e.getAllElements("date").get(0).getContent().toString();

                String sozluk;
                try
                {
                    sozluk = e.getAllElements("sozluk").get(0).getContent().toString();
                    switch (sozluk)
                    {
                        case "0":
                            sozluk = SozlukEnum.EKSI.getName();
                            break;
                        case "1":
                            sozluk = SozlukEnum.INSTELA.getName();
                            break;
                        case "2":
                            sozluk = SozlukEnum.ULUDAG.getName();
                            break;
                        default:
                            throw new IllegalStateException();
                    }
                }
                catch (Exception ex)
                {
                    // eski yedek bu
                    sozluk = SozlukEnum.EKSI.getName();
                }

                entry.setTitle(title);
                entry.setDateTime(date);
                entry.setBody(body);
                entry.setEntryNo(Integer.parseInt(entryNo));
                entry.setUser(author);
                entry.setSozluk(SozlukEnum.getSozlukEnum(sozluk));
                entry.setTitleID(Integer.parseInt(title_id));
                restoredEntries.add(entry);
            }
        }
        catch (Exception ex)
        {
            Log.e("_MK", "Xml okunamadı", ex);
        }
        return restoredEntries;
    }

    public static EntryList restoreEntriesFromBackUpFile(File backupFile)
    {
        Source source = null;
        try
        {
            String sourceUrlString = "file:///" + backupFile.getAbsolutePath();
            source = new Source(new URL(null, sourceUrlString));
        }
        catch (Exception ex)
        {
            Log.e("_MK", "Xml okunamadı", ex);
        }

        Element version = source.getFirstElement("version");
        if (version == null)
        {
            // this means we have an old backup file
            return restoreLegacyBackUpFile(source);
        }

        EntryList restoredEntries = new EntryList();

        List<Element> list = source.getAllElements("Entry");
        for (Element e : list)
        {
            String tag = e.getAllElements("tag").get(0).getContent().toString();
            Entry entry = Entry.createEntryWithTag(tag);
            String entryNo = e.getAllElements("entryNo").get(0).getContent().toString();
            String title = e.getAllElements("title").get(0).getContent().toString();
            String title_id;
            try
            {
                title_id = e.getAllElements("title_id").get(0).getContent().toString();
            }
            catch (Exception ex)
            {
                title_id = "0";
            }

            String user = e.getAllElements("user").get(0).getContent().toString();
            String body = e.getAllElements("body").get(0).getContent().toString();
            String date = e.getAllElements("date_time").get(0).getContent().toString();
            String sozluk = e.getAllElements("sozluk").get(0).getContent().toString();

            entry.setTitle(title);
            entry.setDateTime(date);
            entry.setBody(body);
            entry.setEntryNo(Integer.parseInt(entryNo));
            entry.setUser(user);
            entry.setSozluk(SozlukEnum.getSozlukEnum(sozluk));
            entry.setTitleID(Integer.parseInt(title_id));

            restoredEntries.add(entry);
        }

        return restoredEntries;
    }

    public static boolean saveEntriesToSdCard(EntryList entryList, File file)
    {
        try
        {
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

            org.w3c.dom.Element ele = null;
            org.w3c.dom.Element entryElement = null;

            for (Entry e : entryList)
            {
                entryElement = doc.createElement("Entry");

                ele = doc.createElement("entryNo");
                ele.appendChild(doc.createTextNode(String.valueOf(e.getEntryNo())));
                entryElement.appendChild(ele);

                ele = doc.createElement("title");
                ele.appendChild(doc.createTextNode(e.getTitle()));
                entryElement.appendChild(ele);

                ele = doc.createElement("title_id");
                ele.appendChild(doc.createTextNode(String.valueOf(e.getTitleID())));
                entryElement.appendChild(ele);

                ele = doc.createElement("body");
                ele.appendChild(doc.createTextNode(e.getBody()));
                entryElement.appendChild(ele);

                ele = doc.createElement("user");
                ele.appendChild(doc.createTextNode(e.getUser()));
                entryElement.appendChild(ele);

                ele = doc.createElement("date_time");
                ele.appendChild(doc.createTextNode(e.getDateTime()));
                entryElement.appendChild(ele);

                ele = doc.createElement("tag");
                ele.appendChild(doc.createTextNode(e.getTag()));
                entryElement.appendChild(ele);

                ele = doc.createElement("sozluk");
                ele.appendChild(doc.createTextNode(e.getSozluk().getName()));
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
        catch (Exception ex)
        {
            Log.e("_MK", "XML oluşturma başarısız", ex);
            return false;
        }
    }

    public static boolean saveEntriesToSdCard(EntryList entryList, String fileName)
    {
        return saveEntriesToSdCard(entryList, new File(fileName));
    }

    public static EntryList readEntriesFromXmlFile(File xmlFile) throws JerichoFileReadException
    {
        EntryList entryList = new EntryList();
        String sourceUrlString = "file:" + xmlFile;
        net.htmlparser.jericho.Source entrySource = null;
        try
        {
            entrySource = new net.htmlparser.jericho.Source(new URL(null, sourceUrlString));
        }
        catch (IOException e)
        {
            Log.e("_MK", "Jericho could not read file: " + xmlFile, e);
            throw new JerichoFileReadException("İndirilen XML dosyası okunamadı. Tekrar indirmeyi deneyin.");
        }

        List<Element> list = entrySource.getAllElements("Entry");

        for (Element e : list)
        {
            String entryNo = e.getAllElements("entryNo").get(0).getContent().toString();
            String title = e.getAllElements("title").get(0).getContent().toString();
            String titleId = e.getAllElements("title_id").get(0).getContent().toString();
            String body = e.getAllElements("body").get(0).getContent().toString();
            String user = e.getAllElements("user").get(0).getContent().toString();
            String dateTime = e.getAllElements("date_time").get(0).getContent().toString();
            String tag = e.getAllElements("tag").get(0).getContent().toString();

            Entry entry = Entry.createEntryWithTag(tag);
            entry.setEntryNo(Integer.parseInt(entryNo));
            entry.setTitle(title);
            entry.setTitleID(Integer.parseInt(titleId));
            entry.setBody(body);
            entry.setUser(user);
            entry.setDateTime(dateTime);
            entry.setSozluk(SozlukEnum.EKSI);

            entryList.add(entry);
        }
        return entryList;
    }

    public static EntryList readEntriesFromXmlFile(String xmlFile) throws JerichoFileReadException
    {
        return readEntriesFromXmlFile(new File(xmlFile));
    }
}
