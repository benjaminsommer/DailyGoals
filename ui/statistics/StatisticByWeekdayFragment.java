package com.benjaminsommer.dailygoals.ui.statistics;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.benjaminsommer.dailygoals.Database;
import com.benjaminsommer.dailygoals.R;
import com.benjaminsommer.dailygoals.entities.SummarizedDataSet;
import com.benjaminsommer.dailygoals.entities.Goal;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DEU209213 on 26.12.2016.
 */
public class StatisticByWeekdayFragment extends Fragment {

    private Database db;
    private LinearLayout linearLayout;
    private Button buttonChart, buttonTime;
    private int diagramNumber = 0;
    private int goalNumber = 0;
    private ArrayList<Goal> goalList;
    private CharSequence[] chartHeadline;
    private String[] dayShortcut = {"Mo.", "Di.", "Mi.", "Do.", "Fr.", "Sa.", "So."};

    public StatisticByWeekdayFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statistic_by_weekday, container, false);    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // initialize database
        db = new Database(this.getActivity());

        // initialize layout components
        linearLayout = (LinearLayout) getView().findViewById(R.id.statisticFragmentByWeekday_linearLayout);
        buttonChart = (Button) getView().findViewById(R.id.statisticFragmentByWeekday_buttonChart);

        // get goals and create front chart
        goalList = db.getAllGoalsInDataSetTable();
        if (diagramNumber == 0) {
            goalNumber = 0;
        } else {
            goalNumber = goalList.get(diagramNumber - 1).getGoalId();
        }

        // set button text
        chartHeadline = new CharSequence[goalList.size() + 1];
        chartHeadline[0] = "Alle Ziele";
        for (int x = 0; x < goalList.size(); x++) {
            chartHeadline[x + 1] = "Goal " + String.valueOf(goalList.get(x).getGoalId()) + ": " + goalList.get(x).getGoalName();
        }
        buttonChart.setText(chartHeadline[diagramNumber]);

        // button OnClick listeners
        buttonChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectionDialog(getActivity(), diagramNumber, chartHeadline);
            }
        });

        // generate first chart
        generateStackedBarChart(goalNumber);

    }

    private void generateStackedBarChart(int goalID) {

        // create Radar Chart programmatically
        BarChart chart = new BarChart(getActivity());
        linearLayout.addView(chart);
        chart.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // get data for chart
        List<BarEntry> stackedEntryGroup = new ArrayList<>();

        ArrayList<SummarizedDataSet> dateValues = db.getSummarizedDataSetByTimeframe(goalID, 4); // 4 stands for weekday db query!
        // modification of dateValues --> bringing Sunday to the end
        if (dateValues.get(0).getDate().equals("0")) {
//            SummarizedDataSet dataSet = dateValues.get(0);
//            dataSet.setDate("7");
//            dateValues.add(dataSet);
//            dateValues.remove(0);
            dateValues.get(0).setDate("7");
        }

        for (int x = 0; x < dateValues.size(); x++) {
//            int sum = dateValues.get(x).getGoalValue();
//            // goal calculation
//            float intYes = sum / 100;
//            float intNo = (sum % 100) / 10;
//            float intUnknown = (sum % 100) % 10;
            float floatYes = dateValues.get(x).getValuesYes();
            float floatNo = dateValues.get(x).getValuesNo();
            float floatOpen = dateValues.get(x).getValuesOpen();
            stackedEntryGroup.add(new BarEntry(Integer.valueOf(dateValues.get(x).getDate()), new float[] {floatYes, floatNo, floatOpen}));
        }

        BarDataSet set = new BarDataSet(stackedEntryGroup, "");
        set.setColors(new int[]{R.color.cat_Green_full, R.color.colorRed, R.color.materialGrey500}, getActivity());

        // set value labels
        IValueFormatter valueFormatter = new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                if (value == 0f) {
                    return "";
                } else {
                    return String.valueOf((int) value);
                }
            }
        };

        set.setValueFormatter(valueFormatter);
        set.setBarBorderColor(Color.rgb(255, 255, 255));
        set.setBarBorderWidth(0.25f);
        BarData data = new BarData(set);
        data.setValueTextColor(Color.argb(200, 255, 255, 255));
        data.setValueTextSize(10f);
        data.setBarWidth(0.8f);

        // set axis labels
        IAxisValueFormatter formatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return dayShortcut[(int) value - 1];
            }
        };



        // X-Axis
        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1f);
        // xAxis.setTextSize(16f);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(formatter);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        // Y-Axis
        YAxis yAxisLeft = chart.getAxisLeft();
        yAxisLeft.setDrawGridLines(false);
        yAxisLeft.setEnabled(true);
        yAxisLeft.setGranularity(1f);
        yAxisLeft.setAxisMinimum(0f);
        YAxis yAxisRight = chart.getAxisRight();
        yAxisRight.setDrawGridLines(false);
        yAxisRight.setEnabled(false);

        // chart
        chart.setData(data);
        chart.setDrawGridBackground(false);
        chart.setDrawValueAboveBar(true);
        chart.setFitBars(false);
        chart.setDrawValueAboveBar(false);
        chart.setVisibleXRangeMinimum(7);
        chart.setHighlightPerDragEnabled(false);
        chart.setHighlightPerTapEnabled(false);
        chart.setScaleEnabled(false);
        chart.getDescription().setEnabled(false);

        // legend
        Legend legend = chart.getLegend();
        legend.setEnabled(true);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setXEntrySpace(16f);
        LegendEntry[] legendList = legend.getEntries();
        legendList[0].label = "Erreicht";
        legendList[1].label = "Nicht erreicht";
        legendList[2].label = "Noch nicht ausgewählt";
        legend.setCustom(legendList);

        // chart.setFitBars(true); // makes the x-axis fit exactly all bars
        chart.invalidate();


    }

    public void showSelectionDialog(Context context, int preSelectValue, CharSequence[] items) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Wähle eine Statistik aus");
        builder.setCancelable(true);
        builder.setSingleChoiceItems(items, preSelectValue, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                changeChart(which);
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public void changeChart(int which) {

        // find current chart in layout
        int viewCount = linearLayout.getChildCount();
        if (viewCount > 1) {
            linearLayout.removeViewAt(viewCount - 1);
        }

        diagramNumber = which;
        if (diagramNumber == 0) {
            goalNumber = 0;
        } else {
            goalNumber = goalList.get(diagramNumber - 1).getGoalId();
        }

        buttonChart.setText(chartHeadline[diagramNumber]);
        generateStackedBarChart(goalNumber);

    }

}
