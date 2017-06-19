package com.rnd.snapsplit.view;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.ivbaranov.mli.MaterialLetterIcon;
import com.rnd.snapsplit.CitiAPIManager;
import com.rnd.snapsplit.Profile;
import com.rnd.snapsplit.R;

import java.util.Random;

/**
 * Created by menghou0924 on 11/6/2017.
 */

public class MyAccountFragment extends Fragment {

    private static final String TAG = "MyAccountFragment";
    private Profile profile;
    private int[] mMaterialColors;
    private CitiAPIManager citiAPIManager;
    private static final Random RANDOM = new Random();

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

        profile = new Profile(context);
        citiAPIManager = new CitiAPIManager(context);

        this.mMaterialColors = context.getResources().getIntArray(R.array.colors_icon);

        ((TextView) view.findViewById(R.id.txt_name)).setText(profile.getName());
        ((TextView) view.findViewById(R.id.txt_phone)).setText(profile.getPhoneNumber());
        ((TextView) view.findViewById(R.id.txt_account_no)).setText(profile.getDisplayAccountNumber());
        ((TextView) view.findViewById(R.id.txt_balance_value)).setText(citiAPIManager.getAccountBalance());

        ((MaterialLetterIcon) view.findViewById(R.id.text_icon)).setInitials(true);
        ((MaterialLetterIcon) view.findViewById(R.id.text_icon)).setInitialsNumber(2);
        ((MaterialLetterIcon) view.findViewById(R.id.text_icon)).setLetterSize(18);
        ((MaterialLetterIcon) view.findViewById(R.id.text_icon)).setShapeColor(profile.getDisplayColor());
        ((MaterialLetterIcon) view.findViewById(R.id.text_icon)).setLetter(profile.getName());

        if (profile.getDisplayPic() == 0) {
            ((MaterialLetterIcon) view.findViewById(R.id.text_icon)).setVisibility(View.VISIBLE);
            ((ImageView) view.findViewById(R.id.image_icon)).setVisibility(View.INVISIBLE);
        }
        else {
            ((MaterialLetterIcon) view.findViewById(R.id.text_icon)).setVisibility(View.INVISIBLE);
            ((ImageView) view.findViewById(R.id.image_icon)).setVisibility(View.VISIBLE);
            ((ImageView) view.findViewById(R.id.image_icon)).setImageResource(profile.getDisplayPic());
        }

        return view;
    }
}
