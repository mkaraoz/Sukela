package org.bok.mk.sukela.data.source.repo;

import android.support.annotation.NonNull;
import android.util.Log;

import org.bok.mk.sukela.data.model.Contract;
import org.bok.mk.sukela.data.model.Entry;
import org.bok.mk.sukela.data.model.EntryList;
import org.bok.mk.sukela.data.model.SozlukEnum;
import org.bok.mk.sukela.data.model.pack.eksi.ArsivPack;
import org.bok.mk.sukela.data.source.EntryDataSource;
import org.bok.mk.sukela.data.source.sozluk.eksi.EksiFactory;
import org.bok.mk.sukela.data.source.sozluk.eksi.EksiSozluk;
import org.bok.mk.sukela.data.source.sozluk.eksi.data.EksiEntry;
import org.bok.mk.sukela.data.source.sozluk.instela.Instela;
import org.bok.mk.sukela.data.source.sozluk.instela.InstelaFactory;
import org.bok.mk.sukela.data.source.sozluk.instela.data.InstelaEntry;
import org.bok.mk.sukela.data.source.sozluk.sozlock.Sozlock;
import org.bok.mk.sukela.data.source.sozluk.sozlock.SozlockFactory;
import org.bok.mk.sukela.data.source.sozluk.sozlock.data.SozlockEntry;
import org.bok.mk.sukela.data.source.sozluk.uludag.Uludag;
import org.bok.mk.sukela.data.source.sozluk.uludag.UludagFactory;
import org.bok.mk.sukela.data.source.sozluk.uludag.data.UludagEntry;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RemoteEntryRepository implements EntryDataSource
{
    private static final String LOG_TAG = RemoteEntryRepository.class.getSimpleName();
    private volatile boolean isRunning = true;
    private volatile boolean tagCancelled = false;
    private static RemoteEntryRepository INSTANCE;

    // new listener overrides old listener
    private final Map<String, LoadEntryListCallback> callbackMap = new HashMap<>();
    private final Set<String> mProgressSet = new HashSet<>();

    private RemoteEntryRepository() {
    }

    public static RemoteEntryRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RemoteEntryRepository();
        }
        return INSTANCE;
    }

    @Override
    public int deleteTag(String tag) {
        // Cannot delete from remote repo
        return 0;
    }

    @Override
    public void saveEntries(EntryList entries) {
        // not needed for remote
    }

    @Override
    public void getEntry(SozlukEnum sozlukEnum, final int entryNo, @NonNull LoadEntryListCallback callback) {
        // no internet no entry
        if (!callback.checkIfOnline()) {
            callback.onError("İnternet bağlantısı yok.");
            return;
        }

        String tag = Contract.TAG_SAVE_FOR_GOOD;
        mProgressSet.add(tag);
        callbackMap.put(tag, callback);
        callback.onDataLoadStart(false);

        try {
            if (sozlukEnum == SozlukEnum.EKSI) {
                EksiSozluk eksi = EksiFactory.getInstance();
                new DummyProgressUpdater(callbackMap.get(tag)).start();
                EksiEntry entry = eksi.getEntryByNumber(entryNo);
                callbackMap.get(tag).onProgressUpdate(70);
                List<EksiEntry> list = new ArrayList<>();
                list.add(entry);
                EntryList entries = EntryList.fromEksiList(list, Contract.TAG_SAVE_FOR_GOOD);
                callbackMap.get(tag).onEntriesLoaded(entries);
                cleanUp(tag);
            }
            else if (sozlukEnum == SozlukEnum.INSTELA) {
                Instela instela = InstelaFactory.getInstance();
                new DummyProgressUpdater(callbackMap.get(tag)).start();
                InstelaEntry entry = instela.getEntryByNumber(entryNo);
                callbackMap.get(tag).onProgressUpdate(70);
                List<InstelaEntry> list = new ArrayList<>();
                list.add(entry);
                EntryList entries = EntryList.fromInstelaList(list, Contract.TAG_SAVE_FOR_GOOD);
                callbackMap.get(tag).onEntriesLoaded(entries);
                cleanUp(tag);
            }
            else if (sozlukEnum == SozlukEnum.ULUDAG) {
                Uludag uludag = UludagFactory.getInstance();
                new DummyProgressUpdater(callbackMap.get(tag)).start();
                UludagEntry entry = uludag.getEntryByNumber(entryNo);
                callbackMap.get(tag).onProgressUpdate(70);
                List<UludagEntry> list = new ArrayList<>();
                list.add(entry);
                EntryList entries = EntryList.fromUludagList(list, Contract.TAG_SAVE_FOR_GOOD);
                callbackMap.get(tag).onEntriesLoaded(entries);
                cleanUp(tag);
            }
            else {
                // unknown tag
                throw new RuntimeException("Unknown tag");
            }
        }
        catch (FileNotFoundException fne) {
            callbackMap.get(tag).onError("Laf aramızda bu yazar pek sevilmiyor.");
            callbackMap.put(tag, emptyCallback);
            mProgressSet.remove(tag);
            Log.e(LOG_TAG, fne.getMessage(), fne);
        }
        catch (IOException e) {
            callbackMap.get(tag).onError(e.getMessage());
            callbackMap.put(tag, emptyCallback);
            mProgressSet.remove(tag);
            Log.e(LOG_TAG, e.getMessage(), e);
        }
    }

    @Override
    public void getEntries(final String tag, @NonNull final LoadEntryListCallback callback) {

        // no internet no entry
        if (!callback.checkIfOnline()) {
            callback.onError("İnternet bağlantısı yok.");
            return;
        }

        // if this op is already running do not restart, use the existing one
        if (mProgressSet.contains(tag)) {
            callbackMap.put(tag, callback);
            callback.onDataLoadStart(false);
            return;
        }

        // mark progress as running
        mProgressSet.add(tag);
        callbackMap.put(tag, callback);

        callback.onDataLoadStart(false);

        try {
            if (tag.equals(Contract.TAG_EKSI_DEBE)) {
                downloadEksiDebe(tag);
            }
            else if (tag.startsWith("TAG_EKSI_20")) {
                downloadEksiYear(tag);
            }
            else if (tag.startsWith(Contract.TAG_USER)) {
                downloadUser(tag);
            }
            else if (tag.startsWith(Contract.TAG_ARCHIVE_DAY)) {
                downloadArsivDebe(tag);
            }
            else if (tag.equals(Contract.TAG_EKSI_WEEK)) {
                downloadEksiWeek(tag);
            }
            else if (tag.equals(Contract.TAG_EKSI_GUNDEM)) {
                downloadEksiGundem(tag);
            }
            else if (tag.equals(Contract.TAG_INSTELA_AY) || tag.equals(
                    Contract.TAG_INSTELA_HAFTA) || tag.equals(
                    Contract.TAG_INSTELA_YESTERDAY) || tag.equals(Contract.TAG_INSTELA_TOP_20)) {
                downloadInstelaEntries(tag);
            }
            else if (tag.equals(Contract.TAG_ULUDAG_HAFTA)) {
                downloadUludagHebe(tag);
            }
            else if (tag.equals(Contract.TAG_ULUDAG_YESTERDAY)) {
                downloadUludagDebe(tag);
            }
            else {
                // unknown tag
                throw new RuntimeException("Unknown tag");
            }
        }
        catch (FileNotFoundException fne) {
            callbackMap.get(tag).onError("Laf aramızda bu yazar pek sevilmiyor.");
            callbackMap.put(tag, emptyCallback);
            mProgressSet.remove(tag);
            Log.e(LOG_TAG, fne.getMessage(), fne);
        }
        catch (IOException e) {
            callbackMap.get(tag).onError(e.getMessage());
            callbackMap.put(tag, emptyCallback);
            mProgressSet.remove(tag);
            Log.e(LOG_TAG, e.getMessage(), e);
        }
    }

    @Override
    public void search(String query, boolean title, boolean entry, boolean user, @NonNull LoadEntryListCallback callback) {
        // only required for local repo
    }

    private void downloadUludagDebe(String tag) throws IOException {
        Uludag uludag = UludagFactory.getInstance();
        callbackMap.get(tag).onMessageUpdate("Entry listesi indiriliyor.");
        callbackMap.get(tag).onProgressUpdate(2);
        List<Integer> debeIds = uludag.getDebeIds();
        callbackMap.get(tag).onProgressUpdate(5);
        List<UludagEntry> debeList = new ArrayList<>();
        for (int i = 0; i < debeIds.size(); i++) {
            UludagEntry e = uludag.getEntryByNumber(debeIds.get(i));
            debeList.add(e);
            callbackMap.get(tag).onProgressUpdate(5 + (3 * (i + 2)));
            callbackMap.get(tag).onMessageUpdate(
                    "Entryler indiriliyor: " + (i + 1) + "/" + debeIds.size());
            if (tagCancelled) {
                break;
            }
        }
        EntryList entries = EntryList.fromUludagList(debeList, tag);
        callbackMap.get(tag).onEntriesLoaded(entries);
        cleanUp(tag);
    }

    private void downloadUludagHebe(String tag) throws IOException {
        Uludag uludag = UludagFactory.getInstance();
        callbackMap.get(tag).onMessageUpdate("Entry listesi indiriliyor.");
        callbackMap.get(tag).onProgressUpdate(2);
        List<Integer> hebeIds = uludag.getHebeIds();
        callbackMap.get(tag).onProgressUpdate(10);
        List<UludagEntry> hebeList = new ArrayList<>();
        for (int i = 0; i < hebeIds.size(); i++) {
            UludagEntry e = uludag.getEntryByNumber(hebeIds.get(i));
            hebeList.add(e);
            callbackMap.get(tag).onProgressUpdate(10 + (2 * (i + 2)));
            callbackMap.get(tag).onMessageUpdate(
                    "Entryler indiriliyor: " + (i + 1) + "/" + hebeIds.size());
            if (tagCancelled) {
                break;
            }
        }
        EntryList entries = EntryList.fromUludagList(hebeList, tag);
        callbackMap.get(tag).onEntriesLoaded(entries);
        cleanUp(tag);
    }

    private void downloadInstelaEntries(String tag) throws IOException {
        Instela instela = InstelaFactory.getInstance();
        new DummyProgressUpdater(callbackMap.get(tag)).start();
        List<InstelaEntry> instelaEntryList;
        switch (tag) {
            case Contract.TAG_INSTELA_AY:
                instelaEntryList = downloadInstelaMonth(instela);
                break;
            case Contract.TAG_INSTELA_HAFTA:
                instelaEntryList = downloadInstelaWeek(instela);
                break;
            case Contract.TAG_INSTELA_YESTERDAY:
                instelaEntryList = downloadInstelaDebe(instela);
                break;
            case Contract.TAG_INSTELA_TOP_20:
                instelaEntryList = downloadInstelaTop20(instela);
                break;
            default:
                throw new RuntimeException("Unknown tag");
        }
        callbackMap.get(tag).onProgressUpdate(70);
        EntryList entries = EntryList.fromInstelaList(instelaEntryList, tag);
        callbackMap.get(tag).onEntriesLoaded(entries);
        cleanUp(tag);
    }

    // TAG_INSTELA_TOP_20
    private List<InstelaEntry> downloadInstelaTop20(Instela instela) throws IOException {
        return instela.getTop20();
    }

    // TAG_INSTELA_YESTERDAY
    private List<InstelaEntry> downloadInstelaDebe(Instela instela) throws IOException {
        return instela.getBestOfYesterday();
    }

    // TAG_INSTELA_HAFTA
    private List<InstelaEntry> downloadInstelaWeek(Instela instela) throws IOException {
        return instela.getBestOfWeek();
    }

    // TAG_INSTELA_AY
    private List<InstelaEntry> downloadInstelaMonth(Instela instela) throws IOException {
        return instela.getBestOfMonth();
    }

    private void downloadSozlockEntries(String tag) throws IOException {
        Sozlock sozlock = SozlockFactory.getInstance();
        new DummyProgressUpdater(callbackMap.get(tag)).start();
        List<SozlockEntry> debe;
        if (tag.equals(Contract.TAG_EKSI_DEBE)) {
            debe = sozlock.getDebe();
        }
        else if (tag.startsWith(Contract.TAG_ARCHIVE_DAY)) {
            debe = sozlock.getDebeByDate(ArsivPack.getDateFromTag(tag));
        }
        else { throw new RuntimeException("Unknown tag"); }
        callbackMap.get(tag).onProgressUpdate(70);
        EntryList entries = EntryList.fromSozlockList(debe, tag);
        callbackMap.get(tag).onEntriesLoaded(entries);
        cleanUp(tag);
    }

    // ARSIV
    private void downloadArsivDebe(String tag) throws IOException {
        downloadSozlockEntries(tag);
    }

    // TAG_EKSI_DEBE
    private void downloadEksiDebe(String tag) throws IOException {
        downloadSozlockEntries(tag);
    }

    // TAG_EKSI_20**
    private void downloadEksiYear(String tag) throws IOException {
        EksiSozluk eksi = EksiFactory.getInstance();
        new DummyProgressUpdater(callbackMap.get(tag)).start();
        String year = tag.substring(tag.lastIndexOf('_') + 1);
        List<EksiEntry> bestOfYear = eksi.getBestOfYear(Integer.parseInt(year));
        callbackMap.get(tag).onProgressUpdate(70);
        EntryList entries = EntryList.fromEksiList(bestOfYear, tag);
        callbackMap.get(tag).onEntriesLoaded(entries);
        cleanUp(tag);
    }

    // TAG_EKSI_WEEK
    private void downloadEksiWeek(String tag) throws IOException {
        EksiSozluk eksi = EksiFactory.getInstance();
        callbackMap.get(tag).onMessageUpdate("Entry listesi indiriliyor.");
        callbackMap.get(tag).onProgressUpdate(2);
        List<Integer> hebeIds = eksi.getHebeIds();
        callbackMap.get(tag).onProgressUpdate(5);
        List<EksiEntry> hebeList = new ArrayList<>();
        for (int i = 0; i < hebeIds.size(); i++) {
            EksiEntry e = eksi.getEntryByNumber(hebeIds.get(i));
            hebeList.add(e);
            callbackMap.get(tag).onProgressUpdate(5 * (i + 2));
            callbackMap.get(tag).onMessageUpdate(
                    "Entryler indiriliyor: " + (i + 1) + "/" + hebeIds.size());
            if (tagCancelled) {
                break;
            }
        }
        EntryList entries = EntryList.fromEksiList(hebeList, tag);
        callbackMap.get(tag).onEntriesLoaded(entries);
        cleanUp(tag);
    }

    // TAG_EKSI_GUNDEM
    private void downloadEksiGundem(String tag) throws IOException {
        EksiSozluk eksi = EksiFactory.getInstance();
        callbackMap.get(tag).onMessageUpdate("Entry listesi indiriliyor.");
        callbackMap.get(tag).onProgressUpdate(1);
        List<EksiEntry> gundemList = new ArrayList<>();
        List<String> gundemTitles = eksi.getGundemTitles();
        for (int i = 0; i < gundemTitles.size(); i++) {
            EksiEntry e = eksi.getBestOfPage(gundemTitles.get(i));
            gundemList.add(e);
            callbackMap.get(tag).onProgressUpdate(2 * (i + 2));
            callbackMap.get(tag).onMessageUpdate(
                    "Entryler indiriliyor: " + (i + 1) + "/" + gundemTitles.size());
            if (tagCancelled) {
                break;
            }
        }
        EntryList entries = EntryList.fromEksiList(gundemList, tag);
        callbackMap.get(tag).onEntriesLoaded(entries);
        cleanUp(tag);
    }

    // TAG_USER
    private void downloadUser(String tag) throws IOException {
        EksiSozluk eksi = EksiFactory.getInstance();
        String username = tag.substring(9);
        callbackMap.get(tag).onMessageUpdate(username + " aranıyor...");
        callbackMap.get(tag).onProgressUpdate(2);
        List<Integer> entryNumbers = eksi.getUserBestOfIds(username);
        callbackMap.get(tag).onProgressUpdate(5);
        if (entryNumbers.isEmpty()) {
            callbackMap.get(tag).onDataNotAvailable();
            return;
        }

        List<EksiEntry> usersBestEntryList = new ArrayList<>();
        for (int i = 0; i < entryNumbers.size(); i++) {
            EksiEntry e = eksi.getEntryByNumber(entryNumbers.get(i));
            usersBestEntryList.add(e);
            callbackMap.get(tag).onProgressUpdate(2 * (i + 2));
            callbackMap.get(tag).onMessageUpdate(
                    "Entryler indiriliyor: " + (i + 1) + "/" + entryNumbers.size());
            if (tagCancelled) {
                break;
            }
        }

        EntryList entries = EntryList.fromEksiList(usersBestEntryList, tag);
        callbackMap.get(tag).onEntriesLoaded(entries);
        cleanUp(tag);
    }

    private void cleanUp(String tag) {
        isRunning = false;
        callbackMap.put(tag, emptyCallback);
        mProgressSet.remove(tag);
        tagCancelled = false; // required for multipage downloads
    }

    @Override
    public void cancel(String tag) {
        // multipage downloads must be included in here to support cancel feature
        if (tag.equals(Contract.TAG_EKSI_GUNDEM) || tag.equals(
                Contract.TAG_EKSI_WEEK) || tag.equals(
                Contract.TAG_ULUDAG_YESTERDAY) || tag.startsWith(Contract.TAG_USER) || tag.equals(
                Contract.TAG_ULUDAG_HAFTA)) {
            tagCancelled = true;
        }
        else { callbackMap.put(tag, emptyCallback); }
    }

    private class DummyProgressUpdater extends Thread
    {
        private final LoadEntryListCallback callback;
        private final int UPPER_LIMIT = 60;

        DummyProgressUpdater(final LoadEntryListCallback callback) {
            this.callback = callback;
        }

        private int progress = 0;

        @Override
        public void run() {
            isRunning = true;
            while (isRunning && progress < UPPER_LIMIT) {
                callback.onProgressUpdate(progress++);
                try {
                    Thread.sleep(150);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void refreshEntries(String tag, @NonNull LoadEntryListCallback callback) {
        // Not needed, logic implemented in EntryRepo
    }

    @Override
    public void deleteEntry(Entry e) {
        // not needed for remote repo
    }

    private static final LoadEntryListCallback emptyCallback = new LoadEntryListCallback()
    {
        @Override
        public void onEntriesLoaded(EntryList entries) {

        }

        @Override
        public void onDataNotAvailable() {

        }

        @Override
        public void onProgressUpdate(int progress) {

        }

        @Override
        public void onMessageUpdate(String message) {

        }

        @Override
        public void onDataLoadStart(boolean fromLocal) {

        }

        @Override
        public void onError(String error) {

        }

        @Override
        public boolean checkIfOnline() {
            return false;
        }
    };
}
