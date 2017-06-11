package com.rnd.snapsplit;

import android.hardware.fingerprint.FingerprintManager;
import android.support.annotation.Nullable;

/**
 * Created by Damian on 11/6/2017.
 */

public interface DialogClickListener {
    public void onPurchased(boolean withFingerprint,
                            @Nullable FingerprintManager.CryptoObject cryptoObject, PaymentRequest pr);
    public void onFingerprintNotRecognized();
}
