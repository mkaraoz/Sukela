package org.bok.mk.sukela.ui.entryscreen;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import org.bok.mk.sukela.helper.Contract;
import org.bok.mk.sukela.source.EksiSozluk;

public class UserEntryScreenActivity extends EntryScreenActivity
{
    private String mUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        mUserName = getIntent().getExtras().getString(Contract.TAG_USER);
        super.onCreate(savedInstanceState);
        setTitle(mUserName);
        mFabMenu.removeMenuButton(mFabRefresh);
    }

    @Override
    protected void saveLastPosition()
    {
        sukelaPrefs.putInt(META.getTag() + " " + mUserName, mCurrentIndex);
    }

    @Override
    protected int getLastIndex()
    {
        String key = META.getTag() + "+" + mUserName;

        int lastIndex = sukelaPrefs.getInt(key, 0);

        if (lastIndex > mEntryList.size() - 1)
        {
            lastIndex = mEntryList.size() - 1;
        }
        if (lastIndex < 0)
        {
            lastIndex = 0;
        }

        return lastIndex;
    }

    @Override
    protected void fillEntryList()
    {
        EksiSozluk sozluk = new EksiSozluk(this, META);
        int entryCount = sozluk.countUserEntries(mUserName);

        if (entryCount > 0)
        {
            LocalEntryFiller filler = new LocalEntryFiller(sozluk);
            filler.execute();
        }
    }

    protected class LocalEntryFiller extends AsyncTask<Void, Void, Integer>
    {
        private final EksiSozluk mSozluk;
        private ProgressDialog mSpinner;

        LocalEntryFiller(EksiSozluk sozluk)
        {
            this.mSozluk = sozluk;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            mSpinner = new ProgressDialog(UserEntryScreenActivity.this);
            mSpinner.setCancelable(false);
            mSpinner.setCanceledOnTouchOutside(false);
            mSpinner.setMessage("Entriler okunuyor...");
            mSpinner.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mSpinner.setIndeterminate(true);
            mSpinner.show();
        }

        @Override
        protected Integer doInBackground(Void... voids)
        {
            Uri uri = Uri.withAppendedPath(META.getDataUri(), mUserName);
            mEntryList = mSozluk.getEntriesFromDb(uri);
            return mCurrentIndex;
        }

        @Override
        protected void onPostExecute(Integer lastIndex)
        {
            super.onPostExecute(lastIndex);
            updateViewPager();
            mSpinner.dismiss();
        }
    }

    @Override
    protected void getEntriesFromInternet()
    {
        // yenile butonu belki buraya yönlenir bir gün
    }
}
