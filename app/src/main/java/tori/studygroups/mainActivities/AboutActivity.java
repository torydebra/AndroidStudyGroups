package tori.studygroups.mainActivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;
import tori.studygroups.R;
import tori.studygroups.channels.ChannelsActivity;
import tori.studygroups.otherClass.Disconnection;


public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Element adsElement = new Element();
        //adsElement.setTitle("Advertise with us");

        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setDescription(getString(R.string.about_page_description))
                .setImage(R.drawable.ic_general)
                .addItem(new Element().setTitle("Version 1.0"))
                //.addItem(adsElement)
                .addGroup("Contatti")
                .addEmail("toridebraus@gmail.com", "Tori")
                .addEmail("cyrusgaudry@yahoo.fr", "Cyrus")
                .addEmail("raqueltasal@gmail.com", "Raquel")
                .addGitHub("torydebra/AndroidStudyGroups")
                .addGroup("Contribuzioni")
                .addWebsite("https://firebase.google.com/", "Firebase")
                .addWebsite("https://sendbird.com/", "Sendbird")
                .addWebsite("http://jakewharton.github.io/butterknife/", "Butter Knife")
                .addWebsite("https://github.com/thoughtbot/expandable-recycler-view", "Expandable Recyclerview")
                .addWebsite("https://github.com/medyo/android-about-page", "Android About Page")
                .addItem(getCopyRightsElement())
                .create();

        setContentView(aboutPage);

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
                Intent intent3 = new Intent(AboutActivity.this, SettingsActivity.class);
                startActivity(intent3);
                return true;

            case R.id.menu_home:
                Intent intent2 = new Intent(AboutActivity.this, MainActivity.class);
                startActivity(intent2);
                return true;

            case R.id.menu_general_item_disconnect:
                Disconnection.disconnect(this);
                return true;

            case R.id.menu_general_about:

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }


    Element getCopyRightsElement() {
        Element copyRightsElement = new Element();
        final String copyrights = String.format(getString(R.string.copy_right), java.util.Calendar.getInstance().get(java.util.Calendar.YEAR));
        copyRightsElement.setTitle(copyrights);
        copyRightsElement.setIconDrawable(R.drawable.about_icon_copy_right);
        copyRightsElement.setIconTint(mehdi.sakout.aboutpage.R.color.about_item_icon_color);
        copyRightsElement.setIconNightTint(android.R.color.white);
        copyRightsElement.setGravity(Gravity.CENTER);
        copyRightsElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AboutActivity.this, copyrights, Toast.LENGTH_SHORT).show();
            }
        });
        return copyRightsElement;
    }
}


