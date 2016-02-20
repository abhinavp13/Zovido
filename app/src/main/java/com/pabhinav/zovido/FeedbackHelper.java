package com.pabhinav.zovido;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author pabhinav
 */
public class FeedbackHelper {

    private Bundle savedInstanceState;
    private Bundle intentExtras;
    private ArrayList<Feedback.BundledDataItemLink> bundledDataItemLinkArrayList;
    private HashMap<String,Object> bundledDataItemHashMap;

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


    /** sets up initial value for all bundled items **/
    public void fetchBundledData(ArrayList<Feedback.BundledDataItemLink> bundledDataItemLinkArrayList){

        this.bundledDataItemLinkArrayList = bundledDataItemLinkArrayList;
        HashMap<String, Object> bundleItemsHashMap = new HashMap<>();

        /** Loop through all bundled item links **/
        for(int i = 0; i<bundledDataItemLinkArrayList.size(); i++){

            Feedback.BundledDataItemLink bundledDataItemLink = bundledDataItemLinkArrayList.get(i);
            Object value = null;
            if(bundledDataItemLink.aClass.equals(String.class)){
                bundleItemsHashMap.put(bundledDataItemLink.token, getStringFromBundle(bundledDataItemLink.token, (String) bundledDataItemLink.defaultValue));
            } else if (bundledDataItemLink.aClass.equals(Integer.class)){
                bundleItemsHashMap.put(bundledDataItemLink.token, getIntFromBundle(bundledDataItemLink.token, (int) bundledDataItemLink.defaultValue));
            } else if(bundledDataItemLink.aClass.equals(Boolean.class)){
                bundleItemsHashMap.put(bundledDataItemLink.token, getBooleanFromBundle(bundledDataItemLink.token, (boolean) bundledDataItemLink.defaultValue));
            }
        }
        this.bundledDataItemHashMap = bundleItemsHashMap;
    }

    /** save items in the given bundle **/
    public void saveBundledItems(Bundle bundle){

        for(Feedback.BundledDataItemLink bundledDataItemLink : bundledDataItemLinkArrayList){
            if(bundledDataItemLink.aClass.equals(String.class)){
                bundle.putString(bundledDataItemLink.token, (String)bundledDataItemHashMap.get(bundledDataItemLink.token));
            } else if(bundledDataItemLink.aClass.equals(Boolean.class)){
                bundle.putBoolean(bundledDataItemLink.token, (boolean)bundledDataItemHashMap.get(bundledDataItemLink.token));
            } else if(bundledDataItemLink.aClass.equals(Integer.class)){
                bundle.putInt(bundledDataItemLink.token, (int)bundledDataItemHashMap.get(bundledDataItemLink.token));
            }
        }
    }

    /** Returns the value for a given token **/
    public Object getValueForToken(String token){
        return bundledDataItemHashMap.get(token);
    }

    /** Updates the value for a specified token **/
    public void updateValueForToken(String token, Object value){
        bundledDataItemHashMap.put(token, value);
    }
}


