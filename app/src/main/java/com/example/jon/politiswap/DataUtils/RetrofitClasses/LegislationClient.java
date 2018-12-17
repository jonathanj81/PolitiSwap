package com.example.jon.politiswap.DataUtils.RetrofitClasses;

import com.example.jon.politiswap.DataUtils.Recent.RecentBills;
import com.example.jon.politiswap.DataUtils.Searched.SearchedBills;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface LegislationClient {

    @Headers("X-API-Key: ")
    @GET("search.json")
    Call<SearchedBills> getSearchedResultsFromJson(@Query("query") String query, @Query("sort") String sort);

    @Headers("X-API-Key: ")
    @GET("introduced.json")
    Call<RecentBills> getRecentResultsFromJson(@Query("offset") int offset);
}
