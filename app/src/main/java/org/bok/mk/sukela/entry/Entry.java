package org.bok.mk.sukela.entry;

import org.bok.mk.sukela.helper.SozlukEnum;

import java.io.Serializable;

/**
 * Created by mk on 18.09.2016.
 */
public class Entry implements Serializable
{
    private int entryNo; // bu string olursa başında # var mı yok mu hep karışıyor
    private String title;
    private int titleID;
    private String body;
    private String user;
    private String dateTime;
    private SozlukEnum sozluk;
    private String tag;

    private Entry()
    {
    }

    public static Entry createEntryWithTag(final String tag)
    {
        Entry e = new Entry();
        e.tag = tag;
        return e;
    }

    public Entry createCopyWithAnotherTag(String newTag)
    {
        Entry e = new Entry();
        e.entryNo = this.entryNo;
        e.title = this.title;
        e.titleID = this.titleID;
        e.body = this.body;
        e.user = this.user;
        e.dateTime = this.dateTime;
        e.sozluk = this.sozluk;
        e.tag = newTag;
        return e;
    }

    public String getTag()
    {
        return tag;
    }

    public int getEntryNo()
    {
        return entryNo;
    }

    public void setEntryNo(int entryNo)
    {
        this.entryNo = entryNo;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public int getTitleID()
    {
        return titleID;
    }

    public void setTitleID(int titleID)
    {
        this.titleID = titleID;
    }

    public String getBody()
    {
        return body;
    }

    public void setBody(String body)
    {
        this.body = body;
    }

    public String getUser()
    {
        return user;
    }

    public void setUser(String user)
    {
        this.user = user;
    }

    public String getDateTime()
    {
        return dateTime;
    }

    public void setDateTime(String dateTime)
    {
        this.dateTime = dateTime;
    }

    public SozlukEnum getSozluk()
    {
        return sozluk;
    }

    public void setSozluk(SozlukEnum sozluk)
    {
        this.sozluk = sozluk;
    }
}
