package projektutelematici.projekt112;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by Ivan on 9.11.2016..
 */

public class Poziv112Activity extends Activity{

    final String PREFS_NAME = "MyPrefsFile"; //File u koji se sprema dali je app pokrenut prvi puta
    public static String PATH_FOLDER = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Poziv112";

    private static final String userPrefs = "osobniPodaci";
    SharedPreferences sharedPreferences;

    //Glavne kontrole
    Button button_posalji_info;
    Button button_snimi_foto;

    //Kontrole odabira za prometnu nesrecu
    CheckBox checkBox_prometna_nesreca;
    CheckBox checkBox_prometna_nesreca_ozlijedeni;
    EditText editText_prometna_nesreca_ozlijedeni;
    EditText editText_prometna_nesreca_koliko_vozila;
    CheckBox checkBox_prometna_nesreca_zapaljenje;

    //Kontrole odabira za pozar
    CheckBox checkBox_pozar;
    CheckBox checkBox_pozar_objekta;
    CheckBox checkBox_pozar_otovrenog_prostora;
    CheckBox checkBox_pozar_vozila;
    CheckBox checkBox_pozar_eksplozija;
    CheckBox checkBox_pozar_ozlijedeni;
    EditText editText_pozar_ozlijedeni;

    //Kontrole
    CheckBox checkBox_ubojstvo;
    CheckBox checkBox_teroristicki_napad;
    CheckBox checkBox_anonimno_slanje;


    //Kontrole za odabir razbojstva
    CheckBox checkBox_razbojstvo;
    CheckBox checkBox_razbojstvo_osobe;
    CheckBox checkBox_razbojstvo_objekta;
    CheckBox checkBox_razbojstvo_vozila;
    CheckBox checkBox_razbojstvo_ozlijedeni;
    EditText editText_razbojstvo_ozlijedeni;

    //Kontrole za odabir osobi je pozlilo
    CheckBox checkBox_osobi_je_pozlilo;
    CheckBox checkBox_osobi_je_pozlilo_malo;
    CheckBox checkBox_osobi_je_pozlilo_srednje;
    CheckBox checkBox_osobi_je_pozlilo_jako;
    CheckBox checkBox_osobi_je_pozlilo_bez_svijesti;

    //Kontrole drugo
    CheckBox checkBox_drugo;
    EditText editText_drugo;

    //put do foldera za spremanje
    public String path_folder;

    //varijabla za provjeru ispravnosti unosa
    int provjera_ispisa = 0;

    //varijabla za provjeru da li je snimljena fotografija
    int provjera_foto = 0;
    String naziv_foto = "0";

    //Lokacija
    public static String geo_duzina = "";
    public static String geo_sirina = "";

    //fotografija
    static final int CAM_REQUEST = 1;

    ProgressDialog progressDialog;

    //adresa servera
    public String adresa_servera;

    int statusServera = 0;

    //Varijable za volley dojave
    String vrsta_nesrece = "";
    String podvrsta_nesrece = "";
    String ozlijedeni = "";
    String vozila = "";

    RequestQueue requestQueue;

    Intent lokacija;
    boolean lokacija_radi = false;

    BroadcastReceiver broadcastReceiver;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_112);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        adresa_servera = extras.getString("SLANJE_ADRESE");
        geo_duzina = extras.getString("GEO_DUZINA");
        geo_sirina = extras.getString("GEO_SIRINA");

        if(geo_duzina.equals("") || geo_sirina.equals("")){

            pokretanjeGPS();
        }

        Log.i("longitude",geo_duzina);
        Log.i("latitude",geo_sirina);

        //put do foldera za spremanje slika dojave
        path_folder = PATH_FOLDER;

        //Preuzimanje korisničkih podataka
        sharedPreferences = getSharedPreferences(userPrefs,MODE_PRIVATE);

        // pokretanje GPS servica
        Intent i = new Intent(getApplicationContext(),GPS_Service.class);
        startService(i).toString();

        //Glavne kontrole
        button_posalji_info = (Button)findViewById(R.id.btn_posalji);
        button_snimi_foto = (Button)findViewById(R.id.btn_snimi_foto);


        //Kontrole odabira za prometnu nesrecu
        checkBox_prometna_nesreca = (CheckBox)findViewById(R.id.checkbox_prometna_nesreca);
        checkBox_prometna_nesreca_ozlijedeni = (CheckBox)findViewById(R.id.checkbox_prometna_nesreca_ozlijedeni);
        editText_prometna_nesreca_ozlijedeni = (EditText) findViewById(R.id.edittext_koliko_ozlijedenih);
        editText_prometna_nesreca_koliko_vozila = (EditText)findViewById(R.id.edittext_koliko_vozila);
        checkBox_prometna_nesreca_zapaljenje = (CheckBox)findViewById(R.id.checkbox_prometna_nesreca_zapaljenje);

        //Kontrole odabira za pozar
        checkBox_pozar = (CheckBox)findViewById(R.id.checkbox_pozar);
        checkBox_pozar_objekta = (CheckBox)findViewById(R.id.checkbox_pozar_objekta);
        checkBox_pozar_otovrenog_prostora = (CheckBox)findViewById(R.id.checkbox_pozar_otvorenog_prostora);
        checkBox_pozar_vozila = (CheckBox)findViewById(R.id.checkbox_pozar_vozila);
        checkBox_pozar_eksplozija = (CheckBox)findViewById(R.id.checkbox_pozar_eksplozija);
        checkBox_pozar_ozlijedeni = (CheckBox)findViewById(R.id.checkbox_pozar_ozlijedeni);
        editText_pozar_ozlijedeni = (EditText)findViewById(R.id.edittext_koliko_ozlijedenih_pozar);

        //Kontrole
        checkBox_ubojstvo = (CheckBox)findViewById(R.id.checkbox_ubojstvo);
        checkBox_teroristicki_napad = (CheckBox)findViewById(R.id.checkbox_teroristici_napad);
        checkBox_anonimno_slanje = (CheckBox)findViewById(R.id.checkbox_anonimno_slanje);

        //Kontrole za odabir razbojstva
        checkBox_razbojstvo = (CheckBox)findViewById(R.id.checkbox_razbojstvo);
        checkBox_razbojstvo_osobe = (CheckBox)findViewById(R.id.checkbox_razbojstvo_osobe);
        checkBox_razbojstvo_objekta = (CheckBox)findViewById(R.id.checkbox_razbojstvo_objekta);
        checkBox_razbojstvo_vozila = (CheckBox)findViewById(R.id.checkbox_razbojstvo_vozila);
        checkBox_razbojstvo_ozlijedeni = (CheckBox)findViewById(R.id.checkbox_razbojstvo_ozlijedeni);
        editText_razbojstvo_ozlijedeni = (EditText)findViewById(R.id.edittext_razbojstvo_ozlijedeni);

        //Kontrole za odabir osobi je pozlilo
        checkBox_osobi_je_pozlilo = (CheckBox)findViewById(R.id.checkbox_osobi_je_pozlilo);
        checkBox_osobi_je_pozlilo_malo = (CheckBox)findViewById(R.id.checkbox_osobi_je_pozlilo_malo);
        checkBox_osobi_je_pozlilo_srednje = (CheckBox)findViewById(R.id.checkbox_osobi_je_pozlilo_srednje);
        checkBox_osobi_je_pozlilo_jako = (CheckBox)findViewById(R.id.checkbox_osobi_je_pozlilo_jako);
        checkBox_osobi_je_pozlilo_bez_svijesti = (CheckBox)findViewById(R.id.checkbox_osobi_je_pozlilo_bez_svijesti);

        //Kontrole drugo
        checkBox_drugo = (CheckBox)findViewById(R.id.checkbox_drugo);
        editText_drugo = (EditText) findViewById(R.id.edittext_drugo);

        //kreiranje requesta za volley
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        //Funckije
        //Otvaranje/zatvaranje prometne nesrece
        checkBox_prometna_nesreca.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){

                    checkBox_prometna_nesreca_ozlijedeni.setEnabled(true);

                    checkBox_prometna_nesreca_ozlijedeni.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                            if(isChecked){

                                editText_prometna_nesreca_ozlijedeni.setEnabled(true);

                            }
                            else if(!isChecked){

                                editText_prometna_nesreca_ozlijedeni.setEnabled(false);
                                editText_prometna_nesreca_ozlijedeni.setText("");

                            }

                        }
                    });

                    checkBox_prometna_nesreca_zapaljenje.setEnabled(true);
                    editText_prometna_nesreca_koliko_vozila.setEnabled(true);

                    checkBox_pozar.setChecked(false);
                    checkBox_ubojstvo.setChecked(false);
                    checkBox_teroristicki_napad.setChecked(false);
                    checkBox_razbojstvo.setChecked(false);
                    checkBox_osobi_je_pozlilo.setChecked(false);
                    checkBox_drugo.setChecked(false);

                }
                else if(!isChecked){

                    checkBox_prometna_nesreca_ozlijedeni.setEnabled(false);
                    checkBox_prometna_nesreca_zapaljenje.setEnabled(false);
                    editText_prometna_nesreca_koliko_vozila.setEnabled(false);
                    editText_prometna_nesreca_koliko_vozila.setText("");

                    checkBox_prometna_nesreca_ozlijedeni.setChecked(false);
                    checkBox_prometna_nesreca_zapaljenje.setChecked(false);

                    provjera_ispisa = 0;

                }

            }


        });

        //otvaranje/zatvaranje pozar
        checkBox_pozar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
          @Override
          public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

              if(isChecked){

                  checkBox_pozar_objekta.setEnabled(true);
                  checkBox_pozar_otovrenog_prostora.setEnabled(true);
                  checkBox_pozar_vozila.setEnabled(true);
                  checkBox_pozar_eksplozija.setEnabled(true);
                  checkBox_pozar_ozlijedeni.setEnabled(true);
                  checkBox_pozar_ozlijedeni.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                      @Override
                      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                          if(isChecked){

                              editText_pozar_ozlijedeni.setEnabled(true);
                          }
                          else if(!isChecked){

                              editText_pozar_ozlijedeni.setEnabled(false);
                              editText_pozar_ozlijedeni.setText("");
                          }
                      }
                  });

                  checkBox_prometna_nesreca.setChecked(false);
                  checkBox_ubojstvo.setChecked(false);
                  checkBox_teroristicki_napad.setChecked(false);
                  checkBox_razbojstvo.setChecked(false);
                  checkBox_osobi_je_pozlilo.setChecked(false);
                  checkBox_drugo.setChecked(false);

                  checkBox_pozar_objekta.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                      @Override
                      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                          if(isChecked){
                              checkBox_pozar_otovrenog_prostora.setChecked(false);
                              checkBox_pozar_vozila.setChecked(false);
                              checkBox_pozar_eksplozija.setChecked(false);
                          }
                      }
                  });

                  checkBox_pozar_otovrenog_prostora.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                      @Override
                      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                          if(isChecked){
                              checkBox_pozar_objekta.setChecked(false);
                              checkBox_pozar_vozila.setChecked(false);
                              checkBox_pozar_eksplozija.setChecked(false);
                          }
                      }
                  });

                  checkBox_pozar_vozila.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                      @Override
                      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                          if(isChecked){
                              checkBox_pozar_objekta.setChecked(false);
                              checkBox_pozar_otovrenog_prostora.setChecked(false);
                              checkBox_pozar_eksplozija.setChecked(false);
                          }
                      }
                  });

                  checkBox_pozar_eksplozija.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                      @Override
                      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                          if(isChecked){
                              checkBox_pozar_objekta.setChecked(false);
                              checkBox_pozar_otovrenog_prostora.setChecked(false);
                              checkBox_pozar_vozila.setChecked(false);
                          }
                      }
                  });

              }
              else if(!isChecked){

                  checkBox_pozar_objekta.setEnabled(false);
                  checkBox_pozar_otovrenog_prostora.setEnabled(false);
                  checkBox_pozar_vozila.setEnabled(false);
                  checkBox_pozar_eksplozija.setEnabled(false);
                  checkBox_pozar_ozlijedeni.setEnabled(false);


                  checkBox_pozar_objekta.setChecked(false);
                  checkBox_pozar_otovrenog_prostora.setChecked(false);
                  checkBox_pozar_vozila.setChecked(false);
                  checkBox_pozar_eksplozija.setChecked(false);
                  checkBox_pozar_ozlijedeni.setChecked(false);

                  provjera_ispisa = 0;

              }

          }

        });

        checkBox_ubojstvo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){

                    checkBox_prometna_nesreca.setChecked(false);
                    checkBox_pozar.setChecked(false);
                    checkBox_teroristicki_napad.setChecked(false);
                    checkBox_razbojstvo.setChecked(false);
                    checkBox_osobi_je_pozlilo.setChecked(false);
                    checkBox_drugo.setChecked(false);
                }
                if(!isChecked){

                    provjera_ispisa = 0;
                }
            }
        });

        checkBox_teroristicki_napad.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){

                    checkBox_prometna_nesreca.setChecked(false);
                    checkBox_pozar.setChecked(false);
                    checkBox_ubojstvo.setChecked(false);
                    checkBox_razbojstvo.setChecked(false);
                    checkBox_osobi_je_pozlilo.setChecked(false);
                    checkBox_drugo.setChecked(false);
                }
                if(isChecked){
                    provjera_ispisa = 0;
                }
            }
        });


        checkBox_razbojstvo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){

                    checkBox_razbojstvo_osobe.setEnabled(true);
                    checkBox_razbojstvo_objekta.setEnabled(true);
                    checkBox_razbojstvo_vozila.setEnabled(true);

                    checkBox_razbojstvo_ozlijedeni.setEnabled(true);
                    checkBox_razbojstvo_ozlijedeni.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                            if(isChecked){

                                editText_razbojstvo_ozlijedeni.setEnabled(true);
                            }
                            else if(!isChecked){

                                editText_razbojstvo_ozlijedeni.setEnabled(false);
                                editText_razbojstvo_ozlijedeni.setText("");

                            }
                        }
                    });

                    checkBox_razbojstvo_osobe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if(isChecked){
                                checkBox_razbojstvo_vozila.setChecked(false);
                                checkBox_razbojstvo_objekta.setChecked(false);
                            }
                        }
                    });

                    checkBox_razbojstvo_vozila.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if(isChecked){
                                checkBox_razbojstvo_osobe.setChecked(false);
                                checkBox_razbojstvo_objekta.setChecked(false);
                            }
                        }
                    });

                    checkBox_razbojstvo_objekta.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if(isChecked){
                                checkBox_razbojstvo_osobe.setChecked(false);
                                checkBox_razbojstvo_vozila.setChecked(false);
                            }
                        }
                    });

                    checkBox_prometna_nesreca.setChecked(false);
                    checkBox_pozar.setChecked(false);
                    checkBox_ubojstvo.setChecked(false);
                    checkBox_teroristicki_napad.setChecked(false);
                    checkBox_osobi_je_pozlilo.setChecked(false);
                    checkBox_drugo.setChecked(false);

                }
                else if(!isChecked){

                    checkBox_razbojstvo_osobe.setEnabled(false);
                    checkBox_razbojstvo_objekta.setEnabled(false);
                    checkBox_razbojstvo_vozila.setEnabled(false);
                    checkBox_razbojstvo_ozlijedeni.setEnabled(false);


                    checkBox_razbojstvo_osobe.setChecked(false);
                    checkBox_razbojstvo_objekta.setChecked(false);
                    checkBox_razbojstvo_vozila.setChecked(false);
                    checkBox_razbojstvo_ozlijedeni.setChecked(false);

                    provjera_ispisa = 0;


                }
            }
        });

        checkBox_osobi_je_pozlilo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){

                    checkBox_osobi_je_pozlilo_malo.setEnabled(true);
                    checkBox_osobi_je_pozlilo_malo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                                if(isChecked){

                                    checkBox_osobi_je_pozlilo_malo.setChecked(true);
                                    checkBox_osobi_je_pozlilo_srednje.setChecked(false);
                                    checkBox_osobi_je_pozlilo_jako.setChecked(false);
                                    checkBox_osobi_je_pozlilo_bez_svijesti.setChecked(false);

                                }
                                if(!isChecked){

                                    checkBox_osobi_je_pozlilo_malo.setChecked(false);
                                }


                        }
                    });
                    checkBox_osobi_je_pozlilo_srednje.setEnabled(true);
                    checkBox_osobi_je_pozlilo_srednje.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                            if(isChecked){

                                checkBox_osobi_je_pozlilo_srednje.setChecked(true);
                                checkBox_osobi_je_pozlilo_malo.setChecked(false);
                                checkBox_osobi_je_pozlilo_jako.setChecked(false);
                                checkBox_osobi_je_pozlilo_bez_svijesti.setChecked(false);

                            }
                            if(!isChecked){

                                checkBox_osobi_je_pozlilo_srednje.setChecked(false);
                            }

                        }
                    });
                    checkBox_osobi_je_pozlilo_jako.setEnabled(true);
                    checkBox_osobi_je_pozlilo_jako.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                            if(isChecked){

                                checkBox_osobi_je_pozlilo_jako.setChecked(true);
                                checkBox_osobi_je_pozlilo_srednje.setChecked(false);
                                checkBox_osobi_je_pozlilo_malo.setChecked(false);
                                checkBox_osobi_je_pozlilo_bez_svijesti.setChecked(false);

                            }
                            if(!isChecked){

                                checkBox_osobi_je_pozlilo_jako.setChecked(false);
                            };
                        }
                    });

                    checkBox_osobi_je_pozlilo_bez_svijesti.setEnabled(true);
                    checkBox_osobi_je_pozlilo_bez_svijesti.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                            if(isChecked){

                                checkBox_osobi_je_pozlilo_bez_svijesti.setChecked(true);
                                checkBox_osobi_je_pozlilo_srednje.setChecked(false);
                                checkBox_osobi_je_pozlilo_jako.setChecked(false);
                                checkBox_osobi_je_pozlilo_malo.setChecked(false);

                            }
                            if(!isChecked){

                                checkBox_osobi_je_pozlilo_bez_svijesti.setChecked(false);
                            }
                        }
                    });

                    checkBox_prometna_nesreca.setChecked(false);
                    checkBox_pozar.setChecked(false);
                    checkBox_ubojstvo.setChecked(false);
                    checkBox_teroristicki_napad.setChecked(false);
                    checkBox_razbojstvo.setChecked(false);
                    checkBox_drugo.setChecked(false);


                }
                else if(!isChecked){

                    checkBox_osobi_je_pozlilo_malo.setEnabled(false);
                    checkBox_osobi_je_pozlilo_srednje.setEnabled(false);
                    checkBox_osobi_je_pozlilo_jako.setEnabled(false);
                    checkBox_osobi_je_pozlilo_bez_svijesti.setEnabled(false);

                    checkBox_osobi_je_pozlilo_malo.setChecked(false);
                    checkBox_osobi_je_pozlilo_srednje.setChecked(false);
                    checkBox_osobi_je_pozlilo_jako.setChecked(false);
                    checkBox_osobi_je_pozlilo_bez_svijesti.setChecked(false);

                    provjera_ispisa = 0;


                }
            }
        });

        checkBox_drugo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){

                    editText_drugo.setEnabled(true);

                    checkBox_prometna_nesreca.setChecked(false);
                    checkBox_pozar.setChecked(false);
                    checkBox_ubojstvo.setChecked(false);
                    checkBox_teroristicki_napad.setChecked(false);
                    checkBox_razbojstvo.setChecked(false);
                    checkBox_osobi_je_pozlilo.setChecked(false);

                }
                else if(!isChecked){

                    editText_drugo.setEnabled(false);
                    editText_drugo.setText("");

                    provjera_ispisa = 0;
                }
            }
        });

        //SNIMANJE FOTOGRAFIJA
        button_snimi_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            //TU BI MOŽDA TREBALO DODAT PROVJERU PERMISSON ZA EXTERNAL STORAGE
                File f = new File(Environment.getExternalStorageDirectory() + "/Poziv112");
                if(!f.exists()){

                    File dir = new File(path_folder);
                    dir.mkdir();

                }
                Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File file = getFile();
                camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                startActivityForResult(camera_intent,CAM_REQUEST);

            }
        });


        //SLANJE PODATAKA
        button_posalji_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkBox_prometna_nesreca.isChecked()){

                    vrsta_nesrece = "PROMETNA NESRECA";

                    if(checkBox_prometna_nesreca_zapaljenje.isChecked()){

                        podvrsta_nesrece = "DOSLO JE DO ZAPALJENJA VOZILA";

                    }
                    else{

                        podvrsta_nesrece = "NEMA INFORMACIJA";
                    }

                    if(checkBox_prometna_nesreca_ozlijedeni.isChecked()){

                        if(editText_prometna_nesreca_ozlijedeni.getText().toString().trim().equals("")){

                            Toast.makeText(getApplicationContext(),"Polje za unos količine ozljeđenih ne smije biti prazno", Toast.LENGTH_SHORT).show();
                            provjera_ispisa = 0;

                        }
                        else{

                            provjera_ispisa = 1;
                            ozlijedeni = editText_prometna_nesreca_ozlijedeni.getText().toString();

                        }
                    }else{

                        ozlijedeni = "NEMA INFORMACIJA";
                    }

                    if(editText_prometna_nesreca_koliko_vozila.getText().toString().trim().equals("")){

                        Toast.makeText(getApplicationContext(),"Polje za unos količine vozila ne smije biti prazno", Toast.LENGTH_SHORT).show();
                        provjera_ispisa = 0;

                    }
                    else{


                        provjera_ispisa = 1;
                        vozila =  editText_prometna_nesreca_koliko_vozila.getText().toString();

                    }

                }

                if(checkBox_pozar.isChecked()){

                    vrsta_nesrece = "POZAR";
                    provjera_ispisa = 0;


                    if(checkBox_pozar_objekta.isChecked()){

                        podvrsta_nesrece = "POZAR OBJEKTA";
                        provjera_ispisa = 1;
                    }
                    if(checkBox_pozar_otovrenog_prostora.isChecked()){

                        podvrsta_nesrece = "POZAR OTVORENOG PROSTORA";
                        provjera_ispisa = 1;
                    }
                    if(checkBox_pozar_vozila.isChecked()){

                        podvrsta_nesrece = "POZAR VOZILA";
                        provjera_ispisa = 1;
                    }
                    if(checkBox_pozar_eksplozija.isChecked()){

                        podvrsta_nesrece = "EKSPLOZIJA";
                        provjera_ispisa = 1;
                    }

                    if(checkBox_pozar_ozlijedeni.isChecked()){

                        if(editText_pozar_ozlijedeni.getText().toString().trim().equals("")){

                            Toast.makeText(getApplicationContext(),"Polje za unos količine ozljeđenih ne smije biti prazno", Toast.LENGTH_SHORT).show();
                            provjera_ispisa = 0;
                        }
                        else{

                            ozlijedeni = editText_pozar_ozlijedeni.getText().toString();
                            provjera_ispisa = 1;
                        }
                    }else{

                        ozlijedeni = "NEMA INFORMACIJA";
                    }


                    vozila = "NEMA INFORMACIJA";


                    if(provjera_ispisa == 0){

                        Toast.makeText(getApplicationContext(),"Mora biti odabrana vrsta požara", Toast.LENGTH_SHORT).show();
                    }


                }

                if(checkBox_ubojstvo.isChecked()){

                    vrsta_nesrece = "UBOJSTVO";
                    provjera_ispisa = 1;
                    podvrsta_nesrece = "NEMA INFORMACIJA";
                    ozlijedeni = "NEMA INFORMACIJA";
                    vozila = "NEMA INFORMACIJA";

                }

                if(checkBox_teroristicki_napad.isChecked()){

                    vrsta_nesrece = "TERORISTICKI NAPAD";
                    provjera_ispisa = 1;
                    podvrsta_nesrece = "NEMA INFROMACIJA";
                    ozlijedeni = "NEMA INFORMACIJA";
                    vozila = "NEMA INFORMACIJA";

                }

                if(checkBox_razbojstvo.isChecked()){

                    vrsta_nesrece = "RAZBOJSTVO";
                    provjera_ispisa = 0;

                    if(checkBox_razbojstvo_osobe.isChecked()){

                        podvrsta_nesrece = "RAZBOJSTVO OSOBE";
                        provjera_ispisa = 1;
                    }
                    if(checkBox_razbojstvo_objekta.isChecked()){

                        podvrsta_nesrece = "RAZBOJSTVO OBJEKTA";
                        provjera_ispisa = 1;
                    }
                    if(checkBox_razbojstvo_vozila.isChecked()){


                        podvrsta_nesrece = "RAZBOJSTVO VOZILA";
                        provjera_ispisa = 1;
                    }

                    if(checkBox_razbojstvo_ozlijedeni.isChecked()){

                        if(editText_razbojstvo_ozlijedeni.getText().toString().trim().equals("")){

                            Toast.makeText(getApplicationContext(),"Polje za unos količine ozljeđenih ne smije biti prazno", Toast.LENGTH_SHORT).show();
                            provjera_ispisa = 0;

                        }
                        else{

                            ozlijedeni = editText_razbojstvo_ozlijedeni.getText().toString();
                            provjera_ispisa = 1;

                        }
                    }
                    else{
                        ozlijedeni = "NEMA INFORMACIJA";
                    }

                    vozila = "NEMA INFORMACIJA";


                    if(provjera_ispisa == 0){

                        Toast.makeText(getApplicationContext(),"Mora biti odabrana vrsta razbojstva", Toast.LENGTH_SHORT).show();
                    }

                }

                if(checkBox_osobi_je_pozlilo.isChecked()){

                    vrsta_nesrece = "OSOBI JE POZLILO";
                    provjera_ispisa = 0;

                    if(checkBox_osobi_je_pozlilo_malo.isChecked()){


                        podvrsta_nesrece = "MALO";
                        provjera_ispisa = 1;
                    }
                    else if(checkBox_osobi_je_pozlilo_srednje.isChecked()){

                        podvrsta_nesrece = "SREDNJE";
                        provjera_ispisa = 1;
                    }
                    else if(checkBox_osobi_je_pozlilo_jako.isChecked()){


                        podvrsta_nesrece = "JAKO";
                        provjera_ispisa = 1;
                    }
                    else if(checkBox_osobi_je_pozlilo_bez_svijesti.isChecked()){

                        podvrsta_nesrece = "OSOBA JE BEZ SVIJESTI";
                        provjera_ispisa = 1;
                    }

                    if(provjera_ispisa == 0){

                        Toast.makeText(getApplicationContext(),"Mora biti odabran status osobe", Toast.LENGTH_SHORT).show();
                    }

                    ozlijedeni = "NEMA INFORMACIJA";
                    vozila = "NEMA INFORMACIJA";

                }

                if(checkBox_drugo.isChecked()){

                    vrsta_nesrece = "DRUGO - OSOBNI OPIS";
                    provjera_ispisa = 0;

                    if(editText_drugo.getText().toString().trim().equals("")){

                        Toast.makeText(getApplicationContext(),"Polje za opis nesreće ne smije biti prazno", Toast.LENGTH_SHORT).show();
                        provjera_ispisa = 0;

                    }
                    else{

                        podvrsta_nesrece = editText_drugo.getText().toString();
                        provjera_ispisa = 1;

                    }
                    ozlijedeni = "NEMA INFORMACIJA";
                    vozila = "NEMA INFORMACIJA";

                }
                if(!checkBox_prometna_nesreca.isChecked() && !checkBox_pozar.isChecked() && !checkBox_ubojstvo.isChecked() &&
                        !checkBox_teroristicki_napad.isChecked() && !checkBox_razbojstvo.isChecked() && !checkBox_osobi_je_pozlilo.isChecked() &&
                        !checkBox_drugo.isChecked()){

                    Toast.makeText(getApplicationContext(), "MORA BITI ODABRANA JEDNA VRSTA NESREĆE", Toast.LENGTH_SHORT).show();
                }
                else if(provjera_ispisa == 1) {

                    new ProvjeraVeze().execute();
                    //onDestroy(); //Zaustavlja GPS service da spriječi memory leak
                }

            }
        });

    }


    public void odustani(View view) {

        finish();
    }

    public void pokretanjeGPS(){

        lokacija = new Intent(getApplicationContext(),GPS_Service.class);
        startService(lokacija);
        lokacija_radi = true;
        Log.i("GPS","doslo");

    }
    // ZA GPS
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
                    Log.i("longitude",geo_duzina);
                    Log.i("latitude",geo_sirina);
                }
            };
        }
        registerReceiver(broadcastReceiver,new IntentFilter("location_update"));
    }


    protected void onDestroy() {
        super.onDestroy();
        Log.d("event","onDestroy was Called!");
        /*
        if(broadcastReceiver != null) {
            Log.i("ON DESTROY", "Uslo je u if petlju");

            unregisterReceiver(broadcastReceiver);
        } */
    }


    protected void onPause() {
        super.onPause();

        if(broadcastReceiver != null) {
            Log.i("ON DESTROY", "Uslo je u if petlju");

            unregisterReceiver(broadcastReceiver);
        }

    }

    //Kreiranje fotografije
    public File getFile(){

            Date d = new Date();
            CharSequence  s = DateFormat.format("yyyy-MM-dd-hh-mm-ss", d.getTime());;

            naziv_foto = s.toString();
            Log.i("naziv_foto",naziv_foto);

            File image_file = new File(path_folder, naziv_foto + ".jpg");

            provjera_foto  = 1;

            return image_file;

    }

    private String getMimeType(String path) {

        String extension = MimeTypeMap.getFileExtensionFromUrl(path);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }


    public class LongOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(final String... params) {

            Thread t= new Thread(new Runnable() {
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
                            .url("http://" + adresa_servera + "/poziv112/slanje_podataka.php")
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

        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {


        }
    }

    public class ProvjeraVeze extends AsyncTask<String, Void, String> {

        protected String doInBackground(final String... params) {

            try{
                Log.i("URL","http://"+adresa_servera);
                URL myUrl = new URL("http://"+ adresa_servera);
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

                if (!checkBox_anonimno_slanje.isChecked()) {

                    StringRequest request = new StringRequest(com.android.volley.Request.Method.POST, "http://" + adresa_servera + "/poziv112/insertDojava.php", new com.android.volley.Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                        }
                    }, new com.android.volley.Response.ErrorListener() {
                        public void onErrorResponse(VolleyError error) {

                        }
                    }) {

                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> parameters = new HashMap<String, String>();
                            parameters.put("ime", sharedPreferences.getString("ime", "NEMA INFORMACIJA"));
                            parameters.put("prezime", sharedPreferences.getString("prezime", "NEMA INFORMACIJA"));
                            parameters.put("dat_rod", sharedPreferences.getString("datRod", "0000/00/00"));
                            parameters.put("kg", sharedPreferences.getString("krvnaGrupa", "NEMA INFORMACIJA"));
                            parameters.put("mobitel", sharedPreferences.getString("brojMob", "NEMA INFORMACIJA"));
                            parameters.put("mobitelICE", sharedPreferences.getString("brojMobICE", "NEMA INFORMACIJA"));
                            parameters.put("vrsta_nesrece", vrsta_nesrece);
                            parameters.put("podvrsta_nesrece", podvrsta_nesrece);
                            parameters.put("ozlijedeni", ozlijedeni);
                            parameters.put("vozila", vozila);
                            parameters.put("gps_duzina", geo_duzina);
                            parameters.put("gps_sirina", geo_sirina);
                            parameters.put("foto", naziv_foto + ".jpg");

                            return parameters;
                        }

                    };

                    requestQueue.add(request);

                }else if (checkBox_anonimno_slanje.isChecked()) {

                    StringRequest request = new StringRequest(com.android.volley.Request.Method.POST, "http://" + adresa_servera + "/insertDojava.php", new com.android.volley.Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                        }
                    }, new com.android.volley.Response.ErrorListener() {
                        public void onErrorResponse(VolleyError error) {

                        }
                    }) {

                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> parameters = new HashMap<String, String>();
                            parameters.put("ime", "ANONIMNO");
                            parameters.put("prezime", "ANONIMNO");
                            parameters.put("dat_rod", "0000/00/00");
                            parameters.put("kg", "00");
                            parameters.put("mobitel", "ANONIMNO");
                            parameters.put("mobitelICE", "ANONIMNO");
                            parameters.put("vrsta_nesrece", vrsta_nesrece);
                            parameters.put("podvrsta_nesrece", podvrsta_nesrece);
                            parameters.put("ozlijedeni", ozlijedeni);
                            parameters.put("vozila", vozila);
                            parameters.put("gps_duzina", geo_duzina);
                            parameters.put("gps_sirina", geo_sirina);
                            parameters.put("foto", naziv_foto + ".jpg");

                            return parameters;
                        }

                    };

                    requestQueue.add(request);
                }

                Log.i("provjera_foto",String.valueOf(provjera_foto));
                Log.i("naziv_foto",naziv_foto);

                if (provjera_foto == 1) {

                    Log.i("SLANJE FOTO","USLO U IF");

                    new LongOperation().execute(naziv_foto+".jpg");


                }

                Toast.makeText(getApplicationContext(), "Dojava poslana", Toast.LENGTH_SHORT).show();

                progressDialog.dismiss();

                finish();


            }else {

                Toast.makeText(getApplicationContext(), "Greška poslužitelja", Toast.LENGTH_SHORT).show();

                progressDialog.dismiss();
            }

        }

        protected void onPreExecute() {

            progressDialog = new ProgressDialog(Poziv112Activity.this);
            progressDialog.setMessage("Šaljem...");
            progressDialog.setTitle("Slanje podataka");
            progressDialog.show();
        }


    }



}

/*
<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< KRAJ KORISNOG KODA >>>>>>>>>>>>>>>>>>>>>>>>>>>
 */

/*
    SVE ONO ŠTO SI KORISTIL ALI SI NAŠAL ZAMJENU

    int provjera_slanja = 0;

    GPS LOKACIJA PREKO DRETVE - NE VRAĆA LOKACIJU
    // new Lokacija().execute();

    public class Lokacija extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {

            Thread t = new Thread(new Runnable() {

                public void run() {
                    LocationManager locationManager;
                    LocationListener listener;

                    //Lokacija
                    locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

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
                            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                                    , 10);
                        }
                    }
                    Looper.prepare();
                    try {
                     //   locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, listener);
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, listener);
                    } catch (Exception e) {
                        e.printStackTrace();
                       // locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, listener);

                    }

                    Log.i("geoDuzina",geo_duzina);
                    Log.i("geoSirina",geo_sirina);
                }});
                    t.start();

            return null;
        }

        protected void onPostExecute(String result) {
            Toast.makeText(Poziv112Activity.this, "GPS lokacija pronađena", Toast.LENGTH_SHORT).show();



        }

    }

    SPREMANJE PODATAKA O DOJAVI U TXT FILE

    String info_nesreca = "";
    File file_nesreca = new File(path_folder + "/info_nesreca.txt");

    info_nesreca = "PROMETNA NESRECA\t";
    info_nesreca += "DOSLO JE DO ZAPALJENJA VOZILA\t";
    info_nesreca += "NEMA INFORMACIJA\t";
    info_nesreca += editText_prometna_nesreca_ozlijedeni.getText().toString() + "\t";
    info_nesreca += editText_prometna_nesreca_koliko_vozila.getText().toString();
    info_nesreca = "POZAR\t";
    info_nesreca += "POZAR OBJEKTA\t";
    info_nesreca += "POZAR OTVORENOG PROSTORA\t";
    info_nesreca += "EKSPLOZIJA\t";
    info_nesreca += editText_pozar_ozlijedeni.getText().toString() + "\t";
    info_nesreca += "NEMA INFORMACIJA\t";

    info_nesreca += "NEMA INFORMACIJA\t"; // nema info za vozila jer nema unosa za vozila

    info_nesreca = "UBOJSTVO\t";
    info_nesreca +="NEMA INFROMACIJA\t"; //nema info za podvrstu
    info_nesreca +="NEMA INFROMACIJA\t"; //nema info za ozlijedene
    info_nesreca +="NEMA INFROMACIJA"; //nema info za vozila
    info_nesreca = "TERORISTICKI NAPAD\t";
    info_nesreca +="NEMA INFROMACIJA\t"; //nema info za podvrstu
    info_nesreca +="NEMA INFROMACIJA\t"; //nema info za ozlijedene
    info_nesreca +="NEMA INFROMACIJA"; //nema info za vozila
    info_nesreca = "RAZBOJSTVO\t";
    info_nesreca += "RAZBOJSTVO OSOBE\t";
    info_nesreca += "RAZBOJSTVO OBJEKTA\t";
    info_nesreca += "RAZBOJSTVO VOZILA\t";
    info_nesreca += editText_razbojstvo_ozlijedeni.getText().toString() + "\t";
    info_nesreca += "NEMA INFORMACIJA\t";
    info_nesreca += "NEMA INFORMACIJA"; //nema info za vozila
    info_nesreca = "OSOBI JE POZLILO\t";
    info_nesreca += "MALO\t";
    info_nesreca += "SREDNJE\t";
    info_nesreca += "JAKO\t";
    info_nesreca += "OSOBA JE BEZ SVIJESTI\t";
    info_nesreca += "NEMA INFORMACIJA\t"; //NEMA info za ozlijedene
    info_nesreca += "NEMA INFORMACIJA"; //nema info za vozila
    info_nesreca = "DRUGO - OSOBNI OPIS:\t";
    info_nesreca += editText_drugo.getText().toString() + "\t";
    info_nesreca += "NEMA INFORMACIJA\t"; //NEMA info za ozlijedene
    info_nesreca += "NEMA INFORMACIJA\t"; //NEMA info za vozila

      //Spremanje u txt file [ZA SADA NAJBOLJA VERZIJA]
    public static void spremiPodatke(File file, String data)
    {
        FileOutputStream fos = null;
        try
        {
            fos = new FileOutputStream(file);
        }
        catch (FileNotFoundException e) {e.printStackTrace();}
        try
        {
            try
            {
                for (int i = 0; i<data.length(); i++)
                {
                    fos.write(data.charAt(i));
                }
            }
            catch (IOException e) {e.printStackTrace();}
        }
        finally
        {
            try
            {

                fos.close();

            }
            catch (IOException e) {e.printStackTrace();}
        }
    }

    // new LongOperation().execute("info_nesreca.txt");

    NEKAKVA FUNKCIJA ZA SLANJE PODATAKA
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


    SLANJE PODATAKA - BILO UNUTAR IF(provjera_ispisa == 1) - SADA SI SVE STAVIO KROZ ProvjeraVeze() klasu

    if(!checkBox_anonimno_slanje.isChecked()){

                        StringRequest request = new StringRequest(com.android.volley.Request.Method.POST, "http://"+adresa_servera+"/poziv112/insertDojava.php", new com.android.volley.Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                            }
                        }, new com.android.volley.Response.ErrorListener(){
                            public void onErrorResponse(VolleyError error){

                            }
                        }){

                            protected Map<String,String> getParams() throws AuthFailureError{
                                Map<String,String> parameters = new HashMap<String, String>();
                                parameters.put("ime",sharedPreferences.getString("ime","NEMA INFORMACIJA"));
                                parameters.put("prezime",sharedPreferences.getString("prezime","NEMA INFORMACIJA"));
                                parameters.put("dat_rod",sharedPreferences.getString("datRod","0000/00/00"));
                                parameters.put("kg",sharedPreferences.getString("krvnaGrupa","NEMA INFORMACIJA"));
                                parameters.put("mobitel",sharedPreferences.getString("brojMob","NEMA INFORMACIJA"));
                                parameters.put("mobitelICE",sharedPreferences.getString("brojMobICE","NEMA INFORMACIJA"));
                                parameters.put("vrsta_nesrece",vrsta_nesrece);
                                parameters.put("podvrsta_nesrece",podvrsta_nesrece);
                                parameters.put("ozlijedeni",ozlijedeni);
                                parameters.put("vozila",vozila);
                                parameters.put("gps_duzina",geo_duzina);
                                parameters.put("gps_sirina",geo_sirina);
                                parameters.put("foto",naziv_foto+".jpg");

                                return parameters;
                            }

                        };

                        requestQueue.add(request);



                    }else if(checkBox_anonimno_slanje.isChecked()){

                        StringRequest request = new StringRequest(com.android.volley.Request.Method.POST, "http://"+adresa_servera+"/insertDojava.php", new com.android.volley.Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                            }
                        }, new com.android.volley.Response.ErrorListener(){
                            public void onErrorResponse(VolleyError error){

                            }
                        }){

                            protected Map<String,String> getParams() throws AuthFailureError{
                                Map<String,String> parameters = new HashMap<String, String>();
                                parameters.put("ime","ANONIMNO");
                                parameters.put("prezime","ANONIMNO");
                                parameters.put("dat_rod","0000/00/00");
                                parameters.put("kg","00");
                                parameters.put("mobitel","ANONIMNO");
                                parameters.put("mobitelICE","ANONIMNO");
                                parameters.put("vrsta_nesrece",vrsta_nesrece);
                                parameters.put("podvrsta_nesrece",podvrsta_nesrece);
                                parameters.put("ozlijedeni",ozlijedeni);
                                parameters.put("vozila",vozila);
                                parameters.put("gps_duzina",geo_duzina);
                                parameters.put("gps_sirina",geo_sirina);
                                parameters.put("foto",naziv_foto+".jpg");

                                return parameters;
                            }

                        };

                        requestQueue.add(request);


                    }

                    if(provjera_foto == 1){
                        new LongOperation().execute(naziv_foto+".jpg");
                    }

                    Toast.makeText(getApplicationContext(),"DOJAVA POSLANA",Toast.LENGTH_SHORT).show();
 */
