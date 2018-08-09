package com.dranikpg.gdxmap;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class GeoUtil {


    public static void getCoords(Vector2 v, double lat, double lon, int zoom, int gsize){
        float xtile = (float) ((lon + 180) / 360 * (1<<zoom));
        float ytile = (float) ((1 - Math.log(Math.tan(Math.toRadians(lat)) + 1 / Math.cos(Math.toRadians(lat))) / Math.PI) / 2 * (1<<zoom));
        v.x = xtile;
        v.y = gsize-ytile;
    }

    public static void getTileCoords(Vector2 v, double lat, double lon, int zoom, int gsize){
        int xtile = (int)Math.floor( (lon + 180) / 360 * (1<<zoom) ) ;
        int ytile = (int)Math.floor( (1 - Math.log(Math.tan(Math.toRadians(lat)) + 1 / Math.cos(Math.toRadians(lat))) / Math.PI) / 2 * (1<<zoom) ) ;

        v.x = xtile;
        v.y = gsize-ytile;
    }

    public static void getLatLon(double[] out,float x, float y, int zoom){
        int p  = 1<<zoom;
        double lon_deg = x *1f / p * 360.0 - 180.0;
        double lat_rad = Math.atan(Math.sinh(MathUtils.PI * (1 - 2f * y / p)));
        double lat_deg = lat_rad * 180.0 / MathUtils.PI;
        out[0] = lat_deg;
        out[1] = lon_deg;
    }






}
