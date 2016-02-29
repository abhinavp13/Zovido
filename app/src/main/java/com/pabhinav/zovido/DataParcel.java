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

    private String purpose;

    private String product;

    private String sport;

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
                ", purpose='" + purpose + '\'' +
                ", product='" + product + '\'' +
                ", sport='" + sport + '\'' +
                ", callRemarks='" + callRemarks + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (agentName != null ? agentName.hashCode() : 0);
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        result = 31 * result + (callDuration != null ? callDuration.hashCode() : 0);
        result = 31 * result + (userMobileNumber != null ? userMobileNumber.hashCode() : 0);
        result = 31 * result + (purpose != null ? purpose.hashCode() : 0);
        result = 31 * result + (product != null ? product.hashCode() : 0);
        result = 31 * result + (sport != null ? sport.hashCode() : 0);
        result = 31 * result + (callRemarks != null ? callRemarks.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataParcel that = (DataParcel) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (agentName != null ? !agentName.equals(that.agentName) : that.agentName != null)
            return false;
        if (timestamp != null ? !timestamp.equals(that.timestamp) : that.timestamp != null)
            return false;
        if (callDuration != null ? !callDuration.equals(that.callDuration) : that.callDuration != null)
            return false;
        if (userMobileNumber != null ? !userMobileNumber.equals(that.userMobileNumber) : that.userMobileNumber != null)
            return false;
        if (purpose != null ? !purpose.equals(that.purpose) : that.purpose != null) return false;
        if (product != null ? !product.equals(that.product) : that.product != null) return false;
        if (sport != null ? !sport.equals(that.sport) : that.sport != null) return false;
        return !(callRemarks != null ? !callRemarks.equals(that.callRemarks) : that.callRemarks != null);
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

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getSport() {
        return sport;
    }

    public void setSport(String sport) {
        this.sport = sport;
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
        out.writeString(purpose);
        out.writeString(product);
        out.writeString(sport);
        out.writeString(callRemarks);
    }

    private static DataParcel readFromParcel(Parcel in) {

        DataParcel dataParcel = new DataParcel();
        dataParcel.setName(in.readString());
        dataParcel.setAgentName(in.readString());
        dataParcel.setTimestamp(in.readString());
        dataParcel.setCallDuration(in.readString());
        dataParcel.setUserMobileNumber(in.readString());
        dataParcel.setPurpose(in.readString());
        dataParcel.setProduct(in.readString());
        dataParcel.setSport(in.readString());
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
