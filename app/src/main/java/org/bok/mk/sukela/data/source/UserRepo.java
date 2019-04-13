package org.bok.mk.sukela.data.source;

import android.support.annotation.NonNull;

import org.bok.mk.sukela.data.model.Contract;
import org.bok.mk.sukela.data.model.EntryList;
import org.bok.mk.sukela.data.model.User;
import org.bok.mk.sukela.data.model.pack.eksi.UserPack;
import org.bok.mk.sukela.data.source.db.UserDao;

import java.util.List;

public class UserRepo implements UserDataSource
{
    private static final String LOG_TAG = UserRepo.class.getSimpleName();

    private static UserRepo INSTANCE = null;
    private final EntryRepo mEntryRepo;
    private final UserDao mUserDao;

    private UserRepo(@NonNull UserDao userDao, EntryRepo entryRepo) {
        mUserDao = userDao;
        mEntryRepo = entryRepo;
    }

    public static UserRepo getInstance(@NonNull UserDao userDao, EntryRepo entryRepo) {
        if (INSTANCE == null) {
            synchronized (UserRepo.class) {
                if (INSTANCE == null) {
                    INSTANCE = new UserRepo(userDao, entryRepo);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void getUsers(LoadUsersCallback callback) {
        callback.onDataLoadStart();
        List<User> users = mUserDao.getUsers();
        if (users.isEmpty()) {
            callback.onDataNotAvailable("Veritabanında kayıtlı kullanıcı bulanamadı.");
        }
        else { callback.onUsersLoaded(users); }
    }

    @Override
    public void addUser(final String username, final DownloadUserCallback callback) {
        mEntryRepo.getEntries(UserPack.createTag(username), new EntryDataSource.LoadEntryListCallback()
        {
            // LOAD_ENTRY_CALLBACK
            @Override
            public void onEntriesLoaded(EntryList entries) {
                callback.onMessageUpdate("Entriler kaydediliyor...");
                mEntryRepo.saveEntries(entries);
                mUserDao.saveUser(new User(username));
                callback.onUserEntriesLoaded(username);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onUserNotAvailable("Laf aramızda bu yazar pek sevilmiyor.");
            }

            @Override
            public void onProgressUpdate(int progress) {
                callback.onProgressUpdate(progress);
            }

            @Override
            public void onMessageUpdate(String message) {
                callback.onMessageUpdate(message);
            }

            @Override
            public void onDataLoadStart(EntryDataSource.Provider provider) {
                callback.onUserDownloadLoadStart(username);
            }

            @Override
            public void onError(String error) {
                callback.onRemoteError(error);
            }

            @Override
            public boolean checkIfOnline() {
                return callback.checkIfOnline();
            }
        });
    }

    @Override
    public void deleteUser(String username, UserDeleteCallback callback) {
        mUserDao.deleteUser(username);
        int deletedEntryCount = mEntryRepo.deleteTag(Contract.TAG_USER + "_" + username);
        callback.onUserDelete(username, deletedEntryCount);
    }

    @Override
    public void cancel(String username) {
        mEntryRepo.cancel(UserPack.createTag(username));
    }

}
