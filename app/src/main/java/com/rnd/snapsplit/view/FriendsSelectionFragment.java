package com.rnd.snapsplit.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.res.Resources;
import android.app.Activity;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.rnd.snapsplit.Friend;
import com.rnd.snapsplit.FriendListAdapter;
import com.rnd.snapsplit.R;
import com.rnd.snapsplit.StorageManager;
import com.rnd.snapsplit.Transaction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Damian on 28/5/2017.
 */

public class FriendsSelectionFragment extends ListFragment {

    private static final String TAG = "FriendsSelectionFragment";

    ArrayList<Friend> selectedFriends = new ArrayList<Friend>();
    Transaction splitTransaction;
    StorageManager storageManager;

    public static FriendsSelectionFragment newInstance() {
        return new FriendsSelectionFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        final Resources resources = context.getResources();
        storageManager = new StorageManager(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.friend_selection_activity, container, false);
        final Activity activity = getActivity();
        final Context context = getContext();

        Bundle bundle = this.getArguments();
        splitTransaction = (Transaction) bundle.getSerializable("splitTransaction");
//        splitTransaction = new Transaction("test", 123.5f);

        TextView description = (TextView) view.findViewById(R.id.text_summary_shop);
        description.setText(splitTransaction.getTransactionName());

        TextView amount = (TextView) view.findViewById(R.id.text_summary_amount_value);
        String s = Float.toString(splitTransaction.getTransactionAmount());
        amount.setText(s);

        ArrayList<Friend> friendsList = new ArrayList<Friend>();

        try {
            JSONObject friendsObj = new JSONObject(storageManager.getFile("FRIENDS_LIST"));
            JSONArray friendsArray = friendsObj.getJSONArray("friends");
            for (int i = 0; i < friendsArray.length(); i++) {
                JSONObject temp = friendsArray.getJSONObject(i);
                friendsList.add(new Friend(temp.optString("firstName")
                        , temp.optString("lastName")
                        , temp.optString("phoneNumber")
                        , temp.optString("accountNumber")
                        , temp.optInt("displayPic")));
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        final ImageButton backButton = (ImageButton) view.findViewById(R.id.btn_back);
        backButton.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                              getFragmentManager().popBackStack();
                                          }
                                      }
        );

        // TODO: Move it to Main activity
        final ImageButton frontButton = (ImageButton) view.findViewById(R.id.btn_next);
        frontButton.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {
                                               Bundle bundle = new Bundle();
                                               setFriendSplitAmount();
                                               bundle.putSerializable("selectedFriends", selectedFriends);
                                               bundle.putFloat("each", getSplitAmount());
                                               bundle.putFloat("total", splitTransaction.getTransactionAmount());

                                               ConfirmationFragment fragment = new ConfirmationFragment();
                                               fragment.setArguments(bundle);

                                               getFragmentManager()
                                                       .beginTransaction()
                                                       .add(R.id.fragment_holder, fragment, "ConfirmationFragment")
                                                       .addToBackStack(null)
                                                       .commit();
                                           }
                                       }
        );
        this.setListAdapter(new FriendListAdapter(context, R.layout.friend_list, friendsList));

        return view;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // Log.d("myTag", splitTransaction.getTransactionAmount() +"  " + splitTransaction.getTransactionName());
        // Toast.makeText(this, "Item #" + position + " clicked", Toast.LENGTH_SHORT).show();
        Friend f = (Friend)l.getItemAtPosition(position);
        if (selectedFriends.contains(f)) {
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

    protected void updateAmounts(ListView l) {
        String newSplitAmount = String.format("HKD%.2f",getSplitAmount());
        TextView selectCount = (TextView)getView().findViewById(R.id.text_selected_friends_no);
        selectCount.setText(String.valueOf(selectedFriends.size()));
        TextView each = (TextView)getView().findViewById(R.id.text_each_price);
        each.setText(newSplitAmount);

        for (int pos=0; pos < l.getCount(); pos++) {
            Friend f = (Friend)l.getItemAtPosition(pos);
            if (selectedFriends.contains(f)) {
                View v= l.getChildAt(pos);
                TextView tv = (TextView) v.findViewById(R.id.splitAmount);
                tv.setText(newSplitAmount);
            }
        }
    }

    protected float getSplitAmount() {
        return splitTransaction.getTransactionAmount() / (selectedFriends.size()+1);
    }

    protected void setFriendSplitAmount() {
        for(int i = 0; i < selectedFriends.size(); i++) {
            Friend fdWithAmount = selectedFriends.get(i);
            fdWithAmount.setAmountToPay(getSplitAmount());
            selectedFriends.set(i,fdWithAmount);
        }
    }
}
