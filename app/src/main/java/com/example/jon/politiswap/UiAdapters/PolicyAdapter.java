package com.example.jon.politiswap.UiAdapters;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.jon.politiswap.DataUtils.Policy;
import com.example.jon.politiswap.DataUtils.Recent.RecentBills;
import com.example.jon.politiswap.DialogFragments.BillDialogFragment;
import com.example.jon.politiswap.DialogFragments.CreatePolicyFragment;
import com.example.jon.politiswap.DialogFragments.FragmentArgs;
import com.example.jon.politiswap.DialogFragments.PolicyDetailFragment;
import com.example.jon.politiswap.MainActivity;
import com.example.jon.politiswap.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PolicyAdapter extends RecyclerView.Adapter<PolicyAdapter.PolicyViewHolder> {

    private List<Policy> mPolicies = new ArrayList<>();
    private Context mContext;

    private static final String POLICY_FRAGMENT_NAME = "policy_frag";

    public PolicyAdapter(){
    }

    @NonNull
    @Override
    public PolicyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();

        View layoutView = LayoutInflater.from(mContext).inflate(R.layout.policy_layout_card_view, viewGroup,false);
        return new PolicyViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull final PolicyViewHolder holder, int i) {
        final Policy policy = mPolicies.get(i);
        String subjects = policy.getSubjects().toString();
        final String party = policy.getParty();
        final int wanted = policy.getNetWanted();
        holder.mCreatorView.setText(policy.getCreator());
        holder.mTitleView.setText(policy.getTitle());
        holder.mCountView.setText(String.format(mContext.getResources().getString(R.string.policy_net_wanted),
                wanted, party));
        holder.mSubjectView.setText(subjects.substring(1,subjects.length()-1));
        holder.mSummaryView.setText(policy.getSummary());

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.valueOf(policy.getTimeStamp()));
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        holder.mDateView.setText(String.valueOf(month+1) + "/" + String.valueOf(day) + "/" + String.valueOf(year));

        if (party.equals("Democrat")){
            holder.mContainer.setBackground(mContext.getResources().getDrawable(R.drawable.dem_swap_background));
        } else {
            holder.mContainer.setBackground(mContext.getResources().getDrawable(R.drawable.rep_swap_background));
        }

        holder.mContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentArgs.POLICY_DETAIL_NET_WANTED = wanted;
                FragmentArgs.POLICY_DETAIL_CREATOR = holder.mCreatorView.getText().toString();
                FragmentArgs.POLICY_DETAIL_DATE = holder.mDateView.getText().toString();
                FragmentArgs.POLICY_DETAIL_PARTY = party;
                FragmentArgs.POLICY_DETAIL_TITLE = holder.mTitleView.getText().toString();
                FragmentArgs.POLICY_DETAIL_SUBJECTS = holder.mSubjectView.getText().toString();
                FragmentArgs.POLICY_DETAIL_SUMMARY = holder.mSummaryView.getText().toString();
                FragmentArgs.POLICY_LONG_ID = policy.getLongID();

                FragmentManager fm = ((MainActivity)v.getContext()).getSupportFragmentManager();
                PolicyDetailFragment frag = PolicyDetailFragment.newInstance(null);
                frag.show(fm, POLICY_FRAGMENT_NAME);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (null == mPolicies) return 0;
        return mPolicies.size();
    }

    public void setPolicies(List<Policy> policies) {
        mPolicies = policies;
        notifyDataSetChanged();
    }

    public class PolicyViewHolder extends RecyclerView.ViewHolder{

        final TextView mCreatorView;
        final TextView mTitleView;
        final TextView mSubjectView;
        final TextView mSummaryView;
        final TextView mCountView;
        final TextView mDateView;
        final FrameLayout mContainer;

        public PolicyViewHolder(@NonNull View view) {
            super(view);
            mCreatorView = view.findViewById(R.id.swap_first_creator_name);
            mTitleView = view.findViewById(R.id.swap_first_title_line);
            mSubjectView = view.findViewById(R.id.swap_first_subject_line);
            mSummaryView = view.findViewById(R.id.swap_first_summary_line);
            mCountView = view.findViewById(R.id.swap_first_thumbs_up_count);
            mContainer = view.findViewById(R.id.swap_first_included_container);
            mDateView = view.findViewById(R.id.swap_first_date);
        }
    }
}
