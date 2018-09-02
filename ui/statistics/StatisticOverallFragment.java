package com.benjaminsommer.dailygoals.ui.statistics;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.benjaminsommer.dailygoals.Database;
import com.benjaminsommer.dailygoals.R;
import com.benjaminsommer.dailygoals.entities.SummarizedDataSet;
import com.benjaminsommer.dailygoals.entities.Goal;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by DEU209213 on 25.12.2016.
 */
public class StatisticOverallFragment extends Fragment {

    private Database db;
    private LinearLayout linearLayout;
    private Button button;
    private int diagramNumber = 0;
    private final CharSequence[] chartHeadline = {"Übersicht Erfolgsquote", "Übersicht nach Zielen"};
    private ArrayList<Goal> goalList;

    public StatisticOverallFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statistic_overall, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // initialize database
        db = new Database(this.getActivity());

        // initialize layout components
        linearLayout = (LinearLayout) getView().findViewById(R.id.statisticFragmentOverall_linearLayout);
        button = (Button) getView().findViewById(R.id.statisticFragmentOverall_selectionButton);

        // get goals and create front chart
        goalList = db.getAllGoalsInDataSetTable();

        // delete all existing charts
        // create chart
        generatePieChart();

        // set button text
        button.setText(chartHeadline[diagramNumber]);

        // set OnClickListener for button
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectionDialog(getActivity(), chartHeadline);
            }
        });

    }

    public void generateRadarOverallChart() {

        // fill diagram number
        diagramNumber = 1;

        // create Radar Chart programmatically
        RadarChart radarChart = new RadarChart(getActivity());
        linearLayout.addView(radarChart);
        radarChart.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // get data from Database and put it in the right format
        ArrayList<SummarizedDataSet> dataPerGoal = db.getResultsPerGoal();
        ArrayList<Goal> goalArrayList = new ArrayList<>();
        List<RadarEntry> radarEntryList = new ArrayList<>();
        final String[] labels = new String[dataPerGoal.size()];
        for (int x = 0; x < dataPerGoal.size(); x++) {
//            int sum = dataPerGoal.get(x).getGoalValue();
//            // goal calculation
//            float intYes = sum / 100;
//            float intNo = (sum % 100) / 10;
//            float intUnknown = (sum % 100) % 10;
            float floatYes = dataPerGoal.get(x).getValuesYes();
            float floatNo = dataPerGoal.get(x).getValuesNo();
            float floatOpen = dataPerGoal.get(x).getValuesOpen();
            float percentage = floatYes / (floatYes + floatNo + floatOpen);
            radarEntryList.add(new RadarEntry(percentage));
            Goal goal = db.getGoalByID(dataPerGoal.get(x).getGoalID());
            goalArrayList.add(goal);
            labels[x] = "Goal " + String.valueOf(goalArrayList.get(x).getGoalId());
        }

        // filling dataset
        RadarDataSet set = new RadarDataSet(radarEntryList, "");
        set.setColor(Color.rgb(0, 153, 0));
        set.setFillColor(Color.rgb(0, 153, 0));
        set.setDrawFilled(true);
        set.setFillAlpha(180);
        set.setLineWidth(2f);
        set.setDrawHighlightCircleEnabled(true);
        set.setDrawHighlightIndicators(false);
        set.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                DecimalFormat df = new DecimalFormat(",#0.0 \u0025");
                return df.format(value);
            }
        });

        // filling radar set
        RadarData data = new RadarData(set);
        data.setValueTextSize(8f);
        data.setDrawValues(false);
        data.setValueTextColor(Color.WHITE);

        // filling chart
        radarChart.setBackgroundColor(Color.argb(0, 255, 255, 255));
        radarChart.setData(data);
        radarChart.getDescription().setEnabled(false);

        // X-Axis
        XAxis xAxis = radarChart.getXAxis();
        xAxis.setTextSize(9f);
        xAxis.setYOffset(0f);
        xAxis.setXOffset(0f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if (value < labels.length) {
                    return  labels[(int) value];
                } else {
                    return String.valueOf(value);
                }
            }
        });

        // Y-Axis
        YAxis yAxis = radarChart.getYAxis();
        // yAxis.setLabelCount(5, false);
        yAxis.setTextSize(9f);
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(1f);
        yAxis.setGranularity(0.2f);
        yAxis.setDrawLabels(false);

        // Legend
        Legend l = radarChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(2f);
        LegendEntry[] legendEntry = new LegendEntry[dataPerGoal.size()];
        for (int y = 0; y < dataPerGoal.size(); y++) {
            int goalID = dataPerGoal.get(y).getGoalID();
            Goal goal = db.getGoalByID(goalID);
            String goalName = goal.getGoalName();
            LegendEntry lEntry = new LegendEntry();
            lEntry.label = "Goal " + goalID + ": " + goalName;
            lEntry.formColor = Color.rgb(0, 153, 0);
            lEntry.form = Legend.LegendForm.LINE;
            legendEntry[y] = lEntry;
        }
        l.resetCustom();
        l.setExtra(legendEntry);


        radarChart.invalidate();

    }

    public void generatePieChart() {

        // fill diagram number
        diagramNumber = 0;

        // create Pie Chart programmatically
        PieChart pieChart = new PieChart(getActivity());
        linearLayout.addView(pieChart);
        pieChart.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // get data from Database and put it in the right format
        SummarizedDataSet dataSet = db.getOverallResults();
        // goal calculation
//        float intYes = results / 100;
//        float intNo = (results % 100) / 10;
//        float intUnknown = (results % 100) % 10;
        float floatYes = dataSet.getValuesYes();
        float floatNo = dataSet.getValuesNo();
        float floatOpen = dataSet.getValuesOpen();
        float percentage = floatYes / (floatYes + floatNo + floatOpen);
        float[] floatArray = {floatYes, floatNo, floatOpen};
        ArrayList<PieEntry> pieEntry = new ArrayList<>();
        for (int x = 0; x < floatArray.length; x++) {
            pieEntry.add(new PieEntry(floatArray[x]));
        }

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

        // set pie dataset
        PieDataSet set = new PieDataSet(pieEntry, "");
        set.setSliceSpace(3f);
        set.setSelectionShift(5f);
        set.setValueFormatter(valueFormatter);
        set.setValueTextSize(16f);
        set.setValueTextColor(Color.argb(200, 255, 255, 255));
        set.setColors(new int[]{R.color.cat_Green_full, R.color.colorRed, R.color.materialGrey500}, getActivity());

        // set pie data
        PieData data = new PieData(set);

        // configure Pie Chart
        pieChart.setData(data);
        pieChart.setUsePercentValues(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setHighlightPerTapEnabled(false);
        pieChart.setDrawCenterText(true);
        pieChart.setCenterText(generateCenterSpannableText(percentage));

        // legend
        Legend legend = pieChart.getLegend();
        legend.setEnabled(true);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setXEntrySpace(16f);
        LegendEntry[] legendList = legend.getEntries();
        legendList[0].label = "Erreicht";
        legendList[1].label = "Nicht erreicht";
        legendList[2].label = "Noch nicht ausgewählt";
        legend.setCustom(legendList);

    }

    public void showSelectionDialog(Context context, CharSequence[] items) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Wähle eine Statistik aus");
        builder.setCancelable(true);
        builder.setSingleChoiceItems(items, diagramNumber, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                changeChart(which);
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public void changeChart(int newChartID) {

        // find current chart in layout
        int viewCount = linearLayout.getChildCount();
        if (viewCount > 1) {
            linearLayout.removeViewAt(viewCount - 1);
        }

        // start method to build new chart
        if (newChartID == 0) {
            generatePieChart();
            button.setText(chartHeadline[0]);
        } else if (newChartID == 1) {
            generateRadarOverallChart();
            button.setText(chartHeadline[1]);
        }

    }

    private SpannableString generateCenterSpannableText(float percentage) {

        DecimalFormat df = new DecimalFormat("##0.0 \u0025");

        SpannableString s = new SpannableString("Erreichte Ziele:\n" + df.format(percentage));
        s.setSpan(new RelativeSizeSpan(1.2f), 0, 16, 0);
        s.setSpan(new RelativeSizeSpan(3.0f), 16, s.length(), 0);
        return s;
    }

}
