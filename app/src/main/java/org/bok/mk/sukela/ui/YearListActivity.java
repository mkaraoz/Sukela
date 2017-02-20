package org.bok.mk.sukela.ui;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

import org.bok.mk.sukela.R;
import org.bok.mk.sukela.helper.IntentHelper;
import org.bok.mk.sukela.helper.Meta;
import org.bok.mk.sukela.ui.entryscreen.YearEntryScreenActivity;
import org.bok.mk.sukela.helper.Screen;

public class YearListActivity extends AppCompatActivity
{
    private static final double GOLDEN_RATIO = 1.618;
    private boolean isTablet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_year_list);
        setTitle("Yılların efsane entryleri");

        init();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private void init()
    {
        TextView a2016 = (TextView) findViewById(R.id.a2016);
        TextView a2015 = (TextView) findViewById(R.id.a2015);
        TextView a2014 = (TextView) findViewById(R.id.a2014);
        TextView a2013 = (TextView) findViewById(R.id.a2013);
        TextView a2012 = (TextView) findViewById(R.id.a2012);
        TextView a2011 = (TextView) findViewById(R.id.a2011);
        TextView a2010 = (TextView) findViewById(R.id.a2010);
        TextView a2009 = (TextView) findViewById(R.id.a2009);
        TextView a2008 = (TextView) findViewById(R.id.a2008);
        TextView a2007 = (TextView) findViewById(R.id.a2007);
        TextView a2006 = (TextView) findViewById(R.id.a2006);
        TextView a2005 = (TextView) findViewById(R.id.a2005);
        TextView a2004 = (TextView) findViewById(R.id.a2004);

        addOnClickListener(a2004, 2004);
        addOnClickListener(a2005, 2005);
        addOnClickListener(a2006, 2006);
        addOnClickListener(a2007, 2007);
        addOnClickListener(a2008, 2008);
        addOnClickListener(a2009, 2009);
        addOnClickListener(a2010, 2010);
        addOnClickListener(a2011, 2011);
        addOnClickListener(a2012, 2012);
        addOnClickListener(a2013, 2013);
        addOnClickListener(a2014, 2014);
        addOnClickListener(a2015, 2015);
        addOnClickListener(a2016, 2016);

        int screenWidthPx = (int) (Screen.getWidthPx(this));
        int h = (int) (screenWidthPx / (GOLDEN_RATIO * 2));
        a2004.setHeight(h);
        a2005.setHeight(h);
        a2006.setHeight(h);
        a2007.setHeight(h);
        a2008.setHeight(h);
        a2009.setHeight(h);
        a2010.setHeight(h);
        a2011.setHeight(h);
        a2012.setHeight(h);
        a2013.setHeight(h);
        a2014.setHeight(h);
        a2015.setHeight(h);
        a2016.setHeight(h);

        /*
        setAnimation(a2004);
        setAnimation(a2005);
        setAnimation(a2006);
        setAnimation(a2007);
        setAnimation(a2008);
        setAnimation(a2009);
        setAnimation(a2010);
        setAnimation(a2011);
        setAnimation(a2012);
        setAnimation(a2013);
        setAnimation(a2014);
        setAnimation(a2015);
        setAnimation(a2016);
        */
    }

    private void setAnimation(View viewToAnimate)
    {
        //Animation animation = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(500);
        viewToAnimate.startAnimation(anim);
    }

    private void addOnClickListener(final TextView button, final int year)
    {
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                displayBestEntriesOf(year);
                // Toast.makeText(YearListActivity.this, year + "", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayBestEntriesOf(final int year)
    {
        Meta meta = Meta.eksiYearMeta(this, year);
        Intent intent = IntentHelper.createIntentWithType(YearEntryScreenActivity.class, meta, this);
        startActivity(intent);
    }
}
