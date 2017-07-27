package tori.studygroups.channels;
import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sendbird.android.OpenChannel;
import com.sendbird.android.SendBirdException;
import tori.studygroups.R;

public class CreateChannelActivity extends AppCompatActivity {

    TextInputEditText mNameEditText;
    private boolean enableCreate = false;
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
                createOpenChannel(mNameEditText.getText().toString());
            }
        });

        mCreateButton.setEnabled(enableCreate);

        mNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    if (enableCreate) {
                        mCreateButton.setEnabled(false);
                        enableCreate = false;
                    }
                } else {
                    if (!enableCreate) {
                        mCreateButton.setEnabled(true);
                        enableCreate = true;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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

    private void createOpenChannel(String name) {
        OpenChannel.createChannelWithOperatorUserIds(name, null, null, null, new OpenChannel.OpenChannelCreateHandler() {
            @Override
            public void onResult(OpenChannel openChannel, SendBirdException e) {
                if (e != null) {
                    // Error!
                    return;
                }

                // Open Channel created

                //add created channel to favourite
                FirebaseAuth mAuth;
                mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();

                DatabaseReference dbRefUserPrefChannels = FirebaseDatabase.getInstance().getReference("userPrefChannels");
                dbRefUserPrefChannels.child(user.getUid()).child(openChannel.getUrl()).setValue("true");

                DatabaseReference dbRefChannelPrefUser = FirebaseDatabase.getInstance().getReference("channelPrefUser");
                dbRefChannelPrefUser.child(openChannel.getUrl()).child(user.getUid()).setValue("true");

                finish();
            }
        });
    }


}
