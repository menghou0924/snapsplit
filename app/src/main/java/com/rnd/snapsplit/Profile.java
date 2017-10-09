package com.rnd.snapsplit;

import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Random;

/**
 * Created by menghou0924 on 6/19/17.
 */

public class Profile {
    private Context context;
    private String id = "";
    private String name = "";
    private String phoneNumber = "";
    private String accountNumber = "";
    private String displayAccountNumber = "";
    private int displayPic = 0;
    private int displayColor = 0;
    private String accountBalance = "";
    private final static String STORAGE_FILE_NAME = "PERSONAL_PROFILE";
    private CitiAPIManager citiAPIManager;
    private CitiAPIBase citiAPIBase;
    private StorageManager storageManager;
    private DatabaseReference databaseReference;
    private final static Random RANDOM = new Random();

    public Profile(Context ctx) {
        context = ctx;
        citiAPIManager = new CitiAPIManager(ctx);
        citiAPIBase = new CitiAPIBase(ctx);
        storageManager = new StorageManager(ctx);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        citiAPIBase.API_Accounts_RetrieveAccountsSummary();
        citiAPIBase.API_Customers_RetrieveCustomerBasicName();
//        citiAPIBase.API_MoneyMovement_RetrieveDestSrcAcct("ALL", "");
//        citiAPIBase.API_MoneyMovement_RetrievePayeeList("ALL", "");
//        citiAPIBase.API_MoneyMovement_RetrieveDestSrcAcctPersonal();
//        citiAPIBase.API_MoneyMovement_RetrieveDestSrcAcctInternal("");
//        citiAPIBase.API_MoneyMovement_RetrieveDestSrcAcctExternal("");
//        citiAPIBase.API_MoneyMovement_RetrieveDestSrcAcctBillPayment("");

        if (!storageManager.getFile(STORAGE_FILE_NAME).isEmpty()) {
            JSONObject person = storageManager.getJSONObjectFromFile(STORAGE_FILE_NAME);
            id = person.optString("profile_id");
            name = person.optString("profile_name");
            phoneNumber = person.optString("profile_phoneNumber");
            accountNumber = person.optString("profile_accountNumber");
            displayAccountNumber = person.optString("profile_displayAccountNumber");
            displayPic = person.optInt("profile_displayPic");
            displayColor = person.optInt("profile_displayColor");
            if (!accountNumber.isEmpty()) {
                citiAPIBase.API_Accounts_RetrieveAccountDetails(accountNumber);
            }
            FirebaseDatabase.getInstance().getReference().child(phoneNumber).addListenerForSingleValueEvent
                    (new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if (snapshot.getValue() != null) {
                                String updatedRequestorBalance = (String) ((HashMap<String, String>) snapshot.getValue()).get("accountBalance");
                                JSONObject jsonObject = storageManager.getJSONObjectFromFile(STORAGE_FILE_NAME);
                                try {
                                    jsonObject.put("profile_accountBalance", updatedRequestorBalance);
                                    storageManager.saveFile(STORAGE_FILE_NAME, jsonObject.toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
            accountBalance = person.optString("profile_accountBalance");
            if (Friend.getFriendCount(context) == 1) {
                Friend.resetFriends(context);
            }
        }
        if (storageManager.getFile(STORAGE_FILE_NAME).isEmpty() || citiAPIManager.getEnglishName().isEmpty() || name.equals("Warren Buffett")) {

            id = Integer.toString(RANDOM.nextInt(99999999) + 10000000);
            name = citiAPIManager.getEnglishName();

            if (name.equals("AXDXEX JXNX AXNXT")) { // Sandboxuser1
                name = "Raymond Sak"; // transfer from
                displayPic = R.drawable.raymond;
                phoneNumber = "5139 6515";
                accountNumber = "355a515030616a53576b6a65797359506a634175764a734a3238314e4668627349486a676f7449463949453d";
                Friend.resetFriends(context);
            }
            else if (name.equals("CARSTEN K ANDREAS")) { // Sandboxuser2
                name = "Damian Dutkiewicz"; // transfer to
                displayPic = R.drawable.damian;
                phoneNumber = "5660 0981";
                accountNumber = "7977557255484c7345546c4e53424766634b6c53756841672b556857626e395253334b70416449676b42673d";
                Friend.resetFriends(context);
            }
            else {
                name = "Warren Buffett";
                displayColor = context.getResources().getIntArray(R.array.colors_icon)[RANDOM.nextInt(10)];
                accountNumber = "7977557255484c7345546c4e53424766634b6c53756841672b556857626e395253334b70416449676b42673d";
                phoneNumber = Integer.toString(RANDOM.nextInt(99999999) + 10000000);
                phoneNumber = phoneNumber.substring(0, 4) + " " + phoneNumber.substring(4);// transfer to
            }
            FirebaseDatabase.getInstance().getReference().child(phoneNumber).addListenerForSingleValueEvent
                    (new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if (snapshot.getValue() != null) {
                                String updatedRequestorBalance = (String) ((HashMap<String, String>) snapshot.getValue()).get("accountBalance");
                                JSONObject jsonObject = storageManager.getJSONObjectFromFile(STORAGE_FILE_NAME);
                                try {
                                    jsonObject.put("profile_accountBalance", updatedRequestorBalance);
                                    storageManager.saveFile(STORAGE_FILE_NAME, jsonObject.toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });

//            FirebaseDatabase.getInstance().getReference().child(phoneNumber).child("accountBalance").setValue(accountBalance);
            if (!accountNumber.isEmpty()) {
                citiAPIBase.API_Accounts_RetrieveAccountDetails(accountNumber);
            }

            displayAccountNumber = citiAPIManager.getDisplayAccountNumber(accountNumber);

            JSONObject profile = new JSONObject();
            try {
                profile.put("profile_id", id);
                profile.put("profile_name", name);
                profile.put("profile_phoneNumber", phoneNumber);
                profile.put("profile_accountNumber", accountNumber);
                profile.put("profile_displayAccountNumber", displayAccountNumber);
                profile.put("profile_displayPic", displayPic);
                profile.put("profile_displayColor", displayColor);
                profile.put("profile_accountBalance", accountBalance);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            storageManager.saveFile(STORAGE_FILE_NAME, profile.toString());
        }
        else {

        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getDisplayAccountNumber() {
        return displayAccountNumber;
    }

    public int getDisplayPic() {
        return displayPic;
    }

    public int getDisplayColor() {
        return displayColor;
    }

    public String getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(String newBalance) {
        accountBalance = newBalance;
    }

    public JSONObject getSelfAsJSONObject() {
        JSONObject self = new JSONObject();
        try {
            self.put("profile_id", id);
            self.put("profile_name", name);
            self.put("profile_phoneNumber", phoneNumber);
            self.put("profile_accountNumber", accountNumber);
            self.put("profile_displayAccountNumber", displayAccountNumber);
            self.put("profile_displayPic", displayPic);
            self.put("profile_displayColor", displayColor);
            self.put("profile_accountBalance", accountBalance);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return self;
    }
}
