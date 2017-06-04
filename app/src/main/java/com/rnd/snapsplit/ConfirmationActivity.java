package com.rnd.snapsplit;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Damian on 31/5/2017.
 */

public class ConfirmationActivity extends ListActivity {
    ArrayList<Friend> selectedFriends = new ArrayList<Friend>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        this.selectedFriends = (ArrayList<Friend>) bundle.getSerializable("selectedFriends");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirmation_activity);
        TextView title = (TextView) findViewById(R.id.text_title);
        title.setText("Confirmation");
        TextView each = (TextView) findViewById(R.id.text_each_price);
        each.setText("HKD"+ String.format("%.2f",bundle.getSerializable("each")));
        TextView selectedNo = (TextView) findViewById(R.id.text_selected_friends_no);
        selectedNo.setText(String.valueOf(selectedFriends.size()));
        TextView total = (TextView) findViewById(R.id.text_summary_amount);
        total.setText("HKD"+ String.format("%.2f",bundle.getSerializable("total")));

        final ImageButton backButton = (ImageButton) findViewById(R.id.btn_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backButton.setImageResource(R.drawable.back_green);
                finish();
            }
        });

        this.setListAdapter(new FriendListAdapter(this, R.layout.list_confirmation, selectedFriends));
    }
}
