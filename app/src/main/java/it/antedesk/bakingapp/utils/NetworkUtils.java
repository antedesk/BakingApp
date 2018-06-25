package it.antedesk.bakingapp.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static it.antedesk.bakingapp.utils.SupportVariablesDefinition.RECIPES_DATASOURCE_URL;
import static it.antedesk.bakingapp.utils.SupportVariablesDefinition.RECIPES_LOADING;

/**
 * Created by Antedesk on 15/05/2018.
 */

public class NetworkUtils {

    /**
     * Allows to check if the app is connected to internet or not.
     * source of the below code https://goo.gl/q7gpMi
     * @param activity is the current activity that invokes this method.
     * @return a boolean value that states if the app is connected to internet or not
     */
    public static boolean isOnline(Activity activity) {
        ConnectivityManager cm =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = null;
        if (cm != null)
            netInfo = cm.getActiveNetworkInfo();

        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
