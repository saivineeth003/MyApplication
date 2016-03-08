package com.example.eshwanth.myapplication;

import android.content.Context;
import android.content.SharedPreferences;

public class tokenLocalStore {

    public static final String SP_NAME = "tokenDetails";

    SharedPreferences token;

    public tokenLocalStore(Context context) {
        token = context.getSharedPreferences(SP_NAME, 0);
    }

    public void storeToken(String tkn) {
        SharedPreferences.Editor tokenEditor = token.edit();
        tokenEditor.putString("token_value", tkn);
        tokenEditor.apply();
    }

    public void clearToken() {
        SharedPreferences.Editor tokenEditor = token.edit();
        tokenEditor.clear();
        tokenEditor.apply();
    }

    public String returnToken() {
        return token.getString("token_value","");
    }
}
