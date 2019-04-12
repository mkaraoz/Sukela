package org.bok.mk.sukela.ui.entryscreen.a;

import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.View;

import org.bok.mk.sukela.R;
import org.bok.mk.sukela.data.backup.BackupManager;
import org.bok.mk.sukela.data.model.Contract;
import org.bok.mk.sukela.data.model.Entry;
import org.bok.mk.sukela.data.model.EntryList;
import org.bok.mk.sukela.data.model.SozlukEnum;
import org.bok.mk.sukela.data.model.pack.Pack;
import org.bok.mk.sukela.data.model.pack.sakla.SingleEntryPack;
import org.bok.mk.sukela.data.source.EntryDataSource;
import org.bok.mk.sukela.data.source.EntryRepo;
import org.bok.mk.sukela.ui.base.BasePresenter;
import org.bok.mk.sukela.util.SharedPrefsHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EntryScreenActivityPresenter extends BasePresenter<EntryScreenActivityContract.View> implements EntryScreenActivityContract.Presenter, EntryDataSource.LoadEntryListCallback
{
    private static final String LOG_TAG = EntryScreenActivityPresenter.class.getSimpleName();

    private static final String TAG_REFRESH = "TAG_REFRESH";
    private static final String TAG_SHARE = "TAG_SHARE";
    private static final String TAG_SAVE = "TAG_SAVE";
    private static final String TAG_SETTINGS = "TAG_SETTINGS";
    private static final String TAG_DELETE = "TAG_DELETE";
    private static final String TAG_BACKUP_RESTORE = "TAG_BACKUP_RESTORE";
    private static final String TAG_GET_FROM_WEB = "TAG_GET_FROM_WEB";

    private final SharedPrefsHelper mPreferences;
    private final Pack mPack;
    private boolean isAdmobBannerLoaded = false;
    private int mCurrentIndex = 0;
    private final EntryRepo mEntryRepo;
    private final EntryList mEntryList = new EntryList();

    EntryScreenActivityPresenter(SharedPrefsHelper preferences, EntryRepo repo, Pack pack) {
        mPreferences = preferences;
        mPack = pack;
        mEntryRepo = repo;
    }

    @Override
    public void pickTheme() {
        boolean isNightModeEnabled = mPreferences.get(mView.getStringRes(R.string.key_night_mode),
                false);
        if (isNightModeEnabled) {
            mView.setThisTheme(R.style.NightModeTheme);
        }
        else {
            mView.setThisTheme(mPack.getThemeId());
        }

        // Status bar is painted to black
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mView.paintStatusBar2Black();
        }
    }

    @Override
    public void pagerPageChanged(int position) {
        // no ad for saved entries
        if (!mPack.getTag().equals(Contract.TAG_SAVE_FOR_GOOD)) {
            mView.setAdViewVisibility(View.GONE);
            boolean isInterstitialAdLoaded = mView.isInterstitialAdLoaded();
            int entryCount = mView.getAdapterSize();

            if (position == entryCount - 1 && isInterstitialAdLoaded) {
                mView.showInterstitialAd();
            }
            else if (position == 9 && isAdmobBannerLoaded) {
                mView.setAdViewVisibility(View.VISIBLE);
            }
        }
        mCurrentIndex = position;
        mView.updatePageNumberMenu(mCurrentIndex + 1);

        mView.closeFabMenu();
    }

    @Override
    public void uiLoadCompleted() {
        mView.requestNewInterstitial();
        mView.requestNewBannerAd();
        fillEntryList();
    }

    @Override
    public void saveLastPosition() {
        mPreferences.put(mPack.getTag(), mCurrentIndex);
    }

    @Override
    public int getMenu() {
        if (mPack.getTag().equals(Contract.TAG_SAVE_FOR_GOOD)) {
            return R.menu.menu_save_entry_screen;
        }
        else { return R.menu.menu_entry_screen; }
    }

    @Override
    public void pageNumberClicked() {
        List<String> titleList = mEntryList.getTitles();
        for (int i = 0; i < mEntryList.size(); i++) {
            titleList.set(i, Integer.toString(i + 1) + "-  " + titleList.get(i));
        }
        mView.displayEntryTitlesList(titleList, mCurrentIndex);
    }

    @Override
    public void searchClicked() {
        mView.startSearchActivity();
    }

    @Override
    public void fabOpened() {
        mView.updateFabSettingsVisibility(View.VISIBLE);
    }

    @Override
    public void fabClosed() {
        mView.updateFabSettingsVisibility(View.GONE);
    }

    @Override
    public void fabMenuItemClicked(String tag) {
        mView.closeFabMenu();
        switch (tag) {
            case TAG_REFRESH:
                fabRefreshClicked();
                break;
            case TAG_SAVE:
                fabSaveClicked();
                break;
            case TAG_SHARE:
                if (mEntryList.isEmpty()) { return; }
                this.shareEntry(mEntryList.get(mCurrentIndex));
                break;
            case TAG_SETTINGS:
                settingsFabClicked();
                break;
            case TAG_DELETE:
                fabDelClicked();
                break;
            case TAG_BACKUP_RESTORE:
                backupRestoreClicked();
                break;
            case TAG_GET_FROM_WEB:
                fabGetFromWebClicked();
                break;
        }
    }

    @Override
    public void fabMenuCreated() {
        setUpFabMenuItems();

        // set fab menu colors
        int[] fabItemColors = getFabMenuColors();
        int normal = fabItemColors[0];
        int pressed = fabItemColors[1];
        int ripple = fabItemColors[2];
        mView.setFabMenuColors(normal, pressed, ripple);
    }

    @Override
    public void bannerAdLoaded() {
        isAdmobBannerLoaded = true;
    }

    @Override
    public void titleListItemClicked(int position) {
        mCurrentIndex = position;
        mView.moveToPage(mCurrentIndex);
        mView.dismissAlertDialog();
    }

    @Override
    public void refreshYesClicked() {
        ExecutorService e = Executors.newSingleThreadExecutor();
        e.execute(() -> {
            mPreferences.put(mPack.getTag(), 0);
            mEntryRepo.refreshEntries(mPack.getTag(), EntryScreenActivityPresenter.this);
        });
        e.shutdown();
    }

    @Override
    public void refreshCancelClicked() {
        mView.dismissAlertDialog();
    }

    @Override
    public void saveOptionSelected(int which) {
        ExecutorService e = Executors.newSingleThreadExecutor();
        e.execute(() -> {
            // kopya yaratmamız lazım yoksa orijinal entrinin üzerine yazıyor
            // bir de tag değiştiriyor.
            Entry toSave = new Entry(mEntryList.get(mCurrentIndex));
            if (which == 0) {
                toSave.setTag(Contract.TAG_SAVE_FOR_GOOD);
            }
            else {
                toSave.setTag(Contract.TAG_SAVE_FOR_LATER);
            }
            EntryList list = new EntryList();
            list.add(toSave);
            mEntryRepo.saveEntries(list);
        });
        e.shutdown();
        mView.displayFancySaveToast();
    }

    @Override
    public void deleteYesClicked() {
        ExecutorService e = Executors.newSingleThreadExecutor();
        e.execute(() -> {
            Entry entry = mEntryList.get(mCurrentIndex);
            mEntryRepo.deleteEntry(entry);
            mEntryList.remove(entry);

            if (mEntryList.isEmpty()) {
                mView.finishActivity();
                mView.toast("Entry Silindi. Saklanan entriniz yok.");
            }
            else {
                mView.updateAdapterData(mEntryList);
                mView.toast("Entry Silindi.");
                mView.moveToPage(mCurrentIndex);
            }
        });
        e.shutdown();
    }

    @Override
    public void deleteCancelClicked() {
        mView.dismissAlertDialog();
    }

    @Override
    public void sozlukOptionSelected(SozlukEnum sozlukEnum) {
        mView.showEntryNumberPopup(sozlukEnum);
        mView.closeFabMenu();
    }

    @Override
    public void downloadEntryOkClicked(SozlukEnum sozlukEnum, String entryNo) {
        ExecutorService e = Executors.newSingleThreadExecutor();
        // bunun kendi callbacki var sınıfın callback metotlarını kullanmıyor çünkü onEntryLoaded
        // metodunun farklı davranması gerekiyor. sınıfın metodu tagi silip yeni gelenleri yazarken
        // bu sadece sonuna ek yapıyor. diğer metotlar ortak.
        e.execute(() -> mEntryRepo.getEntry(sozlukEnum, Integer.parseInt(entryNo),
                new EntryDataSource.LoadEntryListCallback()
                {
                    @Override
                    public void onEntriesLoaded(EntryList entries) {
                        Entry e = entries.get(0);
                        mEntryList.add(e);
                        mView.updateAdapterData(mEntryList);
                        mCurrentIndex = mEntryList.size() - 1;
                        mView.moveToPage(mCurrentIndex);
                        mView.hideMessageView();
                        mView.dismissProgressDialog();
                    }

                    @Override
                    public void onDataNotAvailable() {
                        EntryScreenActivityPresenter.this.onDataNotAvailable();
                    }

                    @Override
                    public void onProgressUpdate(int progress) {
                        EntryScreenActivityPresenter.this.onProgressUpdate(progress);
                    }

                    @Override
                    public void onMessageUpdate(String message) {
                        EntryScreenActivityPresenter.this.onMessageUpdate(message);
                    }

                    @Override
                    public void onDataLoadStart(boolean fromLocal) {
                        EntryScreenActivityPresenter.this.onDataLoadStart(fromLocal);
                    }

                    @Override
                    public void onError(String error) {
                        EntryScreenActivityPresenter.this.onError(error);
                    }

                    @Override
                    public boolean checkIfOnline() {
                        return EntryScreenActivityPresenter.this.checkIfOnline();
                    }
                }));
        e.shutdown();
    }

    @Override
    public void downloadEntryCancelClicked() {
        mView.dismissAlertDialog();
    }

    @Override
    public void restoreWarningApproved() {
        final List<String> backupFileNames = getBackupFileNames();
        mView.displayBackUpFileOptions(backupFileNames);
    }

    @Override
    public void progressDialogCancelButtonClicked() {
        // mView.dismissProgressDialog();
        mEntryRepo.cancel(mPack.getTag());
    }

    @Override
    public void backupOptionSelected() {
        ExecutorService e = Executors.newSingleThreadExecutor();
        e.execute(() -> {
            mView.showSpinnerProgressDialog("\"Entriler yedekleniyor...\"");
            String path = mView.getExternalFilesDir();
            boolean isSuccess = BackupManager.saveEntriesToSdCard(mEntryList, path);
            mView.dismissProgressDialog();
            if (isSuccess) {
                mView.toast("Entriler " + path + "altına yedeklendi");
            }
            else { mView.toast("HATA: Entrilerin yedeği alınamadı. Lütfen tekrar deneyin."); }
        });
        e.shutdown();
    }

    @Override
    public void restoreOptionSelected() {
        mView.displayRestorePopup();
    }

    @Override
    public void backupSelected(String backUpFileName) {
        mView.dismissAlertDialog();
        restoreEntriesFromBackUpFile(backUpFileName);
    }

    private void setUpFabMenuItems() {
        switch (mPack.getTag()) {
            case Contract.TAG_SAVE_FOR_GOOD:
                addSaveForGoodFabItems();
                break;
            case Contract.TAG_SAVE_FOR_LATER:
                addSaveForLaterFabItems();
                break;
            case Contract.TAG_SINGLE_ENTRY:
                addSingleSearchEntryFabItems();
                break;
            default:
                addRegularFabItems();
                break;
        }
    }

    private void addSingleSearchEntryFabItems() {
        int[] fabItemColors = getFabMenuItemColors();
        int normal = fabItemColors[0];
        int pressed = fabItemColors[1];
        int ripple = fabItemColors[2];

        mView.addFabMenuItem(TAG_SHARE, R.drawable.ic_share_white_24dp, "Paylaş", normal, pressed,
                ripple);

        mView.setSettingsFabItemColor(normal, pressed, ripple);
    }

    private void addSaveForGoodFabItems() {
        int[] fabItemColors = getFabMenuItemColors();
        int normal = fabItemColors[0];
        int pressed = fabItemColors[1];
        int ripple = fabItemColors[2];

        mView.addFabMenuItem(TAG_SHARE, R.drawable.ic_share_white_24dp, "Paylaş", normal, pressed,
                ripple);
        mView.addFabMenuItem(TAG_GET_FROM_WEB, R.drawable.ic_cloud_download_white_24dp,
                "Sözlükten al", normal, pressed, ripple);
        mView.addFabMenuItem(TAG_BACKUP_RESTORE, R.drawable.ic_import_export_white_24dp, "Yedek",
                normal, pressed, ripple);
        mView.addFabMenuItem(TAG_DELETE, R.drawable.ic_delete_white_24dp, "Sil", normal, pressed,
                ripple);

        mView.setSettingsFabItemColor(normal, pressed, ripple);
    }

    private void addSaveForLaterFabItems() {
        int[] fabItemColors = getFabMenuItemColors();
        int normal = fabItemColors[0];
        int pressed = fabItemColors[1];
        int ripple = fabItemColors[2];

        mView.addFabMenuItem(TAG_SHARE, R.drawable.ic_share_white_24dp, "Paylaş", normal, pressed,
                ripple);
        mView.addFabMenuItem(TAG_DELETE, R.drawable.ic_delete_white_24dp, "Sil", normal, pressed,
                ripple);

        mView.setSettingsFabItemColor(normal, pressed, ripple);
    }

    private void addRegularFabItems() {
        int[] fabItemColors = getFabMenuItemColors();
        int normal = fabItemColors[0];
        int pressed = fabItemColors[1];
        int ripple = fabItemColors[2];

        mView.addFabMenuItem(TAG_REFRESH, R.drawable.ic_cached_white_24dp, "Yenile", normal,
                pressed, ripple);
        mView.addFabMenuItem(TAG_SHARE, R.drawable.ic_share_white_24dp, "Paylaş", normal, pressed,
                ripple);
        mView.addFabMenuItem(TAG_SAVE, R.drawable.ic_save_white_24dp, "Sakla", normal, pressed,
                ripple);

        mView.setSettingsFabItemColor(normal, pressed, ripple);
    }

    private int[] getFabMenuColors() {
        int[] colors = new int[3];
        boolean isNightModeEnabled = mPreferences.get(mView.getStringRes(R.string.key_night_mode),
                false);

        if (isNightModeEnabled) {
            colors[0] = R.color.grey_800;
            colors[1] = R.color.grey_600;
            colors[2] = R.color.grey_900;
        }
        else {
            colors[0] = mPack.getFloatingColors().FAB_MENU_COLOR_NORMAL;
            colors[1] = mPack.getFloatingColors().FAB_MENU_COLOR_PRESSED;
            colors[2] = mPack.getFloatingColors().FAB_MENU_COLOR_RIPPLE;
        }
        return colors;
    }

    private int[] getFabMenuItemColors() {
        int[] colors = new int[3];
        boolean isNightModeEnabled = mPreferences.get(mView.getStringRes(R.string.key_night_mode),
                false);

        if (isNightModeEnabled) {
            colors[0] = R.color.grey_800;
            colors[1] = R.color.grey_600;
            colors[2] = R.color.grey_900;
        }
        else {
            colors[0] = mPack.getFloatingColors().FAB_ITEM_COLOR_NORMAL;
            colors[1] = mPack.getFloatingColors().FAB_ITEM_COLOR_PRESSED;
            colors[2] = mPack.getFloatingColors().FAB_ITEM_COLOR_RIPPLE;
        }
        return colors;
    }

    private void fabRefreshClicked() {
        mView.displayRefreshWarning();
    }

    private void fabSaveClicked() {
        mView.displaySaveOptions(new int[]{R.string.save_for_good, R.string.save_for_later});
    }

    private void settingsFabClicked() {
        mView.startSettingsActivity();
    }

    private void backupRestoreClicked() {
        mView.displayBackupOptions();
    }

    private void fabGetFromWebClicked() {
        String[] sozlukNames = new String[3];
        sozlukNames[0] = SozlukEnum.EKSI.getName();
        sozlukNames[1] = SozlukEnum.INSTELA.getName();
        sozlukNames[2] = SozlukEnum.ULUDAG.getName();
        mView.displaySozlukOptions(sozlukNames);
    }

    private void fabDelClicked() {
        if (mEntryList.isEmpty()) {
            mView.toast("Neyi sileyim şimdi ben?");
            mView.closeFabMenu();
            return;
        }

        mView.displayEntryDeleteWarning();
    }

    private void shareEntry(Entry e) {
        if (e == null) { return; }
        StringBuilder sb = new StringBuilder();
        sb.append(e.getTitle());
        sb.append("/@");
        sb.append(e.getUser());
        sb.append(": ");
        sb.append(e.getEntryUrl());
        Log.d("SHARE_", sb.toString());
        mView.share("text/plain", Intent.FLAG_ACTIVITY_NEW_TASK, sb.toString());
    }

    private void fillEntryList() {
        if (mPack.getTag().equals(Contract.TAG_SINGLE_ENTRY)) {
            getBundledEntry();
            return;
        }

        ExecutorService e = Executors.newSingleThreadExecutor();
        e.execute(() -> mEntryRepo.getEntries(mPack.getTag(), EntryScreenActivityPresenter.this));
        e.shutdown();
    }

    private void getBundledEntry() {
        Entry e = ((SingleEntryPack) mPack).getEntry();
        mEntryList.clear();
        mEntryList.add(e);
        mView.updateAdapterData(mEntryList);
        mCurrentIndex = 0;
        mView.moveToPage(mCurrentIndex);
    }

    private int getLastIndex() {
        String key = mPack.getTag();
        int lastIndex = mPreferences.get(key, 0);

        if (lastIndex > mEntryList.size() - 1) {
            lastIndex = mEntryList.size() - 1;
        }
        if (lastIndex < 0) {
            lastIndex = 0;
        }

        return lastIndex;
    }

    private List<String> getBackupFileNames() {
        File appExtDirectory = new File(mView.getExternalFilesDir());
        File[] backupFiles = appExtDirectory.listFiles();
        List<String> backupFileNames = new ArrayList<>();
        for (File backUp : backupFiles) {
            String extension = "";
            int i = backUp.getName().lastIndexOf('.');
            if (i > 0) {
                extension = backUp.getName().substring(i + 1);
            }
            if (extension.equalsIgnoreCase("xml")) {
                backupFileNames.add(backUp.getName());
            }
        }
        return backupFileNames;
    }

    private void restoreEntriesFromBackUpFile(String backUpFileName) {
        ExecutorService e = Executors.newSingleThreadExecutor();
        e.execute(() -> {
            mView.showSpinnerProgressDialog("Entriler geri getiriliyor.");
            EntryList restoredEntryList = BackupManager.restoreEntriesFromBackUpFile(
                    new File(mView.getExternalFilesDir() + File.separator + backUpFileName));
            for (Entry entry : restoredEntryList)
                mEntryList.add(entry);
            mEntryRepo.saveEntries(restoredEntryList);
            mView.dismissProgressDialog();
            mView.toast(restoredEntryList.size() + " adet entry geri getirildi");
            mCurrentIndex = 0;
            mView.updateAdapterData(mEntryList);
            mView.moveToPage(mCurrentIndex);
        });
        e.shutdown();
    }

    //
    // Load entry callback methods
    //
    @Override
    public void onEntriesLoaded(EntryList entries) {
        if (!EntryScreenActivityPresenter.this.isViewAttached()) {
            return;
        }
        if (entries.isEmpty()) {
            mView.displayNoEntryErrorMessage(R.string.no_entry_here);
        }
        else { mView.hideMessageView(); }

        mEntryList.clear();
        mEntryList.addAll(entries);
        mView.updateAdapterData(entries);
        mCurrentIndex = getLastIndex();
        mView.moveToPage(mCurrentIndex);
        mView.dismissProgressDialog();
    }

    @Override
    public void onDataNotAvailable() {
        if (mPack.getTag().equals(Contract.TAG_SAVE_FOR_GOOD) || mPack.getTag().equals(
                Contract.TAG_SAVE_FOR_LATER)) {
            mView.displayNoEntryErrorMessage(R.string.no_entry_here);
        }
    }

    @Override
    public void onError(String error) {
        mView.toast("Bir sorunla karşılaşıldı.");
        mView.dismissProgressDialog();
        Log.e(LOG_TAG, error);
    }

    @Override
    public void onProgressUpdate(int progress) {
        mView.updateProgress(progress);
    }

    @Override
    public void onMessageUpdate(String message) {
        mView.updateProgressMessage(message);
    }

    @Override
    public void onDataLoadStart(boolean fromLocal) {
        if (fromLocal) {
            mView.showSpinnerProgressDialog("\"Entriler okunuyor...\"");
        }
        else {
            mView.showHorizontalProgressDialog(R.string.reading_entries);
        }
    }

    @Override
    public boolean checkIfOnline() {
        return mView.checkIfOnline();
    }
}