package com.google.firebase.udacity.friendlychat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

public class PollCreationActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    private EditText mTitleText;
    private EditText mDescriptionText;
    private EditText mOption1Text;
    private EditText mOption2Text;
    private Button mPostPollButton;

    private PollCreationActivity.MutableBoolean titleHasText = new PollCreationActivity.MutableBoolean(false);
    private PollCreationActivity.MutableBoolean descriptionHasText = new PollCreationActivity.MutableBoolean(false);
    private PollCreationActivity.MutableBoolean option1HasText = new PollCreationActivity.MutableBoolean(false);
    private PollCreationActivity.MutableBoolean option2HasText = new PollCreationActivity.MutableBoolean(false);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll_creation);

        firebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("polls");

        mTitleText = (EditText) findViewById(R.id.titleEditText);
        mDescriptionText = (EditText) findViewById(R.id.descriptionEditText);
        mOption1Text = (EditText) findViewById(R.id.option1EditText);
        mOption2Text = (EditText) findViewById(R.id.option2EditText);
        mPostPollButton = (Button) findViewById(R.id.postPollButton);
        // Enable Send button when there's text to send
        attachTextListener(mTitleText, titleHasText);
        attachTextListener(mDescriptionText, descriptionHasText);
        attachTextListener(mOption1Text, option1HasText);
        attachTextListener(mOption2Text, option2HasText);

        mPostPollButton.setEnabled(false);
    }

    private void attachTextListener(EditText text, final MutableBoolean flag) {
        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    flag.True();
                    if (titleHasText.isTrue()
                            && descriptionHasText.isTrue()
                            && option1HasText.isTrue()
                            && option2HasText.isTrue()
                            ){
                        mPostPollButton.setEnabled(true);
                    }
                } else {
                    flag.False();
                    mPostPollButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    public void postPoll(View view){
        //create new poll
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String author = "";
        if (user!=null)
            author =  user.getDisplayName();
        String title = mTitleText.getText().toString();
        String description = mDescriptionText.getText().toString();
        String option1 = mOption1Text.getText().toString();
        String option2 = mOption2Text.getText().toString();

        Poll poll = new Poll(author, title, description, option1, option2);
        DatabaseReference push = mDatabaseReference.push();
        push.setValue(poll);

        String poll_key = push.getKey();
        String user_key = user.getUid();
        DatabaseReference user_sub = mFirebaseDatabase.getReference().child("subscriptions").child(user_key).child(poll_key);
        user_sub.setValue(true);

        //send user back to subscribed polls list
        Intent intent = new Intent(this, SubscriptionListActivity.class);
        startActivity(intent);

    }

    class MutableBoolean{

            public boolean value = false;

            MutableBoolean(boolean value){
                this.value = value;
            }

            public boolean isTrue(){
                return this.value;
            }

            public void True(){
                this.value = true;
            }
            public void False(){
                this.value=false;
            }
    }

}
