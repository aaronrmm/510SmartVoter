package com.google.firebase.udacity.friendlychat;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class PollAdapter extends ArrayAdapter<Poll> {
    public PollAdapter(Context context, int resource, List<Poll> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_poll, parent, false);
        }

        TextView titleTextView = (TextView) convertView.findViewById(R.id.titleTextView);
        TextView authorTextView = (TextView) convertView.findViewById(R.id.authorTextView);

        Poll poll = getItem(position);
        titleTextView.setText(poll.getTitle());
        Object tag = poll.getDbKey();
        convertView.setTag(tag);
        Object gotTag = convertView.getTag();

        authorTextView.setText(poll.getAuthor());

        return convertView;
    }
}
