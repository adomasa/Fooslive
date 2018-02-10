package com.unixonly.fooslive.fragments;


import android.app.Fragment;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unixonly.fooslive.R;
import com.unixonly.fooslive.databinding.FragmentHistoryBinding;
import com.unixonly.fooslive.fragment_interaction.FragmentCallback;
import com.unixonly.fooslive.fragment_interaction.OnFragmentInteractionListener;

public class HistoryFragment extends Fragment {
    public static final String TAG = "HistoryFragment";
    //TODO port fragment
    private OnFragmentInteractionListener mListener;
    private FragmentHistoryBinding mBinding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Update top bar title
        Bundle args = new Bundle();
        args.putInt(getString(R.string.argument_title), R.string.title_history);
        mListener.onFragmentCallback(FragmentCallback.ACTION_SET_TITLE, args);

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_items_mode_menu, container,
                false);
        return mBinding.getRoot();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (OnFragmentInteractionListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
