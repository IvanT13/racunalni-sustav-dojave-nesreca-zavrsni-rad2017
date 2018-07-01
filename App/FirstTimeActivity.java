package projektutelematici.projekt112;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


public class FirstTimeActivity extends Activity {

    public int write_permission = 1;

    String PREFS_NAME = "MyPrefsFile"; //File u koji se sprema dali je app pokrenut prvi puta
    SharedPreferences settings;

    public static String PATH_FOLDER = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Poziv112";
    public String path_folder;

    private static final String userPrefs = "osobniPodaci";
    SharedPreferences sharedPreferences;

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
        setContentView(R.layout.first_time);


        path_folder = PATH_FOLDER;
        settings = getSharedPreferences(PREFS_NAME, 0);

        //Kontrole za vuc podatke
        editText_imeKorisnika = (EditText) findViewById(R.id.eT_ImeKorisnika);
        editText_prezimeKorisnika = (EditText) findViewById(R.id.eT_PrezimeKorisnika);
        spinner_krvna_grupa_slovo = (Spinner) findViewById(R.id.spinner_krvna_grupa);
        getSpinner_krvna_grupa_simbol = (Spinner) findViewById(R.id.spinner_krvna_grupa_simbol);
        editText_broj_mobitela_korisnik = (EditText) findViewById(R.id.et_brojMobitela);
        editText_broj_mobitela_ICE = (EditText) findViewById(R.id.et_brojICEMOB);
        datePicker = (DatePicker) findViewById(R.id.datePicker);

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

            Toast.makeText(getApplicationContext(),"Polje za unos imena ne smije biti prazno",Toast.LENGTH_SHORT).show();
            provjera_unos_ime = 0;
        }
        else if(editText_imeKorisnika.getText().toString().trim().matches("[0-9]+")){

            Toast.makeText(getApplicationContext(),"U polje za unos imena ne smiju biti upisani brojevi",Toast.LENGTH_SHORT).show();
            provjera_unos_ime = 0;
        }
        else{
            imeKorisnika = editText_imeKorisnika.getText().toString();
            provjera_unos_ime = 1;
        }

        if(String.valueOf(editText_prezimeKorisnika.getText()).trim().equals("")){

            Toast.makeText(getApplicationContext(),"Polje za unos prezimena ne smije biti prazno",Toast.LENGTH_SHORT).show();
            provjera_unos_prezime = 0;
        }
        else if(String.valueOf(editText_prezimeKorisnika.getText()).trim().matches("[0-9]+")){

            Toast.makeText(getApplicationContext(),"U polje za unos prezimena ne smiju biti upisani brojevi",Toast.LENGTH_SHORT).show();
            provjera_unos_prezime = 0;
        }
        else{

            prezimeKorisnika = String.valueOf(editText_prezimeKorisnika.getText());
            provjera_unos_prezime = 1;

        }

        datumRodjenja = String.valueOf(datePicker.getYear() + "/" +  Integer.valueOf(datePicker.getMonth()+1) + "/" + datePicker.getDayOfMonth());

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

            settings.edit().putBoolean("NISU_ spremljeni_podatci_o_korisniku",false).apply();

            finish();
        }

    }

/*
<<<<<<<<<<<< KRAJ KORISNOG KODA >>>>>>>>>>>>>>>>>>>>>>>>
 */

}
 /*STVARI KOJE SI KORISTIL ALI SI NAŠAO ZAMJENU

 SHARED PREFERENCES
 String PREFS_NAME;
 PREFS_NAME = extras.getString("SLANJE_PREFS_FILE");

 INTENT
  Intent intent = getIntent();
  Bundle extras = intent.getExtras();
CALENDARVIEW
 CalendarView calendarView;
 calendarView = (CalendarView)findViewById(R.id.calendarViewDatumRod);
 SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
 datumRodjenja = sdf.format(new Date(calendarView.getDate()));

 NEŠTO ZA SLANJE PUTA DO FOLDERA
 //path_folder = intent.getStringExtra(MainActivity.SLANJE_PUTA);

 SPREMANJE PODATAKA O KORISNKU U FILE
  File info_korisnika = new File(path_folder + "/info_korisnik.txt");

   ispis = imeKorisnika + "\t" + prezimeKorisnika + "\t" + datumRodjenja + "\t" + krvnaGrupaSlovo + krvnaGrupaSimbol + "\t" + brojMobitela + "\t" + brojMobitelaICE;

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