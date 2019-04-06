package org.bok.mk.sukela.ui.entryscreen.a;

import org.bok.mk.sukela.data.model.EntryList;
import org.bok.mk.sukela.data.model.SozlukEnum;

import java.util.List;

interface EntryScreenActivityContract
{
    interface View
    {
        // FAB methods
        void updateFabSettingsVisibility(int visibility);

        void setFabMenuColors(int normal, int pressed, int ripple);

        void addFabMenuItem(String tag, int iconResId, String title, int normal, int pressed, int ripple);

        void setSettingsFabItemColor(int normal, int pressed, int ripple);

        void closeFabMenu();

        // Ads
        void requestNewInterstitial();

        boolean isInterstitialAdLoaded();

        void showInterstitialAd();

        void requestNewBannerAd();

        void setAdViewVisibility(int visibility);

        // ui ops
        void setThisTheme(int themeId);

        void paintStatusBar2Black();

        void toast(final String message);

        void displayFancySaveToast();

        // adapter
        int getAdapterSize();

        void updatePageNumberMenu(int newIndex);

        void moveToPage(int index);

        void updateAdapterData(EntryList entries);

        // dialog alert pop ups
        void displayEntryTitlesList(List<String> titleList, int position);

        void displayRefreshWarning();

        void displaySaveOptions(int[] optionResIds);

        void displayEntryDeleteWarning();

        void displaySozlukOptions(String[] sozlukNames);

        void showEntryNumberPopup(SozlukEnum sozlukEnum);

        void displayRestorePopup();

        void displayBackupOptions();

        void displayBackUpFileOptions(List<String> backupFileNames);

        void dismissAlertDialog();

        //
        // dialog pop ups - progress dialogs
        //
        void showSpinnerProgressDialog(String message);

        void showHorizontalProgressDialog(int messageResId);

        void dismissProgressDialog();

        void updateProgress(int progress);

        void updateProgressMessage(String message);

        //
        // helper methods
        //
        void displayNoEntryErrorMessage(int messageResId);

        void hideMessageView();

        void share(String type, int flag, String data);

        boolean checkIfOnline();

        String getExternalFilesDir();

        String getStringRes(int key_link_color);

        void startSettingsActivity();

        void startSearchActivity();

        void finishActivity();

    }

    interface Presenter
    {
        void pickTheme();

        void pagerPageChanged(int position);

        void uiLoadCompleted();

        void saveLastPosition();

        int getMenu();

        void pageNumberClicked();

        void searchClicked();

        void fabOpened();

        void fabClosed();

        void fabMenuItemClicked(String tag);

        void fabMenuCreated();

        void bannerAdLoaded();

        void titleListItemClicked(int position);

        void refreshYesClicked();

        void refreshCancelClicked();

        void saveOptionSelected(int which);

        void deleteYesClicked();

        void deleteCancelClicked();

        void sozlukOptionSelected(SozlukEnum sozlukEnum);

        void downloadEntryOkClicked(SozlukEnum sozlukEnum, String entryNo);

        void downloadEntryCancelClicked();

        void restoreWarningApproved();

        void progressDialogCancelButtonClicked();

        void backupOptionSelected();

        void restoreOptionSelected();

        void backupSelected(String backUpFileName);
    }
}
