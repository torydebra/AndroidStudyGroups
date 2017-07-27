package tori.studygroups.channels;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

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
import tori.studygroups.otherClass.MyEvent;
import tori.studygroups.otherClass.MyUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays a list of the participants of a specified Channel.
 */

public class EventsChannelListActivity extends AppCompatActivity {

    private UserListAdapter mListAdapter;
    private RecyclerView mRecyclerView;
    private LinearLayout loadBar;
    private LinearLayoutManager mLayoutManager;
    private String mChannelUrl;
    private OpenChannel mChannel;
    private EventListAdapter eventListAdapter;

    List<MyEvent> eventsList;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_participant_list);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_participant_list);
        loadBar = (LinearLayout) findViewById(R.id.linlaHeaderProgressPartecipant);

        mChannelUrl = getIntent().getStringExtra(ChatFragment.EXTRA_CHANNEL_URL);
        mListAdapter = new UserListAdapter(this);

        loadBar.setVisibility(View.VISIBLE);
        setUpRecyclerView();

        getEventFromDb();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void setUpRecyclerView() {
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mListAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }


    /**
     * Gets the channel instance with the channel URL.
     */
    private void getEventFromDb() {
        eventsList = new ArrayList<MyEvent>();
        DatabaseReference dbRefChannelEvents = FirebaseDatabase.getInstance().getReference("channelEvents").child(mChannelUrl);
        dbRefChannelEvents.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //for each child
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.d("MAH", snapshot.getKey());
                    Log.d("MAH", snapshot.toString());
                    eventsList.add(snapshot.getValue(MyEvent.class));
                    Log.d("MAH", eventsList.get(0).channelName);
                }

                eventListAdapter.setEventList(eventsList);
                loadBar.setVisibility(View.GONE);
            }
        });
    }
}
