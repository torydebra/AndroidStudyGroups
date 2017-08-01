package tori.studygroups.mainActivities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sendbird.android.OpenChannel;

import java.util.ArrayList;
import java.util.List;

import tori.studygroups.channels.ChannelsActivity;
import tori.studygroups.R;

public class MainActivity extends AppCompatActivity {


    public static final String USER_PREF_CHANNEL_LIST = "userPrefChannelList";

    private TextView helloMessage;
    private Button findGroupsButton;
    private Button viewPrefGroupsButton;
    private Button viewEventPartecipation;

    private FirebaseUser user;
    ArrayList<String> prefChannels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        helloMessage = (TextView) findViewById(R.id.hello_Username_Message);
        findGroupsButton = (Button) findViewById(R.id.main_button_find_groups);
        viewPrefGroupsButton = (Button) findViewById(R.id.main_button_view_pref_groups);
        viewEventPartecipation = (Button) findViewById(R.id.main_button_view_event_partecipation);

        prefChannels = null;

        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        helloMessage.setText("Ciao " + user.getDisplayName());

        setupButtons();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_general, menu);
        return true;
    }

    private void setupButtons() {
        findGroupsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ChannelsActivity.class);
                startActivity(intent);
            }
        });

        viewPrefGroupsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference dbRefUserPrefChannels = FirebaseDatabase.getInstance().getReference("userPrefChannels");
                prefChannels = new ArrayList<>();
                dbRefUserPrefChannels.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot snapshot: dataSnapshot.getChildren()){

                            prefChannels.add(snapshot.getKey());
                        }

                        Intent intent = new Intent(MainActivity.this, ChannelsActivity.class);
                        intent.putStringArrayListExtra(USER_PREF_CHANNEL_LIST, prefChannels);
                        Log.d("MAHMAIN0", prefChannels.toString());
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



            }
        });

        viewEventPartecipation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
}
