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

import java.util.ArrayList;

import tori.studygroups.R;
import tori.studygroups.mainActivities.AboutActivity;
import tori.studygroups.mainActivities.MainActivity;
import tori.studygroups.mainActivities.SettingsActivity;
import tori.studygroups.otherClass.Disconnection;

public class ChannelsActivity extends AppCompatActivity{

    private EditText searchChannelsEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MAHHHH", "channlActivity created");

        setContentView(R.layout.activity_channels);
        searchChannelsEditText = (EditText) findViewById(R.id.search_channel_editText);

        String channelUrl = null;
        String channelName = null;
        Intent intent = getIntent();

        channelUrl = intent.getStringExtra("eventNotificationChannelUrl");
        channelName = intent.getStringExtra("eventNotificationChannelName");

        ArrayList<String> userPrefChannelList = intent.getStringArrayListExtra(MainActivity.USER_PREF_CHANNEL_LIST);

        if(channelUrl != null && channelName != null){
            Fragment fragment = ChatFragment.newInstance(channelUrl, channelName);

            FragmentManager manager = getSupportFragmentManager();
            manager.popBackStack();
            manager.beginTransaction()
                    .replace(R.id.container_channels_list, fragment)
                    .commit();

        } else if (userPrefChannelList != null) {

            Fragment fragment;
            fragment = ChannelListFragment.newInstance(userPrefChannelList);
            FragmentManager manager = getSupportFragmentManager();
            manager.popBackStack();

            manager.beginTransaction()
                    .replace(R.id.container_channels_list, fragment, "FRAGMENT_CHANNEL_LIST")
                    .commit();



        } else {

            // Load list of Channels
            Fragment fragment;

            if (savedInstanceState == null) {
                fragment = ChannelListFragment.newInstance();
                FragmentManager manager = getSupportFragmentManager();
                manager.popBackStack();

                manager.beginTransaction()
                        .replace(R.id.container_channels_list, fragment, "FRAGMENT_CHANNEL_LIST")
                        .commit();

            } else { //fragment esiste già (es orientation change)
                fragment = getSupportFragmentManager().findFragmentByTag("FRAGMENT_CHANNEL_LIST");
            }
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
                Intent intent3 = new Intent(ChannelsActivity.this, SettingsActivity.class);
                startActivity(intent3);
                return true;

            case R.id.menu_home:
                Intent intent2 = new Intent(ChannelsActivity.this, MainActivity.class);
                startActivity(intent2);
                return true;

            case R.id.menu_general_item_disconnect:
                Disconnection.disconnect(this);
                return true;

            case R.id.menu_general_about:
                Intent intent = new Intent(ChannelsActivity.this, AboutActivity.class);
                startActivity(intent);
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
