package vivekvaidya.com.lookingfor;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DisplayEvents extends AppCompatActivity {

     ListView listview;

    ArrayList<Integer> eventImageResource = new ArrayList<Integer>();
    ArrayList<String> Name = new ArrayList<String>();
    ArrayList<String> Date = new ArrayList<String>();
    ArrayList<String> Time = new ArrayList<String>();
    ArrayList<String> Location = new ArrayList<String>();

//
//     int[] eventImageResource = {R.drawable.baseball, R.drawable.bball, R.drawable.tennis, R.drawable.xbox, R.drawable.wii};
//     String[] Name = {"Event Name: Baseball", "Event Name: Basketball", "Event Name: Tennis", "Event Name: Xbox", "Event Name: Wii"};
//     String[] Participants = {""};
//     String[] Date = {"Date: 11/1", "Date: 11/2", "Date: 11/3", "Date: 11/4", "Date: 11/5" };
//     String[] Time = {"Time: 1:00", "Time: 2:30", "Time: 3:30", "Time: 4:30", "Time: 5:30"};
//     String[] Location = {"Location: ARC", "Location:  CRCE", "Location:  ISR", "Location:  PAR", "Location:  Siebel"};

     EventsAdapter adapter;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_events);

        listview = (ListView) findViewById(R.id.eventsList);
        adapter = new EventsAdapter(getApplicationContext(), R.layout.row_layout);
        listview.setAdapter(adapter);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("events").child("storage");
//        DatabaseReference users = mDatabase.getReference().child("events").child("storage");


//        eventImageResource.add(R.drawable.baseball);
//        Name.add("Event Name: Baseball");
//        Date.add("Date: 10/1/2019");
//        Time.add("Time: 10:45AM");
//        Location.add("ISR");
//
//        SwipeDismissListViewTouchListener touchListener =
//                new SwipeDismissListViewTouchListener(
//                        listview,
//                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
//                            @Override
//                            public boolean canDismiss(int position) {
//                                return true;
//                            }
//
//                            @Override
//                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
//                                for (int position : reverseSortedPositions) {
//
//                                    eventImageResource.remove(position);
//                                    Name.remove(position);
//                                    Date.remove(position);
//                                    Time.remove(position);
//
//                                    Location.remove(position);
//                                    Object toRemove = adapter.getItem(position);
//                                    adapter.remove(toRemove);
//                                    adapter.notifyDataSetChanged();
//
//                                }
//
//
//                            }
//                        });
//        listview.setOnTouchListener(touchListener);
        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        listview,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {

                                    int i = adapter.getCount();


                                    adapter.remove(position);
                                    Toast.makeText(DisplayEvents.this, String.valueOf(i), Toast.LENGTH_SHORT).show();

                                    adapter.notifyDataSetChanged();

                                }


                            }
                        });
        listview.setOnTouchListener(touchListener);



        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Toast.makeText(DisplayEvents.this, "CALED!", Toast.LENGTH_SHORT).show();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                    eventImageResource.add(R.drawable.baseball);
                    Name.add("Event Name: " + postSnapshot.child("title").getValue(String.class));
                    Date.add("Date: " + postSnapshot.child("dateTime").getValue(String.class));
                    Location.add("Location: " + postSnapshot.child("location").getValue(String.class));
                    Time.add("Time:" + postSnapshot.child("dateTime").getValue(String.class));


                    adapter.notifyDataSetChanged();
                }
                int i = 0;
                for(String titles: Name) {

                    EventData eventCard = new EventData(eventImageResource.get(i), titles, Date.get(i), Time.get(i), Location.get(i));
                    Toast.makeText(DisplayEvents.this, String.valueOf(i), Toast.LENGTH_SHORT).show();
//            EventData eventCard = new EventData(eventImageResource.get(i), titles, Date.get(i), Time.get(i), Location.get(i));
//            EventData eventCard = new EventData(eventImageResource[i], titles, Date[i], Time[i], Location[i]);
                    adapter.add(eventCard);
                    i++;
                }

            }



            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });









    }
}
