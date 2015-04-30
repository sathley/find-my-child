package com.appacitive.findmychild.model;

import com.appacitive.core.AppacitiveObject;
import com.appacitive.core.AppacitiveUser;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by sathley on 4/29/2015.
 */
public class Tracker extends RealmObject {

    public Tracker(){
        this.fences = new RealmList<GeoFence>();
    }

    public Tracker(Tracker tracker)
    {
        if(tracker == null)
            return;
        this.id = tracker.getId();
        this.imei = tracker.getImei();
        this.tag = tracker.getTag();
        this.status = tracker.getStatus();
        this.lastKnowLatitude = tracker.getLastKnowLatitude();
        this.lastKnownLongitude = tracker.getLastKnownLongitude();
        this.lastKnownDate = tracker.getLastKnownDate();
        this.isEnabled = tracker.getisEnabled();
        this.isInMotion = tracker.getisInMotion();
        this.fences = new RealmList<GeoFence>();
        if(tracker.getFences() != null)
            for(GeoFence geoFence : tracker.getFences())
                this.fences.add(new GeoFence(geoFence));
    }

    public Tracker(AppacitiveObject tracker)
    {
        this.fences = new RealmList<GeoFence>();
        if(tracker == null)
            return;
        this.id = String.valueOf(tracker.getId());
        this.imei = tracker.getPropertyAsString("imei");
        this.tag = tracker.getPropertyAsString("tag");
        this.status = tracker.getPropertyAsString("status");
        double[] lastKnownLocation = tracker.getPropertyAsGeo("last_known_location");
        if(lastKnownLocation != null && lastKnownLocation.length == 2)
        {
            this.lastKnowLatitude = lastKnownLocation[0];
            this.lastKnownLongitude = lastKnownLocation[1];
        }
        this.lastKnownDate = tracker.getPropertyAsDateTime("last_active_time");
        this.isEnabled = tracker.getPropertyAsBoolean("isenabled");
        this.isInMotion = tracker.getPropertyAsBoolean("inmotion");
    }

    @PrimaryKey
    private String id;

    private String imei;
    private String tag;
    private String status;
    private double lastKnowLatitude;
    private double lastKnownLongitude;
    private Date lastKnownDate;
    private boolean isEnabled;
    private boolean isInMotion;

    private RealmList<GeoFence> fences;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getLastKnowLatitude() {
        return lastKnowLatitude;
    }

    public void setLastKnowLatitude(double lastKnowLatitude) {
        this.lastKnowLatitude = lastKnowLatitude;
    }

    public double getLastKnownLongitude() {
        return lastKnownLongitude;
    }

    public void setLastKnownLongitude(double lastKnownLongitude) {
        this.lastKnownLongitude = lastKnownLongitude;
    }

    public Date getLastKnownDate() {
        return lastKnownDate;
    }

    public void setLastKnownDate(Date lastKnownDate) {
        this.lastKnownDate = lastKnownDate;
    }

    public boolean getisEnabled() {
        return isEnabled;
    }

    public void setisEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public boolean getisInMotion() {
        return isInMotion;
    }

    public void setisInMotion(boolean isInMotion) {
        this.isInMotion = isInMotion;
    }

    public RealmList<GeoFence> getFences() {
        return fences;
    }

    public void setFences(RealmList<GeoFence> fences) {
        this.fences = fences;
    }
}
