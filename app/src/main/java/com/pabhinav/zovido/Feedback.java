package com.pabhinav.zovido;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ScrollView;
import android.widget.TextView;

public class Feedback extends AppCompatActivity {

    private String phoneNumber;
    private String callEndTime;
    private String callDuration;
    private String userName;
    private String agentName;
    private String callRemarks;
    private String userType;
    private String productType;
    private boolean toggleUserType = false;
    private boolean toggleProductType = false;

    private View userBackgroundView;
    private TextView existingUserTextView;
    private TextView newUserTextView;
    private MyEditText customUserTypeEditText;
    private View customUserTypeUnderline;
    private View userTypeRotateIcon;
    private View productBackgroundView;
    private TextView existingProductTextView;
    private TextView newProductTextView;
    private MyEditText customProductTypeEditText;
    private View customProductTypeUnderline;
    private View productTypeRotateIcon;
    private TextView dummyTimeStamp;
    private View dummyViewCoveringBothDropDown;
    private TextView inputUserType;
    private TextView inputProductType;
    private MyEditText inputName;
    private MyEditText inputPhoneNumber;
    private MyEditText inputAgentName;
    private TextView inputTimeStamp;
    private TextView inputCallDuration;
    private MyEditText remarksEditText;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();

            /** The 'if' case which should not happen :) **/
            if(extras == null) {
                phoneNumber= null;
                callEndTime = null;
                callDuration = null;
                userName = null;
                agentName = null;
                userType = null;
                productType = null;
                callRemarks = null;
                toggleUserType = false;
                toggleProductType = false;

            } else {
                phoneNumber= extras.getString(Constants.phoneNumber);
                callEndTime = extras.getString(Constants.callEndTime);
                callDuration = extras.getString(Constants.callDuration);
                userName = extras.getString(Constants.userName);
                agentName = extras.getString(Constants.agentName);
                userType = extras.getString(Constants.userType);
                productType = extras.getString(Constants.productType);
                callRemarks = extras.getString(Constants.callRemarks);
                toggleUserType = extras.getBoolean(Constants.toggleUserType);
                toggleProductType = extras.getBoolean(Constants.toggleProductType);
            }
        } else {
            phoneNumber = (String) savedInstanceState.getSerializable(Constants.phoneNumber);
            callEndTime = (String) savedInstanceState.getSerializable(Constants.callEndTime);
            callDuration = (String) savedInstanceState.getSerializable(Constants.callDuration);
            userName = (String) savedInstanceState.getSerializable(Constants.userName);
            agentName = (String) savedInstanceState.getSerializable(Constants.agentName);
            userType = (String) savedInstanceState.getSerializable(Constants.userType);
            productType = (String) savedInstanceState.getSerializable(Constants.productType);
            callRemarks = (String) savedInstanceState.getSerializable(Constants.callRemarks);
            toggleUserType = (boolean) savedInstanceState.getSerializable(Constants.toggleUserType);
            toggleProductType = (boolean) savedInstanceState.getSerializable(Constants.toggleProductType);
        }


        /** Need to initialize some views **/
        initializeViews();

        /** Need to do some actions on creation of views **/
        initialActions();

    }

    public void initializeViews(){
        userBackgroundView = (View) findViewById(R.id.user_type_background_view);
        newUserTextView = (TextView) findViewById(R.id.textView_new_user);
        existingUserTextView = (TextView) findViewById(R.id.textView_existing_user);
        customUserTypeEditText = (MyEditText) findViewById(R.id.input_user_type_drop_down);
        customUserTypeUnderline = (View) findViewById(R.id.user_type_dropdown_underline);
        userTypeRotateIcon = (View) findViewById(R.id.user_type_rotate_icon);

        inputUserType = (TextView) findViewById(R.id.input_user_type);
        inputProductType = (TextView) findViewById(R.id.input_product_type);

        productBackgroundView = (View) findViewById(R.id.product_background_view);
        existingProductTextView = (TextView) findViewById(R.id.textView_fixed_product);
        newProductTextView = (TextView) findViewById(R.id.textView_custom_product);
        customProductTypeEditText = (MyEditText) findViewById(R.id.input_product_type_drop_down);
        customProductTypeUnderline = (View) findViewById(R.id.product_type_dropdown_underline);
        productTypeRotateIcon = (View) findViewById(R.id.product_type_rotate_icon);

        dummyTimeStamp = (TextView) findViewById(R.id.input_timestamp);
        dummyViewCoveringBothDropDown = (View)findViewById(R.id.dummy_view_covering_both_drop_down);

        inputName = (MyEditText) findViewById(R.id.input_name);
        inputPhoneNumber = (MyEditText) findViewById(R.id.input_phone_no);
        inputAgentName = (MyEditText) findViewById(R.id.input_agent_name);
        inputTimeStamp = (TextView) findViewById(R.id.input_timestamp_date);
        inputCallDuration = (TextView) findViewById(R.id.input_timestamp_duration);
        remarksEditText = (MyEditText) findViewById(R.id.remarks_edit_text);

        scrollView = (ScrollView) findViewById(R.id.scroll);
    }

    @Override
    public void onSaveInstanceState(Bundle bundle){

        bundle.putString(Constants.phoneNumber,phoneNumber);
        bundle.putString(Constants.callEndTime,callEndTime);
        bundle.putString(Constants.callDuration,callDuration);
        bundle.putString(Constants.userName,userName);
        bundle.putString(Constants.agentName,agentName);
        bundle.putString(Constants.userType,userType);
        bundle.putString(Constants.productType,productType);
        bundle.putString(Constants.callRemarks,callRemarks);
        bundle.putBoolean(Constants.toggleUserType, toggleUserType);
        bundle.putBoolean(Constants.toggleProductType, toggleProductType);
        bundle.putBoolean(Constants.needToToggle, true);

        super.onSaveInstanceState(bundle);
    }

    @Override
    public void onRestoreInstanceState(Bundle bundle){
        super.onRestoreInstanceState(bundle);

        /** So it came after an orientation changes, we need to make sure that
         * what we did in onCreate does not get repeated. Same state of previous
         * orientation is presented to him when he comes back **/
        boolean toggle = bundle.getBoolean(Constants.needToToggle);
        if(toggle){
            initialActions();
        }
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

    public void initialActions() {

        if(callRemarks != null && callRemarks.length() >0 ){
            remarksEditText.setText(callRemarks);
        }

        if(userType != null && userType.length() >0 ){
            inputUserType.setText(userType);
        }

        if(productType != null && productType.length() >0 ){
            inputProductType.setText(productType);
        }

        if (userName == null || userName.length() == 0) {
            inputName.setHint(Constants.unknown);
        } else {
            inputName.setHint(userName);
        }

        /** Can't be null Fields **/
        inputPhoneNumber.setText(phoneNumber);
        inputAgentName.setHint(agentName);
        inputTimeStamp.setText(callEndTime);
        inputCallDuration.setText(callDuration);

        toggleUserTypeViewGroup();
        toggleProductTypeViewGroup();

        enableTextWatchers();
        dynamicFixLayout();
    }

    public void saveClicked(View v){

        if(validateFields()) {

            /** Just before finish set the result for waiting activity **/
            passActivityResult();

            finish();
        } else {
            scrollView.smoothScrollTo(0,0);
        }
    }

    public void dismissClicked(View v){
        finish();
    }

    public void userTypeClicked(View v){

        /** Spinner hide/show Handler for user type **/
        toggleUserTypeViewGroup();
    }

    public void productTypeClicked(View v){

        /** Spinner hide/show handler for product type **/
        toggleProductTypeViewGroup();
    }

    public void newUserClicked(View v){
        inputUserType.setText(getResources().getString(R.string.new_user));
        toggleUserTypeViewGroup();
    }

    public void existingUserClicked(View v){
        inputUserType.setText(getResources().getString(R.string.existing_user));
        toggleUserTypeViewGroup();
    }

    public void fixedProductClicked(View v){
        inputProductType.setText(getResources().getString(R.string.fixed_product));
        toggleProductTypeViewGroup();
    }

    public void customProductClicked(View v){
        inputProductType.setText(getResources().getString(R.string.custom_product));
        toggleProductTypeViewGroup();
    }




    private void dynamicFixLayout(){
        Display display = getWindowManager().getDefaultDisplay();
        dummyTimeStamp.getLayoutParams().width = display.getWidth()/2 - 24;
    }

    private void toggleUserTypeViewGroup(){

        if(!toggleUserType) {
            userBackgroundView.setVisibility(View.GONE);
            newUserTextView.setVisibility(View.GONE);
            existingUserTextView.setVisibility(View.GONE);
            customUserTypeEditText.setVisibility(View.GONE);
            customUserTypeUnderline.setVisibility(View.GONE);
            userTypeRotateIcon.setBackground(getResources().getDrawable(R.drawable.blue_arrow));

            /** Hide keyboard **/
            hideKeyboard();

        } else {
            userBackgroundView.setVisibility(View.VISIBLE);
            newUserTextView.setVisibility(View.VISIBLE);
            existingUserTextView.setVisibility(View.VISIBLE);
            customUserTypeEditText.setVisibility(View.VISIBLE);
            customUserTypeUnderline.setVisibility(View.VISIBLE);
            userTypeRotateIcon.setBackground(getResources().getDrawable(R.drawable.blue_arrow_inverted));
        }

        toggleUserType = !toggleUserType;

        /** Adjust Remarks **/
        dummyViewCoveringBothDropDownToggle();
    }

    private void toggleProductTypeViewGroup(){

        if(!toggleProductType) {
            productBackgroundView.setVisibility(View.GONE);
            newProductTextView.setVisibility(View.GONE);
            existingProductTextView.setVisibility(View.GONE);
            customProductTypeEditText.setVisibility(View.GONE);
            customProductTypeUnderline.setVisibility(View.GONE);
            productTypeRotateIcon.setBackground(getResources().getDrawable(R.drawable.blue_arrow));

            /** Hide keyboard **/
            hideKeyboard();

        } else {
            productBackgroundView.setVisibility(View.VISIBLE);
            newProductTextView.setVisibility(View.VISIBLE);
            existingProductTextView.setVisibility(View.VISIBLE);
            customProductTypeEditText.setVisibility(View.VISIBLE);
            customProductTypeUnderline.setVisibility(View.VISIBLE);
            productTypeRotateIcon.setBackground(getResources().getDrawable(R.drawable.blue_arrow_inverted));

        }

        toggleProductType = !toggleProductType;

        /** Adjust Remarks **/
        dummyViewCoveringBothDropDownToggle();
    }

    private void dummyViewCoveringBothDropDownToggle(){

        if(!toggleProductType || !toggleUserType){
            dummyViewCoveringBothDropDown.setVisibility(View.VISIBLE);
        } else {
            dummyViewCoveringBothDropDown.setVisibility(View.GONE);
        }
    }

    private void enableTextWatchers(){
        customUserTypeEditText.addTextChangedListener(new TypeChangeTextWatcher(this,customUserTypeEditText, inputUserType,true));
        customProductTypeEditText.addTextChangedListener(new TypeChangeTextWatcher(this, customProductTypeEditText, inputProductType, false));
    }

    private void hideKeyboard(){


        /** Luckily, this helped me resize and re-span when keyboard appears. **/
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        /** This will hide the keyboard **/
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void passActivityResult(){
        Intent intent = new Intent();
        intent.putExtra(Constants.dataPojo, getDataPojo());
        setResult(Constants.feedbackActivityRequestCode, intent);
    }

    private DataParcel getDataPojo(){

        DataParcel pojo = new DataParcel();
        pojo.setName(inputName.getText().toString());
        pojo.setAgentName(inputAgentName.getText().toString());
        pojo.setTimestamp(inputTimeStamp.getText().toString());
        pojo.setCallDuration(inputCallDuration.getText().toString());
        pojo.setUserMobileNumber(inputPhoneNumber.getText().toString());
        pojo.setUserType(inputUserType.getText().toString());
        pojo.setProductType(inputProductType.getText().toString());
        pojo.setCallRemarks(remarksEditText.getText().toString());

        return pojo;
    }

    private boolean validateFields(){

        if(inputName.getText().length() == 0) {
            inputName.setText(inputName.getHint().toString());
        }

        if(inputAgentName.getText().length() == 0){
            inputAgentName.setText(inputAgentName.getHint().toString());
        }

        if(inputPhoneNumber.getText().length() >0 && inputPhoneNumber.getText().length() < 10){

            /** Mark field red **/
            inputPhoneNumber.getMyEditTextHeading().setTextColor(getResources().getColor(android.R.color.holo_red_light));
            inputPhoneNumber.getUnderlineView().setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));

            return false;
        }

        return true;
    }
}
