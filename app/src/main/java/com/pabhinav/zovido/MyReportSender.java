package com.pabhinav.zovido;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import org.acra.ACRAConstants;
import org.acra.ReportField;
import org.acra.collector.CrashReportData;
import org.acra.sender.ReportSender;
import org.acra.sender.ReportSenderException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author pabhinav
 */
class MyReportSender implements ReportSender {

    public MyReportSender(){
        // initialize your sender with needed parameters
    }

    @Override
    public void send(final Context context, CrashReportData errorContent) throws ReportSenderException {

        final Map<String, String> finalReport = remap(errorContent);

        /** send data for firebase to handle **/
        FirebaseHelper firebaseHelper = new FirebaseHelper(context);
        firebaseHelper.sendAcraMap(finalReport);
    }

    @NonNull
    private Map<String, String> remap(@NonNull Map<ReportField, String> report) {

        ReportField[] fields = ACRAConstants.DEFAULT_REPORT_FIELDS;

        final Map<String, String> finalReport = new HashMap<String, String>(report.size());
        for (ReportField field : fields) {
            if(field !=null && report.get(field) != null) {
                finalReport.put(field.toString(), report.get(field));
            }
        }
        return finalReport;
    }
}
