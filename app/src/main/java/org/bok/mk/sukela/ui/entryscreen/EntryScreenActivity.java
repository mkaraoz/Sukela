package org.bok.mk.sukela.ui.entryscreen;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import org.bok.mk.sukela.R;
import org.bok.mk.sukela.adapter.EntryPagerAdapter;
import org.bok.mk.sukela.data.LocalDbManager;
import org.bok.mk.sukela.entry.Entry;
import org.bok.mk.sukela.entry.EntryList;
import org.bok.mk.sukela.entry.EntryManager;
import org.bok.mk.sukela.helper.Contract;
import org.bok.mk.sukela.helper.Meta;
import org.bok.mk.sukela.helper.SukelaPrefs;
import org.bok.mk.sukela.helper.T;
import org.bok.mk.sukela.helper.network.NetworkManager;
import org.bok.mk.sukela.source.Sozluk;
import org.bok.mk.sukela.ui.SettingsActivity;

import java.util.ArrayList;
import java.util.List;

public abstract class EntryScreenActivity extends AppCompatActivity
{
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    private boolean isAdmobBannerLoaded = false;

    protected AlertDialog dialog;

    protected Meta META;
    private EntryPagerAdapter mPagerAdapter;
    protected EntryList mEntryList = new EntryList();
    private ViewPager mViewPager;
    private TextView mMessageView;

    protected FloatingActionMenu mFabMenu;
    protected FloatingActionButton mFabRefresh, mFabShare, mFabSave, mFabSettings;

    protected int mCurrentIndex = 0;
    protected SukelaPrefs sukelaPrefs;

    protected EntryManager mEntryManager;
    private boolean mIsNightModeEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // get META-data
        Bundle b = getIntent().getExtras();
        this.META = (Meta) b.getSerializable(Contract.META);
        setTitle(META.getTitle());

        sukelaPrefs = SukelaPrefs.instance(this);

        //set UI
        setEntryScreenTheme(); // this must be called before contentview
        setContentView(R.layout.activity_entry_screen);
        createFabMenu();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // set toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // view pager
        mPagerAdapter = new EntryPagerAdapter(getSupportFragmentManager(), META);
        mPagerAdapter.setData(mEntryList);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(new DefaultViewPagerListener());

        mEntryManager = EntryManager.getManager(this);
        mMessageView = (TextView) findViewById(R.id.message);
        requestAds();

        // load entries
        fillEntryList();
    }

    private void createFabMenu()
    {
        mFabMenu = (FloatingActionMenu) findViewById(R.id.menu);
        createCustomAnimation();
        mFabMenu.setClosedOnTouchOutside(true);

        if (!mIsNightModeEnabled)
        {
            mFabMenu.setMenuButtonColorNormal(META.getFloatingColors().get(Contract.FAB_MENU_COLOR_NORMAL));
            mFabMenu.setMenuButtonColorPressed(META.getFloatingColors().get(Contract.FAB_MENU_COLOR_PRESSED));
            mFabMenu.setMenuButtonColorRipple(META.getFloatingColors().get(Contract.FAB_MENU_COLOR_RIPPLE));
        }
        else
        {
            mFabMenu.setMenuButtonColorNormal(ContextCompat.getColor(this, R.color.grey_800));
            mFabMenu.setMenuButtonColorPressed(ContextCompat.getColor(this, R.color.grey_600));
            mFabMenu.setMenuButtonColorRipple(ContextCompat.getColor(this, R.color.grey_900));
        }

        mFabMenu.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener()
        {
            @Override
            public void onMenuToggle(boolean opened)
            {
                if (opened)
                {
                    mFabSettings.setVisibility(View.VISIBLE);
                }
                else
                {
                    mFabSettings.setVisibility(View.GONE);
                }
            }
        });

        // fab menu items
        mFabRefresh = (FloatingActionButton) findViewById(R.id.fab_item_refresh);
        mFabShare = (FloatingActionButton) findViewById(R.id.fab_item_share);
        mFabSave = (FloatingActionButton) findViewById(R.id.fab_item_save);
        mFabSettings = (FloatingActionButton) findViewById(R.id.fab_item_settings);
        mFabSettings.setVisibility(View.GONE);

        List<FloatingActionButton> fabItemList = new ArrayList<>();
        fabItemList.add(mFabRefresh);
        fabItemList.add(mFabShare);
        fabItemList.add(mFabSave);
        fabItemList.add(mFabSettings);

        setFabMenuItemColors(fabItemList);

        mFabSettings.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent i = new Intent(EntryScreenActivity.this, SettingsActivity.class);
                startActivity(i);
                closeFabMenu(false);
            }
        });

        mFabRefresh.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                closeFabMenu(false);
                displayRefreshEntryListWarning();
            }
        });

        mFabShare.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                closeFabMenu(false);
                if (mEntryList.size() > 0)
                {
                    Entry e = mEntryList.get(mCurrentIndex);
                    shareEntry(e);
                }
            }
        });

        mFabSave.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                closeFabMenu(false);
                displaySaveEntryDialog();
            }
        });
    }

    protected void setFabMenuItemColors(List<FloatingActionButton> fabItemList)
    {
        int normalColorResID = META.getFloatingColors().get(Contract.FAB_ITEM_COLOR_NORMAL);
        int pressedColorResID = META.getFloatingColors().get(Contract.FAB_ITEM_COLOR_PRESSED);
        int rippleColorResID = META.getFloatingColors().get(Contract.FAB_ITEM_COLOR_RIPPLE);

        if (mIsNightModeEnabled)
        {
            normalColorResID = ContextCompat.getColor(this, R.color.grey_600);
            pressedColorResID = ContextCompat.getColor(this, R.color.grey_700);
            rippleColorResID = ContextCompat.getColor(this, R.color.grey_900);
        }

        for (FloatingActionButton fabItem : fabItemList)
        {
            fabItem.setColorNormal(normalColorResID);
            fabItem.setColorPressed(pressedColorResID);
            fabItem.setColorRipple(rippleColorResID);
        }
    }

    private void displaySaveEntryDialog()
    {
        CharSequence options[] = new CharSequence[]{getString(R.string.save_for_good), getString(R.string.save_for_later)};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(options, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Entry currentEntry = mEntryList.get(mCurrentIndex);
                Entry entryToSave;
                if (which == 0) //saveEntryForGood();
                {
                    entryToSave = currentEntry.createCopyWithAnotherTag(Contract.TAG_SAVE_FOR_GOOD);
                }
                else
                {
                    entryToSave = currentEntry.createCopyWithAnotherTag(Contract.TAG_SAVE_FOR_LATER);
                }
                boolean isEntrySaved = mEntryManager.saveEntry(entryToSave);
                if (isEntrySaved)
                {
                    LayoutInflater inflater = EntryScreenActivity.this.getLayoutInflater();
                    View layout = inflater.inflate(R.layout.toast_layout, (ViewGroup) EntryScreenActivity.this.findViewById(R.id.toast_layout_root));

                    Toast toast = new Toast(EntryScreenActivity.this);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();
                }
                else
                {
                    T.toast(EntryScreenActivity.this, "Entry kaydedilemedi :(");
                }

            }
        });
        builder.show();
    }

    private void displayRefreshEntryListWarning()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(EntryScreenActivity.this);
        builder.setTitle("Yenile");
        builder.setMessage("Mevcut entriler silinip, yeni liste indirilsin mi?");

        builder.setPositiveButton("Evet", new DialogInterface.OnClickListener()
        {
            public void onClick(final DialogInterface dialog, int which)
            {
                refreshEntries();
            }
        });

        builder.setNegativeButton("Hayır", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.show();
    }

    protected void shareEntry(Entry e)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(e.getTitle());
        sb.append("/@");
        sb.append(e.getUser());
        sb.append(": ");
        sb.append(Sozluk.getEntryLink(e));
        Intent share = new Intent(Intent.ACTION_SEND);
        Log.d("SHARE_", sb.toString());
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, sb.toString());
        share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(share);
    }

    protected void refreshEntries()
    {
        ProgressDialog progressBar = new ProgressDialog(this);
        progressBar.setCancelable(false);
        progressBar.setCanceledOnTouchOutside(false);
        progressBar.setIndeterminate(true);
        progressBar.setMessage("Entriler siliniyor...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.show();
        LocalDbManager manager = new LocalDbManager(this);

        Uri uri = META.getDataUri();
        if (META.getTag().equals(Contract.TAG_USER))
        {
            uri = Uri.withAppendedPath(uri, mEntryList.get(0).getUser());
        }

        int deletedEntryCount = manager.deleteEntries(uri);
        progressBar.setMessage(deletedEntryCount + "adet entry silindi.");
        mCurrentIndex = 0;
        saveLastPosition();
        progressBar.dismiss();
        fillEntryList();
    }

    private void createCustomAnimation()
    {
        AnimatorSet set = new AnimatorSet();

        ObjectAnimator scaleOutX = ObjectAnimator.ofFloat(mFabMenu.getMenuIconView(), "scaleX", 1.0f, 0.2f);
        ObjectAnimator scaleOutY = ObjectAnimator.ofFloat(mFabMenu.getMenuIconView(), "scaleY", 1.0f, 0.2f);

        ObjectAnimator scaleInX = ObjectAnimator.ofFloat(mFabMenu.getMenuIconView(), "scaleX", 0.2f, 1.0f);
        ObjectAnimator scaleInY = ObjectAnimator.ofFloat(mFabMenu.getMenuIconView(), "scaleY", 0.2f, 1.0f);

        scaleOutX.setDuration(50);
        scaleOutY.setDuration(50);

        scaleInX.setDuration(150);
        scaleInY.setDuration(150);

        scaleInX.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
                mFabMenu.getMenuIconView().setImageResource(mFabMenu.isOpened() ? R.drawable.ic_menu_white_24dp : R.drawable.ic_close_white_24dp);
            }
        });

        set.play(scaleOutX).with(scaleOutY);
        set.play(scaleInX).with(scaleInY).after(scaleOutX);
        set.setInterpolator(new OvershootInterpolator(2));

        mFabMenu.setIconToggleAnimatorSet(set);
    }

    private void setEntryScreenTheme()
    {
        mIsNightModeEnabled = sukelaPrefs.getBoolean(getString(R.string.key_night_mode), false);
        if (mIsNightModeEnabled)
        {
            setTheme(R.style.NightModeTheme);
        }
        else
        {
            setTheme(META.getThemeId());
        }

        // Status bar is painted to black
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.BLACK);
        }
    }

    protected class DefaultViewPagerListener extends ViewPager.SimpleOnPageChangeListener
    {
        @Override
        public void onPageSelected(int position)
        {
            super.onPageSelected(position);
            handleViewPagerPageSelection(position);
            mFabMenu.close(false);
        }
    }

    protected void handleViewPagerPageSelection(int position)
    {
        if (!META.getTag().equals(Contract.TAG_SAVE_FOR_GOOD))
        {
            showAd(position);
        }
        mCurrentIndex = position;
        updatePageNumberMenu();
    }

    private void updatePageNumberMenu()
    {
        invalidateOptionsMenu();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        menu.findItem(R.id.action_page_number).setTitle(String.valueOf(mCurrentIndex + 1));
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_entry_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_page_number)
        {
            displayTitleList();
            return true;
        }
        if (id == android.R.id.home)
        {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    protected void updateViewPager()
    {
        if (mEntryList.isEmpty())
        {
            mMessageView.setText(getString(R.string.no_entry_here));
            mMessageView.setVisibility(View.VISIBLE);
        }
        else
        {
            mMessageView.setVisibility(View.GONE);
        }

        mPagerAdapter.setData(mEntryList);
        mPagerAdapter.notifyDataSetChanged();
        mViewPager.setCurrentItem(mCurrentIndex);
    }

    protected int getLastIndex()
    {
        String key = META.getTag();

        int lastIndex = sukelaPrefs.getInt(key, 0);

        if (lastIndex > mEntryList.size() - 1)
        {
            lastIndex = mEntryList.size() - 1;
        }
        if (lastIndex < 0)
        {
            lastIndex = 0;
        }

        return lastIndex;
    }

    private void displayTitleList()
    {
        List<String> titleList = mEntryList.getTitles();
        for (int i = 0; i < mEntryList.size(); i++)
        {
            titleList.set(i, Integer.toString(i + 1) + "-  " + titleList.get(i));
        }

        LayoutInflater inflater = getLayoutInflater(); // ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        View customView = inflater.inflate(R.layout.orta_liste, null, false);

        AlertDialog.Builder builder = createAlertBuilder("Sayfa numarası", null);
        builder.setView(customView);

        // entryleri gösteren list view
        ListView list = (ListView) customView.findViewById(R.id.listView1);
        list.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, titleList));
        list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                mCurrentIndex = position;
                mViewPager.setCurrentItem(position);
                dialog.dismiss();
            }
        });

        list.setSelection(mCurrentIndex);
        dialog = builder.create();
        dialog.show();

        //Display display = getWindowManager().getDefaultDisplay();
        //@SuppressWarnings("deprecation") int height = display.getHeight();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        //lp.height = height - (height / 5);
        dialog.getWindow().setAttributes(lp);
    }

    private AlertDialog.Builder createAlertBuilder(String title, String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        if (message != null)
        {
            builder.setMessage(message);
        }
        return builder;
    }

    @Override
    public void onPause()
    {
        super.onPause();
        saveLastPosition();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        closeFabMenu(false);
    }

    protected void closeFabMenu(boolean animated)
    {
        if (mFabMenu != null && mFabMenu.isOpened())
        {
            mFabMenu.close(animated);
        }
    }

    protected void saveLastPosition()
    {
        sukelaPrefs.putInt(META.getTag(), mCurrentIndex);
    }

    protected void fillEntryList()
    {
        // Eğer database de kayıtlı ise ordan oku
        EntryManager manager = EntryManager.getManager(this);
        Uri entryUri = META.getDataUri();
        int entryCount = manager.countEntries(entryUri);
        if (entryCount > 0)
        {
            LocalEntryFiller filler = new LocalEntryFiller(manager);
            filler.execute();
            return;
        }

        // Database içinde bulamadık. İnternete çıkacağız
        boolean isOnline = NetworkManager.isOnline(this);
        if (!isOnline)
        {
            T.toast(this, "İnternet bağlantısı yok.");
            return;
        }

        getEntriesFromInternet();
    }

    protected abstract void getEntriesFromInternet();

    protected class LocalEntryFiller extends AsyncTask<Void, Void, Integer>
    {
        private final EntryManager mEntryManager;
        private ProgressDialog mSpinner;

        LocalEntryFiller(EntryManager manager)
        {
            this.mEntryManager = manager;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            mSpinner = new ProgressDialog(EntryScreenActivity.this);
            mSpinner.setCancelable(false);
            mSpinner.setCanceledOnTouchOutside(false);
            mSpinner.setMessage("Entriler okunuyor...");
            mSpinner.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mSpinner.setIndeterminate(true);
            mSpinner.show();
        }

        @Override
        protected Integer doInBackground(Void... voids)
        {
            mEntryList = mEntryManager.getStoredEntries(META.getDataUri());
            mCurrentIndex = getLastIndex();
            return mCurrentIndex;
        }

        @Override
        protected void onPostExecute(Integer lastIndex)
        {
            super.onPostExecute(lastIndex);
            updateViewPager();
            mSpinner.dismiss();
        }
    }

    @Override
    public void onBackPressed()
    {
        if (mFabMenu.isOpened())
        {
            mFabMenu.close(true);
        }
        else
        {
            super.onBackPressed();
        }
    }

    // This method should be a interface call from fragment
    public void nightModeChanged()
    {
        this.recreate();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Reklamlar
    //
    protected void requestAds()
    {
        requestNewInterstitial();
        requestNewBanner();
    }

    private void requestNewInterstitial()
    {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.ad_entry_screen_interstitial));
        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);
    }

    private void requestNewBanner()
    {
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setVisibility(View.GONE);
        mAdView.setAdListener(new AdListener()
        {
            @Override
            public void onAdLoaded()
            {
                super.onAdLoaded();
                isAdmobBannerLoaded = true;
            }
        });
    }

    protected void showAd(int position)
    {
        mAdView.setVisibility(View.GONE);
        if (position == mPagerAdapter.getCount() - 1 && mInterstitialAd.isLoaded())
        {
            mInterstitialAd.show();
        }
        else if (position == 9 && isAdmobBannerLoaded)
        {
            mAdView.setVisibility(View.VISIBLE);
        }
    }
    //
    // Reklamlar
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////
}
