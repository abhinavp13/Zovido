package com.pabhinav.zovido;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.MetadataChangeSet;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;

import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import static jxl.Workbook.createWorkbook;

public class CallDetails extends BaseDrawerActivity implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener, SavedLogTab.OnSavedLogsEventChangeListener {

    public static String AGENT_NAME = "";
    private static final int REQUEST_CODE_CREATOR = 2;
    private static final int REQUEST_CODE_RESOLUTION = 3;
    private boolean toggleUpload;
    private GoogleApiClient mGoogleApiClient;
    private boolean showDialogOnConnectionComplete;
    private Label[][] labels;
    private ArrayList<DataParcel> cachedDataForCustomEntries;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /** Instead of setting content view, call drawer content view **/
        setDrawerContentView(R.layout.activity_call_details, R.id.drawer_toggler);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();

            /** The 'if' case which should not happen :) **/
            if(extras == null) {
                AGENT_NAME = null;
            } else {
                AGENT_NAME = extras.getString(Constants.agentName);
            }
        } else {
            AGENT_NAME = (String) savedInstanceState.getSerializable(Constants.agentName);
        }

        if(AGENT_NAME != null && AGENT_NAME.length() > 0){

            /** Write to a file in private mode **/
            SharedPreferences sharedPref = getSharedPreferences(Constants.mypreferences, Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(Constants.agentName, AGENT_NAME);
            editor.apply();

            TextView agentNameTextView = (TextView) findViewById(R.id.agent_name_text_view);
            agentNameTextView.setText(AGENT_NAME);
        }

        toggleUpload = false;
        TextView savedCounterTextView = (TextView) findViewById(R.id.saved_count_text_view);
        TextView recentCounterTextView = (TextView) findViewById(R.id.recent_count_text_view);
        savedCounterTextView.setText("--");
        recentCounterTextView.setText("--");

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Recent Logs"));
        tabLayout.addTab(tabLayout.newTab().setText("Saved Logs"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        PagerAdapter viewPagerAdapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        findViewById(R.id.saved_item_addition_icon).setVisibility(View.GONE);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    findViewById(R.id.saved_item_addition_icon).setVisibility(View.GONE);
                } else {
                    findViewById(R.id.saved_item_addition_icon).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void feedbackClicked(View v) {

        /**
         * Linked with firebase for receiving feedback.
         * If there is no internet connectivity, then
         * feedback is sent whenever internet connection
         * comes back again. NetworkChangeRecevier does
         * this work.
         */
        LayoutInflater layoutInflater = LayoutInflater.from(CallDetails.this);
        View promptView = layoutInflater.inflate(R.layout.feedback_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CallDetails.this);
        alertDialogBuilder.setView(promptView).setCancelable(false);
        final AlertDialog alert = alertDialogBuilder.create();
        alert.show();

        /** Get feedback edit text **/
        final EditText editText = (EditText) promptView.findViewById(R.id.feedback_edit_text);

        /** send pressed by user **/
        TextView sendButton = (TextView) promptView.findViewById(R.id.send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText != null && editText.getText().toString().length() > 0) {
                    final String feedback = editText.getText().toString();
                    FirebaseHelper firebaseHelper = new FirebaseHelper(CallDetails.this);
                    firebaseHelper.sendFeedback(feedback);
                    Toast.makeText(CallDetails.this, "Thanks for your response !", Toast.LENGTH_LONG).show();
                }
                alert.cancel();
            }
        });

        /** cancel pressed by user **/
        TextView cancelButton = (TextView) promptView.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.cancel();
            }
        });
    }

    @Override
    public void changeAccountClicked(View v) {

        /** Need to initialize google instance synchronously **/
        initializeGoogleApiClient();
        mGoogleApiClient.clearDefaultAccountAndReconnect();
    }

    @Override
    public void aboutClicked(View v) {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    @Override
    public void agentNameChangeClicked(View v) {

        View rootView = getLayoutInflater().inflate(R.layout.new_agent_name, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(CallDetails.this);
        builder.setView(rootView);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        final EditText agentEditText = (EditText)rootView.findViewById(R.id.input_agent_name_dialog);
        rootView.findViewById(R.id.change_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (agentEditText != null && agentEditText.getText() != null && agentEditText.getText().toString().length() > 0) {

                    AGENT_NAME = agentEditText.getText().toString();

                    /** Write to a file in private mode **/
                    SharedPreferences sharedPref = getSharedPreferences(Constants.mypreferences, Context.MODE_PRIVATE);

                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(Constants.agentName, AGENT_NAME);
                    editor.apply();

                    TextView agentNameTextView = (TextView) findViewById(R.id.agent_name_text_view);
                    agentNameTextView.setText(AGENT_NAME);

                } else {
                    Toast.makeText(CallDetails.this, "Your Name cannot be empty !", Toast.LENGTH_LONG).show();
                }

                alertDialog.cancel();
            }
        });

        rootView.findViewById(R.id.cancel_button_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

        /** Need to initialize google instance synchronously **/
        initializeGoogleApiClient();

        // Connect the client.
        mGoogleApiClient.connect();

        showDialogOnConnectionComplete = false;
    }

    /** Only one instance of google api client must be created **/
    private synchronized void initializeGoogleApiClient(){

        if (mGoogleApiClient == null) {
            // Create the API client and bind it to an instance variable.
            // We use this instance as the callback for connection and connection
            // failures.
            // Since no account name is passed, the user is prompted to choose.
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
    }

    @Override
    protected void onPause() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onPause();
    }

    /** Upload fab clicked **/
    public void uploadClicked(View v){

        SavedLogRecyclerViewAdapter savedLogRecyclerViewAdapter = SavedLogTab.savedLogRecyclerViewAdapter;
        if(savedLogRecyclerViewAdapter != null && savedLogRecyclerViewAdapter.getItemCount() == 0){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View rootView = getLayoutInflater().inflate(R.layout.no_saved_logs, null);
            builder.setView(rootView);
            builder.setCancelable(false);
            final AlertDialog alert = builder.create();
            alert.show();
            rootView.findViewById(R.id.button_okay).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert.cancel();
                }
            });

            return;
        }

        toggleFabMenuitems();
    }


    /** Saves data to local storage **/
    public void localExportClicked(View v){

        toggleFabMenuitems();
        if(SavedLogTab.savedLogRecyclerViewAdapter == null || SavedLogTab.savedLogRecyclerViewAdapter.getItemCount() == 0){
            return;
        }
        /** Create Excel data **/
        CreateExcelCellFromDataParcel createExcelCellFromDataParcel = new CreateExcelCellFromDataParcel(SavedLogTab.savedLogRecyclerViewAdapter.getAllItems());
        Label[][] labels = null;
        try {
            labels = createExcelCellFromDataParcel.createExcel();
        } catch (Exception e){
            /** Could not create excel **/
            Toast.makeText(CallDetails.this, "Operation not possible", Toast.LENGTH_LONG).show();
            return;
        }

        /** Choose File explorer for saving excel to local storage **/
        final Label[][] finalLabels = labels;
        DirectoryChoserDialog directoryChooserDialog = new DirectoryChoserDialog(CallDetails.this, new DirectoryChoserDialog.ChosenDirectoryListener() {
            @Override
            public void onChosenDir(String chosenDir) {
                long timeInMillis = System.currentTimeMillis();
                Date date = new Date(timeInMillis);
                File file = new File(chosenDir, "Zovido-"+ date + ".xls");
                try {
                    WritableWorkbook wb = createWorkbook(file);
                    WritableSheet writableSheet = wb.createSheet("Saved Calls Details", 0);
                    for(int i = 0; i< SavedLogTab.savedLogRecyclerViewAdapter.getItemCount() + 1; i++){
                        for (int j = 0; j<8; j++){
                            writableSheet.addCell(finalLabels[j][i]);
                        }
                    }
                    wb.write();
                    wb.close();

                    /** Successfully written **/
                    Toast.makeText(CallDetails.this, "Written Successfully", Toast.LENGTH_LONG).show();

                    askForKeepOrDeleteSavedLogs();

                } catch (Exception e){

                    /** Unable to write **/
                    Toast.makeText(CallDetails.this, "Unable to write in the directory specified", Toast.LENGTH_LONG).show();
                }
            }
        });

        /** Set new folder button **/
        directoryChooserDialog.setNewFolderEnabled(true);
        /** Set selected folder as sdcard directory **/
        directoryChooserDialog.chooseDirectory("");
    }

    /** Saves data to drive **/
    public void cloudExportClicked(View v){

        toggleFabMenuitems();

        /** Create Excel data **/
        CreateExcelCellFromDataParcel createExcelCellFromDataParcel = new CreateExcelCellFromDataParcel(SavedLogTab.savedLogRecyclerViewAdapter.getAllItems());
        try {
            labels = createExcelCellFromDataParcel.createExcel();
        } catch (Exception e){
            /** Could not create excel **/
            Toast.makeText(CallDetails.this, "Operation not possible", Toast.LENGTH_LONG).show();
            return;
        }

        /** Chose google account and drive folder to export **/
        if(mGoogleApiClient.isConnected()){
            saveFileToDrive(labels);

        } else if(mGoogleApiClient.isConnecting()){

            Toast.makeText(CallDetails.this, "Connecting to Google Drive", Toast.LENGTH_LONG).show();

            /** Show dialog on connection complete **/
            showDialogOnConnectionComplete = true;

        } else {

            Toast.makeText(CallDetails.this, "Connecting to Google Drive", Toast.LENGTH_LONG).show();

            /** Try to connect again, and show dialog on connection complete **/
            mGoogleApiClient.connect();

            /** Show dialog on connection complete **/
            showDialogOnConnectionComplete = true;
        }
    }

    /** Open/close fab menu **/
    public void toggleFabMenuitems(){

        View fakeBackgroundEffect = (View) findViewById(R.id.fake_background_effect);
        FloatingActionButton fabUpload = (FloatingActionButton) findViewById(R.id.fab_upload);
        FloatingActionButton localDataStorageFab = (FloatingActionButton) findViewById(R.id.export_to_local_storage);
        FloatingActionButton cloudDataStorageFab = (FloatingActionButton) findViewById(R.id.export_to_cloud_storage);
        TextView exportToDriveText = (TextView) findViewById(R.id.text_for_export_to_drive);
        TextView exportToLocalText = (TextView) findViewById(R.id.text_for_export_to_local_storage);

        if(!toggleUpload){
            fabUpload.setImageDrawable(getResources().getDrawable(R.drawable.ic_clear_white_24dp));
            fakeBackgroundEffect.setVisibility(View.VISIBLE);
            localDataStorageFab.setVisibility(View.VISIBLE);
            cloudDataStorageFab.setVisibility(View.VISIBLE);
            exportToDriveText.setVisibility(View.VISIBLE);
            exportToLocalText.setVisibility(View.VISIBLE);

        } else {
            fabUpload.setImageDrawable(getResources().getDrawable(R.drawable.ic_file_upload_white_24dp));
            fakeBackgroundEffect.setVisibility(View.GONE);
            localDataStorageFab.setVisibility(View.GONE);
            cloudDataStorageFab.setVisibility(View.GONE);
            exportToDriveText.setVisibility(View.GONE);
            exportToLocalText.setVisibility(View.GONE);
        }

        toggleUpload = !toggleUpload;
    }


    @Override
    public void onConnected(Bundle bundle) {

        if(showDialogOnConnectionComplete){
            saveFileToDrive(labels);
            showDialogOnConnectionComplete = false;
        }

    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult result) {

        // Called whenever the API client fails to connect.
        Log.i("Zovido : ", "GoogleApiClient connection failed: " + result.toString());
        if (!result.hasResolution()) {
            // show the localized error dialog.
            GoogleApiAvailability.getInstance().getErrorDialog(this, result.getErrorCode(), 0).show();
            return;
        }
        // The failure has a resolution. Resolve it.
        // Called typically when the app is not yet authorized, and an
        // authorization
        // dialog is displayed to the user.
        try {
            result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
        } catch (IntentSender.SendIntentException e) {
            Log.e("Zovido : ", "Exception while starting resolution activity", e);
        }
    }

    /** Save to Drive **/
    private void saveFileToDrive(final Label[][] labels){

        Drive.DriveApi.newDriveContents(mGoogleApiClient).setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {

            @Override
            public void onResult(DriveApi.DriveContentsResult result) {
                // If the operation was not successful, we cannot do anything
                // and must
                // fail.
                if (!result.getStatus().isSuccess()) {
                    Toast.makeText(CallDetails.this, "Failed To Connect To Google Drive", Toast.LENGTH_LONG).show();
                    return;
                }

                // Get an output stream for the contents.
                OutputStream outputStream = result.getDriveContents().getOutputStream();

                try {
                    WritableWorkbook wb = createWorkbook(outputStream);
                    WritableSheet writableSheet = wb.createSheet("Saved Calls Details", 0);
                    for (int i = 0; i < SavedLogTab.savedLogRecyclerViewAdapter.getItemCount() + 1; i++) {
                        for (int j = 0; j < 8; j++) {
                            writableSheet.addCell(labels[j][i]);
                        }
                    }
                    wb.write();
                    wb.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Toast.makeText(CallDetails.this, "Failed To Create 'xls' File", Toast.LENGTH_LONG).show();
                    return;
                }

                long timeInMillis = System.currentTimeMillis();
                Date date = new Date(timeInMillis);
                MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
                        .setMimeType(MimeTypeMap.getSingleton().getMimeTypeFromExtension(".xls"))
                        .setTitle("Zovido-" + date + ".xls").build();

                // Create an intent for the file chooser, and start it.
                IntentSender intentSender = Drive.DriveApi
                        .newCreateFileActivityBuilder()
                        .setInitialMetadata(metadataChangeSet)
                        .setInitialDriveContents(result.getDriveContents())
                        .build(mGoogleApiClient);
                try {
                    startIntentSenderForResult(intentSender, REQUEST_CODE_CREATOR, null, 0, 0, 0);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                    Toast.makeText(CallDetails.this, "Failed To Initiate Google Drive File Creator", Toast.LENGTH_LONG).show();
                }
            }
        });

    }


    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CODE_CREATOR:

                // Called after a file is saved to Drive.
                if (resultCode == RESULT_OK) {

                    /** Successfully Saved to Drive **/
                    Toast.makeText(CallDetails.this, "Saved File to Drive. File will get synced.", Toast.LENGTH_LONG).show();
                    askForKeepOrDeleteSavedLogs();

                }
                break;

            case Constants.feedbackActivityRequestCode :

                DataParcel dataParcel = data.getExtras().getParcelable(Constants.dataPojo);
                if(dataParcel != null){

                    MyApplication myApplication = (MyApplication) getApplicationContext();

                    /** add to db **/
                    int id = myApplication.writeToDb(dataParcel);
                    dataParcel.setId(id);

                    /** There is a custom entry request **/
                    if(SavedLogTab.savedLogRecyclerViewAdapter != null) {
                        SavedLogTab.savedLogRecyclerViewAdapter.addItem(dataParcel, 0);
                        SavedLogTab.savedLogRecyclerViewAdapter.notifyDataSetChanged();
                    } else {

                        /** Caching data until saved logs appear **/
                        if(cachedDataForCustomEntries == null) {
                            cachedDataForCustomEntries = new ArrayList<>();
                        }

                        /** Add data parcel in cached log **/
                        cachedDataForCustomEntries.add(dataParcel);
                    }
                }
                break;
        }
    }

    /** Ask user to keep or delete saved logs **/
    private void askForKeepOrDeleteSavedLogs(){

        /** Ask :  Keep or remove exported data **/
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CallDetails.this);
        View rootView = getLayoutInflater().inflate(R.layout.keep_or_delete_dialog, null);
        alertDialogBuilder.setView(rootView);
        alertDialogBuilder.setCancelable(false);

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        rootView.findViewById(R.id.button_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MyApplication myApplication = (MyApplication) getApplicationContext();

                for (int i = 0; i < SavedLogTab.savedLogRecyclerViewAdapter.getItemCount(); i++) {

                    /** Delete from DB **/
                    myApplication.deleteFromDb(SavedLogTab.savedLogRecyclerViewAdapter.getItem(i));
                }
                /** Clear All records **/
                SavedLogTab.savedLogRecyclerViewAdapter.deleteAll();

                alertDialog.cancel();
            }
        });

        rootView.findViewById(R.id.button_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });

    }

    @Override
    public void onSavedLogsDataReadFromDB(ArrayList<DataParcel> dataParcelArrayList) {

        /** Tell Call Logs that update ticks **/
        if(dataParcelArrayList != null && dataParcelArrayList.size() >0) {
            CallLogTab.updateTick(dataParcelArrayList);
        }

        /** Custom entries log were cached its time to update **/
        if(cachedDataForCustomEntries != null){
            for(DataParcel dataParcel : cachedDataForCustomEntries) {
                SavedLogTab.savedLogRecyclerViewAdapter.addItem(dataParcel,0);
            }
            SavedLogTab.savedLogRecyclerViewAdapter.notifyDataSetChanged();
            cachedDataForCustomEntries = null;
        }
    }

    /** Custom Call Addition button clicked **/
    public void customCallAdditionClicked(View v){

        /** Call feedback handling activity **/
        final Intent intent = new Intent(this, Feedback.class);
        intent.putExtra(Constants.agentName, AGENT_NAME);
        startActivityForResult(intent, Constants.feedbackActivityRequestCode);
    }
}
