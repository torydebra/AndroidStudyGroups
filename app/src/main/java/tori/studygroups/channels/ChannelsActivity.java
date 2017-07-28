package tori.studygroups.channels;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import tori.studygroups.R;
import tori.studygroups.otherClass.Disconnection;

public class ChannelsActivity extends AppCompatActivity{

    private EditText searchChannelsEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channels);
        searchChannelsEditText = (EditText) findViewById(R.id.search_channel_editText);

        String eventData = null;
        Intent intent = getIntent();
        Log.d("MAHHHH", "channlActivity created");
        eventData = intent.getStringExtra("eventNotification");

        if(eventData != null){
            Log.d("MAHHHH", "channlActivity eventData");
            Fragment fragment = EventFragment.newInstance(eventData);

            FragmentManager manager = getSupportFragmentManager();
            manager.popBackStack();
            manager.beginTransaction()
                    .replace(R.id.container_channels_list, fragment)
                    .commit();
        } else {

            // Load list of Channels
            Fragment fragment = ChannelListFragment.newInstance();

            FragmentManager manager = getSupportFragmentManager();
            manager.popBackStack();

            manager.beginTransaction()
                    .replace(R.id.container_channels_list, fragment)
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
            case R.id.menu_general_personal_page:

                return true;

            case R.id.menu_general_item_disconnect:
                Disconnection.disconnect(this);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }

    //per il nome della chat in alto nel gruppo della chat
    void setActionBarTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

}
