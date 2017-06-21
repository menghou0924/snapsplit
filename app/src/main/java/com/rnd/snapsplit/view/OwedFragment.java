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
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rnd.snapsplit.CitiAPIBase;
import com.rnd.snapsplit.DialogClickListener;
import com.rnd.snapsplit.Friend;
import com.rnd.snapsplit.History;
import com.rnd.snapsplit.PaymentRequest;
import com.rnd.snapsplit.Profile;
import com.rnd.snapsplit.R;
import com.rnd.snapsplit.StorageManager;

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
import java.util.HashMap;

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

public class OwedFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener, DialogClickListener {

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

    @Override
    public void onFingerprintNotRecognized() {

    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        View item;
        TextView description;
        TextView from;
        TextView share;
        TextView splitAmount;
        TextView date;
        CircleImageView receiptIcon;
        String id;
        PaymentRequest pr;

        public MessageViewHolder(View v) {
            super(v);
            item = itemView;
            description = (TextView) itemView.findViewById(R.id.description);
            from = (TextView) itemView.findViewById(R.id.txt_person);
            share = (TextView) itemView.findViewById(R.id.txt_share);
            splitAmount = (TextView) itemView.findViewById(R.id.splitAmount);
            date = (TextView) itemView.findViewById(R.id.date);
            receiptIcon = (CircleImageView) itemView.findViewById(R.id.receiptIcon) ;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickListener.onItemClick(v, getAdapterPosition(), id, pr);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener(){

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
        public interface ClickListener{
            public void onItemClick(View view, int position, String id, PaymentRequest pr);
            //public void onLongItemClick(View view, int position, String id, PaymentRequest pr);
        }
        public interface LongClickListener{
            //public void onItemClick(View view, int position, String id, PaymentRequest pr);
            public void onLongClick(View view, int position, String id, PaymentRequest pr);
        }

        public void setOnClickListener(MessageViewHolder.ClickListener clickListener){
            mClickListener = clickListener;
        }

        public void setOnLongClickListener(MessageViewHolder.LongClickListener clickListener){
            mLongClickListener = clickListener;
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.activity_owed, container, false);
        activity = getActivity();
        profile = new Profile(getContext());
        ((Toolbar) getActivity().findViewById(R.id.tool_bar_hamburger)).setVisibility(View.VISIBLE);

        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        mMessageRecyclerView = (RecyclerView) view.findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        //mLinearLayoutManager.setStackFromEnd(true);
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference().child("requests");
        mFirebaseAdapter = new FirebaseRecyclerAdapter<PaymentRequest, MessageViewHolder>(
                PaymentRequest.class,
                R.layout.list_owed,
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
                if (pr != null && pr.getReceipientPhoneNo().equals(profile.getPhoneNumber())) {

                    if (pr.getStrReceiptPic() != null && !pr.getStrReceiptPic().equals("")) {
                        String encodedReceipt = pr.getStrReceiptPic();
                        byte[] encodeByte = Base64.decode(encodedReceipt, Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                        viewHolder.receiptIcon.setImageBitmap(bitmap);
                    }
                    viewHolder.pr = pr;
                    viewHolder.id = pr.getId();
                    viewHolder.description.setText(pr.getDescription());
                    viewHolder.from.setText("Request sent by: " + pr.getRequestorName() + " - " + pr.getRequestorPhoneNumber());
                    viewHolder.share.setText("Your Share: HKD" + String.format("%.2f", pr.getShareAmount()));
                    viewHolder.splitAmount.setText("Total Amount: HKD" + String.format("%.2f", pr.getTotalAmount()));
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy' 'HH:mm:ss");
                    String date = null;
                    Date temp = new Date(Long.parseLong(pr.getRequestEpochDate())*(-1));
                    date = simpleDateFormat.format(temp);
                    viewHolder.date.setText(date);
                }
                else {
                    ViewGroup.LayoutParams params = view.getLayoutParams();
                    params.height = 0;
                    viewHolder.item.setLayoutParams(params);
                }


                // log a view action on it
                //FirebaseUserActions.getInstance().end(getMessageViewAction(fd));
            }


            @Override
            public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                MessageViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);
                viewHolder.setOnLongClickListener(new MessageViewHolder.LongClickListener(){
                    @Override
                    public void onLongClick(View view, int position, String id, PaymentRequest pr) {
                        AlertDialog.Builder ImageDialog = new AlertDialog.Builder(getActivity());
                        ImageDialog.setTitle("Receipt Preview - "+pr.getDescription());
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

                        ImageDialog.setNegativeButton("Close Preview", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface arg0, int arg1)
                            {
                            }
                        });
                        ImageDialog.show();
                        }
                });
                viewHolder.setOnClickListener(new MessageViewHolder.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position, String id, PaymentRequest pr) {
                        //Toast.makeText(getActivity(), "Item clicked at " + position, Toast.LENGTH_SHORT).show();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("pr", pr);

                        if (initCipher(mCipher, DEFAULT_KEY_NAME)) {
                            // Show the fingerprint dialog. The user has the option to use the fingerprint with
                            // crypto, or you can fall back to using a server-side verified password.
                            DialogFragmentFingerprintAuthentication fragment
                                    = new DialogFragmentFingerprintAuthentication();
                            fragment.setCryptoObject(new FingerprintManager.CryptoObject(mCipher));
                            boolean useFingerprintPreference = mSharedPreferences
                                    .getBoolean(getString(R.string.use_fingerprint_to_authenticate_key),
                                            true);
                            if (useFingerprintPreference) {
                                fragment.setStage(
                                        DialogFragmentFingerprintAuthentication.Stage.FINGERPRINT);
                            } else {
                                fragment.setStage(
                                        DialogFragmentFingerprintAuthentication.Stage.PASSWORD);
                            }
                            fragment.setArguments(bundle);
                            fragment.setTargetFragment(mFragment, 0);
                            fragment.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);
                        } else {
                            // This happens if the lock screen has been disabled or or a fingerprint got
                            // enrolled. Thus show the dialog to authenticate with their password first
                            // and ask the user if they want to authenticate with fingerprints in the
                            // future
                            DialogFragmentFingerprintAuthentication fragment
                                    = new DialogFragmentFingerprintAuthentication();
                            fragment.setCryptoObject(new FingerprintManager.CryptoObject(mCipher));
                            fragment.setStage(
                                    DialogFragmentFingerprintAuthentication.Stage.NEW_FINGERPRINT_ENROLLED);
                            fragment.setArguments(bundle);
                            fragment.setTargetFragment(mFragment, 0);
                            fragment.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);
                        }
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

        try {
            mKeyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (KeyStoreException e) {
            throw new RuntimeException("Failed to get an instance of KeyStore", e);
        }
        try {
            mKeyGenerator = KeyGenerator
                    .getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException("Failed to get an instance of KeyGenerator", e);
        }
        //Cipher defaultCipher;
        Cipher cipherNotInvalidated;
        try {
            mCipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            cipherNotInvalidated = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get an instance of Cipher", e);
        }
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        KeyguardManager keyguardManager = getActivity().getSystemService(KeyguardManager.class);
        FingerprintManager fingerprintManager = getActivity().getSystemService(FingerprintManager.class);


        if (!keyguardManager.isKeyguardSecure()) {
            // Show a message that the user hasn't set up a fingerprint or lock screen.
            Toast.makeText(getActivity(),
                    "Secure lock screen hasn't set up.\n"
                            + "Go to 'Settings -> Security -> Fingerprint' to set up a fingerprint",
                    Toast.LENGTH_LONG).show();
            //return;
        }

        // Now the protection level of USE_FINGERPRINT permission is normal instead of dangerous.
        // See http://developer.android.com/reference/android/Manifest.permission.html#USE_FINGERPRINT
        // The line below prevents the false positive inspection from Android Studio
        // noinspection ResourceType
        if (!fingerprintManager.hasEnrolledFingerprints()) {
            // This happens when no fingerprints are registered.
            Toast.makeText(getActivity(),
                    "Go to 'Settings -> Security -> Fingerprint' and register at least one fingerprint",
                    Toast.LENGTH_LONG).show();
            //return;
        }

        createKey(DEFAULT_KEY_NAME, true);
        createKey(KEY_NAME_NOT_INVALIDATED, false);

        return view;

    }

    private boolean initCipher(Cipher cipher, String keyName) {
        try {
            mKeyStore.load(null);
            SecretKey key = (SecretKey) mKeyStore.getKey(keyName, null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }

    public void onPurchased(boolean withFingerprint,
                            @Nullable FingerprintManager.CryptoObject cryptoObject, final PaymentRequest pr) {
        if (withFingerprint) {
            // If the user has authenticated with fingerprint, verify that using cryptography and
            // then show the confirmation message.
            assert cryptoObject != null;
            tryEncrypt(cryptoObject.getCipher());
        } else {
            // Authentication happened with backup password. Just show the confirmation message.
            //showConfirmation(null);
        }
        // Citi API doesn't work as the five fake accounts all belong to the same person
        // cannot see the updated balance on the other person and also the transaction amount is somehow wrong
//                (new CitiAPIBase(getContext())).API_MoneyMovement_CreateInternalTransfer(
//                        "355a515030616a53576b6a65797359506a634175764a734a3238314e4668627349486a676f7449463949453d"
//                        , Float.toString(pr.getShareAmount()), "SOURCE_ACCOUNT_CURRENCY"
//                        , "7977557255484c7345546c4e53424766634b6c53756841672b556857626e395253334b70416449676b42673d"
//                        , "BENEFICIARY", "123456", pr.getDescription(), "MEDICAL_SERVICES", this, pr);
//
        Toast.makeText(getActivity(), "Payment to " + pr.getRequestorName() + " successful!", Toast.LENGTH_LONG).show();

        // set account balance, "I" paid here so I am the receipt of the request
        FirebaseDatabase.getInstance().getReference().child(pr.getRequestorPhoneNumber()).addListenerForSingleValueEvent
                (new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        String updatedRequestorBalance = Float.toString(Float.valueOf((String) ((HashMap<String,String>) snapshot.getValue()).get("accountBalance")) + pr.getShareAmount());
                        FirebaseDatabase.getInstance().getReference().child(pr.getRequestorPhoneNumber()).child("accountBalance").setValue(updatedRequestorBalance);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
        });
        FirebaseDatabase.getInstance().getReference().child(pr.getReceipientPhoneNo()).addListenerForSingleValueEvent
                (new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        String updatedRecipientBalance = Float.toString(Float.valueOf((String) ((HashMap<String,String>) snapshot.getValue()).get("accountBalance")) - pr.getShareAmount());
                        FirebaseDatabase.getInstance().getReference().child(pr.getReceipientPhoneNo()).child("accountBalance").setValue(updatedRecipientBalance);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

        // remove the request so that it becomes paid
        mFirebaseDatabaseReference.child(pr.getId()).removeValue();

        // set history
        (new History(getContext())).setPaymentHistory(pr);
        (new History(getContext())).setReceivePaymentHistory(Friend.getFriendByPhoneNumber(getContext(), pr.getRequestorPhoneNumber()), pr);
        FirebaseDatabase.getInstance().getReference().child(pr.getRequestorPhoneNumber()).child("history").setValue((new StorageManager(getContext()).getFile("history")));
    }

    private void tryEncrypt(Cipher cipher) {
        try {
            byte[] encrypted = cipher.doFinal(SECRET_MESSAGE.getBytes());
            //showConfirmation(encrypted);
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            Toast.makeText(getActivity(), "Failed to encrypt the data with the generated key. "
                    + "Retry the purchase", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Failed to encrypt the data with the generated key." + e.getMessage());
        }
    }

    public void createKey(String keyName, boolean invalidatedByBiometricEnrollment) {
        // The enrolling flow for fingerprint. This is where you ask the user to set up fingerprint
        // for your flow. Use of keys is necessary if you need to know if the set of
        // enrolled fingerprints has changed.
        try {
            mKeyStore.load(null);
            // Set the alias of the entry in Android KeyStore where the key will appear
            // and the constrains (purposes) in the constructor of the Builder

            KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(keyName,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    // Require the user to authenticate with a fingerprint to authorize every use
                    // of the key
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7);

            // This is a workaround to avoid crashes on devices whose API level is < 24
            // because KeyGenParameterSpec.Builder#setInvalidatedByBiometricEnrollment is only
            // visible on API level +24.
            // Ideally there should be a compat library for KeyGenParameterSpec.Builder but
            // which isn't available yet.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                builder.setInvalidatedByBiometricEnrollment(invalidatedByBiometricEnrollment);
            }
            mKeyGenerator.init(builder.build());
            mKeyGenerator.generateKey();
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException
                | CertificateException | IOException e) {
            throw new RuntimeException(e);
        }
    }


}
