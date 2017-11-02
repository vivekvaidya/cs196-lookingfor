package vivekvaidya.com.lookingfor;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Dictionary;

public class EventBrowser extends AppCompatActivity {
    public ListView eventsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /**Initialize Activity*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_browser);

        /**Initialize Basic UIs*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        eventsListView = (ListView) findViewById(R.id.eventListView);

        /**Download events*/
        DatabaseReference eventStorageReference = FirebaseDatabase.getInstance().getReference().child("events").child("storage");
        eventStorageReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                receiveEvents(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                eventsNotRecieved(databaseError);
            }
        });


//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    public void receiveEvents(DataSnapshot dataSnapshot) {
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
        EventBrowserItemAdapter adapter = new EventBrowserItemAdapter(this, events);
        eventsListView.setAdapter(adapter);
    }

    public void eventsNotRecieved(DatabaseError databaseError) {
        Toast.makeText(this,"Something's wrong when downloading evnents", Toast.LENGTH_LONG).show();
    }

}
