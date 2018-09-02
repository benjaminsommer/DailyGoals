package com.benjaminsommer.dailygoals.ui.dataset;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.benjaminsommer.dailygoals.entities.CombinedDataSet;
import com.benjaminsommer.dailygoals.R;
import com.benjaminsommer.dailygoals.entities.Category;
import com.benjaminsommer.dailygoals.entities.DataSet;
import com.benjaminsommer.dailygoals.ui.goals.GoalsActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by DEU209213 on 04.11.2016.
 */
public class DataSetAdapter extends ListAdapter<CombinedDataSet, DataSetAdapter.MyViewHolder> {

    public static final String TAG = DataSetAdapter.class.getSimpleName();
    public static final String GOAL_EDIT_POSITION = "goal_edit_position";
    public static final int ACCENT_COLOR = R.color.colorAccent;
    public static final int TEXT_COLOR = R.color.material_black_87pc;

    private Context mContext;
    private ClickedButtonInterface clickedButtonInterface;

    public interface ClickedButtonInterface {
        void onDataSetChange(DataSet dataSet);
        void onRemoveButtonClick(DataSet dataSet);
        void onNoticeEditClick(int position, String previousText);
//        void onGoalButtonClick( int position, int newValue, int oldValue);
//        void onNodeSelection(DataSet dataSet);
    }

    public DataSetAdapter(Context mContext, ClickedButtonInterface clickedButtonInterface ) {
        super(DataSetAdapter.DIFF_CALLBACK);
        this.mContext = mContext;
        this.clickedButtonInterface = clickedButtonInterface;
    }

    public static final DiffUtil.ItemCallback<CombinedDataSet> DIFF_CALLBACK = new DiffUtil.ItemCallback<CombinedDataSet>() {
        @Override
        public boolean areItemsTheSame(CombinedDataSet oldItem, CombinedDataSet newItem) {
            return oldItem.getDataSet().getDataSetId() == newItem.getDataSet().getDataSetId();
        }

        @Override
        public boolean areContentsTheSame(CombinedDataSet oldItem, CombinedDataSet newItem) {
            return oldItem == newItem;
        }
    };

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout cardView, rlMainBoxInfo;
        public LinearLayout llColExp, llBoxInfo;
        public ImageView goalImage, ivSpec, ivNote, ivFreq, ivReward;
        public ImageButton buttonYes, buttonNo, btnNoteEdit;
        public FrameLayout colorShower;
        public TextView goalName, txtSpec, txtNote, txtFreq, txtReward, txtBoxInfo, txtBox1, txtBox2, txtBox3, txtBox4, txtBox5, txtBox6, txtBox7;
        public FloatingActionButton btnSpec, btnNote, btnFreq, btnReward;
        public Button btnEdit, btnReset, btnDelete;

        public MyViewHolder (View view) {
            super(view);
            cardView = (RelativeLayout) view.findViewById(R.id.cardDailyGoal_rl_card);
            rlMainBoxInfo = (RelativeLayout) view.findViewById(R.id.cardDailyGoal_box_information);
            llColExp = (LinearLayout) view.findViewById(R.id.cardDailyGoal_ll_colExp);
            llBoxInfo = (LinearLayout) view.findViewById(R.id.cardDailyGoal_box_ll_days);
            goalImage = (ImageView) view.findViewById(R.id.cardDailyGoal_imageView_goalImage);
            //buttonColExp = (ImageView) view.findViewById(R.id.cardDailyGoal_button_collapseExpand);
            buttonYes = (ImageButton) view.findViewById(R.id.cardDailyGoal_imageButton_buttonYes);
            buttonNo = (ImageButton) view.findViewById(R.id.cardDailyGoal_imageButton_buttonNo);
            colorShower = (FrameLayout) view.findViewById(R.id.cardDailyGoal_frameLayout_colorShower);
            goalName = (TextView) view.findViewById(R.id.cardDailyGoal_textView_goalName);
            txtSpec = (TextView) view.findViewById(R.id.cardDailyGoal_textView_spec);
            btnSpec = (FloatingActionButton) view.findViewById(R.id.cardDailyGoal_button_spec);
            ivSpec = (ImageView) view.findViewById(R.id.cardDailyGoal_imageView_spec);
            txtNote = (TextView) view.findViewById(R.id.cardDailyGoal_textView_note);
            btnNote = (FloatingActionButton) view.findViewById(R.id.cardDailyGoal_button_note);
            ivNote = (ImageView) view.findViewById(R.id.cardDailyGoal_imageView_note);
            txtFreq = (TextView) view.findViewById(R.id.cardDailyGoal_textView_freq);
            btnFreq = (FloatingActionButton) view.findViewById(R.id.cardDailyGoal_button_freq);
            ivFreq = (ImageView) view.findViewById(R.id.cardDailyGoal_imageView_freq);
            txtReward = (TextView) view.findViewById(R.id.cardDailyGoal_textView_reward);
            btnReward = (FloatingActionButton) view.findViewById(R.id.cardDailyGoal_button_reward);
            ivReward = (ImageView) view.findViewById(R.id.cardDailyGoal_imageView_reward);
            btnEdit = (Button) view.findViewById(R.id.cardDailyGoal_button_edit);
            btnReset = (Button) view.findViewById(R.id.cardDailyGoal_button_reset);
            btnDelete = (Button) view.findViewById(R.id.cardDailyGoal_button_delete);
            btnNoteEdit = (ImageButton) view.findViewById(R.id.cardDailyGoal_box_button_edit);
            txtBoxInfo = (TextView) view.findViewById(R.id.cardDailyGoal_box_main_info);
            txtBox1 = (TextView) view.findViewById(R.id.cardDailyGoal_box_days_1);
            txtBox2 = (TextView) view.findViewById(R.id.cardDailyGoal_box_days_2);
            txtBox3 = (TextView) view.findViewById(R.id.cardDailyGoal_box_days_3);
            txtBox4 = (TextView) view.findViewById(R.id.cardDailyGoal_box_days_4);
            txtBox5 = (TextView) view.findViewById(R.id.cardDailyGoal_box_days_5);
            txtBox6 = (TextView) view.findViewById(R.id.cardDailyGoal_box_days_6);
            txtBox7 = (TextView) view.findViewById(R.id.cardDailyGoal_box_days_7);
        }

        public void onBindViewHolder(final CombinedDataSet combinedDataSet, final int position) {

            final MyViewHolder holder = this;

            // visibility of lower section and click listener for card view
            llColExp.setVisibility(combinedDataSet.getDataSet().isDataSetExpanded() ? View.VISIBLE : View.GONE);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DataSet dataSet = getItem(getAdapterPosition()).getDataSet();
                    dataSet.setDataSetExpanded(!dataSet.isDataSetExpanded());
                    clickedButtonInterface.onDataSetChange(dataSet);
                    notifyItemChanged(getAdapterPosition());
                }
            });

            // insert goal name and description
            goalName.setText(combinedDataSet.getGoal().getGoalName());

            // insert goal icon
            Category tempCategory = getSelectedCategory(mContext, combinedDataSet.getGoal().getGoalCategory());
            goalImage.setImageDrawable(tempCategory.getIcon());

            // insert goal color
            final int tempColorCode = combinedDataSet.getGoal().getGoalColor();
            colorShower.setBackgroundColor(tempColorCode);
            goalImage.setColorFilter(tempColorCode);

            // put database status of goals on Yes/No-buttons
            int intDBStatus = combinedDataSet.getDataSet().getDataSetValue();
            switch (intDBStatus) {
                case 1:
                    buttonYes.setSelected(false);
                    buttonNo.setSelected(false);
                    buttonYes.setVisibility(View.VISIBLE);
                    buttonNo.setVisibility(View.VISIBLE);
                    cardView.setBackgroundResource(R.color.colorWhite);
                    setButtonEnableState(btnReset, false);
                    break;
                case 10:
                    buttonYes.setSelected(false);
                    buttonNo.setSelected(true);
                    buttonYes.setVisibility(View.GONE);
                    buttonNo.setVisibility(View.VISIBLE);
                    cardView.setBackgroundResource(R.color.materialRed50);
                    setButtonEnableState(btnReset, true);
                    break;
                case 100:
                    buttonYes.setSelected(true);
                    buttonNo.setSelected(false);
                    buttonYes.setVisibility(View.VISIBLE);
                    buttonNo.setVisibility(View.GONE);
                    cardView.setBackgroundResource(R.color.cat_Green_Light_100);
                    setButtonEnableState(btnReset, true);
                    break;
            }

            // select which note should be selected
            if (combinedDataSet.getDataSet().getDataSetSelectedNode() > 0 && combinedDataSet.getDataSet().getDataSetSelectedNode() < 5) {
                visualizeButtons(holder, combinedDataSet, combinedDataSet.getDataSet().getDataSetSelectedNode(), tempColorCode);
            }

            // set Click Events for Yes/No buttons
            buttonYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int goalValue = getItem(getAdapterPosition()).getDataSet().getDataSetValue();
                    DataSet dataSet = getItem(getAdapterPosition()).getDataSet();
                    if (goalValue == 1) {
                        dataSet.setDataSetValue(100);
                        clickedButtonInterface.onDataSetChange(dataSet);
                    } else if (goalValue == 100) {
                        dataSet.setDataSetValue(1);
                        clickedButtonInterface.onDataSetChange(dataSet);
                    }
                    notifyItemChanged(getAdapterPosition());
                }
            });
            buttonNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int goalValue = getItem(getAdapterPosition()).getDataSet().getDataSetValue();
                    DataSet dataSet = getItem(getAdapterPosition()).getDataSet();
                    if (goalValue == 1) {
                        dataSet.setDataSetValue(10);
                        clickedButtonInterface.onDataSetChange(dataSet);
                    } else if (goalValue == 10) {
                        dataSet.setDataSetValue(1);
                        clickedButtonInterface.onDataSetChange(dataSet);
                    }
                    notifyItemChanged(getAdapterPosition());
                }
            });
            btnReset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int goalValue = getItem(getAdapterPosition()).getDataSet().getDataSetValue();
                    if (goalValue != 1) {
                        DataSet dataSet = getItem(getAdapterPosition()).getDataSet();
                        dataSet.setDataSetValue(1);
                        clickedButtonInterface.onDataSetChange(dataSet);
                        notifyItemChanged(getAdapterPosition());
                    }
                }
            });
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    // Set title
                    builder.setTitle("Willst Du das Ziel für heute löschen?");
                    builder.setCancelable(true);
                    // Add the buttons
                    builder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (clickedButtonInterface != null) {
                                clickedButtonInterface.onRemoveButtonClick(getItem(getAdapterPosition()).getDataSet());
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
            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent iGoalEdit = new Intent(mContext, GoalsActivity.class);
                    iGoalEdit.putExtra(GOAL_EDIT_POSITION, combinedDataSet.getGoal().getGoalId());
                    mContext.startActivity(iGoalEdit);
                }
            });

            btnNoteEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickedButtonInterface.onNoticeEditClick(getAdapterPosition(), getItem(getAdapterPosition()).getDataSet().getDataSetNotice());
                }
            });

            View.OnClickListener onButtonClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DataSet dataSet = getItem(getAdapterPosition()).getDataSet();
                    if (v.getId() == btnSpec.getId()) {
                        //visualizeButtons(holder, combinedDataSet, 1, tempColorCode);
                        dataSet.setDataSetSelectedNode(1);
                    } else if (v.getId() == btnNote.getId()) {
                        dataSet.setDataSetSelectedNode(2);
                        //visualizeButtons(holder, combinedDataSet, 2, tempColorCode);
                    } else if (v.getId() == btnFreq.getId()) {
                        dataSet.setDataSetSelectedNode(3);
                        //visualizeButtons(holder, combinedDataSet, 3, tempColorCode);
                    } else if (v.getId() == btnReward.getId()) {
                        dataSet.setDataSetSelectedNode(4);
                        //visualizeButtons(holder, combinedDataSet, 4, tempColorCode);
                    }
                    clickedButtonInterface.onDataSetChange(dataSet);
                    //notifyItemChanged(getAdapterPosition());
                }
            };
            btnSpec.setOnClickListener(onButtonClickListener);
            btnNote.setOnClickListener(onButtonClickListener);
            btnFreq.setOnClickListener(onButtonClickListener);
            btnReward.setOnClickListener(onButtonClickListener);

        }

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dataset, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // get DataSet information
        CombinedDataSet combinedDataSet = getItem(position);

        holder.onBindViewHolder(combinedDataSet, position);

    }

//    // method to change goal buttons to YES
//    public void buttonToYes(ImageButton ibYes, ImageButton ibNo) {
//        ibYes.setColorFilter(Color.argb(255, 0, 153, 0));
//        ibYes.setVisibility(View.VISIBLE);
//        ibNo.setVisibility(View.GONE);
//    }
//
//    // method to change goal buttons to NO
//    public void buttonToNo (ImageButton ibYes, ImageButton ibNo) {
//        ibNo.setColorFilter(Color.argb(255, 204, 0, 0));
//        ibYes.setVisibility(View.GONE);
//        ibNo.setVisibility(View.VISIBLE);
//    }
//
//    // method to change goal buttons to NEUTRAL
//    public void buttonToNew (ImageButton ibYes, ImageButton ibNo) {
//        ibYes.setColorFilter(Color.argb(255, 127, 127, 127));
//        ibNo.setColorFilter(Color.argb(255, 127, 127, 127));
//        ibYes.setVisibility(View.VISIBLE);
//        ibNo.setVisibility(View.VISIBLE);
//    }

    // method to color buttons based on which button is pushed
    private void visualizeButtons(DataSetAdapter.MyViewHolder holder, CombinedDataSet combinedDataSet, int pushedButton, int userColor) {

        switch (pushedButton) {
            case 1:
                holder.txtSpec.setTextColor(mContext.getResources().getColor(ACCENT_COLOR));
                holder.btnSpec.setBackgroundTintList(ColorStateList.valueOf(mContext.getResources().getColor(ACCENT_COLOR)));
                holder.ivSpec.setVisibility(View.VISIBLE);
                holder.txtNote.setTextColor(userColor);
                holder.btnNote.setBackgroundTintList(ColorStateList.valueOf(userColor));
                holder.ivNote.setVisibility(View.INVISIBLE);
                holder.txtFreq.setTextColor(userColor);
                holder.btnFreq.setBackgroundTintList(ColorStateList.valueOf(userColor));
                holder.ivFreq.setVisibility(View.INVISIBLE);
                holder.txtReward.setTextColor(userColor);
                holder.btnReward.setBackgroundTintList(ColorStateList.valueOf(userColor));
                holder.ivReward.setVisibility(View.INVISIBLE);
                if (!combinedDataSet.getGoal().getGoalDescription().equals("")) {
                    holder.txtBoxInfo.setText(combinedDataSet.getGoal().getGoalDescription());
                } else
                    holder.txtBoxInfo.setText("Goal has no description yet!");
                holder.llBoxInfo.setVisibility(View.GONE);
                holder.btnNoteEdit.setVisibility(View.GONE);
                break;
            case 2:
                holder.txtSpec.setTextColor(userColor);
                holder.btnSpec.setBackgroundTintList(ColorStateList.valueOf(userColor));
                holder.ivSpec.setVisibility(View.INVISIBLE);
                holder.txtNote.setTextColor(mContext.getResources().getColor(ACCENT_COLOR));
                holder.btnNote.setBackgroundTintList(ColorStateList.valueOf(mContext.getResources().getColor(ACCENT_COLOR)));
                holder.ivNote.setVisibility(View.VISIBLE);
                holder.txtFreq.setTextColor(userColor);
                holder.btnFreq.setBackgroundTintList(ColorStateList.valueOf(userColor));
                holder.ivFreq.setVisibility(View.INVISIBLE);
                holder.txtReward.setTextColor(userColor);
                holder.btnReward.setBackgroundTintList(ColorStateList.valueOf(userColor));
                holder.ivReward.setVisibility(View.INVISIBLE);
                holder.txtBoxInfo.setText(combinedDataSet.getDataSet().getDataSetNotice());
                holder.llBoxInfo.setVisibility(View.GONE);
                holder.btnNoteEdit.setVisibility(View.VISIBLE);
                break;
            case 3:
                holder.txtSpec.setTextColor(userColor);
                holder.btnSpec.setBackgroundTintList(ColorStateList.valueOf(userColor));
                holder.ivSpec.setVisibility(View.INVISIBLE);
                holder.txtNote.setTextColor(userColor);
                holder.btnNote.setBackgroundTintList(ColorStateList.valueOf(userColor));
                holder.ivNote.setVisibility(View.INVISIBLE);
                holder.txtFreq.setTextColor(mContext.getResources().getColor(ACCENT_COLOR));
                holder.btnFreq.setBackgroundTintList(ColorStateList.valueOf(mContext.getResources().getColor(ACCENT_COLOR)));
                holder.ivFreq.setVisibility(View.VISIBLE);
                holder.txtReward.setTextColor(userColor);
                holder.btnReward.setBackgroundTintList(ColorStateList.valueOf(userColor));
                holder.ivReward.setVisibility(View.INVISIBLE);
                holder.btnNoteEdit.setVisibility(View.GONE);
                String[] strFrequency = {"Täglich", "Wöchentlich", "Monatlich"};
                int intFrequency = combinedDataSet.getGoal().getGoalFrequency().get(0);
                List<Integer> freqList = combinedDataSet.getGoal().getGoalFrequency();
                holder.txtBoxInfo.setText(strFrequency[intFrequency]);
                if (intFrequency == 0) {
                    holder.llBoxInfo.setVisibility(View.GONE);
                } else if (intFrequency == 1) {
                    holder.llBoxInfo.setVisibility(View.VISIBLE);
                    holder.txtBox2.setVisibility(View.VISIBLE);
                    holder.txtBox3.setVisibility(View.VISIBLE);
                    holder.txtBox5.setVisibility(View.VISIBLE);
                    holder.txtBox6.setVisibility(View.VISIBLE);
                    holder.txtBox1.setText("Mo.");
                    holder.txtBox4.setText("Do.");
                    holder.txtBox7.setText("So.");
                    TextView[] textViews = {holder.txtBox1, holder.txtBox2, holder.txtBox3, holder.txtBox4, holder.txtBox5, holder.txtBox6, holder.txtBox7};
                    for (int x = 0; x < textViews.length; x++) {
                        colorDateTextView(textViews[x], freqList.get(x + 1));
                    }
                } else if (intFrequency == 2) {
                    holder.llBoxInfo.setVisibility(View.VISIBLE);
                    holder.txtBox7.setText("Monatsende");
                    colorDateTextView(holder.txtBox7, freqList.get(freqList.get(10)));
                    holder.txtBox4.setText("Monatsmitte");
                    colorDateTextView(holder.txtBox4, freqList.get(freqList.get(9)));
                    holder.txtBox1.setText("Monatsbeginn");
                    colorDateTextView(holder.txtBox1, freqList.get(freqList.get(8)));
                    holder.txtBox2.setVisibility(View.GONE);
                    holder.txtBox3.setVisibility(View.GONE);
                    holder.txtBox5.setVisibility(View.GONE);
                    holder.txtBox6.setVisibility(View.GONE);
                    TextView[] textViews = {holder.txtBox1, holder.txtBox4, holder.txtBox7};
                    for (int x = 0; x < textViews.length; x++) {
                        colorDateTextView(textViews[x], freqList.get(x + 8));
                    }
                }
                break;
            case 4:
                holder.txtSpec.setTextColor(userColor);
                holder.btnSpec.setBackgroundTintList(ColorStateList.valueOf(userColor));
                holder.ivSpec.setVisibility(View.INVISIBLE);
                holder.txtNote.setTextColor(userColor);
                holder.btnNote.setBackgroundTintList(ColorStateList.valueOf(userColor));
                holder.ivNote.setVisibility(View.INVISIBLE);
                holder.txtFreq.setTextColor(userColor);
                holder.btnFreq.setBackgroundTintList(ColorStateList.valueOf(userColor));
                holder.ivFreq.setVisibility(View.INVISIBLE);
                holder.txtReward.setTextColor(mContext.getResources().getColor(ACCENT_COLOR));
                holder.btnReward.setBackgroundTintList(ColorStateList.valueOf(mContext.getResources().getColor(ACCENT_COLOR)));
                holder.ivReward.setVisibility(View.VISIBLE);
                holder.llBoxInfo.setVisibility(View.GONE);
                holder.btnNoteEdit.setVisibility(View.GONE);
                DecimalFormat decimalFormat = new DecimalFormat(",##0.00 \u00A4");
                holder.txtBoxInfo.setText(decimalFormat.format(combinedDataSet.getGoal().getGoalReward()));
                break;

        }

    }

    // color box date text views
    private void colorDateTextView(TextView textView, int freqCode) {
        boolean blnFreq = false;
        if (freqCode == 2) { // active is 2, not active is 1
            blnFreq = true;
        }
        if (blnFreq) {
            textView.setTextColor(mContext.getResources().getColor(ACCENT_COLOR));
        } else {
            textView.setTextColor(mContext.getResources().getColor(R.color.disabledViewColor));
        }
    }

    private void setButtonEnableState(Button button, boolean active) {
        if (active) {
            button.setEnabled(true);
            button.setTextColor(mContext.getResources().getColor(ACCENT_COLOR));
            Drawable[] drawablesPrev = button.getCompoundDrawables();
            drawablesPrev[1].setColorFilter(mContext.getResources().getColor(ACCENT_COLOR), PorterDuff.Mode.SRC_IN);
        } else {
            button.setEnabled(false);
            button.setTextColor(mContext.getResources().getColor(R.color.disabledViewColor));
            Drawable[] drawablesPrev = button.getCompoundDrawables();
            drawablesPrev[1].setColorFilter(mContext.getResources().getColor(R.color.disabledViewColor), PorterDuff.Mode.SRC_IN);
        }
    }

//    // set DataSet list
//    public void setDataSetList(List<CombinedDataSet> liveList) {
//        if (this.dataSetList.size() == 0) {
//            this.dataSetList = liveList;
//            listSelectedNote.clear();
//            for (int x = 0; x < dataSetList.size(); x++) {
//                listSelectedNote.add(x, 1);
//            }
//            notifyItemRangeInserted(0, liveList.size());
//        } else {
//            if (this.dataSetList.size() != liveList.size()) {
//                listSelectedNote.clear();
//                for (int y = 0; y < liveList.size(); y++) {
//                    listSelectedNote.add(y, 1);
//                }
//                this.dataSetList = liveList;
//                notifyDataSetChanged();
//            } else {
//                this.dataSetList = liveList;
//                notifyDataSetChanged();
//            }
//        }
//    }

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
