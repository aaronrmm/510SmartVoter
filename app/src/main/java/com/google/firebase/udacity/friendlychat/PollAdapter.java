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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PollAdapter extends ArrayAdapter<Poll> {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private Map<String, Poll>map;

    public PollAdapter(Context context, int resource, List<Poll> objects) {
        super(context, resource, objects);
        map = new HashMap<String, Poll>();
    }

    /*
        Will set up all of the Topic items (title, author, subscribe button) for the Topic List
     */
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

        //this will check the topic to see if the user is subscribed or not in order to set
        //the topic button to the correct settings (add/delete)
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
                    //do nothing, above may return null when a User has no record of
                    //being subscribed or unsubscribed to a topic
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        /*
            Sets the Click function for the Subscribe button.
            Depending on the current Tag (y/n) the button will subscribe
            or unsubscribe a user from the topic and flip the button.
         */
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

    /*
        This method subscribes a user to a topic
     */
    public void subscribeToTopic(View view) {
        String user_key = mFirebaseAuth.getCurrentUser().getUid();
        View pollView = ((View) view.getParent());
        String poll_key = (String) view.getTag();

        DatabaseReference user_sub = mFirebaseDatabase.getReference().child("subscriptions").child(user_key).child(poll_key);
        user_sub.setValue(true);
    }

    /*
        This method unsubscribes a user from a topic
     */
    public void unsubscribeFromTopic(View view) {
        String user_key = mFirebaseAuth.getCurrentUser().getUid();
        View pollView = ((View) view.getParent());
        String poll_key = (String) view.getTag();

        DatabaseReference user_sub = mFirebaseDatabase.getReference().child("subscriptions").child(user_key).child(poll_key);
        user_sub.setValue(false);
    }
    public Poll getPoll(String poll_id){
        return map.get(poll_id);
    }
    @Override public void add(Poll poll){
        super.add(poll);
        map.put(poll.getDbKey(), poll);
    }
    public void remove(String poll_id){
        this.remove(getPoll(poll_id));
    }

}
