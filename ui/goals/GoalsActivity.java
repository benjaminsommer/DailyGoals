package com.benjaminsommer.dailygoals.ui.goals;

import android.app.AlertDialog;
import android.app.Dialog;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.benjaminsommer.dailygoals.ui.dialogs.CategoryDialog;
import com.benjaminsommer.dailygoals.util.DrawerHelperClass;
import com.benjaminsommer.dailygoals.ui.dialogs.EditTextDialogFragment;
import com.benjaminsommer.dailygoals.ui.dataset.DataSetAdapter;
import com.benjaminsommer.dailygoals.ui.dialogs.MoneyPickerDialogFragment;
import com.benjaminsommer.dailygoals.util.OnStartDragListener;
import com.benjaminsommer.dailygoals.R;
import com.benjaminsommer.dailygoals.util.SimpleItemTouchHelperCallback;
import com.benjaminsommer.dailygoals.di.Injectable;
import com.benjaminsommer.dailygoals.entities.Goal;
import com.benjaminsommer.dailygoals.objects.Resource;
import com.enrico.colorpicker.colorDialog;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

/**
 * Created by DEU209213 on 07.05.2016.
 */
public class GoalsActivity extends AppCompatActivity implements Injectable, GoalCardAdapter.ClickActivityInformation, colorDialog.ColorSelectedListener, AdapterView.OnItemClickListener, OnStartDragListener, MoneyPickerDialogFragment.IMoneyPickerToActivity, EditTextDialogFragment.IEditTextDialogToActicity {

    private static final String TAG = GoalsActivity.class.getSimpleName();
    private static final int DRAWER_TAG = R.id.menuNavigation_goalsDefine;

    //local variables
    private NavigationView navigationView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ItemTouchHelper itemTouchHelper;

    private RecyclerView recyclerView;
    private GoalCardAdapter goalCardAdapter;

    private FloatingActionButton buttonAddGoal;

    private SharedPreferences prefs;
    private List<Goal> goalArrayList;

    private GoalsViewModel viewModel;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private int adapterPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals);

        // initialize ArraLists
        goalArrayList = new ArrayList<Goal>();

        // standard value for adapter position is -1
        adapterPosition = -1;

        // initialize PreferenceManger
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        // initialize Floating Action Button
        buttonAddGoal = (FloatingActionButton) findViewById(R.id.goals_fab);

        // initialize toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.goals_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Ziele definieren");
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        // initialize NavigationView
        navigationView = (NavigationView) findViewById(R.id.goals_navigationView);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.goals_drawer_layout);

        // fill drawer layout, set click listeners and check items
        DrawerHelperClass dhc = new DrawerHelperClass(this, navigationView, mDrawerLayout, DRAWER_TAG);
        dhc.setupNavigationLoginSection();
        dhc.setupItemSelectedListener();
        dhc.checkCurrentItem();

        // initialize Drawer Layout and ActionBarToggle
        setupDrawer();

        // initialize and setup recycler view
        recyclerView = (RecyclerView) findViewById(R.id.goals_recyclerView);
        goalCardAdapter = new GoalCardAdapter(GoalsActivity.this, GoalsActivity.this, GoalsActivity.this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(GoalsActivity.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(goalCardAdapter);

        // view model setup
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(GoalsViewModel.class);
        LiveData<Resource<List<Goal>>> resource = viewModel.getGoalsList();
        resource.observe(this, new Observer<Resource<List<Goal>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<Goal>> listResource) {
                if (listResource.data != null) {
                    goalCardAdapter.setGoalList(listResource.data);
                    goalArrayList.clear();
                    goalArrayList.addAll(listResource.data);

                    if (listResource.data != null) {
                        for (int x = 0; x < listResource.data.size(); x++) {
                            Log.d("GoalsActivityDebug", listResource.data.get(x).toString());
                        }
                    }

                }
//                if (listResource.data == null) {
//                    goalArrayList.clear();
//                    listColExp.clear();
//                    goalCardAdapter.notifyDataSetChanged();
//                } else {
//                    goalArrayList.clear();
//                    goalArrayList.addAll(listResource.data);
//                    listColExp.clear();
//                    for (int y = 0; y < goalArrayList.size(); y++) {
//                        listColExp.add(false);
//                    }
//                    goalCardAdapter.notifyDataSetChanged();
//                }
            }
        });


        // initialize Recycler View and check if we want to show a specific goal (intent from GoalInput
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int goalID = extras.getInt(DataSetAdapter.GOAL_EDIT_POSITION, -1);
            if (goalID != -1) {
                int position = 0;
                for (int x = 0; x < goalArrayList.size(); x++) {
                    if (goalArrayList.get(x).getGoalId() == goalID) {
                        position = x;
                        break;
                    }
                }
                goalCardAdapter.notifyItemChanged(position);
                recyclerView.smoothScrollToPosition(position);
            }
        }

        // hide or show Floating Action Button
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0){
                    buttonAddGoal.hide();
                } else if (dy < 0) {
                    buttonAddGoal.show();
                }
            }
        });

        // Item Touch Listener for Recycler View
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(goalCardAdapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        // ClickListener for Floating Action Button
        buttonAddGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.addGoal();
                recyclerView.smoothScrollToPosition(goalArrayList.size() - 1);

//                // create a new dataSet for Today
//                DataSet newDataSetForToday = new DataSet();
//                String today = mainActivity.generateTodayAsString();
//                newDataSetForToday.setDate(today);
//                newDataSetForToday.setGoalID(newGoal.getId());
//                db.addDataSet(newDataSetForToday);
            }
        });
    }

    @Override
    protected void onStop() {
        viewModel.setExpandedToFalse();
        Log.d("GoalsActivity", "viewModel.setExpandedToFalse is called");
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // TODO: 05.11.2017: change of goal reward should be done in preference section when prefence value is changed 
        // fill array about collapsed and expanded goals
//        for (int y = 0; y < goalArrayList.size(); y++) {
//            listColExp.add(false);
//
//            double curVal = validateRewardValue(goalList.get(y).getGoalRewardType(), goalList.get(y).getGoalReward());
//            if (curVal >= 0.0) {
//                // if the preference reward value is changed, it is validated here and goals are adopted
//                goalList.get(y).setGoalReward(curVal);
//                db.updateGoal(goalList.get(y));
//
//                //// FIRESTORE
//                colRefGoals.document(goalKeyList.get(y))
//                        .update("goalReward", curVal)
//                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                Log.d(TAG, "DocumentSnapshot successfully updated");
//                            }
//                        })
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Log.w(TAG, "Error updating document", e);
//                            }
//                        });
//                //// FIRESTORE
//
//            }
//        }
    }

    @Override
    public void onGoalListFirstLoad() {
        viewModel.setExpandedToFalse();
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

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        itemTouchHelper.startDrag(viewHolder);
    }

    // method to handle click on Category button
    @Override
    public void onHandleSelection(int position) {
        CategoryDialog categoryDialog = new CategoryDialog();
        categoryDialog.show(getSupportFragmentManager(), CategoryDialog.TAG);
        adapterPosition = position;
    }

    // method to handle click on Color button
    @Override
    public void onColorSelection(int position, int formerColor) {
        colorDialog.setPickerColor(this, 1, formerColor);
        colorDialog.showColorPicker(this, 1);
        adapterPosition = position;
    }

    // interface for ColorPicker Dialog
    @Override
    public void onColorSelection(DialogFragment dialogFragment, @ColorInt int selectedColor) {
        int goalID = goalArrayList.get(adapterPosition).getGoalId();
        if (goalID != -1) {
            Goal goal = goalArrayList.get(adapterPosition);
            if (goal.getGoalColor() != selectedColor) {
                goal.setGoalColor(selectedColor);
                viewModel.updateGoal(goal);
                colorDialog.setPickerColor(this, 1, selectedColor);
            }
        }
    }

    @Override
    public void onExpandSection(int position, boolean isExpanded) {
        Goal goal = goalArrayList.get(position);
        goal.setGoalExpanded(isExpanded);
        viewModel.updateGoal(goal);
    }

    // method to handle goal deletion
    @Override
    public void onGoalDeletion(int position) {
        Goal goal = goalArrayList.get(position);
        goal.setActivated(0);
        viewModel.updateGoal(goal);

        // TODO: 13.12.2017: Update dataSet after finalization of goal
//        // delete DataSet input for today if existing
//        String today = mainActivity.generateTodayAsString();
//        if (db.doesSpecificDataSetInputExist(today, goalToDelete.getId())) {
//            DataSet dataSetToDelete = db.getSpecificDataSet(today, goalToDelete.getId());
//            db.deleteDataSet(dataSetToDelete);
//        }
    }

    @Override
    public void onRadioButtonChange(int position, int newFrequency) {
        Goal goal = goalArrayList.get(position);
        List<Integer> goalFrequency = goal.getGoalFrequency();
        goalFrequency.set(0, newFrequency);
        goal.setGoalFrequency(goalFrequency);
        viewModel.updateGoal(goal);

        // changes to today's goals
        // TODO: 19.12.2017: Update dataSet after radio button change
        //changeDataSetAfterFrequencyChange(position);
    }

    @Override
    public void onFrequencySelection(final int position) {
        // get FrequencyType and FrequencyCode
        Goal goal = goalArrayList.get(position);
        this.generateFrequencyDialog(this, goal, position);
    }

    public void generateFrequencyDialog(Context context, final Goal internalGoal, final int position) {
        // get FrequencyType and FrequencyCode
        final List<Integer> goalFrequency = internalGoal.getGoalFrequency();

        Dialog dialog;
        final String[] strWeekdays = context.getResources().getStringArray(R.array.weekdays);
        final String[] strMonthSelection = context.getResources().getStringArray(R.array.months);
        // fill pre-selection
        final boolean[] blnWeekdays = new boolean[7];
        final boolean[] blnMonthSelection = new boolean[3];
        if (goalFrequency.size() > 1) {
            for (int x = 0; x < 7; x++) {
                blnWeekdays[x] = (goalFrequency.get(x + 1) != 1);
            }
            for (int y = 0; y < 3; y++) {
                blnMonthSelection[y] = (goalFrequency.get(y + 8) != 1);
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Dialog Title
        builder.setTitle(context.getString(R.string.freqDialogTitle));

        // MultipleChoice
        if (goalFrequency.get(0) == 1) {
            builder.setMultiChoiceItems(strWeekdays, blnWeekdays, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    if (isChecked) {
                        Log.d("Which1:", String.valueOf(which));
                        goalFrequency.set(which + 1, 2);
                        blnWeekdays[which] = true;
                    } else {
                        Log.d("Which2:", String.valueOf(which));
                        goalFrequency.set(which + 1, 1);
                        blnWeekdays[which] = false;
                    }
                }
            });
        } else if (goalFrequency.get(0) == 2) {
            builder.setMultiChoiceItems(strMonthSelection, blnMonthSelection, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    if (isChecked) {
                        Log.d("Which3:", String.valueOf(which));
                        goalFrequency.set(which + 8, 2);
                        blnMonthSelection[which] = true;
                    } else {
                        Log.d("Which4:", String.valueOf(which));
                        goalFrequency.set(which + 8, 1);
                        blnMonthSelection[which] = false;
                    }
                }
            });
        }
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Updating and saving procedure
                internalGoal.setGoalFrequency(goalFrequency);
                viewModel.updateGoal(internalGoal);

                // changes to today's goals
                // TODO: 19.12.2017: Update dataSet after frequency change
                //changeDataSetAfterFrequencyChange(position);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // TODO: 13.12.2017: update of EditText - necessary? if yes, implement!
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                // check for changes in EditText fields and save them to the database
//                for (int x = 0; x < goalList.size(); x++) {
//                    int goalID = goalList.get(x).getId();
//                    Goal tempGoal = db.getGoalByID(goalID);
//                    tempGoal.setGoalName(goalList.get(x).getGoalName());
//                    tempGoal.setGoalDescription(goalList.get(x).getGoalDescription());
//                    db.updateGoal(tempGoal);
//                    //// FIRESTORE
//                    colRefGoals.document(goalKeyList.get(x))
//                            .update("goalName", goalList.get(x).getGoalName(), "goalDescription", goalList.get(x).getGoalDescription())
//                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void aVoid) {
//                                    Log.d(TAG, "DocumentSnapshot successfully updated");
//                                }
//                            })
//                            .addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Log.w(TAG, "Error updating document", e);
//                                }
//                            });
//                    //// FIRESTORE
//                }
//                // goalCardAdapter.notifyDataSetChanged();
//
//                for (int y = 0; y < goalList.size(); y++) {
//                    goalList.get(y).setGoalPos(y);
//                    db.updateGoal(goalList.get(y));
//                    //// FIRESTORE
//                    colRefGoals.document(goalKeyList.get(y))
//                            .update("goalPos", y)
//                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void aVoid) {
//                                    Log.d(TAG, "DocumentSnapshot successfully updated");
//                                }
//                            })
//                            .addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Log.w(TAG, "Error updating document", e);
//                                }
//                            });
//                    goalArrayList.get(y).setGoalPos(y);
//                    //// FIRESTORE
//                }
//
//            }
//        }).start();

    }

    public void changeDataSetAfterFrequencyChange(int position) {
        // changes to today's goals
//        Calendar calendar = Calendar.getInstance();
//        String today = mainActivity.generateTodayAsString();
//        int goalID = goalList.get(position).getId();
//        DataSet tempDataSet = db.getSpecificDataSet(today, goalID);
//        boolean doesDataSetExist = false;
//        if (tempDataSet.getID() != 0) {
//            doesDataSetExist = true;
//        }
//        boolean isGoalValidForToday = mainActivity.validateGoalPerDay(goalList.get(position), calendar);
//        if (!doesDataSetExist && isGoalValidForToday) {
//            DataSet dataSet = new DataSet();
//            dataSet.setDate(today);
//            dataSet.setGoalID(goalList.get(position).getId());
//            db.addDataSet(dataSet);
//        } else if (doesDataSetExist && !isGoalValidForToday) {
//            db.deleteDataSet(tempDataSet);
//        }
    }

    // interface to GoalCardAdapter - Reward switch touched
    @Override
    public void onRewardSelection(int position, boolean isChecked) {
        Goal goal = goalArrayList.get(position);
        goal.setGoalRewardType(isChecked);
        viewModel.updateGoal(goal);
    }

    // interface to GoalCardAdapter to open MoneyPickerFragment
    @Override
    public void onRewardAmountSelection(int position, double amount) {
        FragmentManager fm = getSupportFragmentManager();
        MoneyPickerDialogFragment moneyPickerDialogFragment = MoneyPickerDialogFragment.newInstance(position, amount, getPrefCurrency());
        moneyPickerDialogFragment.show(fm, MoneyPickerDialogFragment.TAG);
    }

    // interface to MoneyPickerFragment - reward amount changed
    @Override
    public void getMoneyAmount(int position, double amount) {
        Goal goal = goalArrayList.get(position);
        goal.setGoalReward(amount);
        viewModel.updateGoal(goal);
    }

    // interface from TextEditSelection
    @Override
    public void onTextEditSelection(int position, String previousText, int isNameOrDescription) {
        FragmentManager fm = getSupportFragmentManager();
        EditTextDialogFragment editTextDialogFragment = EditTextDialogFragment.newInstance(position, previousText, isNameOrDescription);
        editTextDialogFragment.show(fm, EditTextDialogFragment.TAG);
    }

    // interface from EditTextDialogFragment
    @Override
    public void getTextInput(int position, String text, int type) {
        Goal goal = goalArrayList.get(position);
        if (type == 0) {
            goal.setGoalName(text);
        } else if (type == 1) {
            goal.setGoalDescription(text);
        }
        viewModel.updateGoal(goal);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        int goalID = goalArrayList.get(adapterPosition).getGoalId();
        if (goalID != -1) {
            Goal goal = goalArrayList.get(adapterPosition);
            goal.setGoalCategory(position + 1);
            viewModel.updateGoal(goal);
        }
        getSupportFragmentManager().beginTransaction().remove(getVisibleFragment()).commit();
    }

    public Fragment getVisibleFragment() {
        FragmentManager fm = this.getSupportFragmentManager();
        List<Fragment> fragments = fm.getFragments();
        if(fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment != null && fragment.isVisible()) {
                    return fragment;
                }
            }
        }
        return null;
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

    // method to validate if the goal reward type is standard and if it is so,
    // if preference reward value is different than current value saved in database
    public double validateRewardValue(boolean rewardType, double currentRewardValue) {
        // get reward in preferences
        String strRewardValue = prefs.getString("pref_amount", "0.00");
        double prefRewardValue = Double.valueOf(strRewardValue);

        if (!rewardType && currentRewardValue != prefRewardValue) {
            return prefRewardValue;
        } else {
            return -1.0;
        }
    }

    public int getPrefCurrency() {
        // get currency in preferences
        String strCurrency = prefs.getString("pref_currency", "1");
        return Integer.valueOf(strCurrency);
    }

}
