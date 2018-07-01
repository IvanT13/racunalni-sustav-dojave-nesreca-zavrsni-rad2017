package projektutelematici.projekt112;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

/**
 * Created by Ivan on 23.2.2017..
 */

public class GPS_Service extends Service {

    LocationListener locationListener;
    LocationManager manager;

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {

        Log.i("service", "uslo u service");

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Intent i = new Intent("location_update");
                //i.putExtra("coordinates",location.getLongitude()+" "+location.getLatitude());
                i.putExtra("longitude", location.getLongitude());
                i.putExtra("latitude", location.getLatitude());
                sendBroadcast(i);
                Log.i("service", "poslalo");
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        };

        manager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        //noinspection MissingPermission
        try {

            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1500, 0, locationListener);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
            manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1500, 0, locationListener);
        }
    }


    public void onDestroy(){
        super.onDestroy();
        if (manager != null){
            //noinspection MissingPermission
            manager.removeUpdates(locationListener);
        }
    }
}
