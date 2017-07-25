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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.SendBird;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Map;

import tori.studygroups.R;
import tori.studygroups.mainActivities.LoginActivity;
import tori.studygroups.otherClass.MyEvent;
import tori.studygroups.otherClass.MyUser;

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

    private String creatorName;
    private String eventId;

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

        eventDataString = getArguments().getString(JSONDATAEVENT);
        Log.d("MAH", eventDataString);
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

        try {
            eventId = eventDataJson.getString("eventId");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        setUpPage();


        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
    }


    private void setUpPage() {

        try {
            eventNameText.setText(eventDataJson.getString("name"));
            eventGroupText.setText("Evento del gruppo " + eventDataJson.getString("channelName"));
            eventCreatorText.setText("Creato da: " + eventDataJson.getString("userName"));
            eventDayText.setText("data: " + eventDataJson.getString("day") +
                    " alle ore: " + eventDataJson.getString("time"));
            eventLocationText.setText("Luogo: " + eventDataJson.getString("location"));
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


    }

    private void partecipa() {

        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        DatabaseReference dbRefEventPartecipants = FirebaseDatabase.getInstance().getReference("eventPartecipant");
        dbRefEventPartecipants.child(eventId);
        dbRefEventPartecipants.child(eventId).child(user.getUid()).setValue("true");


    }


}
