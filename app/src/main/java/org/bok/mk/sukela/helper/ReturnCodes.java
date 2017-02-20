package org.bok.mk.sukela.helper;

/**
 * Created by mk on 23.12.2016.
 */

public class ReturnCodes
{
    public static final int SUCCESS_LOCAL_STORAGE = 0;
    public static final int SUCCESS_DOWNLOAD = 1;
    public static final int OFFLINE = 2;//"İnternet bağlantısı yok";
    public static final int YEAR_LIST_DOWNLOAD_FAILED = 3; //"Entriler indirilemedi. Dropbox.com'a erişebildiğinizden emin olup tekrar deneyin.";
    public static final int XML_FILE_READ_ERROR = 4;
    public static final int MISSING_XML_FILE = 5;
    public static final int ENTRY_LIST_PARSE_ERROR = 6;
    public static final int ENTRY_LIST_DOWNLOAD_FAILED = 7;
    public static final int ENTRY_DOWNLOAD_FAILED = 8;
    public static final int ENTRY_PARSE_ERROR = 9;
    public static final int INSTELA_DOWNLOAD_FAILED = 10;
    public static final int FILE_READ_ERROR = 11;
    public static final int MISSING_FILE = 12;
    public static final int DOWNLOAD_CANCELLED = 13;
    public static final int UNPOPULAR_USER = 14;
    public static final int PAGE_DOWNLOAD_FAILED = 15;
}
