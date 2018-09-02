package com.benjaminsommer.dailygoals.ui.todo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.LayoutTransition;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Group;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.benjaminsommer.dailygoals.R;
import com.benjaminsommer.dailygoals.entities.ToDo;
import com.benjaminsommer.dailygoals.entities.ToDoCover;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DecimalFormat;


/**
 * Created by DEU209213 on 06.05.2017.
 */

public class ToDoAdapter extends ListAdapter<ToDoCover, RecyclerView.ViewHolder> {

    private static final String TAG = ToDoAdapter.class.getSimpleName();

    private static final int TYPE_HEADER = 1;
    private static final int TYPE_TODO = 2;
    private static final long ANIM_BUTTON_DURATION = 250;
    private static final int INT_NO_RECYCLE = 5555;

    private static final boolean BOOL_NO_RECYCLE = false;
    private Context mContext;

    private boolean isOpenToDoAdapter; // false = finished, true = open
    private int mExpandedPosition = -1;

    private ClickedButtonInterface clickedButtonInterface;

    public interface ClickedButtonInterface {
        void onGoalButtonClick(ToDo toDo, int newValue);
        void onEditButtonClick(ToDo toDo);
        void onDeleteButtonClick(ToDo toDo);
    }

    public ToDoAdapter(Context context, ClickedButtonInterface clickedButtonInterface, boolean isOpen) {
        super(ToDoAdapter.DIFF_CALLBACK);
        this.clickedButtonInterface = clickedButtonInterface;
        mContext = context;
        isOpenToDoAdapter = isOpen;
    }

    public static final DiffUtil.ItemCallback<ToDoCover> DIFF_CALLBACK = new DiffUtil.ItemCallback<ToDoCover>() {
        @Override
        public boolean areItemsTheSame(ToDoCover oldItem, ToDoCover newItem) {
            if (oldItem.isObjectType() != newItem.isObjectType()) {
                return false;
            } else if (oldItem.isObjectType() && newItem.isObjectType()) {
                return oldItem.getToDo().getToDoId() == newItem.getToDo().getToDoId();
            } else {
                return oldItem.getSelectorCategory() == newItem.getSelectorCategory();
            }
        }

        @Override
        public boolean areContentsTheSame(ToDoCover oldItem, ToDoCover newItem) {
            return oldItem == newItem;
        }
    };

    public class ToDoHeaderViewHolder extends RecyclerView.ViewHolder {
        public TextView txtHeader, txtSort;

        public ToDoHeaderViewHolder(View itemView) {
            super(itemView);
            txtHeader = (TextView) itemView.findViewById(R.id.item_todo_header_text);
            txtSort = (TextView) itemView.findViewById(R.id.item_todo_header_sort);
        }

        public void bindHeaderItem(ToDoCover toDoCover, int position) {

            txtHeader.setText(toDoCover.getHeaderText());
            if (toDoCover.getSelectorCategory() == 0 || toDoCover.getSelectorCategory() == 10) {
                txtHeader.setTextColor(mContext.getResources().getColor(R.color.colorRed));
            } else if (toDoCover.getSelectorCategory() == 100) {
                txtHeader.setTextColor(mContext.getResources().getColor(R.color.colorGreen));
            } else {
                txtHeader.setTextColor(mContext.getResources().getColor(R.color.colorBlack));
            }
            if (isPositionFirstRow(position)) {
                if (isOpenToDoAdapter) {
                    txtSort.setText(R.string.toDo_list_selector_time);
                } else {
                    txtSort.setText(R.string.toDo_list_selector_status);
                }
                txtSort.setVisibility(View.VISIBLE);
            } else {
                txtSort.setVisibility(View.GONE);
            }

        }
        
    }

    public class ToDoItemViewHolder extends RecyclerView.ViewHolder {
        public TextView txtName, txtTime, txtReward;
        public ImageView ivTime, ivSnooze, ivReward;
        public ImageButton btnYes, btnNo;
        public Button btnOne;
        public Button btnTwo;
        public Button btnThree;
        public ConstraintLayout containerItem;
        public Group groupButtons;
        //public RelativeLayout containerButtons;

        public ToDoItemViewHolder(View itemView) {
            super(itemView);
            txtName = (TextView) itemView.findViewById(R.id.item_todo_open_text_header);
            txtTime = (TextView) itemView.findViewById(R.id.item_todo_open_text_reminder);
            txtReward = (TextView) itemView.findViewById(R.id.item_todo_open_text_reward);
            ivTime = (ImageView) itemView.findViewById(R.id.item_todo_open_image_reminder);
            ivSnooze = (ImageView) itemView.findViewById(R.id.item_todo_open_image_snooze);
            ivReward = (ImageView) itemView.findViewById(R.id.item_todo_open_image_reward);
            btnYes = (ImageButton) itemView.findViewById(R.id.item_todo_open_button_yes);
            btnNo = (ImageButton) itemView.findViewById(R.id.item_todo_open_button_no);
            btnOne = (Button) itemView.findViewById(R.id.item_todo_open_button_one);
            btnTwo = (Button) itemView.findViewById(R.id.item_todo_open_button_two);
            btnThree = (Button) itemView.findViewById(R.id.item_todo_open_button_three);
            containerItem = (ConstraintLayout) itemView.findViewById(R.id.item_todo_open_container_listitem);
            groupButtons = (Group) itemView.findViewById(R.id.item_todo_open_group);
            //containerButtons = (RelativeLayout) itemView.findViewById(R.id.item_todo_open_container_buttons);
        }

        public void bindToDoItem(final ToDoCover toDoCover, final int position) {

            // section button container
            //containerButtons.setVisibility(View.GONE);
            //groupButtons.setVisibility(View.GONE);
            // btnThree.setVisibility(View.GONE);

            // section header text
            txtName.setText(toDoCover.getToDo().getToDoName());

            // section reminder
            ivTime.setEnabled(toDoCover.getToDo().isToDoHasReminder());
            if (toDoCover.getToDo().isToDoHasReminder()) {
                DateTime dt = new DateTime(toDoCover.getToDo().getToDoReminderTime());
                DateTimeFormatter fmt = DateTimeFormat.forPattern("dd.MM.yyyy, HH:mm");
                txtTime.setText(fmt.print(dt) + " Uhr");
                ivTime.setSelected(true);
                ivSnooze.setVisibility(View.VISIBLE);
                ivSnooze.setSelected(toDoCover.getToDo().isToDoHasSnooze());
            } else {
                txtTime.setText("Kein Reminder");
                ivTime.setSelected(false);
                ivSnooze.setVisibility(View.INVISIBLE);
            }

            // section reward
            ivReward.setSelected(toDoCover.getToDo().isToDoHasReward());
            if (toDoCover.getToDo().isToDoHasReward()) {
                DecimalFormat currencyFormatter = new DecimalFormat(",##0.00 \u00A4");
                txtReward.setText(currencyFormatter.format(toDoCover.getToDo().getToDoRewardAmount()));
            } else {
                txtReward.setText("Keine Belohnung");
            }

            // section buttons
            if (!isOpenToDoAdapter) {
                btnOne.setText(R.string.toDo_list_btn_delete);
                Drawable img = mContext.getResources().getDrawable(R.drawable.icon_delete);
                img.setBounds( 0, 0, 60, 60 );
                btnOne.setCompoundDrawables(img, null, null, null);
            } else {
                btnOne.setText(R.string.toDo_list_btn_edit);
                Drawable img = mContext.getResources().getDrawable(R.drawable.icon_edit_24px);
                img.setBounds( 0, 0, 60, 60 );
                btnOne.setCompoundDrawables(img, null, null, null);
            }
            btnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    containerItem.setOnClickListener(null);
                    btnYes.setSelected(true);
                    btnNo.setVisibility(View.GONE);
                    new CountDownTimer(1000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                        }

                        @Override
                        public void onFinish() {
                            clickedButtonInterface.onGoalButtonClick(toDoCover.getToDo(), 100);
                        }
                    }.start();
                }
            });

            btnNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    containerItem.setOnClickListener(null);
                    btnNo.setSelected(true);
                    btnYes.setVisibility(View.GONE);
                    new CountDownTimer(1000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                        }

                        @Override
                        public void onFinish() {
                            clickedButtonInterface.onGoalButtonClick(toDoCover.getToDo(), 10);
                        }
                    }.start();
                }
            });
            btnOne.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isOpenToDoAdapter) {
                        clickedButtonInterface.onEditButtonClick(toDoCover.getToDo());
                    } else {
                        clickedButtonInterface.onDeleteButtonClick(toDoCover.getToDo());
                    }
                }
            });
            btnThree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickedButtonInterface.onDeleteButtonClick(toDoCover.getToDo());
                }
            });
            //// AS LONG AS SHARE FUNCTIONALITY IS NOT AVAILABLE
            // TODO: 29.04.2018: Implement share function
            btnTwo.setEnabled(false);

            final boolean isExpanded = position == mExpandedPosition;
            groupButtons.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            btnThree.setVisibility(!isOpenToDoAdapter ? View.GONE : (isExpanded ? View.VISIBLE : View.GONE));
            containerItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mExpandedPosition = isExpanded ? -1 : position;

                    notifyItemChanged(position);
                }
            });

            // section yes/no buttons
            if (!isOpenToDoAdapter) {
                if (toDoCover.getToDo().getToDoState() == 100) {
                    buttonToYes(btnYes, btnNo);
                } else if (toDoCover.getToDo().getToDoState() == 10) {
                    buttonToNo(btnYes, btnNo);
                }
            } else {
                buttonToOpen(btnYes, btnNo);
            }

        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_TODO) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_todo_card, parent, false);
            return new ToDoItemViewHolder(itemView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_todo_header, parent, false);
            return new ToDoHeaderViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {

        final ToDoCover toDoCover = getItem(position);

//        View.OnClickListener onClickListener = new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (v.getId() == R.id.item_todo_open_button_yes) {
//                    //animateButton(toDoCover.getToDo(), true, ((ToDoItemViewHolder) viewHolder).btnYes, ((ToDoItemViewHolder) viewHolder).btnNo);
//                    //((ToDoItemViewHolder) viewHolder).itemView.setTag(INT_NO_RECYCLE, BOOL_NO_RECYCLE);
//                    //((ToDoItemViewHolder) viewHolder).setIsRecyclable(false);
//
//                    ((ToDoItemViewHolder) viewHolder).btnYes.setSelected(true);
//                    ((ToDoItemViewHolder) viewHolder).btnNo.setVisibility(View.GONE);
//                    new CountDownTimer(1000, 1000) {
//                        @Override
//                        public void onTick(long millisUntilFinished) {
//                        }
//
//                        @Override
//                        public void onFinish() {
//                            clickedButtonInterface.onGoalButtonClick(toDo, 100);
//                        }
//                    };
////                    ((ViewGroup) viewHolder.itemView.findViewById(R.id.item_todo_open_card)).getLayoutTransition()
////                            .enableTransitionType(LayoutTransition.CHANGING);
//
//
//
//                }
//            }
//        };

        // section header
        if (viewHolder instanceof ToDoHeaderViewHolder) {

            ((ToDoHeaderViewHolder) viewHolder).bindHeaderItem(toDoCover, position);

        }

        // section to do item
         else if (viewHolder instanceof ToDoItemViewHolder) {

            ((ToDoItemViewHolder) viewHolder).bindToDoItem(toDoCover, position);

        }

    }

//    @Override
//    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
//        if (holder.itemView.getTag(INT_NO_RECYCLE) == BOOL_NO_RECYCLE) {
//            holder.setIsRecyclable(false);
//        }
//        super.onViewDetachedFromWindow(holder);
//    }

    @Override
    public int getItemViewType(int position) {
        if (!getItem(position).isObjectType()) {
            return TYPE_HEADER;
        } else {
            return TYPE_TODO;
        }
    }

    // calculates if it is the first row in the adapter
    private boolean isPositionFirstRow(int position) {
        return position == 0;
    }

    // method to change goal buttons to YES
    public void buttonToYes(ImageButton ibYes, ImageButton ibNo) {
        ibYes.setVisibility(View.VISIBLE);
        ibYes.setSelected(true);
        ibYes.setClickable(false);
        ibNo.setVisibility(View.GONE);
        ibNo.setSelected(false);
        ibNo.setClickable(false);
    }

    // method to change goal buttons to NO
    public void buttonToNo (ImageButton ibYes, ImageButton ibNo) {
        ibYes.setVisibility(View.GONE);
        ibYes.setSelected(false);
        ibYes.setClickable(false);
        ibNo.setVisibility(View.VISIBLE);
        ibNo.setSelected(true);
        ibNo.setClickable(false);
    }

    public void buttonToOpen (ImageButton ibYes, ImageButton ibNo) {
        ibYes.setVisibility(View.VISIBLE);
        ibYes.setSelected(false);
        ibYes.setClickable(true);
        ibNo.setVisibility(View.VISIBLE);
        ibNo.setSelected(false);
        ibNo.setClickable(true);
    }

    public void animateButton(final ToDo toDo, final boolean yesOrNo, final ImageButton ibYes, final ImageButton ibNo) {

        // color animation
        final int colorGreen = mContext.getResources().getColor(R.color.colorGreen);
        final int colorRed = mContext.getResources().getColor(R.color.colorRed);
        final int colorGrey = mContext.getResources().getColor(R.color.colorGrey);
        ValueAnimator colorAnim = ValueAnimator.ofFloat(0f, 1f);
        colorAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float mul = (Float) animation.getAnimatedValue();
                if (yesOrNo) {
                    int alphaGreen = adjustAlpha(colorGreen, colorGrey, mul);
                    ibYes.setColorFilter(alphaGreen, PorterDuff.Mode.SRC_ATOP);
                } else {
                    int alphaRed = adjustAlpha(colorRed, colorGrey, mul);
                    ibNo.setColorFilter(alphaRed, PorterDuff.Mode.SRC_ATOP);
                }

            }
        });
        colorAnim.setDuration(ANIM_BUTTON_DURATION);

        // moving x animation
        float btnPositionX;
        float distanceX;
        if (yesOrNo) {
            btnPositionX = ibYes.getX() + (ibYes.getLayoutParams().width / 2);
            distanceX = mContext.getResources().getDimensionPixelSize(R.dimen.animation_btnYesNo_moveX);
        } else {
            btnPositionX = ibNo.getX() + (ibYes.getLayoutParams().width / 2);
            distanceX = mContext.getResources().getDimensionPixelSize(R.dimen.animation_btnYesNo_moveX) * (-1f);
        }
        ValueAnimator posAnimX = ValueAnimator.ofFloat(btnPositionX, btnPositionX - distanceX);
        posAnimX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (yesOrNo) {
                    ibYes.setX((float) animation.getAnimatedValue() - (ibYes.getLayoutParams().width / 2));
                    ibYes.requestLayout();
                } else {
                    ibNo.setX((float) animation.getAnimatedValue() - (ibNo.getLayoutParams().width / 2));
                    ibNo.requestLayout();
                }
            }
        });
        posAnimX.setDuration(ANIM_BUTTON_DURATION);

        // fading animation
        ValueAnimator fadeAnim = ValueAnimator.ofFloat(1f, 0f);
        fadeAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (yesOrNo) {
                    ibNo.setAlpha((float) animation.getAnimatedValue());
                } else {
                    ibYes.setAlpha((float) animation.getAnimatedValue());
                }
            }
        });
        fadeAnim.setDuration(ANIM_BUTTON_DURATION);

        ValueAnimator pauseAnim = ValueAnimator.ofFloat(0f, 1f);
        pauseAnim.setDuration(ANIM_BUTTON_DURATION);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(colorAnim).with(fadeAnim).before(posAnimX);
        animatorSet.play(colorAnim).before(pauseAnim);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (yesOrNo) {
                    clickedButtonInterface.onGoalButtonClick(toDo, 100);
                } else {
                    clickedButtonInterface.onGoalButtonClick(toDo, 10);
                }
            }
        });
        animatorSet.start();

    }

    public int adjustAlpha(int colorFinish, int colorStart, float factor) {
        float deltaRed = (Color.red(colorFinish) - Color.red(colorStart)) * factor;
        float deltaGreen = (Color.green(colorFinish) - Color.green(colorStart)) * factor;
        float deltaBlue = (Color.blue(colorFinish) - Color.blue(colorStart)) * factor;
        int red = Color.red(colorStart) + (int) deltaRed;
        int green = Color.green(colorStart) + (int) deltaGreen;
        int blue = Color.blue(colorStart) + (int) deltaBlue;
        return Color.argb(255, red, green, blue);
    }

    public void changeAdapterType(boolean type) {
        mExpandedPosition = -1;
        this.isOpenToDoAdapter = type;
    }


}
