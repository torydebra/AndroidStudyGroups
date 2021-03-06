package tori.studygroups.channels;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sendbird.android.OpenChannel;
import com.sendbird.android.SendBirdException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tori.studygroups.R;

public class CreateChannelActivity extends AppCompatActivity {

    public static final int CHANNEL_CREATED = 10;

    TextInputEditText mNameEditText;
    private Button mCreateButton;

    private Pattern pattern = Pattern.compile("^[^\\.#\\$\\[\\]]+$");



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_channel);

        mNameEditText = (TextInputEditText) findViewById(R.id.edittext_create_channel_name);

        String groupName = getIntent().getExtras().getString("groupName","");
        mNameEditText.append(groupName);

        mCreateButton = (Button) findViewById(R.id.button_create_channel);

        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameChannel = mNameEditText.getText().toString().trim();
                Matcher nameMatcher = pattern.matcher(nameChannel);
                if (nameChannel.isEmpty()){
                    mNameEditText.setError("Inserire nome");
                    mNameEditText.requestFocus();
                    return;

                } else if (!nameMatcher.matches()){
                    mNameEditText.setError("Nome gruppo non può contenere . $ # [ ]");
                    mNameEditText.requestFocus();
                    return;

                } else{
                    createOpenChannel(nameChannel);
                }
            }
        });

        mCreateButton.setEnabled(true);

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

    private void createOpenChannel(final String name) {
        mCreateButton.setEnabled(false);

        DatabaseReference dbRefChannels = FirebaseDatabase.getInstance().getReference("channels").child(name);
        dbRefChannels.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    dataSnapshot.getRef().setValue("true");

                    OpenChannel.createChannelWithOperatorUserIds(name, null, null, null, new OpenChannel.OpenChannelCreateHandler() {
                        @Override
                        public void onResult(final OpenChannel openChannel, SendBirdException e) {
                            if (e != null) {
                                // Error!
                                return;
                            }
                            //add created channel to favourite
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            DatabaseReference dbRefUserPrefChannels = FirebaseDatabase.getInstance().getReference("userPrefChannels");
                            dbRefUserPrefChannels.child(user.getUid()).child(openChannel.getUrl()).setValue("true");

                            DatabaseReference dbRefChannelPrefUser = FirebaseDatabase.getInstance().getReference("channelPrefUser");
                            dbRefChannelPrefUser.child(openChannel.getUrl()).child(user.getUid()).setValue("true");

                            //START: add device for notification
                            DatabaseReference dbRefUser = FirebaseDatabase.getInstance().getReference("users");
                            final DatabaseReference dbRefChannelToDevice = FirebaseDatabase.getInstance().getReference("channelToDevice");
                            dbRefUser.child(user.getUid()).child("devices").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                                        //Log.d("MAH", snapshot.getKey());
                                        dbRefChannelToDevice.child(openChannel.getUrl()).child(snapshot.getKey()).setValue("true");
                                    }

                                    Intent intent = new Intent();
                                    intent.putExtra("channelName", name);
                                    setResult(CHANNEL_CREATED, intent);
                                    finish();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            //FINISH: add device for notification

                        }
                    });

                } else {
                    mNameEditText.setError("Esiste già un gruppo con questo nome");
                    mNameEditText.requestFocus();
                    mCreateButton.setEnabled(true);
                    return;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
    });
    }
}
