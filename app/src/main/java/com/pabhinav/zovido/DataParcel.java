package com.pabhinav.zovido;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author pabhinav
 */
public class DataParcel implements Parcelable {

    private int id;

    private String name;

    /** optional **/
    private String agentName;

    private String timestamp;

    private String callDuration;

    private String userMobileNumber;

    private String userType;

    private String productType;

    private String callRemarks;

    @Override
    public String toString() {
        return "DataParcel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", agentName='" + agentName + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", callDuration='" + callDuration + '\'' +
                ", userMobileNumber='" + userMobileNumber + '\'' +
                ", userType='" + userType + '\'' +
                ", productType='" + productType + '\'' +
                ", callRemarks='" + callRemarks + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataParcel pojo = (DataParcel) o;

        if (!name.equals(pojo.name)) return false;
        if (!agentName.equals(pojo.agentName)) return false;
        if (!timestamp.equals(pojo.timestamp)) return false;
        if (!callDuration.equals(pojo.callDuration)) return false;
        if (!userMobileNumber.equals(pojo.userMobileNumber)) return false;
        if (!userType.equals(pojo.userType)) return false;
        if (!productType.equals(pojo.productType)) return false;
        return !(callRemarks != null ? !callRemarks.equals(pojo.callRemarks) : pojo.callRemarks != null);
    }



    /*************************************/
    /********** Getters/setters **********/
    /*************************************/

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getCallDuration() {
        return callDuration;
    }

    public void setCallDuration(String callDuration) {
        this.callDuration = callDuration;
    }

    public String getUserMobileNumber() {
        return userMobileNumber;
    }

    public void setUserMobileNumber(String userMobileNumber) {
        this.userMobileNumber = userMobileNumber;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getCallRemarks() {
        return callRemarks;
    }

    public void setCallRemarks(String callRemarks) {
        this.callRemarks = callRemarks;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

        out.writeString(name);
        out.writeString(agentName);
        out.writeString(timestamp);
        out.writeString(callDuration);
        out.writeString(userMobileNumber);
        out.writeString(userType);
        out.writeString(productType);
        out.writeString(callRemarks);
    }

    private static DataParcel readFromParcel(Parcel in) {

        DataParcel dataParcel = new DataParcel();
        dataParcel.setName(in.readString());
        dataParcel.setAgentName(in.readString());
        dataParcel.setTimestamp(in.readString());
        dataParcel.setCallDuration(in.readString());
        dataParcel.setUserMobileNumber(in.readString());
        dataParcel.setUserType(in.readString());
        dataParcel.setProductType(in.readString());
        dataParcel.setCallRemarks(in.readString());

        return dataParcel;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        public DataParcel createFromParcel(Parcel in) {
            return readFromParcel(in);
        }

        public DataParcel[] newArray(int size) {
            return new DataParcel[size];
        }
    };
}
