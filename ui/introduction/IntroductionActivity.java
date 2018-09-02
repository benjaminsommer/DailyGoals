package com.benjaminsommer.dailygoals.ui.introduction;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.benjaminsommer.dailygoals.R;
import com.benjaminsommer.dailygoals.ui.goals.GoalsActivity;

import java.util.ArrayList;

public class IntroductionActivity extends AppCompatActivity {

    private static final String INTRODUCTION_PAGE_1 = "introductionPage1";
    private static final String INTRODUCTION_PAGE_2 = "introductionPage2";
    private static final String INTRODUCTION_PAGE_3 = "introductionPage3";
    private static final String INTRODUCTION_PAGE_4 = "introductionPage4";
    private static final String INTRODUCTION_PAGE_5 = "introductionPage5";
    private static final String INTRODUCTION_PAGE_6 = "introductionPage6";


    private ViewPager viewPager;
    private IntroductionPagerAdapter pagerAdapter;
    private IntroductionPagerAdapter introductionPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] fragments;
    private Button btnSkip, btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction);

        // initialize
        viewPager = (ViewPager) findViewById(R.id.introductionView_viewPager);
        dotsLayout = (LinearLayout) findViewById(R.id.introductionView_layoutDots);
        btnSkip = (Button) findViewById(R.id.introductionView_button_skip);
        btnNext = (Button) findViewById(R.id.introductionView_button_next);

        // making notification bar transparent
        changeStatusBarColor();

        // set ViewPager adapter
        pagerAdapter = new IntroductionPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        // adding bottom dots
        addBottomDots(0);

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchGoalDefinition();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checking for last page, if last page home screen will be launched
                int current = getItem(+1);
                if (current < pagerAdapter.getCount()) {
                    // move to next screen
                    viewPager.setCurrentItem(current);
                } else {
                    launchGoalDefinition();
                }
            }
        });
    }

    private void addBottomDots (int currentPage) {
        // initialize new Views
        dots = new TextView[pagerAdapter.getCount()];

        // set colors
        int[] colorActiveInactive = getResources().getIntArray(R.array.array_dots);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorActiveInactive[0]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0) {
            dots[currentPage].setTextColor(colorActiveInactive[1]);
        }
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void launchGoalDefinition() {
        Intent i = new Intent(IntroductionActivity.this, GoalsActivity.class);
        //i.putExtra("dataSetPosition", -1);
        startActivity(i);
        finish();
    }


    // ViewPager Change Listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            if (position == (pagerAdapter.getCount() - 1)) {
                btnNext.setText("Let's start");
                btnSkip.setVisibility(View.INVISIBLE);
            } else {
                btnNext.setText("Next");
                btnSkip.setVisibility(View.VISIBLE);
            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {}
    };

    // method to change notification bar color to transparent
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * View pager adapter
     */
    public class IntroductionPagerAdapter extends FragmentPagerAdapter {

        private FragmentManager fm;
        private ArrayList<Fragment> fragments = new ArrayList<>();

        public IntroductionPagerAdapter(FragmentManager fm) {
            super(fm);
            this.fm = fm;
        }

        public void addFragments(Fragment fragments) {
            this.fragments.add(fragments);
        }

        @Override
        public int getCount() {
            return 6;
//            return fragments.size();
        }

        @Override
        public Fragment getItem(int position) {

            Fragment fragment = fm.findFragmentByTag("android:switcher:" + viewPager.getId() + ":" + getItemId(position));

            // Return the fragment if it was stored in the fragment manager
            if (fragment != null) {
                return fragment;
            }

            return IntroductionFragment.newInstance(position);

        }

    }

}
