package com.benjaminsommer.dailygoals.ui.todo;

import android.app.DatePickerDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
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
import com.benjaminsommer.dailygoals.ui.dialogs.DateTimePickerFragment;
import com.benjaminsommer.dailygoals.R;
import com.benjaminsommer.dailygoals.entities.StatResult;
import com.benjaminsommer.dailygoals.entities.ToDo;
import com.benjaminsommer.dailygoals.entities.ToDoCover;
import com.benjaminsommer.dailygoals.objects.Resource;
import com.benjaminsommer.dailygoals.objects.Status;
import com.benjaminsommer.dailygoals.ui.dialogs.MoneyPickerDialogFragment;
import com.benjaminsommer.dailygoals.util.DrawerHelperClass;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class ToDoActivity extends AppCompatActivity implements ToDoInputDialog.IToDoDialogFragmentToActivity, ToDoAdapter.ClickedButtonInterface, View.OnClickListener, DatePickerDialog.OnDateSetListener, MoneyPickerDialogFragment.IMoneyPickerToActivity {

    private static final String TAG = ToDoActivity.class.getSimpleName();
    private static final int DRAWER_TAG = R.id.menuNavigation_toDo;
    public static final String NOTIFICATION_IDENTIFIER = "notification_identifier";
    public static final String REMINDER_TIME = "reminderTime";
    private static final String FRAGMENT_TAG_TODOINPUTDIALOG = "ToDoInputDialog";
    private static final String FRAGMENT_TAG_DATETIMEPICKERFRAGMENT = "DateTimePicker";

    private NavigationView navigationView;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ToDoAdapter toDoAdapter;
    private BottomSheetBehavior bottomSheetBehavior;

    public boolean TAB_SELECTED = true; // true = open; false = finished
    private static final String TAB_TAG_OPEN = "tabTagOpen";
    private static final String TAB_TAG_FINISHED = "tabTagFinished";

    private Button btnToday, btnWeek, btnTotal;
    private TextView txtBottomYes, txtBottomNo, txtBottomOpen, txtBottomPercent, txtBottomMoney;
    private DecimalFormat currencyFormatter;
    private NumberFormat percentageFormatter;

    private ToDoViewModel viewModel;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    //// START of OnCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);


        // initialize Toolbar and setting it as the actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBarWithTab);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.toDoActivity);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        // initialize views
        navigationView = (NavigationView) findViewById(R.id.toDo_navigationView);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.toDo_drawerLayout);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.toDo_recyclerView);

        // initialize bottom sheet
        txtBottomYes = (TextView) findViewById(R.id.bottom_text_section_yes);
        txtBottomNo = (TextView) findViewById(R.id.bottom_text_section_no);
        txtBottomOpen = (TextView) findViewById(R.id.bottom_text_section_open);
        txtBottomPercent = (TextView) findViewById(R.id.bottom_text_section_percent);
        txtBottomMoney = (TextView) findViewById(R.id.bottom_text_section_money);

        // initialize formatter
        currencyFormatter = new DecimalFormat(",##0.00 \u00A4");
        percentageFormatter = NumberFormat.getPercentInstance(Locale.GERMANY);

        // fill drawer layout, set click listener and check items
        DrawerHelperClass dhc = new DrawerHelperClass(this, navigationView, mDrawerLayout, DRAWER_TAG);
        dhc.setupNavigationLoginSection();
        dhc.setupItemSelectedListener();
        dhc.checkCurrentItem();

        // initialize Drawer Layout
        setupDrawer();

        // RecyclerView setup
        toDoAdapter = new ToDoAdapter(this,this, true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(toDoAdapter);
        // end blinking of recycler view at new list submission
        RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }

        // view model setup
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ToDoViewModel.class);
        viewModel.setSelectedTab(TAB_SELECTED);
        viewModel.getSelectedTab().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                Log.d(TAG, "Observer getSelectedTab set up");
                if (aBoolean != null) {
                    Log.d(TAG, "Observer getSelectedTab: " + String.valueOf(aBoolean));
                    toDoAdapter.changeAdapterType(aBoolean);
                }
            }
        });
        viewModel.getToDoCoverList().observe(this, new Observer<Resource<List<ToDoCover>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<ToDoCover>> listResource) {
                Log.d(TAG, "Observer getToDoCoverList set up");

                if (listResource != null) {
                    if (listResource.status != Status.ERROR && listResource.data != null) {
                        Log.d(TAG, "Observer getToDoCoverList: " + listResource.toString());
                        toDoAdapter.submitList(listResource.data);
                    }
                }
            }
        });
        viewModel.setSelectedResult(StatResult.TODAY);
        viewModel.getResultList().observe(this, new Observer<StatResult>() {
            @Override
            public void onChanged(@Nullable StatResult statResult) {

                Log.d(TAG, "Observer getResultList set up");

                if (statResult != null) {
                    Log.d(TAG, "Observer getResultList: " + statResult.toString());
                    txtBottomYes.setText(String.valueOf(statResult.getResultYes()));
                    txtBottomNo.setText(String.valueOf(statResult.getResultNo()));
                    txtBottomOpen.setText(String.valueOf(statResult.getResultOpen()));
                    txtBottomPercent.setText(percentageFormatter.format(statResult.getResultPercent()));
                    txtBottomMoney.setText(currencyFormatter.format(statResult.getResultMoney()));
                }
            }
        });
        viewModel.getToDoList().observe(this, new Observer<Resource<List<ToDo>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<ToDo>> listResource) {
                Log.d(TAG, "Observer getToDoList: " + listResource.toString());
            }
        });

        // initialize Tab Layout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.toDo_tabLayout);
        TabLayout.Tab tabOpen = tabLayout.newTab();
        tabOpen.setTag(TAB_TAG_OPEN);
        tabOpen.setText(R.string.toDo_tab_open);
        TabLayout.Tab tabFinished = tabLayout.newTab();
        viewModel.setSelectedTab(TAB_SELECTED);
        tabFinished.setTag(TAB_TAG_FINISHED);
        tabFinished.setText(R.string.toDo_tab_finished);
        tabLayout.addTab(tabOpen,0,true);
        tabLayout.addTab(tabFinished,1,false);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getTag() == TAB_TAG_OPEN) {
                    TAB_SELECTED = true;
                } else if (tab.getTag() == TAB_TAG_FINISHED) {
                    TAB_SELECTED = false;
                }
                viewModel.setSelectedTab(TAB_SELECTED);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        // initialize Floating Action Button and setting OnClickListener
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.toDo_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                AppCompatDialogFragment newFragment = ToDoInputDialog.newInstance(true, new ToDo());
                transaction.add(newFragment, FRAGMENT_TAG_TODOINPUTDIALOG);
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

        // check for intent from Notification
        Bundle extras = getIntent().getExtras();
        if (getIntent().hasExtra(NOTIFICATION_IDENTIFIER)) {
            int id = extras.getInt(NOTIFICATION_IDENTIFIER, -1);
            if (id != -1) {
                ToDo toDo = viewModel.getSpecificToDoFromRoom(id);
                Log.d(TAG, "id: " + String.valueOf(id));
                Log.d(TAG, toDo.toString());
                startEditDialog(toDo);
                // cancel notification if notificaiton was already fired
                viewModel.cancelNotification(toDo);
            }
        }

    }
    //// END of OnCreate




//    @Override
//    protected void onStart() {
//        super.onStart();
//        viewModel.triggerToDoListAfterReActivityEnter();
//    }

    // OnClickListener interface
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

    // inteface from ToDoInputDialog
    @Override
    public void sendToDo(ToDo toDo, boolean isNew) {
        if (isNew) {
            viewModel.addToDo(toDo);
        } else {
            viewModel.updateToDo(toDo);
        }
    }

    // inteface from ToDoInputDialog
    @Override
    public void deleteToDo(ToDo toDo) {
        viewModel.deleteToDo(toDo);
    }

    // interface from ToDoAdapter
    @Override
    public void onGoalButtonClick(ToDo toDo, int newValue) {
        viewModel.updateButtonClick(toDo, newValue);
    }

    // interface from ToDoAdapter
    @Override
    public void onEditButtonClick(ToDo toDo) {
        startEditDialog(toDo);
    }

    // interface from ToDoAdapter
    @Override
    public void onDeleteButtonClick(ToDo toDo) {
        viewModel.deleteToDo(toDo);
    }

    private void startEditDialog(ToDo toDo) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        AppCompatDialogFragment newFragment = ToDoInputDialog.newInstance(false, toDo);
        transaction.add(newFragment, FRAGMENT_TAG_TODOINPUTDIALOG);
        transaction.commit();
    }

//    // method to activate reminder
//    public void generateReminder(int id, long reminderTime) {
//        ToDoReminderReceiver toDoReminderReceiver = new ToDoReminderReceiver();
//        toDoReminderReceiver.setAlarm(this, id, reminderTime);
//    }
//
//    // method to cancel reminder
//    public void cancelReminder(int id) {
//        // cancel pending intent
//        ToDoReminderReceiver toDoReminderReceiver = new ToDoReminderReceiver();
//        toDoReminderReceiver.cancelAlarm(this, id);
//
//        // cancel notification if notificaiton was already fired
//        NotificationManagerCompat.from(this).cancel(id);
//    }

    // method called from ToDoInputDialog to open DateTimePickerFragment
    public void openDateTimePickerDialog(long reminderTime) {
        DateTimePickerFragment dateTimePickerFragment = new DateTimePickerFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(REMINDER_TIME, reminderTime);
        dateTimePickerFragment.setArguments(bundle);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.add(dateTimePickerFragment, FRAGMENT_TAG_DATETIMEPICKERFRAGMENT);
        transaction.commit();
    }

    // method to receive Date&Time Input from DateTimePickerFragment and send it to ToDoInputDialog
    public void sendSelectedDateToInputDialog(long reminderTime) {
        ToDoInputDialog toDoInputDialog = (ToDoInputDialog) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_TODOINPUTDIALOG);
        if (toDoInputDialog != null) {
            toDoInputDialog.receiveDateTimeInfo(reminderTime);
        } else {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            AppCompatDialogFragment newFragment = ToDoInputDialog.newInstance(true, new ToDo());
            transaction.add(newFragment, FRAGMENT_TAG_TODOINPUTDIALOG);
            transaction.commit();
        }
    }

    // method to open DatePickerFragment when selected in DateTimePickerFragment
    public void openDatePickerFragment(long dateInMillis) {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(DatePickerFragment.DATE_VALUE, dateInMillis);
        datePickerFragment.setArguments(bundle);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.add(datePickerFragment, DatePickerFragment.TAG);
        transaction.commit();
    }

    // method to open MoneyPickerDialogFragment for ToDoInputDialog reward section
    public void openMoneyPickerDialog(double prevMoneyValue) {
        FragmentManager fm = getSupportFragmentManager();
        MoneyPickerDialogFragment moneyPickerDialogFragment = MoneyPickerDialogFragment.newInstance(-1, prevMoneyValue, 1);
        moneyPickerDialogFragment.show(fm, MoneyPickerDialogFragment.TAG);
    }

    // method to send reward value from MoneyPickerDialogFragment to ToDoInputDialog
    @Override
    public void getMoneyAmount(int position, double amount) {
        ToDoInputDialog toDoInputDialog = (ToDoInputDialog) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_TODOINPUTDIALOG);
        if (toDoInputDialog != null) {
            toDoInputDialog.receiveRewardInfo(amount);
        } else {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            AppCompatDialogFragment newFragment = ToDoInputDialog.newInstance(true, new ToDo());
            transaction.add(newFragment, FRAGMENT_TAG_TODOINPUTDIALOG);
            transaction.commit();
        }
    }

    //    // method to receive Date(only) Input from DatePickerFragment and send it to DateTimePickerFragment
//    public void sendDateToDateTimePickerFragment(int year, int month, int day) {
//        DateTimePickerFragment dateTimePickerFragment = (DateTimePickerFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_DATETIMEPICKERFRAGMENT);
//        if (dateTimePickerFragment != null) {
//            dateTimePickerFragment.receiveNewDate(year, month, day);
//        } else {
//            DateTimePickerFragment newDateTimePickerFragment = new DateTimePickerFragment();
//            Bundle bundle = new Bundle();
//            bundle.putLong(REMINDER_TIME, System.currentTimeMillis());
//            newDateTimePickerFragment.setArguments(bundle);
//            FragmentManager fm = getSupportFragmentManager();
//            FragmentTransaction transaction = fm.beginTransaction();
//            transaction.add(newDateTimePickerFragment, FRAGMENT_TAG_DATETIMEPICKERFRAGMENT);
//            transaction.commit();
//        }
//    }

    // method to receive Date(only) Input from DatePickerFragment and send it to DateTimePickerFragment
    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        DateTimePickerFragment dateTimePickerFragment = (DateTimePickerFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_DATETIMEPICKERFRAGMENT);
        if (dateTimePickerFragment != null) {
            dateTimePickerFragment.receiveNewDate(year, month, day);
        } else {
            DateTimePickerFragment newDateTimePickerFragment = new DateTimePickerFragment();
            Bundle bundle = new Bundle();
            bundle.putLong(REMINDER_TIME, System.currentTimeMillis());
            newDateTimePickerFragment.setArguments(bundle);
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.add(newDateTimePickerFragment, FRAGMENT_TAG_DATETIMEPICKERFRAGMENT);
            transaction.commit();
        }
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

        Log.d(TAG, "onResume set up");
        Log.d(TAG, "onResume ToDoCoverList has active Observers: " + String.valueOf(viewModel.getToDoCoverList().hasActiveObservers()));
        Log.d(TAG, "onResume ToDoCoverList has Observers: " + String.valueOf(viewModel.getToDoCoverList().hasObservers()));

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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
