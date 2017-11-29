package vivekvaidya.com.lookingfor;

import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.util.List;


public class EventRowLayoutAdapter extends ArrayAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<Event> events = new ArrayList<>();
    private List<Event> fullEvents = new ArrayList<>();

    EventRowLayoutAdapter(Context mContext, int resource, List<Event> items) {
        super(mContext,resource);
        context = mContext;
        events = items;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        fullEvents = items;
        Log.d("Event number", String.valueOf(events.size()));
    }

    EventRowLayoutAdapter(Context mContext, int resource) {
        super(mContext,resource);

    }
    void setEvents(List<Event> events) {
        this.events = events;
    }

    List<Event> getFullEvents() {
        return fullEvents;
    }

    @Override
    public int getCount() {
        return events == null ? 0 : events.size();
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
        TextView dateLabel;
        TextView timeLabel;
        TextView locationLabel;
        ImageView hostAvatar;
        LinearLayout attendeeAvatarView;
        ImageView eventImage;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        final ViewHolder mViewHolder;
        if (convertView == null) {
            /**UI Variables*/
            mViewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.row_layout, viewGroup, false);
            mViewHolder.titleLabel = (TextView) convertView.findViewById(R.id.eventName);
            mViewHolder.dateLabel = (TextView) convertView.findViewById(R.id.eventDate);
            mViewHolder.timeLabel = (TextView) convertView.findViewById(R.id.eventTime);
            mViewHolder.locationLabel = (TextView) convertView.findViewById(R.id.eventLocation);
            mViewHolder.eventImage = (ImageView) convertView.findViewById(R.id.eventPicture);
            mViewHolder.hostAvatar = (ImageView) convertView.findViewById(R.id.hostAvatar);
            mViewHolder.attendeeAvatarView = (LinearLayout) convertView.findViewById(R.id.attendeeAvatarList);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();

        }
        //TODO: set UI properly
        /**Set UI Displays*/
        final Event currentEvent = events.get(i);
        mViewHolder.titleLabel.setText(currentEvent.getTitle());
        mViewHolder.dateLabel.setText(currentEvent.getDate());
        mViewHolder.timeLabel.setText(currentEvent.getTime());
        mViewHolder.locationLabel.setText(currentEvent.getLocation());

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

//       StorageReference storageReference = FirebaseStorage.getInstance().getReference();
//        StorageReference usersReference = storageReference.child("users");
//        StorageReference hostReference = usersReference.child(hostID);
//        mViewHolder.hostUsername.setText();


        List<String> attendeeID = new ArrayList<>();
        if (currentEvent.getAttendeeID() == null || currentEvent.getAttendeeID().size() == 0) {
            attendeeID.add(hostID);
        } else {
            attendeeID = currentEvent.getAttendeeID();
            if (attendeeID == null) {
                attendeeID.add(hostID);
            }
        }

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

        int counter = 0;
        if (mViewHolder.attendeeAvatarView.getChildCount() > 0) {
            mViewHolder.attendeeAvatarView.removeAllViews();
        }
        for(String id : attendeeID) {
            usersReference.child(id).child("avatar").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ImageView newAvatar = new ImageView(getContext());
                    newAvatar.setImageBitmap(User.stringToBitMap(dataSnapshot.getValue(String.class)));
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(50,50);
                    params.setMarginEnd(5);
                    newAvatar.setLayoutParams(params);
                    mViewHolder.attendeeAvatarView.addView(newAvatar);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            counter++;
            if (counter >= 5){
                break;
            }
        }
//        StorageReference hostAvatarPath = storageReference.child("userAvatar/" + hostID + ".png");
//        Glide.with(this.context).using(new FirebaseImageLoader()).load(hostAvatarPath).into(mViewHolder.hostAvatar);
//        StorageReference attendeeAvatarPath = storageReference.child("userAvatar/" + attendeeID + ".png");
//        Glide.with(this.context).using(new FirebaseImageLoader()).load(attendeeAvatarPath).into(mViewHolder.attendeeAvatar);

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
