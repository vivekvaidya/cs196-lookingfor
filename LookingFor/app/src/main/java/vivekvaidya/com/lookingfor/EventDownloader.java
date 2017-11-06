package vivekvaidya.com.lookingfor;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


class EventDownloader{

    static void downloadEventsTo(final int behavior, final Context context, final CallableAfterDownload call) {
        DatabaseReference eventStorageReference = FirebaseDatabase.getInstance().getReference().child("events").child("storage");
        eventStorageReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Event> events = new ArrayList<>();
                events.addAll(decodeEvents(dataSnapshot));
                call.eventsDownloaded(behavior,events);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(context,"Something's wrong when downloading events: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    //TODO: decode properly
    private static ArrayList<Event> decodeEvents(DataSnapshot dataSnapshot) {
        ArrayList<Event> events = new ArrayList<>();
        for (DataSnapshot eventSnapshot: dataSnapshot.getChildren()) {
            Event newEvent = new Event();
            Object dateTime = eventSnapshot.child("dateTime").getValue();
            Object description = eventSnapshot.child("description").getValue();
            Object eventType = eventSnapshot.child("eventType").getValue();
            Object location = eventSnapshot.child("location").getValue();
            Object title = eventSnapshot.child("title").getValue();
            Object hostID = eventSnapshot.child("hostID").getValue();

            newEvent.setDateTime(dateTime == null ? "" : dateTime.toString());
            newEvent.setDescription(description == null ? "" : description.toString());
            newEvent.setEventType(eventType == null ? "" : eventType.toString());
            newEvent.setLocation(location == null ? "" : location.toString());
            newEvent.setTitle(title == null ? "" : title.toString());
            newEvent.setHostID(hostID == null ? "" : hostID.toString());
            events.add(newEvent);
        }
        return events;
    }
}