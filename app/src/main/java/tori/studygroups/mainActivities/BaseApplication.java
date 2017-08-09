package tori.studygroups.mainActivities;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.sendbird.android.SendBird;

public class BaseApplication extends Application {

    private static final String APP_ID = "91AF2D8C-5DE1-4C0E-A9BA-FECAB3E50EF3"; // tori key
    public static final String VERSION = "0.1";

    @Override
    public void onCreate() {
        super.onCreate();
        SendBird.init(APP_ID, getApplicationContext());


    }
}