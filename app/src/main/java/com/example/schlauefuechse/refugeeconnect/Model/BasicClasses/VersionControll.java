package com.example.schlauefuechse.refugeeconnect.Model.BasicClasses;

/**
 * Created by Eric on 10.06.16.
 */
public class VersionControll {
    private String versionNumber;

    public VersionControll() {

    }

    public VersionControll(String versionNumber) {
        this.versionNumber = versionNumber;
    }

    public String getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(String versionNumber) {
        this.versionNumber = versionNumber;
    }
}
