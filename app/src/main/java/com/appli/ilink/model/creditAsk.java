package com.appli.ilink.model;

/**
 * Created by capp on 17/04/16.
 */
public class creditAsk {
    private String nom_membre,adresse_membre, code_membre, pays_membre, reseau_membre, phone_membre, mail_membre, montant;

    public creditAsk() {
    }

    public creditAsk(String nom_membre, String adresse_membre, String code_membre, String pays_membre, String reseau_membre, String phone_membre, String mail_membre, String montant) {
        this.nom_membre = nom_membre;
        this.adresse_membre = adresse_membre;
        this.code_membre = code_membre;
        this.pays_membre = pays_membre;
        this.reseau_membre = reseau_membre;
        this.phone_membre = phone_membre;
        this.mail_membre = mail_membre;
        this.montant = montant;
    }

    public String getNom_membre() {
        return nom_membre;
    }

    public creditAsk setNom_membre(String nom_membre) {
        this.nom_membre = nom_membre;
        return this;
    }

    public String getAdresse_membre() {
        return adresse_membre;
    }

    public creditAsk setAdresse_membre(String adresse_membre) {
        this.adresse_membre = adresse_membre;
        return this;
    }

    public String getCode_membre() {
        return code_membre;
    }

    public creditAsk setCode_membre(String code_membre) {
        this.code_membre = code_membre;
        return this;
    }

    public String getPays_membre() {
        return pays_membre;
    }

    public creditAsk setPays_membre(String pays_membre) {
        this.pays_membre = pays_membre;
        return this;
    }

    public String getReseau_membre() {
        return reseau_membre;
    }

    public creditAsk setReseau_membre(String reseau_membre) {
        this.reseau_membre = reseau_membre;
        return this;
    }

    public String getPhone_membre() {
        return phone_membre;
    }

    public creditAsk setPhone_membre(String phone_membre) {
        this.phone_membre = phone_membre;
        return this;
    }

    public String getMail_membre() {
        return mail_membre;
    }

    public creditAsk setMail_membre(String mail_membre) {
        this.mail_membre = mail_membre;
        return this;
    }

    public String getMontant() {
        return montant;
    }

    public creditAsk setMontant(String montant) {
        this.montant = montant;
        return this;
    }
}
