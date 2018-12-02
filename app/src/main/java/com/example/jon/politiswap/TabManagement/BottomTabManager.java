package com.example.jon.politiswap.TabManagement;

import android.support.design.widget.TabLayout;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.jon.politiswap.DataUtils.Tasks.BillResultsAsync;
import com.example.jon.politiswap.DataUtils.Tasks.FirebaseRetrievalCalls;
import com.example.jon.politiswap.MainActivity;
import com.example.jon.politiswap.R;
import com.example.jon.politiswap.UiAdapters.CreateSubjectAvailableAdapter;
import com.example.jon.politiswap.UiAdapters.SwapsAdapter;

public class BottomTabManager implements CreateSubjectAvailableAdapter.SubjectChangeManager, TabListeners.FirebaseReturns {

    private MainActivity mActivity;

    private RecyclerView mSubjectAvailableRecycler;
    private CreateSubjectAvailableAdapter mSubjectAvailableAdapter;

    private Button mGoButton;
    private Button mCancelButton;
    private FrameLayout mPlaceholderContainer;
    private TextView mTitleTextView;

    private RecyclerView mMainContentRecyclerView;
    private ContentLoadingProgressBar mProgressBar;
    private TabLayout mThreeTabs;
    private TabLayout mTwoTabs;

    private TabLayout.OnTabSelectedListener mTabSelectedListener;
    private TabListeners mTabListeners;

    BottomTabManager(MainActivity activity) {
        mActivity = activity;
        mMainContentRecyclerView = mActivity.getRecyclerView();
        mThreeTabs = mActivity.findViewById(R.id.included_three_tab_layout);
        mTwoTabs = mActivity.findViewById(R.id.included_two_tab_layout);
        mTabListeners = new TabListeners(mActivity,this);
    }

    void setBottomForSwaps() {
        resetTabs();
        setBottomTabStrings(mActivity.getResources().getString(R.string.bottom_swaps_rated),
                mActivity.getResources().getString(R.string.bottom_swaps_new),
                mActivity.getResources().getString(R.string.bottom_swaps_search));

        mThreeTabs.removeOnTabSelectedListener(mTabSelectedListener);
        mTabSelectedListener = mTabListeners.getSwapsTabListener();
        mThreeTabs.addOnTabSelectedListener(mTabSelectedListener);
        mMainContentRecyclerView.setAdapter(new SwapsAdapter());
    }

    void setBottomForPolicies() {
        resetTabs();
        setBottomTabStrings(mActivity.getResources().getString(R.string.bottom_policies_wanted),
                mActivity.getResources().getString(R.string.bottom_policies_new),
                mActivity.getResources().getString(R.string.bottom_policies_search));

        mThreeTabs.removeOnTabSelectedListener(mTabSelectedListener);
        mTabSelectedListener = mTabListeners.getPolicyTabListener();
        mThreeTabs.addOnTabSelectedListener(mTabSelectedListener);
    }

    void setBottomForMyActivity() {
        resetTabs();
        setBottomTabStrings(mActivity.getResources().getString(R.string.bottom_activity_swaps),
                mActivity.getResources().getString(R.string.bottom_activity_policies),
                mActivity.getResources().getString(R.string.bottom_activity_score));

        mThreeTabs.removeOnTabSelectedListener(mTabSelectedListener);
        mTabSelectedListener = mTabListeners.getActivityTabListener();
        mThreeTabs.addOnTabSelectedListener(mTabSelectedListener);
    }

    void setBottomForLegislation() {
        resetTabs();
        setBottomTabStrings(mActivity.getResources().getString(R.string.bottom_legislation_recent),
                mActivity.getResources().getString(R.string.bottom_legislation_search));

        mTwoTabs.removeOnTabSelectedListener(mTabSelectedListener);
        mTabSelectedListener = mTabListeners.getLegislationTabListener();
        mTwoTabs.addOnTabSelectedListener(mTabSelectedListener);

        new BillResultsAsync(mActivity, mActivity.getBillOffset() * 20).execute();
    }

    @Override
    public void getSearchScreen() {
        mMainContentRecyclerView.setVisibility(View.GONE);
        mActivity.findViewById(R.id.alt_search_layout).setVisibility(View.VISIBLE);
        mActivity.findViewById(R.id.craft_policy_step_number_1).setVisibility(View.GONE);
        mActivity.findViewById(R.id.craft_subject_recycler_and_placeholder_frame).setVisibility(View.GONE);
        TextView titleView = mActivity.findViewById(R.id.craft_subject_title_text_view);
        titleView.setText(mActivity.getResources().getString(R.string.search_alt_subject_hint));

        mSubjectAvailableRecycler = mActivity.findViewById(R.id.alt_search_included_layout)
                .findViewById(R.id.craft_subject_available_recycler);
        mSubjectAvailableRecycler.setVisibility(View.VISIBLE);
        mSubjectAvailableAdapter = new CreateSubjectAvailableAdapter(this, 0);
        mSubjectAvailableAdapter.setAll();
        mSubjectAvailableRecycler.setHasFixedSize(true);
        mSubjectAvailableRecycler.setLayoutManager(new LinearLayoutManager(mActivity,
                LinearLayoutManager.VERTICAL, false));
        mSubjectAvailableRecycler.setAdapter(mSubjectAvailableAdapter);
    }

    @Override
    public void addSubject(final String subject) {
        mSubjectAvailableRecycler.setVisibility(View.GONE);

        mTitleTextView = mActivity.findViewById(R.id.craft_subject_title_text_view);
        mTitleTextView.setVisibility(View.GONE);

        mCancelButton = mActivity.findViewById(R.id.alt_search_cancel_button);
        mGoButton = mActivity.findViewById(R.id.alt_search_go_button);
        mCancelButton.setVisibility(View.VISIBLE);
        mCancelButton.setText(mActivity.getResources().getString(R.string.craft_policy_close_cancel));
        mGoButton.setVisibility(View.VISIBLE);
        mGoButton.setText(String.format(mActivity.getResources().getString(R.string.search_alt_go_button), subject));

        final FirebaseRetrievalCalls caller = new FirebaseRetrievalCalls(mActivity);
        mGoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.findViewById(R.id.alt_search_layout).setVisibility(View.GONE);
                mProgressBar = mActivity.findViewById(R.id.refreshing_bills_progress_bar);
                mProgressBar.show();
                mTitleTextView.setVisibility(View.GONE);
                mGoButton.setVisibility(View.GONE);
                mCancelButton.setVisibility(View.GONE);
                caller.getPolicyAreaSearch(subject);
            }
        });

        mCancelButton.setVisibility(View.VISIBLE);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGoButton.setVisibility(View.GONE);
                mCancelButton.setVisibility(View.GONE);
                mSubjectAvailableRecycler.setVisibility(View.VISIBLE);
                mTitleTextView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void deleteSubject(String subject) {
    }

    private void resetTabs(){
        mMainContentRecyclerView.setVisibility(View.VISIBLE);
        mActivity.findViewById(R.id.alt_search_layout).setVisibility(View.GONE);
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
}
