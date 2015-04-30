package com.appacitive.findmychild.infra;

import com.appacitive.findmychild.model.TrackerUser;

import io.realm.Realm;
import io.realm.RealmQuery;

/**
 * Created by sathley on 4/29/2015.
 */
public class StorageManager {

    private Realm getInstance()
    {
        return Realm.getInstance(TrackerApplication.getAppContext());
    }

    public TrackerUser getUser(String userId)
    {
        Realm realm = getInstance();
        RealmQuery<TrackerUser> query = realm.where(TrackerUser.class);
        query.equalTo("id", userId);
        TrackerUser user = clone(query.findFirst());
        realm.close();
        return user;
    }

    public void saveUser(TrackerUser user)
    {
        Realm realm = getInstance();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(user);
        realm.commitTransaction();
        realm.close();
    }

    private TrackerUser clone(TrackerUser user)
    {
        return new TrackerUser(user);
    }
}
