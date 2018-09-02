package com.benjaminsommer.dailygoals.ui.dataset;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by DEU209213 on 11.07.2016.
 */
public class GoalInput extends AppCompatActivity {
        //implements GoalInputAdapter.ClickedButtonInterface, EditTextDialogFragment.IEditTextDialogToActicity {

//    public static final String TAG = GoalInput.class.getSimpleName();
//    public static final int DRAWER_TAG = R.id.menuNavigation_goalsToday;
//    public static final String DAY_STRING = "dataSetDay";
//
//    // global variables
//    DrawerLayout mDrawerLayout;
//    ActionBarDrawerToggle mDrawerToggle;
//    Database db = new Database(this);
//    ArrayList<Goal> list = new ArrayList<>();
//    ArrayList<Boolean> listColExp = new ArrayList<>();
//    ArrayList<Integer> listSelectedNote = new ArrayList<>();
//
//    Goal goal;
//    private TextView tvMoney, tvDate, tvWeek, tvMoneyTotal, tvMotivator;
//    private String dataSetDate;
//    private RecyclerView recyclerView;
//    private ArrayList<CombinedDataSet> dailyGoalList;
//    private GoalInputAdapter goalInputAdapter;
//    private Calendar calendarDate;
//    private int differenceToToday;
//    private String strHeadline;
//    private float rewardValue;
//    private int rewardType;
//    private GestureDetectorCompat detector;
//    private RelativeLayout swiper1, swiper2;
//    private LinearLayout container;
//    private ImageView swiperColExp;
//    private FrameLayout flSwiper;
//    private NavigationView navigationView;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        AndroidInjection.inject(this);
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.input_dailygoal);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.inputDailyGoal_toolbar);
//        setSupportActionBar(toolbar);
//
//        // initialize NavigationView
//        navigationView = (NavigationView) findViewById(R.id.inputDailyGoal_navigationView);
//        mDrawerLayout = (DrawerLayout) findViewById(R.id.inputDailyGoal_drawer_layout);
//
//        // fill drawer layout, set click listeners and check items
//        DrawerHelperClass dhc = new DrawerHelperClass(this, navigationView, mDrawerLayout, DRAWER_TAG);
//        dhc.setupNavigationLoginSection();
//        dhc.setupItemSelectedListener();
//        dhc.checkCurrentItem();
//
//        // initialize Drawer Layout and ActionBarToggle
//        setupDrawer();
//
//        // identify views
//        tvDate = (TextView) findViewById(R.id.inputDailyGoal_textView_day);
//        tvWeek = (TextView) findViewById(R.id.inputDailyGoal_textView_calendarWeek);
//        tvMoney = (TextView) findViewById(R.id.inputDailyGoal_textView_day_money);
//        tvMoneyTotal = (TextView) findViewById(R.id.inputDailyGoal_textView_total_money);
//        recyclerView = (RecyclerView) findViewById(R.id.inputDailyGoal_recyclerView);
//
//        // set view swiper and hide him first
//        container = (LinearLayout) findViewById(R.id.inputDailyGoal_footer_container);
//        swiper1 = (RelativeLayout) findViewById(R.id.inputDailyGoal_relativeLayout_footer);
//        swiper2 = (RelativeLayout) findViewById(R.id.inputDailyGoal_relativeLayout_swiper);
//        swiperColExp = (ImageView) findViewById(R.id.inputDailyGoal_imageView_swiper_expandCollapse);
//        tvMotivator = (TextView) findViewById(R.id.inputDailyGoal_textView_motivator);
//        flSwiper = (FrameLayout) findViewById(R.id.inputDailyGoal_frameLayout_swiper_expCol);
//        swiper1.setVisibility(View.GONE);
//        swiper2.setVisibility(View.GONE);
//        detector = new GestureDetectorCompat(this, new MyGestureListener());
//
//        // Gesture detector
//        container.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                detector.onTouchEvent(event);
//                return true;
//            }
//        });
//
//        flSwiper.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (swiper1.getVisibility() == View.GONE) {
//                    swiper1.setVisibility(View.VISIBLE);
//                    swiper2.setVisibility(View.VISIBLE);
//                    swiperColExp.setImageResource(R.drawable.icon_expand_24dp);
//                } else {
//                    swiper1.setVisibility(View.GONE);
//                    swiper2.setVisibility(View.GONE);
//                    swiperColExp.setImageResource(R.drawable.icon_collapse_24dp);
//                }
//            }
//        });
//
//        // check for extra from intent to know which date should be shown --> information as date string
//        // if there is no extra from intent, it is today
//        Bundle extras = getIntent().getExtras();
//        dataSetDate = extras.getString(DAY_STRING);
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//        if (dataSetDate == null) {
//            Calendar calendar = Calendar.getInstance();
//            Date date = calendar.getTime();
//            dataSetDate = format.format(date);
//        }
////        // check if it is today
////        Calendar calToday = Calendar.getInstance();
////        Date today = calToday.getTime();
////        String strToday = format.format(today);
////        if (strToday.equals(dataSetDate)) {
////            isToday = true;
////        }
//
//        // get SharedPreferences information (reward value and currency)
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//        String strRewardValue = prefs.getString("pref_amount", "0.00");
//        rewardValue = Float.valueOf(strRewardValue);
//        String strRewardType = prefs.getString("pref_goalsReward", "1");
//        rewardType = Integer.valueOf(strRewardType);
//
//        // fill in shown day information
//        calendarDate = Calendar.getInstance();
//        calendarDate = convertStringToCalendar(dataSetDate);
//        fillDateInformation(calendarDate);
//
//
//        // fill ArrayList with goals of the selected day
//        dailyGoalList = db.getCombinedTableByDate(dataSetDate);
//        for (int y = 0; y < dailyGoalList.size(); y++) {
//            listColExp.add(false);
//            listSelectedNote.add(1);
//        }
//
//        // initialize Recycler View
//        initializeRecyclerView(dailyGoalList, listColExp, listSelectedNote);
//
//        // fill result box
//        calculateDailyResults(dataSetDate);
//        calculateTotalResults();
//
//    }
//
//    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
//        private static final String DEBUG_TAG = "Gestures";
//
//        @Override
//        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//            if ((e1.getY() - e2.getY()) > 0) {
//                swiper1.setVisibility(View.VISIBLE);
//                swiper2.setVisibility(View.VISIBLE);
//                swiperColExp.setImageResource(R.drawable.icon_expand_24dp);
//            } else if ((e2.getY() - e1.getY()) > 0) {
//                swiper1.setVisibility(View.GONE);
//                swiper2.setVisibility(View.GONE);
//                swiperColExp.setImageResource(R.drawable.icon_collapse_24dp);
//            }
//            return true;
//        }
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//        // calculate date difference
//        differenceToToday = calculateDayDifferenceToToday(calendarDate);
//        strHeadline = fillActionBarTitle(differenceToToday);
//        getSupportActionBar().setTitle(strHeadline);
//        getSupportActionBar().setDisplayUseLogoEnabled(true);
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_dailygoal_back_forward, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id =  item.getItemId();
//        // get table of all available dates
//        ArrayList<String> allDatabaseDates = db.getAllDatesFromDataSetDB();
//        int position = 0;
//        for (int i = 0; i < allDatabaseDates.size(); i++) {
//            if (allDatabaseDates.get(i).equals(dataSetDate)) {
//                position = i;
//                break;
//            }
//        }
//        if(id == R.id.menu_back) {
//            if (position > 0) {
//                dataSetDate = allDatabaseDates.get(position - 1);
//                calendarDate = convertStringToCalendar(dataSetDate);
//                // fill date information
//                fillDateInformation(calendarDate);
//                // fill ArrayList with goals of specific date and change adapter
//                dailyGoalList = db.getCombinedTableByDate(dataSetDate);
//                listColExp.clear();
//                listSelectedNote.clear();
//                for (int y = 0; y < dailyGoalList.size(); y++) {
//                    listColExp.add(false);
//                    listSelectedNote.add(1);
//                }
//                initializeRecyclerView(dailyGoalList, listColExp, listSelectedNote);
//                // update result box
//                calculateDailyResults(dataSetDate);
//                // change ActionBar title
//                differenceToToday = calculateDayDifferenceToToday(calendarDate);
//                strHeadline = fillActionBarTitle(differenceToToday);
//                getSupportActionBar().setTitle(strHeadline);
//            }
//        } else if (id == R.id.menu_forward) {
//            if (position < (allDatabaseDates.size() - 1)) {
//                dataSetDate = allDatabaseDates.get(position + 1);
//                calendarDate = convertStringToCalendar(dataSetDate);
//                // fill date information
//                fillDateInformation(calendarDate);
//                // fill ArrayList with goals of specific date and change adapter
//                dailyGoalList = db.getCombinedTableByDate(dataSetDate);
//                listColExp.clear();
//                listSelectedNote.clear();
//                for (int y = 0; y < dailyGoalList.size(); y++) {
//                    listColExp.add(false);
//                    listSelectedNote.add(1);
//                }
//                initializeRecyclerView(dailyGoalList, listColExp, listSelectedNote);
//                // update result box
//                calculateDailyResults(dataSetDate);
//                // change ActionBar title
//                differenceToToday = calculateDayDifferenceToToday(calendarDate);
//                strHeadline = fillActionBarTitle(differenceToToday);
//                getSupportActionBar().setTitle(strHeadline);
//            }
//        }
//
//        if (mDrawerToggle.onOptionsItemSelected(item)) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        // fill drawer layout, set click listeners and check items
//        DrawerHelperClass dhc = new DrawerHelperClass(this, navigationView, mDrawerLayout, DRAWER_TAG);
//        dhc.setupNavigationLoginSection();
//        dhc.setupItemSelectedListener();
//        dhc.checkCurrentItem();
//
//        // initialize Drawer Layout and ActionBarToggle
//        setupDrawer();
//
//    }
//
//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        return true;
//    }
//
//    private void setupDrawer() {
//        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close);
//        // setting the ActionBarToggle to drawer layout and call syncState to activate hamburger icon
//        mDrawerToggle.setDrawerIndicatorEnabled(true);
//        mDrawerLayout.addDrawerListener(mDrawerToggle);
//        mDrawerToggle.syncState();
//    }
//
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        mDrawerToggle.onConfigurationChanged(newConfig);
//    }
//
//    @Override
//    public void onGoalButtonClick(int position, int newValue, int oldValue) {
//        // update adapter
//        dailyGoalList.get(position).setGoalValue(newValue);
//        goalInputAdapter.notifyItemChanged(position);
//        // update DataSet database
//        int goalID = dailyGoalList.get(position).getID();
//        DataSet dataSet = db.getDataSet(goalID);
//        dataSet.setGoalValue(newValue);
//
//        // change MONEY_GOAL value
//        dataSet.setGoalMoney((newValue == 100) ? (float) dailyGoalList.get(position).getGoalReward() : 0.0f);
//        db.updateDataSet(dataSet);
//
////        // update Money database
////        Money tempMoney = db.getMoneyEntryByDate(dataSetDate);
////        float currentMoney = tempMoney.getValue();
////        if (tempMoney.getSettingType() == 1) { // money for each goal
////            if (newValue == 100) {
////                currentMoney = currentMoney + tempMoney.getSettingValue();
////            }
////            if (oldValue == 100) {
////                currentMoney = currentMoney - tempMoney.getSettingValue();
////            }
////        } else if (tempMoney.getSettingType() == 2) { // money for all goals
////            boolean areAllDailyGoalsYes = true;
////            for (int i = 0; i < dailyGoalList.size(); i++) {
////                if (dailyGoalList.get(i).getGoalValue() != 100) {
////                    areAllDailyGoalsYes = false;
////                    break;
////                }
////            }
////            if (areAllDailyGoalsYes) {
////                currentMoney = tempMoney.getSettingValue();
////            } else {
////                currentMoney = 0.0f;
////            }
////        }
////        tempMoney.setValue(currentMoney);
////        db.updateMoneyEntry(tempMoney);
//
//        // recalculate result box
//        calculateDailyResults(dataSetDate);
//        calculateTotalResults();
//    }
//
//    @Override
//    public void onRemoveButtonClick(int position) {
//        // get DataSet ID
//        int goalID = dailyGoalList.get(position).getID();
//        // remove dataset from adapter
//        dailyGoalList.remove(position);
//        goalInputAdapter.notifyDataSetChanged();
//        // remove dataset from DataSet database
//        DataSet dataSet = db.getDataSet(goalID);
//        db.deleteDataSet(dataSet);
//    }
//
//    // interface to GoalInputAdapter - open dialog to set note for daily goal
//    @Override
//    public void onNoticeEditClick(int position, String previousText) {
//        FragmentManager fm = getSupportFragmentManager();
//        EditTextDialogFragment editTextDialogFragment = EditTextDialogFragment.newInstance(position, previousText, 2);
//        editTextDialogFragment.show(fm, EditTextDialogFragment.TAG);
//    }
//
//    // interface to EditTextDialogFragment - get new note (if not "") and save it in dataset
//    @Override
//    public void getTextInput(int position, String text, int type) {
//        // changes to ArrayList for adapter and update it
//        dailyGoalList.get(position).setNotice(text);
//        // inform adapter that note is selected - listSelectedNote to true; set position to note = 2
//        listSelectedNote.set(position, 2);
//        goalInputAdapter.notifyItemChanged(position);
//        // changes to database
//        DataSet dataSet = db.getDataSet(dailyGoalList.get(position).getID());
//        dataSet.setNotice(text);
//        db.updateDataSet(dataSet);
//    }
//
//    private void calculateDailyResults(String date) {
//        // set counters
//        int counterUnknown = 0;
//        int counterNo = 0;
//        int counterYes = 0;
//        for (int i = 0; i < dailyGoalList.size(); i++) {
//            if (dailyGoalList.get(i).getGoalValue() == 1) {
//                counterUnknown++;
//            } else if (dailyGoalList.get(i).getGoalValue() == 10) {
//                counterNo++;
//            } else if (dailyGoalList.get(i).getGoalValue() == 100) {
//                counterYes++;
//            }
//        }
//
//        TextView barGrey = (TextView) findViewById(R.id.inputDailyGoal_textView_day_barChart_grey);
//        TextView barRed = (TextView) findViewById(R.id.inputDailyGoal_textView_day_barChart_red);
//        TextView barGreen = (TextView) findViewById(R.id.inputDailyGoal_textView_day_barChart_green);
//        FrameLayout flFrame = (FrameLayout) findViewById(R.id.inputDailyGoal_frameLayout_day_barChart);
//
//        // get LayoutParams from FrameLayout
//        RelativeLayout.LayoutParams lpFrame = (RelativeLayout.LayoutParams) flFrame.getLayoutParams();
//        int lpFrameLeftMargin = lpFrame.leftMargin;
//        int lpFrameRightMargin = lpFrame.rightMargin;
//
//        DisplayMetrics metrics = getResources().getDisplayMetrics();
//        int displayWidth = metrics.widthPixels;
//        int maxViewSize = displayWidth - (lpFrameLeftMargin + lpFrameRightMargin);
//        int valueGrey = counterUnknown;
//        int valueRed = counterNo;
//        int valueGreen = counterYes;
//        int valueSum = valueGreen + valueRed + valueGrey;
//        ViewGroup.LayoutParams lpGreen = barGreen.getLayoutParams();
//        lpGreen.width = valueGreen * maxViewSize / valueSum;
//        barGreen.setLayoutParams(lpGreen);
//        barGreen.setText(String.valueOf(valueGreen));
//        ViewGroup.LayoutParams lpRed = barRed.getLayoutParams();
//        lpRed.width = valueRed * maxViewSize / valueSum;
//        barRed.setLayoutParams(lpRed);
//        barRed.setText(String.valueOf(valueRed));
//        ViewGroup.LayoutParams lpGrey = barGrey.getLayoutParams();
//        lpGrey.width = valueGrey * maxViewSize / valueSum;
//        barGrey.setLayoutParams(lpGrey);
//        barGrey.setText(String.valueOf(valueGrey));
//
//        // reward calculation
//        Reward reward = db.getRewardPerDate(date, 1);
//        DecimalFormat df = new DecimalFormat(",##0.00 \u00A4");
//        tvMoney.setText(df.format(reward.getRewardValue()));
//
//    }
//
//    private void calculateTotalResults() {
//
//        SummarizedDataSet summarizedDataSet = db.getOverallResults();
//
//        TextView barGrey = (TextView) findViewById(R.id.inputDailyGoal_textView_total_barChart_grey);
//        TextView barRed = (TextView) findViewById(R.id.inputDailyGoal_textView_total_barChart_red);
//        TextView barGreen = (TextView) findViewById(R.id.inputDailyGoal_textView_total_barChart_green);
//        FrameLayout flFrame = (FrameLayout) findViewById(R.id.inputDailyGoal_frameLayout_total_barChart);
//
//        // get LayoutParams from FrameLayout
//        RelativeLayout.LayoutParams lpFrame = (RelativeLayout.LayoutParams) flFrame.getLayoutParams();
//        int lpFrameLeftMargin = lpFrame.leftMargin;
//        int lpFrameRightMargin = lpFrame.rightMargin;
//
//        DisplayMetrics metrics = getResources().getDisplayMetrics();
//        float massstab = metrics.density;
//        int displayWidth = metrics.widthPixels;
//        int maxViewSize = displayWidth - (lpFrameLeftMargin + lpFrameRightMargin);
//        int valueGrey = summarizedDataSet.getValuesOpen();
//        int valueRed = summarizedDataSet.getValuesNo();
//        int valueGreen = summarizedDataSet.getValuesYes();
//        int valueSum = valueGreen + valueRed + valueGrey;
//        ViewGroup.LayoutParams lpGreen = barGreen.getLayoutParams();
//        lpGreen.width = valueGreen * maxViewSize / valueSum;
//        barGreen.setLayoutParams(lpGreen);
//        float percGreen = (float) valueGreen / valueSum;
//        if (percGreen > 0.1f) {
//            barGreen.setText(String.format("%.0f%%",percGreen*100));
//        } else {
//            barGreen.setText("");
//        }
//        ViewGroup.LayoutParams lpRed = barRed.getLayoutParams();
//        lpRed.width = valueRed * maxViewSize / valueSum;
//        barRed.setLayoutParams(lpRed);
//        float percRed = (float) valueRed / valueSum;
//        if (percRed > 0.1f) {
//            barRed.setText(String.format("%.0f%%",percRed*100));
//        } else {
//            barRed.setText("");
//        }
//        ViewGroup.LayoutParams lpGrey = barGrey.getLayoutParams();
//        lpGrey.width = valueGrey * maxViewSize / valueSum;
//        barGrey.setLayoutParams(lpGrey);
//        float percGrey = (float) valueGrey / valueSum;
//        if (percGrey > 0.1f) {
//            barGrey.setText(String.format("%.0f%%",percGrey*100));
//        } else {
//            barGrey.setText("");
//        }
//
//        // reward calculation
//        Reward reward = db.getTotalReward(1);
//        DecimalFormat df = new DecimalFormat(",##0.00 \u00A4");
//        tvMoneyTotal.setText(df.format(reward.getRewardValue()));
//
//    }
//
//    private void fillDateInformation(Calendar calendar) {
//        // fill in shown day information
//        String strDay = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.GERMAN);
//        int intWeek = calendar.get(Calendar.WEEK_OF_YEAR);
//        int intDate = calendar.get(Calendar.DATE);
//        int intYear = calendar.get(Calendar.YEAR);
//        // set day + week inforamtion
//        tvDate.setText(strDay + ", " + intDate + ". " + calendarDate.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.GERMAN) + " " + intYear);
//        tvWeek.setText("KW" + intWeek);
//    }
//
//    public Calendar convertStringToCalendar(String strDate) {
//        // date has to be in format yyyy-MM-dd
//        String[] dateSplit = strDate.split("[./-]");
//        int intYear = Integer.parseInt(dateSplit[0]);
//        int intMonth = Integer.parseInt(dateSplit[1]);
//        int intDate = Integer.parseInt(dateSplit[2]);
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(intYear, intMonth - 1, intDate);
//        return calendar;
//    }
//
//    private void initializeRecyclerView(ArrayList<CombinedDataSet> arrayList, ArrayList<Boolean> listColExp, ArrayList<Integer> listSelectedNote) {
//        goalInputAdapter = new GoalInputAdapter(this, arrayList, listColExp, listSelectedNote, this);
//        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(mLayoutManager);
//        recyclerView.setAdapter(goalInputAdapter);
//    }
//
//    private int calculateDayDifferenceToToday(Calendar calendar) {
//        Calendar today = Calendar.getInstance();
//        // Calculation of date difference
//        long time = today.getTime().getTime() - calendar.getTime().getTime();
//        long days = Math.round((double) time / (24. * 60. * 60. * 1000.));
//        int difference = (int) days;
//        return difference;
//    }
//
//    public void setMoneyValue() {
//
//    }
//
//    private String fillActionBarTitle(int dayDifference) {
//        String headline;
//        switch (dayDifference) {
//            case 0:
//                headline = "Heute";
//                break;
//            case 1:
//                headline = "Gestern";
//                break;
//            case 2:
//                headline = "Vorgestern";
//                break;
//            default:
//                headline = "Vor " + dayDifference + " Tagen";
//        }
//        return headline;
//    }
}

