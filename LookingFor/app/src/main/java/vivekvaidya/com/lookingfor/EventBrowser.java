package vivekvaidya.com.lookingfor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import java.util.ArrayList;

public class EventBrowser extends AppCompatActivity implements CallableAfterDownload {

    public static final String RECEIVE_EVENT_BEHAVIOR = "behavior";
    public static final String EVENTS_TO_DISPLAY = "events";
    public static final int DISPLAY_ALL = 0;
    public static final int GET_EVENTS = 1;
    public static final int DISPLAY_EVENTS = 2;
    public static final int SEARCH_EVENTS = 3;
    public static final int SEARCH_PERSON = 4;
    public static final String EVENTS_RETURNED = "EventsReturned";
    public static final String SEARCH_FOR = "SearchFor";
    EventBrowserItemAdapter adapter;
    Menu myMenu = null;

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
            case SEARCH_PERSON:
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
            case SEARCH_PERSON:
                String uid = getIntent().getStringExtra(SEARCH_FOR);
                events = Event.searchForUserAttendedEvents(events,uid);
                displayEvents(events);
                break;
            case GET_EVENTS:
                Intent returnIntent = new Intent();
                returnIntent.putParcelableArrayListExtra(EVENTS_RETURNED, events);
                setResult(RESULT_OK, returnIntent);
                finish();
                break;
            case SEARCH_EVENTS:
                String query = getIntent().getStringExtra(SEARCH_FOR);
                displayEvents(events);
                SearchView searchView = (SearchView) myMenu.findItem(R.id.search_badge_ID).getActionView();
                searchView.setQuery(query,true);
                //TODO: focus on searchView
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
        myMenu = menu;
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
