package com.rnd.snapsplit.view;

/**
 * Copyright Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rnd.snapsplit.DialogClickListener;
import com.rnd.snapsplit.Friend;
import com.rnd.snapsplit.PaymentRequest;
import com.rnd.snapsplit.Profile;
import com.rnd.snapsplit.R;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Damian on 9/6/2017.
 */

public class SentRequestFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = "OwedFragment";
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<PaymentRequest, MessageViewHolder> mFirebaseAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView mMessageRecyclerView;
    private View view;
    private ProgressBar mProgressBar;
    private Fragment mFragment;
    private Activity activity;
    private Profile profile;

    // fingerprint vars

    private static final String DIALOG_FRAGMENT_TAG = "myFragment";
    private static final String SECRET_MESSAGE = "Very secret message";
    private static final String KEY_NAME_NOT_INVALIDATED = "key_not_invalidated";
    static final String DEFAULT_KEY_NAME = "default_key";
    private Cipher mCipher;

    private KeyStore mKeyStore;
    private KeyGenerator mKeyGenerator;
    private SharedPreferences mSharedPreferences;

    public static OwedFragment newInstance() {
        return new OwedFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mFragment = (Fragment) this;
        final Resources resources = context.getResources();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        View item;
        TextView description;
        TextView toPerson;
        TextView splitAmount;
        TextView date;
        TextView shareText;
        CircleImageView receiptIcon;
        String id;
        PaymentRequest pr;

        public MessageViewHolder(View v) {
            super(v);
            item = itemView;
            description = (TextView) itemView.findViewById(R.id.description);
            toPerson = (TextView) itemView.findViewById(R.id.txt_person);
            splitAmount = (TextView) itemView.findViewById(R.id.splitAmount);
            date = (TextView) itemView.findViewById(R.id.date);
            receiptIcon = (CircleImageView) itemView.findViewById(R.id.receiptIcon);
            shareText = (TextView) itemView.findViewById((R.id.txt_share));

            shareText.setText("Requested Amount:");

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickListener.onItemClick(v, getAdapterPosition(), id, pr);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    mLongClickListener.onLongClick(v, getAdapterPosition(), id, pr);
                    return true;
                }
            });
        }

        private MessageViewHolder.ClickListener mClickListener;
        private MessageViewHolder.LongClickListener mLongClickListener;

        //Interface to send callbacks...
        public interface ClickListener {
            public void onItemClick(View view, int position, String id, PaymentRequest pr);
            //public void onLongItemClick(View view, int position, String id, PaymentRequest pr);
        }

        public interface LongClickListener {
            //public void onItemClick(View view, int position, String id, PaymentRequest pr);
            public void onLongClick(View view, int position, String id, PaymentRequest pr);
        }

        public void setOnClickListener(MessageViewHolder.ClickListener clickListener) {
            mClickListener = clickListener;
        }

        public void setOnLongClickListener(MessageViewHolder.LongClickListener clickListener) {
            mLongClickListener = clickListener;
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.activity_sent_request, container, false);
        activity = getActivity();
        profile = new Profile(getContext());
        ((Toolbar) getActivity().findViewById(R.id.tool_bar_hamburger)).setVisibility(View.VISIBLE);

        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        mMessageRecyclerView = (RecyclerView) view.findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        //mLinearLayoutManager.setStackFromEnd(true);
        //Raymonds phone number here
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference().child("requests");
        mFirebaseAdapter = new FirebaseRecyclerAdapter<PaymentRequest, MessageViewHolder>(
                PaymentRequest.class,
                R.layout.list_sent_requests,
                MessageViewHolder.class,
                mFirebaseDatabaseReference.orderByChild("requestEpochDate")) {

            @Override
            protected PaymentRequest parseSnapshot(DataSnapshot snapshot) {
                PaymentRequest pr = super.parseSnapshot(snapshot);
                if (pr != null) {
                    pr.setId(snapshot.getKey());
                    return pr;
                }
                return null;
            }

            @Override
            protected void populateViewHolder(final MessageViewHolder viewHolder,
                                              PaymentRequest pr, int position) {
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                if (pr != null && pr.getRequestorPhoneNumber().equals(profile.getPhoneNumber())) {

                    if (pr.getStrReceiptPic() != null && !pr.getStrReceiptPic().equals("")) {
                        String encodedReceipt = pr.getStrReceiptPic();
                        byte[] encodeByte = Base64.decode(encodedReceipt, Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                        viewHolder.receiptIcon.setImageBitmap(bitmap);
                    }
                    viewHolder.pr = pr;
                    viewHolder.id = pr.getId();
                    viewHolder.description.setText(pr.getDescription());
                    viewHolder.toPerson.setText("Request sent to: " + Friend.getFriendByPhoneNumber(getContext(), pr.getReceipientPhoneNo()).getName());
                    viewHolder.splitAmount.setText("HKD" + String.format("%.2f", pr.getShareAmount()));
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy' 'HH:mm:ss");
                    String date = null;
                    Date temp = new Date(Long.parseLong(pr.getRequestEpochDate()) * (-1));
                    date = simpleDateFormat.format(temp);
                    viewHolder.date.setText(date);
                }
                else {
                    ViewGroup.LayoutParams params = viewHolder.item.getLayoutParams();
                    params.height = 0;
                    viewHolder.item.setLayoutParams(params);
                }

                // log a view action on it
                //FirebaseUserActions.getInstance().end(getMessageViewAction(fd));
            }


            @Override
            public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                MessageViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);
                viewHolder.setOnLongClickListener(new MessageViewHolder.LongClickListener() {
                    @Override
                    public void onLongClick(View view, int position, String id, PaymentRequest pr) {
                        AlertDialog.Builder ImageDialog = new AlertDialog.Builder(getActivity());
                        ImageDialog.setTitle("Receipt Preview - " + pr.getDescription());
                        ImageView showImage = new ImageView(getActivity());
                        Bitmap bitmap = null;
                        if (pr.getStrReceiptPic() != null && !pr.getStrReceiptPic().equals("")) {
                            String encodedReceipt = pr.getStrReceiptPic();
                            byte[] encodeByte = Base64.decode(encodedReceipt, Base64.DEFAULT);
                            bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                        }
                        if (bitmap != null) {
                            showImage.setImageBitmap(bitmap);
                        }
                        ImageDialog.setView(showImage);

                        ImageDialog.setNegativeButton("Close Preview", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                            }
                        });
                        ImageDialog.show();
                    }
                });
                viewHolder.setOnClickListener(new MessageViewHolder.ClickListener() {
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
}
