package com.mhci.ax.services;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mhci.ax.R;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.mhci.ax.services.RestfulAPI.CORTICAL_BASE_URL;
import static com.mhci.ax.services.RestfulAPI.CUSTOM_SEARCH_BASE_URL;

/**
 * Created by monkeyhanny on 10/3/2018.
 */

public class Utils {

    public static Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    public static Retrofit retrofitCortical = new Retrofit.Builder()
            .baseUrl(CORTICAL_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();
    public static RestfulAPI apiCortical = retrofitCortical.create(RestfulAPI.class);


    public static Retrofit retrofitSearch = new Retrofit.Builder()
            .baseUrl(CUSTOM_SEARCH_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();
    public static RestfulAPI apiSearch = retrofitSearch.create(RestfulAPI.class);


    public static String API_KEY = "AIzaSyAI4w45k1d98TWDhtcPg0BhmYm5K831QM8";
    public static String cxId = "013798557058526750109:dg9gmocpn9c";


    private static String PREF_NAME = "preferences";

    public static void setPreferences(Activity context, String original, String target) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(context.getString(R.string.preference_original_key), original);
        editor.putString(context.getString(R.string.preference_target_key), target);
        editor.apply();
    }

    public static void setInit(Activity context) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(context.getString(R.string.preference_init_key), false);

        editor.apply();
    }

    public static boolean ifInit(Activity context) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPref.getBoolean(context.getString(R.string.preference_init_key), true);
    }

    public static String getOriginal(Activity context) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Log.v("getOriginal", "result: " + sharedPref.getString(context.getString(R.string.preference_original_key), ""));
        return sharedPref.getString(context.getString(R.string.preference_original_key), "");
    }

    public static String getTarget(Activity context) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPref.getString(context.getString(R.string.preference_target_key), "");
    }

}
