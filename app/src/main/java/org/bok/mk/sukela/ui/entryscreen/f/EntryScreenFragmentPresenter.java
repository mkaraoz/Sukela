package org.bok.mk.sukela.ui.entryscreen.f;

import org.bok.mk.sukela.R;
import org.bok.mk.sukela.data.model.Contract;
import org.bok.mk.sukela.data.model.Entry;
import org.bok.mk.sukela.data.model.pack.Pack;
import org.bok.mk.sukela.ui.base.BasePresenter;
import org.bok.mk.sukela.util.SharedPrefsHelper;

public class EntryScreenFragmentPresenter extends BasePresenter<EntryScreenFragmentContract.View> implements EntryScreenFragmentContract.Presenter
{
    private final SharedPrefsHelper mPreferences;
    private final Pack mPack;
    private final Entry mCurrentEntry;

    public EntryScreenFragmentPresenter(SharedPrefsHelper sharedPrefsHelper, Pack pack, Entry entry) {
        mPreferences = sharedPrefsHelper;
        mPack = pack;
        mCurrentEntry = entry;
    }

    @Override
    public void createUi() {
        mView.printEntryTitle(mCurrentEntry.getTitle());

        //Set background colors
        String wvBgColor, wvTextColor;
        boolean isInNightMode = mPreferences.get(mView.getStringRes(R.string.key_night_mode),
                false);
        if (isInNightMode) {
            mView.setMainBackgroundColor(R.color.grey_900);
            mView.setTitleBoxBackColor(R.drawable.border_bottom_night);
            mView.setEntryTitleColor(R.color.grey_600);
            wvBgColor = "#212121";
            wvTextColor = "#757575";
        }
        else {
            mView.setMainBackgroundColor(R.color.default_background);
            mView.setTitleBoxBackColor(R.drawable.border_bottom);
            mView.setEntryTitleColor(R.color.black);
            wvBgColor = "#FAFAFA";
            wvTextColor = "#000000";
        }

        mView.setWebViewTextSize(getTextSize());

        // create html text
        String mime = "text/html";
        String encoding = "utf-8";
        String bgColor = "bgcolor=\"" + wvBgColor + "\"";
        String textColor = "text=\"" + wvTextColor + "\"";
        String linkColor = "link=\"" + mPreferences.get(mView.getStringRes(R.string.key_link_color),
                "#008000") + "\"";
        //String linkColor = "link=\"" + mPreferences.get(LINK_COLOR, "#008000") + "\"";

        String bodyParams = "<body " + bgColor + " " + linkColor + " " + textColor + ">";
        String entryHtml = "<style>img{display: inline;height: auto;max-width: 100%;}</style>" + bodyParams + getEntryBody(
                mCurrentEntry) + "<br/><br/><br/><br/><br/>";

        mView.loadEntryBody(entryHtml, mime, encoding);
    }

    @Override
    public int getBottomColor() {
        if (mPack.getTag().equals(Contract.TAG_SAVE_FOR_GOOD) || mPack.getTag().equals(
                Contract.TAG_SAVE_FOR_LATER)) {
            switch (mCurrentEntry.getSozluk()) {
                case EKSI:
                    return R.color.green_600;
                case INSTELA:
                    return R.color.instela;
                case ULUDAG:
                    return R.color.uludag;
                default:
                    throw new IllegalStateException();
            }
        }
        else {
            return mPack.getBottomBarColorId();
        }
    }

    @Override
    public void titleClicked() {
        mView.openUrl(mCurrentEntry.getTitleUrl());
    }

    private String getEntryBody(Entry entry) {
        String entryBody = entry.getBody() + "<br><br>" + "<b>" + entry.getUser() + "</b>" + "<br><br>" + entry.getDateTime() + "<br><br>" + "#" + entry.getEntryNo();
        entryBody = entryBody.replaceAll("&lt;", "<");
        entryBody = entryBody.replaceAll("&gt;", ">");
        entryBody = entryBody.replaceAll("&amp;", "&");
        entryBody = entryBody.replace("<iframe", "<!--iframe");
        entryBody = entryBody.replace("iframe>", "iframe-->");
        return entryBody;
    }

    private int getTextSize() {
        int zoom;
        String fontSize = mPreferences.get(mView.getStringRes(R.string.key_text_size), DEFAULT_FONT_SIZE);
        int size = Integer.valueOf(fontSize);
        switch (size) {
            case 12:
                zoom = 75;
                break;
            case 15:
                zoom = 95;
                break;
            case 18:
                zoom = 115;
                break;
            case 20:
                zoom = 130;
                break;
            case 30:
                zoom = 185;
                break;
            case 40:
                zoom = 245;
                break;
            default:
                zoom = 115;
        }
        return zoom;
    }

    private final static String DEFAULT_FONT_SIZE = "18";
}
