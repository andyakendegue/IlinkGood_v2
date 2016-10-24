package com.appli.ilink.model;

public class usersModel {
    private String name,lastname, email, phone, country_code, network, member_code,code_parrain, category, balance, latitude, longitude, mbre_reseau, mbre_ss_reseau, validation_code, active;

    public usersModel() {

    }

    public usersModel(String name, String lastname, String email, String phone, String country_code, String network, String member_code, String code_parrain, String category, String balance, String latitude, String longitude, String mbre_reseau, String mbre_ss_reseau, String validation_code, String active) {
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.phone = phone;
        this.country_code = country_code;
        this.network = network;
        this.member_code = member_code;
        this.code_parrain = code_parrain;
        this.category = category;
        this.balance = balance;
        this.latitude = latitude;
        this.longitude = longitude;
        this.mbre_reseau = mbre_reseau;
        this.mbre_ss_reseau = mbre_ss_reseau;
        this.validation_code = validation_code;
        this.active = active;
    }



    public String getName() {
        return name;
    }

    public usersModel setName(String name) {
        this.name = name;
        return this;
    }

    public String getLastname() {
        return lastname;
    }

    public usersModel setLastname(String lastname) {
        this.lastname = lastname;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public usersModel setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public usersModel setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getCountry_code() {
        return country_code;
    }

    public usersModel setCountry_code(String country_code) {
        this.country_code = country_code;
        return this;
    }

    public String getNetwork() {
        return network;
    }

    public usersModel setNetwork(String network) {
        this.network = network;
        return this;
    }

    public String getMember_code() {
        return member_code;
    }

    public usersModel setMember_code(String member_code) {
        this.member_code = member_code;
        return this;
    }

    public String getCategory() {
        return category;
    }

    public usersModel setCategory(String category) {
        this.category = category;
        return this;
    }

    public String getBalance() {
        return balance;
    }

    public usersModel setBalance(String balance) {
        this.balance = balance;
        return this;
    }

    public String getLatitude() {
        return latitude;
    }

    public usersModel setLatitude(String latitude) {
        this.latitude = latitude;
        return this;
    }

    public String getLongitude() {
        return longitude;
    }

    public usersModel setLongitude(String longitude) {
        this.longitude = longitude;
        return this;
    }

    public String getValidation_code() {
        return validation_code;
    }

    public usersModel setValidation_code(String validation_code) {
        this.validation_code = validation_code;
        return this;
    }

    public String getactive() {
        return active;
    }

    public usersModel setactive(String active) {
        this.active = active;
        return this;
    }
    public String getCode_parrain() {
        return code_parrain;
    }

    public usersModel setCode_parrain(String code_parrain) {
        this.code_parrain = code_parrain;
        return this;
    }

    public String getMbre_reseau() {
        return mbre_reseau;
    }

    public usersModel setMbre_reseau(String mbre_reseau) {
        this.mbre_reseau = mbre_reseau;
        return this;
    }

    public String getMbre_ss_reseau() {
        return mbre_ss_reseau;
    }

    public usersModel setMbre_ss_reseau(String mbre_ss_reseau) {
        this.mbre_ss_reseau = mbre_ss_reseau;
        return this;
    }
}
