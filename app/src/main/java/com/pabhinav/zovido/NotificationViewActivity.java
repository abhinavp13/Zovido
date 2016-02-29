package com.pabhinav.zovido;

import android.app.NotificationManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class NotificationViewActivity extends AppCompatActivity {

    String title = "";
    String mssg = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /** stop ongoing notification **/
        stopForgroundServiceAndOngoingNotification();

        if(getIntent().getExtras() != null && getIntent().getExtras().getBoolean("DISMISS")) {
            finish();
            return;
        }

        if(getIntent().getExtras() != null) {
            title = getIntent().getExtras().getString("TITLE");
            mssg = getIntent().getExtras().getString("MESSAGE");
        }

        if(title == null || mssg == null){
            finish();
            return;
        }

        setContentView(R.layout.activity_notification_view);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        View rootView = getLayoutInflater().inflate(R.layout.spreadsheet_dialog, null);
        alertDialogBuilder.setView(rootView).setCancelable(false);

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        ((TextView)rootView.findViewById(R.id.title_alert_spreadsheet)).setText(title);
        ((TextView)rootView.findViewById(R.id.alert_message)).setText(mssg);
        rootView.findViewById(R.id.k_spreadsheet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
                finish();
            }
        });

    }

    private void stopForgroundServiceAndOngoingNotification(){

        /** Its very important to stop foreground service attached to ongoing notification, before killing it. **/
        if(UploadingService.uploadingService != null){
            UploadingService.uploadingService.stopForeground(true);
            UploadingService.uploadingService.stopSelf();
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }
}
