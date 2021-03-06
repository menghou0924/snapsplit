package com.rnd.snapsplit;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.net.Uri;

import com.rnd.snapsplit.view.MainActivity;
import com.rnd.snapsplit.view.OwedFragment;

import org.apache.http.auth.AUTH;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by menghou0924 on 6/3/17.
 */

public class CitiAPIBase {

    // Class members
    private static final String TAG = "CitiAPIBase";
    private static final String BASE_URL = "https://sandbox.apihub.citi.com/gcb/api";
    private static final int SUCCESSFUL_RESPONSE_CODE = 200;
    private StorageManager storageManager;
    private Context context;
    // Authorization members
    private static final String DATA_AUTHORIZATION_RETRIEVE_ACCESS_REFRESH_TOKEN = "DATA_AUTHORIZATION_RETRIEVE_ACCESS_REFRESH_TOKEN";
    private static final String AUTHORIZATION_ACCESS_TOKEN_URL = BASE_URL + "/authCode/oauth2/token/hk/gcb";
    private static final String CLIENT_ID = "54288371-9c5a-4638-81bd-8cdccbb4e9e7";
    private static final String CLIENT_SECRET = "fF1mG0dO2pL5dH0uD4gH7gK2aD8dL8gN6vE6oK3sR8vT4uK3cV";
    private static final String STATE = "12093";
    private static final String REDIRECT_URI = "https://www.snapsplit.com";
    private static final String AUTH_URL = BASE_URL + "/authCode/oauth2/authorize" //?nextStartIndex=
            + "?response_type=%s&client_id=%s&scope=%s&countryCode=%s&businessCode=%s&locale=%s&state=%s&redirect_uri=%s";
    private static final String ENCODED_AUTH_STRING = Base64.encodeToString((CLIENT_ID + ":" + CLIENT_SECRET).getBytes(), Base64.NO_WRAP);
    // Accounts members
    private static final String DATA_ACCOUNTS_RETRIEVE_ACCOUNTS_SUMMARY = "DATA_ACCOUNTS_RETRIEVE_ACCOUNTS_SUMMARY";
    private static final String DATA_ACCOUNTS_RETRIEVE_ACCOUNT_DETAILS = "DATA_ACCOUNTS_RETRIEVE_ACCOUNT_DETAILS";
    private static final String DATA_ACCOUNTS_RETRIEVE_ACCOUNT_TRANSACTIONS = "DATA_ACCOUNTS_RETRIEVE_ACCOUNT_TRANSACTIONS";
    private static final String ACCOUNTS_SUMMARY_URL = BASE_URL + "/v1/accounts";
    private static final String ACCOUNT_DETAILS_URL = BASE_URL + "/v1/accounts/%s";
    private static final String ACCOUNT_TRANSACTION_URL = BASE_URL + "/v1/accounts/%s/transactions?%s";
    // Customer members
    private static final String DATA_CUSTOMER_RETRIEVE_CUSTOMER_BASIC_NAME = "DATA_CUSTOMER_RETRIEVE_CUSTOMER_BASIC_NAME";
    private static final String CUSTOMER_BASIC_NAME_URL = BASE_URL + "/v1/customers/profiles/basic";
    // Money Movement members
    private static final String DATA_MONEY_MOVEMENT_RETRIEVE_DEST_SRC_ACCOUNT = "DATA_MONEY_MOVEMENT_RETRIEVE_DEST_SRC_ACCOUNT";
    private static final String DATA_MONEY_MOVEMENT_RETRIEVE_PAYEE_LIST = "DATA_MONEY_MOVEMENT_RETRIEVE_PAYEE_LIST";
    private static final String DATA_MONEY_MOVEMENT_RETRIEVE_DEST_SRC_ACCOUNT_PERSONAL = "DATA_MONEY_MOVEMENT_RETRIEVE_DEST_SRC_ACCOUNT_PERSONAL";
    private static final String DATA_MONEY_MOVEMENT_CREATE_PERSONAL_TRANSFER = "DATA_MONEY_MOVEMENT_CREATE_PERSONAL_TRANSFER";
    private static final String DATA_MONEY_MOVEMENT_CONFIRM_PERSONAL_TRANSFER = "DATA_MONEY_MOVEMENT_CONFIRM_PERSONAL_TRANSFER";
    private static final String DATA_MONEY_MOVEMENT_RETRIEVE_DEST_SRC_ACCOUNT_INTERNAL = "DATA_MONEY_MOVEMENT_RETRIEVE_DEST_SRC_ACCOUNT_INTERNAL";
    private static final String DATA_MONEY_MOVEMENT_CREATE_INTERNAL_TRANSFER = "DATA_MONEY_MOVEMENT_CREATE_INTERNAL_TRANSFER";
    private static final String DATA_MONEY_MOVEMENT_CONFIRM_INTERNAL_TRANSFER = "DATA_MONEY_MOVEMENT_CONFIRM_INTERNAL_TRANSFER";
    private static final String DATA_MONEY_MOVEMENT_RETRIEVE_DEST_SRC_ACCOUNT_EXTERNAL = "DATA_MONEY_MOVEMENT_RETRIEVE_DEST_SRC_ACCOUNT_EXTERNAL";
    private static final String DATA_MONEY_MOVEMENT_CREATE_EXTERNAL_TRANSFER = "DATA_MONEY_MOVEMENT_CREATE_EXTERNAL_TRANSFER";
    private static final String DATA_MONEY_MOVEMENT_CONFIRM_EXTERNAL_TRANSFER = "DATA_MONEY_MOVEMENT_CONFIRM_EXTERNAL_TRANSFER";
    private static final String DATA_MONEY_MOVEMENT_RETRIEVE_DEST_SRC_ACCOUNT_BILL_PAYMENT = "DATA_MONEY_MOVEMENT_RETRIEVE_DEST_SRC_ACCOUNT_BILL_PAYMENT";
    private static final String DATA_MONEY_MOVEMENT_CREATE_BILL_PAYMENT = "DATA_MONEY_MOVEMENT_CREATE_BILL_PAYMENT";
    private static final String DATA_MONEY_MOVEMENT_CONFIRM_BILL_PAYMENT = "DATA_MONEY_MOVEMENT_CONFIRM_BILL_PAYMENT";
    private static final String MONEY_MOVEMENT_SOURCE_ACCOUNT_URL = BASE_URL + "/v1/moneyMovement/payees/sourceAccounts?%s";
    private static final String MONEY_MOVEMENT_PAYEE_URL = BASE_URL + "/v1/moneyMovement/payees?%s";
    private static final String MONEY_MOVEMENT_PERSONAL_SOURCE_ACCOUNT_URL = BASE_URL + "/v1/moneyMovement/personalDomesticTransfers/destinationAccounts/sourceAccounts";
    private static final String MONEY_MOVEMENT_CREATE_PERSONAL_TRANSFER_URL = BASE_URL + "/v1/moneyMovement/personalDomesticTransfers/preprocess";
    private static final String MONEY_MOVEMENT_CONFIRM_PERSONAL_TRANSFER_URL = BASE_URL + "/v1/moneyMovement/personalDomesticTransfers";
    private static final String MONEY_MOVEMENT_INTERNAL_SOURCE_ACCOUNT_URL = BASE_URL + "/v1/moneyMovement/internalDomesticTransfers/payees/sourceAccounts%s";
    private static final String MONEY_MOVEMENT_CREATE_INTERNAL_TRANSFER_URL = BASE_URL + "/v1/moneyMovement/internalDomesticTransfers/preprocess";
    private static final String MONEY_MOVEMENT_CONFIRM_INTERNAL_TRANSFER_URL = BASE_URL + "/v1/moneyMovement/internalDomesticTransfers";
    private static final String MONEY_MOVEMENT_EXTERNAL_SOURCE_ACCOUNT_URL = BASE_URL + "/v1/moneyMovement/externalDomesticTransfers/payees/sourceAccounts%s";
    private static final String MONEY_MOVEMENT_CREATE_EXTERNAL_TRANSFER_URL = BASE_URL + "/v1/moneyMovement/externalDomesticTransfer/preprocess";
    private static final String MONEY_MOVEMENT_CONFIRM_EXTERNAL_TRANSFER_URL = BASE_URL + "/v1/moneyMovement/externalDomesticTransfers";
    private static final String MONEY_MOVEMENT_BILL_PAYMENT_URL = BASE_URL + "/v1/moneyMovement/billPayments/payees/sourceAccounts%s";
    private static final String MONEY_MOVEMENT_CREATE_BILL_PAYMENT_URL = BASE_URL + "/v1/moneyMovement/billPayments/preprocess";
    private static final String MONEY_MOVEMENT_CONFIRM_BILL_PAYMENTR_URL = BASE_URL + "/v1/moneyMovement/billPayments";
    // Pay with points members
    private static final String DATA_PAY_WITH_POINTS_ENROLL = "DATA_PAY_WITH_POINTS_ENROLL";
    private static final String DATA_PAY_WITH_POINTS_TOKEN_ACTIVATION = "DATA_PAY_WITH_POINTS_TOKEN_ACTIVATION";
    private static final String DATA_PAY_WITH_POINTS_RETRIEVE_REWARD_BALANCE = "DATA_PAY_WITH_POINTS_RETRIEVE_REWARD_BALANCE";
    private static final String DATA_PAY_WITH_POINTS_SUBMIT_REDEMPTION_REQUEST = "DATA_PAY_WITH_POINTS_SUBMIT_REDEMPTION_REQUEST";
    private static final String PAY_WITH_POINTS_ENROLL_URL = BASE_URL + "/v1/apac/rewards/linkage";
    private static final String PAY_WITH_POINTS_TOKEN_ACTIVATION_URL = BASE_URL + "/v1/apac/rewards/%s/activations";
    private static final String PAY_WITH_POINTS_RETRIEVE_REWARD_BALANCE_URL = BASE_URL + "/v1/apac/rewards/%s/pointBalance";
    private static final String PAY_WITH_POINTS_SUBMIT_REDEMPTION_REQUEST_URL = BASE_URL + "/v1/apac/rewards/%s/redemption";

    // Reference Data members
    private static final String DATA_REFERENCE_DATA_RETRIEVE_VALID_VALUES = "DATA_REFERENCE_DATA_RETRIEVE_VALID_VALUES";
    private static final String RETRIEVE_VALID_VALUES_URL = BASE_URL + "/v1/apac/utilities/referenceData/%s";

    // Class functions

    public CitiAPIBase(Context ctx) {
        context = ctx;
        storageManager = new StorageManager(ctx);
    }

    private String getStoredValues(String functionality, String key) {
        return storageManager.getValueFromFile(functionality, key);
    }

    // Authorization functions

    public String getAuthURL() {
        return String.format(AUTH_URL
                , "code" // response_type
                , CLIENT_ID // client_id
                , "pay_with_point accounts_details_transactions customers_profiles payees personal_domestic_transfers "
                        + "internal_domestic_transfers external_domestic_transfers bill_payments cards onboarding reference_data" // scope
                , "HK" // countryCode
                , "GCB" // businessCode
                , "en_HK" // locale
                , STATE // state
                , REDIRECT_URI); // redirect_url
    }

    public void API_Authorization_RetrieveAccessToken(Uri uri) {

        if (checkState(uri)) {
            String code = uri.getQueryParameter("code");

            Request request = new Request.Builder()
                    .addHeader("Authorization", "Basic " + ENCODED_AUTH_STRING)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .url(AUTHORIZATION_ACCESS_TOKEN_URL)
                    .post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"),
                            "grant_type=authorization_code&code=" + code +
                                    "&redirect_uri=" + REDIRECT_URI))
                    .build(); // may need to add nextStartIndex parameter later

            OkHttpClient client = new OkHttpClient();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "ERROR: " + e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String json = response.body().string();

                    JSONObject data = null;
                    try {
                        data = new JSONObject(json);
                        if (response.code() == SUCCESSFUL_RESPONSE_CODE) {
                            storageManager.saveFile(DATA_AUTHORIZATION_RETRIEVE_ACCESS_REFRESH_TOKEN, data.toString());
                            Log.d(TAG, DATA_AUTHORIZATION_RETRIEVE_ACCESS_REFRESH_TOKEN + getStoredValues(DATA_AUTHORIZATION_RETRIEVE_ACCESS_REFRESH_TOKEN, "access_token"));
                            Log.d(TAG, DATA_AUTHORIZATION_RETRIEVE_ACCESS_REFRESH_TOKEN + getStoredValues(DATA_AUTHORIZATION_RETRIEVE_ACCESS_REFRESH_TOKEN, "refresh_token"));

                            Profile profile = new Profile(context);

                            Intent intent2 = new Intent(context, MainActivity.class);
                            context.startActivity(intent2);
                        }
                        else {
                            Log.e(TAG, "ERROR: " + DATA_AUTHORIZATION_RETRIEVE_ACCESS_REFRESH_TOKEN + data.toString());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        else {
            Log.e(TAG, "ERROR: " + DATA_AUTHORIZATION_RETRIEVE_ACCESS_REFRESH_TOKEN + " Incorrect state.");
        }
    }

    public void API_Authorization_RefreshAccessToken() {

        Request request = new Request.Builder()
                .addHeader("Authorization", "Basic " + ENCODED_AUTH_STRING)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .url(AUTHORIZATION_ACCESS_TOKEN_URL)
                .post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"),
                        "grant_type=refresh_token&refresh_token=" + getStoredValues(DATA_AUTHORIZATION_RETRIEVE_ACCESS_REFRESH_TOKEN, "refresh_token")))
                .build(); // may need to add nextStartIndex parameter later

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "ERROR: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();

                JSONObject data = null;
                try {
                    data = new JSONObject(json);
                    if (response.code() == SUCCESSFUL_RESPONSE_CODE) {
                        storageManager.saveFile(DATA_AUTHORIZATION_RETRIEVE_ACCESS_REFRESH_TOKEN, data.toString());
                        Log.d(TAG, DATA_AUTHORIZATION_RETRIEVE_ACCESS_REFRESH_TOKEN + getStoredValues(DATA_AUTHORIZATION_RETRIEVE_ACCESS_REFRESH_TOKEN, "access_token"));
                        Log.d(TAG, DATA_AUTHORIZATION_RETRIEVE_ACCESS_REFRESH_TOKEN + getStoredValues(DATA_AUTHORIZATION_RETRIEVE_ACCESS_REFRESH_TOKEN, "refresh_token"));
                    }
                    else {
                        Log.e(TAG, "ERROR: " + DATA_AUTHORIZATION_RETRIEVE_ACCESS_REFRESH_TOKEN + data.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @NonNull
    private Boolean checkState(Uri uri) {
        if (uri.getQueryParameter("error") != null) {
            String error = uri.getQueryParameter("error");
            Log.e(TAG, "An error has occurred : " + error);
        } else {
            String state = uri.getQueryParameter("state");
            if (state.equals(STATE)) {
                return true;
            }
        }
        return false;
    }

    // Accounts functions

    public void API_Accounts_RetrieveAccountsSummary() {
        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + getStoredValues(DATA_AUTHORIZATION_RETRIEVE_ACCESS_REFRESH_TOKEN, "access_token"))
                .addHeader("uuid", UUID.randomUUID().toString())
                .addHeader("Accept", "application/json")
                .addHeader("client_id", CLIENT_ID)
                .url(ACCOUNTS_SUMMARY_URL)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "ERROR: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                JSONObject data = null;

                try {
                    data = new JSONObject(json);
                    if (response.code() == SUCCESSFUL_RESPONSE_CODE) {
                        storageManager.saveFile(DATA_ACCOUNTS_RETRIEVE_ACCOUNTS_SUMMARY, data.toString());
                        Log.d(TAG, DATA_ACCOUNTS_RETRIEVE_ACCOUNTS_SUMMARY + "JSON DATA = " + data.toString());
                    }
                    else {
                        Log.e(TAG, "ERROR: " + DATA_ACCOUNTS_RETRIEVE_ACCOUNTS_SUMMARY + data.toString());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void API_Accounts_RetrieveAccountDetails(String accountId) {
        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + getStoredValues(DATA_AUTHORIZATION_RETRIEVE_ACCESS_REFRESH_TOKEN, "access_token"))
                .addHeader("uuid", UUID.randomUUID().toString())
                .addHeader("Accept", "application/json")
                .addHeader("client_id", CLIENT_ID)
                .url(String.format(ACCOUNT_DETAILS_URL, accountId))
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "ERROR: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                JSONObject data = null;

                try {
                    data = new JSONObject(json);
                    if (response.code() == SUCCESSFUL_RESPONSE_CODE) {
                        storageManager.saveFile(DATA_ACCOUNTS_RETRIEVE_ACCOUNT_DETAILS, data.toString());
                        Log.d(TAG, DATA_ACCOUNTS_RETRIEVE_ACCOUNT_DETAILS + "JSON DATA = " + data.toString());
                    }
                    else {
                        Log.e(TAG, "ERROR: " + DATA_ACCOUNTS_RETRIEVE_ACCOUNT_DETAILS + data.toString());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void API_Accounts_RetrieveAccountTransactions(String accountId, String transactionStatus, String nextStartIndex
            , String requestSize, String transactionFromDate, String transactionToDate, String amountFrom, String amountTo) {
        String param = "";
        param = param.concat(transactionStatus.isEmpty() ? "" : (param.isEmpty() ? "transactionStatus=" + transactionStatus : "&transactionStatus=" + transactionStatus));
        param = param.concat(nextStartIndex.isEmpty() ? "" : (param.isEmpty() ? "nextStartIndex=" + nextStartIndex : "&nextStartIndex=" + nextStartIndex));
        param = param.concat(requestSize.isEmpty() ? "" : (param.isEmpty() ? "requestSize=" + requestSize : "&requestSize=" + requestSize));
        param = param.concat(transactionFromDate.isEmpty() ? "" : (param.isEmpty() ? "transactionFromDate=" + transactionFromDate : "&transactionFromDate=" + transactionFromDate));
        param = param.concat(transactionToDate.isEmpty() ? "" : (param.isEmpty() ? "transactionToDate=" + transactionToDate : "&transactionToDate=" + transactionToDate));
        param = param.concat(amountFrom.isEmpty() ? "" : (param.isEmpty() ? "amountFrom=" + amountFrom : "&amountFrom=" + amountFrom));
        param = param.concat(amountTo.isEmpty() ? "" : (param.isEmpty() ? "amountTo=" + amountTo : "&amountTo=" + amountTo));

        String request_url = String.format(ACCOUNT_TRANSACTION_URL, accountId, param);
        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + getStoredValues(DATA_AUTHORIZATION_RETRIEVE_ACCESS_REFRESH_TOKEN, "access_token"))
                .addHeader("uuid", UUID.randomUUID().toString())
                .addHeader("Accept", "application/json")
                .addHeader("client_id", CLIENT_ID)
                .url(request_url)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "ERROR: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                JSONObject data = null;

                try {
                    data = new JSONObject(json);
                    if (response.code() == SUCCESSFUL_RESPONSE_CODE) {
                        storageManager.saveFile(DATA_ACCOUNTS_RETRIEVE_ACCOUNT_TRANSACTIONS, data.toString());
                        Log.d(TAG, DATA_ACCOUNTS_RETRIEVE_ACCOUNT_TRANSACTIONS + "JSON DATA = " + data.toString());
                    }
                    else {
                        Log.e(TAG, "ERROR: " + DATA_ACCOUNTS_RETRIEVE_ACCOUNT_TRANSACTIONS + data.toString());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // Customer functions

    public void API_Customers_RetrieveCustomerBasicName() {
        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + getStoredValues(DATA_AUTHORIZATION_RETRIEVE_ACCESS_REFRESH_TOKEN, "access_token"))
                .addHeader("uuid", UUID.randomUUID().toString())
                .addHeader("Accept", "application/json")
                .addHeader("client_id", CLIENT_ID)
                .url(CUSTOMER_BASIC_NAME_URL)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "ERROR: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                JSONObject data = null;

                try {
                    data = new JSONObject(json);
                    if (response.code() == SUCCESSFUL_RESPONSE_CODE) {
                        storageManager.saveFile(DATA_CUSTOMER_RETRIEVE_CUSTOMER_BASIC_NAME, data.toString());
                        Log.d(TAG, DATA_CUSTOMER_RETRIEVE_CUSTOMER_BASIC_NAME + "JSON DATA = " + data.toString());
                    }
                    else {
                        Log.e(TAG, "ERROR: " + DATA_CUSTOMER_RETRIEVE_CUSTOMER_BASIC_NAME + data.toString());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // Money Movement functions

    public void API_MoneyMovement_RetrieveDestSrcAcct(String paymentType, String nextStartIndex) {
        String param = "";
        param = param.concat(paymentType.isEmpty() ? "" : (param.isEmpty() ? "paymentType=" + paymentType : "&paymentType=" + paymentType));
        param = param.concat(nextStartIndex.isEmpty() ? "" : (param.isEmpty() ? "nextStartIndex=" + nextStartIndex : "&nextStartIndex=" + nextStartIndex));
        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + getStoredValues(DATA_AUTHORIZATION_RETRIEVE_ACCESS_REFRESH_TOKEN, "access_token"))
                .addHeader("uuid", UUID.randomUUID().toString())
                .addHeader("Accept", "application/json")
                .addHeader("client_id", CLIENT_ID)
                .url(String.format(MONEY_MOVEMENT_SOURCE_ACCOUNT_URL, param))
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "ERROR: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                JSONObject data = null;

                try {
                    data = new JSONObject(json);
                    if (response.code() == SUCCESSFUL_RESPONSE_CODE) {
                        storageManager.saveFile(DATA_MONEY_MOVEMENT_RETRIEVE_DEST_SRC_ACCOUNT, data.toString());
                        Log.d(TAG, DATA_MONEY_MOVEMENT_RETRIEVE_DEST_SRC_ACCOUNT + "JSON DATA = " + data.toString());
                    }
                    else {
                        Log.e(TAG, "ERROR: " + DATA_MONEY_MOVEMENT_RETRIEVE_DEST_SRC_ACCOUNT + data.toString());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void API_MoneyMovement_RetrievePayeeList(String paymentType, String nextStartIndex) {
        String param = "";
        param = param.concat(paymentType.isEmpty() ? "" : (param.isEmpty() ? "paymentType=" + paymentType : "&paymentType=" + paymentType));
        param = param.concat(nextStartIndex.isEmpty() ? "" : (param.isEmpty() ? "nextStartIndex=" + nextStartIndex : "&nextStartIndex=" + nextStartIndex));
        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + getStoredValues(DATA_AUTHORIZATION_RETRIEVE_ACCESS_REFRESH_TOKEN, "access_token"))
                .addHeader("uuid", UUID.randomUUID().toString())
                .addHeader("Accept", "application/json")
                .addHeader("client_id", CLIENT_ID)
                .url(String.format(MONEY_MOVEMENT_PAYEE_URL, param))
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "ERROR: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                JSONObject data = null;

                try {
                    data = new JSONObject(json);
                    if (response.code() == SUCCESSFUL_RESPONSE_CODE) {
                        storageManager.saveFile(DATA_MONEY_MOVEMENT_RETRIEVE_PAYEE_LIST, data.toString());
                        Log.d(TAG, DATA_MONEY_MOVEMENT_RETRIEVE_PAYEE_LIST + "JSON DATA = " + data.toString());
                    }
                    else {
                        Log.e(TAG, "ERROR: " + DATA_MONEY_MOVEMENT_RETRIEVE_PAYEE_LIST + data.toString());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void API_MoneyMovement_RetrieveDestSrcAcctPersonal() {
        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + getStoredValues(DATA_AUTHORIZATION_RETRIEVE_ACCESS_REFRESH_TOKEN, "access_token"))
                .addHeader("uuid", UUID.randomUUID().toString())
                .addHeader("Accept", "application/json")
                .addHeader("client_id", CLIENT_ID)
                .url(MONEY_MOVEMENT_PERSONAL_SOURCE_ACCOUNT_URL)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "ERROR: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                JSONObject data = null;

                try {
                    data = new JSONObject(json);
                    if (response.code() == SUCCESSFUL_RESPONSE_CODE) {
                        storageManager.saveFile(DATA_MONEY_MOVEMENT_RETRIEVE_DEST_SRC_ACCOUNT_PERSONAL, data.toString());
                        Log.d(TAG, DATA_MONEY_MOVEMENT_RETRIEVE_DEST_SRC_ACCOUNT_PERSONAL + "JSON DATA = " + data.toString());
                    }
                    else {
                        Log.e(TAG, "ERROR: " + DATA_MONEY_MOVEMENT_RETRIEVE_DEST_SRC_ACCOUNT_PERSONAL + data.toString());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void API_MoneyMovement_CreatePersonalTransfer(String sourceAccountId, String transactionAmount, String transferCurrencyIndicator
            , String destinationAccountId, String chargeBearer, String fxDealReferenceNumber, String remark
            , final Fragment currentFragment, final PaymentRequest pr) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("sourceAccountId", sourceAccountId);
            jsonObject.put("transactionAmount", transactionAmount);
            jsonObject.put("transferCurrencyIndicator", transferCurrencyIndicator);
            jsonObject.put("destinationAccountId", destinationAccountId);
            jsonObject.put("chargeBearer", chargeBearer);
            jsonObject.put("fxDealReferenceNumber", fxDealReferenceNumber);
            jsonObject.put("remark", remark);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + getStoredValues(DATA_AUTHORIZATION_RETRIEVE_ACCESS_REFRESH_TOKEN, "access_token"))
                .addHeader("uuid", UUID.randomUUID().toString())
                .addHeader("Accept", "application/json")
                .addHeader("client_id", CLIENT_ID)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString()))
                .url(MONEY_MOVEMENT_CREATE_PERSONAL_TRANSFER_URL)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "ERROR: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                JSONObject data = null;

                try {
                    data = new JSONObject(json);
                    if (response.code() == SUCCESSFUL_RESPONSE_CODE) {
                        storageManager.saveFile(DATA_MONEY_MOVEMENT_CREATE_PERSONAL_TRANSFER, data.toString());
                        Log.d(TAG, DATA_MONEY_MOVEMENT_CREATE_PERSONAL_TRANSFER + "JSON DATA = " + data.toString());
//                        ((OwedFragment) currentFragment).postSuccessfulPayment(pr);
                    }
                    else {
                        Log.e(TAG, "ERROR: " + DATA_MONEY_MOVEMENT_CREATE_PERSONAL_TRANSFER + data.toString());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void API_MoneyMovement_ConfirmPersonalTransfer(String controlFlowId) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("controlFlowId", controlFlowId);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + getStoredValues(DATA_AUTHORIZATION_RETRIEVE_ACCESS_REFRESH_TOKEN, "access_token"))
                .addHeader("uuid", UUID.randomUUID().toString())
                .addHeader("Accept", "application/json")
                .addHeader("client_id", CLIENT_ID)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString()))
                .url(MONEY_MOVEMENT_CONFIRM_PERSONAL_TRANSFER_URL)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "ERROR: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                JSONObject data = null;

                try {
                    data = new JSONObject(json);
                    if (response.code() == SUCCESSFUL_RESPONSE_CODE) {
                        storageManager.saveFile(DATA_MONEY_MOVEMENT_CONFIRM_PERSONAL_TRANSFER, data.toString());
                        Log.d(TAG, DATA_MONEY_MOVEMENT_CONFIRM_PERSONAL_TRANSFER + "JSON DATA = " + data.toString());
                    }
                    else {
                        Log.e(TAG, "ERROR: " + DATA_MONEY_MOVEMENT_CONFIRM_PERSONAL_TRANSFER + data.toString());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void API_MoneyMovement_RetrieveDestSrcAcctInternal(String nextStartIndex) {
        String param = nextStartIndex.isEmpty() ? "" : "?nextStartIndex=" + nextStartIndex;
        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + getStoredValues(DATA_AUTHORIZATION_RETRIEVE_ACCESS_REFRESH_TOKEN, "access_token"))
                .addHeader("uuid", UUID.randomUUID().toString())
                .addHeader("Accept", "application/json")
                .addHeader("client_id", CLIENT_ID)
                .url(String.format(MONEY_MOVEMENT_INTERNAL_SOURCE_ACCOUNT_URL, param))
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "ERROR: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                JSONObject data = null;

                try {
                    data = new JSONObject(json);
                    if (response.code() == SUCCESSFUL_RESPONSE_CODE) {
                        storageManager.saveFile(DATA_MONEY_MOVEMENT_RETRIEVE_DEST_SRC_ACCOUNT_INTERNAL, data.toString());
                        Log.d(TAG, DATA_MONEY_MOVEMENT_RETRIEVE_DEST_SRC_ACCOUNT_INTERNAL + "JSON DATA = " + data.toString());
                    }
                    else {
                        Log.e(TAG, "ERROR: " + DATA_MONEY_MOVEMENT_RETRIEVE_DEST_SRC_ACCOUNT_INTERNAL + data.toString());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void API_MoneyMovement_CreateInternalTransfer(String sourceAccountId, String transactionAmount, String transferCurrencyIndicator
            , String payeeId, String chargeBearer, String fxDealReferenceNumber, String remark, String transferPurpose
            , final Fragment currentFragment, final PaymentRequest paymentRequest) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("sourceAccountId", sourceAccountId);
            jsonObject.put("transactionAmount", transactionAmount);
            jsonObject.put("transferCurrencyIndicator", transferCurrencyIndicator);
            jsonObject.put("payeeId", payeeId);
            jsonObject.put("chargeBearer", chargeBearer);
            jsonObject.put("fxDealReferenceNumber", fxDealReferenceNumber);
            jsonObject.put("remark", remark);
            jsonObject.put("transferPurpose", transferPurpose);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + getStoredValues(DATA_AUTHORIZATION_RETRIEVE_ACCESS_REFRESH_TOKEN, "access_token"))
                .addHeader("uuid", UUID.randomUUID().toString())
                .addHeader("Accept", "application/json")
                .addHeader("client_id", CLIENT_ID)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString()))
                .url(MONEY_MOVEMENT_CREATE_INTERNAL_TRANSFER_URL)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "ERROR: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                JSONObject data = null;

                try {
                    data = new JSONObject(json);
                    if (response.code() == SUCCESSFUL_RESPONSE_CODE) {
                        storageManager.saveFile(DATA_MONEY_MOVEMENT_CREATE_INTERNAL_TRANSFER, data.toString());
                        Log.d(TAG, DATA_MONEY_MOVEMENT_CREATE_INTERNAL_TRANSFER + "JSON DATA = " + data.toString());
                        if (storageManager.getJSONObjectFromFile(DATA_MONEY_MOVEMENT_CREATE_INTERNAL_TRANSFER)
                                .optJSONObject("debitDetails").optString("transactionDebitAmount")
                                .equals(paymentRequest.getShareAmount())) {
//                            API_MoneyMovement_ConfirmInternalTransfer(storageManager.getValueFromFile(
//                                    DATA_MONEY_MOVEMENT_CREATE_INTERNAL_TRANSFER, "controlFlowId"), currentFragment, paymentRequest);
                        }
                    }
                    else {
                        Log.e(TAG, "ERROR: " + DATA_MONEY_MOVEMENT_CREATE_INTERNAL_TRANSFER + data.toString());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void API_MoneyMovement_ConfirmInternalTransfer(String controlFlowId
            , final Fragment currentFragment, final PaymentRequest paymentRequest) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("controlFlowId", controlFlowId);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + getStoredValues(DATA_AUTHORIZATION_RETRIEVE_ACCESS_REFRESH_TOKEN, "access_token"))
                .addHeader("uuid", UUID.randomUUID().toString())
                .addHeader("Accept", "application/json")
                .addHeader("client_id", CLIENT_ID)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString()))
                .url(MONEY_MOVEMENT_CONFIRM_INTERNAL_TRANSFER_URL)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "ERROR: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                JSONObject data = null;

                try {
                    data = new JSONObject(json);
                    if (response.code() == SUCCESSFUL_RESPONSE_CODE) {
                        storageManager.saveFile(DATA_MONEY_MOVEMENT_CONFIRM_INTERNAL_TRANSFER, data.toString());
                        Log.d(TAG, DATA_MONEY_MOVEMENT_CONFIRM_INTERNAL_TRANSFER + "JSON DATA = " + data.toString());
//                        ((OwedFragment) currentFragment).postSuccessfulPayment(paymentRequest);
                    }
                    else {
                        Log.e(TAG, "ERROR: " + DATA_MONEY_MOVEMENT_CONFIRM_INTERNAL_TRANSFER + data.toString());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void API_MoneyMovement_RetrieveDestSrcAcctExternal(String nextStartIndex) {
        String param = nextStartIndex.isEmpty() ? "" : "?nextStartIndex=" + nextStartIndex;
        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + getStoredValues(DATA_AUTHORIZATION_RETRIEVE_ACCESS_REFRESH_TOKEN, "access_token"))
                .addHeader("uuid", UUID.randomUUID().toString())
                .addHeader("Accept", "application/json")
                .addHeader("client_id", CLIENT_ID)
                .url(String.format(MONEY_MOVEMENT_EXTERNAL_SOURCE_ACCOUNT_URL, param))
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "ERROR: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                JSONObject data = null;

                try {
                    data = new JSONObject(json);
                    if (response.code() == SUCCESSFUL_RESPONSE_CODE) {
                        storageManager.saveFile(DATA_MONEY_MOVEMENT_RETRIEVE_DEST_SRC_ACCOUNT_EXTERNAL, data.toString());
                        Log.d(TAG, DATA_MONEY_MOVEMENT_RETRIEVE_DEST_SRC_ACCOUNT_EXTERNAL + "JSON DATA = " + data.toString());
                    }
                    else {
                        Log.e(TAG, "ERROR: " + DATA_MONEY_MOVEMENT_RETRIEVE_DEST_SRC_ACCOUNT_EXTERNAL + data.toString());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void API_MoneyMovement_CreateExternalTransfer(String sourceAccountId, String transactionAmount, String transferCurrencyIndicator
            , String payeeId, String chargeBearer,String paymentMethod, String fxDealReferenceNumber, String remark, String transferPurpose) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("sourceAccountId", sourceAccountId);
            jsonObject.put("transactionAmount", transactionAmount);
            jsonObject.put("transferCurrencyIndicator", transferCurrencyIndicator);
            jsonObject.put("payeeId", payeeId);
            jsonObject.put("chargeBearer", chargeBearer);
            jsonObject.put("paymentMethod", paymentMethod);
            jsonObject.put("fxDealReferenceNumber", fxDealReferenceNumber);
            jsonObject.put("remark", remark);
            jsonObject.put("transferPurpose", transferPurpose);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + getStoredValues(DATA_AUTHORIZATION_RETRIEVE_ACCESS_REFRESH_TOKEN, "access_token"))
                .addHeader("uuid", UUID.randomUUID().toString())
                .addHeader("Accept", "application/json")
                .addHeader("client_id", CLIENT_ID)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString()))
                .url(MONEY_MOVEMENT_CREATE_EXTERNAL_TRANSFER_URL)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "ERROR: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                JSONObject data = null;

                try {
                    data = new JSONObject(json);
                    if (response.code() == SUCCESSFUL_RESPONSE_CODE) {
                        storageManager.saveFile(DATA_MONEY_MOVEMENT_CREATE_EXTERNAL_TRANSFER, data.toString());
                        Log.d(TAG, DATA_MONEY_MOVEMENT_CREATE_EXTERNAL_TRANSFER + "JSON DATA = " + data.toString());
                    }
                    else {
                        Log.e(TAG, "ERROR: " + DATA_MONEY_MOVEMENT_CREATE_EXTERNAL_TRANSFER + data.toString());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void API_MoneyMovement_ConfirmExternalTransfer(String controlFlowId) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("controlFlowId", controlFlowId);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + getStoredValues(DATA_AUTHORIZATION_RETRIEVE_ACCESS_REFRESH_TOKEN, "access_token"))
                .addHeader("uuid", UUID.randomUUID().toString())
                .addHeader("Accept", "application/json")
                .addHeader("client_id", CLIENT_ID)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString()))
                .url(MONEY_MOVEMENT_CONFIRM_EXTERNAL_TRANSFER_URL)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "ERROR: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                JSONObject data = null;

                try {
                    data = new JSONObject(json);
                    if (response.code() == SUCCESSFUL_RESPONSE_CODE) {
                        storageManager.saveFile(DATA_MONEY_MOVEMENT_CONFIRM_EXTERNAL_TRANSFER, data.toString());
                        Log.d(TAG, DATA_MONEY_MOVEMENT_CONFIRM_EXTERNAL_TRANSFER + "JSON DATA = " + data.toString());
                    }
                    else {
                        Log.e(TAG, "ERROR: " + DATA_MONEY_MOVEMENT_CONFIRM_EXTERNAL_TRANSFER + data.toString());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void API_MoneyMovement_RetrieveDestSrcAcctBillPayment(String nextStartIndex) {
        String param = nextStartIndex.isEmpty() ? "" : "?nextStartIndex=" + nextStartIndex;
        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + getStoredValues(DATA_AUTHORIZATION_RETRIEVE_ACCESS_REFRESH_TOKEN, "access_token"))
                .addHeader("uuid", UUID.randomUUID().toString())
                .addHeader("Accept", "application/json")
                .addHeader("client_id", CLIENT_ID)
                .url(String.format(MONEY_MOVEMENT_BILL_PAYMENT_URL, param))
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "ERROR: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                JSONObject data = null;

                try {
                    data = new JSONObject(json);
                    if (response.code() == SUCCESSFUL_RESPONSE_CODE) {
                        storageManager.saveFile(DATA_MONEY_MOVEMENT_RETRIEVE_DEST_SRC_ACCOUNT_BILL_PAYMENT, data.toString());
                        Log.d(TAG, DATA_MONEY_MOVEMENT_RETRIEVE_DEST_SRC_ACCOUNT_BILL_PAYMENT + "JSON DATA = " + data.toString());
                    }
                    else {
                        Log.e(TAG, "ERROR: " + DATA_MONEY_MOVEMENT_RETRIEVE_DEST_SRC_ACCOUNT_BILL_PAYMENT + data.toString());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void API_MoneyMovement_CreateBillPaymentTransfer(String sourceAccountId, String transactionAmount, String transferCurrencyIndicator
            , String payeeId, String remark) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("sourceAccountId", sourceAccountId);
            jsonObject.put("transactionAmount", transactionAmount);
            jsonObject.put("transferCurrencyIndicator", transferCurrencyIndicator);
            jsonObject.put("payeeId", payeeId);
            jsonObject.put("remark", remark);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + getStoredValues(DATA_AUTHORIZATION_RETRIEVE_ACCESS_REFRESH_TOKEN, "access_token"))
                .addHeader("uuid", UUID.randomUUID().toString())
                .addHeader("Accept", "application/json")
                .addHeader("client_id", CLIENT_ID)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString()))
                .url(MONEY_MOVEMENT_CREATE_BILL_PAYMENT_URL)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "ERROR: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                JSONObject data = null;

                try {
                    data = new JSONObject(json);
                    if (response.code() == SUCCESSFUL_RESPONSE_CODE) {
                        storageManager.saveFile(DATA_MONEY_MOVEMENT_CREATE_BILL_PAYMENT, data.toString());
                        Log.d(TAG, DATA_MONEY_MOVEMENT_CREATE_BILL_PAYMENT + "JSON DATA = " + data.toString());
                    }
                    else {
                        Log.e(TAG, "ERROR: " + DATA_MONEY_MOVEMENT_CREATE_BILL_PAYMENT + data.toString());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void API_MoneyMovement_ConfirmBillPayment(String controlFlowId) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("controlFlowId", controlFlowId);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + getStoredValues(DATA_AUTHORIZATION_RETRIEVE_ACCESS_REFRESH_TOKEN, "access_token"))
                .addHeader("uuid", UUID.randomUUID().toString())
                .addHeader("Accept", "application/json")
                .addHeader("client_id", CLIENT_ID)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString()))
                .url(MONEY_MOVEMENT_CONFIRM_BILL_PAYMENTR_URL)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "ERROR: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                JSONObject data = null;

                try {
                    data = new JSONObject(json);
                    if (response.code() == SUCCESSFUL_RESPONSE_CODE) {
                        storageManager.saveFile(DATA_MONEY_MOVEMENT_CONFIRM_BILL_PAYMENT, data.toString());
                        Log.d(TAG, DATA_MONEY_MOVEMENT_CONFIRM_BILL_PAYMENT + "JSON DATA = " + data.toString());
                    }
                    else {
                        Log.e(TAG, "ERROR: " + DATA_MONEY_MOVEMENT_CONFIRM_BILL_PAYMENT + data.toString());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // Pay with Points functions

    public void API_PayWithPoints_Enroll(String lastFourDigitsCardNumber, String citiCardHolderPhoneNumber, String merchantCustomerReferenceId) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("lastFourDigitsCardNumber", lastFourDigitsCardNumber);
            jsonObject.put("citiCardHolderPhoneNumber", citiCardHolderPhoneNumber);
            jsonObject.put("merchantCustomerReferenceId", merchantCustomerReferenceId);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + getStoredValues(DATA_AUTHORIZATION_RETRIEVE_ACCESS_REFRESH_TOKEN, "access_token"))
                .addHeader("uuid", UUID.randomUUID().toString())
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept-Language", "en-us")
                .addHeader("client_id", CLIENT_ID)
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString()))
                .url(PAY_WITH_POINTS_ENROLL_URL)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "ERROR: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                JSONObject data = null;

                try {
                    data = new JSONObject(json);
                    if (response.code() == SUCCESSFUL_RESPONSE_CODE) {
                        storageManager.saveFile(DATA_PAY_WITH_POINTS_ENROLL, data.toString());
                        Log.d(TAG, DATA_PAY_WITH_POINTS_ENROLL + "JSON DATA = " + data.toString());
                    }
                    else {
                        Log.e(TAG, "ERROR: " + DATA_PAY_WITH_POINTS_ENROLL + data.toString());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void API_PayWithPoints_TokenActivation(String rewardLinkCode, String linkageConfirmationCode) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("linkageConfirmationCode", linkageConfirmationCode);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + getStoredValues(DATA_AUTHORIZATION_RETRIEVE_ACCESS_REFRESH_TOKEN, "access_token"))
                .addHeader("uuid", UUID.randomUUID().toString())
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept-Language", "en-us")
                .addHeader("client_id", CLIENT_ID)
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString()))
                .url(String.format(PAY_WITH_POINTS_TOKEN_ACTIVATION_URL, rewardLinkCode))
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "ERROR: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                JSONObject data = null;

                try {
                    data = new JSONObject(json);
                    if (response.code() == SUCCESSFUL_RESPONSE_CODE) {
                        storageManager.saveFile(DATA_PAY_WITH_POINTS_TOKEN_ACTIVATION, data.toString());
                        Log.d(TAG, DATA_PAY_WITH_POINTS_TOKEN_ACTIVATION + "JSON DATA = " + data.toString());
                    }
                    else {
                        Log.e(TAG, "ERROR: " + DATA_PAY_WITH_POINTS_TOKEN_ACTIVATION + data.toString());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void API_PayWithPoints_RetrieveRewardBalance(String rewardLinkCode) {

        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + getStoredValues(DATA_AUTHORIZATION_RETRIEVE_ACCESS_REFRESH_TOKEN, "access_token"))
                .addHeader("uuid", UUID.randomUUID().toString())
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept-Language", "en-us")
                .addHeader("client_id", CLIENT_ID)
                .url(String.format(PAY_WITH_POINTS_RETRIEVE_REWARD_BALANCE_URL, rewardLinkCode))
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "ERROR: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                JSONObject data = null;

                try {
                    data = new JSONObject(json);
                    if (response.code() == SUCCESSFUL_RESPONSE_CODE) {
                        storageManager.saveFile(DATA_PAY_WITH_POINTS_RETRIEVE_REWARD_BALANCE, data.toString());
                        Log.d(TAG, DATA_PAY_WITH_POINTS_RETRIEVE_REWARD_BALANCE + "JSON DATA = " + data.toString());
                    }
                    else {
                        Log.e(TAG, "ERROR: " + DATA_PAY_WITH_POINTS_RETRIEVE_REWARD_BALANCE + data.toString());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void API_PayWithPoints_SubmitRedemptionRequest(String rewardLinkCode, String transactionReferenceNumber, String transactionAmount
            , String currencyCode, String pointsToRedeem, String transactionDescription) {

        JSONObject jsonObject = new JSONObject();
        JSONObject childObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            childObject.put("transactionAmount", transactionAmount);
            childObject.put("currencyCode", currencyCode);
            childObject.put("pointsToRedeem", pointsToRedeem);
            childObject.put("transactionDescription", transactionDescription);
            jsonArray.put(childObject);
            jsonObject.put("transactionReferenceNumber", transactionReferenceNumber);
            jsonObject.put("redemptionOrder", childObject);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + getStoredValues(DATA_AUTHORIZATION_RETRIEVE_ACCESS_REFRESH_TOKEN, "access_token"))
                .addHeader("uuid", UUID.randomUUID().toString())
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept-Language", "en-us")
                .addHeader("client_id", CLIENT_ID)
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString()))
                .url(String.format(PAY_WITH_POINTS_SUBMIT_REDEMPTION_REQUEST_URL, rewardLinkCode))
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "ERROR: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                JSONObject data = null;

                try {
                    data = new JSONObject(json);
                    if (response.code() == SUCCESSFUL_RESPONSE_CODE) {
                        storageManager.saveFile(DATA_PAY_WITH_POINTS_SUBMIT_REDEMPTION_REQUEST, data.toString());
                        Log.d(TAG, DATA_PAY_WITH_POINTS_SUBMIT_REDEMPTION_REQUEST + "JSON DATA = " + data.toString());
                    }
                    else {
                        Log.e(TAG, "ERROR: " + DATA_PAY_WITH_POINTS_SUBMIT_REDEMPTION_REQUEST + data.toString());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // Reference Data functions

    public void API_ReferenceData_RetrieveValidValues(String referenceCode) {
        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + getStoredValues(DATA_AUTHORIZATION_RETRIEVE_ACCESS_REFRESH_TOKEN, "access_token"))
                .addHeader("uuid", UUID.randomUUID().toString())
                .addHeader("Accept", "application/json")
                .addHeader("client_id", CLIENT_ID)
                .url(String.format(RETRIEVE_VALID_VALUES_URL, referenceCode))
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "ERROR: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                JSONObject data = null;

                try {
                    data = new JSONObject(json);
                    if (response.code() == SUCCESSFUL_RESPONSE_CODE) {
                        storageManager.saveFile(DATA_REFERENCE_DATA_RETRIEVE_VALID_VALUES, data.toString());
                        Log.d(TAG, DATA_REFERENCE_DATA_RETRIEVE_VALID_VALUES + "JSON DATA = " + data.toString());
                    }
                    else {
                        Log.e(TAG, "ERROR: " + DATA_REFERENCE_DATA_RETRIEVE_VALID_VALUES + data.toString());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
