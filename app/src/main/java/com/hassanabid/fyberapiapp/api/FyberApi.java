package com.hassanabid.fyberapiapp.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by hassanabid on 8/5/16.
 */
public interface FyberApi {

    @GET("feed/v1/offers.json")
    Call<FyberApiResponse[]> getCupcakesList( @Query("appid") String appid,
                                              @Query("device_id") String device_id,
                                              @Query("ip") String ip,
                                              @Query("locale") String locale,
                                              @Query("offer_types") String offer_types,
                                              @Query("hash_key") String hash_key);
        }
