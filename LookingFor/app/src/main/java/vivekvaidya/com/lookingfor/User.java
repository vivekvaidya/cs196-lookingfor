package vivekvaidya.com.lookingfor;

/**
 * Created by Administrator on 2017/10/26.
 */

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("serial")
public class User implements Serializable {
    /**Basic Data*/
    final private String UID;
    private String emailAddress;
    private String phoneNumber;
    private String userName;
    private String avatarLink;
    private boolean isLoggedIn = false;
    /**Constructors*/
    public User(String UID, String phoneNumber){
        this.UID = UID;
        this.phoneNumber = phoneNumber;
        this.isLoggedIn = true;
    }
    public User(String UID, String emailAddress, String avatarAddress){
        this.UID = UID;
        this.emailAddress = emailAddress;
        this.isLoggedIn = true;
        this.avatarLink = avatarAddress;

    }
    /**Getter and Setters*/
    public String getUID(){
        return this.UID;
    }
    public String getPhoneNumber(){
        return this.phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        if (this.isLoggedIn) {
            this.phoneNumber = phoneNumber;
        }
    }
    public String getEmailAddress(){
        return this.emailAddress;
    }
    public void setEmailAddress(String emailAddress) {
        if (this.isLoggedIn) {
            this.emailAddress = emailAddress;
        }
    }
    public String getUserName(){
        return this.userName;
    }
    public void setUserName(String userName){
        if (this.isLoggedIn){
            this.userName = userName;
        }
    }
    public String getAvatarLink(){
        return this.avatarLink;
    }
    public void setAvatarLink(String avatarLink){
        if (this.isLoggedIn){
            this.avatarLink = avatarLink;
        }
    }
    public boolean getLoginStatus(){
        return this.isLoggedIn;
    }
    public void swapLogin(){
        this.isLoggedIn = !this.isLoggedIn;
    }

    /***/
    public void pushToFirebase(OnCompleteListener<Void> onCompleteListener){
        HashMap<String, Object> dataMap = new HashMap<>();
        dataMap.put("email", getEmailAddress());
        dataMap.put("avatarLink", getAvatarLink());
        dataMap.put("phone", getPhoneNumber());
        dataMap.put("username", getUserName());
        String[] events = {"0","2"};
        List attendingEvents = new ArrayList<>(Arrays.asList(events));
        dataMap.put("attendingEvents", attendingEvents);
        String[] friendsList = {"sojasd","dsnfa98hsndfkj"};
        List friends = new ArrayList<>(Arrays.asList(friendsList));
        dataMap.put("friends", friends);
        String[] hosting = {};
        List hostingEvents = new ArrayList<>(Arrays.asList(hosting));
        dataMap.put("hostingEvents",hostingEvents);


        DatabaseReference newEventReference = FirebaseDatabase.getInstance().getReference().child("users")
                .child(getUID());
        newEventReference.setValue(dataMap).addOnCompleteListener(onCompleteListener);
    }

}
