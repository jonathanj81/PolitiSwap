package com.example.jon.politiswap.DataUtils.Tasks;

import android.os.AsyncTask;

import com.example.jon.politiswap.DataUtils.Recent.RecentBills;
import com.example.jon.politiswap.DataUtils.RetrofitClasses.RetrofitLegislationFetcher;
import com.example.jon.politiswap.DataUtils.Searched.SearchedBills;

public class SearchedBillsAsync extends AsyncTask<Void, Void, SearchedBills> {

    private String mQuery;
    private SearchedBillsHandler mHandler;

    public interface SearchedBillsHandler {
        void searchedBillsCallback(SearchedBills results);
    }

    public SearchedBillsAsync(SearchedBillsHandler handler, String query) {
        mQuery = query;
        mHandler = handler;
    }

    @Override
    protected SearchedBills doInBackground(Void... voids) {
        return RetrofitLegislationFetcher.getSearchedBills(mQuery);
    }

    @Override
    protected void onPostExecute(SearchedBills results) {
        mHandler.searchedBillsCallback(results);
    }
}

