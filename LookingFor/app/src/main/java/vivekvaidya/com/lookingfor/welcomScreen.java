package vivekvaidya.com.lookingfor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

public class welcomScreen extends AppCompatActivity{
    private Button signOut;
    private Button createEvent;
    private Button accountSettings;
    private TextView welcomText;
    private FirebaseAuth myAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcom_screen);
        signOut = (Button) findViewById(R.id.signOut);
        createEvent = (Button) findViewById(R.id.createEventButton);
        accountSettings = (Button) findViewById(R.id.accountSettingsButton);
        welcomText = (TextView) findViewById(R.id.welcomeText);
        showWelcomeText();
        final Context context = this.getApplicationContext();
        myAuth = FirebaseAuth.getInstance();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
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
    }
    private void signOut(){
        myAuth.signOut();
        finish();
    }
    public void showWelcomeText() {
    }
}
