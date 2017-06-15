package com.rnd.snapsplit;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Damian on 29/5/2017.
 */

public class Friend implements Serializable {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String accountNumber;
    private float amountToPay = 0f;
    private int displayPic;
    private StorageManager storageManager;
    private Context context;
    private final static String STORAGE_FILE_NAME = "FRIENDS_LIST";

    @Override
    public boolean equals(Object obj) {
        Friend other = (Friend) obj;
        return other.getPhoneNumber().equals(phoneNumber);
    }

    public Friend(Context ctx) {
        context = ctx;
        storageManager = new StorageManager(ctx);
    }

    public Friend(String firstName, String lastName, String phoneNumber, String accountNumber, int displayPic) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.accountNumber = accountNumber;
        this.displayPic = displayPic;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAccountNo() {
        return accountNumber;
    }

    public int getDisplayPic() { return displayPic; }

    public float getAmountToPay() { return amountToPay; }

    public String getValueWithKey(String key) {
        String result = "";
        switch (key) {
            case "firstName":
                result = this.getFirstName();
                break;
            case "lastName":
                result = this.getLastName();
                break;
            case "name":
                result = this.getFirstName() + " " + this.getLastName();
                break;
            case "phoneNumber":
                result = this.getPhoneNumber();
                break;
            case "accountNo":
                result = this.getAccountNo();
                break;
            default:
                result = this.getFirstName() + " " + this.getLastName();
        }
        return result;
    }

    public void setAmountToPay(float amt) { this.amountToPay = amt; }

    public void saveFriend() {
        JSONObject friend = new JSONObject();
        JSONArray array = new JSONArray();
        JSONObject friendsList = new JSONObject();
        try {
            friend.put("firstName", firstName);
            friend.put("lastName", lastName);
            friend.put("phoneNumber", phoneNumber);
            friend.put("accountNumber", accountNumber);
            friend.put("displayPic", displayPic);
            array.put(friend);
            friendsList.put("friends", friend);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        storageManager.appendFile(STORAGE_FILE_NAME, friendsList.toString());
    }

    public ArrayList<Friend> getFriendsList() {
        ArrayList<Friend> resultList = new ArrayList<Friend>();
        JSONObject friendObj = new JSONObject();
        try {
            friendObj = new JSONObject(storageManager.getFile(STORAGE_FILE_NAME));
            JSONArray friendsArray = friendObj.getJSONArray("friends");
            for (int i = 0; i < friendsArray.length(); i++) {
                JSONObject temp = friendsArray.getJSONObject(i);
                resultList.add(new Friend(
                        temp.optString("firstName")
                        , temp.optString("lastName")
                        , temp.optString("phoneNumber")
                        , temp.optString("accountNumber")
                        , temp.optInt("displayPic")
                ));
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return resultList;
    }
}
