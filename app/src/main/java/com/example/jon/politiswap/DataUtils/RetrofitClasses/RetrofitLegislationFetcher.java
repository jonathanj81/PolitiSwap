package com.example.jon.politiswap.DataUtils.RetrofitClasses;

import com.example.jon.politiswap.BuildConfig;
import com.example.jon.politiswap.DataUtils.Recent.RecentBills;
import com.example.jon.politiswap.DataUtils.Searched.SearchedBills;
import com.example.jon.politiswap.MainActivity;

import java.io.IOException;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitLegislationFetcher {

    private static SearchedBills searchedBills;
    private static RecentBills recentBills;

    public static SearchedBills getSearchedBills(String query, int offset) {

        OkHttpClient httpClient = new OkHttpClient();

        Retrofit retrofit =
                new Retrofit.Builder()
                        .addConverterFactory(GsonConverterFactory.create())
                        .baseUrl("https://api.propublica.org/congress/v1/bills/")
                        .client(httpClient)
                        .build();

        LegislationClient client = retrofit.create(LegislationClient.class);

        Call<SearchedBills> call = client.getSearchedResultsFromJson(MainActivity.mResult, query, "date", offset);

        try {
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static RecentBills getRecentBills(int offset) {

        OkHttpClient httpClient = new OkHttpClient();

        Retrofit retrofit =
                new Retrofit.Builder()
                        .addConverterFactory(GsonConverterFactory.create())
                        .baseUrl("https://api.propublica.org/congress/v1/115/both/bills/")
                        .client(httpClient)
                        .build();

        LegislationClient client = retrofit.create(LegislationClient.class);

        Call<RecentBills> call = client.getRecentResultsFromJson(MainActivity.mResult, offset);

        try {
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
