package tori.studygroups.channels;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import org.json.JSONException;
import org.json.JSONObject;


import java.util.Calendar;


import tori.studygroups.R;

public class EventFragment extends Fragment{

    public static final String JSONDATAEVENT = "jsonDataEvent";
    public static final String EVENT_ID = "eventId";

    private String eventDataString;
    private JSONObject eventDataJson;
    private TextView eventNameText;
    private TextView eventGroupText;
    private TextView eventCreatorText;
    private TextView eventDayText;
    private TextView eventLocationText;
    private Button eventViewPartecipantsButton;
    private Button eventPartecipaConfirmButton;
    private Button eventPartecipaDeleteButton;

    private String creatorName;
    private String eventId;
    private FirebaseUser user;
    private DatabaseReference dbRefEventPartecipants;

    public static EventFragment newInstance(@NonNull String eventDataJson) {
        EventFragment fragment = new EventFragment();

        Bundle args = new Bundle();
        args.putString(JSONDATAEVENT, eventDataJson);
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_event, container, false);

        setRetainInstance(true);
        setHasOptionsMenu(true);

        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        dbRefEventPartecipants = FirebaseDatabase.getInstance().getReference("eventPartecipant");

        eventDataString = getArguments().getString(JSONDATAEVENT);
        //Log.d("MAH", eventDataString);
        eventDataJson = null;
        try {
            eventDataJson = new JSONObject(eventDataString).getJSONObject("event");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        eventNameText = (TextView) rootView.findViewById(R.id.event_name_text);
        eventGroupText = (TextView) rootView.findViewById(R.id.event_group_text);
        eventCreatorText = (TextView) rootView.findViewById(R.id.event_creator_text);
        eventDayText = (TextView) rootView.findViewById(R.id.event_day_text);
        eventLocationText = (TextView) rootView.findViewById(R.id.event_location_text);
        eventViewPartecipantsButton = (Button) rootView.findViewById(R.id.btn_event_view_partecipants);
        eventPartecipaConfirmButton = (Button) rootView.findViewById(R.id.btn_event_partecipa_confirm);
        eventPartecipaDeleteButton = (Button) rootView.findViewById(R.id.btn_event_partecipa_delete);

        try {
            eventId = eventDataJson.getString("eventId");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        String date = null;
        try {
            date = eventDataJson.getString("timestampDateEvent");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //evento passato
        if ((Calendar.getInstance().getTimeInMillis()) >= Long.parseLong(date)){
            eventPartecipaDeleteButton.setEnabled(false);
            eventPartecipaConfirmButton.setEnabled(false);
            eventPartecipaDeleteButton.setVisibility(View.GONE);
            eventPartecipaConfirmButton.setVisibility(View.GONE);

        } else {
            checkPartecipationFirebase();
        }

        setUpPage();
        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
    }


    private void checkPartecipationFirebase() {

        dbRefEventPartecipants.child(eventId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(user.getUid())){
                    eventPartecipaDeleteButton.setEnabled(true);
                    eventPartecipaConfirmButton.setEnabled(false);
                    eventPartecipaDeleteButton.setVisibility(View.VISIBLE);
                    eventPartecipaConfirmButton.setVisibility(View.GONE);
                } else {
                    eventPartecipaDeleteButton.setEnabled(false);
                    eventPartecipaConfirmButton.setEnabled(true);
                    eventPartecipaDeleteButton.setVisibility(View.GONE);
                    eventPartecipaConfirmButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    private void setUpPage() {

        try {
            eventNameText.setText(eventDataJson.getString("name"));
            eventGroupText.setText("Evento del gruppo " + eventDataJson.getString("channelName"));
            eventCreatorText.setText("Creato da: " + eventDataJson.getString("userName"));
            eventDayText.setText(eventDataJson.getString("day") +
                    " alle ore: " + eventDataJson.getString("time"));
            eventLocationText.setText(eventDataJson.getString("location"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        eventViewPartecipantsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EventPartecipantListActivity.class);
                intent.putExtra(EVENT_ID, eventId);
                startActivity(intent);
            }
        });

        eventPartecipaConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Conferma")
                        .setMessage("Vuoi partecipare all'evento?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                partecipa();
                            }})

                        .setNegativeButton(android.R.string.no, null).show();
            }
        });

        eventPartecipaDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Annulla partecipazione")
                        .setMessage("Sei sicuro di non voler più partecipare all'evento?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                cancelPartecipazione();
                            }})

                        .setNegativeButton(android.R.string.no, null).show();

            }
        });


    }


    private void partecipa() {

        dbRefEventPartecipants.child(eventId).child(user.getUid()).setValue("true");

        Toast t = Toast.makeText(getContext(), "Ora partecipi all'evento", Toast.LENGTH_LONG);
        t.show();

        eventPartecipaDeleteButton.setEnabled(true);
        eventPartecipaConfirmButton.setEnabled(false);
        eventPartecipaDeleteButton.setVisibility(View.VISIBLE);
        eventPartecipaConfirmButton.setVisibility(View.GONE);


    }


    private void cancelPartecipazione() {

        dbRefEventPartecipants.child(eventId).child(user.getUid()).getRef().removeValue();

        Toast t = Toast.makeText(getContext(), "Non partecipi più all'evento", Toast.LENGTH_LONG);
        t.show();

        eventPartecipaDeleteButton.setEnabled(false);
        eventPartecipaConfirmButton.setEnabled(true);
        eventPartecipaDeleteButton.setVisibility(View.GONE);
        eventPartecipaConfirmButton.setVisibility(View.VISIBLE);



    }



}
