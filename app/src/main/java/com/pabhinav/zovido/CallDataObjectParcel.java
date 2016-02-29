package com.pabhinav.zovido;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author pabhinav
 */
public class CallDataObjectParcel implements Parcelable {

    private String phoneNumber;

    private String name;

    private String callType;

    private String callDate;

    private String callDuration;

    private boolean showTick;

    private boolean uploadedTick;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CallDataObjectParcel that = (CallDataObjectParcel) o;

        if (!phoneNumber.equals(that.phoneNumber)) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (!callType.equals(that.callType)) return false;
        if (!callDate.equals(that.callDate)) return false;
        return callDuration.equals(that.callDuration);

    }


    @Override
    public String toString() {
        return "CallDataObjectParcel{" +
                "phoneNumber='" + phoneNumber + '\'' +
                ", name='" + name + '\'' +
                ", callType='" + callType + '\'' +
                ", callDate='" + callDate + '\'' +
                ", callDuration='" + callDuration + '\'' +
                '}';
    }

    /*************************************/
    /********** Getters/setters **********/
    /*************************************/
    
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public String getCallDate() {
        return callDate;
    }

    public void setCallDate(String callDate) {
        this.callDate = callDate;
    }

    public String getCallDuration() {
        return callDuration;
    }

    public void setCallDuration(String callDuration) {
        this.callDuration = callDuration;
    }

    public boolean isShowTick() {
        return showTick;
    }

    public void setShowTick(boolean showTick) {
        this.showTick = showTick;
    }

    public boolean isUploadedTick() {
        return uploadedTick;
    }

    public void setUploadedTick(boolean uploadedTick) {
        this.uploadedTick = uploadedTick;
    }

    /******************************************/
    /********** Parcelable interface **********/
    /******************************************/

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags){
        
        out.writeString(phoneNumber);
        out.writeString(name);
        out.writeString(callType);
        out.writeString(callDate);
        out.writeString(callDuration);
    }

    private static CallDataObjectParcel readFromParcel(Parcel in) {

        CallDataObjectParcel CallDataObjectParcel = new CallDataObjectParcel();
        CallDataObjectParcel.setPhoneNumber(in.readString());
        CallDataObjectParcel.setName(in.readString());
        CallDataObjectParcel.setCallType(in.readString());
        CallDataObjectParcel.setCallDate(in.readString());
        CallDataObjectParcel.setCallDuration(in.readString());

        return CallDataObjectParcel;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        public CallDataObjectParcel createFromParcel(Parcel in) {
            return readFromParcel(in);
        }

        public CallDataObjectParcel[] newArray(int size) {
            return new CallDataObjectParcel[size];
        }
    };
}
