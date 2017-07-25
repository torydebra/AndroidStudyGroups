package tori.studygroups.channels;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.UserMessage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import tori.studygroups.R;
import tori.studygroups.mainActivities.LoginActivity;
import tori.studygroups.otherClass.MyEvent;

import static tori.studygroups.channels.ChatFragment.EXTRA_CHANNEL_URL;


public class AddEventActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;

    private String channelUrl;
    private String channelName;
    private TextView addEventTitle;
    private EditText nameEventText;
    private EditText locationEventText;
    private TextView dateEventText;
    private TextView timeEventText;
    private Button createEventButton;
    private Calendar calendarDateEvent;

    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private SimpleDateFormat dateFormatter;
    private SimpleDateFormat timeFormatter;


    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        channelUrl = intent.getStringExtra(EXTRA_CHANNEL_URL);
        channelName = intent.getStringExtra("channelName");

        setContentView(R.layout.activity_add_event);

        progressDialog = new ProgressDialog(AddEventActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creazione evento...");

        addEventTitle = (TextView) findViewById((R.id.add_event_title));
        addEventTitle.append(" " + channelName);
        nameEventText = (EditText) findViewById(R.id.edittext_add_event_name);
        locationEventText = (EditText) findViewById(R.id.edittext_add_event_location);
        dateEventText = (TextView) findViewById(R.id.edittext_add_event_date);
        dateEventText.setInputType(InputType.TYPE_NULL);
        timeEventText = (TextView) findViewById(R.id.edittext_add_event_time);
        timeEventText.setInputType(InputType.TYPE_NULL);
        createEventButton = (Button) findViewById(R.id.button_create_event);

        dateFormatter = new SimpleDateFormat("EEEE, dd-MMMM-yyyy", Locale.ITALY);
        timeFormatter = new SimpleDateFormat("HH:mm", Locale.ITALY);

        calendarDateEvent = Calendar.getInstance();

        setDialogDate();
        setDialogTime();

        setCreateButton();


    }



    private void setDialogDate() {

//        dateEventText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                datePickerDialog.show();
//            }
//        });
        dateEventText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                datePickerDialog.show();
                return false;
            }

        });


        Calendar calendar = Calendar.getInstance();

        OnDateSetListener dateSetListener = new OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                //Calendar newDate = Calendar.getInstance();
                //newDate.set(year, monthOfYear, dayOfMonth);
                calendarDateEvent.set(year, monthOfYear, dayOfMonth);
                //dateEventText.setText(dateFormatter.format(newDate.getTime()));
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
//                Calendar newTime = Calendar.getInstance();
//                newTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
//                newTime.set(Calendar.MINUTE, minute);
//                timeEventText.setText(timeFormatter.format(newTime.getTime()));
                calendarDateEvent.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendarDateEvent.set(Calendar.MINUTE, minute);
                timeEventText.setText(timeFormatter.format(calendarDateEvent.getTime()));
            }
        };


        timePickerDialog = new TimePickerDialog(this, timeSetListener, calendar.get(Calendar.HOUR),
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
                progressDialog.dismiss();

                if (eventCreated != null) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("eventAdded", eventCreated);
                    setResult(Activity.RESULT_OK, resultIntent);
                } else{
                    setResult(Activity.RESULT_CANCELED);
                }
                finish();

            }
        });

    }



    private boolean validate() {
        boolean valid = true;

        if (nameEventText.getText().toString().isEmpty()) {
            nameEventText.setError("inserisci un nome valido");
            valid = false;
        } else {
            nameEventText.setError(null);
        }

        if (locationEventText.getText().toString().isEmpty()) {
            locationEventText.setError("inserisci un luogo");
            valid = false;
        } else {
            locationEventText.setError(null);
        }

        if (dateEventText.getText().toString().isEmpty()) {
            dateEventText.setError("inserisci una data");
            valid = false;
        } else {
            dateEventText.setError(null);
        }

        if (timeEventText.getText().toString().isEmpty()) {
            timeEventText.setError("inserisci un orario");
            valid = false;
        } else {
            timeEventText.setError(null);
        }

        if (calendarDateEvent.before(Calendar.getInstance())) {
            dateEventText.setError("non puoi inserire una data passata");
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

        dateFormatter = new SimpleDateFormat("dd-M-yyyy--HH-mm-ss", Locale.ITALY);
        Calendar cal = Calendar.getInstance();
        String now = dateFormatter.format(cal.getTime());

        MyEvent event = new MyEvent(nameEventText.getText().toString(), locationEventText.getText().toString(),
                dateEventText.getText().toString(), timeEventText.getText().toString(),
                user.getUid(), user.getDisplayName(), channelUrl, channelName, now, null);

        DatabaseReference dbRefEvents = FirebaseDatabase.getInstance().getReference("events").push();
        dbRefEvents.setValue(event);
        String eventId = dbRefEvents.getKey();
        event.setEventId(eventId);

        DatabaseReference dbRefEventPartecipants = FirebaseDatabase.getInstance().getReference("eventPartecipant");
        dbRefEventPartecipants.child(eventId);
        dbRefEventPartecipants.child(eventId).child(user.getUid()).setValue("true");

        return event;

    }


}

