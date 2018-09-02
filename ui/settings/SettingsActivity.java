package com.benjaminsommer.dailygoals.ui.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.benjaminsommer.dailygoals.Database;
import com.benjaminsommer.dailygoals.entities.Money;
import com.benjaminsommer.dailygoals.entities.MoneyExtended;
import com.benjaminsommer.dailygoals.R;
import com.benjaminsommer.dailygoals.entities.SummarizedDataSet;
import com.benjaminsommer.dailygoals.entities.Goal;
import com.benjaminsommer.dailygoals.repository.GoalsRepository;
import com.benjaminsommer.dailygoals.util.DrawerHelperClass;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

/**
 * Created by DEU209213 on 20.04.2016.
 */
public class SettingsActivity extends AppCompatActivity implements SettingsFragment.ISettingFragmentToActivity {

    private static final String TAG = SettingsActivity.class.getSimpleName();
    private static final int DRAWER_TAG = R.id.menuNavigation_settings;

    @Inject
    SharedPreferences sharedPrefs;

    @Inject
    GoalsRepository goalsRepository;

    // private variables
    private NavigationView navigationView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // register SharedPreferenceChangeListener
        // prefs.registerOnSharedPreferenceChangeListener(this);

        // initialize toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.settingsActivity_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.settingsActivity);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        // initialize NavigationView
        navigationView = (NavigationView) findViewById(R.id.settingsActivity_navigationView);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.settingsActivity_drawer_layout);

        // fill drawer layout, set click listeners and check items
        DrawerHelperClass dhc = new DrawerHelperClass(this, navigationView, mDrawerLayout, DRAWER_TAG);
        dhc.setupNavigationLoginSection();
        dhc.setupItemSelectedListener();
        dhc.checkCurrentItem();

        // initialize Drawer Layout and ActionBarToggle
        setupDrawer();

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(R.id.settingsActivity_linearLayout_container, new SettingsFragment())
                .commit();
    }

    // interface to SettingsFragment to handle change of standard goals amount
    @Override
    public void handleStandardAmountChange(final double newRewardAmount) {

        // TODO: 19.08.2018 NOT TESTED YET - test it via break points
        List<Goal> goalList = goalsRepository.getLocalActiveGoals();
        Log.d(TAG, goalList.toString());

        for (Goal goal : goalList) {
            if (!goal.getGoalRewardType()) {
                goal.setGoalReward(newRewardAmount);
                goalsRepository.updateGoal(goal);
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        // is it necessary here???
//        // fill drawer layout, set click listeners and check items
//        DrawerHelperClass dhc = new DrawerHelperClass(this, navigationView, mDrawerLayout, DRAWER_TAG);
//        dhc.setupNavigationLoginSection();
//        dhc.setupItemSelectedListener();
//        dhc.checkCurrentItem();
//
//        // initialize Drawer Layout and ActionBarToggle
//        setupDrawer();

    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close);
        // setting the ActionBarToggle to drawer layout and call syncState to activate hamburger icon
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
