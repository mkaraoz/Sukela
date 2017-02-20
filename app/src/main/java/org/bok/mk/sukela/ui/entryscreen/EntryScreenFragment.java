package org.bok.mk.sukela.ui.entryscreen;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.bok.mk.sukela.R;
import org.bok.mk.sukela.entry.Entry;
import org.bok.mk.sukela.helper.Contract;
import org.bok.mk.sukela.helper.Meta;
import org.bok.mk.sukela.helper.SukelaPrefs;
import org.bok.mk.sukela.source.Sozluk;

/**
 * Created by mk on 18.12.2016.
 */

public class EntryScreenFragment extends Fragment {
    private TextView mTitleBox;
    private WebView mBodyWebView;
    private Meta META;
    private Entry mCurrentEntry;
    private RelativeLayout mContentMain;
    private RelativeLayout mTitleBoxContainer;
    private SukelaPrefs sukelaPrefs;
    private boolean mIsCurrentModeNight = false;
    private String mWebViewLinkColor, mWebViewBgColor, mWebViewTextColor;

    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_entry_screen, container, false);
        sukelaPrefs = SukelaPrefs.instance(getActivity());

        Bundle args = getArguments();
        if (args != null) {
            META = (Meta) args.getSerializable(Contract.META);
            mCurrentEntry = (Entry) args.getSerializable(Contract.ENTRY);
            init(rootView);

            String entry_title = mCurrentEntry.getTitle().replaceAll("&amp;", "&");
            String entry_body = mCurrentEntry.getBody() + "<br><br>" + "<b>" + mCurrentEntry.getUser() + "</b>" + "<br><br>" + mCurrentEntry.getDateTime() + "<br><br>" + "#" + mCurrentEntry.getEntryNo();
            entry_body = entry_body.replaceAll("&lt;", "<");
            entry_body = entry_body.replaceAll("&gt;", ">");
            entry_body = entry_body.replaceAll("&amp;", "&");
            entry_body = entry_body.replace("www.eksisozluk.com", "eksisozluk.com");
            entry_body = entry_body.replace("<iframe", "<!--iframe");
            entry_body = entry_body.replace("iframe>", "iframe-->");

            mTitleBox.setText(entry_title);

            mIsCurrentModeNight = sukelaPrefs.getBoolean(getString(R.string.key_night_mode), false);
            setDayNightMode(mIsCurrentModeNight);


            mBodyWebView.setVisibility(View.VISIBLE);

            String mime = "text/html";
            String encoding = "utf-8";
            String bodyParams = "<body " + mWebViewBgColor + " " + mWebViewLinkColor + " " + mWebViewTextColor + ">";
            String entryHtml = "<style>img{display: inline;height: auto;max-width: 100%;}</style>" +
                    bodyParams +
                    entry_body +
                    "<br/><br/><br/><br/><br/>";
            mBodyWebView.loadDataWithBaseURL(null, entryHtml,
                    mime, encoding, null);
        }
        return rootView;
    }

    private void init(final View rootView) {
        //
        // bottom bar
        //
        TextView mBottomBar = (TextView) rootView.findViewById(R.id.bottom_bar);
        mBottomBar.setBackgroundColor(getBottomColor());

        //
        // title of entry
        //
        mTitleBox = (TextView) rootView.findViewById(R.id.text_title);
        mTitleBox.setClickable(true);
        mTitleBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleTitleClick();
            }
        });

        //
        // entry body
        //
        mBodyWebView = (WebView) rootView.findViewById(R.id.web_body);
        mBodyWebView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        mBodyWebView.setLongClickable(false);
        mBodyWebView.setWebChromeClient(new WebChromeClient());
        mBodyWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        WebSettings settings = mBodyWebView.getSettings();
        settings.setJavaScriptEnabled(false);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        mBodyWebView.getSettings().setSupportMultipleWindows(true);
        mBodyWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onCreateWindow(WebView view, boolean dialog, boolean userGesture, android.os.Message resultMsg) {
                WebView.HitTestResult result = view.getHitTestResult();
                String data = result.getExtra();
                Context context = view.getContext();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data));
                context.startActivity(browserIntent);
                return false;
            }
        });
        String backColorHex = "#" + Integer.toHexString(ContextCompat.getColor(getActivity(), R.color.default_background)).substring(2);
        mWebViewBgColor = "bgcolor=\"" + backColorHex + "\"";

        String textColorHex = "#" + Integer.toHexString(ContextCompat.getColor(getActivity(), R.color.black)).substring(2);
        mWebViewTextColor = "text=\"" + textColorHex + "\"";

        String colorHex = sukelaPrefs.getString(getString(R.string.key_color), "#008000");
        mWebViewLinkColor = "link=\"" + colorHex + "\"";

        // containers
        mContentMain = (RelativeLayout) rootView.findViewById(R.id.content_main);
        mTitleBoxContainer = (RelativeLayout) rootView.findViewById(R.id.text_title_box_container);
    }

    private void handleTitleClick() {
        String url = Sozluk.getTitleLink(mCurrentEntry);
        openTitleOnBrowser(url);
    }

    private void openTitleOnBrowser(final String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getActivity().startActivity(browserIntent);
    }

    @Override
    public void onResume() {
        super.onResume();
        checkNightMode();
        setLinkColor();
        setTextSize();
    }

    private void setTextSize() {
        int zoom;
        String fontSize = sukelaPrefs.getString(getString(R.string.key_text_size), "18");
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
        mBodyWebView.getSettings().setTextZoom(zoom);
    }

    private void setLinkColor() {
        if (mIsCurrentModeNight) {
            String colorHex = "#" + Integer.toHexString(ContextCompat.getColor(getActivity(), R.color.grey_400)).substring(2);
            mWebViewLinkColor = "link=\"" + colorHex + "\"";

        } else {
            String colorHex = sukelaPrefs.getString(getString(R.string.key_color), "#008000");
            mWebViewLinkColor = "link=\"" + colorHex + "\"";
        }
    }

    protected void checkNightMode() {
        boolean isNightModeEnabled = sukelaPrefs.getBoolean(getString(R.string.key_night_mode), false);
        if (mIsCurrentModeNight != isNightModeEnabled) {
            ((EntryScreenActivity) getActivity()).nightModeChanged();
        }
    }

    public void setDayNightMode(boolean nightMode) {
        mIsCurrentModeNight = nightMode;

        if (nightMode) {
            mContentMain.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.grey_900));
            mTitleBoxContainer.setBackgroundResource(R.drawable.border_bottom_night);
            mTitleBox.setTextColor(ContextCompat.getColor(getActivity(), R.color.grey_600));

            String backColorHex = "#" + Integer.toHexString(ContextCompat.getColor(getActivity(), R.color.grey_900)).substring(2);
            mWebViewBgColor = "bgcolor=\"" + backColorHex + "\"";

            String textColorHex = "#" + Integer.toHexString(ContextCompat.getColor(getActivity(), R.color.grey_600)).substring(2);
            mWebViewTextColor = "text=\"" + textColorHex + "\"";
        } else {
            mContentMain.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.default_background));
            mTitleBoxContainer.setBackgroundResource(R.drawable.border_bottom);
            mTitleBox.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));

            String backColorHex = "#" + Integer.toHexString(ContextCompat.getColor(getActivity(), R.color.default_background)).substring(2);
            mWebViewBgColor = "bgcolor=\"" + backColorHex + "\"";

            String textColorHex = "#" + Integer.toHexString(ContextCompat.getColor(getActivity(), R.color.black)).substring(2);
            mWebViewTextColor = "text=\"" + textColorHex + "\"";
        }
        setLinkColor();
    }

    public int getBottomColor() {
        if (META.getTag().equals(Contract.TAG_SEARCHED_ENTRY)) {
            return ContextCompat.getColor(getActivity(), META.getBottomBarColorId());
        }

        if (mCurrentEntry.getTag().equals(Contract.TAG_SAVE_FOR_GOOD) || mCurrentEntry.getTag().equals(Contract.TAG_SAVE_FOR_LATER)) {
            switch (mCurrentEntry.getSozluk()) {
                case EKSI:
                    return ContextCompat.getColor(getActivity(), R.color.green_600);
                case INSTELA:
                    return ContextCompat.getColor(getActivity(), R.color.instela);
                case ULUDAG:
                    return ContextCompat.getColor(getActivity(), R.color.uludag);
                case INCI:
                    return ContextCompat.getColor(getActivity(), R.color.inci);
                default:
                    throw new IllegalStateException();
            }
        } else {
            return ContextCompat.getColor(getActivity(), META.getBottomBarColorId());
        }
    }
}