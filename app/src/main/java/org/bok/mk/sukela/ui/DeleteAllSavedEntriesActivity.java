package org.bok.mk.sukela.ui;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.bok.mk.sukela.R;
import org.bok.mk.sukela.data.LocalDbManager;
import org.bok.mk.sukela.helper.T;

public class DeleteAllSavedEntriesActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setTheme(R.style.DeleteAllTheme);
        setContentView(R.layout.activity_delete_all_saved_entries);
        setTitle("Sakladığın tüm entriler silinsin mi?");

        Button delete = (Button) findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                LocalDbManager manager = new LocalDbManager(DeleteAllSavedEntriesActivity.this);
                manager.deleteAllUserEntries();
                T.toast(DeleteAllSavedEntriesActivity.this, "Saklanılan tüm entriler silindi");
                finish();
            }
        });

        Button cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}
