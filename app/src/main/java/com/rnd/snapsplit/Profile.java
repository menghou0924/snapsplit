package com.rnd.snapsplit;

import android.content.Context;

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
    private final static String STORAGE_FILE_NAME = "PERSONAL_PROFILE";
    private CitiAPIManager citiAPIManager;
    private CitiAPIBase citiAPIBase;
    private StorageManager storageManager;
    private final static Random RANDOM = new Random();

    public Profile(Context ctx) {
        context = ctx;
        citiAPIManager = new CitiAPIManager(ctx);
        citiAPIBase = new CitiAPIBase(ctx);
        storageManager = new StorageManager(ctx);

        citiAPIBase.API_Accounts_RetrieveAccountsSummary();
        citiAPIBase.API_Customers_RetrieveCustomerBasicName();
        citiAPIBase.API_MoneyMovement_RetrieveDestSrcAcct("ALL", "");
        citiAPIBase.API_MoneyMovement_RetrievePayeeList("ALL", "");
        citiAPIBase.API_MoneyMovement_RetrieveDestSrcAcctPersonal();
        citiAPIBase.API_MoneyMovement_RetrieveDestSrcAcctInternal("");
        citiAPIBase.API_MoneyMovement_RetrieveDestSrcAcctExternal("");
        citiAPIBase.API_MoneyMovement_RetrieveDestSrcAcctBillPayment("");

        if (!storageManager.getFile(STORAGE_FILE_NAME).isEmpty()) {
            JSONObject person = storageManager.getJSONObjectFromFile(STORAGE_FILE_NAME);
            id = person.optString("profile_id");
            name = person.optString("profile_name");
            phoneNumber = person.optString("profile_phoneNumber");
            accountNumber = person.optString("profile_accountNumber");
            displayAccountNumber = person.optString("profile_displayAccountNumber");
            displayPic = person.optInt("profile_displayPic");
            displayColor = person.optInt("profile_displayColor");
        }
        if (storageManager.getFile(STORAGE_FILE_NAME).isEmpty() || citiAPIManager.getEnglishName().isEmpty() || name.equals("Warren Buffett")) {

            id = Integer.toString(RANDOM.nextInt(99999999) + 10000000);
            name = citiAPIManager.getEnglishName();

            if (name.equals("AXDXEX JXNX AXNXT")) { // Sandboxuser1
                name = "Raymond Sak";
                displayPic = R.drawable.raymond;
                phoneNumber = "5139 6515";
                accountNumber = "3739334c4d3463614356474f6d7650667a737656664652677747796855646c5552745a43346d37423653553d"; // transfer from

            } else if (name.equals("CARSTEN K ANDREAS")) { // Sandboxuser2
                name = "Damian Dutkiewicz";
                displayPic = R.drawable.damian;
                phoneNumber = "5660 0981";
                accountNumber = "51327a46437565374770547776786c4348367545397331453164414177505a4e6d2b7131566d39476942303d"; // transfer to
            }
            else {
                name = "Warren Buffett";
                displayColor = context.getResources().getIntArray(R.array.colors_icon)[RANDOM.nextInt(10)];
                accountNumber = "3739334c4d3463614356474f6d7650667a737656664652677747796855646c5552745a43346d37423653553d";
                phoneNumber = Integer.toString(RANDOM.nextInt(99999999) + 10000000);
                phoneNumber = phoneNumber.substring(0, 4) + " " + phoneNumber.substring(4);// transfer to
            }

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
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return self;
    }
}
