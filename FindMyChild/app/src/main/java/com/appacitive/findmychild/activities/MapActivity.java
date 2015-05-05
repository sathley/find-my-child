package com.appacitive.findmychild.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.appacitive.core.AppacitiveGraphSearch;
import com.appacitive.core.AppacitiveObject;
import com.appacitive.core.model.AppacitiveGraphNode;
import com.appacitive.core.model.Callback;
import com.appacitive.findmychild.R;
import com.appacitive.findmychild.adapters.MapCarouselAdapter;
import com.appacitive.findmychild.infra.BusProvider;
import com.appacitive.findmychild.infra.SharedPreferencesManager;
import com.appacitive.findmychild.infra.SnackBarManager;
import com.appacitive.findmychild.infra.StorageManager;
import com.appacitive.findmychild.infra.widgets.Carousel;
import com.appacitive.findmychild.model.GeoFence;
import com.appacitive.findmychild.model.Tracker;
import com.appacitive.findmychild.model.TrackerUser;
import com.appacitive.findmychild.model.events.TrackerItemClickedEvent;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.otto.Subscribe;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.realm.RealmList;

public class MapActivity extends ActionBarActivity implements OnMapReadyCallback {

    private String mUserId;
    private TrackerUser mUser;

    @InjectView(R.id.map_carousel)
    protected Carousel mCarousel;

    @InjectView(R.id.tv_no_trackers)
    protected TextView mNoTrackers;

    @InjectView(R.id.toolbar_default)
    protected Toolbar mToolbar;

    protected Socket mSocket;
    private int mSleepCount = 0;
    private GoogleMap mMap;
    private MapCarouselAdapter mAdapter;
    private int mCurrentTrackerSelection = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.inject(this);
        setSupportActionBar(mToolbar);
        mToolbar.setTitleTextColor(Color.WHITE);
        mToolbar.setTitleTextAppearance(this, R.style.AppTheme);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mUserId = SharedPreferencesManager.ReadUserId();
        if (mUserId == null) {
            goToLoginActivity();
        }

        mUser = new StorageManager().getUser(mUserId);
        fetchTrackersFromServer(mUserId);
    }

    private void fetchTrackersFromServer(final String mUserId) {

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Finding your kids");
        dialog.show();

        List<Long> ids = new ArrayList<Long>() {{
            add(Long.valueOf(mUserId));
        }};
        AppacitiveGraphSearch.projectQueryInBackground("account_info", ids, null, new Callback<List<AppacitiveGraphNode>>() {
            @Override
            public void success(List<AppacitiveGraphNode> result) {
                dialog.dismiss();

                // prepare for battle
                if (result == null || result.size() == 0)
                    return;
                List<AppacitiveGraphNode> accounts = result.get(0).getChildren("acc");
                if (accounts == null || accounts.size() == 0)
                    return;
                List<AppacitiveGraphNode> trackers = accounts.get(0).getChildren("trackers");
                if (trackers == null || trackers.size() == 0)
                    return;

                //  empty the trackers
                mUser.setTrackers(new RealmList<Tracker>());
                for (AppacitiveGraphNode trackerNode : trackers) {
                    Tracker tracker = new Tracker((AppacitiveObject) trackerNode.object);
                    List<AppacitiveGraphNode> fences = trackerNode.getChildren("fences");
                    if (fences != null && fences.size() > 0) {
                        for (AppacitiveGraphNode fenceNode : fences) {
                            GeoFence fence = new GeoFence((AppacitiveObject) fenceNode.object);
                            if (fence != null)
                                tracker.getFences().add(fence);
                        }

                    }
                    if (tracker != null) {
                        mUser.getTrackers().add(tracker);
                    }
                }
                new StorageManager().saveUser(mUser);
                initAdapter();

                connectToTrackers();

                BusProvider.getInstance().post(mAdapter.getItem(0).getId());
            }

            @Override
            public void failure(List<AppacitiveGraphNode> result, Exception e) {
                dialog.dismiss();
                SnackBarManager.showError("Unable to fetch your trackers at the moment.", MapActivity.this);
            }
        });
    }

    private void initAdapter() {
        mAdapter = new MapCarouselAdapter(mUser.getTrackers(), this);
        mCarousel.setAdapter(mAdapter);
        mCarousel.setSpacing(1.5f);
        mCarousel.setSelection(0);

        if (mUser.getTrackers().size() == 0) {
            mNoTrackers.setVisibility(View.VISIBLE);

        } else {
            TrackerItemClickedEvent event = new TrackerItemClickedEvent();
            event.position = 0;
            event.tracker = mUser.getTrackers().get(0);
            BusProvider.getInstance().post(event);
        }

    }

    private void connectToTrackers() {

        JSONArray trackerIds = new JSONArray();
        for (int i = 0; i < mUser.getTrackers().size(); i++)
            trackerIds.put(mUser.getTrackers().get(i).getId());
        try {
            IO.Options options = new IO.Options();
            options.forceNew = true;
            options.multiplex = true;
            options.reconnectionAttempts = 3;

            mSocket = IO.socket(getResources().getString(R.string.socketiourl));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        mSocket.on(Socket.EVENT_CONNECT, onConnectListener);
        mSocket.on("ping", onLocationReceived);
        mSocket.on(Socket.EVENT_ERROR, errorListener);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, errorListener);

        mSocket.connect();
        while (mSocket.connected() == false) {
            try {
                Thread.sleep(200);
                mSleepCount++;
                if (mSleepCount == 15) {
                    SnackBarManager.showError("Unable to connect to the tracker at the moment", MapActivity.this);
                    break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        mSocket.emit("room_request", trackerIds);
    }

    private Emitter.Listener onLocationReceived = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (args == null)
                        return;
                    JSONObject jsonObject = (JSONObject) args[0];
                    String trackerId = jsonObject.optString("id");
                    if (trackerId != null && trackerId.equals(mAdapter.getItem(mCurrentTrackerSelection).getId())) {
                        String coordinates = jsonObject.optString("latlang");
                        if (coordinates != null) {
                            String[] coordinateStringArray = coordinates.split(",");
                            if (coordinateStringArray.length == 2) {
                                drawMarker(new LatLng(Double.valueOf(coordinateStringArray[0]), Double.valueOf(coordinateStringArray[1])));
                            }
                        }
                    }
                }
            });
        }
    };

    private Emitter.Listener onConnectListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    SnackBarManager.showSuccess("Connected to socket io", MapActivity.this);
                }
            });
        }
    };

    private void drawMarker(LatLng latLng) {

        mMap.clear();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, mMap.getCameraPosition().zoom));
        mMap.addMarker(new MarkerOptions().position(latLng));
    }

    private Emitter.Listener errorListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    SnackBarManager.showError("Unable to connect to the tracker at the moment", MapActivity.this);
                }
            });

        }
    };

    private void goToLoginActivity() {
        Intent loginIntent = new Intent(MapActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
        return;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_map_logout: {
                SharedPreferencesManager.RemoveUserId();
                startActivity(new Intent(MapActivity.this, LoginActivity.class));
                overridePendingTransition(R.anim.slide_in_left_fast, R.anim.slide_out_right_fast);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.reset(this);
        mSocket.disconnect();
        mSocket.off();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setBuildingsEnabled(true);
        mMap.setMyLocationEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Subscribe
    public void onTrackerClick(TrackerItemClickedEvent event) {
        mMap.clear();
        mCurrentTrackerSelection = event.position;
        Tracker tracker = mUser.getTrackers().get(event.position);
        if (!Double.isNaN(tracker.getLastKnownLongitude()) && !Double.isNaN(tracker.getLastKnowLatitude())) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(tracker.getLastKnowLatitude(), tracker.getLastKnownLongitude()), 15));
            mMap.addMarker(new MarkerOptions().position(new LatLng(tracker.getLastKnowLatitude(), tracker.getLastKnownLongitude())));

        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?");
        builder.setPositiveButton("EXIT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
                System.exit(0);
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                return;
            }
        });
        builder.show();


    }
}
