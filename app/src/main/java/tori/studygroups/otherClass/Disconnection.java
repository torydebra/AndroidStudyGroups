package tori.studygroups.otherClass;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.sendbird.android.SendBird;

import java.io.IOException;

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
                try {
                    FirebaseInstanceId.getInstance().deleteInstanceId();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(context, LoginActivity.class);
                context.startActivity(intent);
            }
        });

    }

}
