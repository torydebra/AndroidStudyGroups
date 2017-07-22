package tori.studygroups.channels;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import tori.studygroups.R;

import static tori.studygroups.channels.ChatFragment.EXTRA_CHANNEL_URL;


public class AddEventActivity extends AppCompatActivity {

    private String channelUrl;
    private EditText nameEventText;
    private EditText locationEventText;
    private TextView dateEventText;
    private TextView timeEventText;
    private Button createEventButton;


    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        channelUrl = intent.getStringExtra(EXTRA_CHANNEL_URL);

        setContentView(R.layout.activity_add_event);

        nameEventText = (EditText) findViewById(R.id.edittext_add_event_name);
        locationEventText = (EditText) findViewById(R.id.edittext_add_event_location);
        dateEventText = (TextView) findViewById(R.id.edittext_add_event_date);
        timeEventText = (TextView) findViewById(R.id.edittext_add_event_time);
        createEventButton = (Button) findViewById(R.id.button_create_event);



    }


}

