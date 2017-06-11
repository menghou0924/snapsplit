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

import android.app.Fragment;
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
import android.support.v7.widget.LinearLayoutManager;
import android.widget.ProgressBar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rnd.snapsplit.DialogClickListener;
import com.rnd.snapsplit.FingerprintAuthenticationDialogFragment;
import com.rnd.snapsplit.PaymentRequest;
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

/**
 * Created by Damian on 9/6/2017.
 */

public class OwedFragment extends Fragment implements
        GoogleApiClient.OnConnectionFailedListener, DialogClickListener {

    private static final String TAG = "OwedFragment";
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<PaymentRequest, MessageViewHolder> mFirebaseAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView mMessageRecyclerView;
    private View view;
    private ProgressBar mProgressBar;
    private Fragment mFragment;

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
        TextView description;
        TextView from;
        TextView splitAmount;
        TextView date;
        String id;
        PaymentRequest pr;

        public MessageViewHolder(View v) {
            super(v);
            description = (TextView) itemView.findViewById(R.id.description);
            from = (TextView) itemView.findViewById(R.id.from);
            splitAmount = (TextView) itemView.findViewById(R.id.splitAmount);
            date = (TextView) itemView.findViewById(R.id.date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickListener.onItemClick(v, getAdapterPosition(), id, pr);

                }
            });
        }

        private MessageViewHolder.ClickListener mClickListener;

        //Interface to send callbacks...
        public interface ClickListener{
            public void onItemClick(View view, int position, String id, PaymentRequest pr);
        }

        public void setOnClickListener(MessageViewHolder.ClickListener clickListener){
            mClickListener = clickListener;
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.owed_activity, container, false);

        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        mMessageRecyclerView = (RecyclerView) view.findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        //mLinearLayoutManager.setStackFromEnd(true);
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference().child("1234 5678");
        mFirebaseAdapter = new FirebaseRecyclerAdapter<PaymentRequest, MessageViewHolder>(
                PaymentRequest.class,
                R.layout.owed_item,
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
                if (pr != null) {
                    viewHolder.pr = pr;
                    viewHolder.id = pr.getId();
                    viewHolder.description.setText(pr.getDescription());
                    viewHolder.from.setText(pr.getRequestorName() + " - " + pr.getRequestorPhoneNumber());
                    viewHolder.splitAmount.setText("HKD" + String.format("%.2f", pr.getShareAmount()));
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy' 'HH:mm:ss");
                    String date = null;
                    Date temp = new Date(Long.parseLong(pr.getRequestEpochDate())*(-1));
                    date = simpleDateFormat.format(temp);
                    viewHolder.date.setText(date);
                }

                // log a view action on it
                //FirebaseUserActions.getInstance().end(getMessageViewAction(fd));
            }


            @Override
            public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                MessageViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);
                viewHolder.setOnClickListener(new MessageViewHolder.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position, String id, PaymentRequest pr) {
                        //Toast.makeText(getActivity(), "Item clicked at " + position, Toast.LENGTH_SHORT).show();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("pr", pr);

                        if (initCipher(mCipher, DEFAULT_KEY_NAME)) {
                            // Show the fingerprint dialog. The user has the option to use the fingerprint with
                            // crypto, or you can fall back to using a server-side verified password.
                            FingerprintAuthenticationDialogFragment fragment
                                    = new FingerprintAuthenticationDialogFragment();
                            fragment.setCryptoObject(new FingerprintManager.CryptoObject(mCipher));
                            boolean useFingerprintPreference = mSharedPreferences
                                    .getBoolean(getString(R.string.use_fingerprint_to_authenticate_key),
                                            true);
                            if (useFingerprintPreference) {
                                fragment.setStage(
                                        FingerprintAuthenticationDialogFragment.Stage.FINGERPRINT);
                            } else {
                                fragment.setStage(
                                        FingerprintAuthenticationDialogFragment.Stage.PASSWORD);
                            }
                            fragment.setArguments(bundle);
                            fragment.setTargetFragment(mFragment, 0);
                            fragment.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);
                        } else {
                            // This happens if the lock screen has been disabled or or a fingerprint got
                            // enrolled. Thus show the dialog to authenticate with their password first
                            // and ask the user if they want to authenticate with fingerprints in the
                            // future
                            FingerprintAuthenticationDialogFragment fragment
                                    = new FingerprintAuthenticationDialogFragment();
                            fragment.setCryptoObject(new FingerprintManager.CryptoObject(mCipher));
                            fragment.setStage(
                                    FingerprintAuthenticationDialogFragment.Stage.NEW_FINGERPRINT_ENROLLED);
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
                            @Nullable FingerprintManager.CryptoObject cryptoObject, PaymentRequest pr) {
        if (withFingerprint) {
            // If the user has authenticated with fingerprint, verify that using cryptography and
            // then show the confirmation message.
            assert cryptoObject != null;
            tryEncrypt(cryptoObject.getCipher());
            mFirebaseDatabaseReference.child(pr.getId()).removeValue();
            Toast.makeText(getActivity(), "Payment to " + pr.getRequestorName() + " successful!", Toast.LENGTH_LONG).show();
        } else {
            // Authentication happened with backup password. Just show the confirmation message.
            //showConfirmation(null);
        }
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
