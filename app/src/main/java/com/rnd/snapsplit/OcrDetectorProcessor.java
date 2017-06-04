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
package com.rnd.snapsplit;

import android.util.Log;
import android.util.SparseArray;

import com.rnd.snapsplit.camera.GraphicOverlay;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;

/**
 * A very simple Processor which gets detected TextBlocks and adds them to the overlay
 * as OcrGraphics.
 */
public class OcrDetectorProcessor implements Detector.Processor<TextBlock> {

    private GraphicOverlay<OcrGraphic> mGraphicOverlay;
    private OnNewBarcodeListener newBarcodeListener;


    OcrDetectorProcessor(GraphicOverlay<OcrGraphic> ocrGraphicOverlay) {
        mGraphicOverlay = ocrGraphicOverlay;
    }

    /**
     * Called by the detector to deliver detection results.
     * If your application called for it, this could be a place to check for
     * equivalent detections by tracking TextBlocks that are similar in location and content from
     * previous frames, or reduce noise by eliminating TextBlocks that have not persisted through
     * multiple detections.
     */
    public interface OnNewBarcodeListener {
        void onNewItem(TextBlock item);
    }

    public void setOnNewBarcodeListener(OnNewBarcodeListener newBarcodeListener) {
        this.newBarcodeListener = newBarcodeListener;
    }

    @Override
    public void receiveDetections(Detector.Detections<TextBlock> detections) {
        mGraphicOverlay.clear();
        SparseArray<TextBlock> items = detections.getDetectedItems();
        for (int i = 0; i < items.size(); ++i) {
            TextBlock item = items.valueAt(i);
            if (item != null && item.getValue().contains("Total")) {
                Log.d("OcrDetectorProcessor", "Text detected! " + item.getValue());
                OcrGraphic graphic = new OcrGraphic(mGraphicOverlay, item);
                mGraphicOverlay.add(graphic);
                TextBlock item2 = findAmount(item, items);
                if (item2 != null){
                    OcrGraphic graphic2 = new OcrGraphic(mGraphicOverlay, item2);
                    mGraphicOverlay.amountItem = graphic2;
                    mGraphicOverlay.add(graphic2);
                }
            }
        }
    }

    protected TextBlock findAmount(TextBlock totalItem, SparseArray<TextBlock> items) {
        int topLeftVerticalPostion = totalItem.getCornerPoints()[0].y;
        int topLeftHorizontalPostion = totalItem.getCornerPoints()[0].x;
        for (int i = 0; i < items.size(); ++i) {
            TextBlock item = items.valueAt(i);
            if (item.getCornerPoints()[0].x > (topLeftHorizontalPostion + 100)) { // setting x axis threshold as 100 to avoid checking items from the same vertical axis
                int positionDiff = topLeftVerticalPostion - item.getCornerPoints()[0].y;
                if ((positionDiff < 15) && (positionDiff > -15)) {
                    return item;
                }
            }
        }
        return null;
    }

    /**
     * Frees the resources associated with this detection processor.
     */
    @Override
    public void release() {
        mGraphicOverlay.clear();
    }
}
