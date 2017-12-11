package vivekvaidya.com.lookingfor;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Map;

public class DetailScreen extends AppCompatActivity {
    public static final String DISPLAY_EVENT = "Event";
    private Event currentEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_screen);
        final Context context = this.getApplicationContext();
        Intent intent = getIntent();
        currentEvent = intent.getParcelableExtra(DISPLAY_EVENT);
        TextView titleLabel = findViewById(R.id.titleLabel);
        TextView dateTimeLabel = findViewById(R.id.dateTimeLabel);
        TextView typeLabel = findViewById(R.id.typeLabel);//TODO:type label
        TextView locationLabel = findViewById(R.id.locationLabel);
        TextView descriptionLabel = findViewById(R.id.descriptionLabel);
        TextView tagLabel = findViewById(R.id.tagLabel);
        final ImageView hostAvatar = findViewById(R.id.hostAvatar);
        final ImageView attendeeAvatar = findViewById(R.id.attendeeImage);
        Button attendEventButton = findViewById((R.id.attendEventButton));
        final TextView hostUsername = findViewById(R.id.hostUserNameLabel);

        titleLabel.setText(currentEvent.getTitle());
        dateTimeLabel.setText(currentEvent.getDateTime());
        ArrayList<String> tags = currentEvent.getTags();
        StringBuilder sb = new StringBuilder();
        for (String tag : tags) {
            sb.append(tag);
            sb.append(" ");
        }
        tagLabel.setText(sb.toString());
        locationLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(context, MapActivity.class);
                startActivity(intent1);
            }
        });
        descriptionLabel.setText(currentEvent.getDescription());

        String hostID = currentEvent.getHostID();
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (hostID == null) {
            if (currentUser != null) {
                hostID = currentUser.getUid();
            } else {
                Toast.makeText(context, "No current User", Toast.LENGTH_LONG).show();
                return;
            }
        }
        String attendeeID;
        if (currentEvent.getAttendeeID() == null || currentEvent.getAttendeeID().size() == 0) {
            attendeeID = hostID;
        } else {
            attendeeID = currentEvent.getAttendeeID().get(0);
            if (attendeeID == null) {
                attendeeID = hostID;
            }
        }

        DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference().child("users");
        usersReference.child(hostID).child("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String someString = dataSnapshot.getValue(String.class);
                if (someString != null)
                hostUsername.setText(someString);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        usersReference.child(hostID).child("avatar").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hostAvatar.setImageBitmap(User.stringToBitMap(dataSnapshot.getValue(String.class)));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        usersReference.child(attendeeID).child("avatar").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                attendeeAvatar.setImageBitmap(User.stringToBitMap(dataSnapshot.getValue(String.class)));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        attendEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean flag = currentEvent.attendEvent(currentUser.getUid(), new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        Toast.makeText(context, "Event attended", Toast.LENGTH_LONG).show();
                    }
                });
                if (!flag) {
                    Toast.makeText(context, "Failed attending event", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
