package com.rnd.snapsplit;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Random;

/**
 * Created by Damian on 10/6/2017.
 */

public class PaymentRequest implements Serializable {
    private String requestorName = "";
    private String description = "";
    private String requestorPhoneNumber = "";
    private String requestEpochDate = "";
    private String receipientPhoneNo = "";
    private String id = "";
    private String strReceiptPic="";
    private float shareAmount = 0f;
    private float totalAmount = 0f;
//    private Boolean isPaid = false;
    private final static Random RANDOM = new Random();

    public PaymentRequest() {
    }

    public void setDesription(String description) {
        this.description = description;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setRequestorName(String requestorName) {
        this.requestorName = requestorName;
    }

    public void setRequestorPhoneNumber(String requestorPhoneNumber) {
        this.requestorPhoneNumber = requestorPhoneNumber;
    }

    public void setRequestEpochDate(String requestEpochDate) {
        this.requestEpochDate = requestEpochDate;
    }

    public void setReceipientPhoneNo(String receipientPhoneNo) {
        this.receipientPhoneNo = receipientPhoneNo;
    }

    public void setShareAmount(float shareAmount) {
        this.shareAmount = shareAmount;
    }

    public void setTotalAmount(float totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }

    public String getRequestorName() {
        return requestorName;
    }

    public String getRequestorPhoneNumber() {
        return requestorPhoneNumber;
    }

    public String getRequestEpochDate() {
        return requestEpochDate;
    }

    public String getReceipientPhoneNo() {
        return receipientPhoneNo;
    }

    public float getShareAmount() {
        return shareAmount;
    }

    public float getTotalAmount() {
        return totalAmount;
    }

    public String getStrReceiptPic() {
        return strReceiptPic;
    }

    public void setStrReceiptPic(String strReceiptPic) {
        this.strReceiptPic = strReceiptPic;
    }

    public JSONObject getSelfWithTotalAsJsonObject(Context context) {
            JSONObject request = new JSONObject();
            try {
                request.put("paymentrequest_id", id);
                request.put("paymentrequest_totalAmount", totalAmount);
                request.put("paymentrequest_description", description);
                request.put("paymentrequest_requestEpochDate", requestEpochDate);
                request.put("paymentrequest_strReceiptPic", strReceiptPic);
                request.put("paymentrequest_requestorName", requestorName);
                request.put("paymentrequest_requestorPhoneNumber", requestorPhoneNumber);

            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            return request;
    }

    public JSONObject getSelfWithIndividualSplitAsJsonObject(Context context) {
        JSONObject request = new JSONObject();
        try {
            request.put("paymentrequest_id", id);
            request.put("paymentrequest_totalAmount", totalAmount);
            request.put("paymentrequest_description", description);
            request.put("paymentrequest_requestEpochDate", requestEpochDate);
            request.put("paymentrequest_strReceiptPic", strReceiptPic);
            request.put("paymentrequest_requestorName", requestorName);
            request.put("paymentrequest_requestorPhoneNumber", requestorPhoneNumber);
            request.put("paymentrequest_shareAmount", shareAmount);
            request.put("paymentrequest_receipientPhoneNo", receipientPhoneNo);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return request;
    }
}
