package com.unixonly.fooslive.fragments;

import android.app.Fragment;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unixonly.fooslive.R;
import com.unixonly.fooslive.databinding.FragmentItemsMainMenuBinding;
import com.unixonly.fooslive.fragment_interaction.FragmentCallback;

import static com.unixonly.fooslive.fragment_interaction.FragmentCallback.ACTION_NAVIGATE_TO;

public class MainMenuButtonsFragment extends Fragment {
    public static final String TAG = "MainMenuButtonsFragment";
    FragmentItemsMainMenuBinding mBinding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Update top bar title
        Bundle args = new Bundle();
        args.putInt(getString(R.string.argument_title), R.string.title_menu);
        ((MainMenuFragment)getParentFragment())
                .mListener.onFragmentCallback(FragmentCallback.ACTION_SET_TITLE, args);

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_items_main_menu, container,
                false);

        mBinding.buttonGameModes.setOnClickListener((View v) ->
            ((MainMenuFragment)getParentFragment()).loadChildFragment(ModeMenuButtonsFragment.TAG));

        mBinding.buttonHistory.setOnClickListener((View v) -> navigateTo(HistoryFragment.TAG));

        mBinding.buttonSettings.setOnClickListener((View v) -> navigateTo(SettingsFragment.TAG));

        mBinding.buttonInfo.setOnClickListener((View v) -> navigateTo(InfoFragment.TAG));
        return mBinding.getRoot();
    }

    /**
     * Retrieves navigation listener from parent fragment and sends callback to Menu activity to
     * change fragments
     * @param fragmentTag new fragment tag
     */
    private void navigateTo(String fragmentTag) {
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.argument_fragment_tag), fragmentTag);
        ((MainMenuFragment)getParentFragment())
                .mListener.onFragmentCallback(ACTION_NAVIGATE_TO, bundle);
    }
}
