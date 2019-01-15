package com.example.jon.politiswap.DialogFragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jon.politiswap.DataUtils.Policy;
import com.example.jon.politiswap.DataUtils.Tasks.FirebaseRetrievalCalls;
import com.example.jon.politiswap.MainActivity;
import com.example.jon.politiswap.R;
import com.example.jon.politiswap.UiAdapters.CreateSubjectAvailableAdapter;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class CreatePolicyFragment extends DialogFragment implements CreateSubjectAvailableAdapter.SubjectChangeManager {

    private RecyclerView mSubjectAvailableRecycler;
    private RecyclerView mSubjectSelectedRecycler;
    private CreateSubjectAvailableAdapter mSubjectAvailableAdapter;
    private CreateSubjectAvailableAdapter mSubjectSelectedAdapter;
    private View mRootView;
    private int mCurrentPage = 0;
    private boolean mProceed = false;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private String mSummary;
    private String mTitle;
    private List<String> mSubjects;
    private String mParty = MainActivity.PARTY;
    private String mUsername = MainActivity.USERNAME;
    private String mUserId = MainActivity.USER_ID;
    private static final String RETAINED_SUMMARY = "retained_summary";
    private static final String RETAINED_TITLE = "retained_title";
    private static final String RETAINED_SUBJECTS = "retained_subjects";
    private static final String RETAINED_SCREEN_POSITION = "retained_screen_position";

    public CreatePolicyFragment() {

    }

    public static CreatePolicyFragment newInstance(Bundle args) {
        CreatePolicyFragment frag = new CreatePolicyFragment();
        frag.setArguments(args);
        return frag;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            mCurrentPage = savedInstanceState.getInt(RETAINED_SCREEN_POSITION);

            if (savedInstanceState.containsKey(RETAINED_SUBJECTS)){
                mSubjects = savedInstanceState.getStringArrayList(RETAINED_SUBJECTS);
                for (String subject : mSubjects) {
                    addSubject(subject);
                }
            }
            mTitle = savedInstanceState.getString(RETAINED_TITLE);
            mSummary = savedInstanceState.getString(RETAINED_SUMMARY);
            if (mCurrentPage == 1) {
                setUpSecondScreen();
            } else if (mCurrentPage == 2) {
                setUpThirdScreen();
            } else if (mCurrentPage == 3) {
                setUpPreviewScreen();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.craft_policy_fragment, container);
        prepAvailableRecycler();
        prepSelectedRecycler();
        prepNextButton();
        prepBackButton();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("Policies");

        mRootView.findViewById(R.id.craft_policy_dialog_close_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeWithWarning();
            }
        });

        return mRootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(RETAINED_SCREEN_POSITION, mCurrentPage);
        outState.putString(RETAINED_TITLE, mTitle);
        outState.putString(RETAINED_SUMMARY, mSummary);
        mSubjects = mSubjectSelectedAdapter.getSelectedSubjects();
        if (mSubjects != null && mSubjects.size() > 0) {
            outState.putStringArrayList(RETAINED_SUBJECTS, new ArrayList<>(mSubjects));
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

    private void prepAvailableRecycler() {
        mSubjectAvailableRecycler = mRootView.findViewById(R.id.craft_subject_available_recycler);
        mSubjectAvailableAdapter = new CreateSubjectAvailableAdapter(this, 0);
        mSubjectAvailableAdapter.setAll();
        mSubjectAvailableRecycler.setHasFixedSize(false);
        mSubjectAvailableRecycler.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false));
        mSubjectAvailableRecycler.setAdapter(mSubjectAvailableAdapter);
    }

    private void prepSelectedRecycler() {
        mSubjectSelectedRecycler = mRootView.findViewById(R.id.craft_subject_selected_recycler);
        mSubjectSelectedAdapter = new CreateSubjectAvailableAdapter(this, 1);
        mSubjectSelectedAdapter.setEmpty();
        mSubjectSelectedRecycler.setHasFixedSize(true);
        mSubjectSelectedRecycler.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL, false));
        mSubjectSelectedRecycler.setAdapter(mSubjectSelectedAdapter);
    }

    private void prepNextButton() {
        Button nextButton = mRootView.findViewById(R.id.craft_policy_next);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (mCurrentPage) {
                    case 0:
                        mSubjects = mSubjectSelectedAdapter.getSelectedSubjects();
                        if (mSubjects.size() == 0) {
                            Toast.makeText(getActivity(), getResources().getString(R.string.step_1_policy_subject_empty_error),
                                    Toast.LENGTH_LONG).show();
                        } else {
                            mCurrentPage++;
                            setUpSecondScreen();
                        }
                        break;
                    case 1:
                        mTitle = ((EditText) mRootView.findViewById(R.id.craft_title_edit_view))
                                .getText().toString();
                        if (mTitle.length() == 0) {
                            Toast.makeText(getActivity(), getResources().getString(R.string.step_2_policy_title_empty_error),
                                    Toast.LENGTH_LONG).show();
                        } else {
                            mCurrentPage++;
                            setUpThirdScreen();
                        }
                        break;
                    case 2:
                        mSummary = ((EditText) mRootView.findViewById(R.id.craft_summary_edit_view))
                                .getText().toString();
                        if (mSummary.length() == 0) {
                            Toast.makeText(getActivity(), getResources().getString(R.string.step_3_policy_summary_empty_error),
                                    Toast.LENGTH_LONG).show();
                        } else {
                            mCurrentPage++;
                            setUpPreviewScreen();
                        }
                        break;
                    case 3:
                        confirmPublish();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void prepBackButton() {
        Button backButton = mRootView.findViewById(R.id.craft_policy_small_back);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (mCurrentPage) {
                    case 0:
                        closeWithWarning();
                        break;
                    case 1:
                        mCurrentPage--;
                        resetFirstScreen();
                        break;
                    case 2:
                        mCurrentPage--;
                        setUpSecondScreen();
                        break;
                    case 3:
                        mCurrentPage--;
                        setUpThirdScreen();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void addSubject(String subject) {
        if (!mProceed) {
            mRootView.findViewById(R.id.craft_subject_placeholder_view).setVisibility(View.GONE);
            mRootView.findViewById(R.id.craft_subject_selected_recycler).setVisibility(View.VISIBLE);
            mRootView.findViewById(R.id.craft_subject_recycler_and_placeholder_frame)
                    .setBackgroundColor(getResources().getColor(android.R.color.transparent));
        }
        mProceed = true;

        mSubjectSelectedAdapter.PushSubject(subject);
        mSubjectAvailableAdapter.PullSubject(subject);

        setSubjectCounter(mSubjectSelectedAdapter.getItemCount());
    }

    @Override
    public void deleteSubject(String subject) {
        mSubjectSelectedAdapter.PullSubject(subject);
        mSubjectAvailableAdapter.PushSubject(subject);
        int count = mSubjectSelectedAdapter.getItemCount();
        if (count == 0) {
            mProceed = false;
            TextView numText = mRootView.findViewById(R.id.craft_subject_title_text_view);
            numText.setText(getResources().getString(R.string.craft_policy_subject_hint));
            mRootView.findViewById(R.id.craft_subject_placeholder_view).setVisibility(View.VISIBLE);
            mRootView.findViewById(R.id.craft_subject_selected_recycler).setVisibility(View.GONE);
            mRootView.findViewById(R.id.craft_subject_recycler_and_placeholder_frame)
                    .setBackgroundColor(getResources().getColor(R.color.light_gray));
        } else {
            setSubjectCounter(count);
        }
    }

    private void setSubjectCounter(int subjects) {
        TextView numText = mRootView.findViewById(R.id.craft_subject_title_text_view);
        numText.setText("");
        numText.append(getResources().getString(R.string.craft_policy_subject_hint));
        numText.append(String.format(getResources().getString(R.string.step_1_policy_subject_num_selected),
                String.valueOf(subjects)));
    }

    private void resetFirstScreen() {
        mRootView.findViewById(R.id.subject_entry_included_layout).setVisibility(View.VISIBLE);
        mRootView.findViewById(R.id.title_entry_included_layout).setVisibility(View.GONE);
        ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(mRootView.getWindowToken(), 0);
    }

    private void setUpSecondScreen() {
        mRootView.findViewById(R.id.summary_entry_included_layout).setVisibility(View.GONE);
        mRootView.findViewById(R.id.subject_entry_included_layout).setVisibility(View.GONE);
        mRootView.findViewById(R.id.title_entry_included_layout).setVisibility(View.VISIBLE);
        mRootView.findViewById(R.id.craft_title_edit_view).requestFocus();
        ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                .toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    private void setUpThirdScreen() {
        mRootView.findViewById(R.id.policy_preview_included_layout).setVisibility(View.GONE);
        mRootView.findViewById(R.id.title_entry_included_layout).setVisibility(View.GONE);
        mRootView.findViewById(R.id.subject_entry_included_layout).setVisibility(View.GONE);
        mRootView.findViewById(R.id.summary_entry_included_layout).setVisibility(View.VISIBLE);
        EditText editView = mRootView.findViewById(R.id.craft_summary_edit_view);
        editView.requestFocus();
        ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                .toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    private void setUpPreviewScreen() {
        mRootView.findViewById(R.id.summary_entry_included_layout).setVisibility(View.GONE);
        mRootView.findViewById(R.id.title_entry_included_layout).setVisibility(View.GONE);
        mRootView.findViewById(R.id.subject_entry_included_layout).setVisibility(View.GONE);
        mRootView.findViewById(R.id.policy_preview_included_layout).setVisibility(View.VISIBLE);
        ((TextView) mRootView.findViewById(R.id.swap_first_summary_line)).setMaxLines(20);
        ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(mRootView.getWindowToken(), 0);
        ((TextView) mRootView.findViewById(R.id.craft_policy_next))
                .setText(getResources().getString(R.string.craft_policy_save_and_publish));
        FrameLayout container = mRootView.findViewById(R.id.policy_preview_included_container);
        if (mParty.equals("Democrat")) {
            container.setBackground(getResources().getDrawable(R.drawable.dem_swap_background));
        } else {
            container.setBackground(getResources().getDrawable(R.drawable.rep_swap_background));
        }

        String subjects = mSubjects.toString();
        ((TextView) mRootView.findViewById(R.id.swap_first_subject_line))
                .setText(subjects.substring(1, subjects.length() - 1));
        ((TextView) mRootView.findViewById(R.id.swap_first_title_line)).setText(mTitle);
        ((TextView) mRootView.findViewById(R.id.swap_first_thumbs_up_count))
                .setText(String.format(getResources().getString(R.string.policy_net_wanted), 0, mParty));
        ((TextView) mRootView.findViewById(R.id.swap_first_creator_name)).setText(mUsername);

        TextView summary = mRootView.findViewById(R.id.swap_first_summary_line);
        summary.setText(mSummary);
        summary.setMaxLines(12);
        summary.setMovementMethod(new ScrollingMovementMethod());
    }

    private void closeWithWarning() {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle(getResources().getString(R.string.craft_policy_close_title));
        alertDialog.setMessage(getResources().getString(R.string.craft_policy_close_warning));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.craft_policy_close_confirm),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                                .hideSoftInputFromWindow(mRootView.getWindowToken(), 0);
                        dismiss();
                        ((MainActivity) getActivity()).getRecyclerView().getLayoutManager().onRestoreInstanceState(MainActivity.recyclerViewState);
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

    private void confirmPublish() {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle(getResources().getString(R.string.craft_policy_publish_title));
        alertDialog.setMessage(getResources().getString(R.string.craft_policy_publish_message));
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

    private void addToDatabase() {
        String pushId = mDatabaseReference.push().getKey();

        Policy policy = new Policy(mUsername, 0, mParty,
                mSubjects, mTitle, mSummary, String.valueOf(System.currentTimeMillis()), pushId);

        mDatabaseReference.child(pushId).setValue(policy);

        mDatabaseReference = mFirebaseDatabase.getReference().child("PolicyByParty").child(mParty);
        mDatabaseReference.push().setValue(pushId);

        mDatabaseReference = mFirebaseDatabase.getReference().child("UserPolicies").child(mUserId)
                .child("Created");
        mDatabaseReference.push().setValue(pushId);

        mDatabaseReference = mFirebaseDatabase.getReference("Subjects");
        for (String subject : mSubjects) {
            subject = subject.replace("/", "");
            mDatabaseReference.child(subject).child("byPolicy").child(pushId).setValue(pushId);
        }
        MainActivity.mUserCreated.add(pushId);

        mDatabaseReference = mFirebaseDatabase.getReference("UserInfo/" + MainActivity.USER_ID);
        MainActivity.USER_POLICY_POINTS += 5;
        MainActivity.USER_OVERALL_POINTS += 5;
        mDatabaseReference.child("overallPoints").setValue(MainActivity.USER_OVERALL_POINTS);
        mDatabaseReference.child("policyCreatedPoints").setValue(MainActivity.USER_POLICY_POINTS);

        Bundle logParams = new Bundle();
        logParams.putString("User", MainActivity.USERNAME);
        FirebaseAnalytics.getInstance(getActivity()).logEvent("policyCreated", logParams);

        if (MainActivity.mAdapterNeeded == 3) {
            new FirebaseRetrievalCalls((MainActivity) getActivity(), false).getTopPolicies();
        } else {
            new FirebaseRetrievalCalls((MainActivity) getActivity(), false).getNewPolicies();
        }
    }
}
