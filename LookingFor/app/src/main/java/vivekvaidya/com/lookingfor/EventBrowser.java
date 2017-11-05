package vivekvaidya.com.lookingfor;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Dictionary;

public class EventBrowser extends AppCompatActivity implements CallableAfterDownload {

    public static final String RECEIVE_EVENT_BEHAVIOR = "behavior";
    public static final String EVENTS_TO_DISPLAY = "events";
    public static final int DISPLAY_ALL = 0;
    public static final int GET_EVENTS = 1;
    public static final int DISPLAY_EVENTS = 2;
    public static final int SEARCH_EVENTS = 3;
    public static final String EVENTS_RETURNED = "EventsReturned";
    public static final String SEARCH_FOR = "SearchFor";
    EventBrowserItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /**Initialize Activity*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_browser);


        /**Initialize Basic UIs*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        listView = (ListView) findViewById(R.id.eventListView);
//        contactAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, contactArray);

        Intent intent = getIntent();
        int behavior = intent.getIntExtra(RECEIVE_EVENT_BEHAVIOR,DISPLAY_ALL);
        //ArrayList<Event> events = intent.getParcelableArrayListExtra(EVENTS_TO_DISPLAY);
        downloadEvents(behavior/*,events*/);


    }

    public void downloadEvents(final int behavior/*, final ArrayList<Event> someEvents*/) {
        switch (behavior) {
            case SEARCH_EVENTS:
            case GET_EVENTS:
            case DISPLAY_ALL:
                EventDownloader.downloadEventsTo(behavior,this,this);
                break;
            case DISPLAY_EVENTS:
                ArrayList<Event> events = getIntent().getParcelableArrayListExtra(EVENTS_TO_DISPLAY);
                displayEvents(events);
            default:
                break;
        }

    }

    @Override
    public void eventsDownloaded(int behavior, ArrayList<Event> events) {
        switch (behavior) {
            case GET_EVENTS:
                Intent returnIntent = new Intent();
                returnIntent.putParcelableArrayListExtra(EVENTS_RETURNED, events);
                setResult(RESULT_OK, returnIntent);
                finish();
                break;
            case SEARCH_EVENTS:
                //TODO: put query into menu
                String query = getIntent().getStringExtra(SEARCH_FOR);
                displayEvents(events);
                adapter.setEvents(Event.searchForEvent(events,query));
                adapter.notifyDataSetChanged();
                break;
            case DISPLAY_ALL:
                displayEvents(events);
                break;
            default:
                break;
        }
    }

    public void displayEvents(ArrayList<Event> events) {
        adapter = new EventBrowserItemAdapter(this, events);
        ListView eventsListView = (ListView) findViewById(R.id.eventListView);
        eventsListView.setAdapter(adapter);
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_search, menu);

        MenuItem menuItem = menu.findItem(R.id.search_badge_ID);

        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.setEvents(Event.searchForEvent(adapter.getFullEvents(),newText));
                adapter.notifyDataSetChanged();
                return false;
            }

        });

        return super.onCreateOptionsMenu(menu);
    }

}
