package com.pabhinav.zovido;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetFeed;
import com.google.gdata.util.ServiceException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Plz do remember, service account needs to be made from developer console and then
 * .p12 format certificate file needs to be added in credentials for OAuth2.0
 * Also, remember your service account email address. Each spreadsheet accessed using below
 * method must have shared spreadsheet to this service account email id.
 *
 * @author pabhinav
 */
public class AsyncTaskForListFeedInSpreadsheet extends AsyncTask {

    private Context context;
    private String worksheetName;
    private String fileKey;
    private GoogleCredential googleCredential;
    public static ArrayList<DataParcel> dataParcels;
    public static SpreadsheetService service;
    public static URL listFeedUrl;

    /** public constructor for this class **/
    public AsyncTaskForListFeedInSpreadsheet(Context context, String worksheetName, String fileKey, GoogleCredential googleCredential, ArrayList<DataParcel> dataParcels){
        this.context = context;
        this.worksheetName = worksheetName;
        this.fileKey = fileKey;
        this.googleCredential = googleCredential;
        AsyncTaskForListFeedInSpreadsheet.dataParcels = dataParcels;
        if(AsyncTaskForListFeedInSpreadsheet.dataParcels == null) {
            AsyncTaskForListFeedInSpreadsheet.dataParcels = new ArrayList<>();
        }
    }

    String[] SCOPESArray = {
            "https://spreadsheets.google.com/feeds",
            "https://spreadsheets.google.com/feeds/spreadsheets/private/full",
            "https://docs.google.com/feeds"
    };

    final List SCOPES = Arrays.asList(SCOPESArray);

    /** Need to convert input stream into a File object **/
    private File getTempPkc12File() throws IOException {

        InputStream pkc12Stream = context.getAssets().open("pK.p12");
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
    protected Void doInBackground(Object[] params) {

        /** Requires OAuth2.0 **/
        if(googleCredential == null) {
            try {
                googleCredential = new GoogleCredential.Builder()
                        .setTransport(new NetHttpTransport())
                        .setJsonFactory(new JacksonFactory())
                        .setServiceAccountId("zovido-827@zovido-1219.iam.gserviceaccount.com")
                        .setServiceAccountScopes(SCOPES)
                        .setServiceAccountPrivateKeyFromP12File(getTempPkc12File())
                        .build();
            } catch (GeneralSecurityException | IOException e) {
                e.printStackTrace();
                Log.e("Zovido : ", "Exception caught while getting Google Credentials");

                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /** Unknown exception occurred try again **/
                        someProblemOccurredMessage();
                    }
                });


                return null;
            }
        }
        service = new SpreadsheetService("MySpreadsheetIntegration-v1");
        service.setOAuth2Credentials(googleCredential);

        URL SPREADSHEET_FEED_URL;
        try {
            SPREADSHEET_FEED_URL = new URL("https://spreadsheets.google.com/feeds/worksheets/"+ fileKey +"/private/full");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e("Zovido : ", "Exception caught while creating url, may file key wrong");


            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(!isNetworkAvailable()){
                        showAlertMessage("No Internet Connection", "Please make sure that you are connected to internet for uploading data to cloud. Try again later after connection is made.");
                        return;
                    }
                    showAlertMessage("Something Wrong With URL :(",Html.fromHtml("Seems like your sheet key is incorrect. Your configured key is : <u>" + fileKey + "</u>. Verify its correction (You can change it from app drawer), and try again"));
                }
            });
            return null;
        }

        WorksheetFeed worksheetFeed;
        try {
            worksheetFeed = service.getFeed(SPREADSHEET_FEED_URL, WorksheetFeed.class);
        } catch (IOException | ServiceException e) {
            e.printStackTrace();

            Log.e("Zovido : ", "Exception caught while getting worksheetFeed");

            /** Need to show alert Dialog **/
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    /** Wrong File Key **/
                    /** No File Permissions **/
                    someProblemOccurredMessage();

                }
            });
            return null;
        }

        List<WorksheetEntry> worksheets;
        if (worksheetFeed != null) {
            worksheets = worksheetFeed.getEntries();
        } else {
            Log.e("Zovido : ","Worksheet Feed null");


            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    /** Unknown exception occurred try again **/
                    someProblemOccurredMessage();
                }
            });

            return null;
        }

        /** Try to get the worksheet name **/
        boolean workSheetMatched = false;
        WorksheetEntry worksheet = null;
        if (worksheets != null) {
            for(WorksheetEntry worksheetEntry : worksheets){
                if(worksheetEntry.getTitle().getPlainText().equals(worksheetName)){
                    worksheet = worksheetEntry;
                    workSheetMatched = true;
                    break;
                }
            }
        } else {
            Log.e("Zovido : ","Worksheets null");


            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    /** Unknown exception occurred try again **/
                    someProblemOccurredMessage();
                }
            });

            return null;
        }
        if(!workSheetMatched){


            /** WorkSheet name problem **/
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showAlertMessage("Worksheet Name Not Found", Html.fromHtml("Seems like the worksheet name that you have configured is not available. Your configured worksheet name is : <u>" + worksheetName + "</u>. Verify its correction and then try again (You can always change it from app drawer)."));
                }
            });


            Log.e("Zovido : ","No Worksheet matched with worksheet name provided");
            return null;
        }

        if(worksheet == null){
            Log.e("Zovido : ","WorkSheet Null");

            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    /** Unknown exception occurred try again **/
                    someProblemOccurredMessage();
                }
            });

            return null;
        }

        /** Fetch the list feed of the worksheet. **/
        listFeedUrl = worksheet.getListFeedUrl();
        if(listFeedUrl == null){
            Log.e("Zovido : ", "Could not fetch list feed for the given worksheet");

            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    /** Unknown exception occurred try again **/
                    someProblemOccurredMessage();
                }
            });

            return null;
        }

        /** Initialization Complete, hide initialization **/
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((Activity) context).findViewById(R.id.initializing_progress_bar).setVisibility(View.GONE);
                ((Activity) context).findViewById(R.id.fake_background_effect).setVisibility(View.GONE);
                Toast.makeText(context, "Started Uploading (Saved Logs will get wiped out) ... ", Toast.LENGTH_LONG).show();
            }
        });

        /** Start service for inserting into spreadsheet **/
        Intent intent = new Intent(context, UploadingService.class);
        ((Activity) context).startService(intent);

        return null;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private void someProblemOccurredMessage(){

        /** No internet connection **/
        if(!isNetworkAvailable()){
            showAlertMessage("No Internet Connection","Please make sure that you are connected to internet for uploading data to cloud. Try again later after connection is made.");
            return;
        }

        /** Guide for error **/
        showAlertMessage("Some Problem Occurred :(", Html.fromHtml("Your configured sheet file key is : <u>" + fileKey + "</u> and Your configured worksheet name : <u>" + worksheetName + "</u>. <br/> Please verify that these are correct (You can change these values from the app drawer). <br/> Also, if you have not shared your sheet with Zovido, please make sure that you do share with <u>zovido-827@zovido-1219.iam.gserviceaccount.com</u>. Verify all these and then try again."));
    }

    private void showAlertMessage(String title, String mssg){
        showAlertMessage(title, Html.fromHtml(mssg));
    }

    private void showAlertMessage(String title, Spanned mssg){


        /** Stop Initializing Effect **/
        ((Activity) context).findViewById(R.id.initializing_progress_bar).setVisibility(View.GONE);
        ((Activity) context).findViewById(R.id.fake_background_effect).setVisibility(View.GONE);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        View rootView = ((Activity) context).getLayoutInflater().inflate(R.layout.spreadsheet_dialog, null);
        alertDialogBuilder.setView(rootView);

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        ((TextView)rootView.findViewById(R.id.title_alert_spreadsheet)).setText(title);
        ((TextView)rootView.findViewById(R.id.alert_message)).setText(mssg);
        rootView.findViewById(R.id.k_spreadsheet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });
    }
}
