package vivekvaidya.com.lookingfor;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/10/26.
 */

public class Event implements Parcelable {
    /**Event Data*/
    private String hostID;
    private String eventID;
    private String title;
    private ArrayList<String> tags;
    private String location;
    private String date;
    private String time;
    private String description;
    private ArrayList<String> attendeeID;
    private boolean visible;
    public Event()  {

    }
    /**Full Constructor*/
    public Event(String hostID, String eventID, String title, ArrayList<String> tags, String location, String date,String time, String description){
        this.hostID = hostID;
        this.eventID = eventID;
        this.title = title;
        this.tags = tags;
        this.location = location;
        this.date = date;
        this.time = time;
        this.description = description;
        this.attendeeID = new ArrayList<>();
        this.attendeeID.add(hostID);
        this.visible = true;
    }
    public Event(String hostID, String title, ArrayList<String> tags, String time, String date, String description){
        this.hostID = hostID;
        this.title = title;
        this.eventID = null;
        this.tags = tags;
        this.date = date;
        this.time = time;
        this.description = description;
        this.attendeeID = new ArrayList<>();
        this.attendeeID.add(hostID);
        this.visible = true;
    }
    /**Getter and Setters*/
    public void setHostID(String hostID) {this.hostID = hostID;}
    public String getHostID(){
        return this.hostID;
    }
    public String getEventID(){
        return this.eventID;
    }
    public String getTitle(){
        return this.title;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public ArrayList<String> getTags(){
        return tags == null ? new ArrayList<String>() : tags;
    }
    public void setTags(ArrayList<String> tags){
        this.tags = tags;
    }
    public String getLocation(){
        return this.location;
    }
    public void setLocation(String location){
        this.location = location;
    }
    public String getDateTime(){
        return this.date + " " + this.time;
    }
    public String getTime() {
        return this.time;
    }
    public String getDate() {
        return this.date;
    }
    public void setDate(String date){
        this.date = date;
    }
    public void setTime(String time) { this.time = time; }
    public String getDescription(){
        return this.description;
    }
    public void setDescription(String description){
        this.description = description;
    }
    public ArrayList<String> getAttendeeID(){
        return this.attendeeID == null ? new ArrayList<String>(): this.attendeeID;
    }
    public boolean isVisible() {
        return visible;
    }
    public void setVisible(boolean visible) {
        this.visible = visible;
    }


    /**Someone tries to join.*/
    public boolean attendEvent(String UID, OnCompleteListener completeListener){
        if (this.attendeeID != null) {
            if (this.attendeeID.contains(UID)){
                return false;
            } else{
                this.attendeeID.add(UID);
                updateToFirebase(completeListener);
                return true;
            }
        } else {
            this.attendeeID = new ArrayList<>();
            this.attendeeID.add(UID);
            updateToFirebase(completeListener);
            return true;
        }
    }
    /**Someone tries to leave*/
    public boolean leaveEvent(String UID, OnCompleteListener completeListener){
        if (attendeeID != null ) {
            if (attendeeID.contains(UID)) {
                attendeeID.remove(UID);
                updateToFirebase(completeListener);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public void updateToFirebase(OnCompleteListener<Void> onCompleteListener) {
        DatabaseReference eventStorageReference = FirebaseDatabase.getInstance().getReference().child("events")
                .child("storage");
            DatabaseReference curEventReference = eventStorageReference.child(eventID);
            curEventReference.setValue(this).addOnCompleteListener(onCompleteListener);
    }

    /**Push event to Firebase.*/
    public void pushToFirebase(OnCompleteListener<Void> onCompleteListener){
        DatabaseReference eventStorageReference = FirebaseDatabase.getInstance().getReference().child("events")
                .child("storage");
        if (this.eventID == null) {
            DatabaseReference newEventReference = eventStorageReference.push();
            eventID = newEventReference.getKey();
            newEventReference.setValue(this).addOnCompleteListener(onCompleteListener);
        } else {
            DatabaseReference curEventReference = eventStorageReference.child(eventID);
            curEventReference.setValue(this).addOnCompleteListener(onCompleteListener);
        }
    }

    @Override
    public String toString() {
        return title == null ? "No title" : title;
    }

    public static ArrayList<Event> searchForUserAttendedEvents(List<Event> events, String uid) {
        ArrayList<Event> newList = new ArrayList<>();
        int count = 0;
        for (int i = 0; i < events.size(); i++){
            if (events.get(i).getAttendeeID().contains(uid)){
                newList.add(events.get(i));
                count++;
            }
        }
        if (count != 0){
            return newList;
        } else {
            newList.add(new Event("Sorry", "Could", "Not", new ArrayList<String>(), "Find", "Any", "Matching", "Event"));
            return newList;
        }
    }

    public static List<Event> searchForEvent(List<Event> events, String query){
        query = query.toLowerCase();
        List<Event> newList = new ArrayList<>();
        int count = 0;
        for (int i = 0; i < events.size(); i++){
            if ((events.get(i).getDescription().toLowerCase().contains(query)
                    || events.get(i).getTitle().toLowerCase().contains(query)
                    || events.get(i).getLocation().toLowerCase().contains(query)
                    || events.get(i).getTags().contains(query)) ){
                newList.add(events.get(i));
                count++;
            }
        }
        if (count != 0){
            return newList;
        } else {
            newList.add(new Event("Sorry", "Could", "Not", new ArrayList<String>(),"Find", "Any", "Matching", "Event"));
            return newList;
        }
    }

    /**Parcelable required methods*/
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.hostID);
        dest.writeString(this.eventID);
        dest.writeString(this.title);
        dest.writeStringList(this.tags);
        dest.writeString(this.location);
        dest.writeString(this.date);
        dest.writeString(this.time);
        dest.writeString(this.description);
        dest.writeStringList(this.attendeeID);
    }

    protected Event(Parcel in) {
        this.hostID = in.readString();
        this.eventID = in.readString();
        this.title = in.readString();
        this.tags = in.createStringArrayList();
        this.location = in.readString();
        this.date = in.readString();
        this.time = in.readString();
        this.description = in.readString();
        this.attendeeID = in.createStringArrayList();
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel source) {
            return new Event(source);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };
}
