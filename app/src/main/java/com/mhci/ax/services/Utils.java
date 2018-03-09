package com.mhci.ax.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.mhci.ax.services.RestfulAPI.BASE_URL;

/**
 * Created by monkeyhanny on 10/3/2018.
 */

public class Utils {

    public static Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    public static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();
    public static RestfulAPI api = retrofit.create(RestfulAPI.class);
}
