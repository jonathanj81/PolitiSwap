package com.politiswap.politiswap.DialogFragments;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.politiswap.politiswap.DataUtils.Swap;
import com.politiswap.politiswap.DataUtils.Tasks.FirebaseRetrievalCalls;
import com.politiswap.politiswap.NewVersion.UserInfo;
import com.politiswap.politiswap.MainActivity;
import com.politiswap.politiswap.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PolicyDetailFragment extends DialogFragment implements View.OnClickListener {

    private View mRootView;
    private Button mCannotButton;
    private boolean mAlreadyVoted = false;
    private boolean mRemovedVote = false;

    public PolicyDetailFragment() {

    }

    public static PolicyDetailFragment newInstance(Bundle args) {
        PolicyDetailFragment frag = new PolicyDetailFragment();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup group, @Nullable Bundle savedInstanceState) {

        String party = FragmentArgs.POLICY_DETAIL_PARTY;
        mRootView = inflater.inflate(R.layout.policy_detail_fragment, group);
        FrameLayout container = mRootView.findViewById(R.id.policy_detail_included_container);
        if (party.equals("Democrat")){
            container.setBackground(getResources().getDrawable(R.drawable.dem_swap_background));
        } else {
            container.setBackground(getResources().getDrawable(R.drawable.rep_swap_background));
        }

        TextView summaryView = mRootView.findViewById(R.id.swap_first_summary_line);
        summaryView.setMovementMethod(new ScrollingMovementMethod());
        summaryView.setMaxLines(12);
        summaryView.setText(FragmentArgs.POLICY_DETAIL_SUMMARY);

        ((TextView)mRootView.findViewById(R.id.swap_first_date)).setText(FragmentArgs.POLICY_DETAIL_DATE);
        ((TextView)mRootView.findViewById(R.id.swap_first_title_line)).setText(FragmentArgs.POLICY_DETAIL_TITLE);
        ((TextView)mRootView.findViewById(R.id.swap_first_subject_line)).setText(FragmentArgs.POLICY_DETAIL_SUBJECTS);
        ((TextView)mRootView.findViewById(R.id.swap_first_creator_name)).setText(FragmentArgs.POLICY_DETAIL_CREATOR);
        ((TextView)mRootView.findViewById(R.id.swap_first_thumbs_up_count))
                .setText(String.format(getResources().getString(R.string.policy_net_wanted),
                FragmentArgs.POLICY_DETAIL_NET_WANTED, party));

        mRootView.findViewById(R.id.policy_detail_close_button).setOnClickListener(this);
        mRootView.findViewById(R.id.policy_detail_vote_yes_button).setOnClickListener(this);
        mRootView.findViewById(R.id.policy_detail_vote_no_button).setOnClickListener(this);
        mCannotButton = mRootView.findViewById(R.id.policy_detail_cannot_vote_button);
        mCannotButton.setOnClickListener(this);

        if (MainActivity.IS_GUEST){
            prohibitButtons(2);
        } else if (!MainActivity.PARTY.equals(party)) {
            prohibitButtons(0);
        } else if (FragmentArgs.POLICY_DETAIL_CREATOR.equals(MainActivity.USERNAME)){
            prohibitButtons(3);
        }
        else {
            alreadyVoted();
        }

        return mRootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
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
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.policy_detail_close_button:
                dismiss();
                ((MainActivity)getActivity()).getRecyclerView().getLayoutManager().onRestoreInstanceState(FragmentArgs.MAIN_RECYCLER_STATE);
                break;
            case R.id.policy_detail_vote_yes_button:
                updateUserPoints();
                incrementVote(1, "up");
                break;
            case R.id.policy_detail_vote_no_button:
                updateUserPoints();
                incrementVote(-1, "down");
                break;
            case R.id.policy_detail_cannot_vote_button:
                confirmSwitch();
                break;
            default:
                break;
        }
    }

    private void alreadyVoted(){
        FirebaseDatabase.getInstance().getReference("UserPolicies").child(MainActivity.USER_ID)
                .child("VotedOn").child(FragmentArgs.POLICY_LONG_ID)
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
        FirebaseDatabase.getInstance().getReference("Policies").child(FragmentArgs.POLICY_LONG_ID)
                .child("netWanted").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int originalValue = dataSnapshot.getValue(Integer.class);
                int newValue = originalValue + increment;
                dataSnapshot.getRef().setValue(newValue);

                double ratingDifference = newValue - originalValue;
                MainActivity.USER_OVERALL_POINTS += ratingDifference;
                MainActivity.USER_POLICY_POINTS += ratingDifference;
                FirebaseDatabase.getInstance().getReference("UserInfo").orderByChild("username")
                        .equalTo(FragmentArgs.POLICY_DETAIL_CREATOR).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snap : dataSnapshot.getChildren()) {
                            UserInfo info = snap.getValue(UserInfo.class);
                            info.setPolicyCreatedPoints(MainActivity.USER_POLICY_POINTS);
                            info.setOverallPoints(MainActivity.USER_OVERALL_POINTS);
                            snap.getRef().setValue(info);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                String message;
                if (type.equals("neutral")){
                    message = getResources().getString(R.string.policy_neutral_vote_partial);
                    FirebaseDatabase.getInstance().getReference("UserPolicies").child(MainActivity.USER_ID)
                            .child("VotedOn").child(FragmentArgs.POLICY_LONG_ID).setValue("");
                }else if (type.equals("up")){
                    message = getResources().getString(R.string.policy_yes_vote_partial);
                    FirebaseDatabase.getInstance().getReference("UserPolicies").child(MainActivity.USER_ID)
                            .child("VotedOn").child(FragmentArgs.POLICY_LONG_ID).setValue("yes");
                    prohibitButtons(1);
                } else {
                    message = getResources().getString(R.string.policy_no_vote_partial);
                    FirebaseDatabase.getInstance().getReference("UserPolicies").child(MainActivity.USER_ID)
                            .child("VotedOn").child(FragmentArgs.POLICY_LONG_ID).setValue("no");
                    prohibitButtons(1);
                }

                updateSwapRatings(increment);

                Toast.makeText(getActivity(),String.format(getResources().getString(R.string.policy_vote_message),message)
                        , Toast.LENGTH_LONG).show();
                FragmentArgs.POLICY_DETAIL_NET_WANTED += increment;
                ((TextView)mRootView.findViewById(R.id.swap_first_thumbs_up_count))
                        .setText(String.format(getResources().getString(R.string.policy_net_wanted),
                                FragmentArgs.POLICY_DETAIL_NET_WANTED, FragmentArgs.POLICY_DETAIL_PARTY));
                if (!MainActivity.mUserVoted.contains(FragmentArgs.POLICY_LONG_ID)){
                    MainActivity.mUserVoted.add(FragmentArgs.POLICY_LONG_ID);
                }

                if (MainActivity.mAdapterNeeded == 3){
                    new FirebaseRetrievalCalls((MainActivity)getActivity(), false).getTopPolicies();
                } else {
                    new FirebaseRetrievalCalls((MainActivity) getActivity(), false).getNewPolicies();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void prohibitButtons(int type){
        mRootView.findViewById(R.id.policy_detail_can_vote_frame).setVisibility(View.GONE);
        mCannotButton.setVisibility(View.VISIBLE);
        if (type == 0){
            mCannotButton.setText(getResources().getString(R.string.policy_detail_other_party_button));
            mCannotButton.setClickable(false);
        } else if (type == 1){
            mCannotButton.setText(getResources().getString(R.string.policy_detail_already_voted_button));
            mCannotButton.setClickable(true);
        } else if (type == 2){
            mCannotButton.setText(getResources().getString(R.string.policy_detail_guest_button));
            mCannotButton.setClickable(false);
        } else if (type == 3){
            mCannotButton.setText(getResources().getString(R.string.policy_detail_vote_self));
            mCannotButton.setClickable(false);
        }
    }

    private void confirmSwitch(){
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle(getResources().getString(R.string.change_policy_vote_title));

        FirebaseDatabase.getInstance().getReference("UserPolicies").child(MainActivity.USER_ID)
                .child("VotedOn").child(FragmentArgs.POLICY_LONG_ID).addListenerForSingleValueEvent(new ValueEventListener() {
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
                                mRootView.findViewById(R.id.policy_detail_can_vote_frame).setVisibility(View.VISIBLE);
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

    private void updateSwapRatings(int increment){
        final List<String> tempSwaps = new ArrayList<>();
        final double incrementDouble = increment;

        FirebaseDatabase.getInstance().getReference("SwapsByPolicy").child(FragmentArgs.POLICY_LONG_ID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snap : dataSnapshot.getChildren()) {
                            String thisKey = snap.getKey();
                            tempSwaps.add(thisKey);
                        }
                        for (final String key : tempSwaps){
                            FirebaseDatabase.getInstance().getReference("Swaps").child(key)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Swap swap = dataSnapshot.getValue(Swap.class);
                                    swap.setPolicyAvgNet(swap.getPolicyAvgNet() + incrementDouble/2);
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
                                                info.setOverallPoints(info.getOverallPoints() + ratingDifference);
                                                snap.getRef().setValue(info);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
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
            mRemovedVote = false;
            MainActivity.USER_VOTE_POINTS -= 1;
            FirebaseDatabase.getInstance().getReference("UserInfo/" + MainActivity.USER_ID)
                    .child("votePoints").setValue(MainActivity.USER_VOTE_POINTS);
            MainActivity.USER_OVERALL_POINTS -= 1;
            FirebaseDatabase.getInstance().getReference("UserInfo/" + MainActivity.USER_ID)
                    .child("overallPoints").setValue(MainActivity.USER_OVERALL_POINTS);
        }
    }
}
