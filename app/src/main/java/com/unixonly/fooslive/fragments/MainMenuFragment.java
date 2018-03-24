package com.unixonly.fooslive.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unixonly.fooslive.R;
import com.unixonly.fooslive.fragments.interaction.OnFragmentInteractionListener;

public class MainMenuFragment extends Fragment {
    public static final String TAG = "MainMenuFragment";

    protected OnFragmentInteractionListener mListener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main_menu, container, false);
        loadChildFragment(MainMenuButtonsFragment.TAG);
        return view;
    }

    /**
     * Loads buttons fragments
     * @param fragmentTag fragment identifier
     */
    public void loadChildFragment(String fragmentTag) {
        Fragment childFragment;
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        switch(fragmentTag)
        {
            case MainMenuButtonsFragment.TAG:
                childFragment = new MainMenuButtonsFragment();
                break;
            case ModeMenuButtonsFragment.TAG:
                childFragment = new ModeMenuButtonsFragment();
                transaction.addToBackStack(null);
                break;
            default:
                Log.e(TAG, "loadChildFragment fragmentTag unidentified: " + fragmentTag);
                return;
        }

        transaction.replace(R.id.items_main_menu, childFragment).commit();
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
