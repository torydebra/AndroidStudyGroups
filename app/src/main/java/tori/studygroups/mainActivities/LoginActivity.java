//package tori.studygroups.mainActivities;
//
//import android.content.Intent;
//import android.graphics.Path;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.view.View.OnClickListener;
//import android.widget.Toast;
//
//import com.sendbird.android.SendBird;
//import com.sendbird.android.SendBirdException;
//import com.sendbird.android.SendBird.ConnectHandler;
//import com.sendbird.android.User;
//import com.sendbird.android.OpenChannel;
//import com.sendbird.android.OpenChannelListQuery;
//import com.sendbird.android.BaseChannel;
//import com.sendbird.android.UserMessage;
//
//import tori.studygroups.R;
//
//
//public class LoginActivity extends AppCompatActivity{
//
//    // TODO: password
//
//    private EditText usernameEditText;
//    private Button loginButton;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.login_activity);
//
//        usernameEditText = (EditText) findViewById(R.id.username_edit_text);
//        loginButton = (Button) findViewById(R.id.loginButton);
//
//        loginButton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                String username = usernameEditText.getText().toString();
//                // Remove all spaces from userID
//                username = username.replaceAll("\\s", "");
//
//                connectToSendBird(username);
//
//
//            }
//        });
//    }
//
//    //TODO onstart con cose login salvato in preferences
//
//    private void connectToSendBird(final String username) {
//
//        loginButton.setEnabled(false);
//        usernameEditText.setEnabled(false);
//
//        SendBird.connect(username, new SendBird.ConnectHandler() {
//            @Override
//            public void onConnected(User user, SendBirdException e) {
//
//                if (e != null) {
//                    // Error!
//                    Toast.makeText(
//                            LoginActivity.this, "" + e.getCode() + ": " + e.getMessage(),
//                            Toast.LENGTH_SHORT)
//                            .show();
//
//                    // Show login failure snackbar
//                    loginButton.setEnabled(true);
//                    return;
//                }
//
//                setCurrentUserInfo(username);
//
//                // Proceed to MainActivity
//                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        });
//
//    }
//
//    private void setCurrentUserInfo (String username){
//
//        //null è campo foto profilo
//        SendBird.updateCurrentUserInfo(username, null, new SendBird.UserInfoUpdateHandler(){
//
//            @Override
//            public void onUpdated(SendBirdException e) {
//                if (e != null) {
//                    // Error!
////                    Toast.makeText(
////                            LoginActivity.this, "" + e.getCode() + ":" + e.getMessage(),
////                            Toast.LENGTH_SHORT)
////                            .show();
//
//                    Toast.makeText(
//                            LoginActivity.this, "Error saving nickname",
//                            Toast.LENGTH_SHORT)
//                            .show();
//
//                    return;
//                }
//
//            }
//
//        });
//    }
//
//}
//




package tori.studygroups.mainActivities;

        import android.app.ProgressDialog;
        import android.os.Bundle;
        import android.support.v7.app.AppCompatActivity;
        import android.util.Log;

        import android.content.Intent;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.TextView;
        import android.widget.Toast;

        import butterknife.ButterKnife;
        import butterknife.Bind;
        import tori.studygroups.R;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    @Bind(R.id.input_username) EditText _usernameText;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.btn_login) Button _loginButton;
    @Bind(R.id.link_signup) TextView _signupLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String email = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();

        // TODO: Implement your own authentication logic here.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess();
                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty()) {
            _usernameText.setError("enter a valid email address");
            valid = false;
        } else {
            _usernameText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}
