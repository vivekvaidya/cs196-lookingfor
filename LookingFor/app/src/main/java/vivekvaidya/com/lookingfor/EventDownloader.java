package vivekvaidya.com.lookingfor;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;


class EventDownloader{

    static void downloadEventsTo(final int behavior, final Context context, final CallableAfterDownload call) {
        DatabaseReference eventStorageReference = FirebaseDatabase.getInstance().getReference().child("events").child("storage");
        eventStorageReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Event> events = new ArrayList<>();
                for (DataSnapshot eventSnapshot: dataSnapshot.getChildren()) {
                    events.add(eventSnapshot.getValue(Event.class));
                    Log.d("Working?", eventSnapshot.getValue(Event.class).getEventID());
                }
                call.eventsDownloaded(behavior,events);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(context,"Something's wrong when downloading events: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}