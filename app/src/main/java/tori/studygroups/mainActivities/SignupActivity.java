package tori.studygroups.mainActivities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import tori.studygroups.R;
import tori.studygroups.otherClass.MyUser;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "BOHMAH";
    private static final int REQUEST_SIGNUP = 0;
    private FirebaseAuth mAuth;
    private Pattern pattern = Pattern.compile("^[^\\.#\\$\\[\\]]+$");


    @Bind(R.id.input_username) EditText _usernameText;
    @Bind(R.id.input_email) EditText _emailText;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.input_password2) EditText _password2Text;
    @Bind(R.id.btn_signup) Button _signupButton;
    @Bind(R.id.link_login) TextView _loginLink;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        ButterKnife.bind(this);
        progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creazione utente...");

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }



    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        String username = _usernameText.getText().toString().trim();
        username = username.replaceAll("\\s", "");

        DatabaseReference dbRefUsernameTaken = FirebaseDatabase.getInstance().getReference("usernameTaken");
        dbRefUsernameTaken.child(username.toLowerCase()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    Log.d("MAHREG", "username in uso");
                    _usernameText.setError("username già in uso");
                    _usernameText.requestFocus();
                    onSignupFailed();
                    return;
                } else {
                    Log.d("MAHREG", "username non in uso");
                    _usernameText.setError(null);
                    _signupButton.setEnabled(false);


                    String email = _emailText.getText().toString().trim();
                    String password = _passwordText.getText().toString().trim();
                    String username = _usernameText.getText().toString().trim();
                    username = username.replaceAll("\\s", "");
                    signupFirebase(email, password, username);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("MAHREG", databaseError.getMessage());
            }

        });

    }

    private void signupFirebase(final String email, final String password, final String username) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
                            _emailText.setError(null);
                            //String username = email.substring(0, email.indexOf('@'));
                            Log.d("BOHMAH", "createUserWithEmail:success");
                            MyUser myUser = new MyUser(username, email);
                            FirebaseUser user = mAuth.getCurrentUser();

                            DatabaseReference dbRefUsers = FirebaseDatabase.getInstance().getReference("users");
                            dbRefUsers.child(user.getUid()).setValue(myUser);

                            DatabaseReference dbRefUsernameTaken = FirebaseDatabase.getInstance().getReference("usernameTaken");
                            dbRefUsernameTaken.child(username.toLowerCase()).setValue("true");

                            String token = FirebaseInstanceId.getInstance().getToken();
                            dbRefUsers.child(user.getUid()).child("devices").child(token).setValue("true");


                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(username)
                                    //.setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "User profile updated.");

                                                progressDialog.dismiss();
                                                onSignupSuccess();
                                            }
                                        }
                                    });



                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("BOHMAH", "createUserWithEmail:failure", task.getException());
                            progressDialog.dismiss();
                            _emailText.setError("Email già in uso");
                            _emailText.requestFocus();
                            onSignupFailed();
                        }
                    }
                });
    }


    public void onSignupSuccess() {
        progressDialog.dismiss();
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        progressDialog.dismiss();
        Toast.makeText(getBaseContext(), "Registrazione fallita", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String username = _usernameText.getText().toString().trim();
        Matcher usernameMatcher = pattern.matcher(username);
        String email = _emailText.getText().toString().trim();
        String password = _passwordText.getText().toString().trim();
        String password2 = _password2Text.getText().toString().trim();

        if (username.isEmpty() || username.length() < 3) {
            _usernameText.setError("almeno 3 caratteri");
            _usernameText.requestFocus();
            valid = false;
        } else if (! usernameMatcher.matches()) {
            _usernameText.setError("Username non può contenere . $ # [ ]");
            _usernameText.requestFocus();
            valid = false;
        } else {
            _usernameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("inserisci una email valida");
            _emailText.requestFocus();
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 6) {
            _passwordText.setError("almeno 6 caratteri");
            _passwordText.requestFocus();
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (password2.isEmpty() || password2.length() < 6) {
            _password2Text.setError("almeno 6 caratteri");
            _password2Text.requestFocus();
            valid = false;
        } else if (!(password2.equals(password))) {
            _password2Text.setError("Password non combaciano");
            _password2Text.requestFocus();
            valid = false;
        } else {
            _password2Text.setError(null);
        }

        return valid;

    }
}