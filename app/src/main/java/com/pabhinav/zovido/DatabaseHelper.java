package com.pabhinav.zovido;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "savedCallDetailsManager";

    // DataParcels table name
    private static final String TABLE_NAME = "dataparcel";

    // DataParcels Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_PH_NO = "phone_number";
    private static final String KEY_AG_NAME = "agent_name";
    private static final String KEY_TIMESTAMP = "timestamp";
    private static final String KEY_DURATION = "duration";
    private static final String KEY_PRP = "purpose";
    private static final String KEY_PRT = "product";
    private static final String KEY_SPT = "sport";
    private static final String KEY_REMARKS = "call_remarks";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_DataParcels_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT,"
                + KEY_PH_NO + " TEXT,"
                + KEY_AG_NAME + " TEXT,"
                + KEY_TIMESTAMP + " TEXT,"
                + KEY_DURATION + " TEXT,"
                + KEY_PRP + " TEXT,"
                + KEY_PRT + " TEXT,"
                + KEY_SPT + " TEXT,"
                + KEY_REMARKS + " TEXT"
                + ")";
        db.execSQL(CREATE_DataParcels_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new contact
    int addDataParcel(DataParcel dataParcel) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, dataParcel.getName());
        values.put(KEY_PH_NO, dataParcel.getUserMobileNumber());
        values.put(KEY_AG_NAME , dataParcel.getAgentName());
        values.put(KEY_TIMESTAMP , dataParcel.getTimestamp());
        values.put(KEY_DURATION ,dataParcel.getCallDuration());
        values.put(KEY_PRP , dataParcel.getPurpose());
        values.put(KEY_PRT , dataParcel.getProduct());
        values.put(KEY_SPT, dataParcel.getSport());
        values.put(KEY_REMARKS, dataParcel.getCallRemarks());

        // Inserting Row
        int id = (int)db.insert(TABLE_NAME, null, values);
        db.close(); // Closing database connection

        Log.d("Zovido : Insert : ", String.valueOf(id));

        return id;
    }

    // Getting All DataParcels
    public ArrayList<DataParcel> getAllDataParcels() {
        
        ArrayList<DataParcel> contactList = new ArrayList<DataParcel>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                DataParcel dataParcel = new DataParcel();
                dataParcel.setId(Integer.parseInt(cursor.getString(0)));
                dataParcel.setName(cursor.getString(1));
                dataParcel.setUserMobileNumber(cursor.getString(2));
                dataParcel.setAgentName(cursor.getString(3));
                dataParcel.setTimestamp(cursor.getString(4));
                dataParcel.setCallDuration(cursor.getString(5));
                dataParcel.setPurpose(cursor.getString(6));
                dataParcel.setProduct(cursor.getString(7));
                dataParcel.setSport(cursor.getString(8));
                dataParcel.setCallRemarks(cursor.getString(9));
                
                // Adding contact to list
                contactList.add(dataParcel);
                
            } while (cursor.moveToNext());
        }

        cursor.close();

        return contactList;
    }

    // Getting All DataParcels
    public void printWholeDatabase() {

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                DataParcel dataParcel = new DataParcel();
                dataParcel.setId(Integer.parseInt(cursor.getString(0)));
                dataParcel.setName(cursor.getString(1));
                dataParcel.setUserMobileNumber(cursor.getString(2));
                dataParcel.setAgentName(cursor.getString(3));
                dataParcel.setTimestamp(cursor.getString(4));
                dataParcel.setCallDuration(cursor.getString(5));
                dataParcel.setPurpose(cursor.getString(6));
                dataParcel.setProduct(cursor.getString(7));
                dataParcel.setSport(cursor.getString(8));
                dataParcel.setCallRemarks(cursor.getString(9));

                Log.d("Zovido : DbItem : ", dataParcel.toString());

            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    // Updating single contact
    public int updateDataParcel(DataParcel dataParcel) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, dataParcel.getName());
        values.put(KEY_PH_NO, dataParcel.getUserMobileNumber());
        values.put(KEY_AG_NAME , dataParcel.getAgentName());
        values.put(KEY_TIMESTAMP , dataParcel.getTimestamp());
        values.put(KEY_DURATION ,dataParcel.getCallDuration());
        values.put(KEY_PRP , dataParcel.getPurpose());
        values.put(KEY_PRT, dataParcel.getProduct());
        values.put(KEY_SPT, dataParcel.getSport());
        values.put(KEY_REMARKS, dataParcel.getCallRemarks());


        int updateCode = db.update(TABLE_NAME, values, KEY_ID + " = ?", new String[] { String.valueOf(dataParcel.getId()) });
        db.close();
        Log.d("Zovido : Update : ", String.valueOf(updateCode));
        return updateCode;
    }

    // Deleting single contact
    public void deleteDataParcel(DataParcel dataParcel) {
        SQLiteDatabase db = this.getWritableDatabase();
        int deletecode = db.delete(TABLE_NAME, KEY_ID + " = ?", new String[] { String.valueOf(dataParcel.getId()) });
        Log.d("Zovido : Delete : ", String.valueOf(deletecode));
        db.close();
    }


    // Getting DataParcels Count
    public int getDataParcelsCount() {

        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        int count = cursor.getCount();
        
        cursor.close();
        
        // return count
        return count;
    }


}