package tori.studygroups.exams;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import tori.studygroups.R;
import tori.studygroups.channels.ChannelListFragment;
import tori.studygroups.mainActivities.AboutActivity;
import tori.studygroups.mainActivities.MainActivity;
import tori.studygroups.mainActivities.SettingsActivity;
import tori.studygroups.otherClass.Disconnection;


public class ActivityExamList extends AppCompatActivity {

    public static final String USERID = "userId";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_personal_page);
        getSupportActionBar().setTitle(getClass().getSimpleName());

        Intent intent = getIntent();
        String userId = intent.getStringExtra(USERID);
        Fragment fragment = null;

        if (savedInstanceState == null) {

            if (userId != null && userId.length() >0){
                fragment = ExamListFragment.newInstance(userId);
            } else {
                fragment = ExamListFragment.newInstance();
            }

            FragmentManager manager = getSupportFragmentManager();
            manager.popBackStack();
            manager.beginTransaction()
                    .replace(R.id.container_personal_page, fragment, "FRAGMENT_EXAM_LIST")
                    .commit();

        } else { //fragment esiste già (es orientation change)
            fragment = getSupportFragmentManager().findFragmentByTag("FRAGMENT_EXAM_LIST");
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
                Intent intent3 = new Intent(ActivityExamList.this, SettingsActivity.class);
                startActivity(intent3);

                return true;
            case R.id.menu_home:

                return true;

            case R.id.menu_general_item_disconnect:
                Disconnection.disconnect(this);
                return true;

            case R.id.menu_general_about:
                Intent intent = new Intent(ActivityExamList.this, AboutActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }

    void setActionBarTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

}
