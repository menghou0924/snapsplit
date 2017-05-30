package com.rnd.snapsplit;

import java.io.Serializable;

/**
 * Created by Damian on 30/5/2017.
 */

public class Transaction implements Serializable {
    private String transactionName;
    private float transactionAmount;

//    @Override
//    public boolean equals(Object obj) {
//        Friend other = (Friend) obj;
//        return other.getPhoneNumber() == phoneNumber;
//    }

    public Transaction (String transactionName, float transactionAmount){
        this.transactionName = transactionName;
        this.transactionAmount = transactionAmount;
    }

    public String getTransactionName(){
        return transactionName;
    }

    public float getTransactionAmount(){
        return transactionAmount;
    }

}
