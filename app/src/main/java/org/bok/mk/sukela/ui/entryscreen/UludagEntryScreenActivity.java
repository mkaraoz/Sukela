package org.bok.mk.sukela.ui.entryscreen;

import android.app.Activity;
import android.os.Bundle;

import org.bok.mk.sukela.helper.ReturnCodes;
import org.bok.mk.sukela.helper.T;
import org.bok.mk.sukela.source.UludagSozluk;
import org.bok.mk.sukela.ui.entryscreen.asyncdownloader.MultiplePageDownloadTask;

public class UludagEntryScreenActivity extends EntryScreenActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void getEntriesFromInternet()
    {
        UludagEntryFiller asyncTask = new UludagEntryFiller(this);
        asyncTask.execute();
    }

    protected class UludagEntryFiller extends MultiplePageDownloadTask
    {
        private UludagSozluk sozluk = new UludagSozluk(UludagEntryScreenActivity.this, META);

        UludagEntryFiller(Activity a)
        {
            super(a);
            super.init(sozluk, META);
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Void... args)
        {
            return super.doInBackground(args);
        }

        @Override
        protected void onPostExecute(Integer result)
        {
            super.onPostExecute(result);

            if (mIsAsyncTaskCancelled)
            {
                mCurrentIndex = 0;
                mEntryList = mDownloadedEntries;
                updateViewPager();
                T.toast(UludagEntryScreenActivity.this, mSavedEntryCount + " adet entry kaydedildi ve işlem iptal edildi.");
            }
            else if (result == ReturnCodes.SUCCESS_DOWNLOAD)
            {
                mCurrentIndex = 0;
                mEntryList = mDownloadedEntries;
                updateViewPager();
                T.toast(UludagEntryScreenActivity.this, mSavedEntryCount + " adet entry kaydedildi.");
            }
            else
            {
                displayFailMessage(result);
            }

            mHorizontalProgressDialog.dismiss();
        }
    }
}
