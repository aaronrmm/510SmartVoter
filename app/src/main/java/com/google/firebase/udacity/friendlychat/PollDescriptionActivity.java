package com.google.firebase.udacity.friendlychat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
    private Button viewDiscussion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll_description);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        poll_key = intent.getStringExtra(getString(R.string.poll_id));
        viewDiscussion = (Button)findViewById(R.id.postPollButton);

        // Capture the layout's TextView and set the string as its text

        // Initialize Firebase components

        FirebaseDatabase mFirebaseDatabase;
        DatabaseReference mMessagesDatabaseReference;
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("polls").child(poll_key);
        final TextView titleView = (TextView) findViewById(R.id.titleText);
        final TextView descriptionView = (TextView) findViewById(R.id.descriptionText);
        final RadioGroup choices = (RadioGroup) findViewById(R.id.choices);
        final RadioButton choice1 = (RadioButton) findViewById(R.id.choice1);
        final RadioButton choice2 = (RadioButton) findViewById(R.id.choice2);

        mMessagesDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Poll poll = dataSnapshot.getValue(Poll.class);
                System.out.print(poll);
                titleView.setText(poll.getTitle());
                descriptionView.setText(poll.getDescription());
                choice1.setText(poll.getOption1());
                choice2.setText(poll.getOption2());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        viewDiscussion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDiscussion(view);
            }
        });

    }

    public void openDiscussion(View view){
        Intent intent = new Intent(this, Argument.class);
        intent.putExtra(getString(R.string.poll_id), poll_key);
        startActivity(intent);
    }
}
