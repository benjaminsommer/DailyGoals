package com.benjaminsommer.dailygoals.ui.dataset;

import android.app.DatePickerDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.benjaminsommer.dailygoals.ui.dialogs.DatePickerFragment;
import com.benjaminsommer.dailygoals.ui.dialogs.EditTextDialogFragment;
import com.benjaminsommer.dailygoals.R;
import com.benjaminsommer.dailygoals.entities.CombinedDataSet;
import com.benjaminsommer.dailygoals.entities.DataSet;
import com.benjaminsommer.dailygoals.entities.StatResult;
import com.benjaminsommer.dailygoals.objects.Resource;
import com.benjaminsommer.dailygoals.objects.Status;
import com.benjaminsommer.dailygoals.util.DrawerHelperClass;
import com.benjaminsommer.dailygoals.util.TimeHelperClass;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

/**
 * Created by SOMMER on 25.01.2018.
 */

public class DataSetActivity extends AppCompatActivity implements DataSetAdapter.ClickedButtonInterface, EditTextDialogFragment.IEditTextDialogToActicity, View.OnClickListener, DatePickerDialog.OnDateSetListener {

    public static final String TAG = DataSetActivity.class.getSimpleName();
    public static final int DRAWER_TAG = R.id.menuNavigation_goalsToday;
    public static final String DAY_STRING = "dataSetDay";

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private BottomSheetBehavior bottomSheetBehavior;

    private DataSetViewModel viewModel;
    private RecyclerView recyclerView;
    private DataSetAdapter adapter;
    private TextView tvDate, tvWeek;
    private Button btnToday, btnWeek, btnTotal;
    private TextView txtBottomYes, txtBottomNo, txtBottomOpen, txtBottomPercent, txtBottomMoney;
    private DecimalFormat currencyFormatter;
    private NumberFormat percentageFormatter;

    private long minDate = -1;
    private long maxDate = -1;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dataset);
        Toolbar toolbar = (Toolbar) findViewById(R.id.dataSet_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        // initialize NavigationView
        NavigationView navigationView = (NavigationView) findViewById(R.id.dataSet_navigationView);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.dataSet_drawer_layout);
        recyclerView = (RecyclerView) findViewById(R.id.dataSet_recyclerView);
        tvDate = (TextView) findViewById(R.id.dataSet_appBar_textView_day);
        tvWeek = (TextView) findViewById(R.id.dataSet_appBar_textView_calendarWeek);

        // initialize bottom sheet
        txtBottomYes = (TextView) findViewById(R.id.bottom_text_section_yes);
        txtBottomNo = (TextView) findViewById(R.id.bottom_text_section_no);
        txtBottomOpen = (TextView) findViewById(R.id.bottom_text_section_open);
        txtBottomPercent = (TextView) findViewById(R.id.bottom_text_section_percent);
        txtBottomMoney = (TextView) findViewById(R.id.bottom_text_section_money);

        // initialize formatter
        currencyFormatter = new DecimalFormat(",##0.00 \u00A4");
        percentageFormatter = NumberFormat.getPercentInstance(Locale.GERMANY);

        // fill drawer layout, set click listeners and check items
        DrawerHelperClass dhc = new DrawerHelperClass(this, navigationView, mDrawerLayout, DRAWER_TAG);
        dhc.setupNavigationLoginSection();
        dhc.setupItemSelectedListener();
        dhc.checkCurrentItem();

        // initialize Drawer Layout and ActionBarToggle
        setupDrawer();

        // fill toolbar date information
        fillToolbarDateInformation();

        // inititalize recycler view
        adapter = new DataSetAdapter(this, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
        // end blinking of recycler view at new list submission
        RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }

        // view model setup
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(DataSetViewModel.class);
        viewModel.setDate(getDate());
        viewModel.getDataSetList().observe(this, new Observer<Resource<List<CombinedDataSet>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<CombinedDataSet>> listResource) {
                if (listResource != null) {
                    if (listResource.status != Status.ERROR && listResource.data != null) {
                        adapter.submitList(listResource.data);
                    }
                }
            }
        });
        viewModel.setSelectedResult(StatResult.TODAY);
        viewModel.getResultList().observe(this, new Observer<StatResult>() {
            @Override
            public void onChanged(@Nullable StatResult statResult) {
                if (statResult != null) {
                    txtBottomYes.setText(String.valueOf(statResult.getResultYes()));
                    txtBottomNo.setText(String.valueOf(statResult.getResultNo()));
                    txtBottomOpen.setText(String.valueOf(statResult.getResultOpen()));
                    txtBottomPercent.setText(percentageFormatter.format(statResult.getResultPercent()));
                    txtBottomMoney.setText(currencyFormatter.format(statResult.getResultMoney()));
                }
            }
        });
        viewModel.getMinDate().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                if (s == null || s.equals("")) {
                    minDate = new Date().getTime();
                } else {
                    minDate = TimeHelperClass.convertStringToCalendar(s).getTimeInMillis();
                }
            }
        });
        viewModel.getMaxDate().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                if (s == null || s.equals("")) {
                    maxDate = new Date().getTime();
                } else {
                    maxDate = TimeHelperClass.convertStringToCalendar(s).getTimeInMillis();
                }
            }
        });

        // initialize Floating Action Button and setting OnClickListener
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.dataSet_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = TimeHelperClass.convertStringToCalendar(viewModel.getDate().getValue());
                DatePickerFragment datePickerFragment = new DatePickerFragment();
                Bundle bundle = new Bundle();
                bundle.putLong(DatePickerFragment.DATE_VALUE, calendar.getTimeInMillis());
                if (minDate != -1 && maxDate != -1) {
                    bundle.putLong(DatePickerFragment.MIN_DATE_VALUE, minDate);
                    bundle.putLong(DatePickerFragment.MAX_DATE_VALUE, maxDate);
                }
                datePickerFragment.setArguments(bundle);
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.add(datePickerFragment, DatePickerFragment.TAG);
                transaction.commit();
            }
        });

        // inititalize Bottom Sheet Layout and Behaviour
        final View greyedBackground = (View) findViewById(R.id.bottom_sheet_bg_color);
        final View bottomSheetShadow = (View) findViewById(R.id.bottom_sheet_shadow);
        greyedBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        NestedScrollView bottomSheetLayout = (NestedScrollView) findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    greyedBackground.setVisibility(View.VISIBLE);
                    fab.setVisibility(View.GONE);
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    greyedBackground.setVisibility(View.GONE);
                    fab.setVisibility(View.VISIBLE);
                } else if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    greyedBackground.setVisibility(View.GONE);
                    bottomSheetShadow.setVisibility(View.GONE);
                    fab.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                greyedBackground.setVisibility(View.VISIBLE);
                greyedBackground.setAlpha(slideOffset);
                if (slideOffset >= 0f) {
                    fab.setScaleX(1f - slideOffset);
                    fab.setScaleY(1f - slideOffset);
                }
            }
        });

        // bottom sheet logic
        btnToday = (Button) findViewById(R.id.bottom_button_today);
        btnWeek = (Button) findViewById(R.id.bottom_button_week);
        btnTotal = (Button) findViewById(R.id.bottom_button_total);
        btnToday.setSelected(true);
        btnToday.setOnClickListener(this);
        btnWeek.setOnClickListener(this);
        btnTotal.setOnClickListener(this);

    }

    // receive data back from FAB date picker
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        String strDate = TimeHelperClass.convertCalendarToString(calendar);
        Log.d(TAG, "strDate at Date change: " + strDate);
        viewModel.setDate(strDate);
        fillDateInformation(calendar);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnToday.getId()) {
            viewModel.setSelectedResult(StatResult.TODAY);
            btnToday.setSelected(true);
            btnWeek.setSelected(false);
            btnTotal.setSelected(false);
        } else if (v.getId() == btnWeek.getId()) {
            viewModel.setSelectedResult(StatResult.WEEK);
            btnToday.setSelected(false);
            btnWeek.setSelected(true);
            btnTotal.setSelected(false);
        } else if (v.getId() == btnTotal.getId()) {
            viewModel.setSelectedResult(StatResult.TOTAL);
            btnToday.setSelected(false);
            btnWeek.setSelected(false);
            btnTotal.setSelected(true);
        }
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private String getDate() {
        // check for extra from intent to know which date should be shown --> information as date string
        // if there is no extra from intent, it is today
        Bundle extras = getIntent().getExtras();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMANY);
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        String today = format.format(date);

        if (extras != null) {
            today = extras.getString(DAY_STRING, today);
        }
        return today;
    }

    @Override
    public void onDataSetChange(DataSet dataSet) {
        viewModel.updateDataSet(dataSet);
    }

    @Override
    public void onRemoveButtonClick(DataSet dataSet) {
        viewModel.deleteDataSet(dataSet);

    }

    // interface to GoalInputAdapter - open dialog to set note for daily goal
    @Override
    public void onNoticeEditClick(int position, String previousText) {
        FragmentManager fm = getSupportFragmentManager();
        EditTextDialogFragment editTextDialogFragment = EditTextDialogFragment.newInstance(position, previousText, 2);
        editTextDialogFragment.show(fm, EditTextDialogFragment.TAG);
    }

    @Override
    public void getTextInput(int position, String text, int type) {
        viewModel.changeDataSetNotice(position, text);
    }

    private void fillToolbarDateInformation() {
        // check for extra from intent to know which date should be shown --> information as date string
        // if there is no extra from intent, it is today
        Bundle extras = getIntent().getExtras();
        String dataSetDate = "";
        if (extras != null && extras.containsKey(DAY_STRING)) {
            dataSetDate = extras.getString(DAY_STRING);
        } else {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Calendar calendar = Calendar.getInstance();
            Date date = calendar.getTime();
            dataSetDate = format.format(date);
        }
        Calendar calendar = TimeHelperClass.convertStringToCalendar(dataSetDate);
        fillDateInformation(calendar);

    }

    private void fillDateInformation(Calendar calendar) {
        // fill in shown day information
        String strDay = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.GERMAN);
        int intWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        int intDate = calendar.get(Calendar.DATE);
        int intYear = calendar.get(Calendar.YEAR);
        // set day + week inforamtion
        tvDate.setText(strDay + ", " + intDate + ". " + calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.GERMAN) + " " + intYear);
        tvWeek.setText("KW" + intWeek);
    }


    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close);
        // setting the ActionBarToggle to drawer layout and call syncState to activate hamburger icon
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

}
