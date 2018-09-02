package com.benjaminsommer.dailygoals.ui.wishlist;

import android.content.res.Configuration;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.benjaminsommer.dailygoals.R;
import com.benjaminsommer.dailygoals.util.DrawerHelperClass;

public class WishListActivity extends AppCompatActivity {

    private static final String TAG = WishListActivity.class.getSimpleName();
    private static final int DRAWER_TAG = R.id.menuNavigation_wishList;

    private Toolbar toolbar;
    private NavigationView navigationView;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set content and views and variables
        setContentView(R.layout.activity_wish_list);

        // initialize Toolbar and setting it as the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolBarWithTab);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Wunschliste");
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        // initialize NavigationView
        navigationView = (NavigationView) findViewById(R.id.wishList_navigationView);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.wishList_drawer_layout);

        // fill drawer layout, set click listener and check items
        DrawerHelperClass dhc = new DrawerHelperClass(this, navigationView, mDrawerLayout, DRAWER_TAG);
        dhc.setupNavigationLoginSection();
        dhc.setupItemSelectedListener();
        dhc.checkCurrentItem();

        // initialize Drawer Layout
        setupDrawer();

        // initialize Tab Layout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.wishList_tabLayout);
        ViewPager viewPager = (ViewPager) findViewById(R.id.wishList_viewPager);
        TabLayoutAdapter tabLayoutAdapter = new TabLayoutAdapter(getSupportFragmentManager());
        tabLayoutAdapter.addFragments(new WishlistSearchFragment(), "Suche");
        tabLayoutAdapter.addFragments(new WishlistOverviewFragment(), "Übersicht");
        viewPager.setAdapter(tabLayoutAdapter);
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
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
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
