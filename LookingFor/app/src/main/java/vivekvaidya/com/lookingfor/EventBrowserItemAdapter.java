package vivekvaidya.com.lookingfor;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by apple on 11/01/17.
 */

public class EventBrowserItemAdapter extends BaseAdapter implements ListAdapter {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<Event> events;
    private ArrayList<Event> fullEvents;

    public EventBrowserItemAdapter(Context mContext, ArrayList<Event> items) {
        context = mContext;
        events = items;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        fullEvents = items;
    }

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    public ArrayList<Event> getFullEvents() {
        return fullEvents;
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

    static class ViewHolder {
        TextView titleLabel;
        TextView dateTimeLabel;
        TextView typeLabel;
        TextView locationLabel;
        TextView descriptionLabel;
        TextView tagLabel;
        ImageView hostAvatar;
        ImageView attendeeAvatar;
        Button attendEventButton;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        ViewHolder mViewHolder;
        if (convertView == null) {
            /**UI Variables*/
            mViewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.event_item_layout, viewGroup, false);
            mViewHolder.titleLabel = (TextView) convertView.findViewById(R.id.titleLabel);
            mViewHolder.dateTimeLabel = (TextView) convertView.findViewById(R.id.dateTimeLabel);
            mViewHolder.typeLabel = (TextView) convertView.findViewById(R.id.typeLabel);
            mViewHolder.locationLabel = (TextView) convertView.findViewById(R.id.locationLabel);
            mViewHolder.descriptionLabel = (TextView) convertView.findViewById(R.id.descriptionLabel);
            mViewHolder.tagLabel = (TextView) convertView.findViewById(R.id.tagLabel);
            mViewHolder.hostAvatar = (ImageView) convertView.findViewById(R.id.hostAvatar);
            mViewHolder.attendeeAvatar = (ImageView) convertView.findViewById(R.id.attendeeImage);
            mViewHolder.attendEventButton = (Button) convertView.findViewById((R.id.attendEventButton));
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();

        }

        /**Set UI Displays*/
        Event currentEvent = events.get(i);
        mViewHolder.titleLabel.setText(currentEvent.getTitle());
        mViewHolder.dateTimeLabel.setText(currentEvent.getDateTime());
        mViewHolder.typeLabel.setText(currentEvent.getEventType());
        mViewHolder.locationLabel.setText(currentEvent.getLocation());
        mViewHolder.descriptionLabel.setText(currentEvent.getDescription());
        mViewHolder.tagLabel.setText("Dummy Label");

        /**Get Avatars*/

        String hostID = currentEvent.getHostID();

        if (hostID == null) {
            FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
            if (auth != null){
                hostID = auth.getUid();
            } else {
                Toast.makeText(context,"No current User", Toast.LENGTH_LONG).show();
                return convertView;
            }
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
        Glide.with(this.context).using(new FirebaseImageLoader()).load(hostAvatarPath).into(mViewHolder.hostAvatar);
        StorageReference attendeeAvatarPath = storageReference.child("userAvatar/" + attendeeID + ".png");
        Glide.with(this.context).using(new FirebaseImageLoader()).load(attendeeAvatarPath).into(mViewHolder.attendeeAvatar);

        mViewHolder.attendEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * TODO: Attend the event and push to database
                 */
            }
        });

        return convertView;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return super.areAllItemsEnabled();
    }

    @Override
    public boolean isEnabled(int position) {
        return super.isEnabled(position);
    }
}
