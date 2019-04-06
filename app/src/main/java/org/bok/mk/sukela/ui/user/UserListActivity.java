package org.bok.mk.sukela.ui.user;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.bok.mk.sukela.Injection;
import org.bok.mk.sukela.R;
import org.bok.mk.sukela.data.model.Contract;
import org.bok.mk.sukela.data.model.User;
import org.bok.mk.sukela.data.model.pack.Pack;
import org.bok.mk.sukela.ui.entryscreen.a.EntryScreenActivity;
import org.bok.mk.sukela.util.Keyboard;
import org.bok.mk.sukela.util.Network;

import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInRightAnimator;

public class UserListActivity extends AppCompatActivity implements UserListContract.View, UserListAdapter.UserRowClickListener
{
    private UserListPresenter mPresenter;
    private ProgressDialog mProgressDialog;
    private AlertDialog mAlertDialog;
    private TextView mWarningLabel;
    private UserListAdapter mAdapter;

    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        mPresenter = new UserListPresenter(Injection.provideUserRepository(getApplicationContext(),
                Injection.provideEntryRepository(getApplicationContext())));
        mPresenter.attachView(this);

        mWarningLabel = findViewById(R.id.no_user);
        createFabMenu();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mAdapter = new UserListAdapter(this);
        mRecyclerView = findViewById(R.id.rv);
        mRecyclerView.setItemAnimator(new SlideInRightAnimator());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mPresenter.uiLoadCompleted();
    }

    private void createFabMenu() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setBackgroundTintList(
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.bacground1_light)));
        fab.setRippleColor(ContextCompat.getColor(this, R.color.blue));
        fab.setOnClickListener(view -> mPresenter.fabAddClicked());
    }

    //
    // UserRowClickListener callback methods
    //
    @Override
    public void rowClicked(String username) {
        mPresenter.rowClicked(username);
    }

    @Override
    public void rowLongClicked(String data) {
        mPresenter.rowLongClicked(data);
    }

    //
    // dialog pop ups - progress dialogs
    //
    @Override
    public void showSpinnerProgressDialog(final int messageResId) {
        this.runOnUiThread(() -> {
            mProgressDialog = new ProgressDialog(UserListActivity.this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setMessage(getString(messageResId));
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.show();
        });
    }

    @Override
    public void showHorizontalProgressDialog(final int messageResID, final String username) {
        this.runOnUiThread(() -> {
            mProgressDialog = new ProgressDialog(UserListActivity.this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setMessage(getString(messageResID));
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
                    (dialog, which) -> mPresenter.progressDialogCancelButtonClicked(username));
            mProgressDialog.show();
        });
    }

    @Override
    public void updateProgressMessage(final String message) {
        this.runOnUiThread(() -> mProgressDialog.setMessage(message));
    }

    @Override
    public void updateProgress(final int progress) {
        this.runOnUiThread(() -> mProgressDialog.setProgress(progress));
    }

    @Override
    public void dismissProgressDialog() {
        this.runOnUiThread(() -> {
            if (mProgressDialog != null) { mProgressDialog.dismiss(); }
        });
    }

    //
    // dialog pop ups - alert dialogs
    //
    @Override
    public void displayAddUserDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserListActivity.this);
        builder.setTitle("Yazar Ekle");
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(50, 10, 50, 10);
        final EditText input = new EditText(this);
        input.setMaxLines(1);
        input.setLayoutParams(lp);
        container.addView(input, lp);
        builder.setView(container);
        builder.setPositiveButton("Tamam", (dialog, which) -> {
            Keyboard.hideSoftKeyboard(input, UserListActivity.this);
            String username = input.getText().toString();
            mPresenter.addUserConfirmed(username);
        });

        builder.setNegativeButton("İptal", (dialog, which) -> mPresenter.addUserCancelled());

        mAlertDialog = builder.create();
        mAlertDialog.show();

        Keyboard.showSoftKeyboard(UserListActivity.this);
    }

    @Override
    public void displayDeleteWarning(final String username) {
        this.runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(UserListActivity.this);
            builder.setTitle("Sil " + username);
            builder.setMessage("Yazarın tüm entrileri silinsin mi?");

            builder.setPositiveButton("Evet",
                    (dialog, which) -> mPresenter.deleteYesClicked(username));

            builder.setNegativeButton("Hayır", (dialog, which) -> mPresenter.deleteCancelClicked());
            mAlertDialog = builder.create();
            mAlertDialog.show();
        });
    }

    @Override
    public void dismissAlertDialog() {
        if (mAlertDialog != null) { mAlertDialog.dismiss(); }
    }

    //
    // adapter
    //
    @Override
    public void initAdapter(final List<User> users) {
        this.runOnUiThread(() -> {
            mAdapter.setData(users);
            mAdapter.notifyDataSetChanged();
            mRecyclerView.setAdapter(mAdapter);
        });
    }

    @Override
    public void deleteUserFromAdapter(final int index) {
        this.runOnUiThread(() -> mAdapter.remove(index));
    }

    @Override
    public void addAndScroll(final User newUser, final int index) {
        this.runOnUiThread(() -> {
            //mAdapter.notifyItemChanged(index);

            mAdapter.add(newUser, index);
            mRecyclerView.scrollToPosition(index);
        });
    }

    //
    // ui and helpers
    //
    @Override
    public void setActivityTitle(int titleResId) {
        this.setTitle(getString(titleResId));
    }

    @Override
    public void displayNoUserMessage() {
        this.runOnUiThread(() -> mWarningLabel.setVisibility(View.VISIBLE));
    }

    @Override
    public void hideNoUserMessage() {
        this.runOnUiThread(() -> mWarningLabel.setVisibility(View.GONE));
    }

    @Override
    public void startEntryScreenActivity(final Pack pack) {
        this.runOnUiThread(() -> {
            Intent i = new Intent(UserListActivity.this, EntryScreenActivity.class);
            i.putExtra(Contract.PACK, pack);
            startActivity(i);
        });
    }

    @Override
    public void toast(final String message, final int length) {
        this.runOnUiThread(() -> Toast.makeText(UserListActivity.this, message, length).show());
    }

    @Override
    public boolean checkIfOnline() {
        return Network.isNetworkAvailable(UserListActivity.this);
    }
}
