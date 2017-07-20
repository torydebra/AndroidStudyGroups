package tori.studygroups.mainActivities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.sendbird.android.OpenChannel;

import tori.studygroups.channels.ChannelsActivity;
import tori.studygroups.R;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//        startActivity(intent);

        Intent intent = new Intent(MainActivity.this, ChannelsActivity.class);
        startActivity(intent);
        finish();


    }
}
