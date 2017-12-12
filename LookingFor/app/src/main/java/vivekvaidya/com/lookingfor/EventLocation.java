package vivekvaidya.com.lookingfor;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by apple on 12/11/17.
 */

public class EventLocation implements Parcelable{
    private double longitude;
    private double latitude;
    private String id;

    public EventLocation() {
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longtitude) {
        this.longitude = longtitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public EventLocation(double longtitude, double latitude, String id) {
        this.longitude = longtitude;
        this.latitude = latitude;
        this.id = id;

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.longitude);
        dest.writeDouble(this.latitude);
        dest.writeString(this.id);
    }

    protected EventLocation(Parcel in) {
        this.longitude = in.readDouble();
        this.latitude = in.readDouble();
        this.id = in.readString();
    }

    public static final Creator<EventLocation> CREATOR = new Creator<EventLocation>() {
        @Override
        public EventLocation createFromParcel(Parcel source) {
            return new EventLocation(source);
        }

        @Override
        public EventLocation[] newArray(int size) {
            return new EventLocation[size];
        }
    };
}
