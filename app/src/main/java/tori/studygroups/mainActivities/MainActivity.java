package tori.studygroups.mainActivities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.sendbird.android.OpenChannel;

import tori.studygroups.channels.ChannelsActivity;
import tori.studygroups.R;

public class MainActivity extends AppCompatActivity {

    private OpenChannel tryChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(MainActivity.this, ChannelsActivity.class);
        startActivity(intent);
        finish();


//        OpenChannel.createChannelWithOperatorUserIds("Prova3", null, null, null, new OpenChannel.OpenChannelCreateHandler() {
//            @Override
//            public void onResult(OpenChannel openChannel, SendBirdException e) {
//                if (e != null) {
//                    // Error!
//                    e.printStackTrace();
//                    Log.d("ERROR", "createchan");
//                    return;
//                }
//            }
//        });



//
//        OpenChannel.getChannel("Prova1", new OpenChannel.OpenChannelGetHandler() {
//            @Override
//            public void onResult(final OpenChannel openChannel, SendBirdException e) {
//                if (e != null) {
//                    // Error.
//                    Log.d("ERROR", "getCahnnerl");
//                    return;
//                }
//
//                openChannel.enter(new OpenChannel.OpenChannelEnterHandler() {
//                    @Override
//                    public void onResult(SendBirdException e) {
//                        if (e != null) {
//                            // Error.
//                            Log.d("ERROR", "enter channel");
//
//                            return;
//                        }
//
//                        tryChannel = openChannel;
//
//                        tryChannel.sendUserMessage("hellochat", new BaseChannel.SendUserMessageHandler() {
//                            @Override
//                            public void onSent(UserMessage userMessage, SendBirdException e) {
//                                if (e != null) {
//                                    // Error.
//                                    Log.d("ERROR", "send message");
//                                    return;
//                                }
//                            }
//                        });
//                    }
//                });
//            }
//        });



    }
}
