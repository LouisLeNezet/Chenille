package com.example.bureau.testhorlogesimple;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;



public class ProximityReceiver extends BroadcastReceiver {
    SharedPreferences positionGps;
    SharedPreferences notifsPref;
    String notificationTitle;
    String notificationContent;
    String tickerMessage;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent background = new Intent(context,BackGroundService.class);
        context.startService(background);
        // TODO Auto-generated method stub
        Log.d("Intent1","Intent received");
        positionGps=context.getSharedPreferences("positionGps",context.MODE_PRIVATE);
        notifsPref=context.getSharedPreferences("notifsPref",Context.MODE_PRIVATE);
        SharedPreferences.Editor positionGpsEditor = positionGps.edit();
        Boolean notifDisplay=notifsPref.getBoolean("Notifs",true);

        Boolean proximity_entering = intent.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, false);
        String proximity_name = intent.getStringExtra("name");

        if(proximity_entering){
            Toast.makeText(context,"Entering the region"  ,Toast.LENGTH_LONG).show();
            notificationTitle="Proximity - Entry";
            notificationContent= proximity_name;
            tickerMessage = proximity_name;
            positionGpsEditor.putBoolean(proximity_name,true);
            positionGpsEditor.commit();
        }else{
            Toast.makeText(context,"Exiting the region"  ,Toast.LENGTH_LONG).show();
            notificationTitle="Proximity - Exit";
            notificationContent=proximity_name;
            tickerMessage = proximity_name;
            positionGpsEditor.putBoolean(proximity_name,false);
            positionGpsEditor.commit();
        }

        if(notifDisplay) {
            Intent notificationIntent = null;
            notificationIntent = new Intent(context, com.example.bureau.testhorlogesimple.NotificationView.class);
            notificationIntent.putExtra("content", notificationContent);

            /** This is needed to make this intent different from its previous intents */
            notificationIntent.setData(Uri.parse("tel:/" + (int) System.currentTimeMillis()));

            /** Creating different tasks for each notification. See the flag Intent.FLAG_ACTIVITY_NEW_TASK */
            PendingIntent pendingIntentNotif = PendingIntent.getActivity(context.getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            /** Getting the System service NotificationManager */
            NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            /** Configuring notification builder to create a notification */
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                    .setWhen(System.currentTimeMillis())
                    .setContentText(notificationContent)
                    .setContentTitle(notificationTitle)
                    .setSmallIcon(R.mipmap.chenillelogo)
                    .setAutoCancel(true)
                    .setTicker(tickerMessage)
                    .setContentIntent(pendingIntentNotif);

            /** Creating a notification from the notification builder */
            Notification notification = notificationBuilder.build();

            /** Sending the notification to system.
             * The first argument ensures that each notification is having a unique id
             * If two notifications share same notification id, then the last notification replaces the first notification
             * */
            nManager.notify((int) System.currentTimeMillis(), notification);
            context.startActivity(new Intent(context,Main.class));
        }
    }
}