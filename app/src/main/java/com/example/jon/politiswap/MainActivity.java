package com.example.jon.politiswap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.jon.politiswap.TabManagement.BottomTabManager;
import com.example.jon.politiswap.UiAdapters.OnBottomReachedListener;
import com.example.jon.politiswap.DataUtils.Policy;
import com.example.jon.politiswap.DataUtils.Recent.RecentBills;
import com.example.jon.politiswap.DataUtils.Searched.SearchedBills;
import com.example.jon.politiswap.DataUtils.Swap;
import com.example.jon.politiswap.DataUtils.Tasks.BillResultsAsync;
import com.example.jon.politiswap.DataUtils.Tasks.ConnectionAsyncTask;
import com.example.jon.politiswap.DataUtils.Tasks.FirebaseRetrievalCalls;
import com.example.jon.politiswap.DataUtils.Tasks.SearchedBillsAsync;
import com.example.jon.politiswap.DataUtils.Tasks.SignInManager;
import com.example.jon.politiswap.DialogFragments.CreatePolicyFragment;
import com.example.jon.politiswap.DialogFragments.CreateSwapFragment;
import com.example.jon.politiswap.TabManagement.TopTabManager;
import com.example.jon.politiswap.UiAdapters.LegislationAdapter;
import com.example.jon.politiswap.UiAdapters.PolicyAdapter;
import com.example.jon.politiswap.UiAdapters.SwapsAdapter;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.ArrayList;
import java.util.List;

import javax.security.auth.login.LoginException;

public class MainActivity extends AppCompatActivity implements BillResultsAsync.BillHandler,
        FirebaseRetrievalCalls.RetrieveFirebase, SearchedBillsAsync.SearchedBillsHandler, OnBottomReachedListener {

    private CollapsingToolbarLayout toolLayout;
    private TextView titleTextView;
    private ContentLoadingProgressBar mProgressBar;
    private FloatingActionButton mFab;
    private RecyclerView mRecyclerView;
    private LegislationAdapter mLegislationAdapter;
    private SwapsAdapter mSwapsAdapter;
    private PolicyAdapter mPolicyAdapter;
    private boolean mAlreadyPaging = false;
    private boolean isLand;
    public static int mBillOffset = 0;
    private BroadcastReceiver mNetworkReceiver;

    private SignInManager mSignInManager;
    public static Parcelable recyclerViewState;

    private static final String CREATE_POLICY_FRAGMENT_NAME = "policy_frag";
    private static final String CREATE_SWAP_FRAGMENT_NAME = "swap_frag";
    private static final String ADAPTER_STATE = "adapter_state";
    private static final String RECYCLER_STATE  = "recycler_state";

    public static boolean IS_GUEST;
    public static String USERNAME;
    public static String PARTY;
    public static String USER_ID;
    public static double USER_OVERALL_POINTS;
    public static double USER_SWAP_POINTS;
    public static double USER_POLICY_POINTS;
    public static double USER_VOTE_POINTS;
    public static boolean ASKED_ABOUT_EMAIL = false;
    public static String mTaskWithPriority;
    public static List<String> mUserCreated = new ArrayList<>();
    public static List<String> mUserVoted = new ArrayList<>();
    public static List<String> mUserSwapCreated = new ArrayList<>();
    public static List<String> mUserSwapVoted = new ArrayList<>();
    public static int mAdapterNeeded = 0;
    public static TopTabManager mTopTabManager;
    public static CreateSwapFragment mSwapFrag;
    public static String mLegislationQuery = "";
    public static String mLastFirebaseNode = "";
    public static boolean isAtEnd = false;
    public static int mLastPolicyNetWanted = Integer.MAX_VALUE;
    public static int mLastPolicyNetOffset = 0;
    public static double mLastSwapNetWanted = Integer.MAX_VALUE;
    public static int mLastSwapNetOffset = 0;
    public static String mCurrentAreaSubject;
    public static String mResult;

    private static final String PREFS_WIDGET_KEY = "user_id_widget_memory";
    private static final String USER_ID_KEY = "user_id_widget_key";

    private FirebaseFunctions mFunctions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isLand = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

        setToolbarLayout();
        prepAdapters();
        prepRecycler();
        prepFab();

        mTopTabManager = new TopTabManager(this);

        mSignInManager = new SignInManager(this);
        mSignInManager.ManageSignIn();

        if (savedInstanceState != null){
            mAdapterNeeded = savedInstanceState.getInt(ADAPTER_STATE);
            recyclerViewState = savedInstanceState.getParcelable(RECYCLER_STATE);

            mTopTabManager.setTopTabs(mAdapterNeeded / 3, mAdapterNeeded % 3);
            mRecyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
        } else {
            mTopTabManager.setTopTabs(0, 0);
        }

        if (isLand){
            ((TextView)findViewById(R.id.user_mini_overall_score_view))
                    .setText(String.format(getResources().getString(R.string.user_score_overall),String.valueOf(USER_OVERALL_POINTS)));
            ((TextView)findViewById(R.id.user_mini_swaps_points_view))
                    .setText(String.format(getResources().getString(R.string.user_score_swap_creation),String.valueOf(USER_SWAP_POINTS)));
            ((TextView)findViewById(R.id.user_mini_policies_points_view))
                    .setText(String.format(getResources().getString(R.string.user_score_policy_creation),String.valueOf(USER_POLICY_POINTS)));
            ((TextView)findViewById(R.id.user_mini_votes_points_view))
                    .setText(String.format(getResources().getString(R.string.user_score_votes),String.valueOf(USER_VOTE_POINTS)));
        }

        SharedPreferences prefs = getSharedPreferences(PREFS_WIDGET_KEY, Context.MODE_PRIVATE);
        if (MainActivity.USER_ID != null && MainActivity.USER_ID.length() > 0) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(USER_ID_KEY, MainActivity.USER_ID);
            editor.apply();
        }

        mNetworkReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                new ConnectionAsyncTask(new ConnectionAsyncTask.InternetCheckListener() {
                    @Override
                    public void onInternetConnect(boolean isConnected) {
                        if (isConnected) {
                            mRecyclerView.setVisibility(View.VISIBLE);
                            findViewById(R.id.alt_no_network_layout).setVisibility(View.GONE);
                            //mTopTabManager.setTopTabs(0);
                        } else {
                            mRecyclerView.setVisibility(View.GONE);
                            findViewById(R.id.alt_no_network_layout).setVisibility(View.VISIBLE);
                        }
                    }
                }).execute();
            }
        };

        mFunctions = FirebaseFunctions.getInstance();
        mFunctions.getHttpsCallable("findPub").call("nothing").addOnCompleteListener(new OnCompleteListener<HttpsCallableResult>() {
            @Override
            public void onComplete(@NonNull Task<HttpsCallableResult> task) {
                mResult = task.getResult().getData().toString();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mResult = "";
            }
        });

        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetworkReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mNetworkReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSignInManager.addListener();
        ((AdView)findViewById(R.id.adView)).loadAd(new AdRequest.Builder().build());
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSignInManager.removeListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_signout:
                AuthUI.getInstance().signOut(this);
                ASKED_ABOUT_EMAIL = false;
                break;
            default:
                break;

        }
        return true;
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
                int bottomMarginCollapsed;
                int leftMargin;
                int bottomMarginExpanded;
                if(isLand) {
                    bottomMarginCollapsed = (int) (48 * (getResources().getDisplayMetrics().density));
                    leftMargin = (int) (64 * (getResources().getDisplayMetrics().density));
                    bottomMarginExpanded = (int) (96 * (getResources().getDisplayMetrics().density));
                } else {
                    bottomMarginCollapsed = (int) (56 * (getResources().getDisplayMetrics().density));
                    leftMargin = (int) (72 * (getResources().getDisplayMetrics().density));
                    bottomMarginExpanded = (int) (112 * (getResources().getDisplayMetrics().density));
                }
                verticalOffset = Math.abs(verticalOffset);

                ViewGroup.MarginLayoutParams textParams = (ViewGroup.MarginLayoutParams) titleTextView.getLayoutParams();
                ViewGroup.MarginLayoutParams toolParams = (ViewGroup.MarginLayoutParams) toolbar.getLayoutParams();
                textParams.setMargins(leftMargin, 0, 0,
                        Math.max(bottomMarginCollapsed, bottomMarginExpanded - verticalOffset / 2));
                titleTextView.setLayoutParams(textParams);
                toolParams.setMargins(0, 0, 0,
                        Math.max(bottomMarginCollapsed, bottomMarginExpanded - verticalOffset / 2));
                toolbar.setLayoutParams(toolParams);

            }
        });
    }

    private void prepAdapters() {
        mLegislationAdapter = new LegislationAdapter(this);
        mSwapsAdapter = new SwapsAdapter(this);
        mPolicyAdapter = new PolicyAdapter(this);
    }

    private void prepRecycler() {
        mRecyclerView = findViewById(R.id.content_recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setHasFixedSize(true);
        mProgressBar = findViewById(R.id.refreshing_bills_progress_bar);
    }

    private void prepFab() {
        mFab = findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (IS_GUEST) {
                    mSignInManager.loginOptionFrag();
                } else {
                    switch (mTopTabManager.getTabType()) {
                        case 0:
                            recyclerViewState = mRecyclerView.getLayoutManager().onSaveInstanceState();
                            mSwapFrag = CreateSwapFragment.newInstance(null);
                            mSwapFrag.show(getSupportFragmentManager(), CREATE_SWAP_FRAGMENT_NAME);
                            break;
                        case 1:
                            recyclerViewState = mRecyclerView.getLayoutManager().onSaveInstanceState();
                            CreatePolicyFragment policyFrag = CreatePolicyFragment.newInstance(null);
                            policyFrag.show(getSupportFragmentManager(), CREATE_POLICY_FRAGMENT_NAME);
                    }
                }
            }
        });
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    @Override
    public void newPoliciesSent(List<Policy> policies, boolean fromScroll) {
        mProgressBar.hide();
        if (policies.size() == 0) {
            findViewById(R.id.alt_search_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.craft_subject_available_recycler).setVisibility(View.GONE);
            findViewById(R.id.craft_subject_title_text_view).setVisibility(View.GONE);
            final Button cancelButton = findViewById(R.id.alt_search_cancel_button);
            cancelButton.setVisibility(View.VISIBLE);
            cancelButton.setText("No policies found.  Try Again?");
            if (mAdapterNeeded != 5) {
                findViewById(R.id.craft_subject_recycler_and_placeholder_frame).setVisibility(View.GONE);
                findViewById(R.id.craft_policy_step_number_1).setVisibility(View.GONE);
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cancelButton.setVisibility(View.GONE);
                        switch (mAdapterNeeded) {
                            case 3:
                                new FirebaseRetrievalCalls(MainActivity.this, false).getTopPolicies();
                                break;
                            case 4:
                                new FirebaseRetrievalCalls(MainActivity.this, false).getNewPolicies();
                                break;
                            case 7:
                                new FirebaseRetrievalCalls(MainActivity.this, false).getUserPolicies();
                                break;
                        }
                    }
                });
            } else {
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cancelButton.setVisibility(View.GONE);
                        findViewById(R.id.craft_subject_available_recycler).setVisibility(View.VISIBLE);
                        findViewById(R.id.craft_subject_title_text_view).setVisibility(View.VISIBLE);
                    }
                });
            }
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            findViewById(R.id.alt_search_layout).setVisibility(View.GONE);
            if (fromScroll) {
                mRecyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
            }
            mPolicyAdapter.setPolicies(policies, fromScroll);
            mRecyclerView.setAdapter(mPolicyAdapter);
        }
    }

    @Override
    public void newSwapsSent(List<Swap> swaps, boolean fromScroll) {
        mProgressBar.hide();
        if (swaps.size() == 0) {
            findViewById(R.id.alt_search_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.craft_subject_available_recycler).setVisibility(View.GONE);
            findViewById(R.id.craft_subject_title_text_view).setVisibility(View.GONE);
            final Button cancelButton = findViewById(R.id.alt_search_cancel_button);
            cancelButton.setVisibility(View.VISIBLE);
            cancelButton.setText("No swaps found.  Try Again?");
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cancelButton.setVisibility(View.GONE);
                    findViewById(R.id.craft_subject_available_recycler).setVisibility(View.VISIBLE);
                    findViewById(R.id.craft_subject_title_text_view).setVisibility(View.VISIBLE);
                }
            });
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            if (fromScroll) {
                if (fromScroll) {
                    mRecyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
                }
            }
            mSwapsAdapter.setSwaps(swaps, fromScroll);
            mRecyclerView.setAdapter(mSwapsAdapter);
        }
    }

    @Override
    public void searchedBillsCallback(SearchedBills results) {
        mRecyclerView.setVisibility(View.VISIBLE);
        findViewById(R.id.alt_search_legislation_layout).setVisibility(View.GONE);
        if (mLegislationAdapter.getType() != 1) {
            mLegislationAdapter = new LegislationAdapter(1, this);
        }
        mProgressBar.hide();
        if (mAlreadyPaging) {
            mRecyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
        }
        mAlreadyPaging = false;
        mLegislationAdapter.setBills(null, results);
        mRecyclerView.setAdapter(mLegislationAdapter);

    }

    @Override
    public void recentBillsCallback(RecentBills results) {
        if (mLegislationAdapter.getType() != 0) {
            mLegislationAdapter = new LegislationAdapter(0, this);
        }
        mProgressBar.hide();
        if (mAlreadyPaging) {
            mRecyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
        }
        mAlreadyPaging = false;
        mLegislationAdapter.setBills(results, null);
        mRecyclerView.setAdapter(mLegislationAdapter);
    }

    @Override
    public void onBottomReached() {
        switch (mAdapterNeeded) {
            case 0:
                recyclerViewState = mRecyclerView.getLayoutManager().onSaveInstanceState();
                new FirebaseRetrievalCalls(MainActivity.this, true).getTopSwaps();
                mProgressBar.show();
                break;
            case 1:
                recyclerViewState = mRecyclerView.getLayoutManager().onSaveInstanceState();
                new FirebaseRetrievalCalls(MainActivity.this, true).getNewSwaps();
                mProgressBar.show();
                break;
            case 2:
                recyclerViewState = mRecyclerView.getLayoutManager().onSaveInstanceState();
                new FirebaseRetrievalCalls(MainActivity.this, true).getSwapAreaSearch(mCurrentAreaSubject);
                mProgressBar.show();
                break;
            case 3:
                recyclerViewState = mRecyclerView.getLayoutManager().onSaveInstanceState();
                new FirebaseRetrievalCalls(MainActivity.this, true).getTopPolicies();
                mProgressBar.show();
                break;
            case 4:
                recyclerViewState = mRecyclerView.getLayoutManager().onSaveInstanceState();
                new FirebaseRetrievalCalls(MainActivity.this, true).getNewPolicies();
                mProgressBar.show();
                break;
            case 5:
                recyclerViewState = mRecyclerView.getLayoutManager().onSaveInstanceState();
                new FirebaseRetrievalCalls(MainActivity.this, true).getPolicyAreaSearch(mCurrentAreaSubject);
                mProgressBar.show();
                break;
            case 6:
                //skip
                break;
            case 7:
                //skip
                break;
            case 8:
                //skip
                break;
            case 9:
                mBillOffset++;
                mAlreadyPaging = true;
                recyclerViewState = mRecyclerView.getLayoutManager().onSaveInstanceState();
                new BillResultsAsync(MainActivity.this, mBillOffset*20).execute();
                mProgressBar.show();
                break;
            case 10:
                mBillOffset++;
                mAlreadyPaging = true;
                recyclerViewState = mRecyclerView.getLayoutManager().onSaveInstanceState();
                new SearchedBillsAsync(MainActivity.this, mLegislationQuery, mBillOffset*20).execute();
                mProgressBar.show();
                break;

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(ADAPTER_STATE, mAdapterNeeded);
        outState.putParcelable(RECYCLER_STATE, mRecyclerView.getLayoutManager().onSaveInstanceState());
        super.onSaveInstanceState(outState);
    }
}
