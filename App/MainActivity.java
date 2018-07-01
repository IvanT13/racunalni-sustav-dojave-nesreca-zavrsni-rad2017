package projektutelematici.projekt112;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {

    //PERMISSIONS CODES
    int FINE_LOCATION_CODE = 50;
    int SYSTEM_ALERT_WINDOW_CODE = 51;


    public boolean fine_location_permission= false;


    final String PREFS_NAME = "MyPrefsFile"; //File u koji se sprema dali je app pokrenut prvi puta
    public static String PATH_FOLDER = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Poziv112";
    SharedPreferences settings;

    ProgressDialog progressDialog;

    private static final String userPrefs = "osobniPodaci";
    SharedPreferences sharedPreferences;

    RequestQueue requestQueue;

    EditText editText_adresa_servera;

    public String adresa_servera = "";
    int provjera_adresa_servera = 0;
    int statusServera = 0;


    //Lokacija
    Intent lokacija;
    public static String geo_duzina = "";
    public static String geo_sirina = "";
    BroadcastReceiver broadcastReceiver;
    boolean lokacija_radi = false;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText_adresa_servera = (EditText) findViewById(R.id.edittext_unosAdrese);

        //Određivanje da li je app pokrenuta prvi puta
        settings = getSharedPreferences(PREFS_NAME, 0);

        sharedPreferences = getSharedPreferences(userPrefs,MODE_PRIVATE);
        requestQueue = Volley.newRequestQueue(getApplicationContext());


        if (settings.getBoolean("my_first_time", true) || settings.getBoolean("NISU_ spremljeni_podatci_o_korisniku", true)) {
            //the app is being launched for first time, do something
            //Kreiranje foldera aplikacije za spremanje podataka
            File dir = new File(PATH_FOLDER);
            dir.mkdir();

            Intent intent = new Intent(this, FirstTimeActivity.class);

            startActivity(intent);

            // record the fact that the app has been started at least once
            settings.edit().putBoolean("my_first_time", false).apply();
        }

        pokretanjeGPS();

    }



    public void pokretanjeGPS(){

        lokacija = new Intent(getApplicationContext(),GPS_Service.class);
        startService(lokacija);
        lokacija_radi = true;
        Log.i("GPS","doslo");

    }

    public void azuriraj_podatkeOnClick(View view) {

       Intent intent = new Intent(this, AzuriranjePodatakaActivity.class);

        startActivity(intent);

    }

    public void izlaz(View view) {

        //onDestroy(); //Zaustavlja GPS service da spriječi memory leak
        Log.i("BUTTON IZLAZ","Sitsnul si button");

        finish();
        System.exit(0);
    }

    public void poziv112(View view) {
        if (String.valueOf(editText_adresa_servera.getText()).trim().equals("")) {

            Toast.makeText(getApplicationContext(), "Polje za unos adrese poslužitelja ne smije biti prazno", Toast.LENGTH_SHORT).show();
            provjera_adresa_servera = 0;
        } else {

            adresa_servera = editText_adresa_servera.getText().toString();
            provjera_adresa_servera = 1;
        }

        if (provjera_adresa_servera == 1) {

            Intent intent = new Intent(this, Poziv112Activity.class);
            Bundle extras = new Bundle();
            extras.putString("SLANJE_ADRESE", adresa_servera);
            extras.putString("GEO_DUZINA",geo_duzina);
            extras.putString("GEO_SIRINA",geo_sirina);
            intent.putExtras(extras);
            startActivity(intent);
        }
    }


    public void korisnikOpasnost(View view) {



            if (String.valueOf(editText_adresa_servera.getText()).trim().equals("")) {

                Toast.makeText(getApplicationContext(), "Polje za unos adrese poslužitelja ne smije biti prazno", Toast.LENGTH_SHORT).show();
                provjera_adresa_servera = 0;
            } else {

                adresa_servera = editText_adresa_servera.getText().toString();
                provjera_adresa_servera = 1;
            }

            if (provjera_adresa_servera == 1) {

                adresa_servera = editText_adresa_servera.getText().toString();

                new ProvjeraVeze().execute();
            }

    }

    //ZA GPS SERVICE
    protected void onResume(){
        super.onResume();
        if(broadcastReceiver == null){
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    //tu primas podatke od servisa
                    //intent.getExtras().get("coordinates");
                    geo_duzina = intent.getExtras().get("longitude").toString();
                    geo_sirina = intent.getExtras().get("latitude").toString();
                }
            };
        }
        registerReceiver(broadcastReceiver,new IntentFilter("location_update"));
    }
    // ZA GPS SERVICE
    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d("event","onDestroy was Called!");
       /*if(broadcastReceiver != null) {
           Log.i("ON DESTROY", "Uslo je u if petlju");

           unregisterReceiver(broadcastReceiver);
       } */
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(broadcastReceiver != null) {
            Log.i("ON DESTROY", "Uslo je u if petlju");

            unregisterReceiver(broadcastReceiver);
        }

    }

    public class ProvjeraVeze extends AsyncTask<String, Void, String> {


        protected String doInBackground(final String... params) {

            try {
                Log.i("URL", "http://" + adresa_servera);
                URL myUrl = new URL("http://" + adresa_servera);
                URLConnection connection = myUrl.openConnection();
                connection.setConnectTimeout(2000);
                connection.connect();
                statusServera = 1;
            } catch (Exception e) {
                e.printStackTrace();
                statusServera = 0;
                // Handle your exceptions

            }

            return null;
        }

        protected void onPostExecute(String result) {

            if(statusServera == 1) {

                StringRequest request = new StringRequest(com.android.volley.Request.Method.POST, "http://"+adresa_servera+"/poziv112/insertDojava.php", new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }, new com.android.volley.Response.ErrorListener(){
                    public void onErrorResponse(VolleyError error){

                    }
                }){

                    protected Map<String,String> getParams() throws AuthFailureError {
                        Map<String,String> parameters = new HashMap<String, String>();
                        parameters.put("ime",sharedPreferences.getString("ime","NEMA INFORMACIJA"));
                        parameters.put("prezime",sharedPreferences.getString("prezime","NEMA INFORMACIJA"));
                        parameters.put("dat_rod",sharedPreferences.getString("datRod","0000/00/00"));
                        parameters.put("kg",sharedPreferences.getString("krvnaGrupa","NEMA INFORMACIJA"));
                        parameters.put("mobitel",sharedPreferences.getString("brojMob","NEMA INFORMACIJA"));
                        parameters.put("mobitelICE",sharedPreferences.getString("brojMobICE","NEMA INFORMACIJA"));
                        parameters.put("vrsta_nesrece","KORISNIK JE U OPASNOSTI");
                        parameters.put("podvrsta_nesrece","AUTOMATSKA DOJAVA");
                        parameters.put("ozlijedeni","NEMA INFORMACIJA");
                        parameters.put("vozila","NEMA INFORMACIJA");
                        parameters.put("gps_duzina",geo_duzina);
                        parameters.put("gps_sirina",geo_sirina);
                        parameters.put("foto","0.jpg");

                        return parameters;
                    }

                };

                requestQueue.add(request);

                Toast.makeText(getApplicationContext(),"Dojava poslana",Toast.LENGTH_SHORT).show();

                progressDialog.dismiss();

            } else {

                Toast.makeText(getApplicationContext(), "Greška poslužitelja", Toast.LENGTH_SHORT).show();

                progressDialog.dismiss();
            }


        }

        protected void onPreExecute() {

            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Šaljem...");
            progressDialog.setTitle("Slanje podataka o dojavi");
            progressDialog.show();
        }


    }


}

/*
<<<<<<<<<<<<<<<<<<<<<<<<<< KRAJ KORISNOG KODA >>>>>>>>>>>>>>>>>>>>>

 */

/* ONO ČA SI KORISTIL ALI SI NAŠAL ZAMJENU

    INTENT
    public static String SLANJE_ADRESE;
     public static String SLANJE_PUTA;
     intent.putExtra(SLANJE_PUTA, PATH_FOLDER);

     Bundle extras = new Bundle();
      //intent.putExtra(SLANJE_PUTA,PATH_FOLDER);
     extras.putString("SLANJE_PUTA", PATH_FOLDER);
            extras.putString("SLANJE_PREFS_FILE", PREFS_NAME);
            intent.putExtras(extras);

            //  intent.putExtra(SLANJE_PUTA,PATH_FOLDER);

             extras.putString("SLANJE_PUTA", PATH_FOLDER);

    STARI GPS
    TU ZOVI GPS
     new Lokacija().execute();

        //Varijable za pristup lokaciji
        LocationManager locationManager;
        LocationListener listener;

            //Lokacija
            locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);

            listener = new LocationListener() {
    @Override
    public void onLocationChanged(Location location) {

            geo_duzina = String.valueOf(location.getLongitude());
            geo_sirina = String.valueOf(location.getLatitude());

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
            startActivity(i);

            }
            };

            //Lokacija
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET}
            ,10);
            }
            return;
            }

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, listener);


    SLANJE DOJAVE
    String info_nesreca = "";
            File file_nesreca = new File(PATH_FOLDER + "/info_nesreca.txt");

            info_nesreca = "KORISNIK JE U OPASNOSTI \t NEMA INFORMACIJA \t  NEMA INFORMACIJA \t  NEMA INFORMACIJA \t" + geo_duzina + "\t" + geo_sirina + "\t" + 0;

// info_nesreca += "--> Geo. duzina: " + geo_duzina + "\n--> Geo. sirina: " + geo_sirina + "\n";
           try {
                spremiPodatke(file_nesreca, info_nesreca); //TU TI JE FUNKCIJA ZA SPREMANJE U DATOTEKU
            } catch (NullPointerException npe) {

                File dir = new File(PATH_FOLDER);
                dir.mkdir();

                spremiPodatke(file_nesreca, info_nesreca);
            }

//slanje na server


// new ProvjeraVeze().execute("");


            /*slanje_podataka("info_nesreca.txt");

            slanje_podataka("info_korisnik.txt");


// Toast.makeText(getApplicationContext(), "Podaci poslani", Toast.LENGTH_SHORT).show();


 //Spremanje u txt file [ZA SADA NAJBOLJA VERZIJA]
    public static void spremiPodatke(File file, String data) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            try {
                for (int i = 0; i < data.length(); i++) {
                    fos.write(data.charAt(i));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } finally {
            try {

                fos.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    SLANJE PODATAKA
     public void slanje_podataka(final String datoteka){


        Thread t= new Thread(new Runnable() {
            @Override
            public void run() {

                File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Poziv112/" + datoteka);
                String content_type = getMimeType(f.getPath());

                String file_path = f.getAbsolutePath();
                OkHttpClient client = new OkHttpClient();
                RequestBody file_body = RequestBody.create(MediaType.parse(content_type),f);

                RequestBody request_body = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("type",content_type)
                        .addFormDataPart("uploaded_file",file_path.substring(file_path.lastIndexOf("/")+1),file_body)
                        .build();

                Request request = new Request.Builder()
                        .url("http://" + adresa_servera + "/slanje_podataka.php")
                        .post(request_body)
                        .build();

                try {
                    Response response = client.newCall(request).execute();

                    if(!response.isSuccessful()){

                        throw new IOException("Error: " +response);
                    }



                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        t.start();
    }


    GPS KROZ DRETVU ALI NE RADI KAKO TREBA - NE VUČE KOORDINATE
    public class Lokacija extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {

            LocationManager locationManager;
            LocationListener listener;

            //Lokacija
            locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);

            listener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    geo_duzina = String.valueOf(location.getLongitude());
                    geo_sirina = String.valueOf(location.getLatitude());

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
                    startActivity(i);

                }
            };

            //Lokacija
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET}
                            ,10);
                }
            }
            Looper.prepare();
            try{
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, listener);
            }catch (Exception e){
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, listener);
            }

            return null;
        }

        protected void onPostExecute(String result) {
            Toast.makeText(MainActivity.this, "GPS lokacija pronađena", Toast.LENGTH_SHORT).show();
        }

    }


    ZA SLANJE PODATAKA KROZ ASYNCTASK - VIŠE TI NE TREBA JE ŠALJEŠ PREKO SHAREDPREFERENCESA

     private String getMimeType(String path) {

        String extension = MimeTypeMap.getFileExtensionFromUrl(path);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }

    public class LongOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(final String... params) {

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {

                    File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Poziv112/" + params[0]);
                    String content_type = getMimeType(f.getPath());

                    String file_path = f.getAbsolutePath();
                    OkHttpClient client = new OkHttpClient();
                    RequestBody file_body = RequestBody.create(MediaType.parse(content_type), f);

                    RequestBody request_body = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("type", content_type)
                            .addFormDataPart("uploaded_file", file_path.substring(file_path.lastIndexOf("/") + 1), file_body)
                            .build();

                    Request request = new Request.Builder()
                            .url("http://" + adresa_servera + "/slanje_podataka.php")
                            .post(request_body)
                            .build();

                    try {

                        Response response = client.newCall(request).execute();


                        if (!response.isSuccessful()) {


                            throw new IOException("Error: " + response);


                        }


                    } catch (IOException e) {
                        e.printStackTrace();

                    }

                }
            });

            t.start();


            return "Gotovo";
        }

        @Override
        protected void onPostExecute(String result) {

            Toast.makeText(getApplicationContext(), "POSLANO", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();

        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {


        }
    }






    UNUTAR korisnikOpasnost() METODE ZA SLANJE PODATAKA - SADA SI TO STAVIL UNUTAR ProvjeraVeze() klase

    StringRequest request = new StringRequest(com.android.volley.Request.Method.POST, "http://"+adresa_servera+"/poziv112/insertDojava.php", new com.android.volley.Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                }
            }, new com.android.volley.Response.ErrorListener(){
                public void onErrorResponse(VolleyError error){

                }
            }){

                protected Map<String,String> getParams() throws AuthFailureError {
                    Map<String,String> parameters = new HashMap<String, String>();
                    parameters.put("ime",sharedPreferences.getString("ime","NEMA INFORMACIJA"));
                    parameters.put("prezime",sharedPreferences.getString("prezime","NEMA INFORMACIJA"));
                    parameters.put("dat_rod",sharedPreferences.getString("datRod","0000/00/00"));
                    parameters.put("kg",sharedPreferences.getString("krvnaGrupa","NEMA INFORMACIJA"));
                    parameters.put("mobitel",sharedPreferences.getString("brojMob","NEMA INFORMACIJA"));
                    parameters.put("mobitelICE",sharedPreferences.getString("brojMobICE","NEMA INFORMACIJA"));
                    parameters.put("vrsta_nesrece","KORISNIK JE U OPASNOSTI");
                    parameters.put("podvrsta_nesrece","AUTOMATSKA DOJAVA");
                    parameters.put("ozlijedeni","NEMA INFORMACIJA");
                    parameters.put("vozila","NEMA INFORMACIJA");
                    parameters.put("gps_duzina",geo_duzina);
                    parameters.put("gps_sirina",geo_sirina);
                    parameters.put("foto","0");

                    return parameters;
                }

            };

            requestQueue.add(request);

            Toast.makeText(getApplicationContext(),"DOJAVA POSLANA",Toast.LENGTH_SHORT).show();


            NEŠTO ZA PERMISSIONE ALI SI TI SVOJE NAPISAL

             //OVO NEĆE TREBAT
    public boolean runtime_permissions() {

        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},10);
            dopustenje_GPS = 1;
            return true;

        }
        dopustenje_GPS = 0;
        return false;
    }
    //NI OVO ISTO
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==10){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){

                dopustenje_GPS = 1;
            }else{
                runtime_permissions();
            }
        }
    }


    PERMISSION ZA GPS




    //ON REQUEST PERMISSION METODA
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if(requestCode == FINE_LOCATION_CODE) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fine_location_permission = true;
                pokretanjeGPS();
            } else {
                fine_location_permission = false;
                Toast.makeText(this, "Nije odobreno pristupanje lokaciji", Toast.LENGTH_SHORT).show();
            }
        }else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

        //METODA ZA FINE LOCATION
    public void access_overlay_permission(){

        if(Build.VERSION.SDK_INT >= 23 &&
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            fine_location_permission = true;


        }else if(Build.VERSION.SDK_INT>=23 &&
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED){

            if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){

                Toast.makeText(this,"Potrebno je dopusiti pristupanje lokaciji zbog slanja žurnih službi",Toast.LENGTH_LONG).show();
            }

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},FINE_LOCATION_CODE);
        }
    }
    //METODA ZA FINE LOCATION
    public void access_fine_location(){

        if(Build.VERSION.SDK_INT >= 23 &&
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            fine_location_permission = true;


        }else if(Build.VERSION.SDK_INT>=23 &&
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED){

            if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){

                Toast.makeText(this,"Potrebno je dopusiti pristupanje lokaciji zbog slanja žurnih službi",Toast.LENGTH_LONG).show();
            }

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},FINE_LOCATION_CODE);
        }
    }

*/
