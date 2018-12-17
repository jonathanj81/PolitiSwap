package com.example.jon.politiswap.TabManagement;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.jon.politiswap.DataUtils.Policy;
import com.example.jon.politiswap.DataUtils.Tasks.BillResultsAsync;
import com.example.jon.politiswap.DataUtils.Tasks.FirebaseRetrievalCalls;
import com.example.jon.politiswap.DataUtils.Tasks.SearchedBillsAsync;
import com.example.jon.politiswap.MainActivity;
import com.example.jon.politiswap.R;

import java.util.List;

public class TabListeners {

    private MainActivity mActivity;

    public TabListeners(MainActivity activity){
        mActivity = activity;
    }

    public PolicyTabListener getPolicyTabListener(){
        return new PolicyTabListener();
    }
    public SwapsTabListener getSwapsTabListener(){
        return new SwapsTabListener();
    }
    public ActivityTabListener getActivityTabListener(){
        return new ActivityTabListener();
    }
    public LegislationTabListener getLegislationTabListener(){
        return new LegislationTabListener();
    }

    private class PolicyTabListener implements TabLayout.OnTabSelectedListener {

        private PolicyTabListener(){
            startFirstRetrieval();
        }

        private void startFirstRetrieval(){
            MainActivity.mAdapterNeeded = 3;
            new FirebaseRetrievalCalls(mActivity).getTopPolicies();
        }

        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            tab.getCustomView().findViewById(R.id.tab_item_text_view)
                    .setBackground(mActivity.getResources().getDrawable(R.drawable.tab_item_background_selected));
            switch (tab.getPosition()) {
                case 0:
                    MainActivity.mAdapterNeeded = 3;
                    mActivity.getRecyclerView().setVisibility(View.VISIBLE);
                    mActivity.findViewById(R.id.alt_search_layout).setVisibility(View.GONE);
                    new FirebaseRetrievalCalls(mActivity).getTopPolicies();
                    break;
                case 1:
                    MainActivity.mAdapterNeeded = 4;
                    mActivity.getRecyclerView().setVisibility(View.VISIBLE);
                    mActivity.findViewById(R.id.alt_search_layout).setVisibility(View.GONE);
                    new FirebaseRetrievalCalls(mActivity).getNewPolices();
                    break;
                case 2:
                    MainActivity.mAdapterNeeded = 5;
                    MainActivity.mTopTabManager.getBottomTabManager().getSearchScreen();
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
            tab.getCustomView().findViewById(R.id.tab_item_text_view)
                    .setBackground(mActivity.getResources().getDrawable(R.drawable.tab_item_background));
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    }

    private class SwapsTabListener implements TabLayout.OnTabSelectedListener{

        private SwapsTabListener(){
            startFirstRetrieval();
        }

        private void startFirstRetrieval(){
            MainActivity.mAdapterNeeded = 0;
            new FirebaseRetrievalCalls(mActivity).getTopSwaps();
        }

        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            tab.getCustomView().findViewById(R.id.tab_item_text_view)
                    .setBackground(mActivity.getResources().getDrawable(R.drawable.tab_item_background_selected));
            switch (tab.getPosition()) {
                case 0:
                    MainActivity.mAdapterNeeded = 0;
                    mActivity.getRecyclerView().setVisibility(View.VISIBLE);
                    mActivity.findViewById(R.id.alt_search_layout).setVisibility(View.GONE);
                    new FirebaseRetrievalCalls(mActivity).getTopSwaps();
                    break;
                case 1:
                    MainActivity.mAdapterNeeded = 1;
                    mActivity.getRecyclerView().setVisibility(View.VISIBLE);
                    mActivity.findViewById(R.id.alt_search_layout).setVisibility(View.GONE);
                    new FirebaseRetrievalCalls(mActivity).getNewSwaps();
                    break;
                case 2:
                    MainActivity.mAdapterNeeded = 2;
                    MainActivity.mTopTabManager.getBottomTabManager().getSearchScreen();
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
            tab.getCustomView().findViewById(R.id.tab_item_text_view)
                    .setBackground(mActivity.getResources().getDrawable(R.drawable.tab_item_background));
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    }

    private class ActivityTabListener implements TabLayout.OnTabSelectedListener{

        private ActivityTabListener(){
            startFirstRetrieval();
        }

        private void startFirstRetrieval(){
            MainActivity.mAdapterNeeded = 6;
            new FirebaseRetrievalCalls(mActivity).getUserSwaps();
        }

        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            tab.getCustomView().findViewById(R.id.tab_item_text_view)
                    .setBackground(mActivity.getResources().getDrawable(R.drawable.tab_item_background_selected));
            switch(tab.getPosition()){
                case 0:
                    MainActivity.mAdapterNeeded = 6;
                    mActivity.getRecyclerView().setVisibility(View.VISIBLE);
                    mActivity.findViewById(R.id.alt_search_layout).setVisibility(View.GONE);
                    new FirebaseRetrievalCalls(mActivity).getUserSwaps();
                    break;
                case 1:
                    MainActivity.mAdapterNeeded = 7;
                    mActivity.getRecyclerView().setVisibility(View.VISIBLE);
                    mActivity.findViewById(R.id.alt_search_layout).setVisibility(View.GONE);
                    new FirebaseRetrievalCalls(mActivity).getUserPolicies();
                    break;
                case 2:
                    break;
                default:
                    break;
            }

        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
            tab.getCustomView().findViewById(R.id.tab_item_text_view)
                    .setBackground(mActivity.getResources().getDrawable(R.drawable.tab_item_background));

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    }

    private class LegislationTabListener implements TabLayout.OnTabSelectedListener{

        private LegislationTabListener(){
            startFirstRetrieval();
        }

        private void startFirstRetrieval(){
            MainActivity.mAdapterNeeded = 9;
            new BillResultsAsync(mActivity, mActivity.getBillOffset() * 20).execute();
        }

        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            tab.getCustomView().findViewById(R.id.tab_item_text_view)
                    .setBackground(mActivity.getResources().getDrawable(R.drawable.tab_item_background_selected));
            switch (tab.getPosition()){
                case 0:
                    MainActivity.mAdapterNeeded = 9;
                    new BillResultsAsync(mActivity, mActivity.getBillOffset() * 20).execute();
                    break;
                case 1:
                    MainActivity.mAdapterNeeded = 10;
                    setUpForSearch();
                    break;
                default:
                    break;
            }

        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
            tab.getCustomView().findViewById(R.id.tab_item_text_view)
                    .setBackground(mActivity.getResources().getDrawable(R.drawable.tab_item_background));
            mActivity.getRecyclerView().setVisibility(View.VISIBLE);
            mActivity.findViewById(R.id.alt_search_legislation_layout).setVisibility(View.GONE);
            ((InputMethodManager)mActivity.getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(mActivity.getRecyclerView().getWindowToken(), 0);
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {
            switch (tab.getPosition()){
                case 0:
                    break;
                case 1:
                    setUpForSearch();
                    break;
                default:
                    break;
            }
        }
    }

    private void setUpForSearch(){
        mActivity.getRecyclerView().setVisibility(View.GONE);
        mActivity.findViewById(R.id.alt_search_legislation_layout).setVisibility(View.VISIBLE);
        final EditText queryView = mActivity.findViewById(R.id.edit_search_text);
        ((InputMethodManager)mActivity.getSystemService(Context.INPUT_METHOD_SERVICE))
                .toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY);
        queryView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String query = queryView.getText().toString();
                    new SearchedBillsAsync(mActivity,query).execute();
                    ((ContentLoadingProgressBar)mActivity.findViewById(R.id.refreshing_bills_progress_bar)).show();
                    ((InputMethodManager)mActivity.getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(queryView.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
    }
}
