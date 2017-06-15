package com.rnd.snapsplit;

import java.io.Serializable;

/**
 * Created by Damian on 10/6/2017.
 */

public class PaymentRequest implements Serializable {
    private String requestorName = "";
    private String description = "";
    private String requestorPhoneNumber = "";
    private String requestEpochDate = "";
    private String receiptPicture = "";
    private String receipientPhoneNo = "";
    private String id ="";
    private float shareAmount = 0f;

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

    public void setReceiptPicture(String receiptPicture) {
        this.receiptPicture = receiptPicture;
    }

    public void setReceipientPhoneNo(String receipientPhoneNo) {
        this.receipientPhoneNo = receipientPhoneNo;
    }

    public void setShareAmount(float shareAmount) {
        this.shareAmount = shareAmount;
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

    public String getReceiptPicture() {
        return receiptPicture;
    }

    public String getReceipientPhoneNo() {
        return receipientPhoneNo;
    }

    public float getShareAmount() {
        return shareAmount;
    }


}
