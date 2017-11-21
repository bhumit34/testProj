package com.schindler.schindler.tag;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.schindler.commnonclass.ConstantData;
import com.schindler.gettersetter.UserPermissionViewModel;

import java.util.ArrayList;

public class Pref {

    @Nullable
    private static SharedPreferences sharedPreferences = null;

    public static void openPref(@NonNull Context context) {

        sharedPreferences = context.getSharedPreferences(ConstantData.PREF_FILE,
                Context.MODE_PRIVATE);
    }

    @Nullable
    public static String getValue(@NonNull Context context, String key, String defaultValue) {
        Pref.openPref(context);
        String result = Pref.sharedPreferences.getString(key, defaultValue);
        Pref.sharedPreferences = null;
        return result;
    }

    public static void setValue(@NonNull Context context, String key, String value) {
        Pref.openPref(context);
        Editor prefsPrivateEditor = Pref.sharedPreferences.edit();
        prefsPrivateEditor.putString(key, value);
        prefsPrivateEditor.commit();
        prefsPrivateEditor = null;
        Pref.sharedPreferences = null;
    }

}
