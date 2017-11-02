package vivekvaidya.com.lookingfor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class welcomScreen extends AppCompatActivity{
    /**UI variables*/
    private Button signOut;
    private Button createEvent;
    private Button accountSettings;
    private TextView welcomText;
    private FirebaseAuth myAuth;
    private Toolbar toolbar;
    private Button allEventButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /**Initialize Screen*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcom_screen);

        /**Initialize UIs*/
        signOut = (Button) findViewById(R.id.signOut);
        createEvent = (Button) findViewById(R.id.createEventButton);
        allEventButton = (Button) findViewById(R.id.allEventButton);
        accountSettings = (Button) findViewById(R.id.accountSettingsButton);
        welcomText = (TextView) findViewById(R.id.welcomeText);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        /**Firebase Constant*/
        myAuth = FirebaseAuth.getInstance();

        setSupportActionBar(toolbar);
        /**Show Welcome Text*/
        showWelcomeText();


//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        /**Set behavior to SignOut Button.*/
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        /**Go to other screens*/
        final Context context = this.getApplicationContext();
        accountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,userSettingsScreen.class);
                startActivity(intent);
            }
        });
        createEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,createEventScreen.class);
                startActivity(intent);
            }
        });
        allEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,EventBrowser.class);
                startActivity(intent);
            }
        });

    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        // Check which request we're responding to
//        if (requestCode == USER_SETTINGS_RESULT) {
//            // Make sure the request was successful
//            if (resultCode == RESULT_OK) {
//                // The user picked a contact.
//                // The Intent's data Uri identifies which contact was selected.
//
//                // Do something with the contact here (bigger example below)
//
//            }
//        }
//    }
    /**Sign Out*/
    private void signOut(){
        myAuth.signOut();
        finish();
    }
    /**Show welcome text by username*/
    public void showWelcomeText() {
        String uid = myAuth.getUid();
        DatabaseReference id = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("username");
        id.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.getValue(String.class);
                if (name == null) {
                    welcomText.setText("Hello! But we can't find your nickname.");
                }
                else {
                    welcomText.setText("Hello," + name.toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                welcomText.setText("Hello! But something's wrong.");
            }
        });
    }
}
