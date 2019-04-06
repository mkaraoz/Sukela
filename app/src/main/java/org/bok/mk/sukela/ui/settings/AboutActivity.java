package org.bok.mk.sukela.ui.settings;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import org.bok.mk.sukela.BuildConfig;
import org.bok.mk.sukela.R;

public class AboutActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setContentView(R.layout.activity_about);

        TextView tvVersion = findViewById(R.id.version);
        tvVersion.setText("v"+BuildConfig.VERSION_NAME);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }
        return true;
    }
}
