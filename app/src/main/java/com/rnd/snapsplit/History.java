package com.rnd.snapsplit;

import android.content.Context;

import com.rnd.snapsplit.Friend;
import com.rnd.snapsplit.Profile;
import com.rnd.snapsplit.Summary;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by menghou0924 on 6/19/17.
 */

public class History {
    private Context context;
    private Profile self;
    private StorageManager storageManager;
    private final static String STORAGE_FILENAME = "History";

    public History(Context ctx) {
        context = ctx;
        self = new Profile(context);
        storageManager = new StorageManager(context);
    }

    public void setRequestHistory(ArrayList<Friend> sendRequestToFriendsList, PaymentRequest paymentRequest) {
        JSONObject history = new JSONObject();
        JSONArray friendArray = new JSONArray();
        try {
            history.put("type", "history_request_made");
            // this puts personal profile information, id, name, phoneNumber, accountNumber, displayAccountNumber, displayPic
            JSONObject selfObject = self.getSelfAsJSONObject(); // self is the one who made the request
            Iterator<?> keys = selfObject.keys();

            while( keys.hasNext() ) {
                String key = (String)keys.next();
                history.put(key, selfObject.get(key));
            }

            // this puts paymentrequest id, requestorName, description, requestorPhoneNumber, requestEpochDate, receiptPicture, receipientPhoneNo, totalAmount
            JSONObject paymentRequestObject = paymentRequest.getSelfWithTotalAsJsonObject(context);
            keys = paymentRequestObject.keys();

            while( keys.hasNext() ) {
                String key = (String)keys.next();
                history.put(key, paymentRequestObject.get(key));
            }

            // this puts friends array with their details: id, firstName, lastName, phoneNumber, accountNumber, displayPic,
            for (Friend f : sendRequestToFriendsList) {
                friendArray.put(f.getSelfAsJSONObject(context));
            }
            history.put("friends", friendArray);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        storageManager.appendFile(STORAGE_FILENAME, "history", history);
    }

    public void setPaymentHistory(PaymentRequest paymentRequest) {
        JSONObject history = new JSONObject();
        try {
            history.put("type", "history_payment_made");
            // this puts personal profile information, id, name, phoneNumber, accountNumber, displayAccountNumber, displayPic
            JSONObject selfObject = self.getSelfAsJSONObject(); // self is the one who made the payment
            Iterator<?> keys = selfObject.keys();

            while( keys.hasNext() ) {
                String key = (String)keys.next();
                history.put(key, selfObject.get(key));
            }

            // this puts paymentrequest id, requestorName, description, requestorPhoneNumber, requestEpochDate, receiptPicture, receipientPhoneNo, totalAmount
            JSONObject paymentRequestObject = paymentRequest.getSelfWithTotalAsJsonObject(context);
            keys = paymentRequestObject.keys();

            while( keys.hasNext() ) {
                String key = (String)keys.next();
                history.put(key, paymentRequestObject.get(key));
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        storageManager.appendFile(STORAGE_FILENAME, "history", history);
    }

    public void setReceivePaymentHistory(Friend friend, PaymentRequest paymentRequest) {
        JSONObject history = new JSONObject();
        try {
            history.put("type", "history_payment_received");
            // this puts personal profile information, id, name, phoneNumber, accountNumber, displayAccountNumber, displayPic
            JSONArray friendArray = new JSONArray(); // friend is the one who received the payment
            friendArray.put(friend.getSelfAsJSONObject(context));
            history.put("friends", friendArray);

            // this puts paymentrequest id, requestorName, description, requestorPhoneNumber, requestEpochDate, receiptPicture, receipientPhoneNo, totalAmount
            JSONObject paymentRequestObject = paymentRequest.getSelfWithTotalAsJsonObject(context);
            Iterator<?> keys = paymentRequestObject.keys();

            while( keys.hasNext() ) {
                String key = (String)keys.next();
                history.put(key, paymentRequestObject.get(key));
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        storageManager.appendFile(STORAGE_FILENAME, "history", history);
    }

//    public String getRelevantHistory() {
//        JSONObject historyObject =
//    }
}
