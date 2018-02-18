package com.unixonly.fooslive.fragments;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unixonly.fooslive.R;
import com.unixonly.fooslive.databinding.ItemFragmentHistoryBinding;

public class HistoryListAdapter extends RecyclerView.Adapter<HistoryListAdapter.HistoryListViewHolder> {
    //TODO create data source with content provider
    @Override
    public HistoryListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_fragment_history, parent, false);
        return new HistoryListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HistoryListViewHolder holder, int position) {

    }


    public HistoryListAdapter() {
        // TODO setup content provider
    }



    @Override
    public int getItemCount() {
        return 0;
    }


    class HistoryListViewHolder extends RecyclerView.ViewHolder {
        protected ItemFragmentHistoryBinding mBinding;
        public HistoryListViewHolder(View itemView) {
            super(itemView);
            mBinding = ItemFragmentHistoryBinding.bind(itemView);
        }
    }


}
