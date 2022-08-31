package org.neshan.delivery.model;

public class Market extends BaseModel {

    private double lat;
    private double lng;

    public double getLat() {
        return lat;
    }

    public Market setLat(double lat) {
        this.lat = lat;
        return this;
    }

    public double getLng() {
        return lng;
    }

    public Market setLng(double lng) {
        this.lng = lng;
        return this;
    }
}
