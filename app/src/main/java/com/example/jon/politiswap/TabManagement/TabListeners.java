package com.example.jon.politiswap.TabManagement;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.jon.politiswap.DataUtils.Tasks.BillResultsAsync;
import com.example.jon.politiswap.DataUtils.Tasks.FirebaseRetrievalCalls;
import com.example.jon.politiswap.DataUtils.Tasks.SearchedBillsAsync;
import com.example.jon.politiswap.MainActivity;
import com.example.jon.politiswap.R;

public class TabListeners {

    private MainActivity mActivity;
    private ContentLoadingProgressBar mProgressBar;
    private boolean mFromInstance;

    public TabListeners(MainActivity activity){
        mActivity = activity;
        mProgressBar = mActivity.findViewById(R.id.refreshing_bills_progress_bar);
    }

    public PolicyTabListener getPolicyTabListener(boolean fromInstance){
        mFromInstance = fromInstance;
        return new PolicyTabListener();
    }
    public SwapsTabListener getSwapsTabListener(boolean fromInstance){
        mFromInstance = fromInstance;
        return new SwapsTabListener();
    }
    public ActivityTabListener getActivityTabListener(boolean fromInstance){
        mFromInstance = fromInstance;
        return new ActivityTabListener();
    }
    public LegislationTabListener getLegislationTabListener(boolean fromInstance){
        mFromInstance = fromInstance;
        return new LegislationTabListener();
    }

    private class PolicyTabListener implements TabLayout.OnTabSelectedListener {

        private PolicyTabListener(){
            startFirstRetrieval();
        }

        private void startFirstRetrieval(){
            mProgressBar.show();
            MainActivity.mLastFirebaseNode = "";
            MainActivity.mBillOffset = 0;
            MainActivity.mAdapterNeeded = 3;
            if (!mFromInstance) {
                new FirebaseRetrievalCalls(mActivity, false).getTopPolicies();
            }
        }

        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            mProgressBar.show();
            MainActivity.mLastFirebaseNode = "";
            tab.getCustomView().findViewById(R.id.tab_item_text_view)
                    .setBackground(mActivity.getResources().getDrawable(R.drawable.tab_item_background_selected));
            switch (tab.getPosition()) {
                case 0:
                    MainActivity.mAdapterNeeded = 3;
                    mActivity.getRecyclerView().setVisibility(View.VISIBLE);
                    mActivity.findViewById(R.id.alt_search_layout).setVisibility(View.GONE);
                    if (!mFromInstance) {
                        new FirebaseRetrievalCalls(mActivity, false).getTopPolicies();
                    } else {
                        mFromInstance = false;
                    }
                    break;
                case 1:
                    MainActivity.mAdapterNeeded = 4;
                    mActivity.getRecyclerView().setVisibility(View.VISIBLE);
                    mActivity.findViewById(R.id.alt_search_layout).setVisibility(View.GONE);
                    if (!mFromInstance) {
                        new FirebaseRetrievalCalls(mActivity, false).getNewPolicies();
                    } else {
                        mFromInstance = false;
                    }
                    break;
                case 2:
                    MainActivity.mAdapterNeeded = 5;
                    MainActivity.mTopTabManager.getBottomTabManager().getSearchScreen();
                    if (mFromInstance){
                        mFromInstance = false;
                    }
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
            mProgressBar.show();
            MainActivity.mLastFirebaseNode = "";
            MainActivity.mBillOffset = 0;
            MainActivity.mAdapterNeeded = 0;
            if (!mFromInstance) {
                new FirebaseRetrievalCalls(mActivity, false).getTopSwaps();
            }
        }

        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            mProgressBar.show();
            MainActivity.mLastFirebaseNode = "";
            tab.getCustomView().findViewById(R.id.tab_item_text_view)
                    .setBackground(mActivity.getResources().getDrawable(R.drawable.tab_item_background_selected));
            switch (tab.getPosition()) {
                case 0:
                    MainActivity.mAdapterNeeded = 0;
                    mActivity.getRecyclerView().setVisibility(View.VISIBLE);
                    mActivity.findViewById(R.id.alt_search_layout).setVisibility(View.GONE);
                    if (!mFromInstance) {
                        new FirebaseRetrievalCalls(mActivity, false).getTopSwaps();
                    } else {
                        mFromInstance = false;
                    }
                    break;
                case 1:
                    MainActivity.mAdapterNeeded = 1;
                    mActivity.getRecyclerView().setVisibility(View.VISIBLE);
                    mActivity.findViewById(R.id.alt_search_layout).setVisibility(View.GONE);
                    if (!mFromInstance) {
                        new FirebaseRetrievalCalls(mActivity, false).getNewSwaps();
                    } else {
                        mFromInstance = false;
                    }
                    break;
                case 2:
                    MainActivity.mAdapterNeeded = 2;
                    MainActivity.mTopTabManager.getBottomTabManager().getSearchScreen();
                    if (mFromInstance){
                        mFromInstance = false;
                    }
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
            mProgressBar.show();
            MainActivity.mLastFirebaseNode = "";
            MainActivity.mBillOffset = 0;
            MainActivity.mAdapterNeeded = 6;
            if (!mFromInstance) {
                new FirebaseRetrievalCalls(mActivity, false).getUserSwaps();
            }
        }

        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            mProgressBar.show();
            MainActivity.mLastFirebaseNode = "";
            tab.getCustomView().findViewById(R.id.tab_item_text_view)
                    .setBackground(mActivity.getResources().getDrawable(R.drawable.tab_item_background_selected));
            switch(tab.getPosition()){
                case 0:
                    MainActivity.mAdapterNeeded = 6;
                    mActivity.getRecyclerView().setVisibility(View.VISIBLE);
                    mActivity.findViewById(R.id.alt_search_layout).setVisibility(View.GONE);
                    mActivity.findViewById(R.id.alt_score_layout).setVisibility(View.GONE);
                    if (!mFromInstance) {
                        new FirebaseRetrievalCalls(mActivity, false).getUserSwaps();
                    } else {
                        mFromInstance = false;
                    }
                    break;
                case 1:
                    MainActivity.mAdapterNeeded = 7;
                    mActivity.getRecyclerView().setVisibility(View.VISIBLE);
                    mActivity.findViewById(R.id.alt_search_layout).setVisibility(View.GONE);
                    mActivity.findViewById(R.id.alt_score_layout).setVisibility(View.GONE);
                    if (!mFromInstance) {
                        new FirebaseRetrievalCalls(mActivity, false).getUserPolicies();
                    } else {
                        mFromInstance = false;
                    }
                    break;
                case 2:
                    MainActivity.mAdapterNeeded = 8;
                    mProgressBar.hide();
                    showScores();
                    if (mFromInstance){
                        mFromInstance = false;
                    }
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

        private void showScores(){
            mActivity.findViewById(R.id.alt_score_layout).setVisibility(View.VISIBLE);
            mActivity.getRecyclerView().setVisibility(View.GONE);
            ((TextView)mActivity.findViewById(R.id.user_overall_score_view))
                    .setText(String.format(mActivity.getResources().getString(R.string.user_score_overall),String.valueOf(MainActivity.USER_OVERALL_POINTS)));
            ((TextView)mActivity.findViewById(R.id.user_swaps_points_view))
                    .setText(String.format(mActivity.getResources().getString(R.string.user_score_swap_creation),String.valueOf(MainActivity.USER_SWAP_POINTS)));
            ((TextView)mActivity.findViewById(R.id.user_policies_points_view))
                    .setText(String.format(mActivity.getResources().getString(R.string.user_score_policy_creation),String.valueOf(MainActivity.USER_POLICY_POINTS)));
            ((TextView)mActivity.findViewById(R.id.user_votes_points_view))
                    .setText(String.format(mActivity.getResources().getString(R.string.user_score_votes),String.valueOf(MainActivity.USER_VOTE_POINTS)));
        }
    }

    private class LegislationTabListener implements TabLayout.OnTabSelectedListener{

        private LegislationTabListener(){
            startFirstRetrieval();
        }

        private void startFirstRetrieval(){
            mProgressBar.show();
            MainActivity.mLastFirebaseNode = "";
            MainActivity.mAdapterNeeded = 9;
            if (!mFromInstance) {
                new BillResultsAsync(mActivity, 0).execute();
            }
        }

        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            MainActivity.mBillOffset = 0;
            tab.getCustomView().findViewById(R.id.tab_item_text_view)
                    .setBackground(mActivity.getResources().getDrawable(R.drawable.tab_item_background_selected));
            switch (tab.getPosition()){
                case 0:
                    mProgressBar.show();
                    MainActivity.mAdapterNeeded = 9;
                    if (!mFromInstance) {
                        new BillResultsAsync(mActivity, 0).execute();
                    } else {
                        mFromInstance = false;
                    }
                    break;
                case 1:
                    MainActivity.mAdapterNeeded = 10;
                    mActivity.findViewById(R.id.adView).setVisibility(View.GONE);
                    mActivity.findViewById(R.id.user_id_view).setVisibility(View.GONE);
                    if (!mFromInstance) {
                        setUpForSearch();
                    } else {
                        mFromInstance = false;
                    }
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

            if (tab.getPosition() == 1){
                mActivity.findViewById(R.id.adView).setVisibility(View.VISIBLE);
                mActivity.findViewById(R.id.user_id_view).setVisibility(View.VISIBLE);
            }
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
        mProgressBar.hide();
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
                    MainActivity.mLegislationQuery = query;
                    new SearchedBillsAsync(mActivity,query, 0).execute();
                    ((ContentLoadingProgressBar)mActivity.findViewById(R.id.refreshing_bills_progress_bar)).show();
                    ((InputMethodManager)mActivity.getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(queryView.getWindowToken(), 0);
                    mActivity.findViewById(R.id.adView).setVisibility(View.VISIBLE);
                    mActivity.findViewById(R.id.user_id_view).setVisibility(View.VISIBLE);
                    return true;
                }
                return false;
            }
        });
    }

    public void setInstance(boolean currentInstance){
        mFromInstance = currentInstance;
    }
}
