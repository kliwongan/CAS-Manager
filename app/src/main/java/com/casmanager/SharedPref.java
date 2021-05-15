package com.casmanager;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {
    SharedPreferences mySharedPref;

    public SharedPref(Context context) {
        mySharedPref = context.getSharedPreferences("filename", Context.MODE_PRIVATE);
    }

    public void setNightModeState(Boolean state) {
        SharedPreferences.Editor editor = mySharedPref.edit();
        editor.putBoolean("NightMode", state);
        editor.commit();
    }

    public Boolean loadNightMode() {
        Boolean state = mySharedPref.getBoolean("NightMode", false);
        return state;
    }

    public void setFragmentState(int fragmentState) {
        SharedPreferences.Editor editor = mySharedPref.edit();
        editor.putInt("fragmentState", fragmentState);
        editor.commit();
    }

    public int getFragmentState() {
        int fragmentState = mySharedPref.getInt("fragmentState", 1);
        return fragmentState;
    }

}
