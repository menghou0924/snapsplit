package com.rnd.snapsplit.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.res.Resources;
import android.app.Activity;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.rnd.snapsplit.Friend;
import com.rnd.snapsplit.FriendListAdapter;
import com.rnd.snapsplit.R;
import com.rnd.snapsplit.Transaction;
import com.rnd.snapsplit.StorageManager;
import com.rnd.snapsplit.Summary;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Damian on 28/5/2017.
 */

public class FriendsSelectionFragment extends ListFragment {

    private static final String TAG = "FriendSelectionFragment";

    ArrayList<Friend> selectedFriends = new ArrayList<Friend>();
    Summary splitTransaction;
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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.view_friend_selection, container, false);
        final Activity activity = getActivity();
        final Context context = getContext();

        ((Toolbar) activity.findViewById(R.id.tool_bar_hamburger)).setVisibility(View.INVISIBLE);
        ((Toolbar) view.findViewById(R.id.tool_bar_process)).setVisibility(View.VISIBLE);
        ((TextView) view.findViewById(R.id.text_title)).setText(R.string.friends_selection);

        storageManager.clearFile("FRIENDS_LIST");
        if (storageManager.isFileEmpty("FRIENDS_LIST")) {
            Friend fd = new Friend("Damian", "Dutkiewicz", "5937 2478", "", R.drawable.damian);
            Friend fd1 = new Friend("Raymond", "Sak", "3423 5435", "", R.drawable.raymond);
            Friend fd2 = new Friend("Megan", "Gibbs", "9053 24438", "", 0);
            Friend fd3 = new Friend("Bryant", "Ryan", "5587 2988", "", 0);
            Friend fd4 = new Friend("Drew", "Jennings", "3557 7837", "", 0);
            fd.saveSelfToFile(context);
            fd1.saveSelfToFile(context);
            fd2.saveSelfToFile(context);
            fd3.saveSelfToFile(context);
            fd4.saveSelfToFile(context);
//            Log.d(TAG, storageManager.getFile("FRIENDS_LIST"));
        }

        Bundle bundle = this.getArguments();
        splitTransaction = (Summary) bundle.getSerializable("splitTransaction");

        TextView description = (TextView) view.findViewById(R.id.text_summary_shop);
        description.setText(splitTransaction.getTransactionName());

        TextView amount = (TextView) view.findViewById(R.id.text_summary_amount_value);
        TextView myShare = (TextView) view.findViewById(R.id.myShare_value);
        String s = Float.toString(splitTransaction.getTransactionAmount());
        amount.setText(s);
        myShare.setText("HKD"+s);

        ArrayList<Friend> friendsList = Friend.getFriendsListFromFile(context);

        final ImageButton backButton = (ImageButton) view.findViewById(R.id.btn_back);
        backButton.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                              ((Toolbar) activity.findViewById(R.id.tool_bar_hamburger)).setVisibility(View.VISIBLE);
                                              ((Toolbar) getActivity().findViewById(R.id.tool_bar_hamburger)).setBackgroundColor(getResources().getColor(android.R.color.transparent));
                                              ((Toolbar) getActivity().findViewById(R.id.tool_bar_hamburger)).setTitle("");
                                              getFragmentManager().popBackStack();
                                          }
                                      }
        );

        final ImageButton forwardButton = (ImageButton) view.findViewById(R.id.btn_next);
        forwardButton.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View v) {
                                                 Bundle bundle = new Bundle();
                                                 setFriendSplitAmount();
                                                 bundle.putSerializable("selectedFriends", selectedFriends);
                                                 bundle.putFloat("each", getSplitAmount());
                                                 bundle.putFloat("total", splitTransaction.getTransactionAmount());
                                                 bundle.putString("description", splitTransaction.getTransactionName());

                                                 ConfirmationFragment fragment = new ConfirmationFragment();
                                                 fragment.setArguments(bundle);

                                                 ((Toolbar) activity.findViewById(R.id.tool_bar_hamburger)).setVisibility(View.INVISIBLE);
                                                 getActivity().getSupportFragmentManager()
                                                         .beginTransaction()
                                                         .add(R.id.fragment_holder, fragment, "ConfirmationFragment")
                                                         .addToBackStack(null)
                                                         .commit();
                                             }
                                         }
        );
        this.setListAdapter(new FriendListAdapter(context, R.layout.list_friend, friendsList, TAG));

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

        TextView myShare = (TextView) getView().findViewById(R.id.myShare_value);
        myShare.setText(newSplitAmount);

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
