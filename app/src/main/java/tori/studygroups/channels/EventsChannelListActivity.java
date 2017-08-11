package tori.studygroups.channels;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sendbird.android.OpenChannel;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;
import com.sendbird.android.UserListQuery;
import tori.studygroups.R;
import tori.studygroups.mainActivities.AboutActivity;
import tori.studygroups.mainActivities.MainActivity;
import tori.studygroups.mainActivities.SettingsActivity;
import tori.studygroups.otherClass.Disconnection;
import tori.studygroups.otherClass.EventDB;
import tori.studygroups.otherClass.MyEvent;
import tori.studygroups.otherClass.MyUser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Displays a list of the participants of a specified Channel.
 */

public class EventsChannelListActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_event_list);

        boolean userEventPartecipation = getIntent().getBooleanExtra(MainActivity.USER_EVENT_PARTECIPATION, false);



        if (userEventPartecipation){

           // EventDB localDB = new EventDB(this);
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference dbRefUserEvents = FirebaseDatabase.getInstance().getReference("userEvents");
            dbRefUserEvents.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    ArrayList<MyEvent> eventList = new ArrayList<>();
                    for (DataSnapshot eventSnapshot: dataSnapshot.getChildren()){
                        MyEvent event = eventSnapshot.getValue(MyEvent.class);
                        eventList.add(event);
                    }

                    Fragment fragment = EventListFragment.newInstance(eventList);
                    FragmentManager manager = getSupportFragmentManager();
                    manager.popBackStack();

                    manager.beginTransaction()
                            .replace(R.id.container_event_list, fragment)
                            .commit();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        } else {
            Fragment fragment = EventListFragment.newInstance();
            FragmentManager manager = getSupportFragmentManager();
            manager.popBackStack();

            manager.beginTransaction()
                    .replace(R.id.container_event_list, fragment)
                    .commit();
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
