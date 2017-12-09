package vivekvaidya.com.lookingfor;

/**
 * Created by user on 11/1/2017.
 */

public class EventData {

    private int eventPictures;
    private String eventName;
    private String eventDate;
    private String eventTime;
    private String eventLocation;

    public EventData(int eventPictures, String Name, String Date, String Time, String Location) {
        this.setEventDate(Date);
        this.setEventLocation(Location);
        this.setEventName(Name);
        this.setEventTime(Time);
        this.setEventPictures(eventPictures);
    }

    public String getEventDate() {
        return eventDate;
    }

    public String getEventName() {
        return eventName;
    }

    public int getEventPictures() {
        return eventPictures;
    }

    public void setEventPictures(int eventPictures) {
        this.eventPictures = eventPictures;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }




}
