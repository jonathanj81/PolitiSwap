package com.example.jon.politiswap.DialogFragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.jon.politiswap.DataUtils.Policy;
import com.example.jon.politiswap.DataUtils.Swap;
import com.example.jon.politiswap.DataUtils.Tasks.FirebaseRetrievalCalls;
import com.example.jon.politiswap.MainActivity;
import com.example.jon.politiswap.R;
import com.example.jon.politiswap.UiAdapters.CreateSubjectAvailableAdapter;
import com.example.jon.politiswap.UiAdapters.PolicyAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CreateSwapFragment extends DialogFragment implements View.OnClickListener, FirebaseRetrievalCalls.RetrieveFirebase, CreateSubjectAvailableAdapter.SubjectChangeManager {

    private View mRootView;
    private MainActivity mActivity;
    private RecyclerView mPolicyChoicesRecycler;
    private RecyclerView mSubjectChoicesRecycler;
    private CreateSubjectAvailableAdapter mSubjectsAdapter;
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

    public CreateSwapFragment() {

    }

    public static CreateSwapFragment newInstance(Bundle args) {
        CreateSwapFragment frag = new CreateSwapFragment();
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
        mRootView = inflater.inflate(R.layout.craft_swap_fragment, container);
        mActivity = (MainActivity) getActivity();
        mHeaderView = mRootView.findViewById(R.id.create_swap_header_text_view);
        ((TextView) mRootView.findViewById(R.id.swap_proposed_by_name))
                .setText(String.format(getResources().getString(R.string.craft_swap_proposer), MainActivity.USERNAME));
        ((TextView) mRootView.findViewById(R.id.swap_first_date)).setText(getTodaysDate());
        ((TextView) mRootView.findViewById(R.id.swap_second_included_layout).findViewById(R.id.swap_first_date)).setText(getTodaysDate());

        mPolicyAdapter = new PolicyAdapter(1);
        mSubjectsAdapter = new CreateSubjectAvailableAdapter(this, 0);

        mPolicyChoicesRecycler = mRootView.findViewById(R.id.create_swap_choices_recycler);
        mPolicyChoicesRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mPolicyChoicesRecycler.setHasFixedSize(true);
        mPolicyChoicesRecycler.setAdapter(mPolicyAdapter);

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
                getSelectionRecycler(0);
                break;
            case R.id.create_swap_browse_recent_button:
                getSelectionRecycler(1);
                break;
            case R.id.create_swap_browse_top_button:
                getSelectionRecycler(2);
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
    }

    private void getSelectionRecycler(int browseType) {
        mRootView.findViewById(R.id.create_swap_browse_method_layout).setVisibility(View.GONE);

        switch (browseType) {
            case 0:
                break;
            case 1:
                new FirebaseRetrievalCalls(this).getNewPolices();
                mRootView.findViewById(R.id.create_swap_choices_layout).setVisibility(View.VISIBLE);
                break;
            case 2:
                new FirebaseRetrievalCalls(this).getTopPolicies();
                mRootView.findViewById(R.id.create_swap_choices_layout).setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void newPoliciesSent(List<Policy> policies) {
        List<Policy> tempPolicies = new ArrayList<>();
        for (Policy policy : policies) {
            if (policy.getParty().equals(mTargetParty) && policy.getNetWanted() >= 0) {
                tempPolicies.add(policy);
            }
        }
        if (tempPolicies.size() == 0) {
            mRootView.findViewById(R.id.create_swap_no_policies_found_text).setVisibility(View.VISIBLE);
        }
        mPolicyAdapter.setPolicies(tempPolicies);
    }

    @Override
    public void newSwapsSent(List<Swap> swaps) {

    }

    @Override
    public void addSubject(String subject) {

    }

    @Override
    public void deleteSubject(String subject) {

    }

    private void resetScreens() {
        mRootView.findViewById(R.id.create_swap_choices_layout).setVisibility(View.GONE);
        mRootView.findViewById(R.id.create_swap_browse_method_layout).setVisibility(View.GONE);
        mRootView.findViewById(R.id.create_swap_progress_layout).setVisibility(View.VISIBLE);
        ((Button) mRootView.findViewById(R.id.craft_swap_choices_back))
                .setText(getResources().getString(R.string.craft_policy_close_cancel));
        mHeaderView.setText(getResources().getString(R.string.craft_swap_header));
        mHeaderView.setTextColor(getResources().getColor(R.color.darkGray));
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
            ((TextView) mLayoutDem.findViewById(R.id.swap_first_creator_name)).setText(policy.getCreator());
            ((TextView) mLayoutDem.findViewById(R.id.swap_first_subject_line)).setText(subjects.substring(1, subjects.length() - 1));
            ((TextView) mLayoutDem.findViewById(R.id.swap_first_title_line)).setText(policy.getTitle());
            ((TextView) mLayoutDem.findViewById(R.id.swap_first_summary_line)).setText(policy.getSummary());
            ((TextView) mLayoutDem.findViewById(R.id.swap_first_thumbs_up_count))
                    .setText(String.format(getResources().getString(R.string.policy_net_wanted),
                            policy.getNetWanted(), party));
            mFrameDem.setBackground(getResources().getDrawable(R.drawable.dem_swap_background));
            mDemButton.setText(getResources().getString(R.string.craft_swap_change_dem_policy));
        } else {
            ((TextView) mLayoutRep.findViewById(R.id.swap_first_creator_name)).setText(policy.getCreator());
            ((TextView) mLayoutRep.findViewById(R.id.swap_first_subject_line)).setText(subjects.substring(1, subjects.length() - 1));
            ((TextView) mLayoutRep.findViewById(R.id.swap_first_title_line)).setText(policy.getTitle());
            ((TextView) mLayoutRep.findViewById(R.id.swap_first_summary_line)).setText(policy.getSummary());
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
            mDemButton.setBackgroundColor(getResources().getColor(R.color.darkGray));
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
                        dismiss();
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
        String pushId = mDatabaseReference.push().getKey();

        Swap swap = new Swap(MainActivity.USERNAME,String.valueOf(System.currentTimeMillis()),
                mSelectedPolicies.get("Democrat"), mSelectedPolicies.get("Republican"),
                mPartyOnTop, 0, 0, 0,
                ((double)mSelectedPolicyVotes.get("Democrat") + mSelectedPolicyVotes.get("Republican"))/2);

        mDatabaseReference.child(pushId).setValue(swap);

        mDatabaseReference = mFirebaseDatabase.getReference().child("SwapsByPolicy");
        mDatabaseReference.child(mSelectedPolicies.get("Democrat")).child(pushId).setValue("yes");
        mDatabaseReference.child(mSelectedPolicies.get("Republican")).child(pushId).setValue("yes");

        mDatabaseReference = mFirebaseDatabase.getReference("UserSwaps").child(MainActivity.USER_ID)
                .child("Created");
        mDatabaseReference.push().setValue(pushId);

        Set<String> subjects = new HashSet<>();
        subjects.addAll(mSelectedPolicySubjects.get("Democrat"));
        subjects.addAll(mSelectedPolicySubjects.get("Republican"));

        mDatabaseReference = mFirebaseDatabase.getReference("Subjects");
        for (String subject : subjects){
            subject = subject.replace("/","");
            mDatabaseReference.child(subject).child("bySwap").push().setValue(pushId);
        }
        MainActivity.mUserSwapCreated.add(pushId);
    }
}
