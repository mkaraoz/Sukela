package org.bok.mk.sukela.ui.entryscreen.asyncdownloader;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;

import org.bok.mk.sukela.entry.EntryList;
import org.bok.mk.sukela.helper.Meta;
import org.bok.mk.sukela.helper.callbacks.ProgressDialogCancelButtonListener;

/**
 * Created by mk on 10.01.2017.
 */

public abstract class BasePageDownloadTask extends AsyncTask<Void, Integer, Integer> implements ProgressDialogCancelButtonListener {
    protected ProgressDialog mHorizontalProgressDialog;
    protected Activity mParentActivity;
    protected volatile boolean mIsAsyncTaskCancelled = false;
    protected EntryList mDownloadedEntries;
    protected Meta mMeta;
    protected int mSavedEntryCount;

    public BasePageDownloadTask(Activity a) {
        mParentActivity = a;
    }

    protected void updateProgressBarMessage(final String message) {
        mParentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mHorizontalProgressDialog.setMessage(message);
            }
        });
    }

    @Override
    public void cancelButtonClicked() {
        mIsAsyncTaskCancelled = true;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mHorizontalProgressDialog = new ProgressDialog(mParentActivity);
        mHorizontalProgressDialog.setCancelable(false);
        mHorizontalProgressDialog.setCanceledOnTouchOutside(false);
        mHorizontalProgressDialog.setMessage("Entriler yükleniyor...");
        mHorizontalProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mHorizontalProgressDialog.setIndeterminate(false);
        mHorizontalProgressDialog.setMax(100);
        mHorizontalProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                BasePageDownloadTask.this.cancelButtonClicked();
                dialog.dismiss();
            }
        });
        mHorizontalProgressDialog.show();
    }

    @Override // async task metodu
    protected void onProgressUpdate(Integer... progress) {
        mHorizontalProgressDialog.setProgress(progress[0]);
    }

    public abstract void displayFailMessage(int failCode);
}
