package com.rnd.snapsplit.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.res.Resources;
import android.app.Activity;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.rnd.snapsplit.Friend;
import com.rnd.snapsplit.FriendListAdapter;
import com.rnd.snapsplit.R;
import com.rnd.snapsplit.StorageManager;
import com.rnd.snapsplit.Summary;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Damian on 28/5/2017.
 */

public class FriendsSelectionFragment extends ListFragment {

    private static final String TAG = "FriendSelectionFragment";

    ArrayList<Friend> selectedFriendsArray = new ArrayList<Friend>();
    Map<Friend, Integer> selectedFriendsMap = new HashMap<>();
    Summary splitTransaction;
    StorageManager storageManager;
    boolean isEqualSplit = true;
    boolean isPercentageSplit = false;

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

        if (Friend.isFriendListEmpty(context)) {
            Friend.resetFriends(context);
        }

        ((Toolbar) activity.findViewById(R.id.tool_bar_hamburger)).setVisibility(View.INVISIBLE);
        ((Toolbar) view.findViewById(R.id.tool_bar_process)).setVisibility(View.VISIBLE);
        ((TextView) view.findViewById(R.id.text_title)).setText(R.string.split_with);

        Bundle bundle = this.getArguments();
        splitTransaction = (Summary) bundle.getSerializable("splitTransaction");
        final String picPath = bundle.getString("receiptPicture");
        final Bitmap bm = BitmapFactory.decodeFile(picPath);

        CircleImageView receipt = (CircleImageView) view.findViewById(R.id.receipt_image);
        receipt.setImageBitmap(bm);

        TextView description = (TextView) view.findViewById(R.id.text_summary_shop);
        description.setText(splitTransaction.getTransactionName());

        TextView amount = (TextView) view.findViewById(R.id.text_summary_amount_value);
        TextView myShare = (TextView) view.findViewById(R.id.text_myShare_value);
        String s = Float.toString(splitTransaction.getTransactionAmount());
        amount.setText(s);
        myShare.setText(s);

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


        final Switch eqSplitSwitch = (Switch) view.findViewById(R.id.eqSplitSwitch);
        eqSplitSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                TextView eqSp = (TextView) view.findViewById(R.id.eqSplit);
                ListView list = (ListView) view.findViewById(android.R.id.list);
                EditText amount = (EditText) list.findViewById(R.id.splitAmount);
                TextView myShare = (TextView) view.findViewById(R.id.text_myShare_value);
                TextView currencyPercentage = (TextView) view.findViewById(R.id.text_myShare_currencyPercetage);
                if (!isChecked){
                    isEqualSplit = false;
                    eqSp.setText("Manual Split");
                    setAmountEditability(list, true);
                    updateAmounts(list,"0.00");
                    myShare.setText("N/A");
                    currencyPercentage.setText("");

                }
                else{
                    isEqualSplit = true;
                    eqSp.setText("Equal Split");
                    setAmountEditability(list, false);
                    if(isPercentageSplit){
                        updateAmounts(list, String.format("%.2f", getPercetageEqualSplit()));
                        currencyPercentage.setText("%");
                    }
                    else{
                        updateAmounts(list,"");
                        currencyPercentage.setText("HKD");
                    }

                }
                // do something, the isChecked will be
                // true if the switch is in the On position
            }
        });

        final Switch percentageSwitch = (Switch) view.findViewById(R.id.percentageSwitch);
        percentageSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    isPercentageSplit = false;
                    changeCurrencyOrPercentage(view,"HKD");
                    covnvertPercentageToAmount(view);
                }
                else{
                    isPercentageSplit = true;
                    changeCurrencyOrPercentage(view,"%");
                    covnvertAmountToPercentage(view);
                }

            }
        });

        final ImageButton forwardButton = (ImageButton) view.findViewById(R.id.btn_next);
        forwardButton.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View v) {
                                                 Bundle bundle = new Bundle();
                                                 setFriendOrder();
                                                 setFriendSplitAmount(view);
                                                 bundle.putSerializable("selectedFriends", selectedFriendsArray);
                                                 bundle.putString("receiptPicture",picPath);
                                                 if (isEqualSplit){
                                                     bundle.putString("myShare", String.format("%.2fHKD",getAmountEqualSplit()));
                                                 }
                                                 else{
                                                     bundle.putString("myShare", "N/A");
                                                 }
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
        EditText currencyOrPercentage = (EditText) v.findViewById(R.id.currencyOrPercent);
        TextView tv = (TextView) v.findViewById(R.id.splitAmount);

        Friend f = (Friend)l.getItemAtPosition(position);
        if (selectedFriendsMap.containsKey(f)) {
            selectedFriendsMap.remove(f);
            if (isEqualSplit){
                if (isPercentageSplit){
                    updateAmounts(l, String.format("%.2f", getPercetageEqualSplit()));
                }else{
                    updateAmounts(l,"");
                }
            }
            tv.setVisibility(View.INVISIBLE);
            currencyOrPercentage.setVisibility(View.INVISIBLE);
            ImageView iv = (ImageView) v.findViewById(R.id.fdTick);
            iv.setImageResource(R.drawable.tick_black);
        }
        else{
            selectedFriendsMap.put(f, position);
            if (isEqualSplit){
                if (isPercentageSplit){
                    updateAmounts(l, String.format("%.2f", getPercetageEqualSplit()));
                }else {
                    updateAmounts(l, "");
                }
            }
            tv.setVisibility(View.VISIBLE);
            currencyOrPercentage.setVisibility(View.VISIBLE);
            ImageView iv = (ImageView) v.findViewById(R.id.fdTick);
            iv.setImageResource(R.drawable.tick_green);
        }
    }

    protected void updateAmounts(ListView l, String newSplitAmount) {
        if (newSplitAmount.equals("")){
            newSplitAmount = String.format("%.2f", getAmountEqualSplit());
        }
        TextView selectCount = (TextView)getView().findViewById(R.id.text_selected_friends_no);
        selectCount.setText(String.valueOf(selectedFriendsMap.size()));

        TextView myShare = (TextView) getView().findViewById(R.id.text_myShare_value);
        myShare.setText(newSplitAmount);

        for (int pos=0; pos < l.getCount(); pos++) {
            Friend f = (Friend)l.getItemAtPosition(pos);
            //if (selectedFriends.contains(f)) {
                View v= l.getChildAt(pos);
                if (v != null) {
                    TextView tv = (TextView) v.findViewById(R.id.splitAmount);
                    tv.setText(newSplitAmount);
                }
            //}
        }
    }

    protected void setAmountEditability(ListView l, boolean makeEditable){
        for (int pos=0; pos < l.getCount(); pos++) {
            // Friend f = (Friend)l.getItemAtPosition(pos);
            View v = l.getChildAt(pos);
            if (v != null) {
                EditText amount = (EditText) v.findViewById(R.id.splitAmount);
                if (!makeEditable) {
                    amount.setFocusable(false);
                    amount.setClickable(false);
                    amount.setFocusableInTouchMode(false);
                    amount.setShowSoftInputOnFocus(false);
                    amount.setCursorVisible(false);
                } else {
                    amount.setFocusable(true);
                    amount.setClickable(true);
                    amount.setFocusableInTouchMode(true);
                    amount.setShowSoftInputOnFocus(true);
                    amount.setCursorVisible(true);
                }
            }
        }
    }

    protected float getAmountEqualSplit() {
        return splitTransaction.getTransactionAmount() / (selectedFriendsMap.size()+1);
    }

    protected float getPercetageEqualSplit() {
        return 100f / (float)(selectedFriendsMap.size()+1);
    }

    protected void setFriendSplitAmount(View v) {
        ListView l = (ListView) v.findViewById(android.R.id.list);
        for (int pos = 0; pos < l.getCount(); pos++) {
            Friend f = (Friend)l.getItemAtPosition(pos);
            if (selectedFriendsMap.containsKey(f)) {
                View updatedElement= l.getChildAt(pos - l.getFirstVisiblePosition());

                EditText updatedAmount = (EditText) updatedElement.findViewById(R.id.splitAmount);
                String updateAmountStr = updatedAmount.getText().toString();
                float updateAmountFloat = Float.valueOf(updateAmountStr);

                if(isPercentageSplit){
                    updateAmountFloat = (updateAmountFloat / 100) * splitTransaction.getTransactionAmount();
                }

                selectedFriendsMap.remove(f);
                f.setAmountToPay(updateAmountFloat);
                selectedFriendsMap.put(f, pos);
            }
//        for(int i = 0; i < selectedFriends.size(); i++) {
//            Friend fdWithAmount = selectedFriends.get(i);
//            fdWithAmount.setAmountToPay(getAmountEqualSplit());
//            selectedFriends.set(i,fdWithAmount);
        }
    }

    protected void changeCurrencyOrPercentage(View view, String changeToVal) {

        if (isEqualSplit) {
            TextView topBarValue = (TextView) view.findViewById(R.id.text_myShare_currencyPercetage);
            topBarValue.setText(changeToVal);
        }
        ListView l = (ListView) view.findViewById(android.R.id.list);
        for (int pos = 0; pos < l.getCount(); pos++) {
            // Friend f = (Friend)l.getItemAtPosition(pos);
            View v = l.getChildAt(pos);
            if (v != null) {
                EditText currencyOrPercent = (EditText) v.findViewById(R.id.currencyOrPercent);
                currencyOrPercent.setText(changeToVal);
            }
        }
    }

    protected void  covnvertAmountToPercentage(View view){
        ListView l = (ListView) view.findViewById(android.R.id.list);
        if (!isEqualSplit) {
            for (int pos = 0; pos < l.getCount(); pos++) {
                View v = l.getChildAt(pos);
                if (v != null) {
                    EditText currencyOrPercent = (EditText) v.findViewById(R.id.splitAmount);
                    String currentAmount = (String) currencyOrPercent.getText().toString();
                    float floatAmount = 0f;
                    try {
                        floatAmount = Float.parseFloat(currentAmount);
                    } catch (NumberFormatException nfe) {
                    }
                    float percentAmount = (floatAmount / splitTransaction.getTransactionAmount()) * 100;
                    currencyOrPercent.setText(String.format("%.2f", percentAmount));
                }
            }
        }
        else{
            float amount = (getAmountEqualSplit() / splitTransaction.getTransactionAmount()) *100;
            updateAmounts(l,String.format("%.2f",amount));
        }
    }

    protected void  covnvertPercentageToAmount(View view) {
        ListView l = (ListView) view.findViewById(android.R.id.list);
        if (!isEqualSplit) {
            for (int pos = 0; pos < l.getCount(); pos++) {
                View v = l.getChildAt(pos);
                if (v != null) {
                    EditText currencyOrPercent = (EditText) v.findViewById(R.id.splitAmount);
                    String currentPercentage = (String) currencyOrPercent.getText().toString();
                    float floatAmount = 0f;
                    try {
                        floatAmount = Float.parseFloat(currentPercentage);
                    } catch (NumberFormatException nfe) {
                    }
                    float amount = (floatAmount / 100) * splitTransaction.getTransactionAmount();
                    currencyOrPercent.setText(String.format("%.2f", amount));
                }
            }

            } else {
            updateAmounts(l, "");
        }
    }

    protected void setFriendOrder() {
        selectedFriendsArray = new ArrayList<Friend>(sortByValue(selectedFriendsMap).keySet());
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue( Map<K, V> map )
    {
        List<Map.Entry<K, V>> list =
                new LinkedList<>( map.entrySet() );
        Collections.sort( list, new Comparator<Map.Entry<K,V>>()
        {
            @Override
            public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 )
            {
                return ( o1.getValue() ).compareTo( o2.getValue() );
            }
        } );

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list)
        {
            result.put( entry.getKey(), entry.getValue() );
        }
        return result;
    }
}
