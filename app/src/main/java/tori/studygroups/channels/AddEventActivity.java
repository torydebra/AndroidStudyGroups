package tori.studygroups.channels;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import tori.studygroups.R;


public class AddEventActivity extends AppCompatActivity {

    private String channelUrl;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        channelUrl = intent.getStringExtra("")

        setContentView(R.layout.activity_add_event);

    }


}
}
