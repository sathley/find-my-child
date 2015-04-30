package com.appacitive.findmychild.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.appacitive.findmychild.R;
import com.appacitive.findmychild.infra.BusProvider;
import com.appacitive.findmychild.model.events.TrackerItemClickedEvent;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sathley on 4/30/2015.
 */
public class MapCarouselAdapter extends BaseAdapter {

    private List<Tracker> mTrackers = new ArrayList<Tracker>();
    private Context mContext;

    public MapCarouselAdapter(List<Tracker> trackers, Context context) {
        this.mContext = context;
        this.mTrackers = trackers;
    }

    @Override
    public int getCount() {
        return mTrackers.size();
    }

    @Override
    public Tracker getItem(int i) {
        return mTrackers.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_map_carousel, viewGroup, false);
        }

        final Tracker tracker = getItem(i);
        CircleImageView imageView = (CircleImageView) view.findViewById(R.id.tracker_image);
        if (i % 2 == 0)
            imageView.setImageResource(R.drawable.kid2);
        else imageView.setImageResource(R.drawable.kid1);
        imageView.setOnClickListener(null);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TrackerItemClickedEvent event = new TrackerItemClickedEvent();
                event.position = i;
                event.tracker = tracker;
                BusProvider.getInstance().post(event);
            }
        });
        return view;
    }
}
