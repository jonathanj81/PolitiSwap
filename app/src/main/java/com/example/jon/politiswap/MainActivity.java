package com.example.jon.politiswap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Parcelable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.ActionBar;
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

import java.util.ArrayList;
import java.util.List;

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
    public static int mBillOffset = 0;
    private BroadcastReceiver mNetworkReceiver;

    private SignInManager mSignInManager;
    public static Parcelable recyclerViewState;

    private static final String CREATE_POLICY_FRAGMENT_NAME = "policy_frag";
    private static final String CREATE_SWAP_FRAGMENT_NAME = "swap_frag";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setToolbarLayout();
        prepAdapters();
        prepRecycler();
        prepFab();

        mTopTabManager = new TopTabManager(this);
        mTopTabManager.setTopTabs();

        mSignInManager = new SignInManager(this);
        mSignInManager.ManageSignIn();

        mNetworkReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                new ConnectionAsyncTask(new ConnectionAsyncTask.InternetCheckListener() {
                    @Override
                    public void onInternetConnect(boolean isConnected) {
                        if (isConnected) {
                            mRecyclerView.setVisibility(View.VISIBLE);
                            findViewById(R.id.alt_no_network_layout).setVisibility(View.GONE);
                            mTopTabManager.setTopTabs();
                        } else {
                            mRecyclerView.setVisibility(View.GONE);
                            findViewById(R.id.alt_no_network_layout).setVisibility(View.VISIBLE);
                        }
                    }
                }).execute();
            }
        };

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
            case R.id.action_faq:
                break;
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
                int bottomMarginCollapsed = (int) (56 * (getResources().getDisplayMetrics().density));
                int leftMargin = (int) (72 * (getResources().getDisplayMetrics().density));
                int bottomMarginExpanded = (int) (112 * (getResources().getDisplayMetrics().density));
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
}
