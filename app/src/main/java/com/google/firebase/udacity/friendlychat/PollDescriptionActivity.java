package com.google.firebase.udacity.friendlychat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
//Temp for debugging
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private Button voteButton;
    private FirebaseAuth firebaseAuth;
    private String choice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll_description);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        poll_key = intent.getStringExtra(getString(R.string.poll_id));
        viewDiscussion = (Button)findViewById(R.id.viewDiscussionButton);
        voteButton = (Button)findViewById(R.id.voteButton);

        // Capture the layout's TextView and set the string as its text

        // Initialize Firebase components

        FirebaseDatabase mFirebaseDatabase;
        final DatabaseReference mMessagesDatabaseReference;
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = firebaseAuth.getInstance();
        mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("polls").child(poll_key);

        //TODO
        final DatabaseReference votesReference = mFirebaseDatabase.getReference().child("votes");

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
                voteButton.setEnabled(false); //disable until option is selected
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

        choice1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choice = choice1.getText().toString();
                voteButton.setEnabled(true);
            }
        });

        choice2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choice = choice2.getText().toString();
                voteButton.setEnabled(true);
            }
        });

        voteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uID = firebaseAuth.getUid().toString();

                //create new vote object and save to database
                Vote vote = new Vote(poll_key, uID, choice);
                DatabaseReference push = votesReference.push();
                push.setValue(vote);

                voteButton.setEnabled(false); //disable now that vote is in

                /*TEMP for debugging
                AlertDialog alertDialog = new AlertDialog.Builder(PollDescriptionActivity.this).create();
                alertDialog.setTitle("Alert");
                alertDialog.setMessage("Push: " + push + ", Vote: " + vote.getUser() + " " +  vote.getPollID() + " " +  vote.getVote());
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();*/
            }
        });
    }

    public void openDiscussion(View view){
        Intent intent = new Intent(this, Argument.class);
        intent.putExtra(getString(R.string.poll_id), poll_key);
        startActivity(intent);
    }

}
