package com.benjaminsommer.dailygoals.ui.introduction;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.benjaminsommer.dailygoals.R;
import com.benjaminsommer.dailygoals.ui.login.LoginActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class IntroductionFragment extends Fragment {


    // flexible variables set from Activity
//    String mNewHeaderText = "";
//    String mNewSubText = "";
//    int mNewBGColor = 0;
//    int mNewImageView = 0;


    public IntroductionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_introduction, container, false);
        // view declaration
        RelativeLayout rlContainer = (RelativeLayout) view.findViewById(R.id.introductionFragment_container);
        TextView header = (TextView) view.findViewById(R.id.introductionFragment_headerText);
        TextView subText = (TextView) view.findViewById(R.id.introductionFragment_bottomText);
        ImageView imageView = (ImageView) view.findViewById(R.id.introductionFragment_image);
        ImageButton btnFB = (ImageButton) view.findViewById(R.id.introductionFragment_button_facebook);
        ImageButton btnGoogle = (ImageButton) view.findViewById(R.id.introductionFragment_button_google);


        switch (getArguments().getInt("fragmentID")) {
            case 0:
                rlContainer.setBackgroundColor(view.getResources().getColor(R.color.colorPrimary));
                header.setText(R.string.introduction_welcome);
                subText.setText(R.string.introduction_welcome_subText);
                imageView.clearColorFilter();
                imageView.setImageResource(R.drawable.icon_dailygoals_logo_24px_v3);
                break;
            case 1:
                rlContainer.setBackgroundColor(view.getResources().getColor(R.color.colorAccent));
                header.setText(R.string.introduction_dailyGoals_header);
                subText.setText(R.string.introduction_dailyGoals_subText);
                imageView.setImageResource(R.drawable.icon_date_24dp);
                break;
            case 2:
                rlContainer.setBackgroundColor(view.getResources().getColor(R.color.cat_Orange_full));
                header.setText(R.string.introduction_toDo_header);
                subText.setText(R.string.introduction_toDo_subText);
                imageView.setImageResource(R.drawable.icon_to_do_24px);
                break;
            case 3:
                rlContainer.setBackgroundColor(view.getResources().getColor(R.color.cat_Green_full));
                header.setText(R.string.introduction_reward_header);
                subText.setText(R.string.introduction_reward_subText);
                imageView.setImageResource(R.drawable.ic_coin);
                break;
            case 4:
                rlContainer.setBackgroundColor(view.getResources().getColor(R.color.cat_Red_full));
                header.setText(R.string.introduction_wishlist_header);
                subText.setText(R.string.introduction_wishlist_subText);
                imageView.setImageResource(R.drawable.icon_wishlist_24dp);
                break;
            case 5:
                rlContainer.setBackgroundColor(view.getResources().getColor(R.color.colorPrimaryDark));
                header.setText(R.string.introduction_login_header);
                subText.setText(R.string.introduction_login_subText);
                imageView.setImageResource(R.drawable.ic_login);
                btnFB.setVisibility(View.VISIBLE);
                btnGoogle.setVisibility(View.VISIBLE);
                View.OnClickListener onClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getActivity(), LoginActivity.class);
                        startActivity(i);
                    }
                };
                btnFB.setOnClickListener(onClickListener);
                btnGoogle.setOnClickListener(onClickListener);
                imageView.setOnClickListener(onClickListener);
                break;
            default:
                break;
        }

        return view;
    }

//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//
//        if (!mNewHeaderText.equals("")) {
//            header.setText(mNewHeaderText);
//        }
//        if (!mNewSubText.equals("")) {
//            subText.setText(mNewSubText);
//        }
//        if (mNewBGColor != 0) {
//            rlContainer.setBackgroundColor(mNewBGColor);
//        }
//        if (mNewImageView != 0) {
//            imageView.setImageResource(mNewImageView);
//        }
//
//    }

    public static IntroductionFragment newInstance(int number) {

        IntroductionFragment introductionFragment = new IntroductionFragment();
        Bundle b = new Bundle();
        b.putInt("fragmentID", number);
        introductionFragment.setArguments(b);
        return introductionFragment;

    }

//    public void changeHeaderText(String newText) {
//        mNewHeaderText = newText;
//    }
//
//    public void changeBackgroundColor(int color) {
//        mNewBGColor = color;
//    }
//
//    public void changeSubText(String newText) {
//        mNewSubText = newText;
//    }
//
//    public void changeImageView(int imageRes) {
//        mNewImageView = imageRes;
//    }

//    public void insertFragmentContent(int fragmentID) {
//
//        switch (fragmentID) {
//            case 0:
//                rlContainer.setBackgroundColor(view.getResources().getColor(R.color.colorPrimary));
//                header.setText(R.string.introduction_welcome);
//                subText.setText(R.string.introduction_welcome_subText);
//                imageView.setImageResource(R.drawable.icon_dailygoals_logo_24px_v3);
//                imageView.clearColorFilter();
//                break;
//            case 1:
//                break;
//            case 2:
//                break;
//            case 3:
//                break;
//            case 4:
//                break;
//            case 5:
//                break;
//            case 6:
//                break;
//            default:
//                break;
//        }
//
//
//    }

}
