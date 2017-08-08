package tori.studygroups.channels;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
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
import tori.studygroups.R;

public class CreateChannelActivity extends AppCompatActivity {

    TextInputEditText mNameEditText;
    private Button mCreateButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_channel);

        mNameEditText = (TextInputEditText) findViewById(R.id.edittext_create_channel_name);

        String groupName = getIntent().getExtras().getString("groupName","merda");
        mNameEditText.append(groupName);

        mCreateButton = (Button) findViewById(R.id.button_create_channel);

        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (! mNameEditText.getText().toString().isEmpty()){
                    createOpenChannel(mNameEditText.getText().toString());
                } else {
                    mNameEditText.setError("Inserire nome");
                    mNameEditText.requestFocus();
                    return;
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
                        public void onResult(OpenChannel openChannel, SendBirdException e) {
                            if (e != null) {
                                // Error!
                                return;
                            }

                            // Open Channel created

                            //add created channel to favourite
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            DatabaseReference dbRefUserPrefChannels = FirebaseDatabase.getInstance().getReference("userPrefChannels");
                            dbRefUserPrefChannels.child(user.getUid()).child(openChannel.getUrl()).setValue("true");

                            DatabaseReference dbRefChannelPrefUser = FirebaseDatabase.getInstance().getReference("channelPrefUser");
                            dbRefChannelPrefUser.child(openChannel.getUrl()).child(user.getUid()).setValue("true");

                            finish();
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
