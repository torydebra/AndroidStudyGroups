package tori.studygroups.channels;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sendbird.android.User;
import tori.studygroups.R;
import tori.studygroups.otherClass.MyEvent;
import tori.studygroups.utils.ImageUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple adapter that displays a list of Users.
 * usato sia per veder lista partecipanti chat e per lista partecipanti evento
 */
public class EventListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<MyEvent> eventList;

    public EventListAdapter(Context context) {
        mContext = context;
        eventList = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_event, parent, false);
        return new EventHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((EventHolder) holder).bind(mContext, eventList.get(position));
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public void setEventList(List<MyEvent> eventList) {
        eventList.addAll(eventList);
        notifyDataSetChanged();
    }

    private class EventHolder extends RecyclerView.ViewHolder {
        private TextView nameText;
        private ImageView profileImage;

        public EventHolder(View itemView) {
            super(itemView);

            nameText = (TextView) itemView.findViewById(R.id.text_user_list_nickname);
            profileImage = (ImageView) itemView.findViewById(R.id.image_user_list_profile);
        }


        private void bind(final Context context, final User user) {
            nameText.setText(user.getNickname());
        }
    }
}
