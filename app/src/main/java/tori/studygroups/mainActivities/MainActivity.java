package tori.studygroups.mainActivities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

import java.util.ArrayList;

import tori.studygroups.exams.ActivityExamList;
import tori.studygroups.exams.ExamListFragment;
import tori.studygroups.channels.ChannelsActivity;
import tori.studygroups.R;
import tori.studygroups.channels.EventsChannelListActivity;
import tori.studygroups.otherClass.Disconnection;

public class MainActivity extends AppCompatActivity {


    public static final String USER_PREF_CHANNEL_LIST = "userPrefChannelList";
    public static final String USER_EVENT_PARTECIPATION = "userEventPartecipation";

    private TextView helloMessage;
    private Button findGroupsButton;
    private Button viewPrefGroupsButton;
    private Button viewEventPartecipation;
    private Button viewPersonalPage;

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
        viewPersonalPage = (Button) findViewById(R.id.main_button_view_personal_page);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id){
            case R.id.menu_home:

                return true;

            case R.id.menu_general_item_disconnect:
                Disconnection.disconnect(this);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

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

                Intent intent = new Intent(MainActivity.this, EventsChannelListActivity.class);
                intent.putExtra(USER_EVENT_PARTECIPATION, true);
                startActivity(intent);

            }
        });

        viewPersonalPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ActivityExamList.class);
                startActivity(intent);
            }
        });

    }
}
