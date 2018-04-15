package com.google.firebase.udacity.friendlychat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewParent;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class SearchPollsActivity extends AppCompatActivity {

    public static final String ANONYMOUS = "anonymous";
    private ListView pollListView;
    private PollAdapter mPollAdaptor;
    private ProgressBar mProgressBar;
    // Firebase instance variables
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDatabaseReference;
    private ChildEventListener mChildEventListener;
    private FirebaseAuth mFirebaseAuth;
    private Button mSearchButton;
    private EditText mSearchText;
    private String queryText;
    private List<Poll> polls;

    private void updateAdapter() {
        mPollAdaptor = new PollAdapter(this, R.layout.item_poll, polls);
        pollListView.setAdapter(mPollAdaptor);
        attachDatabaseReadListener();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_polls);

        // Initialize Firebase components
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("polls");
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        pollListView = (ListView) findViewById(R.id.pollListView);
        mSearchButton = (Button) findViewById(R.id.button7);
        mSearchText = (EditText) findViewById(R.id.searchInputText);

        // Initialize message ListView and its adapter
        polls = new ArrayList<>();
        mPollAdaptor = new PollAdapter(this, R.layout.item_poll, polls);
        pollListView.setAdapter(mPollAdaptor);
        attachDatabaseReadListener();

        // Enable search button when there's text to send
        mSearchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mSearchButton.setEnabled(true);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSearchButton.setEnabled(true);
                } else {
                    mSearchButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                mSearchButton.setEnabled(true);
            }
        });

        // Search button sends a message and clears the EditText
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Search for keyword in Topics and display
                final String keyword = mSearchText.getText().toString();
                if (keyword.equalsIgnoreCase("")) {
                    //blank search, refresh list to default
                    finish();
                    startActivity(getIntent());
                } else {
                    //create a new list with topics ordered according to the entered keyword
                    polls.clear();
                    Query query = mMessagesDatabaseReference;
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {

                                // set up snapshot iterator
                                Iterable<DataSnapshot> topics = dataSnapshot.getChildren();
                                ArrayList<Poll> ps = new ArrayList<Poll>();

                                // convert DataSnapshot into editable list
                                for (DataSnapshot d : topics) {
                                    Poll poll = d.getValue(Poll.class);
                                    System.out.println(d.getKey());
                                    poll.setDbKey(d.getKey());
                                    ps.add(poll);
                                }

                                // check for exact title matches (top priority, will be first on list)
                                for (int i = ps.size() - 1; i >= 0; i--) {
                                    if (ps.get(i).getTitle().trim().equalsIgnoreCase(keyword.trim())) {
                                        polls.add(ps.get(i));
                                        ps.remove(i);
                                    }
                                }

                                // check for match of keyword in description (middle priority, next on list)
                                for (int i = ps.size() - 1; i >= 0; i--) {
                                    String[] desc = ps.get(i).getDescription().split(" ");
                                    for (String s : desc) {
                                        if (s.trim().equalsIgnoreCase(keyword.trim())) {
                                            polls.add(ps.get(i));
                                            ps.remove(i);
                                        }
                                    }
                                }

                                // check for match of any keyword to any title or description (lowest priority, last on list)
                                String[] keywords = keyword.split(" ");
                                for (int i = ps.size() - 1; i >= 0; i--) {
                                    ArrayList<String> titles = new ArrayList<String>(Arrays.asList(ps.get(i).getTitle().split(" ")));
                                    ArrayList<String> desc = new ArrayList<String>(Arrays.asList(ps.get(i).getDescription().split(" ")));
                                    desc.addAll(titles);
                                    for (String k : keywords) {
                                        boolean m = false;
                                        for (String d : desc) {
                                            if (d.equalsIgnoreCase(k)) {
                                                polls.add(ps.get(i));
                                                m = true;
                                                break;
                                            }
                                        }
                                        if (m) {
                                            break;
                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    //show top 100 results, or all results if total is less than 100
                    polls = polls.subList(0, polls.size() < 100 ? polls.size() : 100);
                    // show the query results list
                    updateAdapter();
                    // Clear input box
                    mSearchText.setText("");
                }

                // hide keyboard after click
                InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

        /* Not needed?
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }*/
    }

    private void attachDatabaseReadListener() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    String poll_id = dataSnapshot.getKey();
                    Poll poll = dataSnapshot.getValue(Poll.class);
                    poll.setDbKey(poll_id);
                    System.out.println(poll.getDbKey());
                    mPollAdaptor.add(poll);
                    updateAdapter();
                }

                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    updateAdapter();
                }
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Poll poll = dataSnapshot.getValue(Poll.class);
                    mPollAdaptor.remove(poll);
                }
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    updateAdapter();
                }
                public void onCancelled(DatabaseError databaseError) {
                    updateAdapter();
                }
            };
            mMessagesDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }

    private void detachDatabaseReadListener() {
        if (mChildEventListener != null) {
            mMessagesDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

    public void subscribe(View view){
        String user_key = mFirebaseAuth.getCurrentUser().getUid();
        View pollView = ((View)view.getParent());
        String poll_key = (String)pollView.getTag();

        DatabaseReference user_sub = mFirebaseDatabase.getReference().child("subscriptions").child(user_key).child(poll_key);
        user_sub.setValue(true);
    }
}
