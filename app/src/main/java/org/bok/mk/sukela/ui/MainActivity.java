package org.bok.mk.sukela.ui;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.bok.mk.sukela.R;
import org.bok.mk.sukela.anim.AnimationListener;
import org.bok.mk.sukela.helper.IntentHelper;
import org.bok.mk.sukela.helper.Meta;
import org.bok.mk.sukela.helper.SukelaPrefs;
import org.bok.mk.sukela.ui.entryscreen.EksiEntryScreenActivity;
import org.bok.mk.sukela.ui.entryscreen.InciEntryScreenActivity;
import org.bok.mk.sukela.ui.entryscreen.InstelaEntryScreenActivity;
import org.bok.mk.sukela.ui.entryscreen.SavedEntryScreenActivity;
import org.bok.mk.sukela.ui.entryscreen.UludagEntryScreenActivity;

public class MainActivity extends AppCompatActivity {
    private static final String EXPAND = "EXPAND";
    private RelativeLayout eksi_box, instela_box, uludag_box, inci_box, save_box;
    SukelaPrefs prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = SukelaPrefs.instance(this);
        createComponents();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private void createComponents() {
        eksi_box = (RelativeLayout) findViewById(R.id.eksi_box);
        eksi_box.setTag("E");
        instela_box = (RelativeLayout) findViewById(R.id.instela_box);
        instela_box.setTag("I");
        uludag_box = (RelativeLayout) findViewById(R.id.uludag_box);
        uludag_box.setTag("U");
        inci_box = (RelativeLayout) findViewById(R.id.inci_box);
        inci_box.setTag("C");
        save_box = (RelativeLayout) findViewById(R.id.save_box);
        save_box.setTag("S");

        createButton(R.id.eksi_year, null, YearListActivity.class);
        createButton(R.id.eksi_yazar, Meta.userMeta(MainActivity.this), UserListActivity.class);
        // createButton(R.id.eksi_arsiv, null, SelectArchiveActivity.class);
        createButton(R.id.eksi_gundem, Meta.eksiGundemMeta(MainActivity.this), EksiEntryScreenActivity.class);
        createButton(R.id.eksi_yesterday, Meta.eksiDebeMeta(MainActivity.this), EksiEntryScreenActivity.class);
        createButton(R.id.eksi_week, Meta.eksiHebeMeta(MainActivity.this), EksiEntryScreenActivity.class);
        createButton(R.id.instela_t20, Meta.instelaTop20Meta(MainActivity.this), InstelaEntryScreenActivity.class);
        createButton(R.id.instela_yesterday, Meta.instelaYesterdayMeta(MainActivity.this), InstelaEntryScreenActivity.class);
        createButton(R.id.instela_week, Meta.instelaWeekMeta(MainActivity.this), InstelaEntryScreenActivity.class);
        createButton(R.id.instela_month, Meta.instelaMonthMeta(MainActivity.this), InstelaEntryScreenActivity.class);
        createButton(R.id.uludag_yesterday, Meta.uludagYesterdayMeta(MainActivity.this), UludagEntryScreenActivity.class);
        createButton(R.id.uludag_week, Meta.uludagWeekMeta(MainActivity.this), UludagEntryScreenActivity.class);
        createButton(R.id.inci_yesterday, Meta.inciYesterdayMeta(MainActivity.this), InciEntryScreenActivity.class);
        createButton(R.id.inci_week, Meta.inciWeekMeta(MainActivity.this), InciEntryScreenActivity.class);
        createButton(R.id.save_good, Meta.savedForGoodMeta(MainActivity.this), SavedEntryScreenActivity.class);
        createButton(R.id.save_long, Meta.savedForLaterMeta(MainActivity.this), SavedEntryScreenActivity.class);
    }

    private void createButton(final int textViewId, final Meta meta, final Class<?> cls) {
        TextView button = (TextView) findViewById(textViewId);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = IntentHelper.createIntentWithType(cls, meta, MainActivity.this);
                startActivity(intent);
            }
        });
    }

    private void prepareAnimations(String tag) {
        if (!tag.equals(eksi_box.getTag())) {
            eksi_box.setVisibility(View.GONE);
        }
        TextView eksi_label = (TextView) findViewById(R.id.eksi_label);
        eksi_label.setOnClickListener(new AnimationListener(eksi_box));

        if (!tag.equals(instela_box.getTag())) {
            instela_box.setVisibility(View.GONE);
        }
        TextView instela_label = (TextView) findViewById(R.id.instela_label);
        instela_label.setOnClickListener(new AnimationListener(instela_box));

        if (!tag.equals(uludag_box.getTag())) {
            uludag_box.setVisibility(View.GONE);
        }
        TextView uludag_label = (TextView) findViewById(R.id.uludag_label);
        uludag_label.setOnClickListener(new AnimationListener(uludag_box));

        if (!tag.equals(inci_box.getTag())) {
            inci_box.setVisibility(View.GONE);
        }
        TextView inci_label = (TextView) findViewById(R.id.inci_label);
        inci_label.setOnClickListener(new AnimationListener(inci_box));

        if (!tag.equals(save_box.getTag())) {
            save_box.setVisibility(View.GONE);
        }
        TextView sakla_label = (TextView) findViewById(R.id.save_label);
        sakla_label.setOnClickListener(new AnimationListener(save_box));
    }

    @Override
    public void onPause() {
        super.onPause();
        String expand = (String) eksi_box.getTag();
        if (eksi_box.getVisibility() == View.VISIBLE) {
            expand = (String) eksi_box.getTag();
        } else if (instela_box.getVisibility() == View.VISIBLE) {
            expand = (String) instela_box.getTag();
        } else if (uludag_box.getVisibility() == View.VISIBLE) {
            expand = (String) uludag_box.getTag();
        } else if (inci_box.getVisibility() == View.VISIBLE) {
            expand = (String) inci_box.getTag();
        } else if (save_box.getVisibility() == View.VISIBLE) {
            expand = (String) save_box.getTag();
        }
        prefs.putString(EXPAND, expand);
    }

    @Override
    public void onResume() {
        super.onResume();
        String tag = prefs.getString(EXPAND, "E");
        prepareAnimations(tag);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
