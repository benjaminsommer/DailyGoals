package com.benjaminsommer.dailygoals.ui.goals;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.benjaminsommer.dailygoals.util.ItemTouchHelperAdapter;
import com.benjaminsommer.dailygoals.util.OnStartDragListener;
import com.benjaminsommer.dailygoals.R;
import com.benjaminsommer.dailygoals.entities.Category;
import com.benjaminsommer.dailygoals.entities.Goal;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Created by DEU209213 on 06.09.2016.
 */
public class GoalCardAdapter extends RecyclerView.Adapter<GoalCardAdapter.MyViewHolder> implements ItemTouchHelperAdapter {

    private static final String TAG = GoalCardAdapter.class.getSimpleName();

    private Context mContext;
    private List<Goal> goalList;

    private ClickActivityInformation clickActivityInformation;

    private final OnStartDragListener onStartDragListener;

    public interface ClickActivityInformation {
        void onHandleSelection(int position);
        void onGoalDeletion(int position);
        void onRadioButtonChange (int position, int newFrequency);
        void onFrequencySelection(int position);
        void onColorSelection(int position, int formerColor);
        void onRewardSelection(int position, boolean isChecked);
        void onRewardAmountSelection(int position, double amount);
        void onTextEditSelection(int position, String previousText, int isNameOrDescription);
        void onExpandSection(int position, boolean isExpanded);
        void onGoalListFirstLoad();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout placeholderColExp, rlHeader;
        private TextView txtRewardStd, txtRewardInd, txtFreqDaily, txtFreqWeekly, txtFreqMonthly, goalName, goalDescription;
        private Switch switchReward;
        private ImageView buttonDelete, btnDragDrop;
        private ImageButton frequencySelection, buttonColExp;
        private Button btnCategory, btnColor, btnReward;
        private SeekBar seekbar;

        public MyViewHolder (View view) {
            super(view);
            placeholderColExp = (RelativeLayout) view.findViewById(R.id.goaldCardView_placeholder_collapseExpand);
            rlHeader = (RelativeLayout) view.findViewById(R.id.goalCardView_header);
            goalName = (TextView) view.findViewById(R.id.goalCardView_textView_goalName);
            btnDragDrop = (ImageView) view.findViewById(R.id.goalCardView_dragAndDrop);
            goalDescription = (TextView) view.findViewById(R.id.goalCardView_textView_goalDescription);
            buttonColExp = (ImageButton) view.findViewById(R.id.goalCardView_button_collapseExpand);
            buttonDelete = (ImageView) view.findViewById(R.id.goalCardview_button_delete);
            btnReward = (Button) view.findViewById(R.id.goalCardView_button_goalReward);
            btnCategory = (Button) view.findViewById(R.id.goalCardView_button_category);
            btnColor = (Button) view.findViewById(R.id.goalCardView_button_color);
            txtRewardStd = (TextView) view.findViewById(R.id.goalCardView_label_goalReward_switch_standard);
            txtRewardInd = (TextView) view.findViewById(R.id.goalCardView_label_goalReward_switch_individual);
            switchReward = (Switch) view.findViewById(R.id.goalCardView_switch_goalReward);
            seekbar = (SeekBar) view.findViewById(R.id.goalCardView_seekBar_frequency);
            txtFreqDaily = (TextView) view.findViewById(R.id.goalCardView_text_seekBar_daily);
            txtFreqWeekly = (TextView) view.findViewById(R.id.goalCardView_text_seekBar_weekly);
            txtFreqMonthly = (TextView) view.findViewById(R.id.goalCardView_text_seekBar_monthly);
            frequencySelection = (ImageButton) view.findViewById(R.id.goalCardView_button_frequencySelection);
        }
    }

    public GoalCardAdapter(Context mContext, ClickActivityInformation clickActivityInformation, OnStartDragListener onStartDragListener) {
        this.mContext = mContext;
        this.goalList = new ArrayList<>();
        this.clickActivityInformation = clickActivityInformation;
        this.onStartDragListener = onStartDragListener;
    }

    public GoalCardAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.goal_card, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final GoalCardAdapter.MyViewHolder holder, final int position) {

        // get goal information and set detailed text to gone for the beginning
        final Goal goal = goalList.get(position);

        // set goal to collapsed in the beginning
        if (goal.isGoalExpanded()) {
            holder.placeholderColExp.setVisibility(View.VISIBLE);
            holder.buttonColExp.setImageResource(R.drawable.icon_collapse_24dp);
        } else {
            holder.placeholderColExp.setVisibility(View.GONE);
            holder.buttonColExp.setImageResource(R.drawable.icon_expand_24dp);
        }

        // get Category image
        Category category = getSelectedCategory(mContext, goal.getGoalCategory());
        TypedArray imgs = mContext.getResources().obtainTypedArray(R.array.category_icon_int);
        int imgID = imgs.getResourceId(goal.getGoalCategory() - 1, 0);


        // check for goal color and set it to header and detail section
        holder.rlHeader.setBackgroundColor(goal.getGoalColor());
        //holder.headerCatImage.setImageDrawable(mContext.getResources().getDrawable(imgID, null));
        //holder.headerCatImage.setColorFilter(mContext.getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);

        // set Category button
        Drawable drawable = mContext.getResources().getDrawable(imgID);
        drawable.setColorFilter(goal.getGoalColor(), PorterDuff.Mode.SRC_ATOP);
        drawable.setBounds( 0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        holder.btnCategory.setCompoundDrawables(drawable, null, null, null);
        Drawable[] drawablesPrevCat = holder.btnCategory.getCompoundDrawables();
        drawablesPrevCat[0].setColorFilter(goal.getGoalColor(), PorterDuff.Mode.SRC_ATOP);
        holder.btnCategory.setText(category.getName());

        // set Color button
        Drawable[] drawablesPrev = holder.btnColor.getCompoundDrawables();
        drawablesPrev[0].setColorFilter(goal.getGoalColor(), PorterDuff.Mode.SRC_IN);

        // install drag and drop on drag&drop symbol
        holder.btnDragDrop.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    onStartDragListener.onStartDrag(holder);
                }
                return false;
            }
        });

        // set goal name and goal description and add TextWatcher
        holder.goalName.setText(goalList.get(holder.getAdapterPosition()).getGoalName());
        holder.goalDescription.setText(goalList.get(holder.getAdapterPosition()).getGoalDescription());

        // check if selected color is dark or light and change header textView
        if (!isColorDark(goal.getGoalColor())) {
            holder.goalName.setTextColor(mContext.getResources().getColor(R.color.colorBlack));
            //holder.headerCatImage.setColorFilter(mContext.getResources().getColor(R.color.colorBlack));
        } else {
            holder.goalName.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
            //holder.headerCatImage.setColorFilter(mContext.getResources().getColor(R.color.colorWhite));
        }

        // set Reward section
        DecimalFormat decimalFormat = new DecimalFormat(",##0.00 \u00A4");
        holder.btnReward.setText(decimalFormat.format(goal.getGoalReward()));
        designRewardSection(holder, goal.getGoalRewardType());

        // set frequency and preselect radio buttons
        holder.seekbar.setProgress(goal.getGoalFrequency().get(0));
        changeSeekbarLabels(goal.getGoalFrequency().get(0), holder.txtFreqDaily, holder.txtFreqWeekly, holder.txtFreqMonthly, holder.frequencySelection);

        // Click Listener to collapse or expand goal
        holder.buttonColExp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.placeholderColExp.getVisibility() == View.GONE) {
                    holder.placeholderColExp.setVisibility(View.VISIBLE);
                    holder.buttonColExp.setImageResource(R.drawable.icon_collapse_24dp);
                    clickActivityInformation.onExpandSection(holder.getAdapterPosition(), true);
                } else {
                    holder.placeholderColExp.setVisibility(View.GONE);
                    holder.buttonColExp.setImageResource(R.drawable.icon_expand_24dp);
                    clickActivityInformation.onExpandSection(holder.getAdapterPosition(), false);
                }
            }
        });

        // Click Listener to show Text Input Dialog
        View.OnClickListener textViewClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int viewType;
                if (v.getId() == holder.goalName.getId()) {
                    viewType = 0;
                    clickActivityInformation.onTextEditSelection(holder.getAdapterPosition(), goalList.get(holder.getAdapterPosition()).getGoalName(), 0);
                } else {
                    viewType = 1;
                    clickActivityInformation.onTextEditSelection(holder.getAdapterPosition(), goalList.get(holder.getAdapterPosition()).getGoalDescription(), 1);
                }
            }
        };
        holder.goalName.setOnClickListener(textViewClick);
        holder.goalDescription.setOnClickListener(textViewClick);

        // Click Listener to delete goal
        holder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                // Set title
                builder.setTitle("Willst Du das Ziel wirklich löschen?");
                // Add the buttons
                builder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (clickActivityInformation != null) {
                            clickActivityInformation.onGoalDeletion(holder.getAdapterPosition());
                        }
                    }
                });
                builder.setNegativeButton("Nein", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                // Create the AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        holder.btnCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickActivityInformation != null) {
                    clickActivityInformation.onHandleSelection(holder.getAdapterPosition());
                }
            }
        });

        holder.btnColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickActivityInformation != null) {
                    clickActivityInformation.onColorSelection(holder.getAdapterPosition(), goal.getGoalColor());
                }
            }
        });

        holder.frequencySelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickActivityInformation != null) {
                    clickActivityInformation.onFrequencySelection(holder.getAdapterPosition());
                }
            }
        });

        holder.switchReward.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                designRewardSection(holder, isChecked);
                clickActivityInformation.onRewardSelection(holder.getAdapterPosition(), isChecked);
            }
        });

        holder.btnReward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickActivityInformation.onRewardAmountSelection(holder.getAdapterPosition(), goal.getGoalReward());
            }
        });

        holder.seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    clickActivityInformation.onRadioButtonChange(holder.getAdapterPosition(), progress);
                    changeSeekbarLabels(progress, holder.txtFreqDaily, holder.txtFreqWeekly, holder.txtFreqMonthly, holder.frequencySelection);
                    if (progress == 0) {
                        holder.frequencySelection.setVisibility(View.INVISIBLE);
                    } else {
                        holder.frequencySelection.setVisibility(View.VISIBLE);
                    }

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // recycle image array
        imgs.recycle();

    }

    // method to set list to adapter
    public void setGoalList(final List<Goal> liveList) {
        if (this.goalList.size() == 0) {
//            for (int x = 0; x < liveList.size(); x++) {
//                liveList.get(x).setExpanded(false);
//            }
//            clickActivityInformation.onGoalListFirstLoad();
            this.goalList = liveList;
            notifyItemRangeInserted(0, liveList.size());
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return goalList.size();
                }

                @Override
                public int getNewListSize() {
                    return liveList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return goalList.get(oldItemPosition).getGoalId() == liveList.get(newItemPosition).getGoalId();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Goal newGoal = liveList.get(newItemPosition);
                    Goal oldGoal = goalList.get(oldItemPosition);
                    return newGoal.getGoalId() == oldGoal.getGoalId()
                            && Objects.equals(newGoal.getGoalName(), oldGoal.getGoalName())
                            && Objects.equals(newGoal.getGoalDescription(), oldGoal.getGoalDescription())
                            && newGoal.getGoalCategory() == oldGoal.getGoalCategory()
                            && newGoal.getGoalColor() == oldGoal.getGoalColor()
                            && newGoal.getGoalRewardType() == oldGoal.getGoalRewardType()
                            && newGoal.getGoalReward() == oldGoal.getGoalReward()
                            && newGoal.getGoalFrequency() == oldGoal.getGoalFrequency();
                }
            });
            goalList = liveList;
            result.dispatchUpdatesTo(this);
            notifyDataSetChanged();
        }
    }

    // check if color is dark or light
    public boolean isColorDark(int color){
        double darkness = 1-(0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        if(darkness < 0.25){
            return false; // It's a light color
        }else{
            return true; // It's a dark color
        }
    }

    // text label change when seekbar is moved or firstly set
    private void changeSeekbarLabels(int progress, TextView txtFreqDaily, TextView txtFreqWeekly, TextView txtFreqMonthly, ImageButton frequencySelection) {

        switch (progress) {
            case 0:
                txtFreqDaily.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
                txtFreqWeekly.setTextColor(mContext.getResources().getColor(R.color.material_black_12pc));
                txtFreqMonthly.setTextColor(mContext.getResources().getColor(R.color.material_black_12pc));
                frequencySelection.setVisibility(View.INVISIBLE);
                break;
            case 1:
                txtFreqDaily.setTextColor(mContext.getResources().getColor(R.color.material_black_12pc));
                txtFreqWeekly.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
                txtFreqMonthly.setTextColor(mContext.getResources().getColor(R.color.material_black_12pc));
                frequencySelection.setVisibility(View.VISIBLE);
                break;
            case 2:
                txtFreqDaily.setTextColor(mContext.getResources().getColor(R.color.material_black_12pc));
                txtFreqWeekly.setTextColor(mContext.getResources().getColor(R.color.material_black_12pc));
                txtFreqMonthly.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
                frequencySelection.setVisibility(View.VISIBLE);
                break;
        }

    }

    // design reward section
    private void designRewardSection(MyViewHolder holder, boolean isChecked) {
        holder.switchReward.setChecked(isChecked);
        if (isChecked) {
            holder.txtRewardStd.setTextColor(mContext.getResources().getColor(R.color.disabledViewColor));
            holder.txtRewardInd.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
            holder.btnReward.setTextColor(mContext.getResources().getColor(R.color.material_black_87pc));
            Drawable[] drawablesPrev = holder.btnReward.getCompoundDrawables();
            drawablesPrev[2].setColorFilter(mContext.getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);
            holder.btnReward.setEnabled(true);
        } else {
            holder.txtRewardStd.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
            holder.txtRewardInd.setTextColor(mContext.getResources().getColor(R.color.disabledViewColor));
            holder.btnReward.setTextColor(mContext.getResources().getColor(R.color.disabledViewColor));
            Drawable[] drawablesPrev = holder.btnReward.getCompoundDrawables();
            drawablesPrev[2].setColorFilter(mContext.getResources().getColor(R.color.disabledViewColor), PorterDuff.Mode.SRC_IN);
            holder.btnReward.setEnabled(false);
        }
    }

//    private class EditTextListenerGoalName implements TextWatcher {
//        private int position;
//
//        public void updatePosition(int position) {
//            this.position = position;
//        }
//
//        @Override
//        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//        }
//
//        @Override
//        public void onTextChanged(CharSequence s, int start, int before, int count) {
//            //goalList.get(position).setGoalName(s.toString());
//            //Log.d(position + ":", s.toString());
//            clickActivityInformation.onTextStringChange(position, true, s.toString());
//        }
//
//        @Override
//        public void afterTextChanged(Editable s) {
//        }
//    }
//
//    private class EditTextListenerGoalDescription implements TextWatcher {
//        private int position;
//
//        public void updatePosition(int position) {
//            this.position = position;
//        }
//
//        @Override
//        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//        }
//
//        @Override
//        public void onTextChanged(CharSequence s, int start, int before, int count) {
//            //goalList.get(position).setGoalDescription(s.toString());
//            clickActivityInformation.onTextStringChange(position, false, s.toString());
//
//        }
//
//        @Override
//        public void afterTextChanged(Editable s) {
//        }
//    }


    @Override
    public int getItemCount() {
        return goalList.size();
    }

    //// drag & drop interface implementation
    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(goalList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    public Category getSelectedCategory(Context context, int position) {
        String[] categoryIDs, categoryNames;
        TypedArray imgs;
        Category category;

        ArrayList<Category> categoryList = new ArrayList<Category>();
        categoryIDs = context.getResources().getStringArray(R.array.category_identifier);
        categoryNames = context.getResources().getStringArray(R.array.category_name);
        imgs = context.getResources().obtainTypedArray(R.array.category_icon);
        for (int i = 0; i < categoryNames.length; i++) {
            categoryList.add(new Category(Integer.valueOf(categoryIDs[i]), categoryNames[i], imgs.getDrawable(i)));
        }

        if (position == 0) {
            category = categoryList.get(15 - 1);
        } else {
            category = categoryList.get(position - 1);
        }
        return category;
    }

}