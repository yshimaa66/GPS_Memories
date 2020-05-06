package com.example.gps_memories;
import android.app.AlarmManager;
import android.app.NotificationChannel ;
import android.app.NotificationManager ;
import android.app.PendingIntent;
import android.app.Service ;
import android.content.Context;
import android.content.Intent ;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler ;
import android.os.IBinder ;
import android.os.SystemClock;
import android.util.Log ;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.gps_memories.controller.Memory.View_MemoryActivity;
import com.example.gps_memories.model.Memory_Model;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.Timer ;
import java.util.TimerTask ;

import static com.example.gps_memories.controller.HomeActivity.currentuserid;
import static com.example.gps_memories.controller.HomeActivity.latLngg;

public class NotificationService extends Service {

    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    private final static String default_notification_channel_id = "default" ;
    Timer timer ;
    TimerTask timerTask ;
    String TAG = "Timers" ;
    int Your_X_SECS = 5 ;


    /*String currentuserid;
    LatLng latLng;

    public NotificationService(String currentuserid, LatLng latLng) {
        this.currentuserid = currentuserid;
        this.latLng = latLng;
    }*/

    @Override
    public IBinder onBind (Intent arg0) {
        return null;
    }
    @Override
    public int onStartCommand (Intent intent , int flags , int startId) {
        Log. e ( TAG , "onStartCommand" ) ;
        super .onStartCommand(intent , flags , startId) ;
        startTimer() ;
        return START_STICKY ;
    }
    @Override
    public void onCreate () {
        Log. e ( TAG , "onCreate" ) ;
    }
    @Override
    public void onDestroy () {
        Log. e ( TAG , "onDestroy" ) ;
        stopTimerTask() ;
        super .onDestroy() ;
    }
    //we are going to use a handler to be able to run in our TimerTask
    final Handler handler = new Handler() ;
    public void startTimer () {
        timer = new Timer() ;
        initializeTimerTask() ;
        timer .schedule( timerTask , 5000 , Your_X_SECS * 1000 ) ; //
    }
    public void stopTimerTask () {
        if ( timer != null ) {
            timer .cancel() ;
            timer = null;
        }
    }
    public void initializeTimerTask () {
        timerTask = new TimerTask() {
            public void run () {
                handler .post( new Runnable() {
                    public void run () {
                        createNotification() ;
                    }
                }) ;
            }
        } ;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent){

        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());

        Intent intent = new Intent("com.android.ServiceStopped");
        sendBroadcast(intent);

        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartServicePendingIntent);


        super.onTaskRemoved(rootIntent);
    }

    private void createNotification () {

       /* NotificationManager mNotificationManager = (NotificationManager) getSystemService( NOTIFICATION_SERVICE ) ;
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext() , default_notification_channel_id ) ;
        mBuilder.setContentTitle( "My Notification" ) ;
        mBuilder.setContentText( "Notification Listener Service Example" ) ;
        mBuilder.setTicker( "Notification Listener Service Example" ) ;
        mBuilder.setSmallIcon(R.drawable. ic_launcher_foreground ) ;
        mBuilder.setAutoCancel( true ) ;
        if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {
            int importance = NotificationManager. IMPORTANCE_HIGH ;
            NotificationChannel notificationChannel = new NotificationChannel( NOTIFICATION_CHANNEL_ID , "NOTIFICATION_CHANNEL_NAME" , importance) ;
            mBuilder.setChannelId( NOTIFICATION_CHANNEL_ID ) ;
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(notificationChannel) ;
        }
        assert mNotificationManager != null;
        mNotificationManager.notify(( int ) System. currentTimeMillis () , mBuilder.build()) ;


        */


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Memories");

        reference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Memory_Model memory_model = snapshot.getValue(Memory_Model.class);


                    if (memory_model != null && memory_model.getUserid().equals(currentuserid)) {

                        LatLng latLngo = new LatLng(memory_model.getLatitude(), memory_model.getLongitude());


                                if (Math.ceil(latLngo.latitude) == Math.ceil(latLngg.latitude)) {

                                    Intent intent;


                                    intent = new Intent(getApplicationContext(), View_MemoryActivity.class);
                                    intent.putExtra("memory_id",memory_model.getId());


                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,intent,PendingIntent.FLAG_ONE_SHOT);

                                    Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                                            .setSmallIcon(R.drawable.ic_marker)
                                            .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),
                                                    R.mipmap.ic_launcher))
                                            .setContentTitle("You have a memory here")
                                            .setContentText("Have a look to bring memories back")
                                            .setAutoCancel(true)
                                            .setSound(defaultSound)
                                            .setContentIntent(pendingIntent);

                                    NotificationManager noti = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);




                                    int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);

                                    assert noti != null;
                                    noti.notify(m,builder.build());


                                    currentuserid=null;
                                    latLngg=null;

                                    break;

                                }

                    }

                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });











    }



}
