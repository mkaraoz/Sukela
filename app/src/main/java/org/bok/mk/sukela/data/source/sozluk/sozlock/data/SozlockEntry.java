package org.bok.mk.sukela.data.source.sozluk.sozlock.data;

public class SozlockEntry
{
    private String title;
    private String body;
    private String user;
    private String eksiLink;
    private String date;
    private String titleUrl;
    private int entryNo;
    private String entryUrl;

    public int getEntryNo() {
        return entryNo;
    }

    public void setEntryNo(int entryNo) {
        this.entryNo = entryNo;
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

    public String getEksiLink() {
        return eksiLink;
    }

    public void setEksiLink(String eksiLink) {
        this.eksiLink = eksiLink;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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
