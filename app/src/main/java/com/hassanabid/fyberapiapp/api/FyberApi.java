package com.hassanabid.fyberapiapp.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by hassanabid on 8/5/16.
 */
public interface FyberApi {

    @GET("feed/v1/offers.json")
    Call<FyberApiResponse> getFyberOffers( @Query("appid") String appid,
                                              @Query("device_id") String device_id,
                                              @Query("ip") String ip,
                                              @Query("locale") String locale,
                                              @Query("page") String page,
                                              @Query("ps_time") String ps_time,
                                              @Query("pub0") String pub0,
                                              @Query("timestamp") String timestamp,
                                              @Query("uid") String uid,
                                              @Query("offer_types") String offer_types,
                                              @Query("hash_key") String hash_key);
        }
