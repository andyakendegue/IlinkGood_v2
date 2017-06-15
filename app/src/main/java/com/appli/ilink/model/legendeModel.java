package com.appli.ilink.model;

/**
 * Created by capp on 17/04/16.
 */
public class legendeModel {
    private String legende_name;

    private int legende_picture;
    public legendeModel() {
    }

    public legendeModel(int legende_picture, String legende_name) {
        this.legende_picture = legende_picture;
        this.legende_name = legende_name;
    }

    public int getLegende_picture() {
        return legende_picture;
    }

    public void setLegende_picture(int legende_picture) {
        this.legende_picture = legende_picture;
    }

    public String getLegende_name() {
        return legende_name;
    }

    public void setLegende_name(String legende_name) {
        this.legende_name = legende_name;
    }
}
