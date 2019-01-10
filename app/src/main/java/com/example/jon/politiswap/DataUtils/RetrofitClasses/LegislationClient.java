package com.example.jon.politiswap.DataUtils.RetrofitClasses;

import com.example.jon.politiswap.BuildConfig;
import com.example.jon.politiswap.DataUtils.Recent.RecentBills;
import com.example.jon.politiswap.DataUtils.Searched.SearchedBills;
import com.example.jon.politiswap.MainActivity;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface LegislationClient {

    @GET("search.json")
    Call<SearchedBills> getSearchedResultsFromJson(@Header("X-API-Key") String result, @Query("query") String query, @Query("sort") String sort, @Query("offset") int offset);

    @GET("introduced.json")
    Call<RecentBills> getRecentResultsFromJson(@Header("X-API-Key") String result, @Query("offset") int offset);
}
