package com.example.jon.politiswap.UiAdapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.jon.politiswap.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CreateSubjectAvailableAdapter extends RecyclerView.Adapter<CreateSubjectAvailableAdapter.SubjectAvailableViewHolder> {

    private Context mContext;
    private int mType;
    private SubjectChangeManager mChangeManager;

    private List<String> mAllSubjects =
            Arrays.asList("agriculture/food", "animals", "arts/culture/religion", "banking/finance",
                    "civil rights", "commerce", "corporate governance", "education",
                    "emergency management", "energy", "environment", "governmental operations",
                    "health", "housing/community", "immigration", "international affairs",
                    "labor", "law enforcement", "national security", "public finance/budgeting",
                    "resource management", "science/technology", "social welfare", "taxation",
                    "trade", "transportation");

    private List<String> mEmptySubjects = new ArrayList<>();
    private List<String> mMasterSubjects = new ArrayList<>();

    public CreateSubjectAvailableAdapter(SubjectChangeManager manager, int type){
        mType = type;
        mChangeManager = manager;
    }

    public interface SubjectChangeManager{
        void addSubject(String subject);
        void deleteSubject(String subject);
    }

    @NonNull
    @Override
    public SubjectAvailableViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();

        View layoutView = LayoutInflater.from(mContext).inflate(R.layout.create_subject_recycler_card_view, viewGroup,false);
        return new SubjectAvailableViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectAvailableViewHolder holder, int i) {
        final String subject = mMasterSubjects.get(i);
        holder.mSubjectView.setText(subject);
        final String dbSubject = subject.replace("/","");

        if (mType%2 == 0){
            holder.mRemoveButton.setVisibility(View.GONE);
            holder.mLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String toUse = mType==2 ? dbSubject : subject;
                    mChangeManager.addSubject(toUse);
                }
            });
        } else{
            ViewGroup.LayoutParams params = holder.mLayout.getLayoutParams();
            ViewGroup.LayoutParams params2 = holder.mCard.getLayoutParams();
            params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            params2.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            holder.mLayout.requestLayout();
            holder.mCard.requestLayout();
            holder.mRemoveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mChangeManager.deleteSubject(subject);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (null == mMasterSubjects) return 0;
        return mMasterSubjects.size();
    }

    public void PushSubject(String subject) {
        mMasterSubjects.add(subject);
        Collections.sort(mMasterSubjects);
        notifyDataSetChanged();
    }

    public void PullSubject(String subject){
        mMasterSubjects.remove(subject);
        notifyDataSetChanged();
    }

    public void setEmpty(){
        mMasterSubjects = mEmptySubjects;
        notifyDataSetChanged();
    }

    public void setAll(){
        mMasterSubjects.addAll(mAllSubjects);
        notifyDataSetChanged();
    }

    public List<String> getSelectedSubjects(){
        return mMasterSubjects;
    }

    public class SubjectAvailableViewHolder extends RecyclerView.ViewHolder{

        final TextView mSubjectView;
        final ImageButton mRemoveButton;
        final LinearLayout mLayout;
        final CardView mCard;

        public SubjectAvailableViewHolder(@NonNull View view) {
            super(view);
            mSubjectView = view.findViewById(R.id.create_subject_recycler_card_text_view);
            mRemoveButton = view.findViewById(R.id.create_subject_recycler_card_cancel_button);
            mLayout = view.findViewById(R.id.create_subject_recycler_card_layout);
            mCard = view.findViewById(R.id.create_subject_card_master);
        }
    }
}