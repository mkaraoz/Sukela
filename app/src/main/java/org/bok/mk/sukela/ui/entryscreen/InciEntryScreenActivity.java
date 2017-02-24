package org.bok.mk.sukela.ui.entryscreen;

import android.app.Activity;
import android.os.Bundle;

import org.bok.mk.sukela.helper.ReturnCodes;
import org.bok.mk.sukela.helper.T;
import org.bok.mk.sukela.source.InciSozluk;
import org.bok.mk.sukela.ui.entryscreen.asyncdownloader.MultiplePageDownloadTask;

public class InciEntryScreenActivity extends EntryScreenActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void getEntriesFromInternet() {
        InciEntryFiller asyncTask = new InciEntryFiller(this);
        asyncTask.execute();
    }

    protected class InciEntryFiller extends MultiplePageDownloadTask {
        private InciSozluk sozluk = new InciSozluk(InciEntryScreenActivity.this, META);

        InciEntryFiller(Activity a) {
            super(a);
            super.init(sozluk, META);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Void... args) {
            return super.doInBackground(args);
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);

            if (mIsAsyncTaskCancelled) {
                mCurrentIndex = 0;
                mEntryList = mDownloadedEntries;
                updateViewPager();
                T.toast(InciEntryScreenActivity.this, mSavedEntryCount + " adet entry kaydedildi ve işlem iptal edildi.");
            } else if (result == ReturnCodes.SUCCESS_DOWNLOAD) {
                mCurrentIndex = 0;
                mEntryList = mDownloadedEntries;
                updateViewPager();
                T.toast(InciEntryScreenActivity.this, mSavedEntryCount + " adet entry kaydedildi.");
            } else {
                displayFailMessage(result);
            }

            mHorizontalProgressDialog.dismiss();
        }
    }
}
