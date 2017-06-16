package com.rnd.snapsplit.view;

import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.res.Resources;
import android.app.Activity;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import com.rnd.snapsplit.Friend;
import com.rnd.snapsplit.FriendListAdapter;
import com.rnd.snapsplit.R;
import com.rnd.snapsplit.PaymentRequest;

import java.util.ArrayList;

/**
 * Created by Damian on 28/5/2017.
 */

public class ConfirmationFragment extends ListFragment {

    private static final String TAG = "ConfirmationFragment";
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseAnalytics mFirebaseAnalytics;
    private View view;
    private String description ="";

    ArrayList<Friend> selectedFriends = new ArrayList<Friend>();

    public static ConfirmationFragment newInstance() {
        return new ConfirmationFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        final Resources resources = context.getResources();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.view_confirmation, container, false);
        final Activity activity = getActivity();
        final Context context = getContext();

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            selectedFriends = (ArrayList<Friend>) bundle.getSerializable("selectedFriends");
        }

        ((Toolbar) activity.findViewById(R.id.tool_bar_hamburger)).setVisibility(View.INVISIBLE);
        ((Toolbar) view.findViewById(R.id.tool_bar_process)).setVisibility(View.VISIBLE);
        ((TextView) view.findViewById(R.id.text_title)).setText(R.string.confirm);

        View tb = (View) view.findViewById(R.id.tool_bar_process);
        ImageButton frontButton = (ImageButton) tb.findViewById(R.id.btn_next);
        frontButton.setVisibility(View.INVISIBLE);

        description = (String)bundle.getSerializable("description");
        TextView desc = (TextView) view.findViewById(R.id.text_summary_shop);
        desc.setText(description);

        TextView each = (TextView) view.findViewById(R.id.text_each_price);
        each.setText(String.format("HKD%.2f", bundle.getSerializable("each")));
        TextView selectedNo = (TextView) view.findViewById(R.id.text_selected_friends_no);
        selectedNo.setText(String.valueOf(selectedFriends.size()));
        TextView total = (TextView) view.findViewById(R.id.text_summary_amount);
        total.setText(String.format("HKD%.2f", bundle.getSerializable("total")));

        final ImageButton backButton = (ImageButton) view.findViewById(R.id.btn_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Toolbar) activity.findViewById(R.id.tool_bar_hamburger)).setVisibility(View.INVISIBLE);
                getFragmentManager().popBackStack();
            }
        });

        final ImageButton forwardButton = (ImageButton) view.findViewById(R.id.btn_next);
        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Toolbar) activity.findViewById(R.id.tool_bar_hamburger)).setVisibility(View.INVISIBLE);
            }
        });

        final Button sendButton = (Button) view.findViewById(R.id.button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequestsToFirebase();
            }
        });

        this.setListAdapter(new FriendListAdapter(context, R.layout.list_confirmation, selectedFriends, TAG));

        return view;
    }

    protected void sendRequestsToFirebase() {
        if (selectedFriends != null) {
            for (int i = 0; i < selectedFriends.size(); i++) {
                Friend fd = selectedFriends.get(i); // .timestampNow.put("timestamp", ServerValue.TIMESTAMP);
                PaymentRequest pr = new PaymentRequest();
                pr.setReceipientPhoneNo(fd.getPhoneNumber());
                pr.setReceiptPicture(null);
                pr.setRequestEpochDate(String.valueOf(System.currentTimeMillis()*(-1)));
                pr.setRequestorName("Damian Dutkiewicz");
                pr.setRequestorPhoneNumber("7849 8484");
                pr.setShareAmount(fd.getAmountToPay());
                pr.setDesription(description);
                mFirebaseDatabaseReference.child(fd.getPhoneNumber()).push().setValue(pr);
            }

            RelativeLayout rl1 = (RelativeLayout) view.findViewById(R.id.relative_summary);
            RelativeLayout rl2 = (RelativeLayout) view.findViewById(R.id.relative_fd_list);
            Button sendButton = (Button) view.findViewById(R.id.button);
            RelativeLayout requestView = (RelativeLayout) view.findViewById(R.id.relative_request_sent);

            rl1.setVisibility(View.INVISIBLE);
            rl2.setVisibility(View.INVISIBLE);
            sendButton.setVisibility(View.INVISIBLE);
            requestView.setVisibility(View.VISIBLE);
            (view.findViewById(R.id.btn_back)).setVisibility(View.INVISIBLE);
            (view.findViewById(R.id.tool_bar_process)).setVisibility(View.INVISIBLE);
            (getActivity().findViewById(R.id.tool_bar_hamburger)).setVisibility(View.VISIBLE);
            (getActivity().findViewById(R.id.tool_bar_hamburger)).setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
            getActivity().setTitle(getResources().getString(R.string.app_name));
            mFirebaseAnalytics.logEvent("message_sent", null);

        }
    }
}