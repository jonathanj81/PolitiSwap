package com.politiswap.politiswap.DialogFragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.politiswap.politiswap.UiAdapters.OnBottomReachedListener;
import com.politiswap.politiswap.DataUtils.Policy;
import com.politiswap.politiswap.DataUtils.Swap;
import com.politiswap.politiswap.DataUtils.Tasks.FirebaseRetrievalCalls;
import com.politiswap.politiswap.MainActivity;
import com.politiswap.politiswap.R;
import com.politiswap.politiswap.UiAdapters.CreateSubjectAvailableAdapter;
import com.politiswap.politiswap.UiAdapters.PolicyAdapter;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CreateSwapFragment extends DialogFragment implements View.OnClickListener, FirebaseRetrievalCalls.RetrieveFirebase, CreateSubjectAvailableAdapter.SubjectChangeManager, OnBottomReachedListener {

    private View mRootView;
    private MainActivity mActivity;
    private RecyclerView mPolicyChoicesRecycler;
    private RecyclerView mSubjectChoicesRecycler;
    private CreateSubjectAvailableAdapter mSubjectAvailableAdapter;
    private PolicyAdapter mPolicyAdapter;
    private String mTargetParty;
    private TextView mHeaderView;
    private Map<String,String> mSelectedPolicies = new HashMap<>();
    private Map<String,Integer> mSelectedPolicyVotes = new HashMap<>();
    private Map<String,List<String>> mSelectedPolicySubjects = new HashMap<>();
    private String mPartyOnTop;
    private ConstraintLayout mLayoutDem;
    private ConstraintLayout mLayoutRep;
    private FrameLayout mFrameDem;
    private FrameLayout mFrameRep;
    private boolean mReadyToGo = false;
    private Button mDemButton;
    private Button mRepButton;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private int mBrowseType;
    private ArrayList<Policy> mRetainablePolicyList = new ArrayList<>();
    private static final String RETAINED_POLICIES = "retained_policies";
    private static final String PARTY_ON_TOP = "party_on_top";
    private int mCurrentScreenPosition = 0;
    private static final String CURRENT_SCREEN_POSITION = "current_screen_position";
    private static final String SELECTABLE_POLICIES_RETAINED = "selectable_policies";
    private static final String TARGET_PARTY_RETAINED = "target_party";

    public CreateSwapFragment() {

    }

    public static CreateSwapFragment newInstance(Bundle args) {
        CreateSwapFragment frag = new CreateSwapFragment();
        frag.setArguments(args);
        return frag;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (MainActivity)context;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPolicyAdapter = new PolicyAdapter(1, this);

        mPolicyChoicesRecycler = mRootView.findViewById(R.id.create_swap_choices_recycler);
        mPolicyChoicesRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mPolicyChoicesRecycler.setHasFixedSize(true);
        mPolicyChoicesRecycler.setAdapter(mPolicyAdapter);

        if (savedInstanceState != null){
            List<Policy> receivedPolicies = savedInstanceState.getParcelableArrayList(RETAINED_POLICIES);
            int tempScreenPosition = savedInstanceState.getInt(CURRENT_SCREEN_POSITION);
            mPartyOnTop = savedInstanceState.getString(PARTY_ON_TOP);

            if (receivedPolicies != null && receivedPolicies.size() > 0) {
                for (Policy policy : receivedPolicies) {
                    if (policy.getParty().equals(mPartyOnTop)) {
                        refreshPolicyViews(policy);
                    }
                }
                for (Policy policy : receivedPolicies) {
                    if (!policy.getParty().equals(mPartyOnTop)) {
                        refreshPolicyViews(policy);
                    }
                }
            }
            if (tempScreenPosition == 3){
                List<Policy> tempPolicies = savedInstanceState.getParcelableArrayList(SELECTABLE_POLICIES_RETAINED);
                mRootView.findViewById(R.id.create_swap_choices_layout).setVisibility(View.VISIBLE);
                mRootView.findViewById(R.id.create_swap_progress_layout).setVisibility(View.GONE);
                mPolicyAdapter.setPolicies(tempPolicies, false);
            } else if (tempScreenPosition == 2){
                prepAvailableRecycler();
                mRootView.findViewById(R.id.create_swap_subject_choices_layout).setVisibility(View.VISIBLE);
                mRootView.findViewById(R.id.craft_subject_recycler_and_placeholder_frame).setVisibility(View.GONE);
                mRootView.findViewById(R.id.craft_policy_step_number_1).setVisibility(View.GONE);
                mRootView.findViewById(R.id.create_swap_progress_layout).setVisibility(View.GONE);
                ((TextView)mRootView.findViewById(R.id.craft_subject_title_text_view))
                        .setText(getResources().getString(R.string.search_alt_subject_hint));
            } else if (tempScreenPosition == 1){
                mTargetParty = savedInstanceState.getString(TARGET_PARTY_RETAINED);
                getBrowsingChoice();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.craft_swap_fragment, container);
        mActivity = (MainActivity) getActivity();
        mHeaderView = mRootView.findViewById(R.id.create_swap_header_text_view);
        ((TextView) mRootView.findViewById(R.id.swap_proposed_by_name))
                .setText(String.format(getResources().getString(R.string.craft_swap_proposer), MainActivity.USERNAME, getDate(System.currentTimeMillis())));
        ((TextView) mRootView.findViewById(R.id.swap_first_date)).setText(getTodaysDate());
        ((TextView) mRootView.findViewById(R.id.swap_second_included_layout).findViewById(R.id.swap_first_date)).setText(getTodaysDate());

        mRootView.findViewById(R.id.swap_first_included_layout).findViewById(R.id.swap_first_voted_icon).setVisibility(View.INVISIBLE);
        mRootView.findViewById(R.id.swap_first_included_layout).findViewById(R.id.swap_first_created_icon).setVisibility(View.INVISIBLE);
        mRootView.findViewById(R.id.swap_second_included_layout).findViewById(R.id.swap_first_voted_icon).setVisibility(View.INVISIBLE);
        mRootView.findViewById(R.id.swap_second_included_layout).findViewById(R.id.swap_first_created_icon).setVisibility(View.INVISIBLE);

        mDemButton = mRootView.findViewById(R.id.create_swap_dem_button);
        mRepButton = mRootView.findViewById(R.id.create_swap_rep_button);

        mDemButton.setOnClickListener(this);
        mRepButton.setOnClickListener(this);
        mRootView.findViewById(R.id.craft_swap_choices_back).setOnClickListener(this);
        mRootView.findViewById(R.id.craft_swap_dialog_close_button).setOnClickListener(this);

        mSelectedPolicies.put("Democrat", "");
        mSelectedPolicies.put("Republican", "");
        mSelectedPolicyVotes.put("Democrat", 0);
        mSelectedPolicyVotes.put("Republican", 0);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("Swaps");

        return mRootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(RETAINED_POLICIES, mRetainablePolicyList);
        outState.putString(PARTY_ON_TOP, mPartyOnTop);
        outState.putInt(CURRENT_SCREEN_POSITION, mCurrentScreenPosition);
        if (mCurrentScreenPosition == 3){
            outState.putParcelableArrayList(SELECTABLE_POLICIES_RETAINED, new ArrayList<Parcelable>(mPolicyAdapter.getPolicies()));
        } else if (mCurrentScreenPosition == 1){
            outState.putString(TARGET_PARTY_RETAINED, mTargetParty);
        }
    }

    @Override
    public void onResume() {
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        super.onResume();
    }

    private String getTodaysDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return String.valueOf(month + 1) + "/" + String.valueOf(day) + "/" + String.valueOf(year);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.create_swap_dem_button:
                if (mReadyToGo){
                    confirmPublish();
                } else {
                    mTargetParty = "Democrat";
                    getBrowsingChoice();
                }
                break;
            case R.id.create_swap_rep_button:
                if (mReadyToGo){
                    resetButtons();
                } else {
                    mTargetParty = "Republican";
                    getBrowsingChoice();
                }
                break;
            case R.id.create_swap_browse_subject_button:
                mBrowseType = 0;
                getSelectionRecycler();
                break;
            case R.id.create_swap_browse_recent_button:
                mBrowseType = 1;
                getSelectionRecycler();
                break;
            case R.id.create_swap_browse_top_button:
                mBrowseType = 2;
                getSelectionRecycler();
                break;
            case R.id.craft_swap_choices_back:
                if (mRootView.findViewById(R.id.create_swap_progress_layout).isShown()) {
                    closingDialog();
                } else {
                    resetScreens();
                }
                break;
            case R.id.craft_swap_dialog_close_button:
                closingDialog();
        }
    }

    private void getBrowsingChoice() {
        mHeaderView.setText(String.format(getResources().getString(R.string.craft_swap_choice_header), mTargetParty));
        if (mTargetParty.equals("Democrat")) {
            mHeaderView.setTextColor(getResources().getColor(R.color.raised_button_darker_blue));
        } else {
            mHeaderView.setTextColor(getResources().getColor(R.color.raised_button_darker_red));
        }
        mRootView.findViewById(R.id.create_swap_progress_layout).setVisibility(View.GONE);
        mRootView.findViewById(R.id.create_swap_browse_method_layout).setVisibility(View.VISIBLE);
        mRootView.findViewById(R.id.create_swap_browse_subject_button).setOnClickListener(this);
        mRootView.findViewById(R.id.create_swap_browse_recent_button).setOnClickListener(this);
        mRootView.findViewById(R.id.create_swap_browse_top_button).setOnClickListener(this);
        ((Button) mRootView.findViewById(R.id.craft_swap_choices_back))
                .setText(getResources().getString(R.string.craft_policy_small_back));
        mCurrentScreenPosition = 1;
    }

    private void getSelectionRecycler() {
        mRootView.findViewById(R.id.create_swap_browse_method_layout).setVisibility(View.GONE);
        MainActivity.mLastFirebaseNode = "";

        switch (mBrowseType) {
            case 0:
                prepAvailableRecycler();
                mRootView.findViewById(R.id.create_swap_subject_choices_layout).setVisibility(View.VISIBLE);
                mRootView.findViewById(R.id.craft_subject_recycler_and_placeholder_frame).setVisibility(View.GONE);
                mRootView.findViewById(R.id.craft_policy_step_number_1).setVisibility(View.GONE);
                ((TextView)mRootView.findViewById(R.id.craft_subject_title_text_view))
                        .setText(getResources().getString(R.string.search_alt_subject_hint));
                mCurrentScreenPosition = 2;
                break;
            case 1:
                new FirebaseRetrievalCalls(this, false).getNewPolicies();
                mRootView.findViewById(R.id.create_swap_choices_layout).setVisibility(View.VISIBLE);
                break;
            case 2:
                new FirebaseRetrievalCalls(this, false).getTopPolicies();
                mRootView.findViewById(R.id.create_swap_choices_layout).setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void newPoliciesSent(List<Policy> policies, boolean fromScroll) {
        MainActivity.isAtEnd = policies.size() < 20;
        List<Policy> tempPolicies = new ArrayList<>();
        for (Policy policy : policies) {
            if (policy.getParty().equals(mTargetParty) && policy.getNetWanted() >= 0) {
                tempPolicies.add(policy);
            }
        }
        if (tempPolicies.size() == 0) {
            mRootView.findViewById(R.id.create_swap_no_policies_found_text).setVisibility(View.VISIBLE);
        } else {
            mRootView.findViewById(R.id.create_swap_no_policies_found_text).setVisibility(View.GONE);
        }
        mPolicyAdapter.setPolicies(tempPolicies, fromScroll);
        mCurrentScreenPosition = 3;
    }

    @Override
    public void newSwapsSent(List<Swap> swaps, boolean fromScroll) {

    }

    @Override
    public void addSubject(String subject) {
        new FirebaseRetrievalCalls(this, false).getPolicyAreaSearch(subject);
        mRootView.findViewById(R.id.create_swap_subject_choices_layout).setVisibility(View.GONE);
        mRootView.findViewById(R.id.create_swap_choices_layout).setVisibility(View.VISIBLE);
        MainActivity.mCurrentAreaSubject = subject;
    }

    @Override
    public void deleteSubject(String subject) {

    }

    private void resetScreens() {
        mRootView.findViewById(R.id.create_swap_subject_choices_layout).setVisibility(View.GONE);
        mRootView.findViewById(R.id.create_swap_choices_layout).setVisibility(View.GONE);
        mRootView.findViewById(R.id.create_swap_browse_method_layout).setVisibility(View.GONE);
        mRootView.findViewById(R.id.create_swap_progress_layout).setVisibility(View.VISIBLE);
        ((Button) mRootView.findViewById(R.id.craft_swap_choices_back))
                .setText(getResources().getString(R.string.craft_policy_close_cancel));
        mHeaderView.setText(getResources().getString(R.string.craft_swap_header));
        mHeaderView.setTextColor(getResources().getColor(R.color.darkGray));
        mCurrentScreenPosition = 0;
    }

    private void closingDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle(getResources().getString(R.string.craft_swap_close_title));
        alertDialog.setMessage(getResources().getString(R.string.craft_policy_close_warning));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.craft_policy_close_confirm),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                                .hideSoftInputFromWindow(mRootView.getWindowToken(), 0);
                        dismiss();
                        mActivity.getRecyclerView().getLayoutManager().onRestoreInstanceState(MainActivity.recyclerViewState);
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getResources().getString(R.string.craft_policy_close_cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        alertDialog.show();
    }

    public void refreshPolicyViews(Policy policy) {
        resetScreens();
        addOrReplaceRetainable(policy);
        String party = policy.getParty();
        String subjects = policy.getSubjects().toString();

        if (mSelectedPolicies.get("Democrat").isEmpty() && mSelectedPolicies.get("Republican").isEmpty()) {
            mPartyOnTop = party;
            if (mPartyOnTop.equals("Democrat")){
                mLayoutDem = mRootView.findViewById(R.id.swap_first_included_layout);
                mFrameDem = mRootView.findViewById(R.id.swap_first_included_container);
                mLayoutRep = mRootView.findViewById(R.id.swap_second_included_layout);
                mFrameRep = mRootView.findViewById(R.id.swap_second_included_container);
            } else {
                mLayoutDem = mRootView.findViewById(R.id.swap_second_included_layout);
                mFrameDem = mRootView.findViewById(R.id.swap_second_included_container);
                mLayoutRep = mRootView.findViewById(R.id.swap_first_included_layout);
                mFrameRep = mRootView.findViewById(R.id.swap_first_included_container);
            }
        }
        mSelectedPolicies.put(party, policy.getLongID());
        mSelectedPolicyVotes.put(party, policy.getNetWanted());
        mSelectedPolicySubjects.put(party,policy.getSubjects());

        if (party.equals("Democrat")){
            mLayoutDem.findViewById(R.id.swap_first_icon_layout).setVisibility(View.GONE);
            ((TextView) mLayoutDem.findViewById(R.id.swap_first_creator_name)).setText(policy.getCreator());
            ((TextView) mLayoutDem.findViewById(R.id.swap_first_subject_line)).setText(subjects.substring(1, subjects.length() - 1));
            ((TextView) mLayoutDem.findViewById(R.id.swap_first_title_line)).setText(policy.getTitle());
            ((TextView) mLayoutDem.findViewById(R.id.swap_first_summary_line)).setText(policy.getSummary());
            if (MainActivity.isLand) {
                ((TextView) mLayoutDem.findViewById(R.id.swap_first_summary_line)).setMaxLines(1);
            }
            ((TextView) mLayoutDem.findViewById(R.id.swap_first_thumbs_up_count))
                    .setText(String.format(getResources().getString(R.string.policy_net_wanted),
                            policy.getNetWanted(), party));
            mFrameDem.setBackground(getResources().getDrawable(R.drawable.dem_swap_background));
            mDemButton.setText(getResources().getString(R.string.craft_swap_change_dem_policy));
        } else {
            mLayoutRep.findViewById(R.id.swap_first_icon_layout).setVisibility(View.GONE);
            ((TextView) mLayoutRep.findViewById(R.id.swap_first_creator_name)).setText(policy.getCreator());
            ((TextView) mLayoutRep.findViewById(R.id.swap_first_subject_line)).setText(subjects.substring(1, subjects.length() - 1));
            ((TextView) mLayoutRep.findViewById(R.id.swap_first_title_line)).setText(policy.getTitle());
            ((TextView) mLayoutRep.findViewById(R.id.swap_first_summary_line)).setText(policy.getSummary());
            if (MainActivity.isLand) {
                ((TextView) mLayoutRep.findViewById(R.id.swap_first_summary_line)).setMaxLines(1);
            }
            ((TextView) mLayoutRep.findViewById(R.id.swap_first_thumbs_up_count))
                    .setText(String.format(getResources().getString(R.string.policy_net_wanted),
                            policy.getNetWanted(), party));
            mFrameRep.setBackground(getResources().getDrawable(R.drawable.rep_swap_background));
            mRepButton.setText(getResources().getString(R.string.craft_swap_change_rep_policy));
        }

        if (!(mSelectedPolicies.get("Democrat").isEmpty() || mSelectedPolicies.get("Republican").isEmpty())){
            mReadyToGo = true;
            mDemButton.setText(getResources().getString(R.string.craft_swap_publish));
            mRepButton.setText(getResources().getString(R.string.craft_swap_change_either_policy));
            mDemButton.setBackground(getResources().getDrawable(R.drawable.button_yes_background));
            mRepButton.setBackgroundColor(getResources().getColor(R.color.darkGray));
        }
    }

    private void resetButtons(){
        mReadyToGo = false;
        mDemButton.setText(getResources().getString(R.string.craft_swap_change_dem_policy));
        mRepButton.setText(getResources().getString(R.string.craft_swap_change_rep_policy));
        mDemButton.setBackground(getResources().getDrawable(R.drawable.button_dem_background));
        mRepButton.setBackground(getResources().getDrawable(R.drawable.button_rep_background));
    }

    private void confirmPublish(){
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle(getResources().getString(R.string.craft_swap_publish_title));
        alertDialog.setMessage(getResources().getString(R.string.craft_swap_publish_message));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.craft_policy_publish_yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        addToDatabase();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getResources().getString(R.string.craft_policy_close_cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void addToDatabase(){
        final String demID = mSelectedPolicies.get("Democrat");
        final String repID = mSelectedPolicies.get("Republican");

        mDatabaseReference = mFirebaseDatabase.getReference("SwapsCheckDuplicates");
        mDatabaseReference.child(demID+repID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Toast.makeText(mActivity,getResources().getString(R.string.craft_swap_duplicate_message), Toast.LENGTH_LONG)
                            .show();
                } else {
                    mDatabaseReference = mFirebaseDatabase.getReference("Swaps");
                    String pushId = mDatabaseReference.push().getKey();

                    Swap swap = new Swap(MainActivity.USERNAME,String.valueOf(System.currentTimeMillis()),
                            demID, repID, mPartyOnTop, pushId, 0, 0, 0,
                            ((double)mSelectedPolicyVotes.get("Democrat") + mSelectedPolicyVotes.get("Republican"))/2);

                    mDatabaseReference.child(pushId).setValue(swap);

                    mDatabaseReference = mFirebaseDatabase.getReference().child("SwapsByPolicy");
                    mDatabaseReference.child(demID).child(pushId).setValue("yes");
                    mDatabaseReference.child(repID).child(pushId).setValue("yes");

                    mDatabaseReference = mFirebaseDatabase.getReference().child("SwapsCheckDuplicates");
                    mDatabaseReference.child(demID+repID).setValue(pushId);

                    mDatabaseReference = mFirebaseDatabase.getReference("UserSwaps").child(MainActivity.USER_ID)
                            .child("Created");
                    mDatabaseReference.push().setValue(pushId);

                    Set<String> subjects = new HashSet<>();
                    subjects.addAll(mSelectedPolicySubjects.get("Democrat"));
                    subjects.addAll(mSelectedPolicySubjects.get("Republican"));

                    mDatabaseReference = mFirebaseDatabase.getReference("Subjects");
                    for (String subject : subjects){
                        subject = subject.replace("/","");
                        mDatabaseReference.child(subject).child("bySwap").child(pushId).setValue(pushId);
                    }
                    MainActivity.mUserSwapCreated.add(pushId);

                    mDatabaseReference = mFirebaseDatabase.getReference("UserInfo/" + MainActivity.USER_ID);
                    MainActivity.USER_SWAP_POINTS += 5;
                    MainActivity.USER_OVERALL_POINTS += 5;
                    mDatabaseReference.child("overallPoints").setValue(MainActivity.USER_OVERALL_POINTS);
                    mDatabaseReference.child("swapCreatedPoints").setValue(MainActivity.USER_SWAP_POINTS);

                    Bundle logParams = new Bundle();
                    logParams.putString("User", MainActivity.USERNAME);
                    FirebaseAnalytics.getInstance(getActivity()).logEvent("swapCreated", logParams);

                    dismiss();

                    if (MainActivity.mAdapterNeeded == 0){
                        new FirebaseRetrievalCalls(mActivity, false).getTopSwaps();
                    } else {
                        new FirebaseRetrievalCalls(mActivity, false).getNewSwaps();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String getDate(long time){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return String.valueOf(month + 1) + "/" + String.valueOf(day) + "/" + String.valueOf(year);
    }

    private void prepAvailableRecycler() {
        mSubjectChoicesRecycler = mRootView.findViewById(R.id.craft_subject_available_recycler);
        mSubjectAvailableAdapter = new CreateSubjectAvailableAdapter(this,0);
        mSubjectAvailableAdapter.setAll();
        mSubjectChoicesRecycler.setHasFixedSize(false);
        mSubjectChoicesRecycler.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false));
        mSubjectChoicesRecycler.setAdapter(mSubjectAvailableAdapter);
    }

    @Override
    public void onBottomReached() {
        switch (mBrowseType){
            case 0:
                //recyclerViewState = mRecyclerView.getLayoutManager().onSaveInstanceState();
                new FirebaseRetrievalCalls(this, true).getPolicyAreaSearch(MainActivity.mCurrentAreaSubject);
                break;
            case 1:
                new FirebaseRetrievalCalls(this, true).getNewPolicies();
                break;
            case 2:
                new FirebaseRetrievalCalls(this, true).getTopPolicies();
                break;
        }
    }

    private void addOrReplaceRetainable(Policy policy){
        for (Policy savedPolicy : mRetainablePolicyList){
            if (savedPolicy.getParty().equals(policy.getParty())){
                mRetainablePolicyList.remove(savedPolicy);
                break;
            }
        }
        mRetainablePolicyList.add(policy);
    }
}
