package com.politiswap.politiswap.NewVersion;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.politiswap.politiswap.R;
import com.politiswap.politiswap.databinding.ActivityLaunchBinding;

import java.util.Arrays;

public class LaunchActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityLaunchBinding mBinding;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_launch);
        mBinding.launchViewPoliciesButton.setOnClickListener(this);

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
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(1000);
        anim.setStartOffset(750);
        mBinding.launchViewIcon.startAnimation(anim);
        mBinding.launchViewSwapsButton.startAnimation(anim);
        mBinding.launchViewPoliciesButton.startAnimation(anim);
        mBinding.launchViewLegislationButton.startAnimation(anim);
        mBinding.launchViewTutorialButton.startAnimation(anim);
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
