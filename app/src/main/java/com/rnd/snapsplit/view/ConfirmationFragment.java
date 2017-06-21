package com.rnd.snapsplit.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
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
import com.rnd.snapsplit.History;
import com.rnd.snapsplit.Profile;
import com.rnd.snapsplit.R;
import com.rnd.snapsplit.PaymentRequest;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Damian on 28/5/2017.
 */

public class ConfirmationFragment extends ListFragment {

    private static final String TAG = "ConfirmationFragment";
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseAnalytics mFirebaseAnalytics;
    private View view;
    private String description ="";
    private Bitmap bm;
    private Profile profile;

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
        profile = new Profile(context);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            selectedFriends = (ArrayList<Friend>) bundle.getSerializable("selectedFriends");
            String picPath = bundle.getString("receiptPicture");
            bm = BitmapFactory.decodeFile(picPath);

            CircleImageView receipt = (CircleImageView) view.findViewById(R.id.receipt_image);
            receipt.setImageBitmap(bm);
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

        TextView myShareVal = (TextView) view.findViewById(R.id.text_myShare_value);
        myShareVal.setText(bundle.getString("myShare"));
        TextView selectedNo = (TextView) view.findViewById(R.id.text_selected_friends_no);
        selectedNo.setText(String.valueOf(selectedFriends.size()));
        TextView total = (TextView) view.findViewById(R.id.text_summary_amount_value);
        total.setText(String.format("%.2f", bundle.getSerializable("total")));

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
        String receiptStr = getStrFromReceiptBitmap(bm);
        Bundle bundle = this.getArguments();
        if (selectedFriends != null) {
            for (int i = 0; i < selectedFriends.size(); i++) {
                Friend fd = selectedFriends.get(i); // .timestampNow.put("timestamp", ServerValue.TIMESTAMP);
                PaymentRequest pr = new PaymentRequest();
                pr.setReceipientPhoneNo(fd.getPhoneNumber());
                pr.setStrReceiptPic(receiptStr);
                pr.setRequestEpochDate(String.valueOf(System.currentTimeMillis()*(-1)));
                pr.setRequestorName(profile.getName());
                pr.setRequestorPhoneNumber(profile.getPhoneNumber());
                pr.setShareAmount(fd.getAmountToPay());
                pr.setTotalAmount(bundle.getFloat("total"));
                pr.setDesription(description);
                mFirebaseDatabaseReference.child(fd.getPhoneNumber()).child("unpaid_by_me").push().setValue(pr);
                mFirebaseDatabaseReference.child(profile.getPhoneNumber()).child("unpaid_by_friends").push().setValue(pr);
            }

            PaymentRequest pr = new PaymentRequest();
            pr.setStrReceiptPic(receiptStr);
            pr.setRequestEpochDate(String.valueOf(System.currentTimeMillis()*(-1)));
            pr.setRequestorName(profile.getName());
            pr.setRequestorPhoneNumber(profile.getPhoneNumber());
            pr.setTotalAmount(bundle.getFloat("total"));
            pr.setDesription(description);
            (new History(getContext())).setRequestHistory(selectedFriends, pr);

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

    public static String getStrFromReceiptBitmap(Bitmap bmp){
        ByteArrayOutputStream bYtE = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, bYtE);
        bmp.recycle();
        byte[] byteArray = bYtE.toByteArray();
        String imageFile = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return imageFile;
    }
}