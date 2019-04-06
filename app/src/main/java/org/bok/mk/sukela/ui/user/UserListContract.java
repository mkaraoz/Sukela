package org.bok.mk.sukela.ui.user;

import org.bok.mk.sukela.data.model.User;
import org.bok.mk.sukela.data.model.pack.Pack;

import java.util.List;

interface UserListContract
{
    interface View
    {
        //
        // dialog pop ups - progress dialogs
        //
        void showSpinnerProgressDialog(int messageResId);

        void showHorizontalProgressDialog(int messageResID, final String username);

        void updateProgressMessage(String message);

        void updateProgress(int progress);

        void dismissProgressDialog();

        //
        // dialog pop ups - alert dialogs
        //
        void displayAddUserDialog();

        void displayDeleteWarning(String username);

        void dismissAlertDialog();

        //
        // adapter
        //
        void initAdapter(List<User> users);

        void deleteUserFromAdapter(int index);

        void addAndScroll(User newUser, int index);

        //
        // ui and helpers
        //
        void setActivityTitle(int titleResId);

        void displayNoUserMessage();

        void hideNoUserMessage();

        void startEntryScreenActivity(Pack pack);

        boolean checkIfOnline();

        void toast(String message, int length);

    }

    interface Presenter
    {
        void uiLoadCompleted();

        void fabAddClicked();

        void rowClicked(String username);

        void rowLongClicked(String username);

        void progressDialogCancelButtonClicked(final String username);

        void addUserConfirmed(String username);

        void addUserCancelled();

        void deleteYesClicked(String username);

        void deleteCancelClicked();
    }
}
