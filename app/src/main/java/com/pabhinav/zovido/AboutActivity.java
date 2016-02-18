package com.pabhinav.zovido;

import android.content.pm.PackageManager;
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
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setText("https://github.com/abhinavp13/Zovido");
    }

    public void backArrowPressed(View v){
        finish();
    }
}
