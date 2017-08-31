package tori.studygroups.channels;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tori.studygroups.R;
import tori.studygroups.otherClass.MyEvent;

import static tori.studygroups.channels.ChatFragment.EXTRA_CHANNEL_URL;

//import tori.studygroups.otherClass.EventDB;


public class CreateEventActivity extends AppCompatActivity {

    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1 ;
    private ProgressDialog progressDialog;

    private String channelUrl;
    private String channelName;
    private TextView addEventTitle;
    private EditText nameEventText;
    private EditText locationEventText;
    private TextView dateEventText;
    private TextView timeEventText;
    private EditText descriptionEventText;
    private Button createEventButton;
    private Calendar calendarDateEvent;
    private TextView linkToSawTextView;

    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private SimpleDateFormat dateFormatter;
    private SimpleDateFormat timeFormatter;

    private Pattern pattern = Pattern.compile("^[^\\.#\\$\\[\\]]+$");

    private Activity thisActivity;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        thisActivity = this;
        Intent intent = getIntent();
        channelUrl = intent.getStringExtra(EXTRA_CHANNEL_URL);
        channelName = intent.getStringExtra("channelName");

        setContentView(R.layout.activity_create_event);

        progressDialog = new ProgressDialog(CreateEventActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creazione evento...");

        addEventTitle = (TextView) findViewById((R.id.add_event_title));
        addEventTitle.append(" " + channelName);
        nameEventText = (EditText) findViewById(R.id.edittext_add_event_name);
        descriptionEventText = (EditText) findViewById(R.id.edittext_add_event_description);
        locationEventText = (EditText) findViewById(R.id.edittext_add_event_location);
        locationEventText.setInputType(InputType.TYPE_NULL);
        dateEventText = (TextView) findViewById(R.id.edittext_add_event_date);
        dateEventText.setInputType(InputType.TYPE_NULL);
        timeEventText = (TextView) findViewById(R.id.edittext_add_event_time);
        timeEventText.setInputType(InputType.TYPE_NULL);
        createEventButton = (Button) findViewById(R.id.button_create_event);
        linkToSawTextView = (TextView) findViewById(R.id.link_to_saw_textview);

        linkToSawTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://webdev.dibris.unige.it/~S4119809/SAW2017/map.php"));
                startActivity(i);
            }
        });

        dateFormatter = new SimpleDateFormat("EEEE, dd-MMMM-yyyy", Locale.ITALY);
        timeFormatter = new SimpleDateFormat("HH:mm", Locale.ITALY);

        calendarDateEvent = Calendar.getInstance();

        setLocationPlaceGoogleApi();
        setDialogDate();
        setDialogTime();

        setCreateButton();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i("MAH", "Place: " + place.getAddress());
                locationEventText.setText(place.getAddress());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i("MAH", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
            locationEventText.setEnabled(true);
            locationEventText.setClickable(true);
        }
    }


    private void setLocationPlaceGoogleApi() {

        locationEventText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationEventText.setEnabled(false);
                locationEventText.setClickable(false);
                Intent intent = null;
                try {
                    intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .build(thisActivity);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
            }
        });
    }


    private void setDialogDate() {

        dateEventText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });
        Calendar calendar = Calendar.getInstance();

        OnDateSetListener dateSetListener = new OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                calendarDateEvent.set(year, monthOfYear, dayOfMonth);
                dateEventText.setText(dateFormatter.format(calendarDateEvent.getTime()));
            }

        };

        datePickerDialog = new DatePickerDialog(this, dateSetListener , calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
    }

    private void setDialogTime() {

        timeEventText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog.show();
            }
        });

        Calendar calendar = Calendar.getInstance();

        OnTimeSetListener timeSetListener = new OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay , int minute) {

                calendarDateEvent.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendarDateEvent.set(Calendar.MINUTE, minute);
                timeEventText.setText(timeFormatter.format(calendarDateEvent.getTime()));
            }
        };

        timePickerDialog = new TimePickerDialog(this, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), true);
    }


    private void setCreateButton() {

        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (! validate()){
                    createEventButton.setEnabled(true);
                    nameEventText.setEnabled(true);
                    locationEventText.setEnabled(true);
                    dateEventText.setEnabled(true);
                    timeEventText.setEnabled(true);
                    return;
                }

                createEventButton.setEnabled(false);
                nameEventText.setEnabled(false);
                locationEventText.setEnabled(false);
                dateEventText.setEnabled(false);
                timeEventText.setEnabled(false);

                progressDialog.show();
                MyEvent eventCreated = createEvent();
            }
        });

    }


    private boolean validate() {
        boolean valid = true;
        String eventName = nameEventText.getText().toString().trim();
        Matcher nameMatcher = pattern.matcher(eventName);
        String eventDescr = descriptionEventText.getText().toString().trim();
        Matcher descMatcher = pattern.matcher(eventDescr);

        if (eventName.isEmpty()) {
            nameEventText.setError("inserisci un nome valido");
            valid = false;
        } else if (! nameMatcher.matches()){
            nameEventText.setError("Nome evento non può contenere . $ # [ ] ");
            valid = false;
        } else {
            nameEventText.setError(null);
        }

        if (eventDescr.isEmpty()){
            descriptionEventText.setError(null);
        } else if (! descMatcher.matches()){
            descriptionEventText.setError("Non è possibile usare i caratteri . $ # [ ]");
            valid = false;
        } else {
            descriptionEventText.setError(null);
        }

        if (locationEventText.getText().toString().isEmpty()) {
            locationEventText.setError("");
            Toast.makeText(CreateEventActivity.this, "Inserisci un luogo", Toast.LENGTH_LONG).show();
            valid = false;
        } else {
            locationEventText.setError(null);
        }

        if (dateEventText.getText().toString().isEmpty()) {
            dateEventText.setError("");
            Toast.makeText(CreateEventActivity.this, "Inserisci una data", Toast.LENGTH_LONG).show();
            valid = false;
        } else {
            dateEventText.setError(null);
        }

        if (timeEventText.getText().toString().isEmpty()) {
            timeEventText.setError("");
            Toast.makeText(CreateEventActivity.this, "Inserisci un orario", Toast.LENGTH_LONG).show();
            valid = false;
        } else {
            timeEventText.setError(null);
        }

        if (calendarDateEvent.before(Calendar.getInstance())) {
            dateEventText.setError("");
            Toast.makeText(CreateEventActivity.this, "Non puoi inserire una data passata", Toast.LENGTH_LONG).show();
            valid = false;
        } else {
            dateEventText.setError(null);
        }

        return valid;
    }


    private MyEvent createEvent() {

        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        Calendar cal = Calendar.getInstance();
        long now = cal.getTimeInMillis();
        long timestampDateEvent = calendarDateEvent.getTimeInMillis();

        final MyEvent event = new MyEvent(nameEventText.getText().toString().trim(), descriptionEventText.getText().toString().trim(),
                locationEventText.getText().toString(), timestampDateEvent, dateEventText.getText().toString(),
                timeEventText.getText().toString(), user.getUid(), user.getDisplayName(),
                channelUrl, channelName, now, null);

        DatabaseReference dbRefEvents = FirebaseDatabase.getInstance().getReference("channelEvents")
                .child(channelUrl).push();
        dbRefEvents.setValue(event);
        String eventId = dbRefEvents.getKey();
        event.setEventId(eventId);

        DatabaseReference dbRefEventPartecipants = FirebaseDatabase.getInstance().getReference("eventPartecipant");
        dbRefEventPartecipants.child(eventId).child(user.getUid()).setValue("true");

        DatabaseReference dbRefUserEvents = FirebaseDatabase.getInstance().getReference("userEvents");
        dbRefUserEvents.child(user.getUid()).child(eventId).setValue(event);

        //local db
//        EventDB localDB = new EventDB(this);
//        String insertId = localDB.insertEvent(event);
//        if (insertId != null){
//             Log.d("MAHHHH", "riga inserita in locale");
//        }

        progressDialog.dismiss();

        new AlertDialog.Builder(this)
                .setTitle("Calendario")
                .setMessage("Vuoi inserire l'evento nel calendario?")
                .setIcon(R.drawable.ic_add_event)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        if (event != null) {
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("eventAdded", event);
                            resultIntent.putExtra("calendar", true);
                            setResult(Activity.RESULT_OK, resultIntent);
                        } else{
                            setResult(Activity.RESULT_CANCELED);
                        }

                        finish();

                    }
                })

                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (event != null) {
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("eventAdded", event);
                            resultIntent.putExtra("calendar", false);
                            setResult(Activity.RESULT_OK, resultIntent);
                        } else{
                            setResult(Activity.RESULT_CANCELED);
                        }

                        finish();

                    }
                })
                .show();

        return event;
    }
}

