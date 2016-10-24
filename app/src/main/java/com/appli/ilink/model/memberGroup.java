package com.appli.ilink.model;

/**
 * Created by capp on 15/04/16.
 */
public class memberGroup {
    private String name, balance, adress, phone, active;



    public memberGroup() {

    }



    public memberGroup(String name, String balance, String adress, String phone, String active) {
        this.name = name;
        this.balance = balance;
        this.adress = adress;
        this.phone = phone;
        this.active = active;
    }
    public String getPhone() {
        return phone;
    }

    public memberGroup setPhone(String phone) {
        this.phone = phone;
        return this;
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



    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }
}
