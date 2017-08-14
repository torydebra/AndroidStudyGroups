package tori.studygroups.otherClass;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.sendbird.android.SendBird;

import java.io.IOException;

import tori.studygroups.mainActivities.LoginActivity;
import tori.studygroups.utils.PreferenceUtils;


public class Disconnection {

    public Disconnection(){}

    public static void disconnect(final Context context){
        SendBird.disconnect(new SendBird.DisconnectHandler() {
            @Override
            public void onDisconnected() {
                // You are disconnected from SendBird.

                FirebaseAuth auth = FirebaseAuth.getInstance();

                String token = FirebaseInstanceId.getInstance().getToken();
                Log.d("MAHDISCO", token);

                DatabaseReference dbRefUsers = FirebaseDatabase.getInstance().getReference("users");
                dbRefUsers.child(auth.getCurrentUser().getUid()).child("devices").child(token).getRef().removeValue();

                DatabaseReference dbRefChannelToDevice = FirebaseDatabase.getInstance().getReference("channelToDevice");
                dbRefChannelToDevice.orderByChild(token).getRef().removeValue();

                FirebaseAuth.getInstance().signOut();
                PreferenceUtils.clearAll(context);
                try {
                    FirebaseInstanceId.getInstance().deleteInstanceId();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                context.deleteDatabase(EventDB.DB_NAME);

                Intent intent = new Intent(context, LoginActivity.class);
                context.startActivity(intent);




            }
        });

    }

}
