package com.pabhinav.zovido;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

/**
 * @author pabhinav
 */
public abstract class BaseDrawerActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;

    private String fileSheetKey;
    private String workSheetName;
    private GoogleCredential googleCredential;

    String[] SCOPESArray = {
            "https://spreadsheets.google.com/feeds",
            "https://spreadsheets.google.com/feeds/spreadsheets/private/full",
            "https://docs.google.com/feeds"
    };

    final List SCOPES = Arrays.asList(SCOPESArray);

    /** Need to convert input stream into a File object **/
    private File getTempPkc12File() throws IOException {

        InputStream pkc12Stream = getAssets().open("pK.p12");
        File tempPkc12File = File.createTempFile("temp_pkc12_file", "p12");
        OutputStream tempFileStream = new FileOutputStream(tempPkc12File);

        int read = 0;
        byte[] bytes = new byte[1024];
        while ((read = pkc12Stream.read(bytes)) != -1) {
            tempFileStream.write(bytes, 0, read);
        }
        return tempPkc12File;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.base_activity_drawer_frame);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        /** Inflate drawer **/
        FrameLayout drawerFrame = (FrameLayout)findViewById(R.id.left_drawer);
        getLayoutInflater().inflate(R.layout.drawer_layout, drawerFrame, true);

        /** Need to fetch sheet file key, if not available, write to shared prefs **/
        fileSheetKey = loadFromPrefs(Constants.FILE_SHEET_KEY);
        if(fileSheetKey == null){
            fileSheetKey = BuildConfig.SPREADSHEETFILEKEY;
        }

        /** Worksheet name **/
        workSheetName = loadFromPrefs(Constants.WORKSHEET_KEY);
        if(workSheetName == null){
            workSheetName = BuildConfig.WORKSHEETNAME;
        }

        try {
            googleCredential = new GoogleCredential.Builder()
                    .setTransport(new NetHttpTransport())
                    .setJsonFactory(new JacksonFactory())
                    .setServiceAccountId("zovido-827@zovido-1219.iam.gserviceaccount.com")
                    .setServiceAccountScopes(SCOPES)
                    .setServiceAccountPrivateKeyFromP12File(getTempPkc12File())
                    .build();
        } catch (GeneralSecurityException | IOException e) {
            // Do Nothing ... will be handled later.
        }

        Log.d("Zovido : ","WorkSheet Name : " + getWorkSheetName());
        Log.d("Zovido : ","File Key Name " + getFileSheetKey());
    }

    /**
     * This method must be called by subclass for setting activity main
     * content.
     *
     * @param id  Layout resource id to be inflated as content view.
     * @param vId  View id used for toggling navigation drawer.
     */
    protected void setDrawerContentView(int id, int vId){

        /** Inflate main content layout **/
        FrameLayout frameLayout = (FrameLayout)findViewById(R.id.content_frame);
        View rootView = getLayoutInflater().inflate(id, frameLayout, true);

        /** Set Drawer toggler in the root view **/
        rootView.findViewById(vId).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    @Override
    public void onBackPressed() {

        /** close the drawer, if opened **/
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    /** Catch menu button presses and open drawer. **/
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            if(!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.openDrawer(GravityCompat.START);
            } else {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    public void navigationHeaderClicked(View v){
        // do nothing
    }

    /** Load String data from prefs **/
    private String loadFromPrefs(String key){
        String returnValue = null;
        SharedPreferences sharedPreferences = getSharedPreferences(key, Context.MODE_PRIVATE);
        if(sharedPreferences != null) {
            returnValue = sharedPreferences.getString(key, null);
        }
        return returnValue;
    }

    /** Saves String data to prefs **/
    private void saveToPrefs(String key, String value){

        SharedPreferences sharedPreferences = getSharedPreferences(key, Context.MODE_PRIVATE);
        if(sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if(editor!=null) {
                editor.remove(key).commit();
                editor.putString(key, value);
                editor.commit();
            }
        }
    }


    /** Getter and Setter for WorkSheet and File Key **/
    public void setWorkSheetName(String workSheetName){
        this.workSheetName = workSheetName;
        saveToPrefs(Constants.WORKSHEET_KEY, workSheetName);
    }

    public String getWorkSheetName(){
        return workSheetName;
    }

    public void setFileSheetKey(String fileSheetKey){
        this.fileSheetKey = fileSheetKey;
        saveToPrefs(Constants.FILE_SHEET_KEY, fileSheetKey);
    }

    public String getFileSheetKey(){
        return fileSheetKey;
    }


    public GoogleCredential getGoogleCredential(){
        return googleCredential;
    }



    /** Feedback clicked **/
    public abstract void feedbackClicked(View v);

    /** Need to change sheet file key and worksheet name **/
    public void changeAccountClicked(View v){

        Intent intent = new Intent(this, SpreadSheetSettingsActivity.class);
        startActivityForResult(intent, Constants.SPREADSHEET_REQUEST);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Constants.SPREADSHEET_REQUEST && resultCode == Constants.SPREADSHEET_REQUEST){
            String worksheetName = data.getExtras().getString(Constants.WORKSHEET_INTENT_KEY);
            String fileKey = data.getExtras().getString(Constants.FILE_KEY_INTENT_KEY);
            if(fileKey != null){
                setFileSheetKey(fileKey);
            }
            if(worksheetName != null){
                setWorkSheetName(worksheetName);
            }
        }
    }

    public abstract void aboutClicked(View v);

    public abstract void agentNameChangeClicked(View v);
}
