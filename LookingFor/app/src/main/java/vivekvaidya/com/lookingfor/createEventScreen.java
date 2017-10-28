package vivekvaidya.com.lookingfor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class createEventScreen extends AppCompatActivity {
    private EditText titleET;
    private Spinner eventTypeSPN;
    private TextView dateDisplay;
    private TextView timeDisplay;
    private EditText description;
    private Button confirmButton;
    private DatabaseReference eventsReference;
    private DatabaseReference eventCountReference;
    public static final String EVENT_DATA = "EventData";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        titleET = (EditText) findViewById(R.id.titleET);
        eventTypeSPN = (Spinner) findViewById(R.id.eventSelectionSpinner);
        dateDisplay = (TextView) findViewById(R.id.dateDisplay);
        timeDisplay = (TextView) findViewById(R.id.timeDisplay);
        description = (EditText) findViewById(R.id.descriptionET);
        confirmButton = (Button) findViewById(R.id.addEventBT);
        ArrayAdapter<CharSequence> spAdapter = ArrayAdapter.createFromResource(this,
                R.array.event_types,android.R.layout.simple_spinner_item);
        spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventTypeSPN.setAdapter(spAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        eventsReference = FirebaseDatabase.getInstance().getReference().child("events");
        eventCountReference = eventsReference.child("eventCount");
        eventCountReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    Event.numberOfEvents = dataSnapshot.getValue(Integer.class);
                    Toast.makeText(createEventScreen.this, "Total number of events now:" + Event.numberOfEvents, Toast.LENGTH_LONG).show();
                } catch (NullPointerException e) {
                    Toast.makeText(createEventScreen.this, "Number of events not integer!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(createEventScreen.this, "Read integer canceled.", Toast.LENGTH_LONG).show();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Event newEvent = new Event("hi",
                        titleET.getText().toString(),
                        eventTypeSPN.getSelectedItem().toString(),
                        timeDisplay.getText().toString(),
                        dateDisplay.getText().toString(),
                        description.getText().toString());
                pushNewEvent(newEvent);

            }
        });

    }
    private void pushNewEvent(Event newEvent) {
        //Dummy Code: set up names,email and password or make it something else
        HashMap<String, Object> dataMap = new HashMap<>();
        dataMap.put("title", newEvent.getTitle());
        dataMap.put("eventType", newEvent.getEventType());
        dataMap.put("location", newEvent.getLocation());
        dataMap.put("dateTime", newEvent.getDateTime());
        dataMap.put("description", newEvent.getDescription());
        dataMap.put("hostID", newEvent.getHostID());
        List attendeeID = new ArrayList<>(Arrays.asList(newEvent.getAttendeeID()));
        dataMap.put("attendeeID", attendeeID);

        DatabaseReference newEventReference = eventsReference.child("storage").child(String.valueOf(Event.numberOfEvents));

        newEventReference.setValue(dataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()) {
                    Toast.makeText(createEventScreen.this, "Event registered!", Toast.LENGTH_LONG).show();
                    eventCountReference.setValue(Event.numberOfEvents + 1);
//                Intent data = new Intent();
//                data.putExtra(EVENT_DATA, newEvent);
//                setResult(Activity.RESULT_OK,data);
                    finish();

                } else {
                    Toast.makeText(createEventScreen.this, "Error...", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
