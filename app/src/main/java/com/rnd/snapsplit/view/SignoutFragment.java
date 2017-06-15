package com.rnd.snapsplit.view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.rnd.snapsplit.CitiAPIBase;
import com.rnd.snapsplit.R;
import com.rnd.snapsplit.StorageManager;

/**
 * Created by Damian on 28/5/2017.
 */

public class SignoutFragment extends Fragment {

    private static final String TAG = "SignoutFragment";
    private CitiAPIBase citiApiManagerBase;
    private StorageManager storageManager;
    private static final String DATA_AUTHORIZATION_RETRIEVE_ACCESS_REFRESH_TOKEN = "DATA_AUTHORIZATION_RETRIEVE_ACCESS_REFRESH_TOKEN";

    public static SignoutFragment newInstance() {
        return new SignoutFragment();
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
        citiApiManagerBase = new CitiAPIBase(getContext());

        Button logoutButton = (Button) view.findViewById(R.id.btn_logout);
        assert logoutButton != null;
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLogout();
            }
        });

        return view;
    }

    public void startLogout() {
        storageManager.clearFile(DATA_AUTHORIZATION_RETRIEVE_ACCESS_REFRESH_TOKEN);

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
    }

}