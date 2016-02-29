package com.pabhinav.zovido;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gdata.util.ServiceException;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;

import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import static jxl.Workbook.createWorkbook;

public class CallDetails extends BaseDrawerActivity implements SavedLogTab.OnSavedLogsEventChangeListener {

    public static String AGENT_NAME = "";
    private boolean toggleUpload;
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

    /** Upload fab clicked **/
    public void uploadClicked(View v) throws MalformedURLException, IOException, ServiceException {

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
                        for (int j = 0; j<9; j++){
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

        AsyncTaskForListFeedInSpreadsheet asyncTaskForListFeedInSpreadsheet = new AsyncTaskForListFeedInSpreadsheet(
                this,
                getWorkSheetName(),
                getFileSheetKey(),
                getGoogleCredential(),
                SavedLogTab.savedLogRecyclerViewAdapter.getAllItems()
        );
        asyncTaskForListFeedInSpreadsheet.execute();


        /** Show initialization effect **/
        findViewById(R.id.initializing_progress_bar).setVisibility(View.VISIBLE);
        findViewById(R.id.fake_background_effect).setVisibility(View.VISIBLE);
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
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Constants.feedbackActivityRequestCode && resultCode == Constants.feedbackActivityRequestCode ) {

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
        } else if(dataParcelArrayList != null && dataParcelArrayList.size() == 0){
            CallLogTab.clearTicks();
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
        intent.putExtra(Constants.customForm, true);
        startActivityForResult(intent, Constants.feedbackActivityRequestCode);
    }
}
