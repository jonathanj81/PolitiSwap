package com.politiswap.politiswap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.politiswap.politiswap.DataUtils.Recent.Bill;
import com.politiswap.politiswap.UiAdapters.OnBottomReachedListener;
import com.politiswap.politiswap.DataUtils.Policy;
import com.politiswap.politiswap.DataUtils.Recent.RecentBills;
import com.politiswap.politiswap.DataUtils.Searched.SearchedBills;
import com.politiswap.politiswap.DataUtils.Swap;
import com.politiswap.politiswap.DataUtils.Tasks.BillResultsAsync;
import com.politiswap.politiswap.DataUtils.Tasks.ConnectionAsyncTask;
import com.politiswap.politiswap.DataUtils.Tasks.FirebaseRetrievalCalls;
import com.politiswap.politiswap.DataUtils.Tasks.SearchedBillsAsync;
import com.politiswap.politiswap.DataUtils.Tasks.SignInManager;
import com.politiswap.politiswap.DialogFragments.CreatePolicyFragment;
import com.politiswap.politiswap.DialogFragments.CreateSwapFragment;
import com.politiswap.politiswap.TabManagement.TopTabManager;
import com.politiswap.politiswap.UiAdapters.LegislationAdapter;
import com.politiswap.politiswap.UiAdapters.PolicyAdapter;
import com.politiswap.politiswap.UiAdapters.SwapsAdapter;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

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
    private static final String RETAINED_POLICY_FRAGMENT_NAME = "retained_policy_frag";
    private static final String RETAINED_SWAP_FRAGMENT_NAME = "retained_swap_frag";
    private static final String ADAPTER_STATE = "adapter_state";
    private static final String RECYCLER_STATE = "recycler_state";
    private static final String POLICIES_BEFORE_DESTROY = "policies_saved";
    private static final String SWAPS_BEFORE_DESTROY = "swaps_saved";
    private static final String RECENT_BILLS_BEFORE_DESTROY = "recent_bills_saved";
    private static final String SEARCHED_BILLS_BEFORE_DESTROY = "searched_bills_saved";
    private static final String CURRENT_SEARCH_AREA = "current_search_area";

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
    public static int mAdapterNeeded;
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
    public static String mResult = "";
    public static boolean isLand;
    public static boolean isFresh;

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

        if (savedInstanceState != null) {
            isFresh = false;
            mAdapterNeeded = savedInstanceState.getInt(ADAPTER_STATE);
            recyclerViewState = savedInstanceState.getParcelable(RECYCLER_STATE);
            mCurrentAreaSubject = savedInstanceState.getString(CURRENT_SEARCH_AREA);

            mTopTabManager.setTopTabs(mAdapterNeeded / 3, mAdapterNeeded % 3, true);
            switch (mAdapterNeeded) {
                case 0:
                    newSwapsSent(savedInstanceState.<Swap>getParcelableArrayList(SWAPS_BEFORE_DESTROY), false);
                    break;
                case 1:
                    newSwapsSent(savedInstanceState.<Swap>getParcelableArrayList(SWAPS_BEFORE_DESTROY), false);
                    break;
                case 2:
                    newSwapsSent(savedInstanceState.<Swap>getParcelableArrayList(SWAPS_BEFORE_DESTROY), false);
                    break;
                case 3:
                    newPoliciesSent(savedInstanceState.<Policy>getParcelableArrayList(POLICIES_BEFORE_DESTROY), false);
                    break;
                case 4:
                    newPoliciesSent(savedInstanceState.<Policy>getParcelableArrayList(POLICIES_BEFORE_DESTROY), false);
                    break;
                case 5:
                    newPoliciesSent(savedInstanceState.<Policy>getParcelableArrayList(POLICIES_BEFORE_DESTROY), false);
                    break;
                case 6:
                    newSwapsSent(savedInstanceState.<Swap>getParcelableArrayList(SWAPS_BEFORE_DESTROY), false);
                    break;
                case 7:
                    newPoliciesSent(savedInstanceState.<Policy>getParcelableArrayList(POLICIES_BEFORE_DESTROY), false);
                    break;
                case 8:
                    //skip
                    break;
                case 9:
                    mLegislationAdapter.setRefreshedRecentBills(savedInstanceState.<Bill>getParcelableArrayList(RECENT_BILLS_BEFORE_DESTROY));
                    mRecyclerView.setAdapter(mLegislationAdapter);
                    if (recyclerViewState != null) {
                        mRecyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
                        recyclerViewState = null;
                    }
                    break;
                case 10:
                    List<com.politiswap.politiswap.DataUtils.Searched.Bill> bills = savedInstanceState.getParcelableArrayList(SEARCHED_BILLS_BEFORE_DESTROY);
                    if (bills != null && bills.size() > 0) {
                        mLegislationAdapter = new LegislationAdapter(1, MainActivity.this);
                        mLegislationAdapter.setRefreshedSearchedBills(bills);
                        mRecyclerView.setAdapter(mLegislationAdapter);
                        if (recyclerViewState != null) {
                            mRecyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
                            recyclerViewState = null;
                        }
                    } else {
                        ((TabLayout) findViewById(R.id.included_two_tab_layout)).getTabAt(1).select();
                    }
                    break;
            }

            if (savedInstanceState.containsKey(RETAINED_SWAP_FRAGMENT_NAME)) {
                mSwapFrag = (CreateSwapFragment) getSupportFragmentManager().getFragment(savedInstanceState, RETAINED_SWAP_FRAGMENT_NAME);
            }
        } else {
            isFresh = true;
            mTopTabManager.setTopTabs(0, 0, true);
        }

        mSignInManager = new SignInManager(this);
        mSignInManager.ManageSignIn();

        if (isLand) {
            ((TextView) findViewById(R.id.user_mini_overall_score_view))
                    .setText(String.format(getResources().getString(R.string.user_score_overall), String.valueOf(USER_OVERALL_POINTS)));
            ((TextView) findViewById(R.id.user_mini_swaps_points_view))
                    .setText(String.format(getResources().getString(R.string.user_score_swap_creation), String.valueOf(USER_SWAP_POINTS)));
            ((TextView) findViewById(R.id.user_mini_policies_points_view))
                    .setText(String.format(getResources().getString(R.string.user_score_policy_creation), String.valueOf(USER_POLICY_POINTS)));
            ((TextView) findViewById(R.id.user_mini_votes_points_view))
                    .setText(String.format(getResources().getString(R.string.user_score_votes), String.valueOf(USER_VOTE_POINTS)));
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
                            ((AdView) findViewById(R.id.adView)).loadAd(new AdRequest.Builder().build());
                            if (mResult.isEmpty()) {
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
                            }
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
        this.registerReceiver(mNetworkReceiver, intentFilter);
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
            case R.id.action_signout:
                AuthUI.getInstance().signOut(this);
                ASKED_ABOUT_EMAIL = false;
                break;
            case R.id.action_privacy:
                Intent toPrivacy = new Intent(MainActivity.this, PrivacyActivity.class);
                startActivity(toPrivacy);
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
                if (isLand) {
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
            cancelButton.setText(getResources().getString(R.string.policies_error_message));
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
            if (recyclerViewState != null) {
                mRecyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
                recyclerViewState = null;
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
            cancelButton.setText(getResources().getString(R.string.swaps_error_message));
            if (mAdapterNeeded != 2) {
                findViewById(R.id.craft_subject_recycler_and_placeholder_frame).setVisibility(View.GONE);
                findViewById(R.id.craft_policy_step_number_1).setVisibility(View.GONE);
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cancelButton.setVisibility(View.GONE);
                        switch (mAdapterNeeded) {
                            case 0:
                                new FirebaseRetrievalCalls(MainActivity.this, false).getTopSwaps();
                                break;
                            case 1:
                                new FirebaseRetrievalCalls(MainActivity.this, false).getNewSwaps();
                                break;
                            case 6:
                                new FirebaseRetrievalCalls(MainActivity.this, false).getUserSwaps();
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
            if (recyclerViewState != null) {
                mRecyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
                recyclerViewState = null;
            }
            mSwapsAdapter.setSwaps(swaps, fromScroll);
            mRecyclerView.setAdapter(mSwapsAdapter);
        }
    }

    @Override
    public void searchedBillsCallback(SearchedBills results) {
        if (results != null && results.getStatus().equals("OK")) {
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
        } else {
            findViewById(R.id.alt_search_layout).setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            mProgressBar.hide();
            findViewById(R.id.alt_search_legislation_layout).setVisibility(View.GONE);
            findViewById(R.id.craft_subject_available_recycler).setVisibility(View.GONE);
            findViewById(R.id.craft_subject_title_text_view).setVisibility(View.GONE);
            final Button cancelButton = findViewById(R.id.alt_search_cancel_button);
            cancelButton.setVisibility(View.VISIBLE);
            cancelButton.setText(getResources().getString(R.string.legislation_error_message));
            findViewById(R.id.craft_subject_recycler_and_placeholder_frame).setVisibility(View.GONE);
            findViewById(R.id.craft_policy_step_number_1).setVisibility(View.GONE);
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cancelButton.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.GONE);
                    findViewById(R.id.alt_search_legislation_layout).setVisibility(View.VISIBLE);
                    findViewById(R.id.alt_search_layout).setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    public void recentBillsCallback(RecentBills results) {
        if (results != null && results.getStatus().equals("OK")) {
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
        } else {
            findViewById(R.id.alt_search_layout).setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            mProgressBar.hide();
            findViewById(R.id.craft_subject_available_recycler).setVisibility(View.GONE);
            findViewById(R.id.craft_subject_title_text_view).setVisibility(View.GONE);
            final Button cancelButton = findViewById(R.id.alt_search_cancel_button);
            cancelButton.setVisibility(View.VISIBLE);
            cancelButton.setText(getResources().getString(R.string.legislation_error_message));
            findViewById(R.id.craft_subject_recycler_and_placeholder_frame).setVisibility(View.GONE);
            findViewById(R.id.craft_policy_step_number_1).setVisibility(View.GONE);
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cancelButton.setVisibility(View.GONE);
                    new BillResultsAsync(MainActivity.this, 0).execute();
                }
            });
        }
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
                new BillResultsAsync(MainActivity.this, mBillOffset * 20).execute();
                mProgressBar.show();
                break;
            case 10:
                mBillOffset++;
                mAlreadyPaging = true;
                recyclerViewState = mRecyclerView.getLayoutManager().onSaveInstanceState();
                new SearchedBillsAsync(MainActivity.this, mLegislationQuery, mBillOffset * 20).execute();
                mProgressBar.show();
                break;

        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(RECYCLER_STATE, mRecyclerView.getLayoutManager().onSaveInstanceState());
        ArrayList<Policy> policies = new ArrayList<>(mPolicyAdapter.getPolicies());
        outState.putParcelableArrayList(POLICIES_BEFORE_DESTROY, policies);
        switch (mAdapterNeeded) {
            case 0:
                outState.putParcelableArrayList(SWAPS_BEFORE_DESTROY, new ArrayList<Parcelable>(mSwapsAdapter.getSwaps()));
                break;
            case 1:
                outState.putParcelableArrayList(SWAPS_BEFORE_DESTROY, new ArrayList<Parcelable>(mSwapsAdapter.getSwaps()));
                break;
            case 2:
                outState.putParcelableArrayList(SWAPS_BEFORE_DESTROY, new ArrayList<Parcelable>(mSwapsAdapter.getSwaps()));
                break;
            case 3:
                outState.putParcelableArrayList(POLICIES_BEFORE_DESTROY, new ArrayList<Parcelable>(mPolicyAdapter.getPolicies()));
                break;
            case 4:
                outState.putParcelableArrayList(POLICIES_BEFORE_DESTROY, new ArrayList<Parcelable>(mPolicyAdapter.getPolicies()));
                break;
            case 5:
                outState.putParcelableArrayList(POLICIES_BEFORE_DESTROY, new ArrayList<Parcelable>(mPolicyAdapter.getPolicies()));
                break;
            case 6:
                outState.putParcelableArrayList(SWAPS_BEFORE_DESTROY, new ArrayList<Parcelable>(mSwapsAdapter.getSwaps()));
                break;
            case 7:
                outState.putParcelableArrayList(POLICIES_BEFORE_DESTROY, new ArrayList<Parcelable>(mPolicyAdapter.getPolicies()));
                break;
            case 8:
                //skip
                break;
            case 9:
                outState.putParcelableArrayList(RECENT_BILLS_BEFORE_DESTROY, new ArrayList<Parcelable>(mLegislationAdapter.getRecentBills()));
                break;
            case 10:
                outState.putParcelableArrayList(SEARCHED_BILLS_BEFORE_DESTROY, new ArrayList<Parcelable>(mLegislationAdapter.getSearchedBills()));
                break;
        }
        outState.putInt(ADAPTER_STATE, mAdapterNeeded);
        Fragment swapFrag = getSupportFragmentManager().findFragmentByTag(CREATE_SWAP_FRAGMENT_NAME);
        if (swapFrag != null) {
            getSupportFragmentManager().putFragment(outState, RETAINED_SWAP_FRAGMENT_NAME, swapFrag);
        }
        Fragment policyFrag = getSupportFragmentManager().findFragmentByTag(CREATE_POLICY_FRAGMENT_NAME);
        if (policyFrag != null) {
            getSupportFragmentManager().putFragment(outState, RETAINED_POLICY_FRAGMENT_NAME, policyFrag);
        }
        outState.putString(CURRENT_SEARCH_AREA, mCurrentAreaSubject);
        super.onSaveInstanceState(outState);
    }
}
