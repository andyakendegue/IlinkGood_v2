package com.appli.ilink.model;

/**
 * Created by capp on 17/04/16.
 */
public class askAdmin {

    private String nom_admin, adresse_admin, phone_admin, pays_admin, reseau_admin, code_membre_admin, mail_admin;

    public askAdmin() {
    }

    public askAdmin(String nom_admin, String adresse_admin, String phone_admin, String pays_admin, String reseau_admin, String code_membre_admin, String mail_admin) {
        this.nom_admin = nom_admin;
        this.adresse_admin = adresse_admin;
        this.phone_admin = phone_admin;
        this.pays_admin = pays_admin;
        this.reseau_admin = reseau_admin;
        this.code_membre_admin = code_membre_admin;
        this.mail_admin = mail_admin;
    }

    public String getNom_admin() {
        return nom_admin;
    }

    public askAdmin setNom_admin(String nom_admin) {
        this.nom_admin = nom_admin;
        return this;
    }

    public String getAdresse_admin() {
        return adresse_admin;
    }

    public askAdmin setAdresse_admin(String adresse_admin) {
        this.adresse_admin = adresse_admin;
        return this;
    }

    public String getPhone_admin() {
        return phone_admin;
    }

    public askAdmin setPhone_admin(String phone_admin) {
        this.phone_admin = phone_admin;
        return this;
    }

    public String getPays_admin() {
        return pays_admin;
    }

    public askAdmin setPays_admin(String pays_admin) {
        this.pays_admin = pays_admin;
        return this;
    }

    public String getReseau_admin() {
        return reseau_admin;
    }

    public askAdmin setReseau_admin(String reseau_admin) {
        this.reseau_admin = reseau_admin;
        return this;
    }

    public String getCode_membre_admin() {
        return code_membre_admin;
    }

    public askAdmin setCode_membre_admin(String code_membre_admin) {
        this.code_membre_admin = code_membre_admin;
        return this;
    }

    public String getMail_admin() {
        return mail_admin;
    }

    public askAdmin setMail_admin(String mail_admin) {
        this.mail_admin = mail_admin;
        return this;
    }
}
