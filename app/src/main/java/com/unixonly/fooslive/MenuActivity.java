package com.unixonly.fooslive;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.unixonly.fooslive.databinding.ActivityMenuBinding;
import com.unixonly.fooslive.fragments.HistoryFragment;
import com.unixonly.fooslive.fragments.InfoFragment;
import com.unixonly.fooslive.fragments.MainMenuFragment;
import com.unixonly.fooslive.fragments.SettingsFragment;
import com.unixonly.fooslive.fragment_utils.FragmentCallback;
import com.unixonly.fooslive.fragment_utils.OnFragmentInteractionListener;

import static com.unixonly.fooslive.fragment_utils.FragmentCallback.ACTION_NAVIGATE_TO;
import static com.unixonly.fooslive.fragment_utils.FragmentCallback.ACTION_SET_TITLE;


public class MenuActivity extends AppCompatActivity implements OnFragmentInteractionListener {
    private static final String TAG = "MenuActivity";

    private ActivityMenuBinding mBinding;
    private Fragment mFragment;
    private Fragment mPreviousFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_menu);

        setActionBar(mBinding.toolbar);
        getActionBar().setDisplayShowTitleEnabled(false);

        loadFragment(MainMenuFragment.TAG, false);
    }

    @Override
    public void onFragmentCallback( @FragmentCallback.Action int action, Bundle args) {
        if (args == null) return;

        switch (action) {
            case ACTION_SET_TITLE:
                updateTitle(args.getString(getString(R.string.argument_title)));
                break;
            case ACTION_NAVIGATE_TO:
                String fragmentTag = args.getString(getString(R.string.argument_fragment_tag));
                boolean saveState = args.getBoolean(
                        getString(R.string.argument_fragment_save_state), true);
                loadFragment(fragmentTag, saveState);
                break;
            default:
                Log.e(TAG, "onFragmentCallback action unidentified: " + action);
                break;
        }
    }

    /**
     * Update custom top bar title
     * @param title - new value
     */
    private void updateTitle(String title) {
        mBinding.toolbarTitle.setText(title);
    }

    /**
     * Replace content view with specific fragment
     * @param fragmentTag fragment identifier
     * @param saveState If set to true save state for navigation
     */
    public void loadFragment(String fragmentTag, Boolean saveState)
    {
        mPreviousFragment = mFragment;
        mFragment = null;
        switch (fragmentTag)
        {
            case MainMenuFragment.TAG:
                mFragment = new MainMenuFragment();
                break;
            case SettingsFragment.TAG:
                mFragment = new SettingsFragment();
                break;
            case HistoryFragment.TAG:
                mFragment = new HistoryFragment();
                break;
            case InfoFragment.TAG:
                mFragment = new InfoFragment();
                break;
            default:
                Log.e(TAG, "LoadFragment TAG unidentified: " + fragmentTag);
                return;
        }

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        if (saveState) transaction.addToBackStack(null);
        transaction.replace(R.id.menu_content, mFragment).commit();
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            // Pop default menu fragment
            mFragment = mPreviousFragment;
            getFragmentManager().popBackStack();
            return;
        }

        if (mFragment instanceof MainMenuFragment &&
                mFragment.getChildFragmentManager().getBackStackEntryCount() > 0) {
            // Pop inner MainMenu fragment
            mFragment.getChildFragmentManager().popBackStack();
            return;
        }

        finish();
    }
}
