package com.example.jon.politiswap.DataUtils.Tasks;

import android.os.AsyncTask;
import com.example.jon.politiswap.DataUtils.RetrofitClasses.RetrofitLegislationFetcher;
import com.example.jon.politiswap.DataUtils.Searched.SearchedBills;
import com.example.jon.politiswap.MainActivity;

import java.util.UUID;

public class SearchedBillsAsync extends AsyncTask<Void, Void, SearchedBills> {

    private String mQuery;
    private SearchedBillsHandler mHandler;
    private String queueIdentifier;
    private int mOffset;

    public interface SearchedBillsHandler {
        void searchedBillsCallback(SearchedBills results);
    }

    public SearchedBillsAsync(SearchedBillsHandler handler, String query, int offset) {
        mQuery = query;
        mHandler = handler;
        mOffset = offset;
    }

    @Override
    protected void onPreExecute() {
        queueIdentifier = UUID.randomUUID().toString();
        MainActivity.mTaskWithPriority = queueIdentifier;
        super.onPreExecute();
    }

    @Override
    protected SearchedBills doInBackground(Void... voids) {
        return RetrofitLegislationFetcher.getSearchedBills(mQuery, mOffset);
    }

    @Override
    protected void onPostExecute(SearchedBills results) {
        if (queueIdentifier.equals(MainActivity.mTaskWithPriority)) {
            mHandler.searchedBillsCallback(results);
        }
    }
}

