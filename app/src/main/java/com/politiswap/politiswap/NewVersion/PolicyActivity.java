package com.politiswap.politiswap.NewVersion;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.ads.AdRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.politiswap.politiswap.DataUtils.Policy;
import com.politiswap.politiswap.R;
import com.politiswap.politiswap.UiAdapters.OnBottomReachedListener;
import com.politiswap.politiswap.databinding.ActivityPolicyBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PolicyActivity extends AppCompatActivity implements View.OnClickListener,
        OnBottomReachedListener {

    private ActivityPolicyBinding mBinding;
    private RecyclerView recycler;
    private PolicyAdapter adapter;
    private SnapByOne mSnapHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy);

        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_policy);

        AdRequest request = new AdRequest.Builder().build();
        mBinding.adView.loadAd(request);

        launchWithTop();
        setListeners();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.nav_menu_button:
                ((DrawerLayout)findViewById(R.id.drawer_layout)).openDrawer(GravityCompat.START);
                ((TextView)findViewById(R.id.nav_header_user_text)).setText("Default Name");
                break;
            case R.id.overflow_menu:
                showPopUp(v);
                break;
            case R.id.bar_top_layout:
                getFireBaseTop();
                break;
            case R.id.bar_new_layout:
                getFirebaseNew();
                break;
            case R.id.bar_create_layout:
                getFirebaseCreated();
                break;
        }
    }

    private void setListeners(){
        ImageButton navButton = mBinding.topOptionsIncludedLayout.navMenuButton;
        navButton.setOnClickListener(this);
        mBinding.topOptionsIncludedLayout.overflowMenu.setOnClickListener(this);
        mBinding.barTopLayout.setOnClickListener(this);
        mBinding.barNewLayout.setOnClickListener(this);
        mBinding.barCreateLayout.setOnClickListener(this);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void launchWithTop(){
        recycler = findViewById(R.id.policy_recycler);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false));
        adapter = new PolicyAdapter(this);
        recycler.setAdapter(adapter);
        mSnapHelper = new SnapByOne();
        mSnapHelper.attachToRecyclerView(recycler);

        getFireBaseTop();
    }

    private void getFireBaseTop(){
        Query query = FirebaseDatabase.getInstance().getReference("Policies")
                .orderByChild("netWanted").limitToLast(20);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final List<Policy> tempPolicies = new ArrayList<>();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Policy thisPolicy = snap.getValue(Policy.class);
                    tempPolicies.add(thisPolicy);
                }
                Collections.reverse(tempPolicies);
                adapter.setPolicies(tempPolicies,false);
                recycler.scrollToPosition(0);
                mSnapHelper.setStartingPosition(0);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getFirebaseNew(){
        Query query = FirebaseDatabase.getInstance().getReference("Policies")
                .limitToLast(20);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final List<Policy> tempPolicies = new ArrayList<>();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Policy thisPolicy = snap.getValue(Policy.class);
                    tempPolicies.add(thisPolicy);
                }
                adapter.setPolicies(tempPolicies,false);
                recycler.scrollToPosition(0);
                mSnapHelper.setStartingPosition(0);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getFirebaseCreated(){

    }

    private void showPopUp(View v){
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_main, popup.getMenu());
        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_signout:
                        AuthUI.getInstance().signOut(PolicyActivity.this);
                        startActivity(new Intent(PolicyActivity.this, LaunchActivity.class));
                        break;
                    case R.id.action_privacy:
                        Intent toPrivacy = new Intent(PolicyActivity.this, PrivacyActivity.class);
                        startActivity(toPrivacy);
                        break;
                    default:
                        break;

                }
                return true;
            }
        });
    }

    @Override
    public void onBottomReached() {

    }
}
