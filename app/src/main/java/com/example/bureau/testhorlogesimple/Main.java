package com.example.bureau.testhorlogesimple;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class Main extends AppCompatActivity {
    ImageView AiguilleTest;
    int positAigInt;
    int PersonneChoisie;
    public static final String MY_PREF="mesPrefs";
    SharedPreferences positionLast;
    SharedPreferences positionGps;
    SharedPreferences notifsPref;
    SharedPreferences Aiguilles;
    String positNew = "1";
    String positGps="0000";
    String SENT = "SMS_SENT";
    String DELIVERED = "SMS_DELIVERED";
    BroadcastReceiver sentBroadcast;
    BroadcastReceiver deliveredBroadcast;
    IntentFilter sentIntentFilter;
    IntentFilter deliveredIntentFilter;
    final String[] Personnes ={"Maman","Papa","Marie","Louis","Camille","Perrine","Mathilde","Défaut"};
    int[] AiguilleID ={R.mipmap.aig_maman,R.mipmap.aig_papa,R.mipmap.aig_marie,R.mipmap.aig_louis,R.mipmap.aig_camille,R.mipmap.aig_perrine,R.mipmap.aig_mathilde,R.mipmap.aig_defaut};

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(sentBroadcast, sentIntentFilter);
        registerReceiver(deliveredBroadcast, deliveredIntentFilter);
    }
    @Override
    protected void onPause() {
        if(deliveredBroadcast!=null) {
            unregisterReceiver(deliveredBroadcast);
            deliveredBroadcast=null;
        }
        if (sentBroadcast!=null) {
            unregisterReceiver(sentBroadcast);
            sentBroadcast=null;
        }
        super.onPause();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sentIntentFilter=new IntentFilter(SENT);
        deliveredIntentFilter=new IntentFilter(DELIVERED);
        sentBroadcast=new sentReceiver();
        deliveredBroadcast=new deliveredReceiver();

        setContentView(R.layout.activity_main);
        positionLast=getSharedPreferences(MY_PREF,Context.MODE_PRIVATE);
        notifsPref=getSharedPreferences("notifsPref",Context.MODE_PRIVATE);
        positionGps=getSharedPreferences("positionGps",Context.MODE_PRIVATE);
        Aiguilles=getSharedPreferences("Personne",Context.MODE_PRIVATE);
        String positLast=positionLast.getString("Position","1");
        Boolean positFamilleGps=positionGps.getBoolean("Famille",false);
        Boolean positTravailGps=positionGps.getBoolean("Travail",false);
        Boolean positJokerGps=positionGps.getBoolean("Joker",false);
        Boolean positMaisonGps=positionGps.getBoolean("A la maison",false);
        int AiguillePersonne=Aiguilles.getInt("Personne",8);
        AiguilleTest = findViewById(R.id.Aiguille);
        AiguilleTest.setImageResource(AiguilleID[AiguillePersonne]);

        positGps = BooleantoString(positFamilleGps)+BooleantoString(positTravailGps)+BooleantoString(positJokerGps)+BooleantoString(positMaisonGps);
        switch (positGps){
            case "0000":
                positNew="4";
                break;
            case "1000":
                positNew="1";
                break;
            case "0100":
            case "1100":
                positNew="2";
                break;
            case "0010":
                positNew="5";
                break;
            case "0001":
                positNew="7";
                break;
            default:
                positNew=positLast;
                Toast.makeText(getBaseContext(), "Configuration des zones non pris en charge. Veuillez vérifier la disposition des zones GPS.", Toast.LENGTH_LONG).show();
                break;
        }

        try {
            positAigInt = Integer.parseInt(positNew);
        } catch(NumberFormatException nfe) {
        }
        AiguilleTest.setRotation(positAigInt*40+320);
        SendSMS(positNew);
    }

    public void setPosit(String s) {
        try {
            positAigInt = Integer.parseInt(s);
        } catch(NumberFormatException nfe) {
        }
        AiguilleTest.setRotation(positAigInt*40+320);
        SendSMS(s);
    }


    //Ensemble des fonctions lors d'un appui sur un des boutons
    public void Famille (View view){
        setPosit("1");
    }
    public void Travail (View view){
        setPosit("2");
    }
    public void Voyage (View view){
        setPosit("3");
    }
    public void Dehors (View view){
        setPosit("4");
    }
    public void Joker (View view){
        setPosit("5");
    }
    public void PenseAVous (View view){
        setPosit("6");
    }
    public void ALaMaison (View view){
        setPosit("7");
    }
    public void PasDeNouvelles (View view){
        setPosit("8");

    }
    public void VeuxRentrer (View view){
        setPosit("9");
    }

    private void SendSMS(final String x) {
        if (Integer.parseInt(positionLast.getString("Position", "1")) != Integer.parseInt(x)) {

            PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
                    new Intent(SENT), 0);

            PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                    new Intent(DELIVERED), 0);
            SharedPreferences.Editor editorPosi = positionLast.edit();
            // Storing the latitude for the i-th location
            editorPosi.putString("Position", x);
            editorPosi.commit();
            //---when the SMS has been sent--
            android.telephony.SmsManager sms = android.telephony.SmsManager.getDefault();
            sms.sendTextMessage("+36769424262", null, x, sentPI, deliveredPI);
        }
    }//Fonction d'envoi des sms

    public class sentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    Toast.makeText(getBaseContext(), "SMS sent",
                            Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    Toast.makeText(getBaseContext(), "Generic failure",
                            Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    Toast.makeText(getBaseContext(), "No service",
                            Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    Toast.makeText(getBaseContext(), "Null PDU",
                            Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    Toast.makeText(getBaseContext(), "Radio off",
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    public class deliveredReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    Toast.makeText(getBaseContext(), "SMS delivered",
                            Toast.LENGTH_SHORT).show();
                    break;
                case Activity.RESULT_CANCELED:
                    Toast.makeText(getBaseContext(), "SMS not delivered",
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    public static String BooleantoString(boolean b) {
        return b ? "1" : "0";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        //Set the notifications menu item checked corresponding preferences
        Boolean notifDisplay=notifsPref.getBoolean("Notifs",true);
        menu.findItem(R.id.action_bar_notifs).setChecked(notifDisplay);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_gps:
                startActivity(new Intent(Main.this, MapsActivity.class));
                return true;
            case R.id.action_infos:
                Toast.makeText(getBaseContext(), "Créé par Louis Le Nézet", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_bar_notifs:
                SharedPreferences.Editor notifsPrefEditor=notifsPref.edit();
                if(item.isChecked()){
                    Toast.makeText(getBaseContext(), "Notifications désactivées", Toast.LENGTH_SHORT).show();
                    item.setChecked(false);
                    notifsPrefEditor.putBoolean("Notifs",false);
                }else{
                    Toast.makeText(getBaseContext(), "Notifications activées", Toast.LENGTH_SHORT).show();
                    item.setChecked(true);
                    notifsPrefEditor.putBoolean("Notifs",true);
                }
                notifsPrefEditor.commit();
                return true;
            case R.id.action_aiguille:
                AlertDialog.Builder builder = new AlertDialog.Builder(Main.this);
                builder.setTitle("Choisissez l'aiguille à afficher:");
                builder.setSingleChoiceItems(Personnes, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Get location selected
                        PersonneChoisie = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        // Opening the editor object to delete data from sharedPreferences
                        SharedPreferences.Editor AiguillesEditor = Aiguilles.edit();
                        // Delete position on zone from sharedPreference
                        AiguillesEditor.putInt("Personne",PersonneChoisie);
                        // Committing the changes
                        AiguillesEditor.commit();
                        // Restart activity
                        recreate();
                    }
                });
                builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    protected void onDestroy(){
        SharedPreferences.Editor editorGPS = positionGps.edit();
        editorGPS.putBoolean("Famille", false);
        editorGPS.putBoolean("Travail", false);
        editorGPS.putBoolean("Joker", false);
        editorGPS.putBoolean("A la maison", false);
        editorGPS.commit();

        super.onDestroy();
    }

}
