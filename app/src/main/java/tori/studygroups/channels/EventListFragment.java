package tori.studygroups.channels;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sendbird.android.OpenChannel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tori.studygroups.R;
import tori.studygroups.mainActivities.MainActivity;
import tori.studygroups.otherClass.MyEvent;

public class EventListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private LinearLayout loadBar;
    private LinearLayoutManager mLayoutManager;
    private TextView noEventsFindText;
    private String mChannelUrl;
    private OpenChannel mChannel;
    private EventListAdapter eventListAdapter;

    List<MyEvent> eventsList;

    public static EventListFragment newInstance() {
        EventListFragment fragment = new EventListFragment();
        return fragment;
    }

    public static EventListFragment newInstance(ArrayList<MyEvent> userPartecipationEventList) {
        EventListFragment fragment = new EventListFragment();

        Bundle args = new Bundle();
        args.putParcelableArrayList(MainActivity.USER_EVENT_PARTECIPATION, userPartecipationEventList);
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_event_list, container, false);

        setRetainInstance(true);
        setHasOptionsMenu(true);

        try {
            eventsList = getArguments().getParcelableArrayList(MainActivity.USER_EVENT_PARTECIPATION);
        } catch (Exception e){
            eventsList = null;
        }

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_event_list);
        loadBar = (LinearLayout) rootView.findViewById(R.id.linlaHeaderProgressEvent);
        noEventsFindText = (TextView) rootView.findViewById(R.id.no_event_find);

        eventListAdapter = new EventListAdapter(getContext());

        loadBar.setVisibility(View.VISIBLE);
        setUpAdapter();
        setUpRecyclerView();

        if (eventsList == null){
            mChannelUrl = getActivity().getIntent().getStringExtra(ChatFragment.EXTRA_CHANNEL_URL);
            getEventFromDb();

        } else {

            if (eventsList.size() == 0) {
                noEventsFindText.setVisibility(View.VISIBLE);
                loadBar.setVisibility(View.GONE);
            } else {
                eventListAdapter.setEventList(eventsList);
                noEventsFindText.setVisibility(View.GONE);
                loadBar.setVisibility(View.GONE);
            }

        }

        return rootView;

    }

    @Override
    public void onResume(){
        super.onResume();
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
//        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                if (mLayoutManager.findLastVisibleItemPosition() == eventListAdapter.getItemCount() - 1) {
//                    eventListAdapter.addLast();
//                }
//            }
//        });


    }


    private void getEventFromDb() {
        eventsList = new ArrayList<MyEvent>();
        DatabaseReference dbRefChannelEvents = FirebaseDatabase.getInstance().getReference("channelEvents").child(mChannelUrl);
        dbRefChannelEvents.orderByChild("timestampDateEvent").limitToLast(30).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChildren()){
                    noEventsFindText.setVisibility(View.GONE);
                    //for each child
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Log.d("MAH", snapshot.toString());
                        eventsList.add(snapshot.getValue(MyEvent.class));
                    }

                    Collections.reverse(eventsList);
                    eventListAdapter.setEventList(eventsList);
                    loadBar.setVisibility(View.GONE);

                } else {
                    loadBar.setVisibility(View.GONE);
                    noEventsFindText.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
