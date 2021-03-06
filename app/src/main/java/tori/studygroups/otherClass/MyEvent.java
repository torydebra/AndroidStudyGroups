package tori.studygroups.otherClass;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

public class MyEvent implements Parcelable{

    public String name;
    public long timestampDateEvent;
    public String day;
    public String description;
    public String hour;
    public String location;
    public String userId;
    public String userName;
    public String channelUrl;
    public String channelName;
    public long timestampCreated;

    public String eventId;

    // Default constructor required for calls to
    // DataSnapshot.getValue(MyEvent.class)
    public MyEvent() {
    }

    public MyEvent(String name, String description, String location, long timestampDateEvent,
                   String day, String hour, String userId, String userName,
                   String channelUrl, String channelName, long timestampCreated, @Nullable String eventId) {

        this.name = name;
        this.description = description;
        this.timestampDateEvent = timestampDateEvent;
        this.location = location;
        this.day = day;
        this.hour = hour;
        this.userId = userId;
        this.userName = userName;
        this.channelUrl = channelUrl;
        this.channelName = channelName;
        this.timestampCreated = timestampCreated;
        this.eventId = eventId;
    }
    public void setEventId (String eventId) {
        this.eventId = eventId;
    }

    public String toJsonString (){
        String s =
                "{ \"event\": " +
                        "{" +
                            "\"name\":\"" + name +"\"," +
                            "\"description\":\"" + description +"\"," +
                            "\"location\":\"" + location +"\"," +
                            "\"timestampDateEvent\":\"" + timestampDateEvent +"\"," +
                            "\"day\":\"" + day +"\"," +
                            "\"time\":\"" + hour +"\"," +
                            "\"userId\":\"" + userId +"\"," +
                            "\"userName\":\"" + userName +"\"," +
                            "\"channelUrl\":\"" + channelUrl +"\"," +
                            "\"channelName\":\"" + channelName +"\"," +
                            "\"timestampCreated\":\"" + timestampCreated +"\"," +
                            "\"eventId\":\"" + eventId +"\"" +
                        "}" +
                "}";

        return s;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(location);
        dest.writeLong(timestampDateEvent);
        dest.writeString(day);
        dest.writeString(hour);
        dest.writeString(userId);
        dest.writeString(userName);
        dest.writeString(channelUrl);
        dest.writeString(channelName);
        dest.writeLong(timestampCreated);
        dest.writeString(eventId);
    }

    public static final Parcelable.Creator<MyEvent> CREATOR = new Parcelable.Creator<MyEvent>() {
        public MyEvent createFromParcel(Parcel in) {
            return new MyEvent(in);
        }

        public MyEvent[] newArray(int size) {
            return new MyEvent[size];
        }
    };

    private MyEvent(Parcel in) {
        name = in.readString();
        description = in.readString();
        location = in.readString();
        timestampDateEvent = in.readLong();
        day = in.readString();
        hour = in.readString();
        userId = in.readString();
        userName = in.readString();
        channelUrl = in.readString();
        channelName = in.readString();
        timestampCreated = in.readLong();
        eventId = in.readString();
    }

}
