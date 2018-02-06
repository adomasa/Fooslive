package com.unixonly.fooslive;

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

/**
 * Created by ramu on 06/02/2018.
 */

public class SettingsFragment extends PreferenceFragment implements
        SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceChangeListener {

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                // Set listener to capture & validate changes before saving
                if (preference instanceof EditTextPreference) {
                    // Add description to the summary
                    String sidesInfo =
                            preference.getKey().equals(getString(R.string.pref_key_name_team1)) ?
                            getString(R.string.pref_description_name_team1) :
                            getString(R.string.pref_description_name_team2);
                    preference.setSummary(preference.getSummary() + sidesInfo);
                    preference.setOnPreferenceChangeListener(this);

                }
            }
        }
    }

    /**
     * <p><b> Fooslive: Updates summary for specific preference</b></p>
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
        if (preference instanceof EditTextPreference) {
            String sidesInfo = key.equals(getString(R.string.pref_key_name_team1)) ?
                    getString(R.string.pref_description_name_team1) :
                    getString(R.string.pref_description_name_team2);
            preference.setSummary(value + sidesInfo);
        }
    }

    /**
     * <p><b> Fooslive: Checks if EditTextPreference input is valid to save</b></p>
     * {@inheritDoc}
     */
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (!(preference instanceof EditTextPreference)) return true;
        String stringValue = (String) newValue;
        if (stringValue.length() > getContext().getResources().getInteger(R.integer.max_name_length)
                || stringValue.isEmpty()) {
            Toast.makeText(getContext(), R.string.error_bad_format, Toast.LENGTH_LONG).show();
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
}
