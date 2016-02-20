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

import java.util.ArrayList;

public class Feedback extends AppCompatActivity {

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

    /** Important : only supports: String, Integer, boolean,
     *  to add any new type need to change in FeedbackHelper class **/
    /** Defines a bundled data item with its class type, token value and default value **/
    public class BundledDataItemLink {

        Class aClass;
        Object defaultValue;
        String token;

        public BundledDataItemLink(String token, Class aClass, Object defaultValue){
            this.token = token;
            this.aClass = aClass;
            this.defaultValue = defaultValue;
        }
    }

    ArrayList<BundledDataItemLink> bundledDataItemLinkArrayList;
    FeedbackHelper feedbackHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        /** Helps fetch data from Bundle **/
        feedbackHelper = new FeedbackHelper(savedInstanceState, getIntent().getExtras());

        /** Keep on appending items that can be fetched from bundle **/
        appendBundleItem(Constants.phoneNumber, String.class, null);
        appendBundleItem(Constants.callEndTime, String.class, null);
        appendBundleItem(Constants.callDuration, String.class, null);
        appendBundleItem(Constants.userName, String.class, null);
        appendBundleItem(Constants.agentName, String.class, null);
        appendBundleItem(Constants.productType, String.class, null);
        appendBundleItem(Constants.userType, String.class, null);
        appendBundleItem(Constants.callRemarks, String.class, null);
        appendBundleItem(Constants.toggleProductType, Boolean.class, false);
        appendBundleItem(Constants.toggleUserType, Boolean.class, false);
        appendBundleItem(Constants.position, Integer.class, -1);

        feedbackHelper.fetchBundledData(bundledDataItemLinkArrayList);

        /** Need to initialize some views **/
        initializeViews();

        /** Need to do some actions on creation of views **/
        initialActions();

    }

    private void appendBundleItem(String token, Class aClass, Object defaultValue){

        if(bundledDataItemLinkArrayList == null){
            bundledDataItemLinkArrayList = new ArrayList<>();
        }
        bundledDataItemLinkArrayList.add(new BundledDataItemLink(token, aClass, defaultValue));
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

        feedbackHelper.saveBundledItems(bundle);

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

        if(feedbackHelper.getValueForToken(Constants.callRemarks) != null && ((String)feedbackHelper.getValueForToken(Constants.callRemarks)).length() >0 ){
            remarksEditText.setText((String)feedbackHelper.getValueForToken(Constants.callRemarks));
        }

        if(feedbackHelper.getValueForToken(Constants.userType) != null && ((String)feedbackHelper.getValueForToken(Constants.userType)).length() >0 ){
            inputUserType.setText((String)feedbackHelper.getValueForToken(Constants.userType));
        }

        if(feedbackHelper.getValueForToken(Constants.productType) != null && ((String) feedbackHelper.getValueForToken(Constants.productType)).length() >0 ){
            inputProductType.setText((String)feedbackHelper.getValueForToken(Constants.productType));
        }

        if (feedbackHelper.getValueForToken(Constants.userName) == null || ((String)feedbackHelper.getValueForToken(Constants.userName)).length() == 0) {
            inputName.setHint(Constants.unknown);
        } else {
            inputName.setHint((String)feedbackHelper.getValueForToken(Constants.userName));
        }

        /** Can't be null Fields **/
        inputPhoneNumber.setText((String)feedbackHelper.getValueForToken(Constants.phoneNumber));
        inputAgentName.setHint((String)feedbackHelper.getValueForToken(Constants.agentName));
        inputTimeStamp.setText((String)feedbackHelper.getValueForToken(Constants.callEndTime));
        inputCallDuration.setText((String)feedbackHelper.getValueForToken(Constants.callDuration));

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


        if(!(boolean)feedbackHelper.getValueForToken(Constants.toggleUserType)) {
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

        feedbackHelper.updateValueForToken(Constants.toggleUserType,!(boolean)feedbackHelper.getValueForToken(Constants.toggleUserType));

        /** Adjust Remarks **/
        dummyViewCoveringBothDropDownToggle();
    }

    private void toggleProductTypeViewGroup(){

        if(!(boolean)feedbackHelper.getValueForToken(Constants.toggleProductType)) {
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

        feedbackHelper.updateValueForToken(Constants.toggleProductType, !(boolean)feedbackHelper.getValueForToken(Constants.toggleProductType));

        /** Adjust Remarks **/
        dummyViewCoveringBothDropDownToggle();
    }

    private void dummyViewCoveringBothDropDownToggle(){

        if(!(boolean)feedbackHelper.getValueForToken(Constants.toggleProductType) || !(boolean)feedbackHelper.getValueForToken(Constants.toggleUserType)){
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
        intent.putExtra(Constants.position, (int)feedbackHelper.getValueForToken(Constants.position));
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
