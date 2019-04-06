package org.bok.mk.sukela.data.model.pack.eksi;

import org.bok.mk.sukela.data.model.Contract;

public class ArsivPack extends EksiPack
{
    private final int y;
    private final int m;
    private final int d;

    public ArsivPack(int y, int m, int d) {
        this.y = y;
        this.m = m;
        this.d = d;
    }

    public static int[] split(String tag) {
        tag = tag.substring(12);
        String dates[] = tag.split("-");
        int d[] = new int[3];
        d[0] = Integer.parseInt(dates[0]);
        d[1] = Integer.parseInt(dates[1]);
        d[2] = Integer.parseInt(dates[2]);
        return d;
    }

    @Override
    public String getTitle() {
        return y + "-" + (m < 10 ? ("0" + m) : (m)) + "-" + (d < 10 ? ("0" + d) : (d));
    }

    @Override
    public String getTag() {
        return createTag(y, m, d);
    }

    private static String createTag(int y, int m, int d) {
        return Contract.TAG_ARCHIVE_DAY + "_" + y + "-" + (m < 10 ? ("0" + m) : (m)) + "-" + (d < 10 ? ("0" + d) : (d));
    }

    public static String getDateFromTag(String tag) {
        String year = tag.substring(12);
        year = year.replaceAll("-", "");
        return year;
    }
}
