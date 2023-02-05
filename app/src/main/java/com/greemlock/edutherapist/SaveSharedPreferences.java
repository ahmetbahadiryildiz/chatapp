package com.greemlock.edutherapist;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SaveSharedPreferences {

    static final String PREF_NAME = "NAME";

    static SharedPreferences getSharedPreferences(Context ctx){
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setPrefName(Context ctx, String name){
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString("name",name);
        editor.commit();
    }
    public static String getPrefName(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_NAME, "");
    }
}
