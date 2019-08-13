package com.politiswap.politiswap.NewVersion;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.politiswap.politiswap.DataUtils.Tasks.FirebaseRetrievalCalls;
import com.politiswap.politiswap.MainActivity;
import com.politiswap.politiswap.R;
import com.politiswap.politiswap.databinding.ActivityLaunchBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class LaunchActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityLaunchBinding mBinding;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseUser mUser;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(LaunchActivity.this);
        manageSignIn();
    }

    private void manageSignIn() {
        mFirebaseAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mUser = firebaseAuth.getCurrentUser();
                if (mUser != null) {
                    setUpLaunchScreen();
                    if (!mUser.isAnonymous()){
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString(Constants.PREFS_USER_ID, mUser.getUid());
                        editor.putBoolean(Constants.EMAIL_VERIFIED,mUser.isEmailVerified());
                        editor.apply();
                        getUserInfo();
                    } else {
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean(Constants.GUEST_NOTIFY, false);
                        editor.apply();
                    }
                } else {
                    ActivityOptions options = ActivityOptions.makeCustomAnimation(LaunchActivity.this,R.anim.fade_in,R.anim.fade_out);
                    LaunchActivity.this.startActivityForResult(
                            AuthUI.getInstance().createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.AnonymousBuilder().build(),
                                            new AuthUI.IdpConfig.EmailBuilder().build(),
                                            new AuthUI.IdpConfig.GoogleBuilder().build()))
                                    .setTheme(R.style.LoginTheme)
                                    .setLogo(R.drawable.politiswap_chopped3)
                                    .build(),
                            123, options.toBundle());
                }
            }
        };
    }

    private void setUpLaunchScreen() {
        setContentView(R.layout.activity_launch);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_launch);
        mBinding.launchViewPoliciesButton.setOnClickListener(this);

        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(1000);
        anim.setStartOffset(750);
        mBinding.launchViewIcon.startAnimation(anim);
        mBinding.launchViewSwapsButton.startAnimation(anim);
        mBinding.launchViewPoliciesButton.startAnimation(anim);
        mBinding.launchViewLegislationButton.startAnimation(anim);
        mBinding.launchViewTutorialButton.startAnimation(anim);
    }

    private void getUserInfo(){
        FirebaseDatabase.getInstance().getReference("UserInfo").child(prefs.getString(Constants.PREFS_USER_ID,""))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserInfo info = dataSnapshot.getValue(UserInfo.class);
                        if (info != null) {
                            SharedPreferences.Editor editor = prefs.edit();
                            Set<String> tempSet = new HashSet<>();
                            editor.putString(Constants.PREFS_USER_NAME,info.getUsername());
                            editor.putString(Constants.PREFS_USER_PARTY,info.getParty());
                            editor.putFloat(Constants.PREFS_USER_VOTE_POINTS,(float)info.getVotePoints());
                            editor.putFloat(Constants.PREFS_USER_SWAP_POINTS,(float)info.getSwapCreatedPoints());
                            editor.putFloat(Constants.PREFS_USER_POLICY_POINTS,(float)info.getPolicyCreatedPoints());
                            editor.putFloat(Constants.PREFS_USER_OVERALL_POINTS,(float)info.getOverallPoints());
                            if (info.getPoliciesCreated() != null) {
                                tempSet.addAll(info.getPoliciesCreated());
                                editor.putStringSet(Constants.PREFS_USER_POLICIES_CREATED, tempSet);
                                tempSet.clear();
                            } else {
                                editor.putStringSet(Constants.PREFS_USER_POLICIES_CREATED, new HashSet<String>());
                            }
                            if (info.getPoliciesCommented() != null){
                                tempSet.addAll(info.getPoliciesCommented());
                                editor.putStringSet(Constants.PREFS_USER_POLICIES_COMMENTED,tempSet);
                                tempSet.clear();
                            } else {
                                editor.putStringSet(Constants.PREFS_USER_POLICIES_COMMENTED,new HashSet<String>());
                            }
                            if (info.getPoliciesVoted() != null){
                                tempSet.addAll(info.getPoliciesVoted());
                                editor.putStringSet(Constants.PREFS_USER_POLICIES_VOTED,tempSet);
                                tempSet.clear();
                            } else {
                                editor.putStringSet(Constants.PREFS_USER_POLICIES_VOTED,new HashSet<String>());
                            }
                            if (info.getSwapsCreated() != null){
                                tempSet.addAll(info.getSwapsCreated());
                                editor.putStringSet(Constants.PREFS_USER_SWAPS_CREATED,tempSet);
                                tempSet.clear();
                            } else {
                                editor.putStringSet(Constants.PREFS_USER_SWAPS_CREATED,new HashSet<String>());
                            }
                            if (info.getSwapsCommented() != null){
                                tempSet.addAll(info.getSwapsCommented());
                                editor.putStringSet(Constants.PREFS_USER_SWAPS_COMMENTED,tempSet);
                                tempSet.clear();
                            } else {
                                editor.putStringSet(Constants.PREFS_USER_SWAPS_COMMENTED,new HashSet<String>());
                            }
                            if (info.getSwapsVoted() != null){
                                tempSet.addAll(info.getSwapsVoted());
                                editor.putStringSet(Constants.PREFS_USER_SWAPS_VOTED,tempSet);
                            } else {
                                editor.putStringSet(Constants.PREFS_USER_SWAPS_VOTED,new HashSet<String>());
                            }
                            editor.apply();
                            if (!mUser.isEmailVerified()) {
                                showVerifyEmailAlert();
                            } else {
                                editor.putBoolean(Constants.EMAIL_VERIFIED,true);
                                editor.apply();
                            }
                        } else {
                            getPartyAndEnter();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void getPartyAndEnter(){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setTitle(getResources().getString(R.string.party_choice_dialog_title));
        alertDialog.setMessage(getResources().getString(R.string.party_choice_dialog_message));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.party_choice_dialog_democrat),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString(Constants.PREFS_USER_PARTY,"Democrat");
                        editor.apply();
                        dialog.dismiss();
                        userIntoDatabase();
                        if (!mUser.isEmailVerified()) {
                            showVerifyEmailAlert();
                        } else {
                            editor.putBoolean(Constants.EMAIL_VERIFIED,true);
                            editor.apply();
                        }
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getResources().getString(R.string.party_choice_dialog_republican),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString(Constants.PREFS_USER_PARTY,"Republican");
                        editor.apply();
                        dialogInterface.dismiss();
                        userIntoDatabase();
                        if (!mUser.isEmailVerified()) {
                            showVerifyEmailAlert();
                        } else {
                            editor.putBoolean(Constants.EMAIL_VERIFIED,true);
                            editor.apply();
                        }
                    }
                });
        alertDialog.show();
    }

    private void userIntoDatabase(){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.PREFS_USER_NAME, mUser.getDisplayName());
        editor.putFloat(Constants.PREFS_USER_VOTE_POINTS,0);
        editor.putFloat(Constants.PREFS_USER_SWAP_POINTS,0);
        editor.putFloat(Constants.PREFS_USER_POLICY_POINTS,0);
        editor.putFloat(Constants.PREFS_USER_OVERALL_POINTS,0);
        editor.putStringSet(Constants.PREFS_USER_POLICIES_CREATED,new HashSet<String>());
        editor.putStringSet(Constants.PREFS_USER_POLICIES_COMMENTED,new HashSet<String>());
        editor.putStringSet(Constants.PREFS_USER_POLICIES_VOTED,new HashSet<String>());
        editor.putStringSet(Constants.PREFS_USER_SWAPS_CREATED,new HashSet<String>());
        editor.putStringSet(Constants.PREFS_USER_SWAPS_COMMENTED,new HashSet<String>());
        editor.putStringSet(Constants.PREFS_USER_SWAPS_VOTED,new HashSet<String>());
        editor.putBoolean(Constants.EMAIL_VERIFIED,false);
        editor.apply();
        UserInfo info = new UserInfo(mUser.getDisplayName(), prefs.getString(Constants.PREFS_USER_PARTY,""), 0,
                0, 0, 0, new ArrayList<String>(),new ArrayList<String>(),new ArrayList<String>(),
                new ArrayList<String>(),new ArrayList<String>(),new ArrayList<String>());
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("UserInfo");
        ref.child(prefs.getString(Constants.PREFS_USER_ID,"")).setValue(info);
    }

    private void showVerifyEmailAlert(){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(getResources().getString(R.string.verify_email_title));
        alertDialog.setMessage(getResources().getString(R.string.verify_email_message));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.verify_email_yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        mUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(LaunchActivity.this,
                                            getResources().getString(R.string.verify_email_sent), Toast.LENGTH_LONG)
                                            .show();
                                    AuthUI.getInstance().signOut(LaunchActivity.this);
                                } else {
                                    Toast.makeText(LaunchActivity.this,
                                            getResources().getString(R.string.verify_email_send_fail), Toast.LENGTH_LONG)
                                            .show();
                                }
                            }
                        });
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getResources().getString(R.string.verify_email_no),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.launch_view_policies_button:
                Intent toPolicies = new Intent(LaunchActivity.this, PolicyActivity.class);
                startActivity(toPolicies);
                break;
        }
    }
}
