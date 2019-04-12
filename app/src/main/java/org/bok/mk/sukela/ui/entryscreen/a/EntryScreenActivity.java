package org.bok.mk.sukela.ui.entryscreen.a;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.bok.mk.sukela.Injection;
import org.bok.mk.sukela.R;
import org.bok.mk.sukela.anim.FabAnimator;
import org.bok.mk.sukela.data.model.Contract;
import org.bok.mk.sukela.data.model.EntryList;
import org.bok.mk.sukela.data.model.SozlukEnum;
import org.bok.mk.sukela.data.model.pack.Pack;
import org.bok.mk.sukela.ui.search.SearchActivity;
import org.bok.mk.sukela.ui.settings.SettingsActivity;
import org.bok.mk.sukela.util.Keyboard;
import org.bok.mk.sukela.util.Network;
import org.bok.mk.sukela.util.SharedPrefsHelper;

import java.util.List;

public class EntryScreenActivity extends AppCompatActivity implements EntryScreenActivityContract.View
{
    private static final String TAG_SETTINGS = "TAG_SETTINGS";

    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    private EntryPagerAdapter mPagerAdapter;
    private int mMenuIndex = 1;
    private TextView mMessageView;
    private ViewPager mViewPager;
    private AlertDialog mAlertDialog;
    private ProgressDialog mProgressDialog;

    private FirebaseAnalytics mFirebaseAnalytics;

    private FloatingActionMenu mFabMenu;
    private FloatingActionButton mFabSettings;

    private EntryScreenActivityPresenter mPresenter;
    private Pack mPack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getIntent().getExtras();
        mPack = (Pack) b.getSerializable(Contract.PACK);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mPresenter = new EntryScreenActivityPresenter(new SharedPrefsHelper(prefs),
                Injection.provideEntryRepository(getApplicationContext()), mPack);
        mPresenter.attachView(this);
        mPresenter.pickTheme(); // this must be called before contentview
        setContentView(R.layout.activity_entry_screen);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(mPack.getTitle());

        //set UI
        createFabMenu();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // view pager
        mPagerAdapter = new EntryPagerAdapter(getSupportFragmentManager(), mPack);
        mPagerAdapter.setData(new EntryList()); // prevents null pointer exp
        mViewPager = findViewById(R.id.pager);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener()
        {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mPresenter.pagerPageChanged(position);
            }
        });

        mMessageView = findViewById(R.id.message);

        mPresenter.uiLoadCompleted();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.saveLastPosition();
    }

    @Override
    public void onBackPressed() {
        if (mFabMenu.isOpened()) {
            mFabMenu.close(true);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_page_number).setTitle(String.valueOf(mMenuIndex));
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        int menuId = mPresenter.getMenu();
        getMenuInflater().inflate(menuId, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_page_number) {
            mPresenter.pageNumberClicked();
            return true;
        }
        else if (id == R.id.action_find) {
            mPresenter.searchClicked();
            return true;
        }
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // FAB
    private void createFabMenu() {
        mFabMenu = findViewById(R.id.menu);
        mFabMenu.setClosedOnTouchOutside(true);
        mFabMenu.setIconToggleAnimatorSet(
                FabAnimator.createAnimatorSet(mFabMenu, R.drawable.ic_menu_white_24dp,
                        R.drawable.ic_close_white_24dp));
        mFabMenu.setOnMenuToggleListener(opened -> {
            if (opened) {
                mPresenter.fabOpened();
            }
            else {
                mPresenter.fabClosed();
            }
        });

        mFabSettings = findViewById(R.id.fab_item_settings);
        mFabSettings.setTag(TAG_SETTINGS);
        mFabSettings.setVisibility(View.GONE);
        mFabSettings.setOnClickListener(view -> mPresenter.fabMenuItemClicked(TAG_SETTINGS));

        mPresenter.fabMenuCreated();
    }

    @Override
    public void updateFabSettingsVisibility(int visibility) {
        mFabSettings.setVisibility(visibility);
    }

    @Override
    public void setFabMenuColors(int normal, int pressed, int ripple) {
        mFabMenu.setMenuButtonColorNormal(ContextCompat.getColor(this, normal));
        mFabMenu.setMenuButtonColorPressed(ContextCompat.getColor(this, pressed));
        mFabMenu.setMenuButtonColorRipple(ContextCompat.getColor(this, ripple));
    }

    @Override
    public void addFabMenuItem(String tag, int iconResId, String title, int normal, int pressed, int ripple) {
        FloatingActionButton fab = new FloatingActionButton(this);
        fab.setTag(tag);
        fab.setButtonSize(FloatingActionButton.SIZE_MINI);
        fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), iconResId));
        fab.setLabelText(title);
        fab.setColorNormal(ContextCompat.getColor(this, normal));
        fab.setColorPressed(ContextCompat.getColor(this, pressed));
        fab.setColorRipple(ContextCompat.getColor(this, ripple));
        fab.setOnClickListener(view -> mPresenter.fabMenuItemClicked(tag));
        mFabMenu.addMenuButton(fab);
    }

    @Override
    public void setSettingsFabItemColor(int normal, int pressed, int ripple) {
        mFabSettings.setColorNormal(ContextCompat.getColor(this, normal));
        mFabSettings.setColorPressed(ContextCompat.getColor(this, pressed));
        mFabSettings.setColorRipple(ContextCompat.getColor(this, ripple));
    }

    @Override
    public void closeFabMenu() {
        if (mFabMenu != null && mFabMenu.isOpened()) {
            mFabMenu.close(false);
        }
    }

    //
    // Ads
    //
    @Override
    public void requestNewInterstitial() {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.ad_entry_screen_interstitial));
        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);
    }

    @Override
    public boolean isInterstitialAdLoaded() {
        return mInterstitialAd.isLoaded();
    }

    @Override
    public void showInterstitialAd() {
        mInterstitialAd.show();
    }

    @Override
    public void requestNewBannerAd() {
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setVisibility(View.GONE);
        mAdView.setAdListener(new AdListener()
        {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mPresenter.bannerAdLoaded();
            }
        });
    }

    @Override
    public void setAdViewVisibility(int visibility) {
        mAdView.setVisibility(visibility);
    }

    //
    // UI Ops
    //
    @Override
    public void setThisTheme(int themeId) {
        this.setTheme(themeId);
    }

    @Override
    public void paintStatusBar2Black() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.BLACK);
    }

    @Override
    public void toast(final String message) {
        this.runOnUiThread(
                () -> Toast.makeText(EntryScreenActivity.this, message, Toast.LENGTH_SHORT).show());
    }

    @Override
    public void displayFancySaveToast() {
        LayoutInflater inflater = EntryScreenActivity.this.getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_layout,
                EntryScreenActivity.this.findViewById(R.id.toast_layout_root));

        Toast toast = new Toast(EntryScreenActivity.this);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    //
    // pager adapter
    //
    @Override
    public int getAdapterSize() {
        return mPagerAdapter.getCount();
    }

    @Override
    public void updatePageNumberMenu(int newIndex) {
        // https://stackoverflow.com/a/32072318/891194
        // whenever you want to change the menu, you should call invalidateOptionsMenu()
        mMenuIndex = newIndex;
        invalidateOptionsMenu();
    }

    @Override
    public void moveToPage(final int index) {
        this.runOnUiThread(() -> mViewPager.setCurrentItem(index));
    }

    @Override
    public void updateAdapterData(final EntryList entries) {
        this.runOnUiThread(() -> {
            mPagerAdapter.setData(entries);
            mPagerAdapter.notifyDataSetChanged();
        });
    }

    //
    // dialog pop ups - alert dialogs
    //
    @Override
    public void displayEntryTitlesList(List<String> titleList, int position) {
        LayoutInflater inflater = getLayoutInflater(); // ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        View customView = inflater.inflate(R.layout.title_list, null, false);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sayfa numarası");
        builder.setView(customView);

        // başlıkları gösteren list view
        ListView list = customView.findViewById(R.id.listView1);
        list.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, titleList));
        list.setOnItemClickListener(
                (parent, view, position1, id) -> mPresenter.titleListItemClicked(position1));

        list.setSelection(position);
        mAlertDialog = builder.create();
        mAlertDialog.show();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(mAlertDialog.getWindow().getAttributes());
        mAlertDialog.getWindow().setAttributes(lp);
    }

    @Override
    public void displayRefreshWarning() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Yenile");
        builder.setMessage("Mevcut entriler silinip, yeni liste indirilsin mi?");
        builder.setPositiveButton("Evet", (dialog, which) -> {
            mPresenter.refreshYesClicked();

            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.GROUP_ID, mPack.getSozluk().getName());
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, mPack.getTag());
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        });
        builder.setNegativeButton("Hayır", (dialog, which) -> mPresenter.refreshCancelClicked());
        mAlertDialog = builder.create();
        mAlertDialog.show();
    }

    @Override
    public void displaySaveOptions(int[] optionResIds) {
        CharSequence options[] = new CharSequence[optionResIds.length];
        for (int i = 0; i < optionResIds.length; i++) {
            options[i] = getString(optionResIds[i]);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(EntryScreenActivity.this);
        builder.setItems(options, (dialog, which) -> mPresenter.saveOptionSelected(which));
        mAlertDialog = builder.create();
        mAlertDialog.show();
    }

    @Override
    public void displayEntryDeleteWarning() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EntryScreenActivity.this);
        builder.setTitle("Sil");
        builder.setMessage("Entryi silmek istediğinize emin misiniz?");
        builder.setPositiveButton("Evet", (dialog, which) -> mPresenter.deleteYesClicked());
        builder.setNegativeButton("Hayır", (dialog, which) -> mPresenter.deleteCancelClicked());
        mAlertDialog = builder.create();
        mAlertDialog.show();
    }

    @Override
    public void displaySozlukOptions(String[] sozlukNames) {
        AlertDialog.Builder builder = new AlertDialog.Builder(EntryScreenActivity.this);
        builder.setItems(sozlukNames, (dialog, which) -> mPresenter.sozlukOptionSelected(
                SozlukEnum.getSozlukEnum(sozlukNames[which])));
        mAlertDialog = builder.create();
        mAlertDialog.show();
    }

    @Override
    public void showEntryNumberPopup(final SozlukEnum sozlukEnum) {
        AlertDialog.Builder builder = new AlertDialog.Builder(EntryScreenActivity.this);
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(50, 10, 50, 10);
        final EditText input = new EditText(this);
        input.setLayoutParams(lp);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setMaxLines(1);
        container.addView(input, lp);
        builder.setView(container);
        builder.setMessage("Saklamak istediğiniz entry'nin numarasını girin:");
        builder.setPositiveButton("Tamam", (dialog, whichButton) -> mPresenter.downloadEntryOkClicked(sozlukEnum, input.getText().toString()));
        builder.setNegativeButton("İptal",
                (dialog, whichButton) -> mPresenter.downloadEntryCancelClicked());

        mAlertDialog = builder.create();
        mAlertDialog.show();

        Keyboard.showSoftKeyboard(this);
    }

    @Override
    public void displayRestorePopup() {
        String title = "Uyarı";
        String message = "Aldığınız yedekleri geri yüklemek için, ilgili xml dosyasını sdcard altında " + getExternalFilesDir(
                null).getPath() + " klasörü altına koymalısınız.";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("Tamam",
                (dialog, whichButton) -> mPresenter.restoreWarningApproved());
        mAlertDialog = builder.create();
        mAlertDialog.show();
    }

    @Override
    public void displayBackupOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EntryScreenActivity.this);
        String options[] = new String[]{"Entrileri yedekle", "Yedekten geri yükle"};
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) { mPresenter.backupOptionSelected(); }
            else { mPresenter.restoreOptionSelected(); }
        });
        mAlertDialog = builder.create();
        mAlertDialog.show();
    }

    @Override
    public void displayBackUpFileOptions(List<String> backupFileNames) {
        LayoutInflater inflater = getLayoutInflater();
        View customView = inflater.inflate(R.layout.orta_liste, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Yüklenecek yedek");
        builder.setView(customView);
        ListView list = customView.findViewById(R.id.listView1);
        list.setAdapter(
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, backupFileNames));

        list.setOnItemClickListener((parent, view, position, id) -> {
            String selectedBackUpFileName = backupFileNames.get(position);
            mPresenter.backupSelected(selectedBackUpFileName);
        });

        mAlertDialog = builder.create();
        mAlertDialog.show();
    }

    @Override
    public void dismissAlertDialog() {
        if (mAlertDialog != null) { mAlertDialog.dismiss(); }
    }

    //
    // dialog pop ups - progress dialogs
    //
    @Override
    public void showSpinnerProgressDialog(final String message) {
        this.runOnUiThread(() -> {
            mProgressDialog = new ProgressDialog(EntryScreenActivity.this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setMessage(message);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.show();
        });
    }

    @Override
    public void showHorizontalProgressDialog(final int messageResId) {
        this.runOnUiThread(() -> {
            mProgressDialog = new ProgressDialog(EntryScreenActivity.this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setMessage(getString(messageResId));
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
                    (DialogInterface.OnClickListener) null);
            mProgressDialog.show();
            Button cancelButton  = mProgressDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            cancelButton.setOnClickListener(view -> mPresenter.progressDialogCancelButtonClicked());
        });
    }

    @Override
    public void dismissProgressDialog() {
        this.runOnUiThread(() -> {
            if (mProgressDialog != null) { mProgressDialog.dismiss(); }
        });
    }

    @Override
    public void updateProgress(final int progress) {
        this.runOnUiThread(() -> mProgressDialog.setProgress(progress));
    }

    @Override
    public void updateProgressMessage(final String message) {
        this.runOnUiThread(() -> mProgressDialog.setMessage(message));
    }

    //
    // helper methods
    //
    @Override
    public void displayNoEntryErrorMessage(int messageResId) {
        this.runOnUiThread(() -> {
            mMessageView.setVisibility(View.VISIBLE);
            mMessageView.setText(getString(messageResId));
        });
    }

    @Override
    public void hideMessageView() {
        this.runOnUiThread(() -> mMessageView.setVisibility(View.GONE));
    }

    @Override
    public void share(String type, int flag, String data) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType(type);
        share.addFlags(flag);
        share.putExtra(Intent.EXTRA_TEXT, data);
        startActivity(share);
    }

    @Override
    public boolean checkIfOnline() {
        return Network.isNetworkAvailable(EntryScreenActivity.this);
    }

    @Override
    public String getExternalFilesDir() {
        return getExternalFilesDir(null).getPath();
    }

    @Override
    public String getStringRes(int resId) {
        return getString(resId);
    }

    @Override
    public void startSearchActivity() {
        Intent i = new Intent(this, SearchActivity.class);
        startActivity(i);
    }

    @Override
    public void startSettingsActivity() {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
    }

    @Override
    public void finishActivity() {
        this.runOnUiThread(EntryScreenActivity.this::finish);
    }
}


