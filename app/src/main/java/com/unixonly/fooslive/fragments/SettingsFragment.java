package com.unixonly.fooslive.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.unixonly.fooslive.R;
import com.unixonly.fooslive.fragment_utils.FragmentCallback;
import com.unixonly.fooslive.fragment_utils.OnFragmentInteractionListener;

public class SettingsFragment extends PreferenceFragment implements
        SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceChangeListener {
    public static final String TAG = "SettingsFragment";

    private OnFragmentInteractionListener mListener;

    /**
     * <p><b> Fooslive: Setup layout, set listeners and custom summaries</b></p>
     * {@inheritDoc}
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Update top bar title
        Bundle args = new Bundle();
        args.putString(TAG, getString(R.string.title_settings));
        mListener.onFragmentCallback(FragmentCallback.ACTION_SET_TITLE, args);

        addPreferencesFromResource(R.xml.preferences);
        PreferenceScreen prefScreen = getPreferenceScreen();
        SharedPreferences sharedPref = prefScreen.getSharedPreferences();

        // Set listener to capture changed preferences in order to update summaries
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        // Go through all of the preferences, and set up their current summaries
        for (int i = 0; i < prefScreen.getPreferenceCount(); i++) {
            PreferenceCategory preferenceCategory =
                    (PreferenceCategory) prefScreen.getPreference(i);
            for (int j = 0; j < preferenceCategory.getPreferenceCount(); j++) {
                Preference preference = preferenceCategory.getPreference(j);
                // SwitchPreferences don't use summary, so ignore it
                if (preference instanceof SwitchPreference) continue;
                // Set summaries based on saved values
                //Todo: change defValue. 0 means trouble
                preference.setSummary(sharedPref.getString(preference.getKey(), "0"));
                if (!(preference instanceof EditTextPreference)) continue;
                // Add description to the summary
                String sidesInfo =
                        preference.getKey().equals(getString(R.string.pref_key_name_team1)) ?
                        getString(R.string.pref_description_name_team1) :
                        getString(R.string.pref_description_name_team2);
                preference.setSummary(preference.getSummary() + sidesInfo);
                // Set listener to capture & validate changes before saving
                preference.setOnPreferenceChangeListener(this);

            }
        }
    }

    /**
     * <p><b> Fooslive: Update summary for specific preference</b></p>
     * {@inheritDoc}
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        //
        // Figure out which preference was changed
        Preference preference = findPreference(key);
        // If preference not found or is SwitchPreference, halt
        if (preference == null || preference instanceof SwitchPreference) return;
        //Todo: change defValue. 0 means trouble
        String value = sharedPreferences.getString(key, "0");
        if (!(preference instanceof EditTextPreference)) return;
        String sidesInfo = key.equals(getString(R.string.pref_key_name_team1)) ?
                getString(R.string.pref_description_name_team1) :
                getString(R.string.pref_description_name_team2);
        preference.setSummary(value + sidesInfo);
    }

    /**
     * <p><b> Fooslive: Check if team name input is valid to save</b></p>
     * {@inheritDoc}
     */
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (!(preference instanceof EditTextPreference)) return true;
        String stringValue = (String) newValue;
        if (stringValue.length() > getActivity().getResources().getInteger(R.integer.max_name_length)
            || stringValue.isEmpty()) {
            Toast.makeText(getActivity(), R.string.error_bad_format, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
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
