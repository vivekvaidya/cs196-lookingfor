package vivekvaidya.com.lookingfor;

import android.content.Intent;
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

    public static final String RECEIVE_EVENT_BEHAVIOR = "behavior";
    public static final String EVENTS_TO_DISPLAY = "events";
    public static final int DISPLAY_ALL = 0;
    public static final int GET_EVENTS = 1;
    public static final int DISPLAY_EVENTS = 2;
    public static final String EVENTS_RETURNED = "EventsReturned";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /**Initialize Activity*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_browser);

        /**Initialize Basic UIs*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();

        int behavior = intent.getIntExtra(RECEIVE_EVENT_BEHAVIOR,DISPLAY_ALL);
        ArrayList<Event> events = intent.getParcelableArrayListExtra(EVENTS_TO_DISPLAY);
        downloadEvents(behavior,events);



//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    public void downloadEvents(final int behavior, final ArrayList<Event> someEvents) {
        DatabaseReference eventStorageReference = FirebaseDatabase.getInstance().getReference().child("events").child("storage");
        eventStorageReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Event> events = decodeEvents(dataSnapshot);
                switch (behavior) {
                    case GET_EVENTS:
                        Intent returnIntent = new Intent();
                        returnIntent.putParcelableArrayListExtra(EVENTS_RETURNED, events);
                        setResult(RESULT_OK, returnIntent);
                        finish();
                        break;
                    case DISPLAY_ALL:
                        displayEvents(events);
                        break;
                    case DISPLAY_EVENTS:
                        displayEvents(someEvents);
                    default:
                        break;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                eventsNotReceived(databaseError);
            }
        });
    }

    public ArrayList<Event> decodeEvents(DataSnapshot dataSnapshot) {
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
    public void displayEvents(ArrayList<Event> events) {
        EventBrowserItemAdapter adapter = new EventBrowserItemAdapter(this, events);
        ListView eventsListView = (ListView) findViewById(R.id.eventListView);
        eventsListView.setAdapter(adapter);
    }

    public void eventsNotReceived(DatabaseError databaseError) {
        Toast.makeText(this,"Something's wrong when downloading events", Toast.LENGTH_LONG).show();
    }

}
