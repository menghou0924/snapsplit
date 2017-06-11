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

import android.graphics.Color;
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


    public OcrDetectorProcessor(GraphicOverlay<OcrGraphic> ocrGraphicOverlay) {
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
//            if (item != null && item.getValue().contains("Total")) {
              if (item!= null && isAmount(item)){
                Log.d("OcrDetectorProcessor", "Text detected! " + item.getValue());
                OcrGraphic graphic = null;
                  if (isTotalAmount(item,items)){
                      graphic = new OcrGraphic(mGraphicOverlay, item, Color.GREEN);
                      mGraphicOverlay.amountItem = graphic;
                      mGraphicOverlay.add(graphic);
                  }
                  else{
                      graphic = new OcrGraphic(mGraphicOverlay, item, Color.WHITE);
                      mGraphicOverlay.add(graphic);
                  }
//               TextBlock item2 = findAmount(item, items);
//                if (item2 != null){
//                    OcrGraphic graphic2 = new OcrGraphic(mGraphicOverlay, item2);
//                    mGraphicOverlay.amountItem = graphic2;
//                    mGraphicOverlay.add(graphic2);
//                }
            }
        }
    }

    protected boolean isAmount(TextBlock item){
        String text = item.getValue();
        text = text.replaceAll("\\s+","");
        text = text.replaceAll("[$]","");
        text = text.replaceAll("[s]", "");
        try {
            float f = Float.parseFloat(text);
            return true;
        }
        catch (NumberFormatException nfe){
            return false;
        }
    }

    protected boolean isTotalAmount(TextBlock amountItem, SparseArray<TextBlock> items) {
        int topLeftVerticalPostion = amountItem.getCornerPoints()[0].y;
        int topLeftHorizontalPostion = amountItem.getCornerPoints()[0].x;
        for (int i = 0; i < items.size(); ++i) {
            TextBlock item = items.valueAt(i);
            if (item.getValue().toLowerCase().contains("total")){
                if (topLeftHorizontalPostion  > (item.getCornerPoints()[0].x+100)) { // setting x axis threshold as 100 to avoid checking items from the same vertical axis
                    int positionDiff = topLeftVerticalPostion - item.getCornerPoints()[0].y;
                    if ((positionDiff < 15) && (positionDiff > -15)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Frees the resources associated with this detection processor.
     */
    @Override
    public void release() {
        mGraphicOverlay.clear();
    }
}
