package tori.studygroups.channels;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sendbird.android.OpenChannel;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;
import com.sendbird.android.UserListQuery;

import java.util.ArrayList;
import java.util.List;

import tori.studygroups.R;
import tori.studygroups.exams.ActivityExamList;

import static com.sendbird.android.SendBird.createUserListQuery;

/**
 * Displays a list of the participants of a specified Event.
 */

public class EventPartecipantListActivity extends AppCompatActivity {

    public static final String EVENT_ID = "eventId";

    private UserListAdapter mListAdapter;
    private RecyclerView mRecyclerView;
    private TextView noPartecipantFind;
    private LinearLayout loadBar;
    private LinearLayoutManager mLayoutManager;
    private String mChannelUrl;
    private OpenChannel mChannel;

    private String eventId;

    private List<String> usersIdList;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_participant_list);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_participant_list);
        noPartecipantFind = (TextView) findViewById(R.id.no_Partecipant_Text);
        loadBar = (LinearLayout) findViewById(R.id.linlaHeaderProgressPartecipant);
        eventId = getIntent().getStringExtra("eventId");
        mListAdapter = new UserListAdapter(this);

        loadBar.setVisibility(View.VISIBLE);
        setUpRecyclerView();
        getUserListFromFirebase();

        setOnClickListener();

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


    private void getUserListFromFirebase() {

        usersIdList = new ArrayList<String>();
        DatabaseReference dbRefEvents = FirebaseDatabase.getInstance().getReference("eventPartecipant").child(eventId);
        dbRefEvents.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //for each child
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){

                    Log.d("MAH", snapshot.getKey());
                    usersIdList.add(snapshot.getKey());
                }

                if (usersIdList.size() != 0){
                    noPartecipantFind.setVisibility(View.GONE);
                    getUserListFromSendBird();

                } else {
                    noPartecipantFind.setVisibility(View.VISIBLE);
                    loadBar.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getUserListFromSendBird() {

        UserListQuery userListQuery = createUserListQuery(usersIdList);
        userListQuery.next(new UserListQuery.UserListQueryResultHandler() {
            @Override
            public void onResult(List<User> list, SendBirdException e) {
                if (e != null) {
                    // Error!
                    return;
                }

                mListAdapter.setUserList(list);
                loadBar.setVisibility(View.GONE);
            }
        });
    }


    private void setOnClickListener() {

        mListAdapter.setOnItemClickListener(new UserListAdapter.OnItemClickListener() {
            @Override
            public void onUserItemClick(final User user) {

                new AlertDialog.Builder(EventPartecipantListActivity.this)
                    .setTitle("Opzioni")
                        .setIcon(R.drawable.ic_settings_orange)
                    .setItems(R.array.chat_message_long_clic_options_not_delete, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0: //vedi userpage
                                    Intent intent = new Intent(EventPartecipantListActivity.this, ActivityExamList.class);
                                    intent.putExtra(ActivityExamList.USERID, user.getUserId());
                                    startActivity(intent);
                                    break;
                                default:
                                    break;
                            }
                        }
                    })
                    .create().show();
            }
        });
    }

}
