package com.example.moviles_06_notificaciones;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private TextView tfCodigo;
    private TextView tfDescription;
    private TextView tfPrecio;
    private Button btnNotificar;


    private static final String PRIMARY_CHANNEL_ID = "channel_1";
    private static final String PRIMARY_CHANNEL_NAME = "channel_orders";
    private static final int NOTIFICATION_ID = 0;
    public static final String ACTION_ORDER = "action_order";

    private NotificationManager notificationManager;
    private Pedido pedido;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tfCodigo = findViewById(R.id.tfCodigo);
        tfDescription = findViewById(R.id.tfDescripcion);
        tfPrecio = findViewById(R.id.tfPrecio);

        btnNotificar = findViewById(R.id.btnBotificar);
        btnNotificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObtenerNotification();
            }
        });

        crearNotificacion();
    }

    public void ObtenerNotification(){
        String codigo = tfCodigo.getText().toString();
        String descripcion = tfDescription.getText().toString();
        String precio = tfPrecio.getText().toString();

        if(codigo.isEmpty()){
            Toast.makeText(MainActivity.this, "ingrese un código", Toast.LENGTH_SHORT).show();
        }else if(descripcion.isEmpty()){
            Toast.makeText(MainActivity.this, "ingrese una descripción", Toast.LENGTH_SHORT).show();
        }else if(precio.isEmpty()){
            Toast.makeText(MainActivity.this, "ingrese un precio", Toast.LENGTH_SHORT).show();
        }else{
            Double doubleprecio = Double.parseDouble(precio);
            Pedido mPedido = new Pedido();
            mPedido.setCodigo(codigo);
            mPedido.setDescripcion(descripcion);
            mPedido.setPrecio(doubleprecio);
            pedido = mPedido;
            EnviarNotificacion();
        }
    }


    private NotificationCompat.Builder getNotificationBuilder(){

        Intent intent = new Intent(this, PedidoBroadcast.class);
        intent.setAction(ACTION_ORDER);
        intent.putExtra("code", pedido.getCodigo());
        intent.putExtra("description", pedido.getDescripcion());
        intent.putExtra("price", pedido.getPrecio());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0, intent, 0);

        String bodyNotify = "Su código de Pedido es " + pedido.getCodigo();

        //custom notification sound
        Uri soundCustom = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getApplicationContext().getPackageName() + "/" + R.raw.notification);

        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID);
        notifyBuilder.setSmallIcon(R.drawable.ic_notifications_active_black_24dp);
        notifyBuilder.setContentTitle("¡Tiene un nuevo Pedido!");
        notifyBuilder.setContentText(bodyNotify);
        notifyBuilder.setTicker(bodyNotify);
        notifyBuilder.setSound(soundCustom);
        notifyBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notifyBuilder.setAutoCancel(true);
        notifyBuilder.setContentIntent(pendingIntent);
        notifyBuilder.addAction(0, "Confirmar", pendingIntent);
        return notifyBuilder;
    }


    public void crearNotificacion(){
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            //Create Notification Channel
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID, PRIMARY_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notification from Orders");
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    public void EnviarNotificacion(){
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
        notificationManager.notify(NOTIFICATION_ID, notifyBuilder.build());
    }

}
