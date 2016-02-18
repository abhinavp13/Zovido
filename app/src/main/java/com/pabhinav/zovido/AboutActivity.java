package com.pabhinav.zovido;

import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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





        /**TODO :  Github link  **/
        /** t2.setMovementMethod(LinkMovementMethod.getInstance()); **/






    }

    public void backArrowPressed(View v){
        finish();
    }
}
