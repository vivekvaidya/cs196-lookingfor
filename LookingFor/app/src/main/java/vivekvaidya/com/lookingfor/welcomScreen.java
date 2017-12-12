package vivekvaidya.com.lookingfor;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

public class welcomScreen extends AppCompatActivity {
    /**
     * UI variables
     */
    private Button signOut;
    private Button createEvent;
    private Button accountSettings;
    private TextView welcomeText;
    private FirebaseAuth myAuth;
    private Toolbar toolbar;
    private Button allEventButton;
    private ImageButton createEvent1;
    private ImageButton myEventButton1;
    private ImageButton allEventButton1;
    private ImageButton accountSettings1;
    private ImageButton signout1;
    private EditText searchQuery;
    private Button quickSearch;
    private Button clearHistory;
    private NavigationView navView;
    private ListView historyList;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            moveTaskToBack(true);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit app", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /**Initialize Screen*/
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_welcom_screen);


        /**Initialize UIs*/
        navView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navView.getHeaderView(0);
        searchQuery = (EditText) headerView.findViewById(R.id.searchBox) ;
        quickSearch = (Button) headerView.findViewById(R.id.searchButton);
        clearHistory = (Button) headerView.findViewById(R.id.clearHistory);
        historyList = headerView.findViewById(R.id.searchHistoryList);
        signOut = findViewById(R.id.signOut);
        createEvent = findViewById(R.id.createEventButton);
        allEventButton = findViewById(R.id.allEventButton);
        Button myEventButton = findViewById(R.id.myEventsButton);
        accountSettings = findViewById(R.id.accountSettingsButton);
        welcomeText = findViewById(R.id.welcomeText);
        toolbar = findViewById(R.id.toolbar);
        createEvent1 = findViewById(R.id.createEvent1);
        myEventButton1 = findViewById(R.id.myEventsButton1);
        allEventButton1 = findViewById(R.id.allEventButton1);
        accountSettings1 = findViewById(R.id.accountSettingsButton1);
        signout1 = findViewById(R.id.signOut1);


        final Context context = this.getApplicationContext();
        myAuth = FirebaseAuth.getInstance();
        /**set up search history*/
        if (myAuth == null) {
            Toast.makeText(welcomScreen.this, "Error... UID not available", Toast.LENGTH_LONG).show();
            return;
        }
        FirebaseDatabase.getInstance().getReference().child("users").child(myAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User currentUser = dataSnapshot.getValue(User.class);
                if (currentUser != null) {
                    List<String> searchHistoryArray = currentUser.getHistory();
                    ListAdapter historyAdapter = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,searchHistoryArray);
                    historyList.setAdapter(historyAdapter);
                }
            }
            public void onCancelled(DatabaseError databaseError) {
                Log.d("UserProfile", "Can't find user.");
            }
        });

        setSupportActionBar(toolbar);
        /**Show Welcome Text*/
        showWelcomeText();


        /**Set behavior to SignOut Button.*/
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
        signout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        /**Go to other screens*/
        myEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EventBrowser.class);
                intent.putExtra(EventBrowser.RECEIVE_EVENT_BEHAVIOR, EventBrowser.SEARCH_PERSON);
                intent.putExtra(EventBrowser.SEARCH_FOR, myAuth.getCurrentUser().getUid());
                startActivity(intent);
            }
        });
        myEventButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EventBrowser.class);
                intent.putExtra(EventBrowser.RECEIVE_EVENT_BEHAVIOR, EventBrowser.SEARCH_PERSON);
                intent.putExtra(EventBrowser.SEARCH_FOR, myAuth.getCurrentUser().getUid());
                startActivity(intent);
            }
        });
        accountSettings1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, userSettingsScreen.class);
                startActivity(intent);
            }
        });
        accountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, userSettingsScreen.class);
                startActivity(intent);
            }
        });
        createEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, createEventScreen.class);
                startActivity(intent);
            }
        });
        createEvent1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, createEventScreen.class);
                startActivity(intent);
            }
        });
        allEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EventBrowser.class);
                startActivity(intent);
            }
        });
        allEventButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EventBrowser.class);
                startActivity(intent);
            }
        });
        quickSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (myAuth == null) {
                    Toast.makeText(welcomScreen.this, "Error... UID not available", Toast.LENGTH_LONG).show();
                    return;
                }
                FirebaseDatabase.getInstance().getReference().child("users").child(myAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User currentUser = dataSnapshot.getValue(User.class);
                        if (currentUser != null){
                            if (!searchQuery.getText().toString().equals("")) {
                                currentUser.removeHistory(searchQuery.getText().toString());
                                currentUser.addHistory(searchQuery.getText().toString());
                            }
                            currentUser.pushToFirebase(new OnCompleteListener<Void>(){
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                }
                            });
                            List<String> searchHistoryArray = currentUser.getHistory();
                            ListAdapter historyAdapter = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,searchHistoryArray);
                            historyList.setAdapter(historyAdapter);
                            Intent intent = new Intent(context, EventBrowser.class);
                            intent.putExtra(EventBrowser.SEARCH_FOR,searchQuery.getText().toString());
                            intent.putExtra(EventBrowser.RECEIVE_EVENT_BEHAVIOR,EventBrowser.SEARCH_EVENTS);
                            startActivity(intent);
                        } else {
                            Toast.makeText(welcomScreen.this, "Error...", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("UserProfile", "Can't find user.");
                    }
                });

            }
        });
        clearHistory.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if (myAuth == null) {
                    Toast.makeText(welcomScreen.this, "Error... UID not available", Toast.LENGTH_LONG).show();
                    return;
                }
                FirebaseDatabase.getInstance().getReference().child("users").child(myAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User currentUser = dataSnapshot.getValue(User.class);
                        if (currentUser != null){
                            currentUser.clearHistory();
                            List<String> searchHistoryArray = currentUser.getHistory();
                            ListAdapter historyAdapter = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,searchHistoryArray);
                            historyList.setAdapter(historyAdapter);
                            currentUser.pushToFirebase(new OnCompleteListener<Void>(){
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                }
                            });
                        } else {
                            Toast.makeText(welcomScreen.this, "Error...", Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("UserProfile", "Can't find user.");
                    }
                });
            }
        });
    }

    /**
     * Sign Out
     */
    private void signOut() {
        myAuth.signOut();
        finish();
    }

    /**
     * Show welcome text by username
     */
    public void showWelcomeText() {
        String uid = myAuth.getCurrentUser().getUid();
        DatabaseReference id = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("userName");
        id.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.getValue(String.class);
                if (name == null) {
                    welcomeText.setText("Hello! But we can't find your nickname.");
                } else {
                    welcomeText.setText("Hello, " + name.toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                welcomeText.setText("Hello! But something's wrong.");
            }
        });
    }

}
