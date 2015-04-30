package com.appacitive.findmychild.model;

import com.appacitive.core.AppacitiveObject;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by sathley on 4/29/2015.
 */
public class GeoFence extends RealmObject {

    public GeoFence()
    {

    }

    public GeoFence(GeoFence fence)
    {
        if(fence == null)
            return;
        this.id = fence.getId();
        this.centreLatitude = fence.getCentreLatitude();
        this.centreLongitude = fence.getCentreLongitude();
        this.tag = fence.getTag();
        this.radius = fence.getRadius();
    }

    public GeoFence(AppacitiveObject fence)
    {
        if(fence == null)
            return;
        this.id = String.valueOf(fence.getId());
        this.radius = fence.getPropertyAsDouble("radius");
        this.tag = fence.getPropertyAsString("tag");
        double[] centre = fence.getPropertyAsGeo("center");
        if(centre!= null && centre.length == 2)
        {
            this.centreLatitude = centre[0];
            this.centreLongitude = centre[1];
        }
    }


    @PrimaryKey
    private String id;
    private double centreLatitude;
    private double centreLongitude;
    private double radius;
    private String tag;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getCentreLatitude() {
        return centreLatitude;
    }

    public void setCentreLatitude(double centreLatitude) {
        this.centreLatitude = centreLatitude;
    }

    public double getCentreLongitude() {
        return centreLongitude;
    }

    public void setCentreLongitude(double centreLongitude) {
        this.centreLongitude = centreLongitude;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
