import android.graphics.Path;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.SendBird.ConnectHandler;
import com.sendbird.android.User;
import com.sendbird.android.OpenChannel;
import com.sendbird.android.OpenChannelListQuery;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.UserMessage;

import java.util.List;

import tori.studygroups.R;

public class MainActivity extends AppCompatActivity {

    private OpenChannel tryChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SendBird.connect("tori", new ConnectHandler() {
            @Override
            public void onConnected(User user, SendBirdException e) {
                if (e != null) {
                    // Error.
                    Log.d("ERROR", "connect");
                    return;
                }
                OpenChannelListQuery channelListQuery = OpenChannel.createOpenChannelListQuery();
                channelListQuery.next(new OpenChannelListQuery.OpenChannelListQueryResultHandler() {
                    @Override
                    public void onResult(List<OpenChannel> channels, SendBirdException e) {
                        if (e != null) {
                            Log.d("ERROR", "lista chan");
                            return;
                        }
                        for (int i =0; i< channels.size(); i++){
                            Log.d("ERROR", channels.get(i).getUrl());
                        }

                    }
                });


            }
        });

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




//        OpenChannel.getChannel("sendbird_open_channel_23009_ef0c1bfcb7499750b9236f7b98c1d67265c65252", new OpenChannel.OpenChannelGetHandler() {
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
//                        if (openChannel == null){
//                            Log.d("ERROR", "openCahnnel è null");
//                        } else {
//                            Log.d("ERROR", "no null");
//                        }
//
//                        tryChannel = openChannel;
//                    }
//                });
//            }
//        });

//        tryChannel.sendUserMessage("hellochat", new BaseChannel.SendUserMessageHandler() {
//            @Override
//            public void onSent(UserMessage userMessage, SendBirdException e) {
//                if (e != null) {
//                    // Error.
//                    Log.d("ERROR", "send message");
//                    return;
//                }
//            }
//        });

    }
}
