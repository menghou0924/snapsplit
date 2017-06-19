package com.rnd.snapsplit;

import android.content.Context;
import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by menghou0924 on 6/18/17.
 */

public class CitiAPIManager {
    private Context context;
    private CitiAPIBase citiAPIBase;
    private StorageManager storageManager;

    public CitiAPIManager(Context ctx) {
        context = ctx;
        citiAPIBase = new CitiAPIBase(context);
        storageManager = new StorageManager(context);
    }

    public void basicDataFetch() {
        citiAPIBase.API_Accounts_RetrieveAccountsSummary();
        citiAPIBase.API_Customers_RetrieveCustomerBasicName();
        citiAPIBase.API_MoneyMovement_RetrieveDestSrcAcct("ALL", "");
        citiAPIBase.API_MoneyMovement_RetrievePayeeList("ALL", "");
        citiAPIBase.API_MoneyMovement_RetrieveDestSrcAcctPersonal();
        citiAPIBase.API_MoneyMovement_RetrieveDestSrcAcctInternal("");
        citiAPIBase.API_MoneyMovement_RetrieveDestSrcAcctExternal("");
        citiAPIBase.API_MoneyMovement_RetrieveDestSrcAcctBillPayment("");
    }

    public void logout() {
        String[] xmlAuthorization = context.getResources().getStringArray(R.array.citi_authorization);
        String[] xmlAccounts = context.getResources().getStringArray(R.array.citi_accounts);
        String[] xmlCustomers = context.getResources().getStringArray(R.array.citi_customers);
        String[] xmlMovement = context.getResources().getStringArray(R.array.citi_moneymovement);
        String[] xmlPayWithPoints = context.getResources().getStringArray(R.array.citi_paywithpoints);
        String[] xmlReference = context.getResources().getStringArray(R.array.citi_reference);

        for (String file: xmlAuthorization) {
            storageManager.clearFile(file);
        }
        for (String file: xmlAccounts) {
            storageManager.clearFile(file);
        }
        for (String file: xmlCustomers) {
            storageManager.clearFile(file);
        }
        for (String file: xmlMovement) {
            storageManager.clearFile(file);
        }
        for (String file: xmlPayWithPoints) {
            storageManager.clearFile(file);
        }
        for (String file: xmlReference) {
            storageManager.clearFile(file);
        }
    }

    public String getEnglishName() {
        String englishName = "";
        String DATA_CUSTOMER_RETRIEVE_CUSTOMER_BASIC_NAME = "DATA_CUSTOMER_RETRIEVE_CUSTOMER_BASIC_NAME";
        citiAPIBase.API_Customers_RetrieveCustomerBasicName();

        try {
            JSONObject profile = storageManager.getJSONObjectFromFile(DATA_CUSTOMER_RETRIEVE_CUSTOMER_BASIC_NAME);
            JSONObject particular = profile.optJSONObject("customerParticulars");
            JSONArray names = particular.optJSONArray("names");
            for (int i = 0; i < names.length(); i++) {
                JSONObject obj = names.getJSONObject(i);
                if (obj.optString("nameType").equals("ENGLISH_NAME")) {
                    englishName = obj.optString("fullName");
                }
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return englishName;
    }

    public String getDisplayAccountNumber(String accountNumber) {
        return "xxxxxxxx" + accountNumber.substring(accountNumber.length() - 6);
    }

    public String getAccountBalance() {
        String result = "";
        String DATA_ACCOUNTS_RETRIEVE_ACCOUNT_DETAILS = "DATA_ACCOUNTS_RETRIEVE_ACCOUNT_DETAILS";
        JSONObject obj = storageManager.getJSONObjectFromFile(DATA_ACCOUNTS_RETRIEVE_ACCOUNT_DETAILS);
        if (obj.keys().hasNext()) {
            result = obj.optJSONObject(obj.keys().next()).optString("availableBalance");
        }
        return result;
    }
}
