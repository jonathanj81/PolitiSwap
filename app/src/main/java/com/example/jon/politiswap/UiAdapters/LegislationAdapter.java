package com.example.jon.politiswap.UiAdapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.jon.politiswap.DataUtils.Recent.Bill;
import com.example.jon.politiswap.DataUtils.Recent.RecentBills;
import com.example.jon.politiswap.DataUtils.Searched.SearchedBills;
import com.example.jon.politiswap.DialogFragments.BillDialogFragment;
import com.example.jon.politiswap.DialogFragments.FragmentArgs;
import com.example.jon.politiswap.MainActivity;
import com.example.jon.politiswap.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LegislationAdapter extends RecyclerView.Adapter<LegislationAdapter.LegislationViewHolder> {

    private List<Bill> mBills = new ArrayList<>();
    private List<com.example.jon.politiswap.DataUtils.Searched.Bill> mSearchedBills = new ArrayList<>();
    private Context mContext;
    private int mType;

    private OnBottomReachedListener onBottomReachedListener;

    private static final String BILL_FRAGMENT_NAME = "bill_frag";

    public LegislationAdapter(int type, OnBottomReachedListener onBottomReachedListener){
        mType = type;
        this.onBottomReachedListener = onBottomReachedListener;
    }

    public LegislationAdapter(OnBottomReachedListener onBottomReachedListener){
        this.onBottomReachedListener = onBottomReachedListener;
    }

    @NonNull
    @Override
    public LegislationViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();

        View layoutView = LayoutInflater.from(mContext).inflate(R.layout.legislation_card_view, viewGroup,false);
        return new LegislationViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull LegislationViewHolder holder, int i) {
        final String slug;
        final String date;
        final String subject;
        final String title;
        final String partyAbbreviated;
        final String party;
        final String sponsor;
        final String summary;
        final String link;
        if (mType == 0) {
            Bill bill = mBills.get(i);
            slug = bill.getNumber();
            date = bill.getIntroducedDate();
            subject = bill.getPrimarySubject();
            title = bill.getTitle();
            partyAbbreviated = bill.getSponsorParty();
            party = partyAbbreviated.equals("D") ? "Democrat" : "Republican";
            sponsor = bill.getSponsorName();
            summary = bill.getSummary();
            link = bill.getGovtrackUrl();

            if (i == mBills.size()-1){
                onBottomReachedListener.onBottomReached();
            }
        } else {
            com.example.jon.politiswap.DataUtils.Searched.Bill bill = mSearchedBills.get(i);
            slug = bill.getNumber();
            date = bill.getIntroducedDate();
            subject = bill.getPrimarySubject();
            title = bill.getTitle();
            partyAbbreviated = bill.getSponsorParty();
            party = partyAbbreviated.equals("D") ? "Democrat" : "Republican";
            sponsor = bill.getSponsorName();
            summary = bill.getSummary();
            link = bill.getGovtrackUrl();

            if (i == mSearchedBills.size()-1){
                onBottomReachedListener.onBottomReached();
            }
        }

        holder.titleText.setText(title);
        holder.dateText.setText(date);
        holder.subjectText.setText(subject);
        holder.slugText.setText(slug);

        holder.partyText.setText(String.format(mContext.getResources().getString(R.string.sponsored_by), party));
        if (partyAbbreviated.equals("R")){
            holder.backgroundLayout.setBackgroundColor(mContext.getResources().getColor(R.color.colorAccentTransparency));
        } else {
            holder.backgroundLayout.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
        }

        holder.backgroundLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentArgs.BILL_DIALOG_SLUG_NUMBER = slug;
                FragmentArgs.BILL_DIALOG_DATE = date;
                FragmentArgs.BILL_DIALOG_SUBJECTS = subject;
                FragmentArgs.BILL_DIALOG_TITLE = title;
                FragmentArgs.BILL_DIALOG_SPONSOR_PARTY = party;
                FragmentArgs.BILL_DIALOG_SPONSOR_NAME = sponsor;
                FragmentArgs.BILL_DIALOG_SUMMARY = summary;
                FragmentArgs.BILL_DIALOG_LINK = link;
                FragmentManager fm = ((MainActivity)v.getContext()).getSupportFragmentManager();
                BillDialogFragment frag = BillDialogFragment.newInstance(null);
                frag.show(fm, BILL_FRAGMENT_NAME);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mType == 0){
            return mBills == null ? 0 : mBills.size();
        } else {
            return mSearchedBills == null ? 0 : mSearchedBills.size();
        }
    }

    public void setBills(RecentBills recentBills, SearchedBills searchedBills) {
        if (mType == 0) {
            mSearchedBills.clear();
            mBills.addAll(recentBills.getResults().get(0).getBills());
        } else {
            mBills.clear();
            mSearchedBills.addAll(searchedBills.getResults().get(0).getBills());
        }
        Collections.sort(mSearchedBills,new BillComparator());
        notifyDataSetChanged();
    }

    public class LegislationViewHolder extends RecyclerView.ViewHolder{

        final TextView titleText;
        final TextView dateText;
        final TextView partyText;
        final TextView slugText;
        final TextView subjectText;
        final ConstraintLayout backgroundLayout;

        public LegislationViewHolder(@NonNull View view) {
            super(view);
            titleText = view.findViewById(R.id.recent_bill_title_text_view);
            dateText = view.findViewById(R.id.recent_bill_date_text_view);
            partyText = view.findViewById(R.id.recent_bill_party_text_view);
            slugText = view.findViewById(R.id.recent_bill_slug_number);
            subjectText = view.findViewById(R.id.recent_bill_subjects);
            backgroundLayout = view.findViewById(R.id.recent_legislation_layout);
        }
    }

    public int getType(){
        return mType;
    }

    private class BillComparator implements Comparator<com.example.jon.politiswap.DataUtils.Searched.Bill>{

        @Override
        public int compare(com.example.jon.politiswap.DataUtils.Searched.Bill billA, com.example.jon.politiswap.DataUtils.Searched.Bill billB) {
            Integer billAIntroduced = Integer.parseInt(billA.getIntroducedDate().replaceAll("[^0-9]",""));
            Integer billBIntroduced = Integer.parseInt(billB.getIntroducedDate().replaceAll("[^0-9]",""));
            return billBIntroduced.compareTo(billAIntroduced);
        }
    }
}
