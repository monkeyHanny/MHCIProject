package com.mhci.ax.services;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Created by monkeyhanny on 9/3/2018.
 */

public interface RestfulAPI {
    String CORTICAL_BASE_URL = "http://api.cortical.io:80";
    String CUSTOM_SEARCH_BASE_URL = "https://www.googleapis.com";

    @POST("/rest/text/keywords?retina_name=en_associative")
    Call<JsonArray> getKeyword(@Body String sentence);

    @GET("/customsearch/v1?searchType=image&num=1")
    Call<JsonObject> getImgUrl(@Query("key") String key, @Query("cx") String cx, @Query("q") String keyword);

}
