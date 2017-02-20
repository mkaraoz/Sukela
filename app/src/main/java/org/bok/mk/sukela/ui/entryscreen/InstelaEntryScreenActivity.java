package org.bok.mk.sukela.ui.entryscreen;

import android.app.Activity;
import android.os.Bundle;

import org.bok.mk.sukela.helper.ReturnCodes;
import org.bok.mk.sukela.helper.T;
import org.bok.mk.sukela.source.Instela;
import org.bok.mk.sukela.ui.entryscreen.asyncdownloader.SinglePageDownloadTask;

public class InstelaEntryScreenActivity extends EntryScreenActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void getEntriesFromInternet()
    {
        InstelaEntryFiller asyncTask = new InstelaEntryFiller(this);
        asyncTask.execute();
    }

    protected class InstelaEntryFiller extends SinglePageDownloadTask
    {
        private Instela sozluk = new Instela(InstelaEntryScreenActivity.this, META);

        InstelaEntryFiller(Activity a)
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
                T.toast(InstelaEntryScreenActivity.this, mSavedEntryCount + " adet entry kaydedildi.");
            }
            else if (result == ReturnCodes.PAGE_DOWNLOAD_FAILED)
            {
                T.toastLong(mParentActivity, "Sunucuya bağlanamadı. Instela'ya erişebildiğinizden emin olup tekrar deneyin.");
            }
            else
            {
                displayFailMessage(result);
            }

            mHorizontalProgressDialog.dismiss();
        }

        @Override
        public String getTempFileName()
        {
            return mMeta.getTag() + ".html";
        }
    }
}
