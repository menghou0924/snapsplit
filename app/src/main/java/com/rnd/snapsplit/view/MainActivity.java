package com.rnd.snapsplit.view;

/**
 * Created by menghou0924 on 27/5/2017.
 */

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;
import android.content.res.Configuration;
import android.view.View;

import com.rnd.snapsplit.R;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Toolbar toolBarHamburger;

    private DrawerLayout mDrawer;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolBarHamburger = (Toolbar) findViewById(R.id.tool_bar_hamburger);
        setSupportActionBar(toolBarHamburger);

        mDrawer = (DrawerLayout) findViewById(R.id.root_layout);
        drawerToggle = setupDrawerToggle();
        mDrawer.addDrawerListener(drawerToggle);

        nvDrawer = (NavigationView) findViewById(R.id.nvView);

        setupDrawerContent(nvDrawer);

        // add first fragment
        if (savedInstanceState == null) {
            ((Toolbar) findViewById(R.id.tool_bar_hamburger)).setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_holder, OcrCaptureFragment.newInstance(), "OcrCaptureFragment")
                    .commit();
        }

    }

    // Navigation Drawers

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass;
        ((Toolbar) findViewById(R.id.tool_bar_hamburger)).setVisibility(View.VISIBLE);
        switch(menuItem.getItemId()) {
            case R.id.nav_camera:
                fragmentClass = OcrCaptureFragment.class;
                ((Toolbar) findViewById(R.id.tool_bar_hamburger)).setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
                setTitle("");
                break;
            case R.id.nav_payment_requests:
                fragmentClass = OwedFragment.class;
                ((Toolbar) findViewById(R.id.tool_bar_hamburger)).setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
                setTitle(menuItem.getTitle());
                break;
            case R.id.nav_my_account:
                fragmentClass = NewAddFriendsFragment.class;
                ((Toolbar) findViewById(R.id.tool_bar_hamburger)).setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
                setTitle(menuItem.getTitle());
                break;
            case R.id.nav_friends:
                fragmentClass = NewAddFriendsFragment.class;
                ((Toolbar) findViewById(R.id.tool_bar_hamburger)).setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
                setTitle(menuItem.getTitle());
                break;
            case R.id.nav_sign_out:
                fragmentClass = SignoutFragment.class;
                ((Toolbar) findViewById(R.id.tool_bar_hamburger)).setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
                setTitle("");
                break;
//            case R.id.nav_settings:
//                fragmentClass = MainActivity.class;
//                break;
//            case R.id.nav_about:
//                fragmentClass = MainActivity.class;
//                break;
            default:
                fragmentClass = OcrCaptureFragment.class;
        }
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        for(int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
            fragmentManager.popBackStack();
        }
        fragmentManager.beginTransaction().replace(R.id.fl_content, fragment).commit();

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        // Close the navigation drawer
        mDrawer.closeDrawers();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch(item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        // NOTE: Make sure you pass in a valid toolbar reference.  ActionBarDrawToggle() does not require it
        // and will not render the hamburger icon without it.
        return new ActionBarDrawerToggle(this, mDrawer, toolBarHamburger, R.string.drawer_open,  R.string.drawer_close);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }
}