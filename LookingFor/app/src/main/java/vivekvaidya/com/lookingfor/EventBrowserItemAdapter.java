package vivekvaidya.com.lookingfor;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
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

import org.w3c.dom.Text;

import java.util.ArrayList;


public class EventBrowserItemAdapter extends BaseAdapter implements ListAdapter {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<Event> events;
    private ArrayList<Event> fullEvents;

    EventBrowserItemAdapter(Context mContext, ArrayList<Event> items) {
        context = mContext;
        events = items;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        fullEvents = items;
    }

    void setEvents(ArrayList<Event> events) {
        this.events = events;
    }

    ArrayList<Event> getFullEvents() {
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
        TextView hostUsername;
        ImageView hostAvatar;
        ImageView attendeeAvatar;
        Button attendEventButton;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        final ViewHolder mViewHolder;
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
            mViewHolder.hostUsername = (TextView) convertView.findViewById(R.id.hostUserNameLabel);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();

        }
        //TODO: set UI properly
        /**Set UI Displays*/
        final Event currentEvent = events.get(i);
        mViewHolder.titleLabel.setText(currentEvent.getTitle());
        mViewHolder.dateTimeLabel.setText(currentEvent.getDateTime());
        ArrayList<String> tags = currentEvent.getTags();
        StringBuilder sb = new StringBuilder();
        for (String tag: tags) {
            sb.append(tag);
            sb.append(" ");
        }
        mViewHolder.tagLabel.setText(sb.toString());
        mViewHolder.locationLabel.setText(currentEvent.getLocation());
        mViewHolder.descriptionLabel.setText(currentEvent.getDescription());
        /**Get Avatars*/

        String hostID = currentEvent.getHostID();
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (hostID == null) {
            if (currentUser != null){
                hostID = currentUser.getUid();
            } else {
                Toast.makeText(context,"No current User", Toast.LENGTH_LONG).show();
                return convertView;
            }
        }

       StorageReference storageReference = FirebaseStorage.getInstance().getReference();
//        StorageReference usersReference = storageReference.child("users");
//        StorageReference hostReference = usersReference.child(hostID);
//        mViewHolder.hostUsername.setText();

        String attendeeID;
        if (currentEvent.getAttendeeID() == null || currentEvent.getAttendeeID().size() == 0) {
            attendeeID = hostID;
        } else {
            attendeeID = currentEvent.getAttendeeID().get(0);
            if (attendeeID == null) {
                attendeeID = hostID;
            }
        }
        //TODO: getImage
        DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference().child("users");
        usersReference.child(hostID).child("avatar").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mViewHolder.hostAvatar.setImageBitmap(User.stringToBitMap(dataSnapshot.getValue(String.class)));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        usersReference.child(attendeeID).child("avatar").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mViewHolder.attendeeAvatar.setImageBitmap(User.stringToBitMap(dataSnapshot.getValue(String.class)));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//        StorageReference hostAvatarPath = storageReference.child("userAvatar/" + hostID + ".png");
//        Glide.with(this.context).using(new FirebaseImageLoader()).load(hostAvatarPath).into(mViewHolder.hostAvatar);
//        StorageReference attendeeAvatarPath = storageReference.child("userAvatar/" + attendeeID + ".png");
//        Glide.with(this.context).using(new FirebaseImageLoader()).load(attendeeAvatarPath).into(mViewHolder.attendeeAvatar);

        mViewHolder.attendEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean flag = currentEvent.attendEvent(currentUser.getUid(), new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        Toast.makeText(context,"Event attended", Toast.LENGTH_LONG).show();
                    }
                });
                if (!flag) {
                    Toast.makeText(context,"Failed attending event", Toast.LENGTH_SHORT).show();
                }
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
