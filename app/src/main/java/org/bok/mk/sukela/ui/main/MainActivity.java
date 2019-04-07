package org.bok.mk.sukela.ui.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.MobileAds;

import io.fabric.sdk.android.Fabric;

import org.bok.mk.sukela.R;
import org.bok.mk.sukela.anim.AnimationListener;
import org.bok.mk.sukela.data.model.Contract;
import org.bok.mk.sukela.data.model.pack.eksi.EksiDebePack;
import org.bok.mk.sukela.data.model.pack.eksi.EksiGundemPack;
import org.bok.mk.sukela.data.model.pack.eksi.EksiHebePack;
import org.bok.mk.sukela.data.model.pack.Pack;
import org.bok.mk.sukela.data.model.pack.instela.InstelaDebePack;
import org.bok.mk.sukela.data.model.pack.instela.InstelaHebePack;
import org.bok.mk.sukela.data.model.pack.instela.InstelaMonthPack;
import org.bok.mk.sukela.data.model.pack.instela.InstelaTopPack;
import org.bok.mk.sukela.data.model.pack.sakla.SaklaGoodPack;
import org.bok.mk.sukela.data.model.pack.sakla.SaklaLongPack;
import org.bok.mk.sukela.data.model.pack.uludag.UludagDebePack;
import org.bok.mk.sukela.data.model.pack.uludag.UludagHebePack;
import org.bok.mk.sukela.ui.arsiv.ArsivActivity;
import org.bok.mk.sukela.ui.entryscreen.a.EntryScreenActivity;
import org.bok.mk.sukela.ui.settings.SettingsActivity;
import org.bok.mk.sukela.ui.user.UserListActivity;
import org.bok.mk.sukela.ui.year.YearListActivity;
import org.bok.mk.sukela.util.SharedPrefsHelper;

public class MainActivity extends AppCompatActivity implements MainContract.View
{
    private RelativeLayout eksiBox, instelaBox, uludagBox, saveBox;
    private TextView eksiLabel, instelaLabel, uludagLabel, saklaLabel;

    private MainPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        MobileAds.initialize(this, getString(R.string.ADMOB_APP_ID));

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mPresenter = new MainPresenter(new SharedPrefsHelper(prefs));
        mPresenter.attachView(this);

        initUI();
    }

    private void initUI() {
        String expandedTag = mPresenter.getAnimState();
        final AnimationListener anim = new AnimationListener(expandedTag);

        eksiLabel = findViewById(R.id.eksi_label);
        eksiLabel.setTag("E");
        eksiBox = findViewById(R.id.eksi_box);
        anim.register(eksiLabel.getTag(), eksiBox);
        eksiLabel.setOnClickListener(anim);

        instelaLabel = findViewById(R.id.instela_label);
        instelaLabel.setTag("I");
        instelaBox = findViewById(R.id.instela_box);
        anim.register(instelaLabel.getTag(), instelaBox);
        instelaLabel.setOnClickListener(anim);

        uludagLabel = findViewById(R.id.uludag_label);
        uludagLabel.setTag("U");
        uludagBox = findViewById(R.id.uludag_box);
        anim.register(uludagLabel.getTag(), uludagBox);
        uludagLabel.setOnClickListener(anim);

        saklaLabel = findViewById(R.id.save_label);
        saklaLabel.setTag("S");
        saveBox = findViewById(R.id.save_box);
        anim.register(saklaLabel.getTag(), saveBox);
        saklaLabel.setOnClickListener(anim);

        TextView yearButton = findViewById(R.id.eksi_year);
        yearButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, YearListActivity.class);
            startActivity(intent);
        });

        TextView userButton = findViewById(R.id.eksi_yazar);
        userButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, UserListActivity.class);
            startActivity(intent);
        });

        TextView arsivButton = findViewById(R.id.eksi_archive);
        arsivButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ArsivActivity.class);
            startActivity(intent);
        });

        createEntryScreenButton(R.id.eksi_yesterday, new EksiDebePack());
        createEntryScreenButton(R.id.eksi_week, new EksiHebePack());
        createEntryScreenButton(R.id.eksi_gundem, new EksiGundemPack());
        createEntryScreenButton(R.id.instela_month, new InstelaMonthPack());
        createEntryScreenButton(R.id.instela_week, new InstelaHebePack());
        createEntryScreenButton(R.id.instela_yesterday, new InstelaDebePack());
        createEntryScreenButton(R.id.instela_t20, new InstelaTopPack());
        createEntryScreenButton(R.id.uludag_week, new UludagHebePack());
        createEntryScreenButton(R.id.uludag_yesterday, new UludagDebePack());
        createEntryScreenButton(R.id.save_good, new SaklaGoodPack());
        createEntryScreenButton(R.id.save_long, new SaklaLongPack());
    }

    private void createEntryScreenButton(final int resId, final Pack pack) {
        TextView button = findViewById(resId);
        button.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, EntryScreenActivity.class);
            i.putExtra(Contract.PACK, pack);
            startActivity(i);
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        String expand = (String) eksiBox.getTag();
        if (eksiBox.getVisibility() == View.VISIBLE) {
            expand = (String) eksiLabel.getTag();
        }
        else if (instelaBox.getVisibility() == View.VISIBLE) {
            expand = (String) instelaLabel.getTag();
        }
        else if (uludagBox.getVisibility() == View.VISIBLE) {
            expand = (String) uludagLabel.getTag();
        }
        else if (saveBox.getVisibility() == View.VISIBLE) {
            expand = (String) saklaLabel.getTag();
        }
        mPresenter.saveAnimState(expand);
        mPresenter.detachView();
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
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
