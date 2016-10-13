package it.polito.mad.noticeboard.student.search;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polito.mad.noticeboard.db.Notice;
import it.polito.mad.noticeboard.db_parcel.PNoticeData;
import it.polito.mad.noticeboard.maps.GPSTracker;
import it.polito.mad.noticeboard.student.NoticeDetailsActivity;

/**
 * Created by luigi on 31/05/15.
 */
public class MyMapFragment extends SupportMapFragment implements NoticesListener,
        OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private Map<MarkerOptions, PNoticeData> markersInfo = new HashMap<>();

    //private GoogleMap mMap;
    private LatLngBounds mBounds;
    private LatLng curPosition;

    public MyMapFragment(){
        Location myLoc = new GPSTracker(getActivity()).getLocation();
        if (myLoc != null){
            mBounds = new LatLngBounds.Builder().include(
                    new LatLng(myLoc.getLatitude(), myLoc.getLongitude())).build();
        }

    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        //mMap = googleMap;
        googleMap.clear();
        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                if (mBounds != null) {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(mBounds, 50));
                } else {
                    if (curPosition != null) {
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curPosition, 10f));
                    }
                }
            }
        });
        googleMap.setOnMarkerClickListener(this);
        //Toast.makeText(getActivity(), "Map loaded", Toast.LENGTH_LONG).show();
        for (MarkerOptions options : markersInfo.keySet()){
            googleMap.addMarker(options);
        }
    }

    @Override
    public void update(List<PNoticeData> notices) {
        markersInfo.clear();
        mBounds = null;
        curPosition = null;
        if (notices.isEmpty()){
            curPosition = getDefaultLocation();
        }
        else {
            for (PNoticeData n : notices) {
                if (!n.hasValidCoordinates()) {
                    continue;
                }
                if (n.getLatitude() == 0.0 && n.getLongitude() == 0.0){
                    continue;
                }
                MarkerOptions opt = new MarkerOptions();
                opt.title(n.getTitle() != null ? n.getTitle() : "<no title>");
                opt.position(new LatLng(n.getLatitude(), n.getLongitude()));
                markersInfo.put(opt, n);
            }
            if (!markersInfo.isEmpty()) {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (MarkerOptions opt : markersInfo.keySet()) {
                    builder.include(opt.getPosition());
                }
                mBounds = builder.build();
            }
        }
        //IMPORTANT!!!! Call it IN ANY CASE in order to repaint the map
        getMapAsync(this);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        String title = marker.getTitle();
        LatLng pos = marker.getPosition();
        for (MarkerOptions opt : markersInfo.keySet()){
            if (title.equals(opt.getTitle()) && pos.latitude == opt.getPosition().latitude &&
                    pos.longitude == opt.getPosition().longitude){
                Intent toDetailsAct = new Intent(getActivity(), NoticeDetailsActivity.class);
                toDetailsAct.putExtra("noticeData", markersInfo.get(opt));
                startActivity(toDetailsAct);
                break;
            }
        }
        return false;
    }

    private LatLng getDefaultLocation(){
        Location myLoc = new GPSTracker(getActivity()).getLocation();
        double lat = (myLoc != null) ? myLoc.getLatitude() : 0.0;
        double lng = (myLoc != null) ? myLoc.getLongitude() : 0.0;
        return new LatLng(lat,lng);
    }

}
