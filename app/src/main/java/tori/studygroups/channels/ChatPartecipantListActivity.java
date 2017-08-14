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
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.sendbird.android.OpenChannel;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;
import com.sendbird.android.UserListQuery;

import java.util.List;

import tori.studygroups.R;
import tori.studygroups.exams.ActivityExamList;

/**
 * Displays a list of the participants of a specified Channel.
 */

public class ChatPartecipantListActivity extends AppCompatActivity {

    private UserListAdapter mListAdapter;
    private RecyclerView mRecyclerView;
    private LinearLayout loadBar;
    private LinearLayoutManager mLayoutManager;
    private String mChannelUrl;
    private OpenChannel mChannel;


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
        getChannelFromUrl();
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


    /**
     * Gets the channel instance with the channel URL.
     */
    private void getChannelFromUrl() {
        OpenChannel.getChannel(mChannelUrl, new OpenChannel.OpenChannelGetHandler() {
            @Override
            public void onResult(OpenChannel openChannel, SendBirdException e) {
                mChannel = openChannel;

                getUserList();
            }
        });
    }


    private void getUserList() {
        UserListQuery userListQuery = mChannel.createParticipantListQuery();
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

                new AlertDialog.Builder(ChatPartecipantListActivity.this)
                    .setTitle("Opzioni")
                        .setIcon(R.drawable.ic_settings_orange)
                    .setItems(R.array.chat_message_long_clic_options_not_delete, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0: //vedi userpage
                                    Intent intent = new Intent(ChatPartecipantListActivity.this, ActivityExamList.class);
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
