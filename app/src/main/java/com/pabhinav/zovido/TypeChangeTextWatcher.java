package com.pabhinav.zovido;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

/**
 * @author pabhinav
 */
public class TypeChangeTextWatcher implements TextWatcher {

    private String defaultText;
    private TextView inputTextView;
    private MyEditText editText;
    private boolean userType;
    private Context context;

    public TypeChangeTextWatcher(Context context, MyEditText editText, TextView inputTextView, boolean userType){
        defaultText = inputTextView.getText().toString();
        this.inputTextView = inputTextView;
        this.editText = editText;
        this.userType = userType;
        this.context = context;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if(s.length() == 0) {
            if(userType){
                String newUser = context.getResources().getString(R.string.new_user);
                String existingUser = context.getResources().getString(R.string.existing_user);

                if(!defaultText.equals(newUser) && !defaultText.equals(existingUser)){
                    defaultText = newUser;
                }
            } else {
                String fixedProduct = context.getResources().getString(R.string.fixed_product);
                String customProduct = context.getResources().getString(R.string.custom_product);

                if(!defaultText.equals(fixedProduct) && !defaultText.equals(customProduct)){
                    defaultText = fixedProduct;
                }
            }
            inputTextView.setText(defaultText);
        } else {
            inputTextView.setText(editText.getText().toString());
        }
    }


}
