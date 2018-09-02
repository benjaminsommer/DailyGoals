package com.benjaminsommer.dailygoals.firebase;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by SOMMER on 26.11.2017.
 */

public class ApiResponse<T> {

    public final int code;
    @Nullable
    public final T body;
    @Nullable
    public final String errorMessage;

    public ApiResponse(Throwable error) {
        code = 500;
        body = null;
        errorMessage = error.getMessage();
    }

    public ApiResponse(T response) {
        if (response == null) {
            code = 400;
            body = null;
            errorMessage = "ApiResponse is null!";
        } else {
            code = 300;
            body = response;
            errorMessage = null;
        }
    }



    public boolean isSuccessful() {
        return (code == 300);
    }

}
