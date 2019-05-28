package com.politiswap.politiswap.DataUtils.Tasks;

import android.content.DialogInterface;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import android.widget.TextView;
import android.widget.Toast;

import com.politiswap.politiswap.DataUtils.UserInfo;
import com.politiswap.politiswap.MainActivity;
import com.politiswap.politiswap.R;
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

import java.util.Arrays;

public class SignInManager {

    private MainActivity mActivity;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseUser mUser;

    public SignInManager(MainActivity activity){
        mActivity = activity;
    }

    public void ManageSignIn(){
        mFirebaseAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mUser = firebaseAuth.getCurrentUser();
                if (mUser != null){
                    if (MainActivity.isFresh) {
                        new FirebaseRetrievalCalls(mActivity, false).getTopSwaps();
                    }
                    MainActivity.IS_GUEST = mUser.isAnonymous();
                    MainActivity.USER_ID = mUser.getUid();
                    if (!MainActivity.IS_GUEST){
                        getStartingInfo();
                    } else {
                        ((TextView)mActivity.findViewById(R.id.user_id_view))
                                .setText(mActivity.getResources().getString(R.string.guest));
                    }
                } else {
                    mActivity.startActivityForResult(
                            AuthUI.getInstance().createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.AnonymousBuilder().build(),
                                            new AuthUI.IdpConfig.EmailBuilder().build(),
                                            new AuthUI.IdpConfig.GoogleBuilder().build()))
                                    .build(),
                            123);
                }
            }
        };
    }

    public void getStartingInfo(){
        FirebaseDatabase.getInstance().getReference("UserInfo").child(MainActivity.USER_ID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserInfo info = dataSnapshot.getValue(UserInfo.class);
                        if (info != null) {
                            MainActivity.USERNAME = info.getUsername();
                            MainActivity.PARTY = info.getParty();
                            MainActivity.USER_VOTE_POINTS = info.getVotePoints();
                            MainActivity.USER_SWAP_POINTS = info.getSwapCreatedPoints();
                            MainActivity.USER_POLICY_POINTS = info.getPolicyCreatedPoints();
                            MainActivity.USER_OVERALL_POINTS = info.getOverallPoints();
                            ((TextView)mActivity.findViewById(R.id.user_id_view))
                                    .setText(MainActivity.USERNAME + "       " + MainActivity.PARTY);
                            showVerifyEmailAlert();
                            new FirebaseRetrievalCalls(mActivity, false).getInitialLists();
                        } else {
                            getPartyAndEnter();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public void getPartyAndEnter(){
        AlertDialog alertDialog = new AlertDialog.Builder(mActivity).create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setTitle(mActivity.getResources().getString(R.string.party_choice_dialog_title));
        alertDialog.setMessage(mActivity.getResources().getString(R.string.party_choice_dialog_message));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, mActivity.getResources().getString(R.string.party_choice_dialog_democrat),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.PARTY = "Democrat";
                        dialog.dismiss();
                        userIntoDatabase();
                        showVerifyEmailAlert();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, mActivity.getResources().getString(R.string.party_choice_dialog_republican),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MainActivity.PARTY = "Republican";
                        dialogInterface.dismiss();
                        userIntoDatabase();
                        showVerifyEmailAlert();
                    }
                });
        alertDialog.show();
    }

    public void showVerifyEmailAlert(){
        if (!mUser.isEmailVerified()){
            MainActivity.IS_GUEST = true;
            MainActivity.ASKED_ABOUT_EMAIL = true;
            AlertDialog alertDialog = new AlertDialog.Builder(mActivity).create();
            alertDialog.setTitle(mActivity.getResources().getString(R.string.verify_email_title));
            alertDialog.setMessage(mActivity.getResources().getString(R.string.verify_email_message));
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, mActivity.getResources().getString(R.string.verify_email_yes),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            mUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(mActivity,
                                                mActivity.getResources().getString(R.string.verify_email_sent), Toast.LENGTH_LONG)
                                                .show();
                                    } else {
                                        Toast.makeText(mActivity,
                                                mActivity.getResources().getString(R.string.verify_email_send_fail), Toast.LENGTH_LONG)
                                                .show();
                                    }
                                }
                            });
                        }
                    });
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, mActivity.getResources().getString(R.string.verify_email_neutral),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            AuthUI.getInstance().signOut(mActivity);
                            MainActivity.ASKED_ABOUT_EMAIL = false;
                        }
                    });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, mActivity.getResources().getString(R.string.verify_email_no),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        } else {
            MainActivity.IS_GUEST = false;
        }
    }

    public void userIntoDatabase(){
        MainActivity.USERNAME = mUser.getDisplayName();
        UserInfo info = new UserInfo(MainActivity.USERNAME, MainActivity.PARTY, 0, 0, 0, 0);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("UserInfo");
        ref.child(MainActivity.USER_ID).setValue(info);
        MainActivity.USER_OVERALL_POINTS = 0;
        MainActivity.USER_POLICY_POINTS = 0;
        MainActivity.USER_SWAP_POINTS = 0;
        MainActivity.USER_VOTE_POINTS = 0;
        ((TextView)mActivity.findViewById(R.id.user_id_view))
                .setText(MainActivity.USERNAME + "       " + MainActivity.PARTY);
    }

    public void loginOptionFrag(){
        AlertDialog alertDialog = new AlertDialog.Builder(mActivity).create();
        alertDialog.setTitle(mActivity.getResources().getString(R.string.guest_policy_fab_title));
        alertDialog.setMessage(mActivity.getResources().getString(R.string.guest_policy_fab_message));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, mActivity.getResources().getString(R.string.guest_policy_fab_yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        mActivity.startActivityForResult(
                                AuthUI.getInstance().createSignInIntentBuilder()
                                        .setIsSmartLockEnabled(false)
                                        .setAvailableProviders(Arrays.asList(
                                                new AuthUI.IdpConfig.AnonymousBuilder().build(),
                                                new AuthUI.IdpConfig.EmailBuilder().build(),
                                                new AuthUI.IdpConfig.GoogleBuilder().build()))
                                        .build(),
                                123);
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, mActivity.getResources().getString(R.string.guest_policy_fab_no),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        alertDialog.show();
    }

    public void addListener(){
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    public void removeListener(){
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }
}
