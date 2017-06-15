package com.rnd.snapsplit.view;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rnd.snapsplit.R;

/**
 * Created by menghou0924 on 11/6/2017.
 */

public class MyAccountFragment extends Fragment {

    private static final String TAG = "MyAccountFragment";

    public static MyAccountFragment newInstance() {
        return new MyAccountFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        final Resources resources = context.getResources();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.view_my_account, container, false);
        final Activity activity = getActivity();
        final Context context = getContext();
        ((Toolbar) activity.findViewById(R.id.tool_bar_hamburger)).setVisibility(View.VISIBLE);

        return view;

    }
}
