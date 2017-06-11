package com.rnd.snapsplit;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.ArrayList;

/**
 * Created by Damian on 31/5/2017.
 */

public class ConfirmationActivity extends ListActivity {
    ArrayList<Friend> selectedFriends = new ArrayList<Friend>();
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        this.selectedFriends = (ArrayList<Friend>) bundle.getSerializable("selectedFriends");
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, GoogleAccSignInActivity.class));
            finish();
            return;
        } else {
        }


        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirmation_activity);
        TextView title = (TextView) findViewById(R.id.text_title);
        title.setText("Confirmation");
        TextView each = (TextView) findViewById(R.id.text_each_price);
        each.setText("HKD"+ String.format("%.2f",bundle.getSerializable("each")));
        TextView selectedNo = (TextView) findViewById(R.id.text_selected_friends_no);
        selectedNo.setText(String.valueOf(selectedFriends.size()));
        TextView total = (TextView) findViewById(R.id.text_summary_amount);
        float totalAmount = (float) bundle.getSerializable("total");
        total.setText("HKD"+ String.format("%.2f",totalAmount));

        View tb = (View) findViewById(R.id.tool_bar);
        ImageButton frontButton = (ImageButton) tb.findViewById(R.id.btn_next);
        frontButton.setVisibility(View.INVISIBLE);

        final ImageButton backButton = (ImageButton) findViewById(R.id.btn_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backButton.setImageResource(R.drawable.back_green);
                finish();
            }
        });

        final Button sendButton = (Button) findViewById(R.id.button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequestsToFirebase();
            }
        });

        this.setListAdapter(new FriendListAdapter(this, R.layout.confirmation_list, selectedFriends))
        ;
    }

    protected void sendRequestsToFirebase(){
        if (selectedFriends != null) {
            for(int i =0; i<selectedFriends.size(); i++){
                selectedFriends.get(i).timestampNow.put("timestamp",ServerValue.TIMESTAMP);
                mFirebaseDatabaseReference.child("messages").push().setValue(selectedFriends.get(i));
            }

            RelativeLayout rl1 = (RelativeLayout) findViewById(R.id.relative_summary);
            RelativeLayout rl2 = (RelativeLayout) findViewById(R.id.relative_fdList);
            Button sendButton = (Button) findViewById(R.id.button);
            RelativeLayout requestView = (RelativeLayout) findViewById(R.id.request_sent);

            rl1.setVisibility(View.INVISIBLE);
            rl2.setVisibility(View.INVISIBLE);
            sendButton.setVisibility(View.INVISIBLE);
            requestView.setVisibility(View.VISIBLE);
            mFirebaseAnalytics.logEvent("message_sent", null);

        }

    }
}


