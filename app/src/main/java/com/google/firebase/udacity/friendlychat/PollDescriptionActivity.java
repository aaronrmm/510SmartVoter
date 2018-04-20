package com.google.firebase.udacity.friendlychat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class PollDescriptionActivity extends AppCompatActivity {

    private String poll_key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll_description);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        poll_key = intent.getStringExtra(getString(R.string.poll_id));

        // Capture the layout's TextView and set the string as its text

        // Initialize Firebase components

        FirebaseDatabase mFirebaseDatabase;
        DatabaseReference mMessagesDatabaseReference;
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("polls").child(poll_key);
        final TextView titleView = (TextView) findViewById(R.id.titleEditText);
        final TextView descriptionView = (TextView) findViewById(R.id.descriptionEditText);
        final TextView option1View = (TextView) findViewById(R.id.option1EditText);
        final TextView option2View = (TextView) findViewById(R.id.option2EditText);

        mMessagesDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Poll poll = dataSnapshot.getValue(Poll.class);
                System.out.print(poll);
                titleView.setText(poll.getTitle());
                descriptionView.setText(poll.getDescription());
                option1View.setText(poll.getOption1());
                option2View.setText(poll.getOption2());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void openDiscussion(View view){
        Intent intent = new Intent(this, Argument.class);
        intent.putExtra(getString(R.string.poll_id), poll_key);
        startActivity(intent);
    }
}
