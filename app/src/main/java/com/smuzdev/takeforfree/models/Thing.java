package com.smuzdev.takeforfree.models;

public class Thing {

    private String thingName;
    private String thingDescription;
    private String thingDiscoveryDate;
    private String thingImage;
    private String thingDiscoveryPlace;
    private String thingPickupPoint;
    private String key;
    private String userName;
    private String userEmail;
    private String userPhone;

    public Thing(String thingName, String thingDescription, String thingDiscoveryDate,
                 String thingDiscoveryPlace, String thingPickupPoint, String thingImage,
                 String userName, String userEmail, String userPhone) {
        this.thingName = thingName;
        this.thingDescription = thingDescription;
        this.thingDiscoveryDate = thingDiscoveryDate;
        this.thingDiscoveryPlace = thingDiscoveryPlace;
        this.thingPickupPoint = thingPickupPoint;
        this.thingImage = thingImage;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
    }

    public Thing() {

    }

    public String getThingName() {
        return thingName;
    }

    public String getThingDescription() {
        return thingDescription;
    }

    public String getThingDiscoveryDate() {
        return thingDiscoveryDate;
    }

    public String getThingDiscoveryPlace() {
        return thingDiscoveryPlace;
    }

    public String getThingPickupPoint() {
        return thingPickupPoint;
    }

    public String getThingImage() {
        return thingImage;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
