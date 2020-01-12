package com.tiarnan.firststep;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.tiarnan.firststep.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_first_step);
    }
}
