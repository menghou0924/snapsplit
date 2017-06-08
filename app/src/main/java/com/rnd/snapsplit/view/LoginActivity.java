package com.rnd.snapsplit.view;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;

import com.rnd.snapsplit.CitiAPIBase;
import com.rnd.snapsplit.R;
import com.rnd.snapsplit.StorageManager;


/**
 * Created by menghou0924 on 30/5/2017.
 */

public class LoginActivity extends Activity {

    private static final String TAG = "LoginActivity";
    private static final String DATA_AUTHORIZATION_RETRIEVE_ACCESS_REFRESH_TOKEN = "DATA_AUTHORIZATION_RETRIEVE_ACCESS_REFRESH_TOKEN";
    private static final int REQUEST_INTERNET_PERMISSION = 200;
    private CitiAPIBase citiApiManagerBase;
    private StorageManager storageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storageManager = new StorageManager(this);
        citiApiManagerBase = new CitiAPIBase(this);

        storageManager.clearFile(DATA_AUTHORIZATION_RETRIEVE_ACCESS_REFRESH_TOKEN);
        citiApiManagerBase.API_Authorization_RefreshAccessToken();
        if (storageManager.isFileEmpty(DATA_AUTHORIZATION_RETRIEVE_ACCESS_REFRESH_TOKEN)) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        setContentView(R.layout.login_view);

        Button loginButton = (Button) findViewById(R.id.btn_login);
        assert loginButton != null;
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSignIn();
            }
        });

        Button detailsButton = (Button) findViewById(R.id.btn_details);
        assert detailsButton != null;
        detailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDetails();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getIntent() != null && getIntent().getAction().equals(Intent.ACTION_VIEW)) {
            Uri uri = getIntent().getData();
            citiApiManagerBase.API_Authorization_RetrieveAccessToken(uri);
        }
    }

    public void startSignIn() {
        if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.INTERNET}, REQUEST_INTERNET_PERMISSION);
            return;
        }
        Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(citiApiManagerBase.getAuthURL()));
        startActivity(intent1);
    }

    public void getDetails() {
//        citiApiManagerBase.API_Accounts_RetrieveAccountsSummary();
//        citiApiManagerBase.API_Accounts_RetrieveAccountDetails("416b79676b52556f53465a61722b4a68647a6f677273346443794a503441444669316e3163452b767976343d");
//        citiApiManagerBase.API_Accounts_RetrieveAccountTransactions("355a515030616a53576b6a65797359506a634175764a734a3238314e4668627349486a676f7449463949453d", "", "", "", "", "", "", "");
//        citiApiManagerBase.API_Customers_RetrieveCustomerBasicName();
//        citiApiManagerBase.API_MoneyMovement_RetrieveDestSrcAcct("ALL", "");
//        citiApiManagerBase.API_MoneyMovement_RetrievePayeeList("ALL", "");
//        citiApiManagerBase.API_MoneyMovement_RetrieveDestSrcAcctPersonal();
//        citiApiManagerBase.API_MoneyMovement_CreatePersonalTransfer("355a515030616a53576b6a65797359506a634175764a734a3238314e4668627349486a676f7449463949453d"
//                , "3000", "SOURCE_ACCOUNT_CURRENCY", "57706472614c786a31716f5855743050597473703259494179505959776a377370614b364167516a57336b3d", "BENEFICIARY", "123456", "remark");
//        citiApiManagerBase.API_MoneyMovement_ConfirmPersonalTransfer("584b303659755a337a6238544b776d696c526d435a68695774574f4f48464f47526d4d59373337507234493d");
//        citiApiManagerBase.API_MoneyMovement_RetrieveDestSrcAcctInternal("");
//        citiApiManagerBase.API_MoneyMovement_CreateInternalTransfer("355a515030616a53576b6a65797359506a634175764a734a3238314e4668627349486a676f7449463949453d"
//                , "3000", "SOURCE_ACCOUNT_CURRENCY", "7977557255484c7345546c4e53424766634b6c53756841672b556857626e395253334b70416449676b42673d", "BENEFICIARY", "123456", "remark", "MEDICAL_SERVICES");
//        citiApiManagerBase.API_MoneyMovement_ConfirmInternalTransfer("45534b7438634c567a566777354c5861486d59616c4665467a624e61724c73574b4c50494f386664306d6f3d");
//        citiApiManagerBase.API_MoneyMovement_RetrieveDestSrcAcctExternal("");
//        citiApiManagerBase.API_MoneyMovement_CreateExternalTransfer("41375159436b366b32335a6b566d53315753684d2b69464f43427347654b496e2f6a4f6d4971546e622f773d"
//                , "100", "SOURCE_ACCOUNT_CURRENCY", "3970304a4d2b432f453739362b46716630304b756256464869626b4c615565493243442f6f734438534f673d", "BENEFICIARY", "GIRO", "", "Fund Transfer", "CREDIT_CARD_PAYMENT");
//        citiApiManagerBase.API_MoneyMovement_ConfirmExternalTransfer("594163764f5137486431484179637a43794d5864586761374a7742663459597657496662676a43456148773d");
//        citiApiManagerBase.API_MoneyMovement_RetrieveDestSrcAcctBillPayment("");
//        citiApiManagerBase.API_MoneyMovement_CreateBillPaymentTransfer("41375159436b366b32335a6b566d53315753684d2b69464f43427347654b496e2f6a4f6d4971546e622f773d"
//                , "3000", "SOURCE_ACCOUNT_CURRENCY", "C$0000172391$SG$XX$02342389812313", "remark");
//        citiApiManagerBase.API_MoneyMovement_ConfirmBillPayment("45374f54526c53674e6b6f777a464c57396e6b626b696d4868594a4a503377737a384b6f445350717643633d");
//        citiApiManagerBase.API_ReferenceData_RetrieveValidValues("paymentType");
    }

}
