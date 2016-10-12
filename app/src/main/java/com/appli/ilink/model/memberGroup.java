package com.appli.ilink.model;

/**
 * Created by capp on 15/04/16.
 */
public class memberGroup {
    private String name, balance, adress, email,phone;



    public memberGroup() {

    }

    public String getPhone() {
        return phone;
    }

    public memberGroup setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public memberGroup(String name, String balance, String adress, String phone) {
        this.name = name;
        this.balance = balance;
        this.adress = adress;
        this.email = email;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getEmail() {
        return email;
    }

    public memberGroup setEmail(String email) {
        this.email = email;
        return this;
    }
}
