package tori.studygroups.mainActivities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;

import java.io.File;

import tori.studygroups.R;
import tori.studygroups.otherClass.Disconnection;
import tori.studygroups.utils.PreferenceUtils;

public class SettingsActivity extends AppCompatActivity {

    private static final int SELECT_PICTURE = 50;
    private Button changeMailButton;
    private Button changePasswordButton;
    private Button changeProfileImageButton;

    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = FirebaseAuth.getInstance().getCurrentUser();

        setContentView(R.layout.activity_settings);
        changeMailButton = (Button) findViewById(R.id.settings_button_change_mail);
        changePasswordButton = (Button) findViewById(R.id.settings_button_change_password);
        changeProfileImageButton = (Button) findViewById(R.id.settings_button_change_profile_image);

        setupButtons();

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
                return true;

            case R.id.menu_home:
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent);
                return true;

            case R.id.menu_general_item_disconnect:
                Disconnection.disconnect(this);
                return true;

            case R.id.menu_general_about:
                Intent intent2 = new Intent(SettingsActivity.this, AboutActivity.class);
                startActivity(intent2);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE && data != null) {
                new AlertDialog.Builder(SettingsActivity.this)
                    .setTitle("Conferma upload")
                    .setMessage("Vuoi caricare l'immagine?")
                    .setIcon(R.drawable.ic_file_upload)
                    .setPositiveButton(R.string.upload, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == DialogInterface.BUTTON_POSITIVE) {
                                uploadImage(data);
                            }
                        }
                    })
                    .setNegativeButton(R.string.cancel, null).show();
            }
        }
    }

    private void setupButtons() {
        changeMailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            LayoutInflater inflater = getLayoutInflater();
            final Dialog dialog = new Dialog(SettingsActivity.this);
            dialog.setTitle(R.string.change_mail);
            dialog.setContentView(inflater.inflate(R.layout.dialog_change_mail, null));
            dialog.setCancelable(true);

            Button change_button = (Button) dialog.findViewById(R.id.dialog_confirm_button);
            Button cancel_button = (Button) dialog.findViewById(R.id.dialog_cancel_button);

            cancel_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            change_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeMail(dialog);
                }
            });


            dialog.show();
            }
        });

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            LayoutInflater inflater = getLayoutInflater();
            final Dialog dialog = new Dialog(SettingsActivity.this);
            dialog.setTitle(R.string.change_password);
            dialog.setContentView(inflater.inflate(R.layout.dialog_change_password, null));
            dialog.setCancelable(true);

            Button change_button = (Button) dialog.findViewById(R.id.dialog_confirm_button);
            Button cancel_button = (Button) dialog.findViewById(R.id.dialog_cancel_button);

            cancel_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            change_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changePassword(dialog);
                }
            });
            dialog.show();
            }
        });

        changeProfileImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);

            }
        });
    }


    private void changeMail(final Dialog dialog) {

        EditText oldMailEditText = (EditText) dialog.findViewById(R.id.settings_input_oldEmail);
        final EditText passwordEditText = (EditText) dialog.findViewById(R.id.settings_input_password_newMail);
        EditText newMailEditText = (EditText) dialog.findViewById(R.id.settings_input_newEmail);

        final String newEmail = newMailEditText.getText().toString().trim();
        String oldEmail = oldMailEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();

        if (oldEmail.isEmpty()){
            oldMailEditText.setError("Inserire vecchia Email");
            oldMailEditText.requestFocus();

        } else if (password.isEmpty()){
            passwordEditText.setError("Inserire password");
            passwordEditText.requestFocus();

        } else if (newEmail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()){
            newMailEditText.setError("Inserire Email valida");
            newMailEditText.requestFocus();

        } else {
            AuthCredential credential = EmailAuthProvider.getCredential(oldEmail, password);

            user.reauthenticate(credential).addOnSuccessListener(new OnSuccessListener<Void>() {

                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("BOH", "User re-authenticated.");
                    changeMailDb(newEmail);
                    Toast.makeText(SettingsActivity.this, "Email cambiata", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("BOH", e.getMessage());
                    passwordEditText.setError("Combinazione password email non valida");
                    passwordEditText.requestFocus();
                }
            });
        }
    }

    private void changeMailDb(final String newEmail) {
        user.updateEmail(newEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                DatabaseReference dbRefUsers = FirebaseDatabase.getInstance().getReference("users");
                dbRefUsers.child(user.getUid()).child("email").setValue(newEmail);
                PreferenceUtils.setEmail(SettingsActivity.this, newEmail);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SettingsActivity.this, "Error", Toast.LENGTH_LONG).show();
            }
        });
    }


    private void changePassword(final Dialog dialog) {

        EditText oldMailEditText = (EditText) dialog.findViewById(R.id.settings_input_oldEmail);
        final EditText passwordEditText = (EditText) dialog.findViewById(R.id.settings_input_oldPassword);
        EditText newPasswordEditText = (EditText) dialog.findViewById(R.id.settings_input_newPassword);
        EditText newPasswordRepeatEditText = (EditText) dialog.findViewById(R.id.settings_input_newPasswordRepeat);
        final String newPassword = newPasswordEditText.getText().toString().trim();
        final String oldEmail = oldMailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String newPassword2 = newPasswordRepeatEditText.getText().toString().trim();

        if (oldEmail.isEmpty()){
            oldMailEditText.setError("Inserire vecchia Email");
            oldMailEditText.requestFocus();

        } else if (password.isEmpty()){
            passwordEditText.setError("Inserire vecchia password");
            passwordEditText.requestFocus();
        }
        else if (newPassword.isEmpty() || newPassword.length() < 6){
            newPasswordEditText.setError("Password deve essere di almeno 6 caratteri");
            newPasswordEditText.requestFocus();

        } else if (! newPassword.equals(newPassword2) ){
            newPasswordRepeatEditText.setError("Nuova password non combacia");
            newPasswordRepeatEditText.requestFocus();

        } else {
            AuthCredential credential = EmailAuthProvider.getCredential(oldEmail, password);

            user.reauthenticate(credential).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                Log.d("BOH", "User re-authenticated.");
                Log.d("BOHnewPASSRODREADreauth", newPassword);
                user.updatePassword(newPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("BOH", "passCambiata");
                        PreferenceUtils.setPassword(SettingsActivity.this, newPassword);
                        Toast.makeText(SettingsActivity.this, "Password cambiata", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("BOHERORR", e.getMessage());
                        Toast.makeText(SettingsActivity.this, "Error", Toast.LENGTH_LONG).show();
                    }
                });
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("BOH", e.getMessage());
                    passwordEditText.setError("Combinazione password email non valida");
                    passwordEditText.requestFocus();
                }
            });
        }
    }


    private void uploadImage(Intent data) {

        final ProgressDialog progressDialog = new ProgressDialog(SettingsActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Caricamento immagine...");
        progressDialog.show();

        File img = new File(data.getData().getPath());

        if (img.length() > 1024*500) { //500 kb max
            Toast.makeText(SettingsActivity.this, "Immagine troppo grande", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();

        } else {
            StorageReference stRefUser = FirebaseStorage.getInstance().getReference().child(user.getUid());
            stRefUser.child(data.getData().getPath());

            UploadTask uploadTask = stRefUser.putFile(data.getData());

            // Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.wtf("BOH", "upload to storage failed");
                    Toast.makeText(SettingsActivity.this, "Errore di caricamento immagine profilo", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            });

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Log.wtf("BOH", "upload to storage success");
                    final Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    Log.d("BOH", downloadUrl.toString());

                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setPhotoUri(downloadUrl).build();

                    user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {

                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("BOH", "User profile updated.");
                                Log.d("BOH", user.getPhotoUrl().toString());
                                SendBird.updateCurrentUserInfo(user.getDisplayName(), downloadUrl.toString(), new SendBird.UserInfoUpdateHandler() {
                                    @Override
                                    public void onUpdated(SendBirdException e) {
                                        if (e == null) {
                                            Log.d("BOH", "image updated");
                                            Toast.makeText(SettingsActivity.this, "Immagine profilo modificata con successo", Toast.LENGTH_LONG).show();
                                            progressDialog.dismiss();
                                        } else {
                                            Log.d("BOH", e.getMessage());
                                            Toast.makeText(SettingsActivity.this, "Errore di caricamento immagine profilo", Toast.LENGTH_LONG).show();
                                            progressDialog.dismiss();
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            });
        }
    }
}