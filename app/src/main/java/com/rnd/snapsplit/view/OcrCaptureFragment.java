/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rnd.snapsplit.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.rnd.snapsplit.OcrDetectorProcessor;
import com.rnd.snapsplit.OcrGraphic;
import com.rnd.snapsplit.R;
import com.rnd.snapsplit.StorageManager;
import com.rnd.snapsplit.Summary;
import com.rnd.snapsplit.camera.CameraSource;
import com.rnd.snapsplit.camera.CameraSourcePreview;
import com.rnd.snapsplit.camera.GraphicOverlay;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.IntBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

/**
 * Activity for the Ocr Detecting app.  This app detects text and displays the value with the
 * rear facing camera. During detection overlay graphics are drawn to indicate the position,
 * size, and contents of each TextBlock.
 */
public final class OcrCaptureFragment extends Fragment {
    private static final String TAG = "OcrCaptureFragment";
    StorageManager storageManager;

    // Intent request code to handle updating play services if needed.
    private static final int RC_HANDLE_GMS = 9001;
    boolean shouldContinue = true;

    int rotationAngle = 0;
    int TAKE_PHOTO_CODE = 0;
    public static int count = 0;

    // Permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;

    // Constants used to pass extra data in the intent
    private boolean isPaused = false;
    public static final String AutoFocus = "AutoFocus";
    public static final String UseFlash = "UseFlash";
    public static final String TextBlockObject = "String";
    private static boolean picReceived = false;
    private static String picPath = "";

    private CameraSource mCameraSource;
    private CameraSourcePreview mPreview;
    private GraphicOverlay<OcrGraphic> mGraphicOverlay;

    private Thread t;

    // Helper objects for detecting taps and pinches.
    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;

    // A TextToSpeech engine for speaking a String value.
    private TextToSpeech tts;

    public static OcrCaptureFragment newInstance() {
        return new OcrCaptureFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        final Resources resources = context.getResources();
        storageManager = new StorageManager(context);
    }

    /**
     * Initializes the UI and creates the detector pipeline.
     */
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == TAKE_PHOTO_CODE && resultCode == RESULT_OK) {
//            Toast.makeText(getContext(), "pic saved", Toast.LENGTH_LONG).show();
//            Log.d("CameraDemo", "Pic saved");
//        }
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.view_ocr_capture, container, false);
        final Activity activity = getActivity();
        final Context context = getContext();

        ((Toolbar) activity.findViewById(R.id.tool_bar_hamburger)).setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
        final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/picFolder/";
        File newdir = new File(dir);
        newdir.mkdirs();

        mPreview = (CameraSourcePreview) view.findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay<OcrGraphic>) view.findViewById(R.id.graphicOverlay);


        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        // Set good defaults for capturing text.
        boolean autoFocus = true;
        boolean useFlash = false;

//        createNewThread();
//        t.start();

        final ImageView upArrow = (ImageView) view.findViewById(R.id.arrow_up);
        upArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rotationAngle ==  0){ // arrow up
                    //mCameraSource.takePicture(null, mPicture);
                    //mGraphicOverlay.clear();
//                    mGraphicOverlay.clear();
//                    mGraphicOverlay.amountItem = null;
                    onPause();
                    //shouldContinue = false;
                    //mCamera.takePicture(null, null, mPicture);

                    File pictureFile = getOutputMediaFile();
                    if (pictureFile == null) {
                        return;
                    }
                    try {
                        FileOutputStream fos = new FileOutputStream(pictureFile);
                        Bitmap receiptBitmap = byteStreamToBitmap(mCameraSource.mostRecentBitmap);
                        receiptBitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
                        picPath = pictureFile.getAbsolutePath();
                        //fos.write(mCameraSource.mostRecentBitmap);
                        fos.close();
                    } catch (FileNotFoundException e) {

                    } catch (IOException e) {
                    }

                    upArrow.animate().rotation(180).setDuration(500).start();


                    TextView amount = (TextView) view.findViewById(R.id.text_amount_value);
                    if (mGraphicOverlay.amountItem == null){
                        amount.setText("0.00");
                    }
                    else{
                        amount.setText(String.format("%.2f",mGraphicOverlay.amountItemAfterFormat));
                    }
                    TextView desc = (TextView) view.findViewById(R.id.text_name_value);
                    desc.setText(mGraphicOverlay.description);

                    RelativeLayout box = (RelativeLayout) view.findViewById(R.id.recognition_box);
                    box.setVisibility(View.VISIBLE);
                    Animation slide_up = AnimationUtils.loadAnimation(activity.getApplicationContext(),
                            R.anim.slide_up);

                    box.startAnimation(slide_up);
                    rotationAngle = 180;

                }
                else {
//                    t.interrupt();
//                    t = null;
                    RelativeLayout box = (RelativeLayout) view.findViewById(R.id.recognition_box);
                    Animation slide_down = AnimationUtils.loadAnimation(activity.getApplicationContext(),
                            R.anim.slide_down);

                    upArrow.animate().rotation(0).setDuration(500).start();

                    box.startAnimation(slide_down);
                    box.setVisibility(View.INVISIBLE);
                    //shouldContinue = true;
                    mGraphicOverlay.amountItem = null;
                    mGraphicOverlay.amountItemAfterFormat = 0f;
                    mGraphicOverlay.description = "";
                    onResume();
//                    createNewThread();
//                    t.start();
                    rotationAngle = 0;
                }
            }
        });

        ImageView addButton = (ImageView) view.findViewById(R.id.add_icon);
        addButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 // takePicture();
                 EditText description = (EditText) view.findViewById(R.id.text_name_value);
                 EditText amount = (EditText) view.findViewById(R.id.text_amount_value);
                 float floatAmount = Float.parseFloat(amount.getText().toString());
                 Summary t = new Summary(description.getText().toString(), floatAmount);

                 Bundle bundle = new Bundle();
                 bundle.putSerializable("splitTransaction", t);

//                 ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                 mCameraSource.mostRecentBitmap.compress(Bitmap.CompressFormat.PNG, 80, stream);
//                 byte[] byteArray = stream.toByteArray();
                 //Bitmap receiptBitmap = byteStreamToBitmap(mCameraSource.mostRecentBitmap);
                 //bundle.putParcelable("receiptPicture",receiptBitmap);
                 bundle.putString("receiptPicture", picPath);

                 FriendsSelectionFragment fragment = new FriendsSelectionFragment();
                 fragment.setArguments(bundle);

                 ((Toolbar) activity.findViewById(R.id.tool_bar_hamburger)).setVisibility(View.INVISIBLE);
                 getActivity().getSupportFragmentManager()
                         .beginTransaction()
                         .add(R.id.fragment_holder, fragment, "FriendsSelectionFragment")
                         .addToBackStack(null)
                         .commit();
             }
         });

        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        int rc = ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource(autoFocus, useFlash);
        } else {
            requestCameraPermission();
        }

        gestureDetector = new GestureDetector(context, new CaptureGestureListener());
        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());

//        Snackbar.make(mGraphicOverlay, "Tap to Speak. Pinch/Stretch to zoom",
//                Snackbar.LENGTH_LONG)
//                .show();

        // Set up the Text To Speech engine.
        TextToSpeech.OnInitListener listener =
                new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(final int status) {
                        if (status == TextToSpeech.SUCCESS) {
                            Log.d("OnInitListener", "Text to speech engine started successfully.");
                            tts.setLanguage(Locale.US);
                        } else {
                            Log.d("OnInitListener", "Error starting the text to speech engine.");
                        }
                    }
                };
        tts = new TextToSpeech(activity.getApplicationContext(), listener);

        return view;
    }

    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */

    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "MyCameraApp");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    };


    private void createNewThread(){
        t = new Thread() {

            @Override
            public void run() {
                try {
                    while (shouldContinue) {
                        Thread.sleep(1);

                        if (getActivity() == null) {
                            return;
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(mGraphicOverlay.amountItem != null) {
                                    final ImageView upArrow = (ImageView) getView().findViewById(R.id.arrow_up);
                                    upArrow.animate().rotation(180).setDuration(500).start();
                                    rotationAngle = 180;
                                    TextView amountTV = (TextView) getView().findViewById(R.id.text_amount_value);
                                    String amount = mGraphicOverlay.amountItem.getTextBlock().getValue();
                                    amount = amount.replaceAll("\\s+","");
                                    amount = amount.replaceAll("[$]","");
                                    amountTV.setText(amount);
                                    RelativeLayout box = (RelativeLayout) getView().findViewById(R.id.recognition_box);
                                    box.setVisibility(View.VISIBLE);
                                    Animation slide_up = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),
                                            R.anim.slide_up);

                                    box.startAnimation(slide_up);
                                    onPause();
                                    t.interrupt();

                                }
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
    }

    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(getActivity(), permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }


        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(getActivity(), permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
    }

    /**
     * Creates and starts the camera.  Note that this uses a higher resolution in comparison
     * to other detection examples to enable the ocr detector to detect small text samples
     * at long distances.
     *
     * Suppressing InlinedApi since there is a check that the minimum version is met before using
     * the constant.
     */
    @SuppressLint("InlinedApi")
    private void createCameraSource(boolean autoFocus, boolean useFlash) {
        Context context = getActivity().getApplicationContext();

        // A text recognizer is created to find text.  An associated multi-processor instance
        // is set to receive the text recognition results, track the text, and maintain
        // graphics for each text block on screen.  The factory is used by the multi-processor to
        // create a separate tracker instance for each text block.
        TextRecognizer textRecognizer = new TextRecognizer.Builder(context).build();
        //textRecognizer.setProcessor(new OcrDetectorProcessor(mGraphicOverlay));
        OcrDetectorProcessor idp = new OcrDetectorProcessor(mGraphicOverlay);
//        idp.setOnNewBarcodeListener(new OcrDetectorProcessor.OnNewBarcodeListener(){
//            @Override
//            public void onNewItem(TextBlock item) {
//                onPause();
//            }
//        });
        textRecognizer.setProcessor(idp);

        if (!textRecognizer.isOperational()) {
            // Note: The first time that an app using a Vision API is installed on a
            // device, GMS will download a native libraries to the device in order to do detection.
            // Usually this completes before the app is run for the first time.  But if that
            // download has not yet completed, then the above call will not detect any text,
            // barcodes, or faces.
            //
            // isOperational() can be used to check if the required native libraries are currently
            // available.  The detectors will automatically become operational once the library
            // downloads complete on device.
            Log.w(TAG, "Detector dependencies are not yet available.");

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = getActivity().registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(getContext(), R.string.low_storage_error, Toast.LENGTH_LONG).show();
                Log.w(TAG, getString(R.string.low_storage_error));
            }
        }

        // Creates and starts the camera.  Note that this uses a higher resolution in comparison
        // to other detection examples to enable the text recognizer to detect small pieces of text.

        mCameraSource =
                new CameraSource.Builder(getActivity().getApplicationContext(), textRecognizer)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1280, 1024)
                .setRequestedFps(10.0f)
                .setFlashMode(useFlash ? Camera.Parameters.FLASH_MODE_TORCH : null)
                .setFocusMode(autoFocus ? Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE : null)
                .build();

    }

    /**
     * Restarts the camera.
     */
    @Override
    public void onResume() {
        super.onResume();

        startCameraSource();
    }

    /**
     * Stops the camera.
     */
    @Override
    public void onPause() {
        super.onPause();
        if (mPreview != null) {
            mPreview.stop();
        }
    }

    /**
     * Releases the resources associated with the camera source, the associated detectors, and the
     * rest of the processing pipeline.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPreview != null) {
            mPreview.release();
        }
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // we have permission, so create the camerasource
            boolean autoFocus = getActivity().getIntent().getBooleanExtra(AutoFocus,false);
            boolean useFlash = getActivity().getIntent().getBooleanExtra(UseFlash, false);
            createCameraSource(autoFocus, useFlash);
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));
//
//        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//                finish();
//            }
//        };
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setTitle("Multitracker sample")
//                .setMessage(R.string.no_camera_permission)
//                .setPositiveButton(R.string.ok, listener)
//                .show();
    }

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() throws SecurityException {
        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getActivity().getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    /**
     * onTap is called to speak the tapped TextBlock, if any, out loud.
     *
     * @param rawX - the raw position of the tap
     * @param rawY - the raw position of the tap.
     * @return true if the tap was on a TextBlock
     */
    private boolean onTap(float rawX, float rawY) {

        OcrGraphic graphic = mGraphicOverlay.getGraphicAtLocation(rawX, rawY);
        TextBlock text = null;
        if (graphic != null) {
            text = graphic.getTextBlock();
            if (text != null && text.getValue() != null) {
                Log.d(TAG, "text data is being spoken! " + text.getValue());
                // Speak the string.
                tts.speak(text.getValue(), TextToSpeech.QUEUE_ADD, null, "DEFAULT");
            }
            else {
                Log.d(TAG, "text data is null");
            }
        }
        else {
            Log.d(TAG,"no text detected");
        }
        return text != null;
    }

    private class CaptureGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return onTap(e.getRawX(), e.getRawY()) || super.onSingleTapConfirmed(e);
        }
    }

    private class ScaleListener implements ScaleGestureDetector.OnScaleGestureListener {

        /**
         * Responds to scaling events for a gesture in progress.
         * Reported by pointer motion.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         * @return Whether or not the detector should consider this event
         * as handled. If an event was not handled, the detector
         * will continue to accumulate movement until an event is
         * handled. This can be useful if an application, for example,
         * only wants to update scaling factors if the change is
         * greater than 0.01.
         */
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            return false;
        }

        /**
         * Responds to the beginning of a scaling gesture. Reported by
         * new pointers going down.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         * @return Whether or not the detector should continue recognizing
         * this gesture. For example, if a gesture is beginning
         * with a focal point outside of a region where it makes
         * sense, onScaleBegin() may return false to ignore the
         * rest of the gesture.
         */
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        /**
         * Responds to the end of a scale gesture. Reported by existing
         * pointers going up.
         * <p/>
         * Once a scale has ended, {@link ScaleGestureDetector#getFocusX()}
         * and {@link ScaleGestureDetector#getFocusY()} will return focal point
         * of the pointers remaining on the screen.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         */
        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            if (mCameraSource != null) {
                mCameraSource.doZoom(detector.getScaleFactor());
            }
        }
    }

    public Bitmap byteStreamToBitmap(byte[] data){
        int imageWidth = mCameraSource.getPreviewSize().getWidth();
        int imageHeight = mCameraSource.getPreviewSize().getHeight();
//            YuvImage yuvimage=new YuvImage(data, ImageFormat.NV16, previewSizeW, previewSizeH, null);
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            yuvimage.compressToJpeg(new Rect(0, 0, previewSizeW, previewSizeH), 80, baos);
//            byte[] jdata = baos.toByteArray();

            Bitmap bitmap = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888);
            int numPixels = imageWidth*imageHeight;

// the buffer we fill up which we then fill the bitmap with
            IntBuffer intBuffer = IntBuffer.allocate(imageWidth*imageHeight);
// If you're reusing a buffer, next line imperative to refill from the start,
// if not good practice
            intBuffer.position(0);

// Set the alpha for the image: 0 is transparent, 255 fully opaque
            final byte alpha = (byte) 255;

// Get each pixel, one at a time
            for (int y = 0; y < imageHeight; y++) {
                for (int x = 0; x < imageWidth; x++) {
                    // Get the Y value, stored in the first block of data
                    // The logical "AND 0xff" is needed to deal with the signed issue
                    int Y = data[y*imageWidth + x] & 0xff;

                    // Get U and V values, stored after Y values, one per 2x2 block
                    // of pixels, interleaved. Prepare them as floats with correct range
                    // ready for calculation later.
                    int xby2 = x/2;
                    int yby2 = y/2;

                    // make this V for NV12/420SP
                    float U = (float)(data[numPixels + 2*xby2 + yby2*imageWidth] & 0xff) - 128.0f;

                    // make this U for NV12/420SP
                    float V = (float)(data[numPixels + 2*xby2 + 1 + yby2*imageWidth] & 0xff) - 128.0f;

                    // Do the YUV -> RGB conversion
                    float Yf = 1.164f*((float)Y) - 16.0f;
                    int R = (int)(Yf + 1.596f*V);
                    int G = (int)(Yf - 0.813f*V - 0.391f*U);
                    int B = (int)(Yf            + 2.018f*U);

                    // Clip rgb values to 0-255
                    R = R < 0 ? 0 : R > 255 ? 255 : R;
                    G = G < 0 ? 0 : G > 255 ? 255 : G;
                    B = B < 0 ? 0 : B > 255 ? 255 : B;

                    // Put that pixel in the buffer
                    intBuffer.put(alpha*16777216 + R*65536 + G*256 + B);
                }
            }

// Get buffer ready to be read
            intBuffer.flip();

// Push the pixel information from the buffer onto the bitmap.
            bitmap.copyPixelsFromBuffer(intBuffer);

            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap,imageWidth,imageHeight,true);
            Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap , 0, 0, scaledBitmap .getWidth(), scaledBitmap .getHeight(), matrix, true);
            return rotatedBitmap;
    }
}
