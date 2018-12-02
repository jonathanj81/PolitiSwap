package com.example.jon.politiswap.TabManagement;

import android.support.design.widget.TabLayout;
import android.view.View;

import com.example.jon.politiswap.DataUtils.Policy;
import com.example.jon.politiswap.DataUtils.Tasks.FirebaseRetrievalCalls;
import com.example.jon.politiswap.MainActivity;
import com.example.jon.politiswap.R;

import java.util.List;

public class TabListeners {

    private MainActivity mActivity;
    private FirebaseReturns mFirebaseReturns;

    public TabListeners(MainActivity activity, FirebaseReturns returner){
        mActivity = activity;
        mFirebaseReturns = returner;
    }

    public interface FirebaseReturns{
        void getSearchScreen();
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
            new FirebaseRetrievalCalls(mActivity).getTopPolicies();
        }

        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            tab.getCustomView().findViewById(R.id.tab_item_text_view)
                    .setBackground(mActivity.getResources().getDrawable(R.drawable.tab_item_background_selected));
            switch (tab.getPosition()) {
                case 0:
                    mActivity.getRecyclerView().setVisibility(View.VISIBLE);
                    mActivity.findViewById(R.id.alt_search_layout).setVisibility(View.GONE);
                    new FirebaseRetrievalCalls(mActivity).getTopPolicies();
                    break;
                case 1:
                    mActivity.getRecyclerView().setVisibility(View.VISIBLE);
                    mActivity.findViewById(R.id.alt_search_layout).setVisibility(View.GONE);
                    new FirebaseRetrievalCalls(mActivity).getNewPolices();
                    break;
                case 2:
                    mFirebaseReturns.getSearchScreen();
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

        private SwapsTabListener(){}

        @Override
        public void onTabSelected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    }

    private class ActivityTabListener implements TabLayout.OnTabSelectedListener{

        private ActivityTabListener(){}

        @Override
        public void onTabSelected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    }

    private class LegislationTabListener implements TabLayout.OnTabSelectedListener{

        private LegislationTabListener(){}

        @Override
        public void onTabSelected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    }
}
