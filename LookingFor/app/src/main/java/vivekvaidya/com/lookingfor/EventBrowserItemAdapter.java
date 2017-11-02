package vivekvaidya.com.lookingfor;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by apple on 11/01/17.
 */

public class EventBrowserItemAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<Event> events;

    public EventBrowserItemAdapter(Context mContext, ArrayList<Event> items) {
        context = mContext;
        events = items;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public Object getItem(int i) {
        return events.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        /**UI Variables*/
        View myView = inflater.inflate(R.layout.event_item_layout, viewGroup, false);
        TextView titleLabel = (TextView) myView.findViewById(R.id.titleLabel);
        TextView dateTimeLabel = (TextView) myView.findViewById(R.id.dateTimeLabel);
        TextView typeLabel = (TextView) myView.findViewById(R.id.typeLabel);
        TextView locationLabel = (TextView) myView.findViewById(R.id.locationLabel);
        TextView descriptionLabel = (TextView) myView.findViewById(R.id.descriptionLabel);
        TextView tagLabel = (TextView) myView.findViewById(R.id.tagLabel);
        final ImageView hostAvatar = (ImageView) myView.findViewById(R.id.hostAvatar);
        ImageView attendeeAvatar = (ImageView) myView.findViewById(R.id.attendeeImage);
        Button attendEventButton = (Button) myView.findViewById((R.id.attendEventButton));

        /**Set UI Displays*/
        Event currentEvent = events.get(i);
        titleLabel.setText(currentEvent.getTitle());
        dateTimeLabel.setText(currentEvent.getDateTime());
        typeLabel.setText(currentEvent.getEventType());
        locationLabel.setText(currentEvent.getLocation());
        descriptionLabel.setText(currentEvent.getDescription());
        tagLabel.setText("Dummy Label");

        /**Get Avatars*/

        String hostID = currentEvent.getHostID();
        if (hostID == null) {
            hostID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        String attendeeID;
        if (currentEvent.getAttendeeID() == null || currentEvent.getAttendeeID().length == 0) {
            attendeeID = hostID;
        } else {
            attendeeID = currentEvent.getAttendeeID()[0];
            if (attendeeID == null) {
                attendeeID = hostID;
            }
        }
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference hostAvatarPath = storageReference.child("userAvatar/" + hostID + ".png");
        Glide.with(this.context).using(new FirebaseImageLoader()).load(hostAvatarPath).into(hostAvatar);
        StorageReference attendeeAvatarPath = storageReference.child("userAvatar/" + attendeeID + ".png");
        Glide.with(this.context).using(new FirebaseImageLoader()).load(attendeeAvatarPath).into(attendeeAvatar);

        attendEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * TODO: Attend the event and push to database
                 */
            }
        });

        return myView;
    }
}
