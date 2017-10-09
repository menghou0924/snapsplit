package com.rnd.snapsplit.view;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.ivbaranov.mli.MaterialLetterIcon;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.vision.text.Text;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rnd.snapsplit.CitiAPIManager;
import com.rnd.snapsplit.Friend;
import com.rnd.snapsplit.History;
import com.rnd.snapsplit.PaymentRequest;
import com.rnd.snapsplit.Profile;
import com.rnd.snapsplit.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by menghou0924 on 11/6/2017.
 */

public class MyAccountFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MyAccountFragment";
    private Profile profile;
    private int[] mMaterialColors;
    private static final Random RANDOM = new Random();
    private DatabaseReference mFirebaseDatabaseReference;
    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private FirebaseRecyclerAdapter<History, HistoryViewHolder> mFirebaseAdapter;
    private ProgressBar mProgressBar;
    private SharedPreferences mSharedPreferences;

    public static MyAccountFragment newInstance() {
        return new MyAccountFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        final Resources resources = context.getResources();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.view_my_account, container, false);
        final Activity activity = getActivity();
        final Context context = getContext();
        ((Toolbar) activity.findViewById(R.id.tool_bar_hamburger)).setVisibility(View.VISIBLE);

        profile = new Profile(context);

        // settting personal account details
        this.mMaterialColors = context.getResources().getIntArray(R.array.colors_icon);

        ((TextView) view.findViewById(R.id.txt_name)).setText(profile.getName());
        ((TextView) view.findViewById(R.id.txt_phone)).setText(profile.getPhoneNumber());
        ((TextView) view.findViewById(R.id.txt_account_no)).setText(profile.getDisplayAccountNumber());
        ((TextView) view.findViewById(R.id.txt_balance_value)).setText(profile.getAccountBalance());

        if (profile.getName().equals("Raymond Sak")) {
            ((TextView) ((View) view.findViewById(R.id.list_transacton)).findViewById(R.id.txt_description)).setText("You Paid Damian $26.3 for Starbucks");
        }
        else if (profile.getName().equals("Damian Dutkiewicz")) {
            ((TextView) ((View) view.findViewById(R.id.list_transacton)).findViewById(R.id.txt_description)).setText("You Received $26.3 from Raymond for Starbucks");
        }
        ((View) view.findViewById(R.id.list_transacton)).setVisibility(View.INVISIBLE);

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

        // setting transaction history
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        mMessageRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_history);
        mLinearLayoutManager = new LinearLayoutManager(getContext());

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference().child(profile.getPhoneNumber()).child("history");
        mFirebaseAdapter = new FirebaseRecyclerAdapter<History, HistoryViewHolder>(
                History.class,
                R.layout.list_transaction,
                HistoryViewHolder.class,
                mFirebaseDatabaseReference.orderByChild("transactionDate")) {

            @Override
            protected History parseSnapshot(DataSnapshot snapshot) {
                History history = super.parseSnapshot(snapshot);
                if (history != null) {
                    history.setId(snapshot.getKey());
                    return history;
                }
                return null;
            }

            @Override
            protected void populateViewHolder(HistoryViewHolder viewHolder,
                                              History history, int position) {
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                if (history != null) {
                    String historyType = history.getType();
                    String description = "";

                    viewHolder.id = history.getId();

                    if (history.getStrReceiptPic() != null && !history.getStrReceiptPic().equals("")) {
                        String encodedReceipt = history.getStrReceiptPic();
                        byte[] encodeByte = Base64.decode(encodedReceipt, Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                        viewHolder.receiptIcon.setImageBitmap(bitmap);
                    }

                    if (historyType.equals(History.HISTORY_TYPE_PAYMENT_MADE)) {
//                        viewHolder.item.setBackground(ContextCompat.getDrawable(context, R.drawable.border_primary_color));
                        description = "You sent " + history.getRequestorName() + " "
                                + Float.toString(history.getShareAmount()) + " for " + history.getDescription();
                    }
                    else if (historyType.equals(History.HISTORY_TYPE_PAYMENT_RECEIVED)) {
//                        viewHolder.item.setBackground(ContextCompat.getDrawable(context, R.drawable.border_accent_color));
                        description = "You received $" + Float.toString(history.getShareAmount()) + " from "
                                + history.getReceipientName()
                                + " for " + history.getDescription();
                    }
                    viewHolder.description.setText(description);

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy' 'HH:mm:ss");
                    String date = null;
                    Date temp = new Date(Long.parseLong(history.getTransactionDate()) * (-1));
                    date = simpleDateFormat.format(temp);
                    viewHolder.date.setText(date);
                } else {
                    ViewGroup.LayoutParams params = viewHolder.item.getLayoutParams();
                    params.height = 0;
                    viewHolder.item.setLayoutParams(params);
                }
            }

            @Override
            public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                HistoryViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);
                viewHolder.setOnClickListener(new HistoryViewHolder.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position, String id, PaymentRequest pr) {
                        //Toast.makeText(getActivity(), "Item clicked at " + position, Toast.LENGTH_SHORT).show();
                    }

                });
                return viewHolder;
            }
        };

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the user is at the bottom of the list, scroll
                // to the bottom of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) && lastVisiblePosition == (positionStart - 1))) {
                    mMessageRecyclerView.scrollToPosition(positionStart);
                }
            }
        });

        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        return view;
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        View item;
        String id;
        TextView date;
        TextView description;
        CircleImageView receiptIcon;
        private ClickListener mClickListener;

        public HistoryViewHolder(View v) {
            super(v);
            item = itemView;
            description = (TextView) itemView.findViewById(R.id.txt_description);
            date = (TextView) itemView.findViewById(R.id.txt_date);
            receiptIcon = (CircleImageView) itemView.findViewById(R.id.receiptIcon);
        }

        public interface ClickListener {
            public void onItemClick(View view, int position, String id, PaymentRequest pr);
            //public void onLongItemClick(View view, int position, String id, PaymentRequest pr);
        }

        public void setOnClickListener(HistoryViewHolder.ClickListener clickListener) {
            mClickListener = clickListener;
        }
    }
}
