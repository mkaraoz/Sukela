package org.bok.mk.sukela.ui;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.bok.mk.sukela.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setTitle("Hakkında");

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = "v" + pInfo.versionName;
            TextView versionBox = (TextView) findViewById(R.id.version);
            versionBox.setText(version);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("_MK", "Paket adı okunamadı", e);
        }

    }
}
