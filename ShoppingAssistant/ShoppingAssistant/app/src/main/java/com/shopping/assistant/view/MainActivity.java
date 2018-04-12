package com.shopping.assistant.view;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.shopping.assistant.R;
import com.shopping.assistant.constants.Constants;

import java.util.HashMap;


public class MainActivity extends AppCompatActivity {
    private ActionBarDrawerToggle mDrawerToggle;

    // Widget Members
    private DriverFragment driverFragment = null;
    SharedPreferences preferences;
    private Fragment activeFragment;
    private AppPreferenceFragment appPreferenceFragment = null;
    private MenuItem addList;

    private static final HashMap<Integer,Integer> StringIDs = new HashMap<>();
    static{
        StringIDs.put(Constants.FRAGMENT_MARKETS, R.string.markets );
        StringIDs.put(Constants.FRAGMENT_DEFINIG_PRODUCT, R.string.defining_product );
        StringIDs.put(Constants.FRAGMENT_SETTINGS_ORDER, R.string.nav_settings );

    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, 0);
            }
        };
        drawerLayout.setDrawerListener(mDrawerToggle);
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header);
        TextView user = (TextView) headerLayout.findViewById(R.id.user);

        // User Name for Drawer
        user.setText( preferences.getString(Constants.USERNAME, "NoUserName") );

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();

                if( menuItem.getItemId() == R.id.nav_home ) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    if (fragmentManager != null) {
                        int stackCount = fragmentManager.getBackStackEntryCount();
                        for (int i = 0; i < stackCount; ++i)
                            fragmentManager.popBackStack();
                    }

                    ActionBar actionBar = getSupportActionBar();
                    if (actionBar != null)
                        actionBar.setTitle(R.string.app_name);

                    actionBar.show();
                    addList.setVisible(true);
                    restoreHamburger();
                    final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                } else if( menuItem.getItemId() == R.id.log_out ){
                    preferences.edit().clear().apply(); // clear all user preferences
                    preferences.edit().putBoolean("logOut", true).apply();
                    Intent intent = new Intent(MainActivity.this, AuthenticatorActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else if( menuItem.getItemId() == R.id.nav_settings ) {
                    appPreferenceFragment = new AppPreferenceFragment();
                    showFragment(appPreferenceFragment, R.string.nav_settings);
                    addList.setVisible(false);
                } else {
                    // TODO : My Own Fragments for Shopping Assistant
                    // For Ex : Markets , Shopping Lists and so on ...
                    FragmentFactory FManager = new FragmentFactory(getApplicationContext());
                    activeFragment = FManager.createFragment(menuItem.getOrder());
                    showFragment( activeFragment, StringIDs.get(menuItem.getOrder()) );
                    addList.setVisible(false);
                }
                return true;
            }
        });
        final FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (driverFragment == null) {
            driverFragment = DriverFragment.newInstance();
        }
        fragmentTransaction.add(R.id.fragment_container, driverFragment);
        fragmentTransaction.commit();

        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if (!manager.isProviderEnabled( LocationManager.GPS_PROVIDER )) {
            buildAlertMessageNoGps();
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.gps_activation_alert_msg)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            activeFragment = null;
            navigationView.getMenu().performIdentifierAction(R.id.nav_home, 0);
        } else {
            super.onBackPressed();
        }
    }


    private void showFragment(final Fragment fragment, int title) {
        if (fragment.isVisible())
            return;

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setTitle(title);

        mDrawerToggle.setDrawerIndicatorEnabled(false);
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                navigationView.getMenu().performIdentifierAction(R.id.nav_home, 0);
            }
        });


        if (fragment instanceof AppPreferenceFragment) {
            final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    private void restoreHamburger() {
        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(false);
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.list_actions, menu);
        addList = menu.findItem(R.id.add_list);
        addList.setVisible(true);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_list:
                addNewListActivity();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // TODO : add new list to user's list
    private void addNewListActivity(){
        Toast.makeText( getApplicationContext(), "HEYYY!! implement meee ;)", Toast.LENGTH_LONG).show();
    }

}
