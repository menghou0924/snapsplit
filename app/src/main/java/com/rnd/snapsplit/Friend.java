package com.rnd.snapsplit;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Damian on 29/5/2017.
 */

public class Friend implements Serializable {
    private String id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String accountNumber;
    private float amountToPay = 0f;
    public HashMap<String, Object> timestampNow = new HashMap<>();
    private int displayPic;
    private final static String STORAGE_FILE_NAME = "FRIENDS_LIST";

    @Override
    public boolean equals(Object obj) {
        Friend other = (Friend) obj;
        return other.getPhoneNumber().equals(phoneNumber);
    }

    public Friend() {
    }

    public Friend(String firstName, String lastName, String phoneNumber, String accountNumber, int displayPic) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.accountNumber = accountNumber;
        this.displayPic = displayPic;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Object getValueWithKey(String key) {
        Object result = "";
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
            case "displayPic":
                result = this.getDisplayPic();
                break;
            default:
                result = this.getFirstName() + " " + this.getLastName();
        }
        return result;
    }

    public void setAmountToPay(float amt) { this.amountToPay = amt; }

    // access from files

    public void saveSelfToFile(Context ctx) {
        JSONObject friendListObj = new JSONObject();
        JSONArray friendArray = new JSONArray();
        JSONObject newFriendObj = new JSONObject();

        try {
            if ((new StorageManager(ctx)).getFile(STORAGE_FILE_NAME).isEmpty() == false) {
                friendListObj = new JSONObject((new StorageManager(ctx)).getFile(STORAGE_FILE_NAME));
                friendArray = friendListObj.getJSONArray("friends");
            }
            newFriendObj.put("firstName", firstName);
            newFriendObj.put("lastName", lastName);
            newFriendObj.put("phoneNumber", phoneNumber);
            newFriendObj.put("accountNumber", accountNumber);
            newFriendObj.put("displayPic", displayPic);
            friendArray.put(newFriendObj);

            friendListObj.put("friends", friendArray);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        (new StorageManager(ctx)).saveFile(STORAGE_FILE_NAME, friendListObj.toString());
    }

    public static ArrayList<Friend> getFriendsListFromFile(Context ctx) {
        ArrayList<Friend> resultList = new ArrayList<Friend>();
        JSONObject friendObj = new JSONObject();
        try {
            friendObj = new JSONObject((new StorageManager(ctx)).getFile(STORAGE_FILE_NAME));
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

    public static Friend getFriendByPhoneNumber(Context ctx, String phoneNumber) {
        Friend result = new Friend();
        JSONObject friendObj = new JSONObject();
        try {
            friendObj = new JSONObject((new StorageManager(ctx)).getFile(STORAGE_FILE_NAME));
            JSONArray friendsArray = friendObj.getJSONArray("friends");
            for (int i = 0; i < friendsArray.length(); i++) {
                JSONObject temp = friendsArray.getJSONObject(i);
                if (temp.optString("phoneNumber").equals(phoneNumber)) {
                    result = new Friend(
                            temp.optString("firstName")
                            , temp.optString("lastName")
                            , temp.optString("phoneNumber")
                            , temp.optString("accountNumber")
                            , temp.optInt("displayPic")
                    );
                }
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
}