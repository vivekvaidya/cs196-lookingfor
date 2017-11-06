package vivekvaidya.com.lookingfor;

import java.util.ArrayList;

public interface CallableAfterDownload {
    void eventsDownloaded(int behavior, ArrayList<Event> events);
}
