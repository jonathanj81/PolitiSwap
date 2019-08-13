package com.politiswap.politiswap.DialogFragments;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.politiswap.politiswap.DataUtils.Policy;
import com.politiswap.politiswap.DataUtils.Swap;
import com.politiswap.politiswap.DataUtils.Tasks.FirebaseRetrievalCalls;
import com.politiswap.politiswap.NewVersion.UserInfo;
import com.politiswap.politiswap.MainActivity;
import com.politiswap.politiswap.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class SwapDetailFragment extends DialogFragment implements View.OnClickListener {

    TextView mProposedByView;
    TextView mRatingView;
    FrameLayout mTopFrame;
    FrameLayout mBottomFrame;
    ConstraintLayout mTopLayout;
    ConstraintLayout mBottomLayout;
    View mRootView;
    Button mCannotButton;
    boolean mAlreadyVoted = false;
    boolean mRemovedVote = false;

    public SwapDetailFragment() {

    }

    public static SwapDetailFragment newInstance(Bundle args) {
        SwapDetailFragment frag = new SwapDetailFragment();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.swap_detail_fragment, container);

        mProposedByView = mRootView.findViewById(R.id.swap_proposed_by_name);
        mRatingView = mRootView.findViewById(R.id.swap_net_rating_view);
        mTopFrame = mRootView.findViewById(R.id.swap_first_included_container);
        mBottomFrame = mRootView.findViewById(R.id.swap_second_included_container);
        mTopLayout = mRootView.findViewById(R.id.swap_first_included_layout);
        mBottomLayout = mRootView.findViewById(R.id.swap_second_included_layout);

        boolean topIsDem = FragmentArgs.SWAP_DETAIL_TOP_POLICY.getParty().equals("Democrat");

        if (topIsDem){
            mTopFrame.setBackground(getResources().getDrawable(R.drawable.dem_swap_background));
            mBottomFrame.setBackground(getResources().getDrawable(R.drawable.rep_swap_background));
        } else {
            mTopFrame.setBackground(getResources().getDrawable(R.drawable.rep_swap_background));
            mBottomFrame.setBackground(getResources().getDrawable(R.drawable.dem_swap_background));
        }
        fillPolicyLayout(mTopLayout, FragmentArgs.SWAP_DETAIL_TOP_POLICY);
        fillPolicyLayout(mBottomLayout, FragmentArgs.SWAP_DETAIL_BOTTOM_POLICY);

        mRatingView.setText(String.format(getResources().getString(R.string.craft_swap_rating),
                String.valueOf(FragmentArgs.SWAP_DETAIL_RATING)));
        mProposedByView.setText(String.format(getResources().getString(R.string.craft_swap_proposer),
                FragmentArgs.SWAP_DETAIL_CREATOR, getDate(Long.valueOf(FragmentArgs.SWAP_DETAIL_TIMESTAMP))));

        mRootView.findViewById(R.id.swap_detail_close_button).setOnClickListener(this);
        mRootView.findViewById(R.id.swap_detail_vote_yes_button).setOnClickListener(this);
        mRootView.findViewById(R.id.swap_detail_vote_no_button).setOnClickListener(this);
        mCannotButton = mRootView.findViewById(R.id.swap_detail_cannot_vote_button);
        mCannotButton.setOnClickListener(this);

        if (MainActivity.IS_GUEST){
            prohibitButtons(2);
        } else if (FragmentArgs.SWAP_DETAIL_CREATOR.equals(MainActivity.USERNAME)){
            prohibitButtons(3);
        }
        else {
            alreadyVoted();
        }
        return mRootView;
    }

    @Override
    public void onResume() {
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        super.onResume();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void fillPolicyLayout(ConstraintLayout layout, Policy policy){

        TextView summaryView = layout.findViewById(R.id.swap_first_summary_line);
        summaryView.setText(policy.getSummary());
        summaryView.setMovementMethod(new ScrollingMovementMethod());
        summaryView.setMaxLines(4);
        ((TextView)layout.findViewById(R.id.swap_first_title_line)).setText(policy.getTitle());
        ((TextView)layout.findViewById(R.id.swap_first_creator_name)).setText(policy.getCreator());
        ((TextView)layout.findViewById(R.id.swap_first_thumbs_up_count))
                .setText(String.format(getResources().getString(R.string.policy_net_wanted),
                        policy.getNetWanted(), policy.getParty()));
        String subjects = policy.getSubjects().toString();
        ((TextView)layout.findViewById(R.id.swap_first_subject_line))
                .setText(subjects.substring(1, subjects.length() - 1));
        if (MainActivity.mUserCreated.contains(policy.getLongID())) {
            ((ImageView)layout.findViewById(R.id.swap_first_created_icon))
                    .setImageDrawable(getResources().getDrawable(R.drawable.ic_document_svg_black));
        }
        if (MainActivity.mUserVoted.contains(policy.getLongID())) {
            ((ImageView)layout.findViewById(R.id.swap_first_voted_icon))
                    .setImageDrawable(getResources().getDrawable(R.drawable.ic_check_box_black));
        }
        layout.findViewById(R.id.swap_first_voted_icon).setVisibility(View.INVISIBLE);
        layout.findViewById(R.id.swap_first_created_icon).setVisibility(View.INVISIBLE);
        ((TextView)layout.findViewById(R.id.swap_first_date))
                .setText(getDate(Long.valueOf(policy.getTimeStamp())));
    }

    private String getDate(long time){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return String.valueOf(month + 1) + "/" + String.valueOf(day) + "/" + String.valueOf(year);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.swap_detail_close_button:
                dismiss();
                ((MainActivity)getActivity()).getRecyclerView().getLayoutManager().onRestoreInstanceState(FragmentArgs.MAIN_RECYCLER_STATE);
                break;
            case R.id.swap_detail_vote_yes_button:
                updateUserPoints();
                incrementVote(1, "up");
                break;
            case R.id.swap_detail_vote_no_button:
                updateUserPoints();
                incrementVote(-1, "down");
                break;
            case R.id.swap_detail_cannot_vote_button:
                confirmSwitch();
                break;
            default:
                break;
        }
    }

    private void alreadyVoted(){
        FirebaseDatabase.getInstance().getReference("UserSwaps").child(MainActivity.USER_ID)
                .child("VotedOn").child(FragmentArgs.SWAP_LONG_ID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists() && dataSnapshot.getValue(String.class).length() > 0){
                            prohibitButtons(1);
                            mAlreadyVoted = true;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    private void incrementVote(final int increment, final String type){
        mAlreadyVoted = true;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Swaps").child(FragmentArgs.SWAP_LONG_ID);
        if (MainActivity.PARTY.equals("Democrat")){
            reference = reference.child("demNetVotes");
        } else {
            reference = reference.child("repNetVotes");
        }
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int value = dataSnapshot.getValue(Integer.class) + increment;
                dataSnapshot.getRef().setValue(value);
                String message;
                if (type.equals("neutral")){
                    message = getResources().getString(R.string.policy_neutral_vote_partial);
                    FirebaseDatabase.getInstance().getReference("UserSwaps").child(MainActivity.USER_ID)
                            .child("VotedOn").child(FragmentArgs.SWAP_LONG_ID).setValue("");
                }else if (type.equals("up")){
                    message = getResources().getString(R.string.policy_yes_vote_partial);
                    FirebaseDatabase.getInstance().getReference("UserSwaps").child(MainActivity.USER_ID)
                            .child("VotedOn").child(FragmentArgs.SWAP_LONG_ID).setValue("yes");
                    prohibitButtons(1);
                } else {
                    message = getResources().getString(R.string.policy_no_vote_partial);
                    FirebaseDatabase.getInstance().getReference("UserSwaps").child(MainActivity.USER_ID)
                            .child("VotedOn").child(FragmentArgs.SWAP_LONG_ID).setValue("no");
                    prohibitButtons(1);
                }

                if (!MainActivity.mUserSwapVoted.contains(FragmentArgs.SWAP_LONG_ID)){
                    MainActivity.mUserSwapVoted.add(FragmentArgs.SWAP_LONG_ID);
                }

                updateSwapRating();

                Toast.makeText(getActivity(),String.format(getResources().getString(R.string.policy_vote_message),message)
                        , Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void prohibitButtons(int type){
        mRootView.findViewById(R.id.swap_detail_can_vote_frame).setVisibility(View.GONE);
        mCannotButton.setVisibility(View.VISIBLE);
        if (type == 1){
            mCannotButton.setText(getResources().getString(R.string.swap_detail_already_voted_button));
            mCannotButton.setClickable(true);
        } else if (type == 2){
            mCannotButton.setText(getResources().getString(R.string.swap_detail_guest_button));
            mCannotButton.setClickable(false);
        } else if (type == 3){
            mCannotButton.setText(getResources().getString(R.string.swap_detail_vote_self));
            mCannotButton.setClickable(false);
        }
    }

    private void confirmSwitch(){
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle(getResources().getString(R.string.change_policy_vote_title));

        FirebaseDatabase.getInstance().getReference("UserSwaps").child(MainActivity.USER_ID)
                .child("VotedOn").child(FragmentArgs.SWAP_LONG_ID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String previousVote = dataSnapshot.getValue(String.class);
                String message;
                final int increment;
                final int neutralIncrement;
                if (previousVote.equals("yes")){
                    message = getResources().getString(R.string.policy_yes_vote_partial);
                    increment = -2;
                    neutralIncrement = -1;
                } else {
                    message = getResources().getString(R.string.policy_no_vote_partial);
                    increment = 2;
                    neutralIncrement = 1;
                }
                alertDialog.setMessage(String.format(getResources().getString(R.string.change_policy_vote_message),
                        message));
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.change_policy_vote_yes),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                incrementVote(increment, increment > 0 ? "up" : "down");
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getResources().getString(R.string.change_policy_vote_no),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                Toast.makeText(getActivity(),getResources().getString(R.string.change_policy_vote_no_response),
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getResources().getString(R.string.change_policy_vote_remove),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                dialog.dismiss();
                                incrementVote(neutralIncrement, "neutral");
                                mRootView.findViewById(R.id.swap_detail_can_vote_frame).setVisibility(View.VISIBLE);
                                mCannotButton.setVisibility(View.GONE);
                                mRemovedVote = true;
                                updateUserPoints();
                            }
                        });
                alertDialog.show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateSwapRating(){

        FirebaseDatabase.getInstance().getReference("Swaps").child(FragmentArgs.SWAP_LONG_ID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Swap swap = dataSnapshot.getValue(Swap.class);
                        double avgNet = ((double)swap.getDemNetVotes() + swap.getRepNetVotes())/2;
                        double previousRating = swap.getRating();
                        swap.setRating(Math.min(avgNet*2, avgNet + Math.round(Math.pow(swap.getPolicyAvgNet(), 2.0/3)*10/10)));
                        dataSnapshot.getRef().setValue(swap);

                        final double ratingDifference = swap.getRating() - previousRating;
                        FirebaseDatabase.getInstance().getReference("UserInfo").orderByChild("username")
                                .equalTo(swap.getCreator()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                    UserInfo info = snap.getValue(UserInfo.class);
                                    info.setSwapCreatedPoints(info.getSwapCreatedPoints()+ratingDifference);
                                    info.setOverallPoints(info.getOverallPoints()+ratingDifference);
                                    snap.getRef().setValue(info);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        if (MainActivity.mAdapterNeeded == 0){
                            new FirebaseRetrievalCalls((MainActivity)getActivity(), false).getTopSwaps();
                        } else {
                            new FirebaseRetrievalCalls((MainActivity)getActivity(), false).getNewSwaps();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void updateUserPoints(){
        if (!mAlreadyVoted){
            MainActivity.USER_VOTE_POINTS += 1;
            FirebaseDatabase.getInstance().getReference("UserInfo/" + MainActivity.USER_ID)
                    .child("votePoints").setValue(MainActivity.USER_VOTE_POINTS);
            MainActivity.USER_OVERALL_POINTS += 1;
            FirebaseDatabase.getInstance().getReference("UserInfo/" + MainActivity.USER_ID)
                    .child("overallPoints").setValue(MainActivity.USER_OVERALL_POINTS);
        } else if (mRemovedVote){
            mAlreadyVoted = false;
            MainActivity.USER_VOTE_POINTS -= 1;
            FirebaseDatabase.getInstance().getReference("UserInfo/" + MainActivity.USER_ID)
                    .child("votePoints").setValue(MainActivity.USER_VOTE_POINTS);
            MainActivity.USER_OVERALL_POINTS -= 1;
            FirebaseDatabase.getInstance().getReference("UserInfo/" + MainActivity.USER_ID)
                    .child("overallPoints").setValue(MainActivity.USER_OVERALL_POINTS);
        }
    }
}
