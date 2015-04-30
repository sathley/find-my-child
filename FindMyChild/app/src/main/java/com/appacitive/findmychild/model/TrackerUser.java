package com.appacitive.findmychild.model;

import com.appacitive.core.AppacitiveUser;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by sathley on 4/29/2015.
 */
public class TrackerUser extends RealmObject {

    public TrackerUser(){
        this.trackers = new RealmList<Tracker>();
    }

    public TrackerUser(TrackerUser user)
    {
        if(user == null)
            return;
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.phone = user.getPhone();
        this.trackers = new RealmList<Tracker>();
        if(user.getTrackers() != null)
            for(Tracker tracker : user.getTrackers())
                this.trackers.add(new Tracker(tracker));
    }

    public TrackerUser(AppacitiveUser user)
    {
        this.trackers = new RealmList<Tracker>();
        if(user == null)
            return;
        this.id = String.valueOf(user.getId());
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.phone = user.getPhone();
    }

    @PrimaryKey
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String username;
    private String phone;

    private RealmList<Tracker> trackers;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public RealmList<Tracker> getTrackers() {
        return trackers;
    }

    public void setTrackers(RealmList<Tracker> trackers) {
        this.trackers = trackers;
    }
}
