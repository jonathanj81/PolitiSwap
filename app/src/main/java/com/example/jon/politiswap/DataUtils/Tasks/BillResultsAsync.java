package com.example.jon.politiswap.DataUtils.Tasks;

import android.os.AsyncTask;

import com.example.jon.politiswap.DataUtils.Recent.RecentBills;
import com.example.jon.politiswap.DataUtils.RetrofitClasses.RetrofitLegislationFetcher;

public class BillResultsAsync extends AsyncTask<Void, Void, RecentBills> {

    private int mOffset;
    private BillHandler mHandler;

    public interface BillHandler {
        void recentBillsCallback(RecentBills results);
    }

    public BillResultsAsync(BillHandler handler, int offset) {
        mOffset = offset;
        mHandler = handler;
    }

    @Override
    protected RecentBills doInBackground(Void... voids) {
        return RetrofitLegislationFetcher.getRecentBills(mOffset);
    }

    @Override
    protected void onPostExecute(RecentBills results) {
        mHandler.recentBillsCallback(results);
    }
}
