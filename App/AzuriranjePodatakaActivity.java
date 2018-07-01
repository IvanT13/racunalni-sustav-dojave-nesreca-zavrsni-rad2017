package projektutelematici.projekt112;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Created by Ivan on 9.11.2016..
 */

public class AzuriranjePodatakaActivity extends Activity{

    private static final String userPrefs = "osobniPodaci";
    SharedPreferences sharedPreferences;

    public static String PATH_FOLDER = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Poziv112";
    public String path_folder;

    //Kontrole za povuc podatke
    EditText editText_imeKorisnika;
    EditText editText_prezimeKorisnika;
    Spinner spinner_krvna_grupa_slovo;
    Spinner getSpinner_krvna_grupa_simbol;
    EditText editText_broj_mobitela_korisnik;
    EditText editText_broj_mobitela_ICE;
    DatePicker datePicker;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.azuriraj_podatke);

        Intent intent = getIntent();
        path_folder = PATH_FOLDER;

        // Navigacijske kontrole

        //Kontrole za vuc podatke
        editText_imeKorisnika = (EditText)findViewById(R.id.eT_ImeKorisnika);
        editText_prezimeKorisnika = (EditText)findViewById(R.id.eT_PrezimeKorisnika);
        spinner_krvna_grupa_slovo = (Spinner)findViewById(R.id.spinner_krvna_grupa);
        getSpinner_krvna_grupa_simbol = (Spinner)findViewById(R.id.spinner_krvna_grupa_simbol);
        editText_broj_mobitela_korisnik = (EditText)findViewById(R.id.et_brojMobitela);
        editText_broj_mobitela_ICE = (EditText)findViewById(R.id.et_brojICEMOB);
        datePicker = (DatePicker)findViewById(R.id.datePicker);

        sharedPreferences = getSharedPreferences(userPrefs,MODE_PRIVATE);
        editText_imeKorisnika.setHint(sharedPreferences.getString("ime","Nema unosa"));
        editText_prezimeKorisnika.setHint(sharedPreferences.getString("prezime","Nema unosa"));
        editText_broj_mobitela_korisnik.setHint(sharedPreferences.getString("brojMob","Nema unosa"));
        editText_broj_mobitela_ICE.setHint(sharedPreferences.getString("brojMobICE","Nema unosa"));

    }

    public void spremiPodatke(View v) {

        String imeKorisnika = "";
        String prezimeKorisnika = "";
        String datumRodjenja = "";
        String krvnaGrupaSlovo = "";
        String krvnaGrupaSimbol = "";
        String krvnaGrupa = "";
        String brojMobitela = "";
        String brojMobitelaICE = "";
        int provjera_unos_ime = 0;
        int provjera_unos_prezime = 0;
        int provjera_unos_mob = 0;
        int provjera_unos_mob_ICE = 0;

        if(editText_imeKorisnika.getText().toString().trim().equals("")){

            Toast.makeText(getApplicationContext(),"Polje za unos imena ne smije biti prazno",Toast.LENGTH_LONG).show();
            provjera_unos_ime = 0;
        }
        else if(editText_imeKorisnika.getText().toString().trim().matches("[0-9]+$")){

            Toast.makeText(getApplicationContext(),"U polje za unos imena ne smiju biti upisani brojevi",Toast.LENGTH_LONG).show();
            provjera_unos_ime = 0;
        }
        else{
            imeKorisnika = editText_imeKorisnika.getText().toString();
            provjera_unos_ime = 1;
        }


        if(String.valueOf(editText_prezimeKorisnika.getText()).trim().equals("")){

            Toast.makeText(getApplicationContext(),"Polje za unos prezimena ne smije biti prazno",Toast.LENGTH_LONG).show();
            provjera_unos_prezime = 0;
        }
        else if(String.valueOf(editText_prezimeKorisnika.getText()).trim().matches("[0-9]+$")){

            Toast.makeText(getApplicationContext(),"U polje za unos prezimena ne smiju biti upisani brojevi",Toast.LENGTH_LONG).show();
            provjera_unos_prezime = 0;
        }
        else{

            prezimeKorisnika = String.valueOf(editText_prezimeKorisnika.getText());
            provjera_unos_prezime = 1;

        }

        datumRodjenja = String.valueOf(datePicker.getYear() + "/" +  Integer.valueOf(datePicker.getMonth()+1) + "/" + datePicker.getDayOfMonth());

        Log.i("datum rod",datumRodjenja);

        krvnaGrupaSlovo = spinner_krvna_grupa_slovo.getSelectedItem().toString();
        krvnaGrupaSimbol = getSpinner_krvna_grupa_simbol.getSelectedItem().toString();
        krvnaGrupa = krvnaGrupaSlovo + krvnaGrupaSimbol;

        if(String.valueOf(editText_broj_mobitela_korisnik.getText()).trim().equals("")){

            Toast.makeText(getApplicationContext(),"Polje za unos broja mobitela ne smije biti prazna",Toast.LENGTH_SHORT).show();
            provjera_unos_mob = 0;
        }
        else{

            brojMobitela = String.valueOf(editText_broj_mobitela_korisnik.getText());
            provjera_unos_mob = 1;

        }

        if(String.valueOf(editText_broj_mobitela_ICE.getText()).trim().equals("")){

            Toast.makeText(getApplicationContext(),"Polje za unos broja mobitela ICE ne smije biti prazna",Toast.LENGTH_SHORT).show();
            provjera_unos_mob_ICE = 0;
        }
        else{

            brojMobitelaICE = String.valueOf(editText_broj_mobitela_ICE.getText());
            provjera_unos_mob_ICE = 1;

        }

        if(provjera_unos_ime == 1 && provjera_unos_prezime == 1 && provjera_unos_mob == 1 && provjera_unos_mob_ICE == 1){ //Podatci ce se spremiti jedino ako su sve provjere zadovoljene

            sharedPreferences = getSharedPreferences(userPrefs,MODE_PRIVATE);
            sharedPreferences.edit().putString("ime",imeKorisnika).apply();
            sharedPreferences.edit().putString("prezime",prezimeKorisnika).apply();
            sharedPreferences.edit().putString("datRod",datumRodjenja).apply();
            sharedPreferences.edit().putString("krvnaGrupa",krvnaGrupa).apply();
            sharedPreferences.edit().putString("brojMob",brojMobitela).apply();
            sharedPreferences.edit().putString("brojMobICE",brojMobitelaICE).apply();

            Toast.makeText(getApplicationContext(),"Podaci spremljeni",Toast.LENGTH_SHORT).show();

            finish();
        }

    }

    public void odustani(View view){

        finish();
    }

}

/*
<<<<<<<<<<<<<<< KRAJ KORISNOG KODA >>>>>>>>>>>>>>
 */
/* STVARI KOJE SI KORISTIL ALI SI NAŠAL ZAMJENU
    CALENDARVIEW
    CalendarView calendarView;
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    datumRodjenja = sdf.format(new Date(calendarView.getDate()));
    //calendarView = (CalendarView)findViewById(R.id.calendarViewDatumRod);

    SPREMANJE U FILE
     String ispis;
     File info_korisnika = new File(path_folder + "/info_korisnik.txt");

      //ispis = imeKorisnika + "\t" + prezimeKorisnika + "\t" + datumRodjenja + "\t" + krvnaGrupaSlovo + krvnaGrupaSimbol + "\t" + brojMobitela + "\t" + brojMobitelaICE;

            ispis = "Ime: " + imeKorisnika + "\n" + "Prezime: " + prezimeKorisnika +
                    "\n" + "Datum rodjenja: " + datumRodjenja + "\n" + "Krvna grupa: " +
                    krvnaGrupaSlovo + " " + krvnaGrupaSimbol + "\n" + "Broj mob: " +
                    brojMobitela + "\n" + "Broj mob ICE: " + brojMobitelaICE;

            try {
                spremiPodatke(info_korisnika, ispis);
            }catch (NullPointerException npe){

                File dir = new File(path_folder);
                dir.mkdir();

                spremiPodatke(info_korisnika, ispis);
            }

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


 */
