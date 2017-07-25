package tori.studygroups.otherClass;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import java.util.Calendar;
import java.util.HashMap;

public class MyEvent implements Parcelable{

    public String name;
    public String day;
    public String hour;
    public String location;
    public String userId;
    public String userName;
    public String channelUrl;
    public String channelName;
    public String creationDate;

    public String eventId;

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public MyEvent() {
    }

    public MyEvent(String name, String location, String day, String hour, String userId, String userName,
                   String channelUrl, String channelName, String creationDate, @Nullable String eventId) {

        this.name = name;
        this.location = location;
        this.day = day;
        this.hour = hour;
        this.userId = userId;
        this.userName = userName;
        this. channelUrl = channelUrl;
        this.channelName = channelName;
        this.creationDate = creationDate;
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
                            "\"location\":\"" + location +"\"," +
                            "\"day\":\"" + day +"\"," +
                            "\"time\":\"" + hour +"\"," +
                            "\"userId\":\"" + userId +"\"," +
                            "\"userName\":\"" + userName +"\"," +
                            "\"channelUrl\":\"" + channelUrl +"\"," +
                            "\"channelName\":\"" + channelName +"\"," +
                            "\"creationDate\":\"" + creationDate +"\"," +
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
        dest.writeString(location);
        dest.writeString(day);
        dest.writeString(hour);
        dest.writeString(userId);
        dest.writeString(userName);
        dest.writeString(channelUrl);
        dest.writeString(channelName);
        dest.writeString(creationDate);
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
        location = in.readString();
        day = in.readString();
        hour = in.readString();
        userId = in.readString();
        userName = in.readString();
        channelUrl = in.readString();
        channelName = in.readString();
        creationDate = in.readString();
        eventId = in.readString();
    }

}
