package com.example.jon.politiswap.UiAdapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.jon.politiswap.DataUtils.Recent.RecentBills;
import com.example.jon.politiswap.R;
import java.util.Arrays;
import java.util.List;

public class SwapsAdapter extends RecyclerView.Adapter<SwapsAdapter.SwapsViewHolder> {

    private List<String> mPlaceholder = Arrays.asList(".", ".", ".", ".", ".", ".", ".");
    private Context mContext;

    public SwapsAdapter(){
    }

    @NonNull
    @Override
    public SwapsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();

        View layoutView = LayoutInflater.from(mContext).inflate(R.layout.swap_layout_card_view, viewGroup,false);
        return new SwapsViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull SwapsViewHolder holder, int i) {
    }

    @Override
    public int getItemCount() {
        if (null == mPlaceholder) return 0;
        return mPlaceholder.size();
    }

    public void setBills(RecentBills recentBills) {
    }

    public class SwapsViewHolder extends RecyclerView.ViewHolder{


        public SwapsViewHolder(@NonNull View view) {
            super(view);
        }
    }
}
