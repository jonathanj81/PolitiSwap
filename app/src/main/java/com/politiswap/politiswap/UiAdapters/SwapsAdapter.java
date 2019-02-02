package com.politiswap.politiswap.UiAdapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.politiswap.politiswap.DataUtils.Policy;
import com.politiswap.politiswap.DataUtils.Swap;
import com.politiswap.politiswap.DialogFragments.FragmentArgs;
import com.politiswap.politiswap.DialogFragments.SwapDetailFragment;
import com.politiswap.politiswap.MainActivity;
import com.politiswap.politiswap.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SwapsAdapter extends RecyclerView.Adapter<SwapsAdapter.SwapsViewHolder> {

    private List<Swap> mSwaps = new ArrayList<>();
    private SparseArray<List<Policy>> mPolicyPairs = new SparseArray<>();
    private Context mContext;
    private DatabaseReference mDatabaseReference;
    private int mModifier = 0;
    private OnBottomReachedListener onBottomReachedListener;

    private static final String SWAP_FRAGMENT_NAME = "swap_frag";

    public SwapsAdapter(OnBottomReachedListener onBottomReachedListener) {
        this.onBottomReachedListener = onBottomReachedListener;
    }

    @NonNull
    @Override
    public SwapsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Policies");

        View layoutView = LayoutInflater.from(mContext).inflate(R.layout.swap_layout_card_view, viewGroup, false);
        return new SwapsViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull final SwapsViewHolder holder, int i) {
        if (i == 0 && MainActivity.mAdapterNeeded == 2) {
            mModifier = 1;
            holder.mMainLayout.setVisibility(View.GONE);
            holder.mRefreshButton.setVisibility(View.VISIBLE);
            holder.mRefreshButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainActivity.mTopTabManager.getBottomTabManager().getSearchScreen();
                }
            });
        } else {
            final int position = i - mModifier;
            Swap swap = mSwaps.get(position);
            holder.mRatingView.setText(String.format(mContext.getResources().getString(R.string.craft_swap_rating),
                    String.valueOf(swap.getRating())));
            holder.mProposedByView.setText(String.format(mContext.getResources().getString(R.string.craft_swap_proposer),
                    swap.getCreator(), getDate(Long.valueOf(swap.getTimestamp()))));
            holder.mProposedByView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            holder.mProposedByView.setSingleLine(true);
            holder.mProposedByView.setMarqueeRepeatLimit(-1);
            holder.mProposedByView.setSelected(true);

            final boolean topIsDems = swap.getPartyOnTop().equals("Democrat");

            if (topIsDems) {
                holder.mTopFrame.setBackground(mContext.getResources().getDrawable(R.drawable.dem_swap_background));
                holder.mBottomFrame.setBackground(mContext.getResources().getDrawable(R.drawable.rep_swap_background));
            } else {
                holder.mTopFrame.setBackground(mContext.getResources().getDrawable(R.drawable.rep_swap_background));
                holder.mBottomFrame.setBackground(mContext.getResources().getDrawable(R.drawable.dem_swap_background));
            }

            if (MainActivity.mUserSwapCreated.contains(swap.getLongID())) {
                holder.mCreatedIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_document_svg_black));
            } else {
                holder.mCreatedIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_document_svg_gray));
            }
            if (MainActivity.mUserSwapVoted.contains(swap.getLongID())) {
                holder.mVotedIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_check_box_black));
            } else {
                holder.mVotedIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_check_box_gray));
            }

            final List<Policy> tempList = new ArrayList<>();

            mDatabaseReference.child(swap.getDemPolicyID()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Policy policy = dataSnapshot.getValue(Policy.class);
                    tempList.add(policy);
                    ConstraintLayout layout;
                    if (topIsDems) {
                        layout = holder.mTopLayout;
                    } else {
                        layout = holder.mBottomLayout;
                    }
                    fillPolicyLayout(layout, policy);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            mDatabaseReference.child(swap.getRepPolicyID()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Policy policy = dataSnapshot.getValue(Policy.class);
                    tempList.add(policy);
                    ConstraintLayout layout;
                    if (topIsDems) {
                        layout = holder.mBottomLayout;
                    } else {
                        layout = holder.mTopLayout;
                    }
                    fillPolicyLayout(layout, policy);
                    mPolicyPairs.append(position, tempList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            if (i == mSwaps.size() - 1 && !MainActivity.isAtEnd) {
                onBottomReachedListener.onBottomReached();
            }
        }
    }

    @Override
    public int getItemCount() {
        if (null == mSwaps) return 0;
        if (MainActivity.mAdapterNeeded == 2) return (mSwaps.size() + 1);
        return mSwaps.size();
    }

    public void setSwaps(List<Swap> swaps, boolean fromScroll) {
        MainActivity.isAtEnd = swaps.size() < 20;

        if (fromScroll) {
            swaps.remove(0);
            mSwaps.addAll(swaps);
        } else {
            mSwaps = swaps;
        }
        mModifier = 0;
        if (swaps.size() > 0) {
            MainActivity.mLastFirebaseNode = mSwaps.get(mSwaps.size() - 1).getLongID();
        }
        notifyDataSetChanged();
    }

    public List<Swap> getSwaps(){
        return mSwaps;
    }

    public class SwapsViewHolder extends RecyclerView.ViewHolder {

        final TextView mProposedByView;
        final TextView mRatingView;
        final FrameLayout mTopFrame;
        final FrameLayout mBottomFrame;
        final ConstraintLayout mTopLayout;
        final ConstraintLayout mBottomLayout;
        final ConstraintLayout mMainLayout;
        final ImageButton mRefreshButton;
        final ImageView mCreatedIcon;
        final ImageView mVotedIcon;

        public SwapsViewHolder(@NonNull View view) {
            super(view);
            mProposedByView = view.findViewById(R.id.swap_proposed_by_name);
            mRatingView = view.findViewById(R.id.swap_net_rating_view);
            mTopFrame = view.findViewById(R.id.swap_first_included_container);
            mBottomFrame = view.findViewById(R.id.swap_second_included_container);
            mTopLayout = view.findViewById(R.id.swap_first_included_layout);
            mBottomLayout = view.findViewById(R.id.swap_second_included_layout);
            mRefreshButton = view.findViewById(R.id.swap_layout_refresh_button);
            mMainLayout = view.findViewById(R.id.swap_constraint_layout);
            mCreatedIcon = view.findViewById(R.id.swap_center_created_icon);
            mVotedIcon = view.findViewById(R.id.swap_center_voted_icon);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition() - mModifier;
                    Swap swap = mSwaps.get(position);
                    Policy demPolicy = mPolicyPairs.get(position).get(0);
                    Policy repPolicy = mPolicyPairs.get(position).get(1);

                    FragmentArgs.SWAP_DETAIL_CREATOR = swap.getCreator();
                    FragmentArgs.SWAP_DETAIL_RATING = swap.getRating();
                    FragmentArgs.SWAP_DETAIL_TOP_POLICY = swap.getPartyOnTop().equals("Democrat") ? demPolicy : repPolicy;
                    FragmentArgs.SWAP_DETAIL_BOTTOM_POLICY = swap.getPartyOnTop().equals("Democrat") ? repPolicy : demPolicy;
                    FragmentArgs.SWAP_DETAIL_TIMESTAMP = swap.getTimestamp();
                    FragmentArgs.SWAP_LONG_ID = swap.getLongID();

                    FragmentArgs.MAIN_RECYCLER_STATE = ((MainActivity) view.getContext()).getRecyclerView().getLayoutManager().onSaveInstanceState();
                    FragmentManager fm = ((MainActivity) view.getContext()).getSupportFragmentManager();
                    SwapDetailFragment frag = SwapDetailFragment.newInstance(null);
                    frag.show(fm, SWAP_FRAGMENT_NAME);
                }
            });
        }
    }

    private void fillPolicyLayout(ConstraintLayout layout, Policy policy) {
        ((TextView) layout.findViewById(R.id.swap_first_title_line)).setText(policy.getTitle());
        ((TextView) layout.findViewById(R.id.swap_first_summary_line)).setText(policy.getSummary());
        ((TextView) layout.findViewById(R.id.swap_first_creator_name)).setText(policy.getCreator());
        ((TextView) layout.findViewById(R.id.swap_first_thumbs_up_count))
                .setText(String.format(mContext.getResources().getString(R.string.policy_net_wanted),
                        policy.getNetWanted(), policy.getParty()));
        String subjects = policy.getSubjects().toString();
        ((TextView) layout.findViewById(R.id.swap_first_subject_line))
                .setText(subjects.substring(1, subjects.length() - 1));
        if (MainActivity.mUserCreated.contains(policy.getLongID())) {
            ((ImageView) layout.findViewById(R.id.swap_first_created_icon))
                    .setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_document_svg_black));
        }
        if (MainActivity.mUserVoted.contains(policy.getLongID())) {
            ((ImageView) layout.findViewById(R.id.swap_first_voted_icon))
                    .setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_check_box_black));
        }
        layout.findViewById(R.id.swap_first_icon_layout).setVisibility(View.GONE);
        ((TextView) layout.findViewById(R.id.swap_first_date))
                .setText(getDate(Long.valueOf(policy.getTimeStamp())));
    }

    private String getDate(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return String.valueOf(month + 1) + "/" + String.valueOf(day) + "/" + String.valueOf(year);
    }
}
