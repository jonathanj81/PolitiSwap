package com.example.jon.politiswap.DialogFragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jon.politiswap.R;

public class BillDialogFragment extends DialogFragment {

    public BillDialogFragment() {

    }

    public static BillDialogFragment newInstance(Bundle args) {
        BillDialogFragment frag = new BillDialogFragment();
        frag.setArguments(args);
        return frag;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bill_dialog_fragment, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((TextView)view.findViewById(R.id.bill_dialog_slug_number)).setText(FragmentArgs.BILL_DIALOG_SLUG_NUMBER);
        ((TextView)view.findViewById(R.id.bill_dialog_title_text_view)).setText(FragmentArgs.BILL_DIALOG_TITLE);
        ((TextView)view.findViewById(R.id.bill_dialog_summary_text_view)).setText(FragmentArgs.BILL_DIALOG_SUMMARY);
        ((TextView)view.findViewById(R.id.bill_dialog_subjects_text_view))
                .setText(FragmentArgs.BILL_DIALOG_SUBJECTS.equals("") ? getActivity().getResources()
                .getString(R.string.bill_no_subject) : FragmentArgs.BILL_DIALOG_SUBJECTS);
        ((TextView)view.findViewById(R.id.bill_dialog_link_text_view)).setText(FragmentArgs.BILL_DIALOG_LINK);
        ((TextView)view.findViewById(R.id.bill_dialog_date_text_view)).setText(FragmentArgs.BILL_DIALOG_DATE);
        ((TextView)view.findViewById(R.id.bill_dialog_sponsor_text_view))
                .setText(String.format(getActivity().getResources().getString(R.string.sponsored_by),
                        FragmentArgs.BILL_DIALOG_SPONSOR_NAME + " (" + FragmentArgs.BILL_DIALOG_SPONSOR_PARTY + ")"));

        if (FragmentArgs.BILL_DIALOG_SPONSOR_PARTY.equals("Republican")){
            view.findViewById(R.id.bill_fragment_background_layout)
                    .setBackgroundColor(getActivity().getResources().getColor(R.color.colorAccentTransparency));
        }

        view.findViewById(R.id.bill_dialog_close_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        view.findViewById(R.id.bill_dialog_link_text_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(FragmentArgs.BILL_DIALOG_LINK));
                startActivity(i);
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
