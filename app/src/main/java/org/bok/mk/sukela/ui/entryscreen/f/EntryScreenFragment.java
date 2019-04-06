package org.bok.mk.sukela.ui.entryscreen.f;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
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
import org.bok.mk.sukela.data.model.Contract;
import org.bok.mk.sukela.data.model.Entry;
import org.bok.mk.sukela.data.model.pack.Pack;
import org.bok.mk.sukela.util.SharedPrefsHelper;

public class EntryScreenFragment extends Fragment implements EntryScreenFragmentContract.View
{
    private EntryScreenFragmentPresenter mPresenter;
    private TextView mTitleBox;
    private WebView mBodyWebView;
    private RelativeLayout mTopLevelRelativeLayout;
    private RelativeLayout mTitleBoxContainer;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_entry_screen, container, false);
        Bundle args = getArguments();
        if (args == null) { return rootView; }

        Entry currentEntry = (Entry) args.getSerializable(Contract.ENTRY);
        Pack mPack = (Pack) args.getSerializable(Contract.PACK);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mPresenter = new EntryScreenFragmentPresenter(new SharedPrefsHelper(prefs), mPack,
                currentEntry);
        mPresenter.attachView(this);
        init(rootView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.createUi();
    }

    private void init(View rootView) {
        //
        // bottom bar
        //
        TextView mBottomBar = rootView.findViewById(R.id.bottom_bar);
        int colorId = mPresenter.getBottomColor();
        mBottomBar.setBackgroundColor(
                ContextCompat.getColor(getActivity().getApplicationContext(), colorId));

        //
        // title of entry
        //
        mTitleBox = rootView.findViewById(R.id.text_title);
        mTitleBox.setClickable(true);
        mTitleBox.setOnClickListener(v -> mPresenter.titleClicked());

        //
        // entry body
        //
        mBodyWebView = rootView.findViewById(R.id.web_body);
        mBodyWebView.setLongClickable(false);
        mBodyWebView.setWebChromeClient(new WebChromeClient());
        mBodyWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        WebSettings settings = mBodyWebView.getSettings();
        settings.setJavaScriptEnabled(false);
        settings.setAllowContentAccess(false);
        settings.setAllowFileAccess(false);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        // containers
        mTopLevelRelativeLayout = rootView.findViewById(R.id.content_main);
        mTitleBoxContainer = rootView.findViewById(R.id.text_title_box_container);
    }

    @Override
    public void openUrl(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getActivity().startActivity(browserIntent);
    }

    @Override
    public void printEntryTitle(String title) {
        mTitleBox.setText(title);
    }

    @Override
    public void setMainBackgroundColor(int mainBackgroundColorResId) {
        mTopLevelRelativeLayout.setBackgroundColor(
                ContextCompat.getColor(getActivity(), mainBackgroundColorResId));
    }

    @Override
    public void setTitleBoxBackColor(int titleBoxBackgroundDrawableId) {
        mTitleBoxContainer.setBackgroundResource(titleBoxBackgroundDrawableId);
    }

    @Override
    public void setEntryTitleColor(int titleBoxColor) {
        mTitleBox.setTextColor(ContextCompat.getColor(getActivity(), titleBoxColor));
    }

    @Override
    public void loadEntryBody(String data, String mimeType, String encoding) {
        //loadDataWithBaseURL yerine loadData kullanırsam türkçe karakterler düzgün görüntülenmiyor.
        mBodyWebView.loadDataWithBaseURL(null, data, mimeType, encoding, null);
    }

    @Override
    public String getStringRes(int resId) {
        return getActivity().getString(resId);
    }

    @Override
    public void setWebViewTextSize(int zoom) {
        mBodyWebView.getSettings().setTextZoom(zoom);
    }
}
