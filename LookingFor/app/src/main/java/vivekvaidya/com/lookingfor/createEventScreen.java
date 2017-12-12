package vivekvaidya.com.lookingfor;


import android.content.Intent;
import android.location.Location;
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

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;



public class createEventScreen extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    /**UI Variables*/

    private EditText titleET;
    private Spinner eventTypeSPN;
    private TextView dateDisplay;
    private TextView timeDisplay;
    private EditText description;
    private TextView locationDisplay;
    private EventLocation location;

    /*Database Constants*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*Initialize Screen*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event_screen);

        /*Initialize Common UIs*/
        titleET =  findViewById(R.id.titleET);
        dateDisplay =  findViewById(R.id.dateDisplay);
        timeDisplay =  findViewById(R.id.timeDisplay);
        description =  findViewById(R.id.descriptionET);
        Button confirmButton =  findViewById(R.id.addEventBT);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        locationDisplay = findViewById(R.id.locationDisplay);
        eventTypeSPN =  findViewById(R.id.eventSelectionSpinner);
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
                        location,
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
        dateDisplay.setOnClickListener(this);
        timeDisplay.setOnClickListener(this);
        locationDisplay.setOnClickListener(this);
    }

    /**
     * Things to do when Event sent to Firebase.
     * @param task some task
     */
    public void onEventPushComplete(@NonNull Task<Void> task) {
        if(task.isSuccessful()) {
            Toast.makeText(createEventScreen.this, "Event registered!", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(createEventScreen.this, "Error...", Toast.LENGTH_LONG).show();
        }
    }
    private void pickDate() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.show(getFragmentManager(),"Datepickerdialog");
    }

    private void pickTime() {
        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(this,now.get(Calendar.HOUR_OF_DAY),now.get(Calendar.MINUTE),true);
        tpd.show(getFragmentManager(),"Timepickerdialog");
    }

    private static final int PLACE_PICKER_REQUEST = 827;
    private void pickLocation() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException|GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dateDisplay:
                pickDate();
                break;
            case R.id.timeDisplay:
                pickTime();
                break;
            case R.id.locationDisplay:
                pickLocation();
                break;
            default:
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this,data);
                location = new EventLocation();
                location.setLatitude(place.getLatLng().latitude);
                location.setLongitude(place.getLatLng().longitude);
                location.setId(place.getId());
                locationDisplay.setText(place.getName());
            }
        }
    }
    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = monthOfYear + 1 + "/" + dayOfMonth + "/" + year;
        dateDisplay.setText(date);
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        String sminute = String.valueOf(minute);
        if (minute <= 9) {
            sminute = "0" + sminute;
        }
        String shour = String.valueOf(hourOfDay);
        if (hourOfDay <= 9) {
            shour = "0" + shour;
        }
        String time = shour + ":" + sminute;
        timeDisplay.setText(time);
    }
}
