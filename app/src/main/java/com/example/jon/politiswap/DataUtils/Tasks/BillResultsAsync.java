package com.example.jon.politiswap.DataUtils.Tasks;

import android.os.AsyncTask;

import com.example.jon.politiswap.DataUtils.Recent.RecentBills;
import com.example.jon.politiswap.DataUtils.RetrofitClasses.RetrofitLegislationFetcher;
import com.example.jon.politiswap.MainActivity;

import java.util.UUID;

public class BillResultsAsync extends AsyncTask<Void, Void, RecentBills> {

    private int mOffset;
    private BillHandler mHandler;
    private String queueIdentifier;

    public interface BillHandler {
        void recentBillsCallback(RecentBills results);
    }

    public BillResultsAsync(BillHandler handler, int offset) {
        mOffset = offset;
        mHandler = handler;
    }

    @Override
    protected void onPreExecute() {
        queueIdentifier = UUID.randomUUID().toString();
        MainActivity.mTaskWithPriority = queueIdentifier;
        super.onPreExecute();
    }

    @Override
    protected RecentBills doInBackground(Void... voids) {
        return RetrofitLegislationFetcher.getRecentBills(mOffset);
    }

    @Override
    protected void onPostExecute(RecentBills results) {
        if (queueIdentifier.equals(MainActivity.mTaskWithPriority)) {
            mHandler.recentBillsCallback(results);
        }
    }
}
