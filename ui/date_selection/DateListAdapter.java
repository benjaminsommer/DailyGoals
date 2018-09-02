package com.benjaminsommer.dailygoals.ui.date_selection;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.benjaminsommer.dailygoals.R;
import com.benjaminsommer.dailygoals.entities.StatResult;
import com.benjaminsommer.dailygoals.entities.Week;
import com.benjaminsommer.dailygoals.ui.dataset.DataSetActivity;
import com.benjaminsommer.dailygoals.entities.DaySummary;
import com.benjaminsommer.dailygoals.util.TimeHelperClass;
import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by DEU209213 on 06.08.2016.
 */
public class DateListAdapter extends ExpandableRecyclerAdapter<DateListAdapter.WeekViewHolder, DateListAdapter.DayViewHolder> {
    private LayoutInflater inflater;
    private Context mContext;

    public DateListAdapter(Context context, List<? extends ParentListItem> parentItemList) {
        super(parentItemList);
        inflater = LayoutInflater.from(context);
        mContext = context;
    }

    public class WeekViewHolder extends ParentViewHolder {
        private TextView calWeek;
        private ImageView colExp;

        public WeekViewHolder(View itemView) {
            super(itemView);
            calWeek = (TextView) itemView.findViewById(R.id.parentView_calWeek);
            colExp = (ImageView) itemView.findViewById(R.id.parentView_colExp);

        }

        @Override
        public boolean shouldItemViewClickToggleExpansion() {
            return false;
        }

        public void bind(Week week) {
//            String[] dateSplit = week.getWeekText().split("[./-]");
//            calWeek.setText("KW " + dateSplit[1] + " / " + dateSplit[0]);
            calWeek.setText("KW " + week.getWeekText());
            colExp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isExpanded()) {
                        collapseView();
                    } else {
                        expandView();
                    }
                }
            });
        }

    }

    public class DayViewHolder extends ChildViewHolder {
        private TextView weekday, date, money, barGreen, barRed, barGrey;
        private RelativeLayout box;
        private FrameLayout frame;


        public DayViewHolder(View itemView) {
            super(itemView);
            weekday = (TextView) itemView.findViewById(R.id.childView_weekday);
            date = (TextView) itemView.findViewById(R.id.childView_date);
            money = (TextView) itemView.findViewById(R.id.childView_textView_resultMoney);
            barGreen = (TextView) itemView.findViewById(R.id.childView_barChart_green);
            barRed = (TextView) itemView.findViewById(R.id.childView_barChart_red);
            barGrey = (TextView) itemView.findViewById(R.id.childView_barChart_grey);
            box = (RelativeLayout) itemView.findViewById(R.id.childView_box);
            frame = (FrameLayout) itemView.findViewById(R.id.childView_frameLayout_barChart);
        }

        public void bind (StatResult statResult) {
            String[] dateSplit = statResult.getDate().split("[./-]");
//            Calendar calendar = Calendar.getInstance();
//            calendar.set(Integer.valueOf(dateSplit[2]), Integer.valueOf(dateSplit[1]), Integer.valueOf(dateSplit[0]));
            Calendar calendar = TimeHelperClass.convertStringToCalendar(statResult.getDate());
            String strWeekday = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.GERMAN);
            weekday.setText(strWeekday);

            // date formats + set date field
            SimpleDateFormat sdfGermany = new SimpleDateFormat("dd.MM.yyyy");
            Date dtDate = calendar.getTime();
            date.setText(sdfGermany.format(dtDate));

            // money formats + set money field
            DecimalFormat df = new DecimalFormat(",##0.00 \u00A4");
            money.setText(df.format(statResult.getResultMoney()));


            // get LayoutParams from FrameLayout
            RelativeLayout.LayoutParams lpFrame = (RelativeLayout.LayoutParams) frame.getLayoutParams();
            int lpFrameLeftMargin = lpFrame.leftMargin;
            int lpFrameRightMargin = lpFrame.rightMargin;
            RecyclerView.LayoutParams lpBox = (RecyclerView.LayoutParams) box.getLayoutParams();
            int lpBoxLeftMargin = lpBox.leftMargin;
            int lpBoxRightMargin = lpBox.rightMargin;

            DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
            int displayWidth = metrics.widthPixels;
            int maxViewSize = displayWidth - (lpFrameLeftMargin + lpFrameRightMargin + lpBoxLeftMargin + lpBoxRightMargin);
            int valueGrey = statResult.getResultOpen();
            int valueRed = statResult.getResultNo();
            int valueGreen = statResult.getResultYes();
            int valueSum = valueGreen + valueRed + valueGrey;
            ViewGroup.LayoutParams lpGreen = barGreen.getLayoutParams();
            lpGreen.width = valueGreen * maxViewSize / valueSum;
            barGreen.setLayoutParams(lpGreen);
            barGreen.setText(String.valueOf(valueGreen));
            ViewGroup.LayoutParams lpRed = barRed.getLayoutParams();
            lpRed.width = valueRed * maxViewSize / valueSum;
            barRed.setLayoutParams(lpRed);
            barRed.setText(String.valueOf(valueRed));
            ViewGroup.LayoutParams lpGrey = barGrey.getLayoutParams();
            lpGrey.width = valueGrey * maxViewSize / valueSum;
            barGrey.setLayoutParams(lpGrey);
            barGrey.setText(String.valueOf(valueGrey));

        }
    }

    @Override
    public WeekViewHolder onCreateParentViewHolder(ViewGroup parentViewGroup) {
        View weekView = inflater.inflate(R.layout.list_item_week_parent, parentViewGroup, false);
        return new WeekViewHolder(weekView);
    }

    @Override
    public DayViewHolder onCreateChildViewHolder(ViewGroup childViewGroup) {
        View daySummaryView = inflater.inflate(R.layout.list_item_day_child, childViewGroup, false);
        return new DayViewHolder(daySummaryView);
    }

    @Override
    public void onBindParentViewHolder(WeekViewHolder weekViewHolder, int position, ParentListItem parentListItem) {
        Week week = (Week) parentListItem;
        weekViewHolder.bind(week);
    }

    @Override
    public void onBindChildViewHolder(DayViewHolder dayViewHolder, int position, Object childListItem) {
        final StatResult statResult = (StatResult) childListItem;
        dayViewHolder.bind(statResult);
        dayViewHolder.box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = statResult.getDate();
                // sent intent
                Intent intent = new Intent(mContext, DataSetActivity.class);
                intent.putExtra(DataSetActivity.DAY_STRING, date);
                mContext.startActivity(intent);
            }
        });
    }

}

