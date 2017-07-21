package tori.studygroups.otherClass;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;
import com.sendbird.android.SendBird;

import tori.studygroups.mainActivities.LoginActivity;
import tori.studygroups.mainActivities.MainActivity;
import tori.studygroups.utils.PreferenceUtils;


public class Disconnection {

    public Disconnection(){};

    public static void disconnect(final Context context){
        SendBird.disconnect(new SendBird.DisconnectHandler() {
            @Override
            public void onDisconnected() {
                // You are disconnected from SendBird.
                FirebaseAuth.getInstance().signOut();
                PreferenceUtils.clearAll(context);

                Intent intent = new Intent(context, LoginActivity.class);
                context.startActivity(intent);
            }
        });

    }

}
