package com.benjaminsommer.dailygoals.ui.statistics;

import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.benjaminsommer.dailygoals.R;
import com.benjaminsommer.dailygoals.util.DrawerHelperClass;

public class StatisticActivity extends AppCompatActivity {

    private static final String TAG = StatisticActivity.class.getSimpleName();
    private static final int DRAWER_TAG = R.id.menuNavigation_statistics;

    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private StatisticSelectionAdapter statisticSelectionAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);

        // initialize toolbar
        toolbar = (Toolbar) findViewById(R.id.toolBarWithTab);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Statistik");
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        // initialize NavigationView
        navigationView = (NavigationView) findViewById(R.id.statisticsActivity_navigationView);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.statisticsActivity_drawer_layout);

        // fill drawer layout, set click listeners and check items
        DrawerHelperClass dhc = new DrawerHelperClass(this, navigationView, mDrawerLayout, DRAWER_TAG);
        dhc.setupNavigationLoginSection();
        dhc.setupItemSelectedListener();
        dhc.checkCurrentItem();

        // initialize Drawer Layout and ActionBarToggle
        setupDrawer();

        // initalize Tab Layout
        tabLayout = (TabLayout) findViewById(R.id.statisticsActivity_tabLayout);
        viewPager = (ViewPager) findViewById(R.id.statisticsActivity_viewPager);
        statisticSelectionAdapter = new StatisticSelectionAdapter(getSupportFragmentManager());
        statisticSelectionAdapter.addFragments(new StatisticOverallFragment(), "Gesamt");
        statisticSelectionAdapter.addFragments(new StatisticByDateFragment(), "Nach Datum");
        statisticSelectionAdapter.addFragments(new StatisticByGoalFragment(), "Nach Ziel");
        statisticSelectionAdapter.addFragments(new StatisticByWeekdayFragment(), "Nach Wochentag");
        viewPager.setAdapter(statisticSelectionAdapter);
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    protected void onResume() {
        super.onResume();

        // fill drawer layout, set click listeners and check items
        DrawerHelperClass dhc = new DrawerHelperClass(this, navigationView, mDrawerLayout, DRAWER_TAG);
        dhc.setupNavigationLoginSection();
        dhc.setupItemSelectedListener();
        dhc.checkCurrentItem();

        // initialize Drawer Layout and ActionBarToggle
        setupDrawer();

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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.optionsmenu, menu);
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
