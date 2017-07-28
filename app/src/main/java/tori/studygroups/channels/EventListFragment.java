package tori.studygroups.channels;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

public class EventListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private LinearLayout loadBar;
    private LinearLayoutManager mLayoutManager;
    private String mChannelUrl;
    private OpenChannel mChannel;
    private EventListAdapter eventListAdapter;

    List<MyEvent> eventsList;

    public static EventListFragment newInstance() {
        EventListFragment fragment = new EventListFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_event_list, container, false);

        setRetainInstance(true);
        setHasOptionsMenu(true);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_event_list);
        loadBar = (LinearLayout) rootView.findViewById(R.id.linlaHeaderProgressEvent);

        mChannelUrl = getActivity().getIntent().getStringExtra(ChatFragment.EXTRA_CHANNEL_URL);
        eventListAdapter = new EventListAdapter(getContext());

        loadBar.setVisibility(View.VISIBLE);
        setUpAdapter();
        setUpRecyclerView();
        getEventFromDb();

        return rootView;

    }


    private void setUpAdapter() {
        eventListAdapter.setOnItemClickListener(new EventListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(MyEvent event) {


                EventFragment fragment = EventFragment.newInstance(event.toJsonString());
                getFragmentManager().beginTransaction()
                        .replace(R.id.container_event_list, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        eventListAdapter.setOnItemLongClickListener(new EventListAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongPress(MyEvent event) {
            }
        });

    }

    private void setUpRecyclerView() {
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(eventListAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
    }


    /**
     * Gets the channel instance with the channel URL.
     */
    private void getEventFromDb() {
        eventsList = new ArrayList<MyEvent>();
        DatabaseReference dbRefChannelEvents = FirebaseDatabase.getInstance().getReference("channelEvents").child(mChannelUrl);
        dbRefChannelEvents.orderByChild("timestampDateEvent").addListenerForSingleValueEvent(new ValueEventListener() {

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

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
