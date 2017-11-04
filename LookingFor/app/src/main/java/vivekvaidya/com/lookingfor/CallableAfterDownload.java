package vivekvaidya.com.lookingfor;

import java.util.ArrayList;

/**
 * Created by apple on 11/04/17.
 */

public interface CallableAfterDownload {
    void eventsDownloaded(int behavior, ArrayList<Event> events);
}
