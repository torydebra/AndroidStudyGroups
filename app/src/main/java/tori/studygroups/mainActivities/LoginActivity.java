package tori.studygroups.mainActivities;

import android.content.Intent;
import android.graphics.Path;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.SendBird.ConnectHandler;
import com.sendbird.android.User;
import com.sendbird.android.OpenChannel;
import com.sendbird.android.OpenChannelListQuery;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.UserMessage;

import tori.studygroups.R;


public class LoginActivity extends AppCompatActivity{

    // TODO: password

    private EditText usernameEditText;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        usernameEditText = (EditText) findViewById(R.id.username_edit_text);
        loginButton = (Button) findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = usernameEditText.getText().toString();
                // Remove all spaces from userID
                username = username.replaceAll("\\s", "");

                connectToSendBird(username);


            }
        });
    }

    //TODO onstart con cose login salvato in preferences

    private void connectToSendBird(final String username) {

        loginButton.setEnabled(false);
        usernameEditText.setEnabled(false);

        SendBird.connect(username, new SendBird.ConnectHandler() {
            @Override
            public void onConnected(User user, SendBirdException e) {

                if (e != null) {
                    // Error!
                    Toast.makeText(
                            LoginActivity.this, "" + e.getCode() + ": " + e.getMessage(),
                            Toast.LENGTH_SHORT)
                            .show();

                    // Show login failure snackbar
                    loginButton.setEnabled(true);
                    return;
                }

                setCurrentUserInfo(username);

                // Proceed to MainActivity
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void setCurrentUserInfo (String username){

        //null è campo foto profilo
        SendBird.updateCurrentUserInfo(username, null, new SendBird.UserInfoUpdateHandler(){

            @Override
            public void onUpdated(SendBirdException e) {
                if (e != null) {
                    // Error!
//                    Toast.makeText(
//                            LoginActivity.this, "" + e.getCode() + ":" + e.getMessage(),
//                            Toast.LENGTH_SHORT)
//                            .show();

                    Toast.makeText(
                            LoginActivity.this, "Error saving nickname",
                            Toast.LENGTH_SHORT)
                            .show();

                    return;
                }

            }

        });
    }

}
