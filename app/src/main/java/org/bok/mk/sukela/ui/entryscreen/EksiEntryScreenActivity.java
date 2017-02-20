package org.bok.mk.sukela.ui.entryscreen;

import android.app.Activity;
import android.os.Bundle;

import org.bok.mk.sukela.helper.Contract;
import org.bok.mk.sukela.helper.ReturnCodes;
import org.bok.mk.sukela.helper.T;
import org.bok.mk.sukela.source.EksiSozluk;
import org.bok.mk.sukela.ui.entryscreen.asyncdownloader.MultiplePageDownloadTask;
import org.bok.mk.sukela.ui.entryscreen.asyncdownloader.SinglePageDownloadTask;

public class EksiEntryScreenActivity extends EntryScreenActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void getEntriesFromInternet()
    {
        if (META.getTag().equals(Contract.TAG_EKSI_DEBE))
        {
            SozlockDebeFiller debeTask = new SozlockDebeFiller(this);
            debeTask.execute();
        }
        else
        {
            EksiEntryFiller asyncTask = new EksiEntryFiller(this);
            asyncTask.execute();
        }
    }

    protected class SozlockDebeFiller extends SinglePageDownloadTask
    {
        private EksiSozluk sozluk = new EksiSozluk(EksiEntryScreenActivity.this, META);

        SozlockDebeFiller(Activity a)
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
                T.toast(EksiEntryScreenActivity.this, mSavedEntryCount + " adet entry kaydedildi.");
            }
            else if (result == ReturnCodes.PAGE_DOWNLOAD_FAILED)
            {
                T.toastLong(mParentActivity, "Sunucuya bağlanamadı. Sözlüğe'a erişebildiğinizden emin olup tekrar deneyin.");
            }
            else
            {
                displayFailMessage(result);
            }

            mHorizontalProgressDialog.dismiss();
        }
    }

    protected class EksiEntryFiller extends MultiplePageDownloadTask
    {
        private EksiSozluk sozluk = new EksiSozluk(EksiEntryScreenActivity.this, META);

        public EksiEntryFiller(Activity a)
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
                T.toast(EksiEntryScreenActivity.this, mSavedEntryCount + " adet entry kaydedildi ve işlem iptal edildi.");
            }
            else if (result == ReturnCodes.SUCCESS_DOWNLOAD)
            {
                mCurrentIndex = 0;
                mEntryList = mDownloadedEntries;
                updateViewPager();
                T.toast(EksiEntryScreenActivity.this, mSavedEntryCount + " adet entry kaydedildi.");
            }
            else
            {
                displayFailMessage(result);
            }

            mHorizontalProgressDialog.dismiss();
        }
    }
}
