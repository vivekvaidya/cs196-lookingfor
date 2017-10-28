package vivekvaidya.com.lookingfor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

import org.w3c.dom.Text;

public class createEventScreen extends AppCompatActivity {
    private EditText titleET;
    private Spinner eventTypeSPN;
    private TextView dateDisplay;
    private TextView timeDisplay;
    private EditText description;
    private Button confirmButton;
    private Button backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        titleET = (EditText) findViewById(R.id.titleET);
        backButton = (Button) findViewById(R.id.backButton);
        eventTypeSPN = (Spinner) findViewById(R.id.eventSelectionSpinner);
        dateDisplay = (TextView) findViewById(R.id.dateDisplay);
        timeDisplay = (TextView) findViewById(R.id.timeDisplay);
        description = (EditText) findViewById(R.id.descriptionET);
        confirmButton = (Button) findViewById(R.id.profileConfirmBT);
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
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Event newEvent = new Event("hi",
                        titleET.getText().toString(),
                        eventTypeSPN.getSelectedItem().toString(),
                        timeDisplay.getText().toString(),
                        dateDisplay.getText().toString(),
                        description.getText().toString());
                Intent data = new Intent();
                data.putExtra("EventData", newEvent);
                setResult(Activity.RESULT_OK,data);
                finish();
            }
        });
        backButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                finish();
            }
        });
    }

}
