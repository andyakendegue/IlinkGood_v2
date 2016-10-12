package com.appli.ilink.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

public class PrefManager {
    private static final String KEY_EMAIL = "email";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_IS_WAITING_FOR_SMS = "IsWaitingForSms";
    private static final String KEY_MOBILE = "mobile";
    private static final String KEY_MOBILE_NUMBER = "mobile_number";
    private static final String KEY_NAME = "name";
    private static final String PREF_NAME = "AndroidHive";
    int PRIVATE_MODE;
    Context _context;
    Editor editor;
    SharedPreferences pref;

    public PrefManager(Context context) {
        this.PRIVATE_MODE = 0;
        this._context = context;
        this.pref = this._context.getSharedPreferences(PREF_NAME, this.PRIVATE_MODE);
        this.editor = this.pref.edit();
    }

    public void setIsWaitingForSms(boolean isWaiting) {
        this.editor.putBoolean(KEY_IS_WAITING_FOR_SMS, isWaiting);
        this.editor.commit();
    }

    public boolean isWaitingForSms() {
        return this.pref.getBoolean(KEY_IS_WAITING_FOR_SMS, false);
    }

    public void setMobileNumber(String mobileNumber) {
        this.editor.putString(KEY_MOBILE_NUMBER, mobileNumber);
        this.editor.commit();
    }

    public String getMobileNumber() {
        return this.pref.getString(KEY_MOBILE_NUMBER, null);
    }

    public void createLogin(String name, String email, String mobile) {
        this.editor.putString(KEY_NAME, name);
        this.editor.putString(KEY_EMAIL, email);
        this.editor.putString(KEY_MOBILE, mobile);
        this.editor.putBoolean(KEY_IS_LOGGED_IN, true);
        this.editor.commit();
    }

    public boolean isLoggedIn() {
        return this.pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void clearSession() {
        this.editor.clear();
        this.editor.commit();
    }

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> profile = new HashMap();
        profile.put(KEY_NAME, this.pref.getString(KEY_NAME, null));
        profile.put(KEY_EMAIL, this.pref.getString(KEY_EMAIL, null));
        profile.put(KEY_MOBILE, this.pref.getString(KEY_MOBILE, null));
        return profile;
    }
}
