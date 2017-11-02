package vivekvaidya.com.lookingfor;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Arrays;

import static android.R.attr.data;

/**
 * Created by Administrator on 2017/10/26.
 */

public class Event implements Parcelable {
    public static long numberOfEvents = 0;
    final private String hostID;
    final private long eventID;
    private String title;
    private String eventType;
    private String location;
    private String dateTime;
    private String description;
    private String[] attendeeID;
    public Event(String hostID, String title, String eventType, String location, String dateTime, String description){
        this.hostID = hostID;
        this.title = title;
        this.eventType = eventType;
        this.location = location;
        this.dateTime = dateTime;
        this.description = description;
        this.eventID = Event.numberOfEvents;
        this.attendeeID = new String[1];
        this.attendeeID[0] = hostID;
    }
    public String getHostID(){
        return this.hostID;
    }
    public long getEventID(){
        return this.eventID;
    }
    public String getTitle(){
        return this.title;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public String getEventType(){
        return this.eventType;
    }
    public void setEventType(String eventType){
        this.eventType = eventType;
    }
    public String getLocation(){
        return this.location;
    }
    public void setLocation(String location){
        this.location = location;
    }
    public String getDateTime(){
        return this.dateTime;
    }
    public void setDateTime(String dateTime){
        this.dateTime = dateTime;
    }
    public String getDescription(){
        return this.description;
    }
    public void setDescription(String description){
        this.description = description;
    }
    public String[] getAttendeeID(){
        return this.attendeeID;
    }
    public boolean joinEvent(String UID){
        if (Arrays.asList(this.attendeeID).contains(UID)){
            return false;
        } else {
            String[] dummyArray = new String[this.attendeeID.length+1];
            for (int i = 0; i < this.attendeeID.length; i++){
                dummyArray[i] = this.attendeeID[i];
            }
            dummyArray[dummyArray.length-1] = UID;
            this.attendeeID = dummyArray;
            return true;
        }
    }
    public boolean leaveEvent(String UID){
        boolean inEvent = false;
        int IDLocation = 0;
        for (int i = 0; i < this.attendeeID.length; i++) {
            if (UID.equals(this.attendeeID[i])) {
                inEvent = true;
                this.attendeeID[i] = "";
                break;
            }
        }
        if (inEvent){
            String[] dummyArray = new String[this.attendeeID.length-1];
            boolean empty = false;
            for (int i = 0; i < dummyArray.length; i++){
                if (this.attendeeID[i].equals("")){
                    empty = true;
                }
                if (empty){
                    dummyArray[i] = attendeeID[i+1];
                } else {
                    dummyArray[i] = attendeeID[i];
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(hostID);
        dest.writeLong(eventID);
        dest.writeString(title);
        dest.writeString(eventType);
        dest.writeString(location);
        dest.writeString(dateTime);
        dest.writeString(description);
        dest.writeStringArray(attendeeID);
    }

    private Event(Parcel in) {
        hostID = in.readString();
        eventID = in.readLong();
        title = in.readString();
        eventType = in.readString();
        location = in.readString();
        dateTime = in.readString();
        description = in.readString();
        attendeeID = in.createStringArray();
    }

    public static final Parcelable.Creator<Event> CREATOR
            = new Parcelable.Creator<Event>() {
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        public Event[] newArray(int size) {
            return new Event[size];
        }
    };
}
