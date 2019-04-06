package org.bok.mk.sukela.data.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "entry2")
public class Entry implements Serializable
{
    @PrimaryKey(autoGenerate = true) private int id;
    private String title;
    private String body;
    private String user;
    private String dateTime;
    private int entryNo;
    private String titleUrl;
    private SozlukEnum sozluk;
    private String tag;
    private String entryUrl;

    @Ignore
    public Entry() {
    }

    // for room db
    public Entry(int id, String title, String body, String user, String dateTime, int entryNo, String titleUrl, String entryUrl, SozlukEnum sozluk, String tag) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.user = user;
        this.dateTime = dateTime;
        this.entryNo = entryNo;
        this.titleUrl = titleUrl;
        this.entryUrl = entryUrl;
        this.sozluk = sozluk;
        this.tag = tag;
    }

    public Entry(Entry e) {
        this.title = e.getTitle();
        this.body = e.getBody();
        this.user = e.getUser();
        this.dateTime = e.getDateTime();
        this.entryNo = e.getEntryNo();
        this.titleUrl = e.getTitleUrl();
        this.entryUrl = e.getEntryUrl();
        this.sozluk = e.getSozluk();
        this.tag = e.getTag();
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public int getEntryNo() {
        return entryNo;
    }

    public void setEntryNo(int entryNo) {
        this.entryNo = entryNo;
    }

    public SozlukEnum getSozluk() {
        return sozluk;
    }

    public void setSozluk(SozlukEnum sozluk) {
        this.sozluk = sozluk;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTitleUrl() {
        return titleUrl;
    }

    public void setTitleUrl(String titleUrl) {
        this.titleUrl = titleUrl;
    }

    public String getEntryUrl() {
        return entryUrl;
    }

    public void setEntryUrl(String entryUrl) {
        this.entryUrl = entryUrl;
    }
}
