package com.benjaminsommer.dailygoals.ui.date_selection;

import android.app.FragmentTransaction;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.benjaminsommer.dailygoals.R;
import com.benjaminsommer.dailygoals.entities.Week;
import com.benjaminsommer.dailygoals.objects.Resource;
import com.benjaminsommer.dailygoals.objects.Status;
import com.benjaminsommer.dailygoals.util.DrawerHelperClass;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjection;


/**
 * Created by DEU209213 on 05.08.2016.
 */

public class DateSelectionActivity extends AppCompatActivity {

    private static final String TAG = DateSelectionActivity.class.getSimpleName();
    private static final int DRAWER_TAG = R.id.menuNavigation_goalsCalendar;

    private NavigationView navigationView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private RecyclerView recyclerView;
    private List<Week> weekList;
    private DateSelectionViewModel viewModel;
    private Context context;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_selection);
        context = this;

        // initialize toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.dateSelection_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Wähle ein Datum");
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        // initialize NavigationView
        navigationView = (NavigationView) findViewById(R.id.dateSelection_navigationView);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.dateSelection_drawer_layout);

        // fill drawer layout, set click listeners and check items
        DrawerHelperClass dhc = new DrawerHelperClass(this, navigationView, mDrawerLayout, DRAWER_TAG);
        dhc.setupNavigationLoginSection();
        dhc.setupItemSelectedListener();
        dhc.checkCurrentItem();

        // initialize Drawer Layout and ActionBarToggle
        setupDrawer();

        // fill recycler view
        //weekList = new ArrayList<>();


        // observe view model
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(DateSelectionViewModel.class);
        viewModel.getWeekList().observe(this, new Observer<Resource<List<Week>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<Week>> listResource) {
                if (listResource != null && listResource.status == Status.SUCCESS) {
                    weekList = listResource.data;
                    recyclerView = (RecyclerView) findViewById(R.id.dateSelection_recyclerView);
                    final DateListAdapter dateListAdapter = new DateListAdapter(context, weekList);
                    recyclerView.setAdapter(dateListAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    dateListAdapter.expandParent(0);
                    dateListAdapter.notifyDataSetChanged();
                }
            }
        });

        // initalize Tab Layout
//        TabLayout tabLayout = (TabLayout) findViewById(R.id.dateSeleciton_tabLayout);
//        ViewPager viewPager = (ViewPager) findViewById(R.id.dateSelection_viewPager);
//        DateSelectionAdapter dateSelectionAdapter = new DateSelectionAdapter(getSupportFragmentManager());
//        dateSelectionAdapter.addFragments(new DateListFragment(), "Liste");
//        dateSelectionAdapter.addFragments(new DateCalendarFragment(), "Kalender");
//        viewPager.setAdapter(dateSelectionAdapter);
//        tabLayout.setupWithViewPager(viewPager);
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
