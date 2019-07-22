package ru.mapkittest.path;

import java.util.ArrayList;
import java.util.List;

import android.R;
import android.content.Context;

import ru.yandex.yandexmapkit.MapController;
import ru.yandex.yandexmapkit.overlay.Overlay;
import ru.yandex.yandexmapkit.overlay.OverlayItem;
import ru.yandex.yandexmapkit.utils.GeoPoint;

public class OverlayRect extends Overlay {

    OverlayRectItem overlayRectItem;
    Context mContext;
    MapController mMapController;
    RectRender rectRender; 
     
    public OverlayRect(MapController mapController) {
        super(mapController);
        mMapController = mapController;
        mContext = mapController.getContext();
        rectRender = new RectRender();
        setIRender(rectRender);
        // TODO Auto-generated constructor stub
        overlayRectItem = new OverlayRectItem(new GeoPoint(0,0), mContext.getResources().getDrawable(R.drawable.btn_star));
        addOverlayItem(overlayRectItem);
    }

    @Override
    public List<OverlayItem> prepareDraw() {
        // TODO Auto-generated method stub
        ArrayList<OverlayItem> draw = new ArrayList<OverlayItem>();
        overlayRectItem.screenPoint.clear();
        for( GeoPoint point : overlayRectItem.geoPoint){
            overlayRectItem.screenPoint.add(mMapController.getScreenPoint(point)); 
        }
        draw.add(overlayRectItem);
        
        return draw;
    }

}
