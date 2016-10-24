package com.appli.ilink.lib;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.maps.model.Marker;
import com.appli.ilink.model.usersModel;

import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "utilisateurs";

    // Login table name
    private static final String TABLE_LOGIN = "login";

    // Simple Users table name
    private static final String TABLE_SIMPLE_USERS = "simple";



    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_FIRSTNAME = "fistname";
    private static final String KEY_LASTNAME = "lastname";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_COUNTRY_CODE = "country_code";
    private static final String KEY_NETWORK = "network";
    private static final String KEY_MEMBER_CODE = "member_code";
    private static final String KEY_CATEGORY = "category";
    private static final String KEY_BALANCE = "balance";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_VALIDATION_CODE = "validation_code";
    private static final String KEY_ACTIVE = "active";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_PHOTO = "photo";
    private static final String KEY_GENRE = "genre";
    private static final String KEY_CODE_PARRAIN = "code_parrain";
    private static final String KEY_MBRE_RESEAU = "mbre_reseau";
    private static final String KEY_MBRE_SS_RESEAU = "mbre_ss_reseau";

    private ArrayList<usersModel> usersModelArray = new ArrayList<usersModel>();
    private HashMap<Marker, usersModel> mMarkersHashMap;



    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
       /* String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_LOGIN + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_FIRSTNAME + " TEXT,"
                + KEY_LASTNAME + " TEXT,"
                + KEY_EMAIL + " TEXT UNIQUE,"
                + KEY_PHONE + " TEXT,"
                + KEY_PASSWORD + " TEXT,"
                + KEY_PHOTO + " TEXT,"
                + KEY_GENRE + " TEXT" + ")";
        db.execSQL(CREATE_LOGIN_TABLE);
        */
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_SIMPLE_USERS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_FIRSTNAME + " TEXT,"
                + KEY_LASTNAME + " TEXT,"
                + KEY_EMAIL + " TEXT,"
                + KEY_PHONE + " TEXT,"
                + KEY_COUNTRY_CODE + " TEXT,"
                + KEY_NETWORK + " TEXT,"
                + KEY_MEMBER_CODE + " TEXT,"
                + KEY_CODE_PARRAIN + " TEXT,"
                + KEY_CATEGORY + " TEXT,"
                + KEY_BALANCE + " TEXT,"
                + KEY_LATITUDE + " TEXT,"
                + KEY_LONGITUDE + " TEXT,"
                + KEY_MBRE_RESEAU + " TEXT,"
                + KEY_MBRE_SS_RESEAU + " TEXT,"
                + KEY_VALIDATION_CODE + " TEXT,"
                + KEY_ACTIVE + " TEXT" + ")";

        db.execSQL(CREATE_USERS_TABLE);

    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGIN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SIMPLE_USERS);

        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     * */
    public void addUser(String fname, String lname, String email, String phone, String password, String genre,String photo) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FIRSTNAME, fname); // FirstName
        values.put(KEY_LASTNAME, lname); // LastName
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_PHONE, phone); // Email
        values.put(KEY_PASSWORD, password); // Password
        values.put(KEY_PHOTO, photo); // Photo
        values.put(KEY_GENRE, genre); // Genre; // Created At

        // Inserting Row
        db.insert(TABLE_LOGIN, null, values);
        db.close(); // Closing database connection
    }

    /**
     * Storing users in database
     * */
    public void addUsers(String firstname, String lastname, String email, String phone, String country_code, String network, String member_code,String code_parrain,
                         String category, String balance, String latitude, String longitude, String mbre_reseau, String mbre_ss_reseau, String validation_code, String active) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FIRSTNAME, firstname); // FirstName
        values.put(KEY_LASTNAME, lastname); // LastName
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_PHONE, phone); // Email
        values.put(KEY_COUNTRY_CODE, country_code); // Password
        values.put(KEY_NETWORK, network); // Photo
        values.put(KEY_MEMBER_CODE, member_code);  // Created At
        values.put(KEY_CODE_PARRAIN, code_parrain);
        values.put(KEY_CATEGORY, category);
        values.put(KEY_BALANCE, balance);
        values.put(KEY_LATITUDE, latitude);
        values.put(KEY_LONGITUDE, longitude);
        values.put(KEY_MBRE_RESEAU, mbre_reseau);
        values.put(KEY_MBRE_SS_RESEAU, mbre_ss_reseau);
        values.put(KEY_VALIDATION_CODE, validation_code);
        values.put(KEY_ACTIVE, active);

        // Inserting Row
        db.insert(TABLE_SIMPLE_USERS, null, values);
        db.close(); // Closing database connection
    }



    /**
     * Getting user data from database
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String,String> user = new HashMap<String,String>();
        String selectQuery = "SELECT  * FROM " + TABLE_LOGIN;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if(cursor.getCount() > 0){
            user.put("fname", cursor.getString(1));
            user.put("lname", cursor.getString(2));
            user.put("email", cursor.getString(3));
            user.put("phone", cursor.getString(4));
            user.put("password", cursor.getString(5));
            user.put("photo", cursor.getString(6));
            user.put("genre", cursor.getString(7));
        }
        cursor.close();
        db.close();
        // return user
        return user;
    }

    /**
     * Getting users data from database
     * */
    public ArrayList<usersModel> getUsersDetails(){

        ArrayList<usersModel> usersModelArray = new ArrayList<usersModel>();

        String selectQuery = "SELECT  * FROM " + TABLE_SIMPLE_USERS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        int rowCount = cursor.getCount();

        if(cursor.moveToFirst()){
            do {


                usersModelArray.add(new usersModel(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4),
                        cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8),
                        cursor.getString(9), cursor.getString(10), cursor.getString(11), cursor.getString(12),
                        cursor.getString(13),cursor.getString(14), cursor.getString(15), cursor.getString(16)));



                //usersModel users
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return user
        return usersModelArray;
    }

    /**
     * Getting users data ASC from database
     * */
    public ArrayList<usersModel> getUsersDetailsAsc(){

        ArrayList<usersModel> usersModelArray = new ArrayList<usersModel>();

        String selectQuery = "SELECT  * FROM " + TABLE_SIMPLE_USERS +" ORDER BY " + KEY_BALANCE +" ASC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        int rowCount = cursor.getCount();

        if(cursor.moveToFirst()){
            do {


                usersModelArray.add(new usersModel(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4),
                        cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8),
                        cursor.getString(9), cursor.getString(10), cursor.getString(11), cursor.getString(12),
                        cursor.getString(13),cursor.getString(14), cursor.getString(15), cursor.getString(16)));



                //usersModel users
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return user
        return usersModelArray;
    }


    /**
     * Getting users data DSC from database
     * */
    public ArrayList<usersModel> getUsersDetailsDsc(){

        ArrayList<usersModel> usersModelArray = new ArrayList<usersModel>();

        String selectQuery = "SELECT  * FROM " + TABLE_SIMPLE_USERS +" ORDER BY " + KEY_BALANCE +" DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        int rowCount = cursor.getCount();

        if(cursor.moveToFirst()){
            do {


                usersModelArray.add(new usersModel(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4),
                        cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8),
                        cursor.getString(9), cursor.getString(10), cursor.getString(11), cursor.getString(12),
                        cursor.getString(13),cursor.getString(14), cursor.getString(15), cursor.getString(16)));



                //usersModel users
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return user
        return usersModelArray;
    }





    /**
     * Getting user login status
     * return true if rows are there in table
     * */
    public int getRowCount() {
        String countQuery = "SELECT  * FROM " + TABLE_LOGIN;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();

        // return row count
        return rowCount;
    }

    /**
     * Getting ascending golocated users users
     * return true if rows are there in table
     * */
    public int getUserAsc() {
        String countQuery = "SELECT  * FROM " + TABLE_LOGIN +"ORDER BY" + KEY_BALANCE +"ASC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();

        // return row count
        return rowCount;
    }

    /**
     * Getting descending geolocated users
     * return true if rows are there in table
     * */
    public int getUsersDsc() {
        String countQuery = "SELECT  * FROM " + TABLE_LOGIN +"ORDER BY" + KEY_BALANCE +"DSC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();

        // return row count
        return rowCount;
    }


    /**
     * Re crate database
     * Delete all tables and create them again
     * */
    public void resetTables(){
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_SIMPLE_USERS, null, null);
        db.close();
    }

}
