package com.example.jon.politiswap.UiAdapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jon.politiswap.DataUtils.Recent.Bill;
import com.example.jon.politiswap.DataUtils.Recent.RecentBills;
import com.example.jon.politiswap.DialogFragments.BillDialogFragment;
import com.example.jon.politiswap.DialogFragments.FragmentArgs;
import com.example.jon.politiswap.MainActivity;
import com.example.jon.politiswap.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LegislationAdapter extends RecyclerView.Adapter<LegislationAdapter.LegislationViewHolder> {

    private List<Bill> mBills = new ArrayList<>();
    private Context mContext;

    private static final String BILL_FRAGMENT_NAME = "bill_frag";

    public LegislationAdapter(){
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
        Bill bill = mBills.get(i);
        final String slug = bill.getNumber();
        final String date = bill.getIntroducedDate();
        final String subject = bill.getPrimarySubject();
        final String title = bill.getTitle();
        String partyAbbreviated = bill.getSponsorParty();
        final String party = partyAbbreviated.equals("D") ? "Democrat" : "Republican";
        final String sponsor = bill.getSponsorName();
        final String summary = bill.getSummary();
        final String link = bill.getGovtrackUrl();

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
        if (null == mBills) return 0;
        return mBills.size();
    }

    public void setBills(RecentBills recentBills) {
        mBills.addAll(recentBills.getResults().get(0).getBills());
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
}
