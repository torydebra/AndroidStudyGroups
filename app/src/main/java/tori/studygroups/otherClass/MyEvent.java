package tori.studygroups.otherClass;

import java.util.Calendar;
import java.util.HashMap;

public class MyEvent {

    public String name;
    public String day;
    public String hour;
    public String location;
    public String userId;
    public String channelUrl;
    public String creationDate;

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public MyEvent() {
    }

    public MyEvent(String name, String location, String day, String hour, String userId,
                   String channelUrl, String creationDate) {

        this.name = name;
        this.location = location;
        this.day = day;
        this.hour = hour;
        this.userId = userId;
        this. channelUrl = channelUrl;
        this.creationDate = creationDate;
    }
}
