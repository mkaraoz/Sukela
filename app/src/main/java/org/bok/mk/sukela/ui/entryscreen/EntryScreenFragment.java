package org.bok.mk.sukela.ui.entryscreen;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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

public class EntryScreenFragment extends Fragment
{
    private TextView mBottomBar, mTitleBox, mBodyBox;
    private Meta META;
    private Entry mCurrentEntry;
    private RelativeLayout mContentMain;
    private RelativeLayout mTitleBoxContainer;
    private ScrollView mScroll;
    private SukelaPrefs sukelaPrefs;
    private boolean mIsCurrentModeNight = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_entry_screen, container, false);
        sukelaPrefs = SukelaPrefs.instance(getActivity());

        Bundle args = getArguments();
        if (args != null)
        {
            META = (Meta) args.getSerializable(Contract.META);
            mCurrentEntry = (Entry) args.getSerializable(Contract.ENTRY);
            init(rootView);

            String entry_title = mCurrentEntry.getTitle().replaceAll("&amp;", "&");
            String entry_body = mCurrentEntry.getBody() + "<br><br>" + "<b>" + mCurrentEntry.getUser() + "</b>" + "<br><br>" + mCurrentEntry.getDateTime() + "<br><br>" + "#" + mCurrentEntry.getEntryNo();
            entry_body = entry_body.replaceAll("&lt;", "<");
            entry_body = entry_body.replaceAll("&gt;", ">");
            entry_body = entry_body.replaceAll("&amp;", "&");
            entry_body = entry_body.replaceAll("www.eksisozluk.com", "eksisozluk.com");

            mTitleBox.setText(entry_title);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
            {
                mBodyBox.setText(Html.fromHtml(entry_body, Html.FROM_HTML_MODE_COMPACT));
            }
            else
            {
                mBodyBox.setText(Html.fromHtml(entry_body));
            }
        }

        mIsCurrentModeNight = sukelaPrefs.getBoolean(getString(R.string.key_night_mode), false);
        setDayNightMode(mIsCurrentModeNight);

        return rootView;
    }

    private void init(final View rootView)
    {
        //
        // bottom bar
        //
        mBottomBar = (TextView) rootView.findViewById(R.id.bottom_bar);
        mBottomBar.setBackgroundColor(getBottomColor());

        //
        // title of entry
        //
        mTitleBox = (TextView) rootView.findViewById(R.id.text_title);
        mTitleBox.setClickable(true);
        mTitleBox.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                handleTitleClick();
            }
        });

        //
        // entry body
        //
        mBodyBox = (TextView) rootView.findViewById(R.id.text_body);
        mBodyBox.setLinksClickable(true);
        mBodyBox.setMovementMethod(LinkMovementMethod.getInstance());
        //setLinkColor(mBodyBox);
        //setTextSize(mBodyBox);

        mContentMain = (RelativeLayout) rootView.findViewById(R.id.content_main);
        mScroll = (ScrollView) rootView.findViewById(R.id.scroll_view);
        mTitleBoxContainer = (RelativeLayout) rootView.findViewById(R.id.text_title_box_container);
    }

    private void handleTitleClick()
    {
        String url = Sozluk.getTitleLink(mCurrentEntry);
        openTitleOnBrowser(url);
    }

    private void openTitleOnBrowser(final String url)
    {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getActivity().startActivity(browserIntent);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        checkNightMode();
        setLinkColor();
        setTextSize();
    }

    private void setTextSize()
    {
        String fontSize = sukelaPrefs.getString(getString(R.string.key_text_size), "18");
        mBodyBox.setTextSize(TypedValue.COMPLEX_UNIT_SP, Float.valueOf(fontSize));
    }

    private void setLinkColor()
    {
        if (!mIsCurrentModeNight)
        {
            String color_code = sukelaPrefs.getString(getString(R.string.key_color), "#008000");
            mBodyBox.setLinkTextColor(Color.parseColor(color_code));
        }
        else
        {
            mBodyBox.setLinkTextColor(ContextCompat.getColor(getActivity(), R.color.grey_400));
        }
    }

    protected void checkNightMode()
    {
        boolean isNightModeEnabled = sukelaPrefs.getBoolean(getString(R.string.key_night_mode), false);
        if (mIsCurrentModeNight != isNightModeEnabled)
        {
            ((EntryScreenActivity) getActivity()).nightModeChanged();
        }
    }

    public void setDayNightMode(boolean nightMode)
    {
        mIsCurrentModeNight = nightMode;

        if (nightMode)
        {
            mContentMain.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.grey_900));
            mScroll.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.grey_900));
            mBodyBox.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.grey_900));
            mBodyBox.setTextColor(ContextCompat.getColor(getActivity(), R.color.grey_600));
            mTitleBoxContainer.setBackgroundResource(R.drawable.border_bottom_night);
            mTitleBox.setTextColor(ContextCompat.getColor(getActivity(), R.color.grey_600));
            setLinkColor();
        }
        else
        {
            mContentMain.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.default_bacground));
            mScroll.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.default_bacground));
            mBodyBox.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.default_bacground));
            mBodyBox.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
            mTitleBoxContainer.setBackgroundResource(R.drawable.border_bottom);
            mTitleBox.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
        }
    }

    public int getBottomColor()
    {
        if (mCurrentEntry.getTag().equals(Contract.TAG_SAVE_FOR_GOOD) || mCurrentEntry.getTag().equals(Contract.TAG_SAVE_FOR_LATER))
        {
            switch (mCurrentEntry.getSozluk())
            {
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
        }
        else
        {
            return ContextCompat.getColor(getActivity(), META.getBottomBarColorId());
        }
    }
}

























