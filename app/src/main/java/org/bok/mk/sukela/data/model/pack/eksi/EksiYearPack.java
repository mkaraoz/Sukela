package org.bok.mk.sukela.data.model.pack.eksi;

import java.util.HashMap;
import java.util.Map;

public class EksiYearPack extends EksiPack
{
    private final Year mYear;
    private static final Map<Year, String> titleMap = new HashMap<>();
    private static final Map<Year, String> tagMap = new HashMap<>();

    private final static String TITLE_2016 = "2016'nın en beğenilenleri";
    private final static String TITLE_2015 = "2015'in en beğenilenleri";
    private final static String TITLE_2014 = "2014'ün en beğenilenleri";
    private final static String TITLE_2013 = "2013'ün en beğenilenleri";
    private final static String TITLE_2012 = "2012'nin en beğenilenleri";
    private final static String TITLE_2011 = "2011'in en beğenilenleri";
    private final static String TITLE_2010 = "2010'nun en beğenilenleri";
    private final static String TITLE_2009 = "2009'un en beğenilenleri";
    private final static String TITLE_2008 = "2008'in en beğenilenleri";
    private final static String TITLE_2007 = "2007'nin en beğenilenleri";
    private final static String TITLE_2006 = "2006'nın en beğenilenleri";
    private final static String TITLE_2005 = "2005'in en beğenilenleri";
    private final static String TITLE_2004 = "2004'ün en beğenilenleri";

    private static final String TAG_EKSI_2016 = "TAG_EKSI_2016";
    private static final String TAG_EKSI_2015 = "TAG_EKSI_2015";
    private static final String TAG_EKSI_2014 = "TAG_EKSI_2014";
    private static final String TAG_EKSI_2013 = "TAG_EKSI_2013";
    private static final String TAG_EKSI_2012 = "TAG_EKSI_2012";
    private static final String TAG_EKSI_2011 = "TAG_EKSI_2011";
    private static final String TAG_EKSI_2010 = "TAG_EKSI_2010";
    private static final String TAG_EKSI_2009 = "TAG_EKSI_2009";
    private static final String TAG_EKSI_2008 = "TAG_EKSI_2008";
    private static final String TAG_EKSI_2007 = "TAG_EKSI_2007";
    private static final String TAG_EKSI_2006 = "TAG_EKSI_2006";
    private static final String TAG_EKSI_2005 = "TAG_EKSI_2005";
    private static final String TAG_EKSI_2004 = "TAG_EKSI_2004";

    static {
        titleMap.put(Year.a2016, TITLE_2016);
        titleMap.put(Year.a2015, TITLE_2015);
        titleMap.put(Year.a2014, TITLE_2014);
        titleMap.put(Year.a2013, TITLE_2013);
        titleMap.put(Year.a2012, TITLE_2012);
        titleMap.put(Year.a2011, TITLE_2011);
        titleMap.put(Year.a2010, TITLE_2010);
        titleMap.put(Year.a2009, TITLE_2009);
        titleMap.put(Year.a2008, TITLE_2008);
        titleMap.put(Year.a2007, TITLE_2007);
        titleMap.put(Year.a2006, TITLE_2006);
        titleMap.put(Year.a2005, TITLE_2005);
        titleMap.put(Year.a2004, TITLE_2004);

        tagMap.put(Year.a2016, TAG_EKSI_2016);
        tagMap.put(Year.a2015, TAG_EKSI_2015);
        tagMap.put(Year.a2014, TAG_EKSI_2014);
        tagMap.put(Year.a2013, TAG_EKSI_2013);
        tagMap.put(Year.a2012, TAG_EKSI_2012);
        tagMap.put(Year.a2011, TAG_EKSI_2011);
        tagMap.put(Year.a2010, TAG_EKSI_2010);
        tagMap.put(Year.a2009, TAG_EKSI_2009);
        tagMap.put(Year.a2008, TAG_EKSI_2008);
        tagMap.put(Year.a2007, TAG_EKSI_2007);
        tagMap.put(Year.a2006, TAG_EKSI_2006);
        tagMap.put(Year.a2005, TAG_EKSI_2005);
        tagMap.put(Year.a2004, TAG_EKSI_2004);
    }

    public EksiYearPack(final Year year) {
        mYear = year;
    }

    @Override
    public String getTitle() {
        return titleMap.get(mYear);
    }

    @Override
    public String getTag() {
        return tagMap.get(mYear);
    }

    public static String getDownloadLink(int year)
    {
        if (year == 2016) {
            return "https://www.dropbox.com/s/gfzplirk0krb8gv/2016.xml?dl=1";
        } else if (year == 2015) {
            return "https://www.dropbox.com/s/7e5qgoilcvkaicm/2015.xml?dl=1";
        } else if (year == 2014) {
            return "https://www.dropbox.com/s/98woolcbmlxe2cd/2014.xml?dl=1";
        } else if (year == 2013) {
            return "https://www.dropbox.com/s/xvyrj87ug7jsqyb/2013.xml?dl=1";
        } else if (year == 2012) {
            return "https://www.dropbox.com/s/06ayw5umzo1m3p6/2012.xml?dl=1";
        } else if (year == 2011) {
            return "https://www.dropbox.com/s/q0bi0aked6kickp/2011.xml?dl=1";
        } else if (year == 2010) {
            return "https://www.dropbox.com/s/fg747yot5033ope/2010.xml?dl=1";
        } else if (year == 2009) {
            return "https://www.dropbox.com/s/n1dz64ypllevvdp/2009.xml?dl=1";
        } else if (year == 2008) {
            return "https://www.dropbox.com/s/b5o5bj1fczm3d8i/2008.xml?dl=1";
        } else if (year == 2007) {
            return "https://www.dropbox.com/s/uhp10tinim2je7k/2007.xml?dl=1";
        } else if (year == 2006) {
            return "https://www.dropbox.com/s/xxh73s75296y8bf/2006.xml?dl=1";
        } else if (year == 2005) {
            return "https://www.dropbox.com/s/dc075z2wq24gu19/2005.xml?dl=1";
        } else if (year == 2004) {
            return "https://www.dropbox.com/s/xhk0h6ma62qdar2/2004.xml?dl=1";
        }
        else throw new RuntimeException("Hatalı yıl");
    }

    public enum Year
    {
        a2016("2016"),
        a2015("2015"),
        a2014("2014"),
        a2013("2013"),
        a2012("2012"),
        a2011("2011"),
        a2010("2010"),
        a2009("2009"),
        a2008("2008"),
        a2007("2007"),
        a2006("2006"),
        a2005("2005"),
        a2004("2004");

        private String year;

        Year(String year) {
            this.year = year;
        }

        private static final Map<String, Year> reverseLookupMap = new HashMap<>();

        static {
            for (Year y : Year.values()) {
                reverseLookupMap.put(y.year, y);
            }
        }

        public static Year getYearEnum(final String year) {
            return reverseLookupMap.get(year);
        }
    }
}
