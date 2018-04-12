package com.shopping.assistant.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import com.shopping.assistant.constants.Constants;

public class FragmentFactory {

    private SharedPreferences preferences;

    public FragmentFactory(){}

    public FragmentFactory(Context context){
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    // TODO : InventoryFragment ler yerine kendi Fragmentlerim !!!
    public Fragment createFragment(int fragmentOrder ){
        switch (fragmentOrder) {
            case Constants.FRAGMENT_MARKETS:
                return InventoryFragment.newInstance(3, Color.parseColor("#FFE0B2"));
            case Constants.FRAGMENT_DEFINIG_PRODUCT:
                return InventoryFragment.newInstance(6, Color.WHITE);
            case Constants.FRAGMENT_SETTINGS_ORDER:
                return new AppPreferenceFragment();

        }
        return null;
    }

}
