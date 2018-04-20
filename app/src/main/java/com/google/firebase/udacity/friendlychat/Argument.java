package com.google.firebase.udacity.friendlychat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class Argument extends AppCompatActivity {
    //layout variable
    private ListView listview;
    private Button button;
    private EditText etText;
    private String poll_key;

    //Firebase
    private DatabaseReference mDatabase;
    private FirebaseAuth mFirebaseAuth;
    //List
    private ArrayList<String> data = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_argument);

        Intent intent = getIntent();
        poll_key = intent.getStringExtra(getString(R.string.poll_id));

        mDatabase = FirebaseDatabase.getInstance().getReference().child("polls").child(poll_key).child("arguments");
        mFirebaseAuth = FirebaseAuth.getInstance();

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data);

        listview = (ListView) findViewById(R.id.list_view);
        button = (Button) findViewById(R.id.submit);
        etText = (EditText) findViewById(R.id.etArguments);

        listview.setAdapter(adapter);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String user_key = mFirebaseAuth.getCurrentUser().getUid();
                mDatabase.push().setValue(etText.getText().toString());
            }
        });

        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
               // String key=dataSnapshot.getKey();

                String argument = dataSnapshot.getValue(String.class);
                data.add(argument);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
               // String key=dataSnapshot.getKey();

                String argument = dataSnapshot.getValue(String.class);
                data.remove(argument);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

