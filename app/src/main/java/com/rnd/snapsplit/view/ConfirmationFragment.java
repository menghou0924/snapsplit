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
import com.rnd.snapsplit.PaymentRequest;
import com.rnd.snapsplit.R;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.confirmation_view, container, false);
        final Activity activity = getActivity();
        final Context context = getContext();

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            selectedFriends = (ArrayList<Friend>) bundle.getSerializable("selectedFriends");
        }

        TextView title = (TextView) view.findViewById(R.id.text_title);
        title.setText(R.string.confirm);

        description = (String)bundle.getSerializable("description");
        TextView desc = (TextView) view.findViewById(R.id.text_summary_shop);
        desc.setText(description);

        TextView each = (TextView) view.findViewById(R.id.text_each_price);
        each.setText("HKD"+ String.format("%.2f",bundle.getSerializable("each")));
        TextView selectedNo = (TextView) view.findViewById(R.id.text_selected_friends_no);
        selectedNo.setText(String.valueOf(selectedFriends.size()));
        TextView total = (TextView) view.findViewById(R.id.text_summary_amount);
        total.setText("HKD" + String.format("%.2f", (Float) bundle.getSerializable("total")));

        View tb = (View) view.findViewById(R.id.tool_bar);
        ImageButton frontButton = (ImageButton) tb.findViewById(R.id.btn_next);
        frontButton.setVisibility(View.INVISIBLE);


        final ImageButton backButton = (ImageButton) view.findViewById(R.id.btn_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

        final Button sendButton = (Button) view.findViewById(R.id.button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequestsToFirebase();
            }
        });

        this.setListAdapter(new FriendListAdapter(context, R.layout.confirmation_list, selectedFriends));

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
            RelativeLayout rl2 = (RelativeLayout) view.findViewById(R.id.relative_fdList);
            Button sendButton = (Button) view.findViewById(R.id.button);
            RelativeLayout requestView = (RelativeLayout) view.findViewById(R.id.request_sent);

            rl1.setVisibility(View.INVISIBLE);
            rl2.setVisibility(View.INVISIBLE);
            sendButton.setVisibility(View.INVISIBLE);
            requestView.setVisibility(View.VISIBLE);
            mFirebaseAnalytics.logEvent("message_sent", null);

        }
    }
}
