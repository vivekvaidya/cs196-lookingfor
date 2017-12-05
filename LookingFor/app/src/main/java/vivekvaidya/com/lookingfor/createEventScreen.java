package vivekvaidya.com.lookingfor;


import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;


public class createEventScreen extends NavigationDrawer implements View.OnClickListener {
    /**UI Variables*/

    private EditText titleET;
    private Spinner eventTypeSPN;
    private TextView dateDisplay;
    private TextView timeDisplay;
    private EditText description;

    /*Database Constants*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*Initialize Screen*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event_screen);

        /*Initialize Common UIs*/
        titleET =  findViewById(R.id.titleET);
        Button backButton = (Button) findViewById(R.id.backButton);
        eventTypeSPN = (Spinner) findViewById(R.id.eventSelectionSpinner);
        dateDisplay = (TextView) findViewById(R.id.dateDisplay);
        timeDisplay = (TextView) findViewById(R.id.timeDisplay);
        description = (EditText) findViewById(R.id.descriptionET);
        Button confirmButton = (Button) findViewById(R.id.addEventBT);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*Initialize Spinner*/
        ArrayAdapter<CharSequence> spAdapter = ArrayAdapter.createFromResource(this,
                R.array.event_types,android.R.layout.simple_spinner_item);
        spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventTypeSPN.setAdapter(spAdapter);

        /*Try sending Event with Button*/
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uid;
                /*Try fetching uid of current user*/
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) {
                    Toast.makeText(createEventScreen.this, "Error... UID not available", Toast.LENGTH_LONG).show();
                    return;
                }

                uid = user.getUid();
                String tag = eventTypeSPN.getSelectedItem().toString();
                ArrayList<String> tags = new ArrayList<>();
                tags.add(tag);
                /*Prepare the event to be sent*/
                Event newEvent = new Event(uid,
                        titleET.getText().toString(),
                        tags,
                        timeDisplay.getText().toString(),
                        dateDisplay.getText().toString(),
                        description.getText().toString());
                /*Send event*/
                newEvent.pushToFirebase(new OnCompleteListener<Void>(){
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        onEventPushComplete(task);
                    }
                });
            }
        });
        backButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Toast.makeText(createEventScreen.this, "Create event canceled.", Toast.LENGTH_LONG).show();
                finish();
            }
        });
        dateDisplay.setOnClickListener(this);
        timeDisplay.setOnClickListener(this);
    }

    /**
     * Things to do when Event sent to Firebase.
     * @param task some task (Well, I don't know what this is.)
     */
    public void onEventPushComplete(@NonNull Task<Void> task) {
        if(task.isSuccessful()) {
            Toast.makeText(createEventScreen.this, "Event registered!", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(createEventScreen.this, "Error...", Toast.LENGTH_LONG).show();
        }
    }




    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dateDisplay:

                break;
            case R.id.timeDisplay:
                break;
            default:
                break;
        }
    }
}
