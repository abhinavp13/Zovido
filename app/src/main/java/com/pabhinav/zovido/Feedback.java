package com.pabhinav.zovido;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Feedback extends AppCompatActivity {

    /** ButterKnife initializations **/
    @Bind(R.id.input_name) MyEditText nameMainEditText;
    @Bind(R.id.input_agent_name) MyEditText agentNameMainEditText;
    @Bind(R.id.input_phone_no) MyEditText phoneNumberMainEditText;
    @Bind(R.id.input_call_time) MyEditText callTimeMainEditText;
    @Bind(R.id.input_call_duration) MyEditText callDurationMainEditText;
    @Bind(R.id.remarks_edit_text) MyEditText remarksMainEditText;
    @Bind(R.id.sport_main_text_view) TextView sportMainTextView;
    @Bind(R.id.product_main_text_view) TextView productMainTextView;
    @Bind(R.id.purpose_main_text_view) TextView purposeMainTextView;
    @Bind(R.id.input_sport_edit_text)  MyEditText sportsEditText;
    @Bind(R.id.input_product_edit_text)  MyEditText productEditText;
    @Bind(R.id.input_purpose_edit_text)  MyEditText purposeEditText;
    @Bind(R.id.shadow_product_linear_layout) View shadowProductLinearLayout;
    @Bind(R.id.product_linear_layout) LinearLayout productLinearLayout;
    @Bind(R.id.product_drop_down_icon) View productDropDownIcon;
    @Bind(R.id.shadow_purpose_linear_layout) View shadowPurposeLinearLayout;
    @Bind(R.id.purpose_linear_layout) LinearLayout purposeLinearLayout;
    @Bind(R.id.purpose_drop_down_icon) View purposeDropDownIcon;
    @Bind(R.id.shadow_sport_linear_layout) View shadowSportLinearLayout;
    @Bind(R.id.sport_linear_layout) LinearLayout sportLinearLayout;
    @Bind(R.id.sport_drop_down_icon) View sportDropDownIcon;

    /** Bundled items that comes with this activity's intent **/
    class BundleData{
        String name;
        String agentName;
        String timestamp;
        String callDuration;
        String userMobileNumber;
        String purpose;
        String product;
        String sport;
        String callRemarks;
        int position;
        boolean customForm;
    }

    /** Contains drop down states(open/close) for each drop-down menu **/
    class ToggleStatesForDropDown{
        boolean toggleForProduct;
        boolean toggleForPurpose;
        boolean toggleForSport;
        public ToggleStatesForDropDown(boolean toggleForPurpose, boolean toggleForProduct, boolean toggleForSport){
            this.toggleForPurpose = toggleForPurpose;
            this.toggleForProduct = toggleForProduct;
            this.toggleForSport = toggleForSport;
        }
    }

    /** Helper class for this activity **/
    FeedbackHelper feedbackHelper;

    /** Bundled data **/
    BundleData bundleData;

    /** Toggle states class instance **/
    ToggleStatesForDropDown toggleStatesForDropDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_feedback);

        /** Initialize ButterKnife **/
        ButterKnife.bind(this);

        /** This helped me resize and re-span when keyboard appears. **/
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        /** Helps fetch data from Bundle **/
        feedbackHelper = new FeedbackHelper(savedInstanceState, getIntent().getExtras());

        /** Close all drop down menus **/
        toggleStatesForDropDown = new ToggleStatesForDropDown(false,false,false);

        /** Confirms drop down states **/
        invalidateToggleForDropDowns();

        /** sports custom entry edit text **/
        sportsEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                sportMainTextView.setText(s.toString());
            }
        });

        /** product custom entry edit text **/
        productEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                productMainTextView.setText(s.toString());
            }
        });

        /** purpose custom entry edit text **/
        purposeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                purposeMainTextView.setText(s.toString());
            }
        });

        /** Initialize bundled data items and fetch their values **/
        bundleData = new BundleData();
        bundleData.name = feedbackHelper.getStringFromBundle(Constants.userName, "");
        bundleData.userMobileNumber = feedbackHelper.getStringFromBundle(Constants.phoneNumber, "");
        bundleData.agentName = feedbackHelper.getStringFromBundle(Constants.agentName, "");
        bundleData.callDuration = feedbackHelper.getStringFromBundle(Constants.callDuration, "");
        bundleData.timestamp = feedbackHelper.getStringFromBundle(Constants.callEndTime, "");
        bundleData.position = feedbackHelper.getIntFromBundle(Constants.position, -1);
        bundleData.purpose = feedbackHelper.getStringFromBundle(Constants.purpose, "");
        bundleData.product = feedbackHelper.getStringFromBundle(Constants.product, "");
        bundleData.sport = feedbackHelper.getStringFromBundle(Constants.sport, "");
        bundleData.callRemarks = feedbackHelper.getStringFromBundle(Constants.callRemarks, "");
        bundleData.customForm = feedbackHelper.getBooleanFromBundle(Constants.customForm, false);

        /** Need to do some actions on creation of views **/
        initialActions();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        final int keycode = event.getKeyCode();
        final int action = event.getAction();
        if (keycode == KeyEvent.KEYCODE_MENU && action == KeyEvent.ACTION_UP) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    /** Some initial actions need to be done. **/
    public void initialActions() {

        /** Not a custom form Custom form **/
        if(!bundleData.customForm){
            callDurationMainEditText.setText(bundleData.callDuration);
            callTimeMainEditText.setText(bundleData.timestamp);
            if(bundleData.name == null || bundleData.name.length() == 0){
                bundleData.name = Constants.unknown;
            }
            nameMainEditText.setText(bundleData.name);
            phoneNumberMainEditText.setText(bundleData.userMobileNumber);
            if(bundleData.purpose != null && bundleData.purpose.length() != 0){
                purposeMainTextView.setText(bundleData.purpose);
            }
            if(bundleData.product != null && bundleData.product.length() != 0){
                productMainTextView.setText(bundleData.product);
            }
            if(bundleData.sport != null && bundleData.sport.length() != 0){
                sportMainTextView.setText(bundleData.sport);
            }
            if(bundleData.callRemarks != null && bundleData.callRemarks.length() != 0){
                remarksMainEditText.setText(bundleData.callRemarks);
            }
        } else {
            /** Custom form **/
            callTimeMainEditText.setText("");
            callDurationMainEditText.setText("");
        }

        /** Agent Name **/
        agentNameMainEditText.setText(bundleData.agentName);
    }

    /** Save button clicked **/
    public void saveClicked(View v){

        /** Invalidate consistency with bundle data items **/
        invalidateBundleItems();

        passActivityResult();
        finish();
    }

    /** Dismiss should finish it **/
    public void dismissClicked(View v){
        finish();
    }

    /** Makes sure that bundle items are consistent with actual data entered by user in form. **/
    public void invalidateBundleItems(){
        bundleData.name = nameMainEditText.getText().toString();
        bundleData.agentName = agentNameMainEditText.getText().toString();
        bundleData.userMobileNumber = phoneNumberMainEditText.getText().toString();
        bundleData.timestamp = callTimeMainEditText.getText().toString();
        bundleData.callDuration = callDurationMainEditText.getText().toString();
        bundleData.callRemarks = remarksMainEditText.getText().toString();
        bundleData.sport = sportMainTextView.getText().toString();
        bundleData.product = productMainTextView.getText().toString();
        bundleData.purpose = purposeMainTextView.getText().toString();
    }

    /** Makes sure that toggle states are in consistent with actual UI drop down menus.
     * Need to be called after every toggle state change **/
    public void invalidateToggleForDropDowns(){

        /** Validate Purpose Menu **/
        if(toggleStatesForDropDown.toggleForPurpose){
            purposeLinearLayout.setVisibility(View.VISIBLE);
            shadowPurposeLinearLayout.setVisibility(View.VISIBLE);
            purposeDropDownIcon.setBackground(getResources().getDrawable(R.drawable.blue_arrow_inverted));
        } else {
            purposeLinearLayout.setVisibility(View.GONE);
            shadowPurposeLinearLayout.setVisibility(View.GONE);
            purposeDropDownIcon.setBackground(getResources().getDrawable(R.drawable.blue_arrow));
        }

        /** Validate Product Menu **/
        if(toggleStatesForDropDown.toggleForProduct){
            productLinearLayout.setVisibility(View.VISIBLE);
            shadowProductLinearLayout.setVisibility(View.VISIBLE);
            productDropDownIcon.setBackground(getResources().getDrawable(R.drawable.blue_arrow_inverted));
        } else {
            productLinearLayout.setVisibility(View.GONE);
            shadowProductLinearLayout.setVisibility(View.GONE);
            productDropDownIcon.setBackground(getResources().getDrawable(R.drawable.blue_arrow));
        }

        /** Validate Sport Menu **/
        if(toggleStatesForDropDown.toggleForSport){
            sportLinearLayout.setVisibility(View.VISIBLE);
            shadowSportLinearLayout.setVisibility(View.VISIBLE);
            sportDropDownIcon.setBackground(getResources().getDrawable(R.drawable.blue_arrow_inverted));
        } else {
            sportLinearLayout.setVisibility(View.GONE);
            shadowSportLinearLayout.setVisibility(View.GONE);
            sportDropDownIcon.setBackground(getResources().getDrawable(R.drawable.blue_arrow));
        }

    }

    /*******************************************************
     *********** Drop-down Menu Toggle click events ********
     *******************************************************/

    public void productToggleClicked(View v){
        toggleStatesForDropDown.toggleForProduct = !toggleStatesForDropDown.toggleForProduct;
        invalidateToggleForDropDowns();
    }

    public void purposeToggleClicked(View v){
        toggleStatesForDropDown.toggleForPurpose = !toggleStatesForDropDown.toggleForPurpose;
        invalidateToggleForDropDowns();
    }

    public void sportToggleClicked(View v){
        toggleStatesForDropDown.toggleForSport = !toggleStatesForDropDown.toggleForSport;
        invalidateToggleForDropDowns();
    }

    /*******************************************************
     *********** Sport menu item click event ***************
     *******************************************************/
    public void sportMenuItemClicked(View v){
        sportMainTextView.setText(((TextView) v).getText().toString());
        sportToggleClicked(null);
    }

    /*******************************************************
     *********** Product menu item click event *************
     *******************************************************/
    public void productMenuItemClicked(View v){
        productMainTextView.setText(((TextView)v).getText().toString());
        productToggleClicked(null);
    }

    /*******************************************************
     *********** Purpose menu item click event *************
     *******************************************************/
    public void purposeMenuItemClicked(View v){
        purposeMainTextView.setText(((TextView)v).getText().toString());
        purposeToggleClicked(null);
    }


    /** Prepares intent and passes the result **/
    private void passActivityResult(){

        Intent intent = new Intent();
        intent.putExtra(Constants.position,bundleData.position);
        intent.putExtra(Constants.customForm, bundleData.customForm);
        intent.putExtra(Constants.dataPojo, getDataPojo());
        setResult(Constants.feedbackActivityRequestCode, intent);
    }

    /** Fill in pojo **/
    private DataParcel getDataPojo(){

        DataParcel pojo = new DataParcel();
        pojo.setName(bundleData.name);
        pojo.setAgentName(bundleData.agentName);
        pojo.setTimestamp(bundleData.timestamp);
        pojo.setCallDuration(bundleData.callDuration);
        pojo.setUserMobileNumber(bundleData.userMobileNumber);
        pojo.setPurpose(bundleData.purpose);
        pojo.setProduct(bundleData.product);
        pojo.setSport(bundleData.sport);
        pojo.setCallRemarks(bundleData.callRemarks);

        return pojo;
    }
}
