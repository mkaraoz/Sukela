package org.bok.mk.sukela.ui.settings;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import org.bok.mk.sukela.Injection;
import org.bok.mk.sukela.R;
import org.bok.mk.sukela.data.model.Contract;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DeleteSavedEntriesActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_saved_entries);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setTitle(getString(R.string.delete_all_saved_entries));

        Button delete = findViewById(R.id.delete);
        delete.setOnClickListener(view -> {

            ExecutorService e = Executors.newSingleThreadExecutor();
            e.execute(() -> {
                Injection.provideEntryRepository(DeleteSavedEntriesActivity.this).deleteTag(
                        Contract.TAG_SAVE_FOR_GOOD);
                Injection.provideEntryRepository(DeleteSavedEntriesActivity.this).deleteTag(
                        Contract.TAG_SAVE_FOR_LATER);
            });
            e.shutdown();
            Toast.makeText(DeleteSavedEntriesActivity.this, "Saklanılan tüm entriler silindi",
                    Toast.LENGTH_SHORT).show();
            finish();

        });

        Button cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(view -> finish());
    }
}
