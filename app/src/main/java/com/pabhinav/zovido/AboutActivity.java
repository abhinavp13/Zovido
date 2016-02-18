package com.pabhinav.zovido;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        try {
            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            TextView versionTextView = (TextView) findViewById(R.id.version_text_view);
            if(versionTextView != null){
                versionTextView.setText("App Version : v"+versionName);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        TextView textView = ((TextView) findViewById(R.id.textView5));
        textView.setText("https://github.com/abhinavp13/Zovido");
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = Uri.parse("https://github.com/abhinavp13/Zovido"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }

    public void backArrowPressed(View v){
        finish();
    }
}
