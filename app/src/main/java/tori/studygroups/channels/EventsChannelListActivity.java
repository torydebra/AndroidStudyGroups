package tori.studygroups.channels;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import tori.studygroups.R;
import tori.studygroups.mainActivities.AboutActivity;
import tori.studygroups.mainActivities.MainActivity;
import tori.studygroups.mainActivities.SettingsActivity;
import tori.studygroups.otherClass.Disconnection;
import tori.studygroups.otherClass.MyEvent;


public class EventsChannelListActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_event_list);

        boolean userEventPartecipation = getIntent().getBooleanExtra(MainActivity.USER_EVENT_PARTECIPATION, false);

        if (userEventPartecipation){

           // EventDB localDB = new EventDB(this);
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference dbRefUserEvents = FirebaseDatabase.getInstance().getReference("userEvents");

            dbRefUserEvents.child(user.getUid()).orderByChild("timestampDateEvent").limitToLast(30).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    ArrayList<MyEvent> eventList = new ArrayList<>();
                    for (DataSnapshot eventSnapshot: dataSnapshot.getChildren()){
                        MyEvent event = eventSnapshot.getValue(MyEvent.class);
                        eventList.add(event);
                    }
                    Collections.reverse(eventList);

                    if (savedInstanceState == null) {
                        Fragment fragment = EventListFragment.newInstance(eventList);
                        FragmentManager manager = getSupportFragmentManager();
                        manager.popBackStack();

                        manager.beginTransaction()
                                .replace(R.id.container_event_list, fragment, "FRAGMENT_USER_EVENT_LIST")
                                .commit();

                    } else { //fragment esiste già (es orientation change)
                        getSupportFragmentManager().findFragmentByTag("FRAGMENT_USER_EVENT_LIST");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        } else {

            if (savedInstanceState == null){
                Fragment fragment = EventListFragment.newInstance();
                FragmentManager manager = getSupportFragmentManager();
                manager.popBackStack();

                manager.beginTransaction()
                        .replace(R.id.container_event_list, fragment, "FRAGMENT_CHAT_EVENT_LIST")
                        .commit();
            } else {
                getSupportFragmentManager().findFragmentByTag("FRAGMENT_CHAT_EVENT_LIST");
            }

        }
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
            case R.id.user_setting:
                Intent intent3 = new Intent(EventsChannelListActivity.this, SettingsActivity.class);
                startActivity(intent3);
                return true;

            case R.id.menu_home:
                Intent intent2 = new Intent(EventsChannelListActivity.this, MainActivity.class);
                startActivity(intent2);
                return true;

            case R.id.menu_general_item_disconnect:
                Disconnection.disconnect(this);
                return true;

            case R.id.menu_general_about:
                Intent intent = new Intent(EventsChannelListActivity.this, AboutActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }

}
