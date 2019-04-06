package org.bok.mk.sukela.data.source;

import org.bok.mk.sukela.data.model.User;

import java.util.List;

public interface UserDataSource
{
    interface LoadUsersCallback
    {
        void onUsersLoaded(List<User> users);

        void onDataNotAvailable(String message);

        void onDataLoadStart();

    }

    interface DownloadUserCallback
    {
        void onUserEntriesLoaded(String username);

        void onUserNotAvailable(String message);

        void onMessageUpdate(String message);

        void onProgressUpdate(int progress);

        void onUserDownloadLoadStart(String username);

        void onRemoteError(String error);

        boolean checkIfOnline();
    }

    interface UserDeleteCallback
    {
        void onUserDelete(String username, int entryCount);
    }

    void getUsers(LoadUsersCallback callback);

    void addUser(String username, final DownloadUserCallback callback);

    void deleteUser(String username, UserDeleteCallback callback);

    void cancel(String username);
}
