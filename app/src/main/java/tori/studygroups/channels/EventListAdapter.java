package tori.studygroups.channels;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import tori.studygroups.R;
import tori.studygroups.otherClass.MyEvent;


public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.EventHolder> {

    private Context mContext;
    private List<MyEvent> eventList;
    private OnItemClickListener mItemClickListener;
    private OnItemLongClickListener mItemLongClickListener;


    interface OnItemClickListener {
        void onItemClick(MyEvent event);
    }

    interface OnItemLongClickListener {
        void onItemLongPress(MyEvent event);
    }



   EventListAdapter(Context context) {
        mContext = context;
        eventList = new ArrayList<>();
    }

    @Override
    public EventListAdapter.EventHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_event, parent, false);
        return new EventHolder(view);
    }

    @Override
    public void onBindViewHolder(EventHolder holder, int position) {
        holder.bind(mContext, eventList.get(position), position, mItemClickListener, mItemLongClickListener);
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    void setOnItemClickListener(EventListAdapter.OnItemClickListener listener) {
        mItemClickListener = listener;
    }

    void setOnItemLongClickListener(EventListAdapter.OnItemLongClickListener listener) {
        mItemLongClickListener = listener;
    }

    void setEventList(List<MyEvent> eventList) {
        (this.eventList).addAll(eventList);
        notifyDataSetChanged();
    }

    void addLast(MyEvent event) {
        eventList.add(event);
        notifyDataSetChanged();
    }

    static class EventHolder extends RecyclerView.ViewHolder {

        private String[] colorList = {"#ff2de3e1", "#ff35a3fb", "#ff805aff", "#ffcf47fb", "#ffe248c3"};
        private TextView nameEventText;
        private TextView locationEventText;
        private TextView dayEventText;
        private TextView timeEventText;
        private ImageView decoratorImage;


        EventHolder(View itemView) {
            super(itemView);

            nameEventText = (TextView) itemView.findViewById(R.id.event_name_list_item);
            locationEventText = (TextView) itemView.findViewById(R.id.event_location_list_item);
            dayEventText = (TextView) itemView.findViewById(R.id.event_date_list_item);
            timeEventText = (TextView) itemView.findViewById(R.id.event_time_list_item);
            decoratorImage = (ImageView) itemView.findViewById(R.id.event_image_list_item);
        }


        private void bind(final Context context, final MyEvent event, int position,
                          @Nullable final OnItemClickListener clickListener,
                          @Nullable final OnItemLongClickListener longClickListener) {

            int gray = Color.parseColor("#d9d9d9");
            int black = Color.parseColor("#000000");
            int darkGray = Color.parseColor("#8e8e8e");
            boolean old = Calendar.getInstance().getTimeInMillis() >= (event.timestampDateEvent);
            nameEventText.setText(event.name);
            locationEventText.setText(event.location);
            dayEventText.setText(event.day);
            timeEventText.setText(event.hour);

            // evento passato
            if (old) {
                decoratorImage.setBackgroundColor(gray);
                nameEventText.setTextColor(gray);
                locationEventText.setTextColor(gray);
                dayEventText.setTextColor(gray);
                timeEventText.setTextColor(gray);


            } else {
                decoratorImage.setBackgroundColor(Color.parseColor(colorList[position % colorList.length]));
                nameEventText.setTextColor(black);
                locationEventText.setTextColor(darkGray);
                dayEventText.setTextColor(black);
                timeEventText.setTextColor(darkGray);

            }

            if (clickListener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickListener.onItemClick(event);
                    }
                });
            }

            if (longClickListener != null) {
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        longClickListener.onItemLongPress(event);

                        // return true if the callback consumed the long click
                        return true;
                    }
                });

            }
        }
    }
}
