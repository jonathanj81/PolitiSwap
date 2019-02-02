package com.politiswap.politiswap.TabManagement;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import com.politiswap.politiswap.DataUtils.Tasks.FirebaseRetrievalCalls;
import com.politiswap.politiswap.MainActivity;
import com.politiswap.politiswap.R;
import com.politiswap.politiswap.UiAdapters.CreateSubjectAvailableAdapter;

public class BottomTabManager implements CreateSubjectAvailableAdapter.SubjectChangeManager {

    private MainActivity mActivity;

    private RecyclerView mSubjectAvailableRecycler;
    private CreateSubjectAvailableAdapter mSubjectAvailableAdapter;

    private RecyclerView mMainContentRecyclerView;
    private ContentLoadingProgressBar mProgressBar;
    private TabLayout mThreeTabs;
    private TabLayout mTwoTabs;

    private TabLayout.OnTabSelectedListener mTabSelectedListener;
    private TabListeners mTabListeners;

    BottomTabManager(MainActivity activity) {
        mActivity = activity;
        mMainContentRecyclerView = mActivity.getRecyclerView();
        mProgressBar = mActivity.findViewById(R.id.refreshing_bills_progress_bar);
        mThreeTabs = mActivity.findViewById(R.id.included_three_tab_layout);
        mTwoTabs = mActivity.findViewById(R.id.included_two_tab_layout);
        mTabListeners = new TabListeners(mActivity);
    }

    public void setBottomForSwaps(int startAt, boolean fromInstance) {
        resetTabs();
        setBottomTabStrings(mActivity.getResources().getString(R.string.bottom_swaps_rated),
                mActivity.getResources().getString(R.string.bottom_swaps_new),
                mActivity.getResources().getString(R.string.bottom_swaps_search));

        mActivity.findViewById(R.id.fab).setVisibility(View.VISIBLE);
        mTabSelectedListener = mTabListeners.getSwapsTabListener(fromInstance);
        mThreeTabs.addOnTabSelectedListener(mTabSelectedListener);
        mThreeTabs.getTabAt(startAt).select();
        mTabListeners.setInstance(false);
    }

    public void setBottomForPolicies(int startAt, boolean fromInstance) {
        resetTabs();
        setBottomTabStrings(mActivity.getResources().getString(R.string.bottom_policies_wanted),
                mActivity.getResources().getString(R.string.bottom_policies_new),
                mActivity.getResources().getString(R.string.bottom_policies_search));

        mActivity.findViewById(R.id.fab).setVisibility(View.VISIBLE);
        mTabSelectedListener = mTabListeners.getPolicyTabListener(fromInstance);
        mThreeTabs.addOnTabSelectedListener(mTabSelectedListener);
        mThreeTabs.getTabAt(startAt).select();
        mTabListeners.setInstance(false);
    }

    public void setBottomForMyActivity(int startAt, boolean fromInstance) {
        resetTabs();
        setBottomTabStrings(mActivity.getResources().getString(R.string.bottom_activity_swaps),
                mActivity.getResources().getString(R.string.bottom_activity_policies),
                mActivity.getResources().getString(R.string.bottom_activity_score));

        mActivity.findViewById(R.id.fab).setVisibility(View.GONE);
        mTabSelectedListener = mTabListeners.getActivityTabListener(fromInstance);
        mThreeTabs.addOnTabSelectedListener(mTabSelectedListener);
        mThreeTabs.getTabAt(startAt).select();
        mTabListeners.setInstance(false);
    }

    public void setBottomForLegislation(int startAt, boolean fromInstance) {
        resetTabs();
        setBottomTabStrings(mActivity.getResources().getString(R.string.bottom_legislation_recent),
                mActivity.getResources().getString(R.string.bottom_legislation_search));

        mActivity.findViewById(R.id.fab).setVisibility(View.GONE);
        mTabSelectedListener = mTabListeners.getLegislationTabListener(fromInstance);
        mTwoTabs.addOnTabSelectedListener(mTabSelectedListener);
        mTwoTabs.getTabAt(startAt).select();
        mTabListeners.setInstance(false);
    }

    public void getSearchScreen() {
        mProgressBar.hide();
        mMainContentRecyclerView.setVisibility(View.GONE);
        mActivity.findViewById(R.id.alt_search_layout).setVisibility(View.VISIBLE);
        mActivity.findViewById(R.id.craft_policy_step_number_1).setVisibility(View.GONE);
        mActivity.findViewById(R.id.craft_subject_recycler_and_placeholder_frame).setVisibility(View.GONE);
        mActivity.findViewById(R.id.alt_search_cancel_button).setVisibility(View.GONE);
        mActivity.findViewById(R.id.craft_subject_title_text_view).setVisibility(View.VISIBLE);
        TextView titleView = mActivity.findViewById(R.id.craft_subject_title_text_view);
        titleView.setText(mActivity.getResources().getString(R.string.search_alt_subject_hint));

        mSubjectAvailableRecycler = mActivity.findViewById(R.id.alt_search_included_layout)
                .findViewById(R.id.craft_subject_available_recycler);
        mSubjectAvailableRecycler.setVisibility(View.VISIBLE);
        mSubjectAvailableAdapter = new CreateSubjectAvailableAdapter(this, 2);
        mSubjectAvailableAdapter.setAll();
        mSubjectAvailableRecycler.setHasFixedSize(true);
        mSubjectAvailableRecycler.setLayoutManager(new LinearLayoutManager(mActivity,
                LinearLayoutManager.VERTICAL, false));
        mSubjectAvailableRecycler.setAdapter(mSubjectAvailableAdapter);
    }

    @Override
    public void addSubject(final String subject) {
        mActivity.findViewById(R.id.alt_search_layout).setVisibility(View.GONE);
        mProgressBar.show();
        MainActivity.mCurrentAreaSubject = subject;

        if (MainActivity.mAdapterNeeded == 2){
            new FirebaseRetrievalCalls(mActivity, false).getSwapAreaSearch(subject);
        } else {
            new FirebaseRetrievalCalls(mActivity, false).getPolicyAreaSearch(subject);
        }
    }

    @Override
    public void deleteSubject(String subject) {
    }

    private void resetTabs(){
        mTwoTabs.clearOnTabSelectedListeners();
        mThreeTabs.clearOnTabSelectedListeners();
        mMainContentRecyclerView.setVisibility(View.VISIBLE);
        mActivity.findViewById(R.id.alt_search_layout).setVisibility(View.GONE);
        mActivity.findViewById(R.id.alt_score_layout).setVisibility(View.GONE);
        mActivity.findViewById(R.id.adView).setVisibility(View.VISIBLE);
        mActivity.findViewById(R.id.ad_container).setVisibility(View.VISIBLE);
        mActivity.findViewById(R.id.user_id_view).setVisibility(View.VISIBLE);
        if (MainActivity.mAdapterNeeded == 10) {
            mActivity.findViewById(R.id.alt_search_legislation_layout).setVisibility(View.GONE);
            ((InputMethodManager)mActivity.getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(mActivity.getRecyclerView().getWindowToken(), 0);
        }
        mThreeTabs.getTabAt(0).select();
        mThreeTabs.getTabAt(0).getCustomView().findViewById(R.id.tab_item_text_view)
                .setBackground(mActivity.getResources().getDrawable(R.drawable.tab_item_background_selected));
        mThreeTabs.getTabAt(1).getCustomView().findViewById(R.id.tab_item_text_view)
                .setBackground(mActivity.getResources().getDrawable(R.drawable.tab_item_background));
        mThreeTabs.getTabAt(2).getCustomView().findViewById(R.id.tab_item_text_view)
                .setBackground(mActivity.getResources().getDrawable(R.drawable.tab_item_background));

        mTwoTabs.getTabAt(0).select();
        mTwoTabs.getTabAt(0).getCustomView().findViewById(R.id.tab_item_text_view)
                .setBackground(mActivity.getResources().getDrawable(R.drawable.tab_item_background_selected));
        mTwoTabs.getTabAt(1).getCustomView().findViewById(R.id.tab_item_text_view)
                .setBackground(mActivity.getResources().getDrawable(R.drawable.tab_item_background));
    }

    private void setBottomTabStrings(String... args){
        if (args.length == 3){
            ((TextView) mThreeTabs.getTabAt(0).getCustomView().findViewById(R.id.tab_item_text_view))
                    .setText(args[0]);
            ((TextView) mThreeTabs.getTabAt(1).getCustomView().findViewById(R.id.tab_item_text_view))
                    .setText(args[1]);
            ((TextView) mThreeTabs.getTabAt(2).getCustomView().findViewById(R.id.tab_item_text_view))
                    .setText(args[2]);
        } else {
            ((TextView) mTwoTabs.getTabAt(0).getCustomView().findViewById(R.id.tab_item_text_view))
                    .setText(args[0]);
            ((TextView) mTwoTabs.getTabAt(1).getCustomView().findViewById(R.id.tab_item_text_view))
                    .setText(args[1]);
        }
    }

    public TabLayout.OnTabSelectedListener getTabListener(){
        return mTabSelectedListener;
    }
}
