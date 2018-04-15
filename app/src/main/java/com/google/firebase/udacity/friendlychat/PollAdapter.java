package com.google.firebase.udacity.friendlychat;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class PollAdapter extends ArrayAdapter<Poll> {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDatabaseReference;
    private FirebaseAuth mFirebaseAuth;

    public PollAdapter(Context context, int resource, List<Poll> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_poll, parent, false);
        }

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        TextView titleTextView = (TextView) convertView.findViewById(R.id.titleTextView);
        TextView authorTextView = (TextView) convertView.findViewById(R.id.authorTextView);

        Poll poll = getItem(position);
        titleTextView.setText(poll.getTitle());
        Object tag = poll.getDbKey();
        convertView.setTag(tag);
        Object gotTag = convertView.getTag();
        final Button subscribe = convertView.findViewById(R.id.subscribeButton);

        final String user_key = mFirebaseAuth.getCurrentUser().getUid();
        final String poll_key = tag.toString();
        DatabaseReference user_sub = mFirebaseDatabase.getReference().child("subscriptions").child(user_key).child(poll_key);

        user_sub.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    boolean subbed = dataSnapshot.getValue(Boolean.class);
                    if (subbed) {
                        // subscribe to topic
                        subscribe.setTag("y");
                        //change color without changing button size
                        subscribe.getBackground().setColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY);
                        subscribe.setText("X");
                    } else {
                        // set button to allow subscription
                        subscribe.setTag("n");
                        //change color without changing button size
                        subscribe.getBackground().clearColorFilter();
                        subscribe.setText("+");
                    }
                } catch (NullPointerException e) {
                    //do nothing
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        subscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (subscribe.getTag().toString().equalsIgnoreCase("n")) {
                    // subscribe to topic
                    subscribeToTopic((View) view.getParent());
                    //change button
                    subscribe.setTag("y");
                    subscribe.getBackground().setColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY);
                    subscribe.setText("X");
                } else {
                    // unsubscribe to topic
                    unsubscribeFromTopic((View) view.getParent());
                    //change button
                    subscribe.setTag("n");
                    subscribe.getBackground().clearColorFilter();
                    subscribe.setText("+");
                }
            }
        });

        authorTextView.setText(poll.getAuthor());

        return convertView;
    }

    public void subscribeToTopic(View view) {
        String user_key = mFirebaseAuth.getCurrentUser().getUid();
        View pollView = ((View) view.getParent());
        String poll_key = (String) view.getTag();

        DatabaseReference user_sub = mFirebaseDatabase.getReference().child("subscriptions").child(user_key).child(poll_key);
        user_sub.setValue(true);
    }

    public void unsubscribeFromTopic(View view) {
        String user_key = mFirebaseAuth.getCurrentUser().getUid();
        View pollView = ((View) view.getParent());
        String poll_key = (String) view.getTag();

        DatabaseReference user_sub = mFirebaseDatabase.getReference().child("subscriptions").child(user_key).child(poll_key);
        user_sub.setValue(false);
    }
}
