package com.example.jon.politiswap;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jon.politiswap.DataUtils.Policy;
import com.example.jon.politiswap.DataUtils.Recent.RecentBills;
import com.example.jon.politiswap.DataUtils.Tasks.BillResultsAsync;
import com.example.jon.politiswap.DataUtils.Tasks.FirebaseRetrievalCalls;
import com.example.jon.politiswap.DataUtils.UserInfo;
import com.example.jon.politiswap.DialogFragments.CreatePolicyFragment;
import com.example.jon.politiswap.TabManagement.TopTabManager;
import com.example.jon.politiswap.UiAdapters.LegislationAdapter;
import com.example.jon.politiswap.UiAdapters.PolicyAdapter;
import com.example.jon.politiswap.UiAdapters.SwapsAdapter;
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
import java.util.List;

public class MainActivity extends AppCompatActivity implements BillResultsAsync.BillHandler, FirebaseRetrievalCalls.RetrieveFirebase {

    private CollapsingToolbarLayout toolLayout;
    private TextView titleTextView;
    private ContentLoadingProgressBar mProgressBar;
    private FloatingActionButton mFab;
    private RecyclerView mRecyclerView;
    private LegislationAdapter mLegislationAdapter;
    private SwapsAdapter mSwapsAdapter;
    private PolicyAdapter mPolicyAdapter;
    private TopTabManager mTopTabManager;
    private int mAdapterNeeded = 0;
    private boolean mAlreadyPaging = false;
    private int mBillOffset = 0;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseUser mUser;

    private static final String CREATE_POLICY_FRAGMENT_NAME = "policy_frag";

    public static boolean IS_GUEST;
    public static String USERNAME;
    public static String PARTY;
    public static String USER_ID;

    private boolean mAlreadyAskedAboutEmail = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAuth = FirebaseAuth.getInstance();

        setToolbarLayout();
        prepAdapters();
        prepRecycler();
        prepFab();

        mTopTabManager = new TopTabManager(this);
        mTopTabManager.setTopTabs();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mUser = firebaseAuth.getCurrentUser();
                if (mUser != null){
                    IS_GUEST = mUser.isAnonymous();
                    USER_ID = mUser.getUid();
                    if (!IS_GUEST && !mAlreadyAskedAboutEmail){
                        getStartingInfo();
                    }
                } else {
                    startActivityForResult(
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

    private void setToolbarLayout() {

        titleTextView = findViewById(R.id.toolbar_title_textview);

        toolLayout = findViewById(R.id.toolbar_layout);
        toolLayout.setTitleEnabled(false);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowTitleEnabled(false);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_handshake);

        AppBarLayout appBarLayout = findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int bottomMarginCollapsed = (int) (56 * (getResources().getDisplayMetrics().density));
                int leftMargin = (int) (72 * (getResources().getDisplayMetrics().density));
                int bottomMarginExpanded = (int) (112 * (getResources().getDisplayMetrics().density));
                verticalOffset = Math.abs(verticalOffset);

                ViewGroup.MarginLayoutParams textParams = (ViewGroup.MarginLayoutParams) titleTextView.getLayoutParams();
                ViewGroup.MarginLayoutParams toolParams = (ViewGroup.MarginLayoutParams) toolbar.getLayoutParams();
                textParams.setMargins(leftMargin, 0, 0,
                        Math.max(bottomMarginCollapsed, bottomMarginExpanded - verticalOffset/2));
                titleTextView.setLayoutParams(textParams);
                toolParams.setMargins(0, 0, 0,
                        Math.max(bottomMarginCollapsed, bottomMarginExpanded - verticalOffset/2));
                toolbar.setLayoutParams(toolParams);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_faq:
                break;
            case R.id.action_signout:
                AuthUI.getInstance().signOut(this);
                mAlreadyAskedAboutEmail = false;
                break;
            default:
                break;

        }
        return true;
    }

    @Override
    public void recentBillsCallback(RecentBills results) {
        mProgressBar.hide();
        if (mAlreadyPaging){
            mRecyclerView.scrollToPosition(mLegislationAdapter.getItemCount()-1);
        }
        mAlreadyPaging = false;
        mLegislationAdapter.setBills(results);
        mRecyclerView.setAdapter(mLegislationAdapter);
    }

    private void prepAdapters(){
        mLegislationAdapter = new LegislationAdapter();
        mSwapsAdapter = new SwapsAdapter();
        mPolicyAdapter = new PolicyAdapter();
    }

    private void prepRecycler(){
        mRecyclerView = findViewById(R.id.content_recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setHasFixedSize(true);
        mProgressBar = findViewById(R.id.refreshing_bills_progress_bar);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    if (!mAlreadyPaging){
                        mAlreadyPaging = true;
                        mProgressBar.show();
                        switch (mAdapterNeeded/3){
                            case 0:
                                mProgressBar.hide();
                                break;
                            case 1:
                                mProgressBar.hide();
                                break;
                            case 2:
                                mProgressBar.hide();
                                break;
                            case 3:
                                mBillOffset++;
                                new BillResultsAsync(MainActivity.this, mBillOffset*20).execute();
                                break;
                            default:
                                break;

                        }
                    }

                }
            }
        });
    }

    private void prepFab(){
        mFab = findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (IS_GUEST) {
                    loginOptionFrag();
                } else {
                    switch (mTopTabManager.getTabType()) {
                        case 0:
                            break;
                        case 1:
                            FragmentManager fm = getSupportFragmentManager();
                            CreatePolicyFragment frag = CreatePolicyFragment.newInstance(null);
                            frag.show(fm, CREATE_POLICY_FRAGMENT_NAME);
                    }
                }
            }
        });
    }

    public int getBillOffset(){
        return mBillOffset;
    }

    public RecyclerView getRecyclerView(){
        return mRecyclerView;
    }

    private void loginOptionFrag(){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(getResources().getString(R.string.guest_policy_fab_title));
        alertDialog.setMessage(getResources().getString(R.string.guest_policy_fab_message));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.guest_policy_fab_yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startActivityForResult(
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
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getResources().getString(R.string.guest_policy_fab_no),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void showVerifyEmailAlert(){
        if (!mUser.isEmailVerified()){
            IS_GUEST = true;
            mAlreadyAskedAboutEmail = true;
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
                                        Toast.makeText(MainActivity.this,
                                                getResources().getString(R.string.verify_email_sent), Toast.LENGTH_LONG)
                                                .show();
                                    } else {
                                        Toast.makeText(MainActivity.this,
                                                getResources().getString(R.string.verify_email_send_fail), Toast.LENGTH_LONG)
                                                .show();
                                    }
                                }
                            });
                        }
                    });
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getResources().getString(R.string.verify_email_neutral),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            AuthUI.getInstance().signOut(MainActivity.this);
                            mAlreadyAskedAboutEmail = false;
                        }
                    });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getResources().getString(R.string.verify_email_no),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        } else {
            IS_GUEST = false;
        }
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
                            PARTY = "Democrat";
                            dialog.dismiss();
                            userIntoDatabase();
                            showVerifyEmailAlert();
                        }
                    });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getResources().getString(R.string.party_choice_dialog_republican),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            PARTY = "Republican";
                            dialogInterface.dismiss();
                            userIntoDatabase();
                            showVerifyEmailAlert();
                        }
                    });
            alertDialog.show();
    }

    private void userIntoDatabase(){
        USERNAME = mUser.getDisplayName();
        UserInfo info = new UserInfo(USERNAME, PARTY);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("UserInfo");
        ref.child(USER_ID).setValue(info);
    }

    private void getStartingInfo(){
        FirebaseDatabase.getInstance().getReference("UserInfo").child(USER_ID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserInfo info = dataSnapshot.getValue(UserInfo.class);
                        if (info != null) { ;
                            USERNAME = info.getUsername();
                            PARTY = info.getParty();
                            showVerifyEmailAlert();
                        } else {
                            getPartyAndEnter();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void newPoliciesSent(List<Policy> policies) {
        mProgressBar.hide();
        if (policies == null){
            findViewById(R.id.alt_search_layout).setVisibility(View.VISIBLE);
            Button cancelButton = findViewById(R.id.alt_search_cancel_button);
            cancelButton.setVisibility(View.VISIBLE);
            cancelButton.setText("No policies found.  Try Again?");
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mPolicyAdapter.setPolicies(policies);
            mRecyclerView.setAdapter(mPolicyAdapter);
        }
    }
}
