package com.example.jon.politiswap.TabManagement;

import android.app.Activity;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.jon.politiswap.DataUtils.Tasks.BillResultsAsync;
import com.example.jon.politiswap.MainActivity;
import com.example.jon.politiswap.R;

public class TopTabManager implements TabLayout.OnTabSelectedListener {

    private MainActivity mActivity;
    private BottomTabManager mBottomManager;
    private int mTabType = 0;

    public TopTabManager(MainActivity activity) {
        mActivity = activity;
        mBottomManager = new BottomTabManager(mActivity);
    }

    public void setTopTabs(int startAt, int startBottomAt) {
        TabLayout topTab = mActivity.findViewById(R.id.top_tab_layout);
        ((TextView) topTab.getTabAt(0).getCustomView().findViewById(R.id.tab_item_text_view))
                .setText(mActivity.getResources().getString(R.string.tab_swaps));
        ((TextView) topTab.getTabAt(1).getCustomView().findViewById(R.id.tab_item_text_view))
                .setText(mActivity.getResources().getString(R.string.tab_policies));
        ((TextView) topTab.getTabAt(2).getCustomView().findViewById(R.id.tab_item_text_view))
                .setText(mActivity.getResources().getString(R.string.tab_activity));
        ((TextView) topTab.getTabAt(3).getCustomView().findViewById(R.id.tab_item_text_view))
                .setText(mActivity.getResources().getString(R.string.tab_legislation));

        topTab.getTabAt(startAt).select();

        topTab.getTabAt(startAt).getCustomView().findViewById(R.id.tab_item_text_view)
                .setBackground(mActivity.getResources().getDrawable(R.drawable.tab_item_background_selected));

        switch (startAt){
            case 0:
                mBottomManager.setBottomForSwaps(startBottomAt);
                break;
            case 1:
                mBottomManager.setBottomForPolicies(startBottomAt);
                break;
            case 2:
                mBottomManager.setBottomForMyActivity(startBottomAt);
                break;
            case 3:
                mBottomManager.setBottomForLegislation(startBottomAt);
                mActivity.findViewById(R.id.included_three_tab_layout).setVisibility(View.GONE);
                mActivity.findViewById(R.id.included_two_tab_layout).setVisibility(View.VISIBLE);
                break;
        }
        topTab.clearOnTabSelectedListeners();
        topTab.addOnTabSelectedListener(this);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        tab.getCustomView().findViewById(R.id.tab_item_text_view)
                .setBackground(mActivity.getResources().getDrawable(R.drawable.tab_item_background_selected));
        int pos = tab.getPosition();
        mTabType = pos;

        switch (pos / 3) {
            case 1:
                mActivity.findViewById(R.id.included_three_tab_layout).setVisibility(View.GONE);
                mActivity.findViewById(R.id.included_two_tab_layout).setVisibility(View.VISIBLE);
                break;
            default:
                mActivity.findViewById(R.id.included_three_tab_layout).setVisibility(View.VISIBLE);
                mActivity.findViewById(R.id.included_two_tab_layout).setVisibility(View.GONE);
                break;
        }

        switch (pos) {
            case 0:
                mBottomManager.setBottomForSwaps(0);
                mActivity.findViewById(R.id.fab).setVisibility(View.VISIBLE);
                break;
            case 1:
                mBottomManager.setBottomForPolicies(0);
                mActivity.findViewById(R.id.fab).setVisibility(View.VISIBLE);
                break;
            case 2:
                mBottomManager.setBottomForMyActivity(0);
                mActivity.findViewById(R.id.fab).setVisibility(View.GONE);
                break;
            case 3:
                mBottomManager.setBottomForLegislation(0);
                mActivity.findViewById(R.id.fab).setVisibility(View.GONE);
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

    public int getTabType(){
        return mTabType;
    }

    public BottomTabManager getBottomTabManager(){
        return mBottomManager;
    }
}
