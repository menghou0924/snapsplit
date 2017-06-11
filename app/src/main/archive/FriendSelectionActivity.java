package com.rnd.snapsplit;

/**
 * Created by Damian on 28/5/2017.
 */

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class FriendSelectionActivity extends ListActivity {

    ArrayList<Friend> selectedFriends = new ArrayList<Friend>();
    Summary splitSummary;

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        // Log.d("myTag", splitSummary.getTransactionAmount() +"  " + splitSummary.getTransactionName());
        // Toast.makeText(this, "Item #" + position + " clicked", Toast.LENGTH_SHORT).show();
        Friend f = (Friend)l.getItemAtPosition(position);
        if (selectedFriends.contains(f)){
            selectedFriends.remove(f);
            updateAmounts(l);
            TextView tv = (TextView) v.findViewById(R.id.splitAmount);
            tv.setVisibility(View.INVISIBLE);
            ImageView iv = (ImageView) v.findViewById(R.id.fdTick);
            iv.setImageResource(R.drawable.tick_black);
        }
        else{
            selectedFriends.add(f);
            updateAmounts(l);
            TextView tv = (TextView) v.findViewById(R.id.splitAmount);
            tv.setVisibility(View.VISIBLE);
            ImageView iv = (ImageView) v.findViewById(R.id.fdTick);
            iv.setImageResource(R.drawable.tick_green);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        final Context context = this;

        this.splitSummary = (Summary) bundle.getSerializable("splitSummary");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_selection_activity);

        TextView description = (TextView) findViewById(R.id.text_summary_shop);
        description.setText(splitSummary.getTransactionName());

        TextView amount = (TextView) findViewById(R.id.text_summary_amount_value);
        String s = Float.toString(splitSummary.getTransactionAmount());
        amount.setText(s);

        ArrayList<Friend> fdList = new ArrayList<Friend>();
        fdList.add(new Friend("Raymond", "Sak", "1234 5678", "", R.drawable.damian));
        fdList.add(new Friend("Megan", "Gibbs", "9422 7284", "", R.drawable.user_blue));
        fdList.add(new Friend("Bryant", "Ryan", "6990 4671", "", R.drawable.user_blue));
        fdList.add(new Friend("Drew", "Jennings", "9078 4420", "", R.drawable.user_blue));
        fdList.add(new Friend("Roman", "Sherman", "7559 0909", "", R.drawable.user_blue));

        final ImageButton backButton = (ImageButton) findViewById(R.id.btn_back);
        backButton.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  //backButton.setImageResource(R.drawable.back_green);
                  finish();
              }
            }
        );

        final ImageButton frontButton = (ImageButton) findViewById(R.id.btn_next);
        frontButton.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  //frontButton.setImageResource(R.drawable.forward_green);
                  Intent intent = new Intent(context, ConfirmationActivity.class);
                  Bundle bundle = new Bundle();
                  setFriendSplitAmount();
                  bundle.putSerializable("selectedFriends", selectedFriends);
                  bundle.putFloat("each", getSplitAmount());
                  bundle.putFloat("total", splitSummary.getTransactionAmount());
                  intent.putExtras(bundle);
                  startActivity(intent);
                  //frontButton.setImageResource(R.drawable.forward);

              }
          }
        );

        //this.setListAdapter(new ArrayAdapter<String>(
        //        this, R.layout.mylist,
        //        R.id.Itemname,itemname));
        //FriendListAdapter fdAdapter = new FriendListAdapter(this, R.layout.mylist, fdList);
        this.setListAdapter(new FriendListAdapter(this, R.layout.mylist, fdList));

    }

    protected void updateAmounts(ListView l){
        String newSplitAmount = "HKD"+ String.format("%.2f",getSplitAmount());
        TextView selectCount = (TextView)this.findViewById(R.id.text_selected_friends_no);
        selectCount.setText(String.valueOf(selectedFriends.size()));
        TextView each = (TextView)this.findViewById(R.id.text_each_price);
        each.setText(newSplitAmount);

        for (int pos=0; pos < l.getCount(); pos++){
            Friend f = (Friend)l.getItemAtPosition(pos);
            if (selectedFriends.contains(f)){
                View v= l.getChildAt(pos);
                TextView tv = (TextView) v.findViewById(R.id.splitAmount);
                tv.setText(newSplitAmount);
            }
        }
    }

    protected float getSplitAmount(){
        return splitSummary.getTransactionAmount() / (selectedFriends.size()+1);
    }

    protected void setFriendSplitAmount(){
        for(int i = 0; i < selectedFriends.size(); i++){
            Friend fdWithAmount = selectedFriends.get(i);
            fdWithAmount.setAmountToPay(getSplitAmount());
            selectedFriends.set(i,fdWithAmount);
        }
    }


}