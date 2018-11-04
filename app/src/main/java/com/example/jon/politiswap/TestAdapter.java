package com.example.jon.politiswap;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.TestViewHolder> {

    private List<String> placeholderList = Arrays.asList(".",".",".",".",".",".",".",".",".",".",".");
    private Context mContext;

    public TestAdapter(){
    }

    @NonNull
    @Override
    public TestViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();

        View layoutView = LayoutInflater.from(mContext).inflate(R.layout.placeholder_card_view, viewGroup,false);
        return new TestViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull TestViewHolder testViewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        if (null == placeholderList) return 0;
        return placeholderList.size();
    }

    public void setMarkers(List<String> placeholders) {
        placeholderList = placeholders;
        notifyDataSetChanged();
    }

    public class TestViewHolder extends RecyclerView.ViewHolder{

        final TextView titleText;

        public TestViewHolder(@NonNull View view) {
            super(view);
            titleText = view.findViewById(R.id.placeholder_text_view);
        }
    }
}
