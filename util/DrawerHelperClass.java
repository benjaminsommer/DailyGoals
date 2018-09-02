package com.benjaminsommer.dailygoals.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.benjaminsommer.dailygoals.ui.date_selection.DateSelectionActivity;
import com.benjaminsommer.dailygoals.R;
import com.benjaminsommer.dailygoals.ui.settings.SettingsActivity;
import com.benjaminsommer.dailygoals.ui.statistics.StatisticActivity;
import com.benjaminsommer.dailygoals.ui.todo.ToDoActivity;
import com.benjaminsommer.dailygoals.ui.wishlist.WishListActivity;
import com.benjaminsommer.dailygoals.ui.dataset.DataSetActivity;
import com.benjaminsommer.dailygoals.ui.goals.GoalsActivity;
import com.benjaminsommer.dailygoals.ui.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

/**
 * Created by DEU209213 on 10.03.2017.
 */

public class DrawerHelperClass implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener, DrawerLayout.DrawerListener {

    private static final String TAG = DrawerHelperClass.class.getSimpleName();

    private Context context;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private int drawerTag;
    private FirebaseAuth mAuth;
    private Class<?> aClass = null;

    public DrawerHelperClass(Context context, NavigationView navigationView, DrawerLayout drawerLayout, int drawerTag) {
        this.context = context;
        this.navigationView = navigationView;
        this.drawerLayout = drawerLayout;
        this.drawerTag = drawerTag;
        mAuth = FirebaseAuth.getInstance();
    }

    public void setupNavigationLoginSection() {

        // initiate header components
        View headerLayout = navigationView.getHeaderView(0);
        Button loginButton = (Button) headerLayout.findViewById(R.id.navigationHeader_button_login);
        ImageView profilePic = (ImageView) headerLayout.findViewById(R.id.navigationHeader_imageView_accountImage);
        TextView profileName = (TextView) headerLayout.findViewById(R.id.navigationHeader_textView_accountName);
        TextView profileEmail = (TextView) headerLayout.findViewById(R.id.navigationHeader_textView_accountMail);
        Spinner spinner = (Spinner) headerLayout.findViewById(R.id.navigationHeader_spinner);

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            loginButton.setVisibility(View.VISIBLE);
            profileName.setVisibility(View.GONE);
            profileEmail.setVisibility(View.GONE);
            spinner.setVisibility(View.GONE);
            profilePic.setImageResource(R.drawable.icon_account_circle_24dp);
            profilePic.setColorFilter(Color.argb(255, 255, 255, 255));
        } else {
            loginButton.setVisibility(View.GONE);
            profileName.setVisibility(View.VISIBLE);
            profileEmail.setVisibility(View.VISIBLE);
            spinner.setVisibility(View.VISIBLE);

            Uri userPic = user.getPhotoUrl();
            Log.d(TAG, "firebase: " + userPic.toString());
            String strUserEmail = user.getEmail();
            for (UserInfo profile : user.getProviderData()) {
                Log.d(TAG, profile.getProviderId());

                // check if the provider id matches "facebook.com"
                if (profile.getProviderId().equals("facebook.com")) {

                    userPic = profile.getPhotoUrl();
                    strUserEmail = profile.getEmail();
                    Log.d(TAG, "facebook: " + userPic.toString());

                    // check if the provider id matches "google.com"
                } else if (profile.getProviderId().equals("google.com")) {
                    userPic = profile.getPhotoUrl();
                    strUserEmail = profile.getEmail();
                    Log.d(TAG, "google: " + userPic.toString());
                    break;

                }
            }

            Log.d(TAG, "end: "+ userPic.toString());
            if (userPic != null) {
                profilePic.clearColorFilter();
                new LoadProfileImage(profilePic).execute(userPic.toString());
            } else {
                profilePic.setImageResource(R.drawable.icon_account_circle_24dp);
                profilePic.setColorFilter(Color.argb(255, 255, 255, 255));
            }
            profileName.setText(user.getDisplayName());
            profileEmail.setText(strUserEmail);
        }

        profilePic.setOnClickListener(this);
        profileName.setOnClickListener(this);
        profileEmail.setOnClickListener(this);
        loginButton.setOnClickListener(this);

    }

    public void setupItemSelectedListener() {
        navigationView.setNavigationItemSelectedListener(this);
        drawerLayout.addDrawerListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // checking if the item is in checked state or not, if not make it in checked state
        if (!item.isChecked()) {
            item.setChecked(true);
        }

        // closing drawer on item click
        drawerLayout.closeDrawers();

        if (item.getItemId() != drawerTag) {
            // check to see which item was being clicked and perform appropriate action
            switch (item.getItemId()) {
                case R.id.menuNavigation_goalsToday:
                    aClass = DataSetActivity.class;
                    break;
                case R.id.menuNavigation_goalsCalendar:
                    aClass = DateSelectionActivity.class;
                    break;
                case R.id.menuNavigation_toDo:
                    aClass = ToDoActivity.class;
                    break;
                case R.id.menuNavigation_goalsDefine:
                    aClass = GoalsActivity.class;
                    break;
                case R.id.menuNavigation_wishList:
                    aClass = WishListActivity.class;
                    break;
                case R.id.menuNavigation_statistics:
                    aClass = StatisticActivity.class;
                    break;
                case R.id.menuNavigation_settings:
                    aClass = SettingsActivity.class;
                    break;
                default:
                    Toast.makeText(context, "Something is wrong", Toast.LENGTH_SHORT).show();
            }
        }
        return true;
    }

    @Override
    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
    }

    @Override
    public void onDrawerOpened(@NonNull View drawerView) {
    }

    @Override
    public void onDrawerClosed(@NonNull View drawerView) {
        if (aClass != null) {
            startNewActivity(aClass, false, -1);
        }
        aClass = null;
    }

    @Override
    public void onDrawerStateChanged(int newState) {
    }

    private void startNewActivity(Class<?> newClass, boolean hasExtra, int extra) {
        Intent intent = new Intent(context, newClass);
        if (!hasExtra) {
            intent.putExtra("dataSetPosition", extra);
        }
        context.startActivity(intent);
    }

    public void checkCurrentItem() {
        navigationView.getMenu().findItem(drawerTag).setChecked(true);
    }

    @Override
    public void onClick(View v) {
        Intent loginIntent = new Intent(context, LoginActivity.class);
        context.startActivity(loginIntent);
    }

}