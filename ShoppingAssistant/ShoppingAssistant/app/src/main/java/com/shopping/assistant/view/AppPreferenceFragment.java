package com.shopping.assistant.view;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.shopping.assistant.R;

public class AppPreferenceFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.fragment_preference);
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }
}
