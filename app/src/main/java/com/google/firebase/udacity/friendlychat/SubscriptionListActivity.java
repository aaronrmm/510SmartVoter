package com.google.firebase.udacity.friendlychat;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SubscriptionListActivity extends RestrictedActivity {

    public static final String ANONYMOUS = "anonymous";
    private ListView pollListView;
    private PollAdapter mPollAdaptor;
    private ProgressBar mProgressBar;
    // Firebase instance variables
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDBSubReference;
    private ChildEventListener mChildEventListener;
    private ChildEventListener mSubListener;
    private List<Poll> polls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_list);

        // Initialize Firebase components
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        pollListView = (ListView) findViewById(R.id.pollListView);

        // Initialize message ListView and its adapter
        polls = new ArrayList<>();
        mPollAdaptor = new PollAdapter(this, R.layout.item_poll, polls);
        pollListView.setAdapter(mPollAdaptor);
    }

    @Override
    public void onSignedInInitialize(String username) {
        String userid = mFirebaseAuth.getCurrentUser().getUid();
        mDBSubReference = mFirebaseDatabase.getReference().child("subscriptions").child(userid);
        attachSubscriptionListener();
    }

    private void attachSubscriptionListener() {
        if (mSubListener == null) {
            mSubListener = new ChildEventListener() {
                //loads the poll IDs user is subscribed to
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if (!(boolean)dataSnapshot.getValue())
                        return;
                    String poll_id = dataSnapshot.getKey();
                    Query query = mFirebaseDatabase.getReference().child("polls").child(poll_id);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                         @Override
                         public void onDataChange(DataSnapshot dataSnapshot) {
                             String poll_id = dataSnapshot.getKey();
                             Poll poll = dataSnapshot.getValue(Poll.class);
                             poll.setDbKey(poll_id);
                             mPollAdaptor.add(poll);
                         }

                         @Override
                         public void onCancelled(DatabaseError databaseError) {

                         }
                    });
                }

                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    boolean subscribed = (boolean)dataSnapshot.getValue();
                    String poll_id = dataSnapshot.getKey();
                    if (subscribed) {
                        Query query = mFirebaseDatabase.getReference().child("polls").child(poll_id);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String poll_id = dataSnapshot.getKey();
                                Poll poll = dataSnapshot.getValue(Poll.class);
                                poll.setDbKey(poll_id);
                                mPollAdaptor.add(poll);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                    else{
                        mPollAdaptor.remove(poll_id);
                    }
                }
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    String poll_id = dataSnapshot.getKey();
                    mPollAdaptor.remove(poll_id);
                }
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            mDBSubReference.addChildEventListener(mSubListener);
        }
    }

    public void openPoll(View view){
        View pollView = ((View)view.getParent());
        String poll_key = (String)pollView.getTag();
        Intent intent = new Intent(this, PollDescriptionActivity.class);
        intent.putExtra(getString(R.string.poll_id), poll_key);
        startActivity(intent);
    }

    public void openSearchPolls(View view){
        Intent intent = new Intent(this, SearchPollsActivity.class);
        startActivity(intent);
    }

    public void openPollCreation(View view){
        Intent intent = new Intent(this, PollCreationActivity.class);
        startActivity(intent);
    }
}
