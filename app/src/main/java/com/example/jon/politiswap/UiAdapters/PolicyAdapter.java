package com.example.jon.politiswap.UiAdapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jon.politiswap.DataUtils.Policy;
import com.example.jon.politiswap.DialogFragments.FragmentArgs;
import com.example.jon.politiswap.DialogFragments.PolicyDetailFragment;
import com.example.jon.politiswap.MainActivity;
import com.example.jon.politiswap.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PolicyAdapter extends RecyclerView.Adapter<PolicyAdapter.PolicyViewHolder> {

    private List<Policy> mPolicies = new ArrayList<>();
    private Context mContext;
    private int mModifier = 0;
    private int mType = 0;
    private int mPreviousAdPosition = 0;
    private OnBottomReachedListener onBottomReachedListener;

    private static final String POLICY_FRAGMENT_NAME = "policy_frag";
    private static final int CONTENT_VIEW = 0;
    private static final int AD_VIEW = 1;

    public PolicyAdapter(OnBottomReachedListener onBottomReachedListener) {
        this.onBottomReachedListener = onBottomReachedListener;
    }

    public PolicyAdapter(int type, OnBottomReachedListener onBottomReachedListener) {
        mType = type;
        this.onBottomReachedListener = onBottomReachedListener;
    }

    @NonNull
    @Override
    public PolicyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();
        View layoutView = LayoutInflater.from(mContext).inflate(R.layout.policy_layout_card_view, viewGroup, false);

        return new PolicyViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull final PolicyViewHolder holder, int i) {
        if (i == 0 && MainActivity.mAdapterNeeded == 5) {
            mModifier = 1;
            holder.mContainer.setVisibility(View.GONE);
            holder.mRefreshButton.setVisibility(View.VISIBLE);
            holder.mRefreshButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainActivity.mTopTabManager.getBottomTabManager().getSearchScreen();
                }
            });
        } else {
            holder.mContainer.setVisibility(View.VISIBLE);
            holder.mRefreshButton.setVisibility(View.GONE);
            final Policy policy = mPolicies.get(i - mModifier);
            String subjects = policy.getSubjects().toString();
            final String party = policy.getParty();
            final int wanted = policy.getNetWanted();
            holder.mCreatorView.setText(policy.getCreator());
            holder.mTitleView.setText(policy.getTitle());
            holder.mCountView.setText(String.format(mContext.getResources().getString(R.string.policy_net_wanted),
                    wanted, party));
            holder.mSubjectView.setText(subjects.substring(1, subjects.length() - 1));
            holder.mSummaryView.setText(policy.getSummary());

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.valueOf(policy.getTimeStamp()));
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            holder.mDateView.setText(String.valueOf(month + 1) + "/" + String.valueOf(day) + "/" + String.valueOf(year));

            if (party.equals("Democrat")) {
                holder.mContainer.setBackground(mContext.getResources().getDrawable(R.drawable.dem_swap_background));
            } else {
                holder.mContainer.setBackground(mContext.getResources().getDrawable(R.drawable.rep_swap_background));
            }

            if (MainActivity.mUserCreated.contains(policy.getLongID())) {
                holder.mCreatedIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_document_svg_black));
            } else {
                holder.mCreatedIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_document_svg_gray));
            }
            if (MainActivity.mUserVoted.contains(policy.getLongID())) {
                holder.mVotedIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_check_box_black));
            } else {
                holder.mVotedIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_check_box_gray));
            }

            holder.mContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mType == 1) {
                        MainActivity.mSwapFrag.refreshPolicyViews(policy);
                    } else {
                        FragmentArgs.POLICY_DETAIL_NET_WANTED = wanted;
                        FragmentArgs.POLICY_DETAIL_CREATOR = holder.mCreatorView.getText().toString();
                        FragmentArgs.POLICY_DETAIL_DATE = holder.mDateView.getText().toString();
                        FragmentArgs.POLICY_DETAIL_PARTY = party;
                        FragmentArgs.POLICY_DETAIL_TITLE = holder.mTitleView.getText().toString();
                        FragmentArgs.POLICY_DETAIL_SUBJECTS = holder.mSubjectView.getText().toString();
                        FragmentArgs.POLICY_DETAIL_SUMMARY = holder.mSummaryView.getText().toString();
                        FragmentArgs.POLICY_LONG_ID = policy.getLongID();

                        FragmentArgs.MAIN_RECYCLER_STATE = ((MainActivity) v.getContext()).getRecyclerView().getLayoutManager().onSaveInstanceState();
                        FragmentManager fm = ((MainActivity) v.getContext()).getSupportFragmentManager();
                        PolicyDetailFragment frag = PolicyDetailFragment.newInstance(null);
                        frag.show(fm, POLICY_FRAGMENT_NAME);
                    }
                }
            });
        }
        if (i == getItemCount() - 1 && !MainActivity.isAtEnd) {
            onBottomReachedListener.onBottomReached();
        }
    }

    @Override
    public int getItemCount() {
        if (null == mPolicies || mPolicies.size() == 0) return 0;
        int numItems = mPolicies.size();
        if (MainActivity.mAdapterNeeded == 5) return (numItems + 1);
        return numItems;
    }

    public void setPolicies(List<Policy> policies, boolean fromScroll) {
        if (mType == 0) {
            MainActivity.isAtEnd = policies.size() < 20;
        }

        if (fromScroll && policies.size() > 0) {
            policies.remove(0);
            mPolicies.addAll(policies);
        } else {
            mPolicies = policies;
            mModifier = 0;
        }

        if (policies.size() > 0) {
            MainActivity.mLastFirebaseNode = mPolicies.get(mPolicies.size() - 1).getLongID();
        }

        notifyDataSetChanged();
    }

    public List<Policy> getPolicies(){
        return mPolicies;
    }

    public class PolicyViewHolder extends RecyclerView.ViewHolder {

        final TextView mCreatorView;
        final TextView mTitleView;
        final TextView mSubjectView;
        final TextView mSummaryView;
        final TextView mCountView;
        final TextView mDateView;
        final FrameLayout mContainer;
        final ImageView mCreatedIcon;
        final ImageView mVotedIcon;
        final ImageButton mRefreshButton;

        public PolicyViewHolder(@NonNull View view) {
            super(view);
            mCreatorView = view.findViewById(R.id.swap_first_creator_name);
            mTitleView = view.findViewById(R.id.swap_first_title_line);
            mSubjectView = view.findViewById(R.id.swap_first_subject_line);
            mSummaryView = view.findViewById(R.id.swap_first_summary_line);
            mCountView = view.findViewById(R.id.swap_first_thumbs_up_count);
            mContainer = view.findViewById(R.id.swap_first_included_container);
            mDateView = view.findViewById(R.id.swap_first_date);
            mCreatedIcon = view.findViewById(R.id.swap_first_created_icon);
            mVotedIcon = view.findViewById(R.id.swap_first_voted_icon);
            mRefreshButton = view.findViewById(R.id.policy_layout_refresh_button);
        }
    }
}
