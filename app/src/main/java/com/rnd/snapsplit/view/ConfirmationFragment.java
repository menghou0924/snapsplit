package com.rnd.snapsplit.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.res.Resources;
import android.app.Activity;
import android.widget.ImageButton;
import android.widget.TextView;

import com.rnd.snapsplit.Friend;
import com.rnd.snapsplit.FriendListAdapter;
import com.rnd.snapsplit.R;

import java.util.ArrayList;

/**
 * Created by Damian on 28/5/2017.
 */

public class ConfirmationFragment extends ListFragment {

    private static final String TAG = "ConfirmationFragment";

    ArrayList<Friend> selectedFriends = new ArrayList<Friend>();

    public static ConfirmationFragment newInstance() {
        return new ConfirmationFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        final Resources resources = context.getResources();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.confirmation_view, container, false);
        final Activity activity = getActivity();
        final Context context = getContext();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            selectedFriends = (ArrayList<Friend>) bundle.getSerializable("selectedFriends");
        }

        TextView title = (TextView) view.findViewById(R.id.text_title);
        title.setText(R.string.confirm);

        TextView each = (TextView) view.findViewById(R.id.text_each_price);
        each.setText(String.format("HKD%.2f",bundle.getSerializable("each")));
        TextView selectedNo = (TextView) view.findViewById(R.id.text_selected_friends_no);
        selectedNo.setText(String.valueOf(selectedFriends.size()));
        TextView total = (TextView) view.findViewById(R.id.text_summary_amount);
        total.setText(String.format("HKD%.2f",bundle.getSerializable("total")));


        final ImageButton backButton = (ImageButton) view.findViewById(R.id.btn_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

        this.setListAdapter(new FriendListAdapter(context, R.layout.confirmation_list, selectedFriends));

        return view;
    }
}
