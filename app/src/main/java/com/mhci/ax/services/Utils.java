package com.mhci.ax.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
}
