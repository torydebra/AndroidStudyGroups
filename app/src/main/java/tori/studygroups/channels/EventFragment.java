package tori.studygroups.channels;

import android.R.color;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
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
import com.sendbird.android.BaseChannel;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.OpenChannel;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.UserMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import tori.studygroups.R;
import tori.studygroups.otherClass.EventDB;
import tori.studygroups.otherClass.MyEvent;

public class EventFragment extends Fragment {

    public static final String JSONDATAEVENT = "jsonDataEvent";
    public static final String EVENT_ID = "eventId";
    private static final long CALENDAR_ID = 1;

    private String eventDataString;
    private JSONObject eventDataJson;
    private TextView eventNameText;
    private TextView eventGroupText;
    private TextView eventCreatorText;
    private TextView eventDayText;
    private TextView eventLocationText;
    private Button eventViewPartecipantsButton;
    private Button eventPartecipaButton;
    private boolean eventPartecipaBool;
    private Button eventViewMaps;

    private String eventId;
    private FirebaseUser user;
    private DatabaseReference dbRefEventPartecipants;

    private MyEvent event;

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

        try {
            event = new MyEvent(
                    eventDataJson.getString("name"),
                    eventDataJson.getString("location"),
                    eventDataJson.getLong("timestampDateEvent"),
                    eventDataJson.getString("day"),
                    eventDataJson.getString("time"),
                    eventDataJson.getString("userId"),
                    eventDataJson.getString("userName"),
                    eventDataJson.getString("channelUrl"),
                    eventDataJson.getString("channelName"),
                    eventDataJson.getLong("timestampCreated"),
                    eventDataJson.getString("eventId")
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("MAHHHisneritot", event.toJsonString());

        eventNameText = (TextView) rootView.findViewById(R.id.event_name_text);
        eventGroupText = (TextView) rootView.findViewById(R.id.event_group_text);
        eventCreatorText = (TextView) rootView.findViewById(R.id.event_creator_text);
        eventDayText = (TextView) rootView.findViewById(R.id.event_day_text);
        eventLocationText = (TextView) rootView.findViewById(R.id.event_location_text);
        eventViewPartecipantsButton = (Button) rootView.findViewById(R.id.btn_event_view_partecipants);
        eventViewMaps = (Button) rootView.findViewById(R.id.btn_event_view_maps);
        eventPartecipaButton = (Button) rootView.findViewById(R.id.btn_event_partecipa);


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
        if ((Calendar.getInstance().getTimeInMillis()) >= Long.parseLong(date)) {
            eventPartecipaButton.setEnabled(false);
            eventPartecipaButton.setVisibility(View.GONE);

        } else {
            checkPartecipationFirebase();
        }

        setUpPage();
        return rootView;
    }




    private void checkPartecipationFirebase() {

        dbRefEventPartecipants.child(eventId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(user.getUid())) {
                    eventPartecipaButton.setText(R.string.partecipa_delete);
                    eventPartecipaButton.setBackgroundColor(ResourcesCompat.getColor(getResources(), color.holo_red_light, null));
                    eventPartecipaBool = true;
                } else {
                    eventPartecipaButton.setText(R.string.partecipa_confirmation);
                    eventPartecipaButton.setBackgroundColor(ResourcesCompat.getColor(getResources(), color.holo_green_light, null));
                    eventPartecipaBool = false;
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

        eventPartecipaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (eventPartecipaBool) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Annulla partecipazione")
                            .setMessage("Sei sicuro di non voler più partecipare all'evento?")
                            .setIcon(R.drawable.ic_delete_forever)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    cancelPartecipazione();
                                }
                            })

                            .setNegativeButton(android.R.string.no, null).show();
                } else {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Conferma")
                            .setMessage("Vuoi partecipare all'evento?")
                            .setIcon(R.drawable.ic_person_add)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    partecipa();
                                }
                            })

                            .setNegativeButton(android.R.string.no, null).show();


                }
            }
        });

        eventViewMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri gmmIntentUri = null;
                try {
                    gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(eventDataJson.getString("location")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);


            }
        });
    }


    private void partecipa() {

        dbRefEventPartecipants.child(eventId).child(user.getUid()).setValue("true");

        DatabaseReference dbRefUserEvents = FirebaseDatabase.getInstance().getReference("userEvents");
        dbRefUserEvents.child(user.getUid()).child(eventId).setValue(event);

        Toast t = Toast.makeText(getContext(), "Ora partecipi all'evento", Toast.LENGTH_LONG);
        t.show();

        eventPartecipaButton.setText(R.string.partecipa_delete);
        eventPartecipaButton.setBackgroundColor(ResourcesCompat.getColor(getResources(), color.holo_red_light, null));
        eventPartecipaBool = true;

        EventDB localDB = new EventDB(getContext());
        String insertId = localDB.insertEvent(event);
//        if (insertId != null) {
//            // Log.d("MAHHHH", "riga inserita in locale");
//        }

        ArrayList<MyEvent> events = localDB.getEvents();
        for (MyEvent ev : events) {
            Log.d("MAHHEVETN", ev.toJsonString());
        }

        new AlertDialog.Builder(getContext())
            .setTitle("Calendario")
            .setMessage("Vuoi inserire l'evento nel calendario?")
            .setIcon(R.drawable.ic_add_event)
            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int whichButton) {

                Intent intent = new Intent(Intent.ACTION_INSERT)
                    .setData(CalendarContract.Events.CONTENT_URI)
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, event.timestampDateEvent)
                    .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, event.timestampDateEvent + 3*60*60*1000)
                    .putExtra(CalendarContract.Events.TITLE, event.name)
                    .putExtra(CalendarContract.Events.DESCRIPTION, "evento creato con l'app StudyGroups")
                    .putExtra(CalendarContract.Events.ORGANIZER, event.userName)
                    .putExtra(CalendarContract.Attendees.EVENT_ID, event.timestampDateEvent)
                    .putExtra(CalendarContract.Events.EVENT_LOCATION, event.location);
                startActivity(intent);

                }
            })
            .setNegativeButton(R.string.no, null).show();

    }


    private void cancelPartecipazione() {

        DatabaseReference dbRefSpecifiedEvent = dbRefEventPartecipants.child(eventId);
        dbRefSpecifiedEvent.child(user.getUid()).getRef().removeValue();

        DatabaseReference dbRefUserEvents = FirebaseDatabase.getInstance().getReference("userEvents");
        dbRefUserEvents.child(user.getUid()).child(eventId).getRef().removeValue();

        Toast t = Toast.makeText(getContext(), "Non partecipi più all'evento, ricordati di cancellare l'evento dal tuo calendario", Toast.LENGTH_LONG);
        t.show();

        eventPartecipaButton.setText(R.string.partecipa_confirmation);
        eventPartecipaButton.setBackgroundColor(ResourcesCompat.getColor(getResources(), color.holo_green_light, null));
        eventPartecipaBool = false;

        EventDB localDB = new EventDB(getContext());
        int deleteCount = localDB.deleteEvent(event.eventId);
//        if (deleteCount == 1) {
//            Log.d("MAHDELEt", "cancellato");
//        }

        dbRefSpecifiedEvent.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (! dataSnapshot.exists() && event.userId.equals(user.getUid())){
                    Log.d("BOHH", "nex partecipante rimasto e sei il creatore dell'evento");
                    deleteEvent();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void deleteEvent() {
        new AlertDialog.Builder(getContext())
            .setTitle("Cancella Evento")
            .setMessage("Eri l'ultimo partecipante all'evento. Vuoi cancellarlo?")
            .setIcon(android.R.drawable.ic_delete)
            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int whichButton) {
                    FirebaseDatabase.getInstance().getReference("channelEvents").child(event.channelUrl)
                            .child(eventId).removeValue();

                    new FindAndDeleteEventMessage().execute();
                    getActivity().onBackPressed();

                }
            })
            .setNegativeButton(R.string.no, null).show();

    }

    private class FindAndDeleteEventMessage extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            OpenChannel.getChannel(event.channelUrl, new OpenChannel.OpenChannelGetHandler() {
                @Override
                public void onResult(final OpenChannel openChannel, SendBirdException e) {
                    if (e != null) {
                        // Error!
                        e.printStackTrace();
                        return;
                    }

                    // Enter the channel
                    openChannel.enter(new OpenChannel.OpenChannelEnterHandler() {
                        @Override
                        public void onResult(SendBirdException e) {
                            if (e != null) {
                                // Error!
                                e.printStackTrace();
                                return;
                            }

                            openChannel.getNextMessagesByTimestamp(event.timestampCreated, true, 20,
                                false, BaseChannel.MessageTypeFilter.USER, ChatFragment.CUSTOM_TYPE_MESSAGE_TEXT_EVENT,
                                new BaseChannel.GetMessagesHandler() {

                                @Override
                                public void onResult(List<BaseMessage> list, SendBirdException e) {
                                    if (e != null) {
                                        // Error!
                                        e.printStackTrace();
                                        return;
                                    }

                                    for (BaseMessage message : list) {

                                        String eventIdfound = null;
                                        try {
                                            JSONObject jsonDataEvent = new JSONObject(((UserMessage) message).getData()).getJSONObject("event");
                                            eventIdfound = jsonDataEvent.getString("eventId");
                                        } catch (JSONException e1) {
                                            e1.printStackTrace();
                                        }

                                        if (eventIdfound.equals(eventId)) {
                                            openChannel.deleteMessage(message, new BaseChannel.DeleteMessageHandler() {
                                                @Override
                                                public void onResult(SendBirdException e) {
                                                    if (e != null) {
                                                        // Error!
                                                        Toast.makeText(getActivity(), R.string.error_deleting_message, Toast.LENGTH_SHORT).show();
                                                        return;
                                                    }
                                                }
                                            });

                                            break ;
                                        }


                                    }
                                }
                            });
                        }

                    });
                }
            });
            return null;
        }
    }
}
