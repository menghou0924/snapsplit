package com.rnd.snapsplit;

import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rnd.snapsplit.Friend;
import com.rnd.snapsplit.Profile;
import com.rnd.snapsplit.Summary;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by menghou0924 on 6/19/17.
 */

public class History implements Serializable {
    private Context context;
    private String id;
    private String type;
    private String transactionDate;
    private String requestorName = "";
    private String description = "";
    private String requestorPhoneNumber = "";
    private String requestEpochDate = "";
    private String receipientPhoneNo = "";
    private String receipientName = "";
    private String strReceiptPic="";
    private float shareAmount = 0f;
    private float totalAmount = 0f;
    private StorageManager storageManager;
    private final static String STORAGE_FILENAME = "History";
    public final static String HISTORY_TYPE_PAYMENT_MADE = "history_payment_made";
    public final static String HISTORY_TYPE_PAYMENT_RECEIVED = "history_payment_received";

    public History() {

    }

    public History(Context ctx) {
        context = ctx;
        storageManager = new StorageManager(context);
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public String getDescription() {
        return description;
    }

    public String getRequestorName() {
        return requestorName;
    }

    public String getReceipientName() {
        return receipientName;
    }

    public String getRequestorPhoneNumber() {
        return requestorPhoneNumber;
    }

    public String getRequestEpochDate() {
        return requestEpochDate;
    }

    public String getReceipientPhoneNo() {
        return receipientPhoneNo;
    }

    public float getShareAmount() {
        return shareAmount;
    }

    public float getTotalAmount() {
        return totalAmount;
    }

    public String getStrReceiptPic() {
        return strReceiptPic;
    }

    public void setDesription(String description) {
        this.description = description;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public void setRequestorName(String requestorName) {
        this.requestorName = requestorName;
    }

    public void setReceipientName(String receipientName) {
        this.receipientName = receipientName;
    }

    public void setRequestorPhoneNumber(String requestorPhoneNumber) {
        this.requestorPhoneNumber = requestorPhoneNumber;
    }

    public void setRequestEpochDate(String requestEpochDate) {
        this.requestEpochDate = requestEpochDate;
    }

    public void setReceipientPhoneNo(String receipientPhoneNo) {
        this.receipientPhoneNo = receipientPhoneNo;
    }

    public void setShareAmount(float shareAmount) {
        this.shareAmount = shareAmount;
    }

    public void setTotalAmount(float totalAmount) {
        this.totalAmount = totalAmount;
    }

//    public JSONObject getOwnHistory() {
//
//    }

//    public void setRequestHistory(ArrayList<Friend> sendRequestToFriendsList, PaymentRequest paymentRequest) {
//        JSONObject history = new JSONObject();
//        JSONArray friendArray = new JSONArray();
//        try {
//            history.put("type", "history_request_made");
//            // this puts personal profile information, id, name, phoneNumber, accountNumber, displayAccountNumber, displayPic
//            JSONObject selfObject = self.getSelfAsJSONObject(); // self is the one who made the request
//            Iterator<?> keys = selfObject.keys();
//
//            while( keys.hasNext() ) {
//                String key = (String)keys.next();
//                history.put(key, selfObject.get(key));
//            }
//
//            // this puts paymentrequest id, requestorName, description, requestorPhoneNumber, requestEpochDate, receiptPicture, receipientPhoneNo, totalAmount
//            JSONObject paymentRequestObject = paymentRequest.getSelfWithTotalAsJsonObject(context);
//            keys = paymentRequestObject.keys();
//
//            while( keys.hasNext() ) {
//                String key = (String)keys.next();
//                history.put(key, paymentRequestObject.get(key));
//            }
//
//            // this puts friends array with their details: id, firstName, lastName, phoneNumber, accountNumber, displayPic,
//            for (Friend f : sendRequestToFriendsList) {
//                friendArray.put(f.getSelfAsJSONObject(context));
//            }
//            history.put("friends", friendArray);
//        }
//        catch (JSONException e) {
//            e.printStackTrace();
//        }
//        storageManager.appendFile(STORAGE_FILENAME, "history", history);
//    }

    public void setPaymentHistory(PaymentRequest paymentRequest) {
        // I paid someone else
        this.type = HISTORY_TYPE_PAYMENT_MADE;
        this.transactionDate = String.valueOf(System.currentTimeMillis()*(-1));
        this.requestorName = paymentRequest.getRequestorName();
        this.description = paymentRequest.getDescription();
        this.requestorPhoneNumber = paymentRequest.getRequestorPhoneNumber();
        this.requestEpochDate = paymentRequest.getRequestEpochDate();
        this.receipientPhoneNo = paymentRequest.getReceipientPhoneNo();
        this.receipientName = (Friend.getFriendByPhoneNumber(context, paymentRequest.getReceipientPhoneNo())).getName();
        this.strReceiptPic= paymentRequest.getStrReceiptPic();
        this.shareAmount = paymentRequest.getShareAmount();
        this.totalAmount = paymentRequest.getTotalAmount();

        FirebaseDatabase.getInstance().getReference().child(paymentRequest.getReceipientPhoneNo()).child("history").push().setValue(this);
    }

    public void setFriendReceivePaymentHistory(PaymentRequest paymentRequest) {
        // someone received my payment
        this.type = HISTORY_TYPE_PAYMENT_RECEIVED;
        this.transactionDate = String.valueOf(System.currentTimeMillis()*(-1));
        this.requestorName = paymentRequest.getRequestorName();
        this.description = paymentRequest.getDescription();
        this.requestorPhoneNumber = paymentRequest.getRequestorPhoneNumber();
        this.requestEpochDate = paymentRequest.getRequestEpochDate();
        this.receipientPhoneNo = paymentRequest.getReceipientPhoneNo();
        this.receipientName = (Friend.getFriendByPhoneNumber(context, paymentRequest.getReceipientPhoneNo())).getName();
        this.strReceiptPic= paymentRequest.getStrReceiptPic();
        this.shareAmount = paymentRequest.getShareAmount();
        this.totalAmount = paymentRequest.getTotalAmount();

        FirebaseDatabase.getInstance().getReference().child(paymentRequest.getRequestorPhoneNumber()).child("history").push().setValue(this);
    }

//    public String getRelevantHistory() {
//        JSONObject historyObject =
//    }
}
