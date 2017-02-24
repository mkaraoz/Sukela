package org.bok.mk.sukela.ui.entryscreen.asyncdownloader;

import android.app.Activity;

import com.androidnetworking.error.ANError;

import org.apache.commons.io.FileUtils;
import org.bok.mk.sukela.helper.Contract;
import org.bok.mk.sukela.helper.Meta;
import org.bok.mk.sukela.helper.ReturnCodes;
import org.bok.mk.sukela.helper.T;
import org.bok.mk.sukela.helper.callbacks.SingleFileDownloadCallback;
import org.bok.mk.sukela.helper.exception.JerichoFileReadException;
import org.bok.mk.sukela.helper.network.DownloadPack;
import org.bok.mk.sukela.helper.network.NetworkManager;
import org.bok.mk.sukela.source.SinglePageSource;

import java.io.File;
import java.io.IOException;

/**
 * Created by mk on 10.01.2017.
 */

public class SinglePageDownloadTask extends BasePageDownloadTask implements SingleFileDownloadCallback {
    protected SinglePageSource mSinglePageSource;
    protected volatile boolean mDownloadComplete = false;
    protected volatile boolean mErrorOccurred = false;

    public SinglePageDownloadTask(Activity a) {
        super(a);
    }

    protected void init(SinglePageSource singlePageSource, Meta meta) {
        this.mSinglePageSource = singlePageSource;
        this.mMeta = meta;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    public void displayFailMessage(int failCode) {
        if (failCode == ReturnCodes.FILE_READ_ERROR) {
            T.toastLong(mParentActivity, "İndirilen dosya okunamadı. Lütfen listeyi yenileyin.");
        } else if (failCode == ReturnCodes.MISSING_FILE) {
            T.toastLong(mParentActivity, "İnsan gerçekten hayret ediyor. İndirdiğim dosyayı bulamıyorum.");
        } else if (failCode == ReturnCodes.DOWNLOAD_CANCELLED) {
            T.toastLong(mParentActivity, "İşlem iptal edildi");
        }
    }

    @Override
    protected Integer doInBackground(Void... voids) {
        // önce indirilecek entilerin numalarını bul
        updateProgressBarMessage("Yeni liste indiriliyor.");
        String directory = mParentActivity.getFilesDir().getAbsolutePath();
        String fileName = getTempFileName();//mMeta.getTag() + ".html";

        try {
            NetworkManager.downloadPage(DownloadPack.create(mMeta.getListUrl(), directory, fileName), mMeta.getTag(), this);

            for (; ; ) {
                if (mIsAsyncTaskCancelled) {
                    return ReturnCodes.DOWNLOAD_CANCELLED;
                }

                if (mErrorOccurred) {
                    throw new IOException();
                }

                if (mDownloadComplete) {
                    break;
                }
            }
        } catch (IOException e) {
            return ReturnCodes.PAGE_DOWNLOAD_FAILED;
        }

        publishProgress(100); // async task metodu
        updateProgressBarMessage("Entriler okunuyor...");

        File tempFile = new File(directory + File.separator + fileName);
        if (tempFile.exists()) {
            try {
                mDownloadedEntries = mSinglePageSource.readEntriesFromHtmlFile(tempFile);
                FileUtils.deleteQuietly(tempFile);
                mSavedEntryCount = mSinglePageSource.saveEntriesToLocalStorage(mDownloadedEntries.unmodifiableList());
                return ReturnCodes.SUCCESS_DOWNLOAD;
            } catch (JerichoFileReadException e) {
                return ReturnCodes.FILE_READ_ERROR;
            }
        } else {
            return ReturnCodes.MISSING_FILE;
        }
    }

    private boolean isDownloadComplete() {
        return mDownloadComplete;
    }

    @Override
    public void onDownloadComplete() {
        mDownloadComplete = true;
    }

    @Override
    public void onError(ANError anError) {
        mErrorOccurred = true;
    }

    @Override
    public void onProgress(long bytesDownloaded, long lengthOfFile) {
        if (lengthOfFile <= 0) {
            lengthOfFile = 200000; // average estimate
        }
        long progress = (bytesDownloaded * 100) / lengthOfFile;
        this.publishProgress((int) Math.floor(progress));
    }

    protected String getTempFileName() {
        if (mMeta.getTag().equals(Contract.TAG_EKSI_DEBE)) {
            return mMeta.getTag() + ".html";
        } else {
            return mMeta.getTag() + ".xml";
        }
    }
}
