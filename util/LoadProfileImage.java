package com.benjaminsommer.dailygoals.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.benjaminsommer.dailygoals.R;

import java.io.InputStream;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by SOMMER on 28.01.2018.
 */


/**
 * Background Async task to load user profile picture from url
 * */
public class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {

    ImageView bmImage;

    public LoadProfileImage(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    protected Bitmap doInBackground(String... uri) {
        String url = uri[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(url).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {

        if (result != null) {

            Bitmap resized = Bitmap.createScaledBitmap(result,200,200, true);
            bmImage.setImageBitmap(ImageHelper.getRoundedCornerBitmap(getApplicationContext(), resized, 250, 200, 200, false, false, false, false));

            if (bmImage.getId() == R.id.loginActivity_facebook_profilePic) {
                // TODO: 05.10.2017 What's up here???
                //user.setFacebookProfilePicture(resized);
            } else if (bmImage.getId() == R.id.loginActivity_google_profilePic) {
                // TODO: 05.10.2017 What's up here???
                //user.setGoogleProfilePicture(resized);
            }

        }
    }

}
