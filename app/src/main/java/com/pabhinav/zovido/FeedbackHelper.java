package com.pabhinav.zovido;

import android.os.Bundle;
import android.widget.EditText;

/**
 * @author pabhinav
 */
public class FeedbackHelper {

    private Bundle savedInstanceState;
    private Bundle intentExtras;

    /** Constructor **/
    public FeedbackHelper(Bundle savedInstanceState, Bundle intentExtras){
        this.savedInstanceState = savedInstanceState;
        this.intentExtras = intentExtras;
    }

    /** Returns string value from initialized bundle **/
    public String getStringFromBundle(String token, String defaultValue){

        if(savedInstanceState == null){
            if(intentExtras == null){
                return defaultValue;
            } else {
                return intentExtras.getString(token);
            }
        } else {
            return (String) savedInstanceState.getSerializable(token);
        }
    }

    /** Returns boolean value from initialized bundle **/
    public boolean getBooleanFromBundle(String token, boolean defaultValue){
        if(savedInstanceState == null){
            if(intentExtras == null){
                return defaultValue;
            } else {
                return intentExtras.getBoolean(token);
            }
        } else {
            return (boolean) savedInstanceState.getSerializable(token);
        }
    }

    /** Returns int value from initialized bundle **/
    public int getIntFromBundle(String token, int defaultValue){
        if(savedInstanceState == null){
            if(intentExtras == null){
                return defaultValue;
            } else {
                return intentExtras.getInt(token);
            }
        } else {
            return (int) savedInstanceState.getSerializable(token);
        }
    }

}


