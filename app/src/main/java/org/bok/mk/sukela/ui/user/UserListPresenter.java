package org.bok.mk.sukela.ui.user;

import android.widget.Toast;

import org.bok.mk.sukela.R;
import org.bok.mk.sukela.data.model.User;
import org.bok.mk.sukela.data.model.pack.Pack;
import org.bok.mk.sukela.data.model.pack.eksi.UserPack;
import org.bok.mk.sukela.data.source.UserDataSource;
import org.bok.mk.sukela.data.source.UserRepo;
import org.bok.mk.sukela.ui.base.BasePresenter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserListPresenter extends BasePresenter<UserListContract.View> implements UserListContract.Presenter, UserDataSource.LoadUsersCallback, UserDataSource.DownloadUserCallback, UserDataSource.UserDeleteCallback
{
    private final UserRepo mUserRepo;

    private final List<User> mCurrentUsers = new ArrayList<>();

    UserListPresenter(UserRepo userRepo) {
        mUserRepo = userRepo;
    }

    @Override
    public void fabAddClicked() {
        mView.displayAddUserDialog();
    }

    @Override
    public void uiLoadCompleted() {
        mView.setActivityTitle(R.string.yazarlar);
        fillUserList();
    }

    @Override
    public void rowClicked(String username) {
        Pack pack = new UserPack(username);
        mView.startEntryScreenActivity(pack);
    }

    @Override
    public void rowLongClicked(String username) {
        mView.displayDeleteWarning(username);
    }

    @Override
    public void addUserConfirmed(String username) {
        mView.dismissAlertDialog();
        addUser(username.trim());
    }

    private void addUser(final String username) {
        boolean alreadyAdded = checkIfUserExists(username);
        if (alreadyAdded) {
            mView.toast("Bu yazar zaten eklenmiş.", Toast.LENGTH_SHORT);
            return;
        }

        ExecutorService e = Executors.newSingleThreadExecutor();
        e.execute(() -> mUserRepo.addUser(username, UserListPresenter.this));
        e.shutdown();
    }

    private boolean checkIfUserExists(String username) {
        for (User u : mCurrentUsers)
            if (u.getUserName().equals(username)) { return true; }
        return false;
    }

    @Override
    public void addUserCancelled() {
        mView.dismissAlertDialog();
    }

    private void fillUserList() {
        ExecutorService e = Executors.newSingleThreadExecutor();
        e.execute(() -> mUserRepo.getUsers(UserListPresenter.this));
        e.shutdown();
    }

    @Override
    public void progressDialogCancelButtonClicked(final String username) {
        mView.dismissProgressDialog();
        mUserRepo.cancel(username);
    }

    @Override
    public void deleteYesClicked(final String username) {
        ExecutorService e = Executors.newSingleThreadExecutor();
        e.execute(() -> mUserRepo.deleteUser(username, UserListPresenter.this));
        e.shutdown();
    }

    @Override
    public void deleteCancelClicked() {
        mView.dismissAlertDialog();
    }

    //
    // LoadUsersCallback
    //
    @Override
    public void onUsersLoaded(List<User> users) {
        if (UserListPresenter.this.isViewAttached()) {
            mView.hideNoUserMessage();
            mCurrentUsers.clear();
            mCurrentUsers.addAll(users);
            mView.initAdapter(mCurrentUsers);
            mView.dismissProgressDialog();
        }
    }

    @Override
    public void onDataNotAvailable(String message) {
        mView.displayNoUserMessage();
        mView.initAdapter(mCurrentUsers);
        mView.toast(message, Toast.LENGTH_SHORT);
        mView.dismissProgressDialog();
    }

    @Override
    public void onDataLoadStart() {
        mView.showSpinnerProgressDialog(R.string.reading_users);
    }

    //
    // DownloadUserCallback
    //
    @Override
    public void onUserEntriesLoaded(String username) {
        mView.dismissProgressDialog();
        mView.hideNoUserMessage();
        User newUser = new User(username);
        mCurrentUsers.add(newUser);
        Collections.sort(mCurrentUsers, (u1, u2) -> u1.getUserName().compareTo(u2.getUserName()));
        int index = mCurrentUsers.indexOf(newUser);
        //mView.deleteUserFromAdapter(index, mCurrentUsers);

        mView.addAndScroll(newUser, index);
        mView.toast("Kullanıcı kaydedildi.", Toast.LENGTH_SHORT);
    }

    @Override
    public void onUserNotAvailable(String message) {
        mView.toast(message, Toast.LENGTH_SHORT);
        mView.dismissProgressDialog();
    }

    @Override
    public void onMessageUpdate(String message) {
        mView.updateProgressMessage(message);
    }

    @Override
    public void onProgressUpdate(int progress) {
        mView.updateProgress(progress);
    }

    @Override
    public void onUserDownloadLoadStart(final String username) {
        mView.showHorizontalProgressDialog(R.string.searching_user, username);
    }

    @Override
    public void onRemoteError(String error) {
        mView.dismissProgressDialog();
        mView.toast(error, Toast.LENGTH_SHORT);
    }

    @Override
    public boolean checkIfOnline() {
        return mView.checkIfOnline();
    }

    // DeleteUserCallback
    @Override
    public void onUserDelete(String username, int entryCount) {
        for (int i = 0; i < mCurrentUsers.size(); i++) {
            if (mCurrentUsers.get(i).getUserName().equals(username)) {
                handleUserDelete(i, username, entryCount);
                break;
            }
        }
    }

    private void handleUserDelete(int deletedIndex, String username, int entryCount) {
        mCurrentUsers.remove(deletedIndex);
        mView.deleteUserFromAdapter(deletedIndex);
        if (mCurrentUsers.isEmpty()) { mView.displayNoUserMessage(); }
        mView.toast(username + " ve " + entryCount + " adet entrisi silindi.", Toast.LENGTH_SHORT);
    }
}
