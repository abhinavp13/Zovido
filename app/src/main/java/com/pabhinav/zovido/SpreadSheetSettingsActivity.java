package com.pabhinav.zovido;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author pabhinav
 */
public class SpreadSheetSettingsActivity extends AppCompatActivity {

    @Bind(R.id.sheet_file_key_heading) TextView fileKeyHeading;
    @Bind(R.id.worksheet_heading) TextView workSheetHeading;
    @Bind(R.id.input_sheet_file_key) EditText inputFileKey;
    @Bind(R.id.input_worksheet) EditText inputWorksheet;
    @Bind(R.id.sheet_file_key_underline) View fileKeyUnderline;
    @Bind(R.id.worksheet_underline) View worksheetUnderline;

    private String fileKey;
    private String workSheetName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spread_sheet_settings);

        ButterKnife.bind(this);


        inputFileKey.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                fileKey = s.toString();
                fileKeyHeading.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                fileKeyUnderline.setBackgroundColor(getResources().getColor(android.R.color.white));
            }
        });

        inputWorksheet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                workSheetName = s.toString();
                workSheetHeading.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                worksheetUnderline.setBackgroundColor(getResources().getColor(android.R.color.white));
            }
        });
    }

    /** Know more clicked **/
    public void knowMoreClicked(View v){

        try {
            Uri uri = Uri.parse("https://zovido.firebaseapp.com/");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }catch (Exception e){
            // do nothing.
        }
    }

    /** Save settings clicked **/
    public void saveSettingsClicked(View v){

        /** Validations **/
        if(workSheetName == null || workSheetName.length() == 0){
            workSheetHeading.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
            worksheetUnderline.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
            return;
        }

        if(fileKey == null || fileKey.length() == 0){
            fileKeyUnderline.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
            fileKeyHeading.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
            return;
        }

        passActivityResult();

        finish();
    }
    
    public void revertSettingsClicked(View v){

        fileKey  = BuildConfig.SPREADSHEETFILEKEY;
        workSheetName = BuildConfig.WORKSHEETNAME;
        inputFileKey.setText(fileKey);
        inputWorksheet.setText(workSheetName);

        Toast.makeText(SpreadSheetSettingsActivity.this, "Settings Reverted To Default File Key and WorkSheet Name", Toast.LENGTH_SHORT).show();

        passActivityResult();

        finish();
    }

    public void backClicked(View v){
        finish();
    }

    private void passActivityResult(){
        Intent intent = new Intent();
        intent.putExtra(Constants.FILE_KEY_INTENT_KEY, fileKey);
        intent.putExtra(Constants.WORKSHEET_INTENT_KEY, workSheetName);
        setResult(Constants.SPREADSHEET_REQUEST, intent);
    }
}
