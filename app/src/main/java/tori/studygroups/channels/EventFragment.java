package tori.studygroups.channels;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.sendbird.android.BaseChannel;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.SendBird;

import org.w3c.dom.Text;

import tori.studygroups.R;
import tori.studygroups.otherClass.MyEvent;

public class EventFragment extends Fragment{

    public static final String JSONDATAEVENT = "jsonDataEvent";

    private String eventDataJson;
    private TextView eventNameText;
    private TextView eventGroupText;
    private TextView eventDayText;
    private TextView eventLocationText;
    private Button eventViewPartecipantsButton;
    private Button eventPartecipaConfirmButton;

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

        eventDataJson = getArguments().getString(JSONDATAEVENT);
        Log.d("MAH", eventDataJson);

        eventNameText = (TextView) rootView.findViewById(R.id.event_name_text);
        eventGroupText = (TextView) rootView.findViewById(R.id.event_group_text);
        eventDayText = (TextView) rootView.findViewById(R.id.event_day_text);
        eventLocationText = (TextView) rootView.findViewById(R.id.event_location_text);
        eventViewPartecipantsButton = (Button) rootView.findViewById(R.id.btn_event_view_partecipants);
        eventPartecipaConfirmButton = (Button) rootView.findViewById(R.id.btn_event_partecipa_confirm);

        setUpPage();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void setUpPage() {


    }


}
