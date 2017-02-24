package org.bok.mk.sukela.ui.entryscreen.asyncdownloader;

import android.app.Activity;

import org.bok.mk.sukela.helper.Meta;
import org.bok.mk.sukela.helper.callbacks.MultiFileDownloadCallback;
import org.bok.mk.sukela.helper.ReturnCodes;
import org.bok.mk.sukela.helper.T;
import org.bok.mk.sukela.source.MultiPageSource;

import java.io.IOException;
import java.util.List;

/**
 * Created by mk on 10.01.2017.
 */

public class MultiplePageDownloadTask extends BasePageDownloadTask implements MultiFileDownloadCallback {
    protected MultiPageSource mMultiPageSource;

    public MultiplePageDownloadTask(Activity a) {
        super(a);
    }

    protected void init(MultiPageSource multiPageSource, Meta meta) {
        this.mMultiPageSource = multiPageSource;
        this.mMeta = meta;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    public void displayFailMessage(int failCode) {
        if (failCode == ReturnCodes.ENTRY_LIST_DOWNLOAD_FAILED) {
            T.toast(mParentActivity, "Entry listesi indirilemedi.");
        } else if (failCode == ReturnCodes.ENTRY_DOWNLOAD_FAILED) {
            T.toast(mParentActivity, "Entryler indirilemedi. Sözlüğe erişim engellenmiş olabilir.");
        }
    }

    @Override
    protected Integer doInBackground(Void... voids) {
        // önce indirilecek entilerin numalarını bul
        updateProgressBarMessage("Yeni liste indiriliyor.");
        List<String> entryIds;
        try {
            entryIds = mMultiPageSource.getEntryNumbersFromUrl(mMeta.getListUrl());
            if (mIsAsyncTaskCancelled) {
                return ReturnCodes.DOWNLOAD_CANCELLED;
            }
        } catch (IOException e) {
            return ReturnCodes.ENTRY_LIST_DOWNLOAD_FAILED;
        }

        mHorizontalProgressDialog.setMax(entryIds.size());

        try {
            mDownloadedEntries = mMultiPageSource.downloadEntries(entryIds, this);
            publishProgress(entryIds.size());
            updateProgressBarMessage("Entriler kaydediliyor...");
            mSavedEntryCount = mMultiPageSource.saveEntriesToLocalStorage(mDownloadedEntries.unmodifiableList());
            return ReturnCodes.SUCCESS_DOWNLOAD;
        } catch (IOException e) {
            return ReturnCodes.ENTRY_DOWNLOAD_FAILED;
        }
    }

    @Override
    public void updateProgress(int progress) // callbacks metodu
    {
        this.publishProgress(progress); // async task metodu
    }

    @Override
    public boolean isTaskCancelled() // callbacks metodu
    {
        return mIsAsyncTaskCancelled;
    }
}
