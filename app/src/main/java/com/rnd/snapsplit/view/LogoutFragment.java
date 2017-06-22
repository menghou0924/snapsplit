package com.rnd.snapsplit.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;
import com.rnd.snapsplit.CitiAPIBase;
import com.rnd.snapsplit.CitiAPIManager;
import com.rnd.snapsplit.R;
import com.rnd.snapsplit.StorageManager;

/**
 * Created by Damian on 28/5/2017.
 */

public class LogoutFragment extends Fragment {

    private static final String TAG = "LogoutFragment";
    private CitiAPIManager citiAPIManager;
    private CitiAPIBase citiAPIBase;
    private StorageManager storageManager;
    private static final String DATA_AUTHORIZATION_RETRIEVE_ACCESS_REFRESH_TOKEN = "DATA_AUTHORIZATION_RETRIEVE_ACCESS_REFRESH_TOKEN";

    public static LogoutFragment newInstance() {
        return new LogoutFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        final Resources resources = context.getResources();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.view_logout, container, false);
        final Activity activity = getActivity();
        final Context context = getContext();

        ((Toolbar) activity.findViewById(R.id.tool_bar_hamburger)).setVisibility(View.VISIBLE);

        storageManager = new StorageManager(getContext());
        citiAPIManager = new CitiAPIManager(getContext());
        citiAPIBase = new CitiAPIBase(getContext());

        Button logoutButton = (Button) view.findViewById(R.id.btn_logout);
        assert logoutButton != null;
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLogout();
            }
        });
        Button detailsButton = (Button) view.findViewById(R.id.btn_details);
        assert detailsButton != null;
        detailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testAPI();
            }
        });
        return view;
    }

    public void startLogout() {
        citiAPIManager.logout();
        String[] xmlFriends = getResources().getStringArray(R.array.friends);
        String[] xmlProfile = getResources().getStringArray(R.array.profile);

        for (String file: xmlFriends) {
            storageManager.clearFile(file);
        }
        for (String file: xmlProfile) {
            storageManager.clearFile(file);
        }
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
    }

    public void testAPI() {
        storageManager.clearFile("history");
//        FirebaseDatabase.getInstance().getReference().setValue(null);
        FirebaseDatabase.getInstance().getReference().child("5139 6515").child("accountBalance").setValue("43678.9");
        FirebaseDatabase.getInstance().getReference().child("5660 0981").child("accountBalance").setValue("17893.6");
        FirebaseDatabase.getInstance().getReference().child("5139 6515").child("history").setValue(null);
        FirebaseDatabase.getInstance().getReference().child("5660 0981").child("history").setValue(null);


        // Define what to get from citi api
//        citiAPIBase.API_Accounts_RetrieveAccountsSummary();
//        citiAPIBase.API_MoneyMovement_RetrieveDestSrcAcct("ALL", "");
//        citiAPIBase.API_MoneyMovement_RetrievePayeeList("ALL", "");
//        citiAPIBase.API_MoneyMovement_RetrieveDestSrcAcctPersonal();
//        citiAPIBase.API_MoneyMovement_RetrieveDestSrcAcctInternal("");
//        citiAPIBase.API_MoneyMovement_RetrieveDestSrcAcctExternal("");
//        citiAPIBase.API_MoneyMovement_RetrieveDestSrcAcctBillPayment("");
//        (new CitiAPIBase(getContext())).API_MoneyMovement_CreateInternalTransfer(
//                "3739334c4d3463614356474f6d7650667a737656664652677747796855646c5552745a43346d37423653553d"
//                , "123.4", "SOURCE_ACCOUNT_CURRENCY"
//                , "51327a46437565374770547776786c4348367545397331453164414177505a4e6d2b7131566d39476942303d"
//                , "BENEFICIARY", "123456", "test", "test");
        // Define what to send to email
//        String[] xmlAuthorization = getResources().getStringArray(R.array.citi_authorization);
//        String[] xmlAccounts = getResources().getStringArray(R.array.citi_accounts);
//        String[] xmlCustomers = getResources().getStringArray(R.array.citi_customers);
//        String[] xmlMovement = getResources().getStringArray(R.array.citi_moneymovement);
//        String[] xmlPayWithPoints = getResources().getStringArray(R.array.citi_paywithpoints);
//        String[] xmlReference = getResources().getStringArray(R.array.citi_reference);

//        String emailBody = "";
//        for (String file : xmlMovement) {
//            emailBody += file + "\n";
//            emailBody += storageManager.getFileLastModifiedDate(file) + "\n";
//            emailBody += storageManager.getFile(file);
//            emailBody += "\n\n\n";
//        }
//        for (String file : xmlAccounts) {
//            emailBody += file + "\n";
//            emailBody += storageManager.getFileLastModifiedDate(file) + "\n";
//            emailBody += storageManager.getFile(file);
//            emailBody += "\n\n\n";
//        }
////
//        emailBody += "getAccountDetails" + "\n";
//        emailBody += storageManager.getFile("DATA_ACCOUNTS_RETRIEVE_ACCOUNT_DETAILS") + "\n";
//        emailBody += "\n\n\n";
//        Intent i = new Intent(Intent.ACTION_SEND);
//        i.setType("message/rfc822");
//        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"menghou0924@gmail.com"});
//        i.putExtra(Intent.EXTRA_SUBJECT, "Debug Snapsplit");
//        i.putExtra(Intent.EXTRA_TEXT   , emailBody);
//        try {
//            getActivity().startActivity(Intent.createChooser(i, "Send mail..."));
//        } catch (android.content.ActivityNotFoundException ex) {
//            Toast.makeText(getContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
//        }
    }
}