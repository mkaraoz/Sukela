package org.bok.mk.sukela.ui.entryscreen;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.androidnetworking.error.ANError;
import com.github.clans.fab.FloatingActionButton;

import org.apache.commons.io.FileUtils;
import org.bok.mk.sukela.R;
import org.bok.mk.sukela.data.DatabaseContract;
import org.bok.mk.sukela.data.LocalDbManager;
import org.bok.mk.sukela.entry.Entry;
import org.bok.mk.sukela.entry.EntryList;
import org.bok.mk.sukela.entry.EntryManager;
import org.bok.mk.sukela.helper.BackupManager;
import org.bok.mk.sukela.helper.CommonOps;
import org.bok.mk.sukela.helper.Contract;
import org.bok.mk.sukela.helper.ReturnCodes;
import org.bok.mk.sukela.helper.SozlukEnum;
import org.bok.mk.sukela.helper.SozlukFactory;
import org.bok.mk.sukela.helper.T;
import org.bok.mk.sukela.helper.callbacks.ProgressDialogCancelButtonListener;
import org.bok.mk.sukela.helper.callbacks.SingleFileDownloadCallback;
import org.bok.mk.sukela.helper.network.DownloadPack;
import org.bok.mk.sukela.helper.network.NetworkManager;
import org.bok.mk.sukela.source.Sozluk;
import org.bok.mk.sukela.ui.SearchActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SavedEntryScreenActivity extends EntryScreenActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupSavedEntryScreenMenu();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    private void setupSavedEntryScreenMenu() {
        mFabMenu.removeMenuButton(mFabRefresh);
        mFabMenu.removeMenuButton(mFabSave);

        FloatingActionButton mFabGetEntryFromWeb = new FloatingActionButton(this);
        mFabGetEntryFromWeb.setButtonSize(FloatingActionButton.SIZE_MINI);
        mFabGetEntryFromWeb.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_cloud_download_white_24dp));
        mFabGetEntryFromWeb.setLabelText("Sözlükten al");
        mFabGetEntryFromWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] sozluk_names = new String[4];
                sozluk_names[0] = SozlukEnum.EKSI.getName();
                sozluk_names[1] = SozlukEnum.INSTELA.getName();
                sozluk_names[2] = SozlukEnum.ULUDAG.getName();
                sozluk_names[3] = SozlukEnum.INCI.getName();

                AlertDialog.Builder builder = new AlertDialog.Builder(SavedEntryScreenActivity.this);
                builder.setItems(sozluk_names, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        showEntryNumberPopup(SozlukEnum.getSozlukEnum(sozluk_names[which]));
                    }
                });

                dialog = builder.create();
                dialog.show();

                mFabMenu.close(true);
            }
        });

        FloatingActionButton mFabBackupRestore = new FloatingActionButton(this);
        mFabBackupRestore.setButtonSize(FloatingActionButton.SIZE_MINI);
        mFabBackupRestore.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_import_export_white_24dp));
        mFabBackupRestore.setLabelText("Yedek");
        mFabBackupRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFabMenu.close(false);
                CharSequence options[] = new CharSequence[]{getString(R.string.backup), getString(R.string.restore)};

                AlertDialog.Builder builder = new AlertDialog.Builder(SavedEntryScreenActivity.this);
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) //saveEntryForGood();
                        {
                            backupSavedEntries();
                        } else {
                            displayRestorePopup();
                        }
                    }
                });
                builder.show();
            }
        });

        FloatingActionButton mFabDelete = new FloatingActionButton(this);
        mFabDelete.setButtonSize(FloatingActionButton.SIZE_MINI);
        mFabDelete.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_delete_white_24dp));
        mFabDelete.setLabelText("Sil");
        mFabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mEntryList.isEmpty()) {
                    T.toast(SavedEntryScreenActivity.this, "Neyi sileyim şimdi ben?");
                    mFabMenu.close(false);
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(SavedEntryScreenActivity.this);
                builder.setTitle("Sil");
                builder.setMessage(getString(R.string.delete_entry));

                builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Entry e = mEntryList.get(mCurrentIndex);
                        int entryNumber = e.getEntryNo();
                        LocalDbManager manager = new LocalDbManager(SavedEntryScreenActivity.this);
                        String uriTag = META.getTag().equals(Contract.TAG_SAVE_FOR_GOOD) ? Contract.TAG_DELETE_SINGLE_GOOD : Contract.TAG_DELETE_SINGLE_LONG;
                        Uri uri = Uri.withAppendedPath(DatabaseContract.EntryTable.CONTENT_URI, uriTag);
                        uri = Uri.withAppendedPath(uri, String.valueOf(entryNumber));
                        int count = manager.deleteEntries(uri);
                        T.toast(SavedEntryScreenActivity.this, count + " adet entry silindi.");
                        mEntryList.remove(e);
                        updateViewPager();
                        mFabMenu.close(false);
                    }
                });

                builder.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        List<FloatingActionButton> fabItemList = new ArrayList<>();
        fabItemList.add(mFabGetEntryFromWeb);
        fabItemList.add(mFabBackupRestore);
        fabItemList.add(mFabDelete);
        setFabMenuItemColors(fabItemList);

        if (META.getTag().equals(Contract.TAG_SAVE_FOR_GOOD)) {
            mFabMenu.addMenuButton(mFabGetEntryFromWeb);
            mFabMenu.addMenuButton(mFabBackupRestore);
        }
        mFabMenu.addMenuButton(mFabDelete);
    }

    private void displayRestorePopup() {
        String title = "Uyarı";
        String message = "Aldığınız yedekleri geri yüklemek için, ilgili xml dosyasını sdcard altında " +
                getExternalFilesDir(null).getPath() + " klasörü altına koymalısınız.";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                selectBackUpFile();
            }
        });
        dialog = builder.create();
        dialog.show();
    }

    private void selectBackUpFile() {
        final List<String> backupFileNames = getBackupFileNames();
        LayoutInflater inflater = getLayoutInflater();
        View customView = inflater.inflate(R.layout.orta_liste, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Yüklenecek yedek");
        builder.setView(customView);
        ListView list = (ListView) customView.findViewById(R.id.listView1);
        list.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, backupFileNames));

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedBackUpFileName = backupFileNames.get(position);
                restoreEntriesFromBackUpFile(selectedBackUpFileName);
                dialog.dismiss();
            }
        });

        dialog = builder.create();
        dialog.show();
    }

    private void restoreEntriesFromBackUpFile(final String selectedBackUpFileName) {
        AsyncTask<Void, Void, Integer> asyncTask = new AsyncTask<Void, Void, Integer>() {
            private ProgressDialog progressBar;
            private EntryManager manager = EntryManager.getManager(SavedEntryScreenActivity.this);

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar = new ProgressDialog(SavedEntryScreenActivity.this);
                progressBar.setCancelable(false);
                progressBar.setCanceledOnTouchOutside(false);
                progressBar.setIndeterminate(true);
                progressBar.setMessage("Entriler geri getiriliyor.");
                progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressBar.show();
            }

            @Override
            protected Integer doInBackground(Void... params) {
                EntryList restoredEntryList = BackupManager.restoreEntriesFromBackUpFile(new File(getExternalFilesDir(null) + File.separator + selectedBackUpFileName));
                int counter = 0;
                for (Entry e : restoredEntryList) {
                    boolean isSaved = manager.saveEntry(e.createCopyWithAnotherTag(META.getTag()));
                    if (isSaved) {
                        counter++;
                    }
                }
                return counter;
            }

            @Override
            protected void onPostExecute(Integer result) {
                super.onPostExecute(result);
                mEntryList = manager.getStoredEntries(META.getDataUri());
                String message = result + " adet entry geri getirildi.";
                T.toast(SavedEntryScreenActivity.this, message);

                mCurrentIndex = 0;
                updateViewPager();
                progressBar.dismiss();
            }
        };
        asyncTask.execute();
    }

    private void backupSavedEntries() {
        if (mEntryList.size() > 0) // Saklayacak entry yoksa yolun başından dönüyoruz
        {
            AsyncTask<Void, Void, Boolean> asyncTask = new AsyncTask<Void, Void, Boolean>() {
                private ProgressDialog progressBar;

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    progressBar = new ProgressDialog(SavedEntryScreenActivity.this);
                    progressBar.setCancelable(false);
                    progressBar.setCanceledOnTouchOutside(false);
                    progressBar.setIndeterminate(true);
                    progressBar.setMessage("Entryler yedekleniyor.");
                    progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressBar.show();
                }

                @Override
                protected Boolean doInBackground(Void... params) {
                    for (Entry e : mEntryList) {
                        String entryBody = e.getBody();
                        entryBody = entryBody.replaceAll("&lt;", "<");
                        entryBody = entryBody.replaceAll("&gt;", ">");
                        entryBody = entryBody.replaceAll("&amp;", "&");
                        e.setBody(entryBody);
                    }
                    String backUpFileName = generateNameForBackUpFile();

                    File f = new File(getExternalFilesDir(null).getPath() + File.separator + backUpFileName);
                    return BackupManager.saveEntriesToSdCard(mEntryList, f);
                }

                @Override
                protected void onPostExecute(Boolean entriesBackedUp) {
                    super.onPostExecute(entriesBackedUp);
                    progressBar.dismiss();
                    if (entriesBackedUp) {
                        T.toastLong(SavedEntryScreenActivity.this, "Entriler " + getExternalFilesDir(null).getPath() + " altında yedeklendi.");
                    } else {
                        T.toast(SavedEntryScreenActivity.this, "HATA: Entrilerin yedeği alınamadı. Lütfen tekrar deneyin.");
                    }
                }
            };
            asyncTask.execute();
        }
    }

    private void showEntryNumberPopup(final SozlukEnum sozlukEnum) {
        META.setSozluk(sozlukEnum);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Saklamak istediğiniz entry'nin numarasını girin:");

        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                CommonOps.hideSoftKeyboard(input, SavedEntryScreenActivity.this);
                String entryNumber = input.getText().toString();
                SingleEntryGetter seg = new SingleEntryGetter(entryNumber);
                seg.execute();
            }
        });
        builder.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });

        dialog = builder.create();
        dialog.show();

        CommonOps.showSoftKeyboard(this);
    }

    @Override
    protected void fillEntryList() {
        EntryManager manager = EntryManager.getManager(this);
        int entryCount = manager.countEntries(META.getDataUri());

        // if (mEntryList!=null && mEntryList.size() != entryCount)

        if (entryCount > 0) {
            LocalEntryFiller filler = new LocalEntryFiller(manager);
            filler.execute();
        } else {
            mEntryList.clear();
            updateViewPager();
        }
    }

    public List<String> getBackupFileNames() {
        File appExtDirectory = getExternalFilesDir(null);
        File[] backupFiles = appExtDirectory.listFiles();
        List<String> backupFileNames = new ArrayList<>();
        for (File backUp : backupFiles) {
            String extension = "";
            int i = backUp.getName().lastIndexOf('.');
            if (i > 0) {
                extension = backUp.getName().substring(i + 1);
            }
            if (extension.equalsIgnoreCase("xml")) {
                backupFileNames.add(backUp.getName());
            }
        }
        return backupFileNames;
    }

    protected class LocalEntryFiller extends AsyncTask<Void, Void, Integer> {
        private final EntryManager mEntryManager;
        private ProgressDialog mSpinner;

        LocalEntryFiller(EntryManager manager) {
            this.mEntryManager = manager;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mSpinner = new ProgressDialog(SavedEntryScreenActivity.this);
            mSpinner.setCancelable(false);
            mSpinner.setCanceledOnTouchOutside(false);
            mSpinner.setMessage("Entriler okunuyor...");
            mSpinner.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mSpinner.setIndeterminate(true);
            mSpinner.show();
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            mEntryList = mEntryManager.getStoredEntries(META.getDataUri());
            mCurrentIndex = getLastIndex();
            return mCurrentIndex;
        }

        @Override
        protected void onPostExecute(Integer lastIndex) {
            super.onPostExecute(lastIndex);
            updateViewPager();
            mSpinner.dismiss();
        }
    }

    @Override
    protected void getEntriesFromInternet() {
        // burası sadece database'den entry gösteriyor
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (META.getTag().equals(Contract.TAG_SAVE_FOR_GOOD)) {
            getMenuInflater().inflate(R.menu.menu_saved_entry_screen, menu);
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (META.getTag().equals(Contract.TAG_SAVE_FOR_GOOD)) {
            menu.findItem(R.id.action_page_number).setTitle(String.valueOf(mCurrentIndex + 1));
            return super.onPrepareOptionsMenu(menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.find) {
            Intent i = new Intent(this, SearchActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Menuden tüm entriler silindi mi diye bakıyor
        checkIfStillHaveEntries();
    }

    private void checkIfStillHaveEntries() {
        EntryManager manager = EntryManager.getManager(this);
        int entryCount = manager.countEntries(META.getDataUri());
        if (entryCount == 0) {
            fillEntryList();
        }
    }

    //@SuppressLint("SimpleDateFormat")
    private static String generateNameForBackUpFile() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        return dateFormat.format(date) + ".xml";
    }

    protected class SingleEntryGetter extends AsyncTask<Void, Integer, Integer> implements SingleFileDownloadCallback, ProgressDialogCancelButtonListener {
        private boolean mDownloadComplete = false;
        private boolean mErrorOccurred = false;
        private Sozluk mSozluk = SozlukFactory.getSozluk(META, SavedEntryScreenActivity.this);
        private String mEntryNumber;
        private ProgressDialog mHorizontalProgressDialog;
        boolean mIsAsyncTaskCancelled = false;

        SingleEntryGetter(String entryNumber) {
            mEntryNumber = entryNumber;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            super.onPreExecute();
            mHorizontalProgressDialog = new ProgressDialog(SavedEntryScreenActivity.this);
            mHorizontalProgressDialog.setCancelable(false);
            mHorizontalProgressDialog.setCanceledOnTouchOutside(false);
            mHorizontalProgressDialog.setMessage("Entriler yükleniyor...");
            mHorizontalProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mHorizontalProgressDialog.setIndeterminate(false);
            mHorizontalProgressDialog.setMax(100);
            mHorizontalProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    cancelButtonClicked();
                    dialog.dismiss();
                }
            });
            mHorizontalProgressDialog.show();
        }

        @Override
        protected Integer doInBackground(Void... args) {
            // önce indirilecek entilerin numalarını bul
            updateProgressBarMessage("Entry indiriliyor.");
            String directory = SavedEntryScreenActivity.this.getFilesDir().getAbsolutePath();
            String fileName = "sakla.html";

            try {
                DownloadPack pack = DownloadPack.create(mSozluk.getBaseEntryPath() + mEntryNumber, directory, fileName);
                NetworkManager.downloadPage(pack, META.getTag(), this);
                while (!mIsAsyncTaskCancelled && !mDownloadComplete) {
                    if (mErrorOccurred) {
                        throw new IOException();
                    }
                }

                if (mIsAsyncTaskCancelled) {
                    return ReturnCodes.DOWNLOAD_CANCELLED;
                }
            } catch (IOException e) {
                return ReturnCodes.PAGE_DOWNLOAD_FAILED;
            }

            publishProgress(100); // async task metodu
            updateProgressBarMessage("Entriler okunuyor...");

            File tempFile = new File(directory + File.separator + fileName);
            if (tempFile.exists()) {
                try {
                    Entry entry = mSozluk.getEntryFromUrl("file:///" + tempFile.getAbsolutePath(), mEntryNumber);
                    FileUtils.deleteQuietly(tempFile);
                    EntryManager manager = EntryManager.getManager(SavedEntryScreenActivity.this);
                    manager.saveEntry(entry);
                    mEntryList.add(entry);
                    return ReturnCodes.SUCCESS_DOWNLOAD;
                } catch (Exception e) {
                    return ReturnCodes.FILE_READ_ERROR;
                }
            } else {
                return ReturnCodes.MISSING_FILE;
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);

            if (result == ReturnCodes.SUCCESS_DOWNLOAD) {
                mCurrentIndex = mEntryList.size() - 1;
                updateViewPager();
                T.toast(SavedEntryScreenActivity.this, mEntryNumber + " numaralı entry kaydedildi.");
            } else if (result == ReturnCodes.PAGE_DOWNLOAD_FAILED) {
                T.toastLong(SavedEntryScreenActivity.this, mEntryNumber + " numaralı entry bulunamadı.");
            } else if (result == ReturnCodes.FILE_READ_ERROR) {
                T.toastLong(SavedEntryScreenActivity.this, "İndirilen dosya okunamadı. Lütfen tekrar deneyin.");
            } else if (result == ReturnCodes.MISSING_FILE) {
                T.toastLong(SavedEntryScreenActivity.this, "İnsan gerçekten hayret ediyor. İndirdiğim dosyayı bulamıyorum.");
            } else if (result == ReturnCodes.DOWNLOAD_CANCELLED) {
                T.toastLong(SavedEntryScreenActivity.this, "İşlem iptal edildi");
            }

            mHorizontalProgressDialog.dismiss();
        }

        @Override
        public void cancelButtonClicked() {
            mIsAsyncTaskCancelled = true;
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

        protected void updateProgressBarMessage(final String message) {
            SavedEntryScreenActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mHorizontalProgressDialog.setMessage(message);
                }
            });
        }
    }
}
