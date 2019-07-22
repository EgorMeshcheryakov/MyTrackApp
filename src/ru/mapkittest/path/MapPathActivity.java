package ru.mapkittest.path;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import ru.mapkittest.R;
import ru.yandex.yandexmapkit.*;
import ru.yandex.yandexmapkit.overlay.location.MyLocationItem;
import ru.yandex.yandexmapkit.overlay.location.OnMyLocationListener;
import ru.yandex.yandexmapkit.utils.GeoPoint;

import java.lang.Math;

public class MapPathActivity extends Activity implements OnMyLocationListener {
    /** Called when the activity is first created. */
    private static final String TAG = "MyApp";
    private static final double minDist = 1e-4;
    private static final double maxDist = 1e+3;
    MapController mMapController;
    private static final int PERMISSIONS_CODE = 109;
    private MyLocationItem prevLocationItem;
    private OverlayRect overlayRect;
    private boolean isTracking = false;
    private SharedPreferences lastTrack;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.sample12_head);
        lastTrack = getSharedPreferences("lastTrack", Context.MODE_PRIVATE);

        setContentView(R.layout.sample);

        final MapView mapView = (MapView) findViewById(R.id.map);
        mapView.showBuiltInScreenButtons(true);

        mMapController = mapView.getMapController();
        overlayRect = new OverlayRect(mMapController);

        mMapController.getOverlayManager().addOverlay(overlayRect);
        mMapController.getOverlayManager().getMyLocation().addMyLocationListener(this);

        checkPermission();

        prevLocationItem = new MyLocationItem(new GeoPoint(55.235288,35.451233), this.getResources().getDrawable(R.drawable.a));

        final Button btn = (Button)findViewById(R.id.button);
        btn.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if(isTracking){
                    btn.setText("Старт");
                    isTracking = false;
                }
                else{
                    overlayRect.overlayRectItem.geoPoint.clear();
                    btn.setText("Стоп");
                    isTracking = true;
                }
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        // Запоминаем данные
        SharedPreferences.Editor editor = lastTrack.edit();
        editor.putInt("last_track_key", //overlayRect.overlayRectItem.geoPoint);
        editor.apply();
    }

    private void checkPermission() {
        int permACL = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permAFL = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permACL != PackageManager.PERMISSION_GRANTED ||
                permAFL != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_CODE);
        }

    }

    @Override
    public void onMyLocationChange(MyLocationItem myLocationItem) {
        final double dist = distance(prevLocationItem, myLocationItem);


        if(dist >= minDist && dist <= maxDist && isTracking){
            overlayRect.overlayRectItem.geoPoint.add(myLocationItem.getGeoPoint());
            prevLocationItem.setGeoPoint(myLocationItem.getGeoPoint());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mMapController.getOverlayManager().getMyLocation().refreshPermission();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private double distance(MyLocationItem myLocationItem1, MyLocationItem myLocationItem2){
        double a = Math.abs(myLocationItem1.getGeoPoint().getLat() - myLocationItem2.getGeoPoint().getLat());
        double b = Math.abs(myLocationItem1.getGeoPoint().getLon() - myLocationItem2.getGeoPoint().getLon());
        return Math.sqrt(Math.pow(a,2)+Math.pow(b,2));
    }

    private void saveArrayList(String name, ArrayList<String> list) {
        SharedPreferences prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        StringBuilder sb = new StringBuilder();
        for (String s : list) sb.append(s).append("<s>");
        sb.delete(sb.length() - 3, sb.length());
        editor.putString(name, sb.toString()).apply();
    }

    private ArrayList<String> loadArrayList(String name) {
        SharedPreferences prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
        String[] strings = prefs.getString(name, "").split("<s>");
        ArrayList<String> list = new ArrayList<>();
        list.addAll(Arrays.asList(strings));
        return list;
    }
}