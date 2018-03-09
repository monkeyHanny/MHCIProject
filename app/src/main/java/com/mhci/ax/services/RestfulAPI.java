package com.mhci.ax.services;


import com.google.gson.JsonArray;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by monkeyhanny on 9/3/2018.
 */

public interface RestfulAPI {
    String BASE_URL = "http://api.cortical.io:80/rest/";

    @FormUrlEncoded
    @POST("text/keywords?retina_name=en_associative")
    Call<JsonArray> getKeyword(@Field("body") String sentence);
}
