package org.bok.mk.sukela.helper;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.ContextCompat;

import org.bok.mk.sukela.R;
import org.bok.mk.sukela.data.DatabaseContract;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static org.bok.mk.sukela.helper.Contract.TAG_EKSI_2004;
import static org.bok.mk.sukela.helper.Contract.TAG_EKSI_2005;
import static org.bok.mk.sukela.helper.Contract.TAG_EKSI_2006;
import static org.bok.mk.sukela.helper.Contract.TAG_EKSI_2007;
import static org.bok.mk.sukela.helper.Contract.TAG_EKSI_2008;
import static org.bok.mk.sukela.helper.Contract.TAG_EKSI_2009;
import static org.bok.mk.sukela.helper.Contract.TAG_EKSI_2010;
import static org.bok.mk.sukela.helper.Contract.TAG_EKSI_2011;
import static org.bok.mk.sukela.helper.Contract.TAG_EKSI_2012;
import static org.bok.mk.sukela.helper.Contract.TAG_EKSI_2013;
import static org.bok.mk.sukela.helper.Contract.TAG_EKSI_2014;
import static org.bok.mk.sukela.helper.Contract.TAG_EKSI_2015;
import static org.bok.mk.sukela.helper.Contract.TAG_EKSI_2016;
import static org.bok.mk.sukela.helper.Contract.TAG_EKSI_DEBE;
import static org.bok.mk.sukela.helper.Contract.TAG_EKSI_WEEK;
import static org.bok.mk.sukela.helper.Contract.TAG_INCI_HAFTA;
import static org.bok.mk.sukela.helper.Contract.TAG_INCI_YESTERDAY;
import static org.bok.mk.sukela.helper.Contract.TAG_INSTELA_AY;
import static org.bok.mk.sukela.helper.Contract.TAG_INSTELA_HAFTA;
import static org.bok.mk.sukela.helper.Contract.TAG_INSTELA_TOP_20;
import static org.bok.mk.sukela.helper.Contract.TAG_INSTELA_YESTERDAY;
import static org.bok.mk.sukela.helper.Contract.TAG_SAVE_FOR_GOOD;
import static org.bok.mk.sukela.helper.Contract.TAG_SAVE_FOR_LATER;
import static org.bok.mk.sukela.helper.Contract.TAG_SEARCHED_ENTRY;
import static org.bok.mk.sukela.helper.Contract.TAG_ULUDAG_HAFTA;
import static org.bok.mk.sukela.helper.Contract.TAG_ULUDAG_YESTERDAY;
import static org.bok.mk.sukela.helper.Contract.TAG_USER;

/**
 * Created by mk on 18.12.2016.
 */

public final class Meta implements Serializable {
    private String title;
    private String dataUri;
    private Map<String, Integer> floatingColors;
    private String tag;
    private int themeId;
    private int bottomBarColorId;
    private SozlukEnum sozluk;
    private String listUrl;

    private Meta() {
    }

    public String getTitle() {
        return title;
    }

    public Uri getDataUri() {
        return Uri.parse(dataUri);
    }

    public Map<String, Integer> getFloatingColors() {
        return floatingColors;
    }

    public String getTag() {
        return tag;
    }

    public int getThemeId() {
        return themeId;
    }

    public int getBottomBarColorId() {
        return bottomBarColorId;
    }

    public SozlukEnum getSozluk() {
        return sozluk;
    }

    public String getListUrl() {
        return listUrl;
    }

    public static Meta savedForGoodMeta(final Context ctx) {
        Meta meta = new Meta();
        meta.sozluk = null; // can be any one of them
        meta.title = ctx.getResources().getString(R.string.title_save_for_good);
        meta.tag = TAG_SAVE_FOR_GOOD;
        meta.themeId = R.style.SaveTheme;
        meta.listUrl = null;
        meta.bottomBarColorId = R.color.colorPrimary;
        meta.dataUri = Uri.withAppendedPath(DatabaseContract.EntryTable.CONTENT_URI, meta.tag).toString();
        meta.floatingColors = getSaklaFloatingColors(ctx);

        return meta;
    }

    public static Meta savedForLaterMeta(final Context ctx) {
        Meta meta = new Meta();
        meta.sozluk = null; // can be any one of them
        meta.title = ctx.getResources().getString(R.string.title_save_for_later);
        meta.tag = TAG_SAVE_FOR_LATER;
        meta.themeId = R.style.SaveTheme;
        meta.listUrl = null;
        meta.bottomBarColorId = R.color.pink_500;
        meta.dataUri = Uri.withAppendedPath(DatabaseContract.EntryTable.CONTENT_URI, meta.tag).toString();
        meta.floatingColors = getSaklaLongFloatingColors(ctx);

        return meta;
    }

    public static Meta singleEntryMeta(final Context ctx) {
        Meta meta = new Meta();
        meta.sozluk = null; // can be any one of them
        meta.title = "Aranan Entry";
        meta.tag = TAG_SEARCHED_ENTRY;
        meta.themeId = R.style.SearchActivityTheme;
        meta.listUrl = null;
        meta.bottomBarColorId = R.color.colorPrimaryDarkSearch;
        meta.floatingColors = getSaklaLongFloatingColors(ctx);
        return meta;
    }

    public static Meta userMeta(final Context ctx) {
        Meta meta = new Meta();
        meta.sozluk = SozlukEnum.EKSI;
        meta.title = ctx.getResources().getString(R.string.best_of_user);
        meta.tag = TAG_USER;
        meta.themeId = R.style.EksiTheme;
        meta.listUrl = "https://eksisozluk.com/basliklar/istatistik/";
        meta.bottomBarColorId = R.color.green_500;
        meta.dataUri = Uri.withAppendedPath(DatabaseContract.EntryTable.CONTENT_URI, meta.tag).toString();
        meta.floatingColors = getEksiFloatingColors(ctx);

        return meta;
    }

    public static Meta eksiGundemMeta(final Context ctx) {
        Meta meta = new Meta();
        meta.sozluk = SozlukEnum.EKSI;
        meta.title = ctx.getResources().getString(R.string.eksi_gundem);
        meta.tag = Contract.TAG_EKSI_GNDM;
        meta.themeId = R.style.EksiTheme;
        meta.listUrl = "https://eksisozluk.com/";
        meta.bottomBarColorId = R.color.green_600;
        meta.dataUri = Uri.withAppendedPath(DatabaseContract.EntryTable.CONTENT_URI, meta.tag).toString();
        meta.floatingColors = getEksiFloatingColors(ctx);

        return meta;
    }

    public static Meta eksiDebeMeta(final Context ctx) {
        Meta meta = new Meta();
        meta.sozluk = SozlukEnum.EKSI;
        meta.title = ctx.getResources().getString(R.string.best_of_yesterday);
        meta.tag = TAG_EKSI_DEBE;
        meta.themeId = R.style.EksiTheme;
        // meta.listUrl = "https://eksisozluk.com/istatistik/dunun-en-begenilen-entryleri";
        meta.listUrl = "http://sozlock.com/";
        meta.bottomBarColorId = R.color.green_600;
        meta.dataUri = Uri.withAppendedPath(DatabaseContract.EntryTable.CONTENT_URI, meta.tag).toString();
        meta.floatingColors = getEksiFloatingColors(ctx);

        return meta;
    }

    public static Meta eksiHebeMeta(final Context ctx) {
        Meta meta = new Meta();
        meta.sozluk = SozlukEnum.EKSI;
        meta.title = ctx.getResources().getString(R.string.best_of_week);
        meta.tag = TAG_EKSI_WEEK;
        meta.themeId = R.style.EksiTheme;
        meta.listUrl = "https://eksisozluk.com/istatistik/gecen-haftanin-en-begenilen-entryleri";
        meta.bottomBarColorId = R.color.green_600;
        meta.dataUri = Uri.withAppendedPath(DatabaseContract.EntryTable.CONTENT_URI, meta.tag).toString();
        meta.floatingColors = getEksiFloatingColors(ctx);

        return meta;
    }

    public static Meta instelaTop20Meta(final Context ctx) {
        Meta meta = new Meta();
        meta.sozluk = SozlukEnum.INSTELA;
        meta.title = ctx.getResources().getString(R.string.top20);
        meta.tag = TAG_INSTELA_TOP_20;
        meta.themeId = R.style.InstelaTheme;
        meta.listUrl = "https://tr.instela.com/stats/top20";
        meta.bottomBarColorId = R.color.instela;
        meta.dataUri = Uri.withAppendedPath(DatabaseContract.EntryTable.CONTENT_URI, meta.tag).toString();
        meta.floatingColors = getInstelaFloatingColors(ctx);
        return meta;
    }

    public static Meta instelaYesterdayMeta(final Context ctx) {
        Meta meta = new Meta();
        meta.sozluk = SozlukEnum.INSTELA;
        meta.title = ctx.getResources().getString(R.string.best_of_yesterday);
        meta.tag = TAG_INSTELA_YESTERDAY;
        meta.themeId = R.style.InstelaTheme;
        meta.listUrl = "https://tr.instela.com/stats/yesterday";
        meta.bottomBarColorId = R.color.instela;
        meta.dataUri = Uri.withAppendedPath(DatabaseContract.EntryTable.CONTENT_URI, meta.tag).toString();
        meta.floatingColors = getInstelaFloatingColors(ctx);
        return meta;
    }

    public static Meta instelaWeekMeta(final Context ctx) {
        Meta meta = new Meta();
        meta.sozluk = SozlukEnum.INSTELA;
        meta.title = ctx.getResources().getString(R.string.best_of_week);
        meta.tag = TAG_INSTELA_HAFTA;
        meta.themeId = R.style.InstelaTheme;
        meta.listUrl = "https://tr.instela.com/stats/lastweek";
        meta.bottomBarColorId = R.color.instela;
        meta.dataUri = Uri.withAppendedPath(DatabaseContract.EntryTable.CONTENT_URI, meta.tag).toString();
        meta.floatingColors = getInstelaFloatingColors(ctx);
        return meta;
    }

    public static Meta instelaMonthMeta(final Context ctx) {
        Meta meta = new Meta();
        meta.sozluk = SozlukEnum.INSTELA;
        meta.title = ctx.getResources().getString(R.string.best_of_month);
        meta.tag = TAG_INSTELA_AY;
        meta.themeId = R.style.InstelaTheme;
        meta.listUrl = "https://tr.instela.com/stats/lastmonth";
        meta.bottomBarColorId = R.color.instela;
        meta.dataUri = Uri.withAppendedPath(DatabaseContract.EntryTable.CONTENT_URI, meta.tag).toString();
        meta.floatingColors = getInstelaFloatingColors(ctx);
        return meta;
    }

    public static Meta uludagYesterdayMeta(final Context ctx) {
        Meta meta = new Meta();
        meta.sozluk = SozlukEnum.ULUDAG;
        meta.title = ctx.getResources().getString(R.string.best_of_yesterday);
        meta.tag = TAG_ULUDAG_YESTERDAY;
        meta.themeId = R.style.UludagTheme;
        meta.listUrl = "https://www.uludagsozluk.com/index.php?sa=istatistik&is=503&ic=entry_dun_dikkatceken";
        meta.bottomBarColorId = R.color.uludag;
        meta.dataUri = Uri.withAppendedPath(DatabaseContract.EntryTable.CONTENT_URI, meta.tag).toString();
        meta.floatingColors = getUludagFloatingColors(ctx);

        return meta;
    }

    public static Meta uludagWeekMeta(final Context ctx) {
        Meta meta = new Meta();
        meta.sozluk = SozlukEnum.ULUDAG;
        meta.title = ctx.getResources().getString(R.string.best_of_week);
        meta.tag = TAG_ULUDAG_HAFTA;
        meta.themeId = R.style.UludagTheme;
        meta.listUrl = "https://www.uludagsozluk.com/index.php?sa=istatistik&is=501&ic=entry_gecen_enbegenilen";
        meta.bottomBarColorId = R.color.uludag;
        meta.dataUri = Uri.withAppendedPath(DatabaseContract.EntryTable.CONTENT_URI, meta.tag).toString();
        meta.floatingColors = getUludagFloatingColors(ctx);

        return meta;
    }

    public static Meta inciYesterdayMeta(final Context ctx) {
        Meta meta = new Meta();
        meta.sozluk = SozlukEnum.INCI;
        meta.title = ctx.getResources().getString(R.string.best_of_yesterday);
        meta.tag = TAG_INCI_YESTERDAY;
        meta.themeId = R.style.InciTheme;
        meta.listUrl = "http://www.incisozluk.com.tr/index.php?sa=istatistik&is=503&ic=entry_dun_dikkatceken";
        meta.bottomBarColorId = R.color.inci;
        meta.dataUri = Uri.withAppendedPath(DatabaseContract.EntryTable.CONTENT_URI, meta.tag).toString();
        meta.floatingColors = getInciFloatingColors(ctx);

        return meta;
    }

    public static Meta inciWeekMeta(final Context ctx) {
        Meta meta = new Meta();
        meta.sozluk = SozlukEnum.INCI;
        meta.title = ctx.getResources().getString(R.string.best_of_week);
        meta.tag = TAG_INCI_HAFTA;
        meta.themeId = R.style.InciTheme;
        meta.listUrl = "http://www.incisozluk.com.tr/index.php?sa=istatistik&is=5011&ic=baslik_gecen_enbegenilen";
        meta.bottomBarColorId = R.color.inci;
        meta.dataUri = Uri.withAppendedPath(DatabaseContract.EntryTable.CONTENT_URI, meta.tag).toString();
        meta.floatingColors = getInciFloatingColors(ctx);

        return meta;
    }

    public static Meta eksiYearMeta(final Context ctx, final int year) {
        Meta meta = new Meta();
        meta.sozluk = SozlukEnum.EKSI;

        if (year == 2016) {
            meta.title = ctx.getResources().getString(R.string.best_of_2016);
            meta.tag = TAG_EKSI_2016;
            meta.themeId = R.style.Eksi_Year_Theme;
            meta.listUrl = "https://www.dropbox.com/s/gfzplirk0krb8gv/2016.xml?dl=1";
        } else if (year == 2015) {
            meta.title = ctx.getResources().getString(R.string.best_of_2015);
            meta.tag = TAG_EKSI_2015;
            meta.themeId = R.style.Eksi_Year_Theme;
            meta.listUrl = "https://www.dropbox.com/s/7e5qgoilcvkaicm/2015.xml?dl=1";
        } else if (year == 2014) {
            meta.title = ctx.getResources().getString(R.string.best_of_2014);
            meta.tag = TAG_EKSI_2014;
            meta.themeId = R.style.Eksi_Year_Theme;
            meta.listUrl = "https://www.dropbox.com/s/98woolcbmlxe2cd/2014.xml?dl=1";
        } else if (year == 2013) {
            meta.title = ctx.getResources().getString(R.string.best_of_2013);
            meta.tag = TAG_EKSI_2013;
            meta.themeId = R.style.Eksi_Year_Theme;
            meta.listUrl = "https://www.dropbox.com/s/xvyrj87ug7jsqyb/2013.xml?dl=1";
        } else if (year == 2012) {
            meta.title = ctx.getResources().getString(R.string.best_of_2012);
            meta.tag = TAG_EKSI_2012;
            meta.themeId = R.style.Eksi_Year_Theme;
            meta.listUrl = "https://www.dropbox.com/s/06ayw5umzo1m3p6/2012.xml?dl=1";
        } else if (year == 2011) {
            meta.title = ctx.getResources().getString(R.string.best_of_2011);
            meta.tag = TAG_EKSI_2011;
            meta.themeId = R.style.Eksi_Year_Theme;
            meta.listUrl = "https://www.dropbox.com/s/q0bi0aked6kickp/2011.xml?dl=1";
        } else if (year == 2010) {
            meta.title = ctx.getResources().getString(R.string.best_of_2010);
            meta.tag = TAG_EKSI_2010;
            meta.themeId = R.style.Eksi_Year_Theme;
            meta.listUrl = "https://www.dropbox.com/s/fg747yot5033ope/2010.xml?dl=1";
        } else if (year == 2009) {
            meta.title = ctx.getResources().getString(R.string.best_of_2009);
            meta.tag = TAG_EKSI_2009;
            meta.themeId = R.style.Eksi_Year_Theme;
            meta.listUrl = "https://www.dropbox.com/s/n1dz64ypllevvdp/2009.xml?dl=1";
        } else if (year == 2008) {
            meta.title = ctx.getResources().getString(R.string.best_of_2008);
            meta.tag = TAG_EKSI_2008;
            meta.themeId = R.style.Eksi_Year_Theme;
            meta.listUrl = "https://www.dropbox.com/s/b5o5bj1fczm3d8i/2008.xml?dl=1";
        } else if (year == 2007) {
            meta.title = ctx.getResources().getString(R.string.best_of_2007);
            meta.tag = TAG_EKSI_2007;
            meta.themeId = R.style.Eksi_Year_Theme;
            meta.listUrl = "https://www.dropbox.com/s/uhp10tinim2je7k/2007.xml?dl=1";
        } else if (year == 2006) {
            meta.title = ctx.getResources().getString(R.string.best_of_2006);
            meta.tag = TAG_EKSI_2006;
            meta.themeId = R.style.Eksi_Year_Theme;
            meta.listUrl = "https://www.dropbox.com/s/xxh73s75296y8bf/2006.xml?dl=1";
        } else if (year == 2005) {
            meta.title = ctx.getResources().getString(R.string.best_of_2005);
            meta.tag = TAG_EKSI_2005;
            meta.themeId = R.style.Eksi_Year_Theme;
            meta.listUrl = "https://www.dropbox.com/s/dc075z2wq24gu19/2005.xml?dl=1";
        } else if (year == 2004) {
            meta.title = ctx.getResources().getString(R.string.best_of_2004);
            meta.tag = TAG_EKSI_2004;
            meta.themeId = R.style.Eksi_Year_Theme;
            meta.listUrl = "https://www.dropbox.com/s/xhk0h6ma62qdar2/2004.xml?dl=1";
        }

        meta.bottomBarColorId = R.color.green_600;
        meta.floatingColors = getEksiFloatingColors(ctx);
        meta.dataUri = Uri.withAppendedPath(DatabaseContract.EntryTable.CONTENT_URI, meta.tag).toString();

        return meta;
    }

    private static Map<String, Integer> getEksiFloatingColors(Context ctx) {
        return createFloatingColorsMap(ctx, R.color.green_600, R.color.green_800, R.color.green_900, R.color.eksi, R.color.green_C100, R.color.green_C200);
    }

    private static Map<String, Integer> getSaklaFloatingColors(Context ctx) {
        return createFloatingColorsMap(ctx, R.color.colorPrimary, R.color.blue_700, R.color.colorPrimaryDark, R.color.blue_500, R.color.blue_600, R.color.blue_800);
    }

    private static Map<String, Integer> getSaklaLongFloatingColors(Context ctx) {
        return createFloatingColorsMap(ctx, R.color.pink_500, R.color.pink_600, R.color.pink_800, R.color.pink_400, R.color.pink_500, R.color.pink_600);
    }

    private static Map<String, Integer> getInstelaFloatingColors(Context ctx) {
        return createFloatingColorsMap(ctx, R.color.instela, R.color.cyan_800, R.color.cyan_900, R.color.cyan_500, R.color.cyan_600, R.color.cyan_800);
    }

    private static Map<String, Integer> getInciFloatingColors(Context ctx) {
        return createFloatingColorsMap(ctx, R.color.inci, R.color.amber_700, R.color.amber_900, R.color.amber_400, R.color.amber_500, R.color.amber_800);
    }

    private static Map<String, Integer> getUludagFloatingColors(Context ctx) {
        return createFloatingColorsMap(ctx, R.color.uludag, R.color.dark_purple_700, R.color.dark_purple_900, R.color.dark_purple_400, R.color.dark_purple_500, R.color.dark_purple_700);
    }

    private static Map<String, Integer> createFloatingColorsMap(Context ctx, int menuColorNormal, int menuColorPressed, int menuColorRipple, int itemColorNormal, int itemColorPressed, int itemColorRipple) {
        Map<String, Integer> floatingColors = new HashMap<>();
        floatingColors.put(Contract.FAB_MENU_COLOR_NORMAL, ContextCompat.getColor(ctx, menuColorNormal));
        floatingColors.put(Contract.FAB_MENU_COLOR_PRESSED, ContextCompat.getColor(ctx, menuColorPressed));
        floatingColors.put(Contract.FAB_MENU_COLOR_RIPPLE, ContextCompat.getColor(ctx, menuColorRipple));
        floatingColors.put(Contract.FAB_ITEM_COLOR_NORMAL, ContextCompat.getColor(ctx, itemColorNormal));
        floatingColors.put(Contract.FAB_ITEM_COLOR_PRESSED, ContextCompat.getColor(ctx, itemColorPressed));
        floatingColors.put(Contract.FAB_ITEM_COLOR_RIPPLE, ContextCompat.getColor(ctx, itemColorRipple));
        return floatingColors;
    }

    public void setSozluk(SozlukEnum sozluk) {
        this.sozluk = sozluk;
    }
}
