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
    private int displayColor;
    private final static String STORAGE_FILE_NAME = "FRIENDS_LIST";

    @Override
    public boolean equals(Object obj) {
        Friend other = (Friend) obj;
        return other.getPhoneNumber().equals(phoneNumber);
    }

    public Friend() {
    }

    public Friend(String firstName, String lastName, String phoneNumber, String accountNumber, int displayPic, int displayColor) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.accountNumber = accountNumber;
        this.displayPic = displayPic;
        this.displayColor = displayColor;
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

    public String getName() {
        if (firstName == null) {
            if (lastName == null) {
                return "";
            }
            else {
                return lastName;
            }
        }
        else {
            if (lastName == null) {
                return firstName;
            }
            else {
                return firstName + " " + lastName;
            }
        }
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAccountNo() {
        return accountNumber;
    }

    public int getDisplayPic() { return displayPic; }

    public int getDisplayColor() { return displayColor; }

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
        (new StorageManager(ctx)).appendFile(STORAGE_FILE_NAME, "friends", getSelfAsJSONObject(ctx));
    }

    public JSONObject getSelfAsJSONObject(Context ctx) {
        JSONObject friend = new JSONObject();
        try {
            friend.put("friend_id", id);
            friend.put("friend_firstName", firstName);
            friend.put("friend_lastName", lastName);
            friend.put("friend_phoneNumber", phoneNumber);
            friend.put("friend_accountNumber", accountNumber);
            friend.put("friend_displayPic", displayPic);
            friend.put("friend_displayColor", displayColor);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return friend;
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
                        temp.optString("friend_firstName")
                        , temp.optString("friend_lastName")
                        , temp.optString("friend_phoneNumber")
                        , temp.optString("friend_accountNumber")
                        , temp.optInt("friend_displayPic")
                        , temp.optInt("friend_displayColor")
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
                if (temp.optString("friend_phoneNumber").equals(phoneNumber)) {
                    result = new Friend(
                            temp.optString("friend_firstName")
                            , temp.optString("friend_lastName")
                            , temp.optString("friend_phoneNumber")
                            , temp.optString("friend_accountNumber")
                            , temp.optInt("friend_displayPic")
                            , temp.optInt("friend_displayColor")
                    );
                }
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Boolean isFriendListEmpty(Context ctx) {

        return (new StorageManager(ctx)).isFileEmpty(STORAGE_FILE_NAME);
    }

    public static void removeAllFriends(Context ctx) {
        (new StorageManager(ctx)).clearFile(STORAGE_FILE_NAME);
    }

    public static void resetFriends(Context ctx) {
        removeAllFriends(ctx);
        Profile profile = new Profile(ctx);
        if (profile.getName().equals("Damian Dutkiewicz")) {
            Friend fd1 = new Friend("Raymond", "Sak", "5139 6515", "355a515030616a53576b6a65797359506a634175764a734a3238314e4668627349486a676f7449463949453d", R.drawable.raymond, 0);
            fd1.saveSelfToFile(ctx);
        }
        else if (profile.getName().equals("Raymond Sak")) {
            Friend fd = new Friend("Damian", "Dutkiewicz", "5660 0981", "7977557255484c7345546c4e53424766634b6c53756841672b556857626e395253334b70416449676b42673d", R.drawable.damian, 0);
            fd.saveSelfToFile(ctx);
        }
        Friend fd2 = new Friend("Megan", "Gibbs", "9053 2443", "", 0, ctx.getResources().getIntArray(R.array.colors_icon)[0]);
        Friend fd3 = new Friend("Bryant", "Ryan", "5587 2988", "", 0, ctx.getResources().getIntArray(R.array.colors_icon)[5]);
        Friend fd4 = new Friend("Drew", "Jennings", "3557 7837", "", 0, ctx.getResources().getIntArray(R.array.colors_icon)[9]);
        fd2.saveSelfToFile(ctx);
        fd3.saveSelfToFile(ctx);
        fd4.saveSelfToFile(ctx);
    }
}