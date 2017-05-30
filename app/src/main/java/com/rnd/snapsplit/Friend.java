package com.rnd.snapsplit;

/**
 * Created by Damian on 29/5/2017.
 */

public class Friend {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String accountNumber;
    private int displayPic;

    @Override
    public boolean equals(Object obj) {
        Friend other = (Friend) obj;
        return other.getPhoneNumber() == phoneNumber;
    }

    public Friend (String firstName, String lastName,
        String phoneNumber, String accountNumber, int displayPic){
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.accountNumber = accountNumber;
        this.displayPic = displayPic;
    }

    public String getFirstName(){
        return firstName;
    }

    public String getLastName(){
        return lastName;
    }

    public String getPhoneNumber(){
        return phoneNumber;
    }

    public int getDisplayPic() { return displayPic; }

    public String getAccountNo(){
        return accountNumber;
    }

}
