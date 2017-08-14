package tori.studygroups.mainActivities;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;

import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.sendbird.android.User;

import butterknife.ButterKnife;
import butterknife.Bind;
import tori.studygroups.R;
import tori.studygroups.utils.PreferenceUtils;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "BOHMAH";
    private static final int REQUEST_SIGNUP = 0;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    @Bind(R.id.input_email) EditText _emailText;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.btn_login) Button _loginButton;
    @Bind(R.id.link_signup) TextView _signupLink;
    @Bind(R.id.password_forget) TextView _passwordForget;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Autenticazione...");

        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();

        _emailText.setText(PreferenceUtils.getEmail(this));
        _passwordText.setText(PreferenceUtils.getPassword(this));

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo == null || !netInfo.isConnected()) {
            Toast.makeText(LoginActivity.this,
                    "Devi essere connesso alla rete per usare l'applicazione", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
            finish();

        }

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
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        _passwordForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordForget();
            }
        });
    }



    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) { //già loggato con firebase, basta connetersi a sendibird
            Log.d("BOHMAH", "già loggato");
            _emailText.setText(currentUser.getEmail());

            //save device token sul server
            DatabaseReference dbRefUsers = FirebaseDatabase.getInstance().getReference("users");
            String token = FirebaseInstanceId.getInstance().getToken();
            dbRefUsers.child(currentUser.getUid()).child("devices").child(token).setValue("true");

            connectToSendBird(currentUser.getUid(), currentUser.getDisplayName());
        }
        else if (PreferenceUtils.getConnected(this)) { //non loggato con firebase,
            Log.d("BOHMAH", "preferences salvate in locale");
            Log.d("BOHMAH", PreferenceUtils.getEmail(this));
            Log.d("BOHMAH", PreferenceUtils.getPassword(this));
            connectToFirebase(PreferenceUtils.getEmail(this), PreferenceUtils.getPassword(this));
        }
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);
        _emailText.setEnabled(false);
        _passwordText.setEnabled(false);

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        PreferenceUtils.setEmail(LoginActivity.this, email);
        PreferenceUtils.setPassword(LoginActivity.this, password);

        connectToFirebase(email, password);

    }


    /**
     * in ritorno dal register activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                FirebaseUser user = mAuth.getCurrentUser();
                connectToSendBird(user.getUid(), user.getDisplayName());

            }
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }


    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        progressDialog.dismiss();
        _loginButton.setEnabled(true);
        _passwordText.setEnabled(true);
        _emailText.setEnabled(true);
    }


    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty()) {
            _emailText.setError("inserisci un username valido");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 ) {
            _passwordText.setError("almeno 4 caratteri");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }


    private void connectToFirebase(final String email, String password){

        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            _passwordText.setError(null);
                            Log.d(TAG, "signInWithEmail:success");

                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.d(TAG, user.getDisplayName());

                            //save device token sul server
                            DatabaseReference dbRefUsers = FirebaseDatabase.getInstance().getReference("users");
                            String token = FirebaseInstanceId.getInstance().getToken();
                            dbRefUsers.child(user.getUid()).child("devices").child(token).setValue("true");

                            connectToSendBird(user.getUid(), user.getDisplayName());
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            _emailText.setError("combinazione email password errata");
                            _passwordText.setError("combinazione email password errata");
                            onLoginFailed();
                        }
                    }
                });

    }

    private void connectToSendBird(final String userId, final String username) {

        if(!progressDialog.isShowing()){
            progressDialog.show();
        }


        SendBird.connect(userId, new SendBird.ConnectHandler() {
            @Override
            public void onConnected(User user, SendBirdException e) {

                if (e != null) {
                    // Error!
//                    Toast.makeText(
//                            LoginActivity.this, "" + e.getCode() + ": " + e.getMessage(),
//                            Toast.LENGTH_SHORT)
//                            .show();

                    _loginButton.setEnabled(true);
                    _emailText.setEnabled(true);
                    PreferenceUtils.setConnected(LoginActivity.this, false);
                    return;
                }

                PreferenceUtils.setConnected(LoginActivity.this, true);
                setCurrentUserInfo(username);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                progressDialog.dismiss();
                _loginButton.setEnabled(true);
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

                    Toast.makeText(
                            LoginActivity.this, "Errore salvataggio username",
                            Toast.LENGTH_SHORT)
                            .show();

                    return;
                }

            }

        });
    }


    private void passwordForget() {

        LayoutInflater inflater = getLayoutInflater();
        final Dialog dialog = new Dialog(LoginActivity.this);
        dialog.setTitle(R.string.reset_password);
        dialog.setContentView(inflater.inflate(R.layout.dialog_reset_password, null));
        dialog.setCancelable(true);

        Button confirm_button = (Button) dialog.findViewById(R.id.dialog_confirm_button);
        Button cancel_button = (Button) dialog.findViewById(R.id.dialog_cancel_button);
        final EditText emailEditText = (EditText) dialog.findViewById(R.id.reset_pass_email);
        if (! _emailText.getText().toString().isEmpty()){
            emailEditText.append(_emailText.getText().toString());
        }

        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseAuth auth = FirebaseAuth.getInstance();
                String emailAddress = emailEditText.getText().toString();

                if (emailAddress.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()){
                    emailEditText.setText("Inserire email valida");

                } else {
                    auth.sendPasswordResetEmail(emailAddress).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                                Toast.makeText(LoginActivity.this, "Email inviata", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (e.getMessage().equals("There is no user record corresponding to this identifier. The user may have been deleted.")){
                                Toast.makeText(LoginActivity.this, "Errore: non esiste nessun utente registrato con questa email", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(LoginActivity.this, "Errore", Toast.LENGTH_LONG).show();
                            }
                            Log.wtf("BOH", e.getMessage());

                        }
                    });
                }
            }
        });

        dialog.show();

    }

}
