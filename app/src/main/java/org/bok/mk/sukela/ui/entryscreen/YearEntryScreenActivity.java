package org.bok.mk.sukela.ui.entryscreen;

import android.app.Activity;
import android.os.Bundle;

import org.bok.mk.sukela.helper.ReturnCodes;
import org.bok.mk.sukela.helper.T;
import org.bok.mk.sukela.source.EksiSozluk;
import org.bok.mk.sukela.ui.entryscreen.asyncdownloader.SinglePageDownloadTask;

public class YearEntryScreenActivity extends EntryScreenActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void getEntriesFromInternet()
    {
        YearEntryFiller asyncTask = new YearEntryFiller(this);
        asyncTask.execute();
    }

    protected class YearEntryFiller extends SinglePageDownloadTask
    {
        private EksiSozluk sozluk = new EksiSozluk(YearEntryScreenActivity.this, META);

        YearEntryFiller(Activity a)
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

            if (result == ReturnCodes.SUCCESS_DOWNLOAD)
            {
                mCurrentIndex = 0;
                mEntryList = mDownloadedEntries;
                updateViewPager();
                T.toast(YearEntryScreenActivity.this, mSavedEntryCount + " adet entry kaydedildi.");
            }
            else if (result == ReturnCodes.PAGE_DOWNLOAD_FAILED)
            {
                T.toastLong(mParentActivity, "Sunucuya bağlanamadı. Dropbox'a erişebildiğinizden emin olup tekrar deneyin.");
            }
            else
            {
                displayFailMessage(result);
            }

            mHorizontalProgressDialog.dismiss();
        }
    }
}
