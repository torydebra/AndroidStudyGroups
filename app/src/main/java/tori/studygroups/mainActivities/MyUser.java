package tori.studygroups.mainActivities;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class MyUser {

    private String username;
    private String email;

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public MyUser() {
    }

    public MyUser(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername(){
         return username;
     }
}