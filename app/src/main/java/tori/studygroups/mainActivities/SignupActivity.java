package tori.studygroups.mainActivities;

import android.support.annotation.NonNull;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;


import butterknife.ButterKnife;
import butterknife.Bind;
import tori.studygroups.R;
import tori.studygroups.otherClass.MyUser;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "BOHMAH";
    private static final int REQUEST_SIGNUP = 0;
    private FirebaseAuth mAuth;

    @Bind(R.id.input_username) EditText _nameText;
    @Bind(R.id.input_email) EditText _emailText;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.input_password2) EditText _password2Text;
    @Bind(R.id.btn_signup) Button _signupButton;
    @Bind(R.id.link_login) TextView _loginLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        ButterKnife.bind(this);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }



    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        String username = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String password2 = _password2Text.getText().toString();
        
        username = username.replaceAll("\\s", "");
        signupFirebase(email, password);

    }

    private void signupFirebase(final String email, final String password) {
        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creazione Account...");
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
                            _emailText.setError(null);
                            String username = email.substring(0, email.indexOf('@'));
                            Log.d("BOHMAH", "createUserWithEmail:success");
                            MyUser myUser = new MyUser(username, email);
                            FirebaseUser user = mAuth.getCurrentUser();

                            DatabaseReference dbRefUsers = FirebaseDatabase.getInstance().getReference("users");
                            dbRefUsers.child(user.getUid()).setValue(myUser);

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
                            onSignupFailed();
                        }
                    }
                });
    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Registrazione fallita", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        //String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String password2 = _password2Text.getText().toString();

//        if (name.isEmpty() || name.length() < 3) {
//            _nameText.setError("almeno 3 caratteri");
//            valid = false;
//        } else {
//            _nameText.setError(null);
//        }


        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("inserisci una email valida");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4) {
            _passwordText.setError("almeno 4 caratteri");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (password2.isEmpty() || password2.length() < 4 || !(password2.equals(password))) {
            _password2Text.setError("Password non combaciano");
            valid = false;
        } else {
            _password2Text.setError(null);
        }

        return valid;
    }
}