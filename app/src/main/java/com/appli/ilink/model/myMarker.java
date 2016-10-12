package com.appli.ilink.model;

/**
 * Created by capp on 07/04/16.
 */
public class myMarker
{
    private String dfirstName;
    private String dlastName;
    private String dphone;
    private String dcountry_code;
    private String dnetwork;
    private String dmember_code;
    private String demail;
    private String dcategory;
    private String dbalance;
    private String dlatitude;
    private String dlongitude;
    private String ddistance;


    public myMarker(String dfirstName, String dlastName, String dphone, String dcountry_code, String dnetwork, String dmember_code, String demail, String dcategory, String dbalance, String dlatitude, String dlongitude, String ddistance) {
        this.dfirstName = dfirstName;
        this.dlastName = dlastName;
        this.dphone = dphone;
        this.dcountry_code = dcountry_code;
        this.dnetwork = dnetwork;
        this.dmember_code = dmember_code;
        this.demail = demail;
        this.dcategory = dcategory;
        this.dbalance = dbalance;
        this.dlatitude = dlatitude;
        this.dlongitude = dlongitude;
        this.ddistance = ddistance;
    }

    public String getDfirstName() {
        return dfirstName;
    }

    public myMarker setDfirstName(String dfirstName) {
        this.dfirstName = dfirstName;
        return this;
    }

    public String getDlastName() {
        return dlastName;
    }

    public myMarker setDlastName(String dlastName) {
        this.dlastName = dlastName;
        return this;
    }

    public String getDphone() {
        return dphone;
    }

    public myMarker setDphone(String dphone) {
        this.dphone = dphone;
        return this;
    }

    public String getDcountry_code() {
        return dcountry_code;
    }

    public myMarker setDcountry_code(String dcountry_code) {
        this.dcountry_code = dcountry_code;
        return this;
    }

    public String getDnetwork() {
        return dnetwork;
    }

    public myMarker setDnetwork(String dnetwork) {
        this.dnetwork = dnetwork;
        return this;
    }

    public String getDmember_code() {
        return dmember_code;
    }

    public myMarker setDmember_code(String dmember_code) {
        this.dmember_code = dmember_code;
        return this;
    }

    public String getDemail() {
        return demail;
    }

    public myMarker setDemail(String demail) {
        this.demail = demail;
        return this;
    }

    public String getDcategory() {
        return dcategory;
    }

    public myMarker setDcategory(String dcategory) {
        this.dcategory = dcategory;
        return this;
    }

    public String getDbalance() {
        return dbalance;
    }

    public myMarker setDbalance(String dbalance) {
        this.dbalance = dbalance;
        return this;
    }

    public String getDlatitude() {
        return dlatitude;
    }

    public myMarker setDlatitude(String dlatitude) {
        this.dlatitude = dlatitude;
        return this;
    }

    public String getDlongitude() {
        return dlongitude;
    }

    public myMarker setDlongitude(String dlongitude) {
        this.dlongitude = dlongitude;
        return this;
    }

    public String getDdistance() {
        return ddistance;
    }

    public myMarker setDdistance(String ddistance) {
        this.ddistance = ddistance;
        return this;
    }
}

