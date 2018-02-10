package com.unixonly.fooslive.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unixonly.fooslive.R;
import com.unixonly.fooslive.fragment_utils.FragmentCallback;
import com.unixonly.fooslive.fragment_utils.OnFragmentInteractionListener;

public class InfoFragment extends Fragment {
    public static final String TAG = "InfoFragment";

    protected OnFragmentInteractionListener mListener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Update top bar title
        Bundle args = new Bundle();
        args.putString(getString(R.string.argument_title), getString(R.string.title_info));
        mListener.onFragmentCallback(FragmentCallback.ACTION_SET_TITLE, args);

        return inflater.inflate(R.layout.fragment_info, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
