package com.example.moviles_06_notificaciones;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class PedidoBroadcast extends BroadcastReceiver {

    private static final int NOTIFICATION_ID = 1;
    private static final String PRIMARY_CHANNEL_ID = "channel_2";
    private static final String PRIMARY_CHANNEL_NAME = "channel_check";
    private NotificationManager notificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(MainActivity.ACTION_ORDER.equals(intent.getAction())) {
            Pedido order = new Pedido();
            order.setCodigo(intent.getStringExtra("code"));
            order.setDescripcion(intent.getStringExtra("description"));
            order.setPrecio(intent.getDoubleExtra("price", 0));
            showNotification(context, order);
            sendNotification(context, order);
        }
    }

    private void showNotification(Context context, Pedido order){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(order.getCodigo()).append(" - ").append(order.getDescripcion()).append(" - S/ ").append(order.getPrecio());
        Toast.makeText(context, stringBuilder, Toast.LENGTH_SHORT).show();
    }

    private void sendNotification(Context context, Pedido order){

        String titleNotify ="Su Pedido " + order.getCodigo() + " ha sido confirmado";
        String bodyNotify = order.getDescripcion() + " S/ " + order.getPrecio();

        //default notification sound
        Uri soundDefault = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID);
        notifyBuilder.setSmallIcon(R.drawable.ic_notifications_active_black_24dp);
        notifyBuilder.setContentTitle(titleNotify);
        notifyBuilder.setContentText(bodyNotify);
        notifyBuilder.setTicker(bodyNotify);
        notifyBuilder.setSound(soundDefault);
        notifyBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notifyBuilder.setAutoCancel(true);

        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            //Create Notification Channel
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID, PRIMARY_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notification from Checks");
            notificationManager.createNotificationChannel(notificationChannel);
        }
        assert notificationManager != null;
        notificationManager.notify(NOTIFICATION_ID, notifyBuilder.build());
    }
}
