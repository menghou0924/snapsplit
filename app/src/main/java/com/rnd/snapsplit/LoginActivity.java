package com.rnd.snapsplit;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


/**
 * Created by menghou0924 on 30/5/2017.
 */

public class LoginActivity extends Activity {

    private static final String TAG = "LoginActivity";
    private Button loginButton;
    private Button detailsButton;
    private ImageView logoImage;
    private CitiAPIBase citiApiManagerBase;
    private static final int REQUEST_INTERNET_PERMISSION = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_view);

        citiApiManagerBase = new CitiAPIBase(this);

        loginButton = (Button) findViewById(R.id.btn_login);
        assert loginButton != null;
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSignIn();
            }
        });

        detailsButton = (Button) findViewById(R.id.btn_details);
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

        if(getIntent() != null && getIntent().getAction().equals(Intent.ACTION_VIEW)) {
            Uri uri = getIntent().getData();
            citiApiManagerBase.API_Authorization_RetrieveAccessToken(uri);
        }
    }

    public void startSignIn() {
        if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.INTERNET}, REQUEST_INTERNET_PERMISSION);
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(citiApiManagerBase.getAuthURL()));
        startActivity(intent);
    }

    public void getDetails() {
//        citiApiManagerBase.API_Accounts_RetrieveAccountsSummary();
//        citiApiManagerBase.API_Accounts_RetrieveAccountDetails("416b79676b52556f53465a61722b4a68647a6f677273346443794a503441444669316e3163452b767976343d");
//        citiApiManagerBase.API_Accounts_RetrieveAccountTransactions("355a515030616a53576b6a65797359506a634175764a734a3238314e4668627349486a676f7449463949453d", "", "", "", "", "", "", "");
//        citiApiManagerBase.API_Customers_RetrieveCustomerBasicName();
//        citiApiManagerBase.API_MoneyMovement_RetrieveDestSrcAcct("ALL", "");
//        citiApiManagerBase.API_MoneyMovement_RetrievePayeeList("ALL", "");
//        citiApiManagerBase.API_MoneyMovement_RetrieveDestSrcAcctPersonal();
//        citiApiManagerBase.API_MoneyMovement_CreatePersonalTransfer ("355a515030616a53576b6a65797359506a634175764a734a3238314e4668627349486a676f7449463949453d"
//                , "3000", "SOURCE_ACCOUNT_CURRENCY", "57706472614c786a31716f5855743050597473703259494179505959776a377370614b364167516a57336b3d", "BENEFICIARY", "123456", "1");
//        citiApiManagerBase.API_MoneyMovement_ConfirmPersonalTransfer("584b303659755a337a6238544b776d696c526d435a68695774574f4f48464f47526d4d59373337507234493d");
//        citiApiManagerBase.API_MoneyMovement_RetrieveDestSrcAcctInternal("");
//        citiApiManagerBase.API_MoneyMovement_CreateInternalTransfer ("355a515030616a53576b6a65797359506a634175764a734a3238314e4668627349486a676f7449463949453d"
//                , "3000", "SOURCE_ACCOUNT_CURRENCY", "7977557255484c7345546c4e53424766634b6c53756841672b556857626e395253334b70416449676b42673d", "BENEFICIARY", "123456", "1", "MEDICAL_SERVICES");
//        citiApiManagerBase.API_MoneyMovement_ConfirmInternalTransfer("45534b7438634c567a566777354c5861486d59616c4665467a624e61724c73574b4c50494f386664306d6f3d");
//        citiApiManagerBase.API_MoneyMovement_CreateInternalTransfer();
//        citiApiManagerBase.API_MoneyMovement_ConfirmInternalTransfer();
//        citiApiManagerBase.API_MoneyMovement_RetrieveDestSrcAcctExternal("");
//                citiApiManagerBase.API_MoneyMovement_CreateInternalTransfer();
//        citiApiManagerBase.API_MoneyMovement_RetrieveDestSrcAcctBillPayment("");

//        citiApiManagerBase.API_MoneyMovement_ConfirmInternalTransfer();
//        citiApiManagerBase.API_ReferenceData_RetrieveValidValues("paymentType");
    }

}
