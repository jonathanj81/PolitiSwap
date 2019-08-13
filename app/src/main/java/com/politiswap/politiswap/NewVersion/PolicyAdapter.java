package com.politiswap.politiswap.NewVersion;

import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.politiswap.politiswap.DataUtils.Policy;
import com.politiswap.politiswap.MainActivity;
import com.politiswap.politiswap.R;
import com.politiswap.politiswap.UiAdapters.OnBottomReachedListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PolicyAdapter extends RecyclerView.Adapter<PolicyAdapter.PolicyViewHolder> {

    private Context mContext;
    private List<Policy> mPolicies = new ArrayList<>();
    private OnBottomReachedListener onBottomReachedListener;

    public PolicyAdapter(OnBottomReachedListener onBottomReachedListener) {
        this.onBottomReachedListener = onBottomReachedListener;
    }

    public PolicyAdapter(int type, OnBottomReachedListener onBottomReachedListener) {
        //mType = type;
        this.onBottomReachedListener = onBottomReachedListener;
    }

    @NonNull
    @Override
    public PolicyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View layoutView = LayoutInflater.from(mContext).inflate(R.layout.policy_layout, parent, false);
        return new PolicyViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull PolicyViewHolder holder, int position) {
        final Policy policy = mPolicies.get(position);
        final String creator = policy.getCreator();
        final String score = String.valueOf(policy.getNetWanted());
        final String title = policy.getTitle();
        final String content = policy.getSummary();
        final String party = policy.getParty();
        String subjects = policy.getSubjects().toString();
        subjects = subjects.substring(1,subjects.length()-1);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.valueOf(policy.getTimeStamp()));
        String year = String.valueOf(calendar.get(Calendar.YEAR));
        String month = String.valueOf(calendar.get(Calendar.MONTH)+1);
        String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));

        holder.mRatingView.setText(score);
        holder.mCreatorView.setText(String
                .format(mContext.getResources().getString(R.string.policy_creator_string),creator));
        holder.mTitleView.setText(title);
        holder.mContentView.setText(content + "\n\n" + String
                .format(mContext.getResources().getString(R.string.policy_content_subjects),subjects));
        holder.mTimeView.setText(month + "/" + day + "/" + year);

        if (party.equals("Democrat")){
            holder.mLineView.setBackground(mContext.getResources().getDrawable(R.color.colorFullBlueOpacity));
            holder.mContentLayout.setBackground(mContext.getResources().getDrawable(R.drawable.policy_card_background_content_dem));
            holder.mVoteButton.setBackground(mContext.getResources().getDrawable(R.drawable.small_button_background_dem));
        } else {
            holder.mLineView.setBackground(mContext.getResources().getDrawable(R.color.colorFullRedOpacity));
            holder.mContentLayout.setBackground(mContext.getResources().getDrawable(R.drawable.policy_card_background_content));
            holder.mVoteButton.setBackground(mContext.getResources().getDrawable(R.drawable.small_button_background));
        }
    }

    @Override
    public int getItemCount() {
        if (null == mPolicies || mPolicies.size() == 0) return 0;
        else return mPolicies.size();
    }

    public void setPolicies(List<Policy> policies, boolean fromScroll) {

        if (fromScroll && policies.size() > 0) {
            policies.remove(0);
            mPolicies.addAll(policies);
        } else {
            mPolicies = policies;
        }

        notifyDataSetChanged();
    }

    protected class PolicyViewHolder extends RecyclerView.ViewHolder {

        final TextView mCreatorView;
        final TextView mRatingView;
        final TextView mTitleView;
        final TextView mContentView;
        final TextView mTimeView;
        final View mLineView;
        final FrameLayout mContentLayout;
        final Button mVoteButton;

        protected PolicyViewHolder(@NonNull View view) {
            super(view);
            mCreatorView = view.findViewById(R.id.policy_creator_text_view);
            mRatingView = view.findViewById(R.id.policy_rating_text_view);
            mTitleView = view.findViewById(R.id.policy_content_text_title);
            mContentView = view.findViewById(R.id.policy_content_text_description);
            mTimeView = view.findViewById(R.id.policy_creator_timestamp_text_view);
            mLineView = view.findViewById(R.id.policy_content_separator_line);
            mContentLayout = view.findViewById(R.id.policy_content_container);
            mVoteButton = view.findViewById(R.id.policy_vote_button);
        }
    }
}
