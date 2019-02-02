package com.politiswap.politiswap.DataUtils.RetrofitClasses;

import com.politiswap.politiswap.DataUtils.Recent.RecentBills;
import com.politiswap.politiswap.DataUtils.Searched.SearchedBills;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface LegislationClient {

    @GET("search.json")
    Call<SearchedBills> getSearchedResultsFromJson(@Header("X-API-Key") String result, @Query("query") String query, @Query("sort") String sort, @Query("offset") int offset);

    @GET("introduced.json")
    Call<RecentBills> getRecentResultsFromJson(@Header("X-API-Key") String result, @Query("offset") int offset);
}
