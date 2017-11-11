package vivekvaidya.com.lookingfor;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 11/1/2017.
 */

public class EventsAdapter extends ArrayAdapter {


    List list = new ArrayList();

    public EventsAdapter(@NonNull Context context, int resource) {
        super(context, resource);

    }

    static class DataHandler{
        ImageView eventImaage;
        TextView eventName;
        TextView eventLocation;
        TextView eventDate;
        TextView eventTime;

    }


    @Override
    public void add(@Nullable Object object) {
        super.add(object);
        list.add(object);
    }

    @Override
    public int getCount() {
        return this.list.size();
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return this.list.get(position);
    }


    public void remove(int position) {list.remove(position);}


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View row;
        row = convertView;
        DataHandler handler;

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.row_layout,parent,false);
            handler = new DataHandler();
            handler.eventImaage = (ImageView) row.findViewById(R.id.eventPicture);
            handler.eventName = (TextView) row.findViewById(R.id.ette);
            handler.eventDate = (TextView) row.findViewById(R.id.eventDate);
            handler.eventLocation = (TextView) row.findViewById(R.id.eventLocation);
            handler.eventTime = (TextView) row.findViewById(R.id.eventTime);

            row.setTag(handler);
        }
        else {
            handler = (DataHandler) row.getTag();
        }

        EventData data;
        data = (EventData) this.getItem(position);
        handler.eventImaage.setImageResource(data.getEventPictures());
        handler.eventName.setText(data.getEventName());
        handler.eventDate.setText(data.getEventDate());
        handler.eventTime.setText(data.getEventTime());
        handler.eventLocation.setText(data.getEventLocation());


        return row;
    }
}
