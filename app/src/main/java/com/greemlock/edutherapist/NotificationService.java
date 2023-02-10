package com.greemlock.edutherapist;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;

public class NotificationService extends Service {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ArrayList<ObjectMessage> messages;
    ObjectMessage lastMessage;
    private NotificationCompat.Builder builder;
    int loopNumber;

    @Override
    public void onCreate() {

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("messages");
        messages = new ArrayList<>();
        lastMessage = new ObjectMessage();
        loopNumber = 0;
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    ObjectMessage objectMessage = dataSnapshot.getValue(ObjectMessage.class);
                    messages.add(objectMessage);
                }

                if(loopNumber > 0){
                    lastMessage = messages.get(messages.size()-1);
                    String name = SaveSharedPreferences.getPrefName(NotificationService.this);

                    if(!lastMessage.getMessage_name().equals(name)) {
                        bildirimGonder(lastMessage);
                        Log.e("isWorking","true");
                    }
                }
                loopNumber = loopNumber + 1;
                Log.e("loopNumber",String.valueOf(loopNumber));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void bildirimGonder(ObjectMessage message){
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(getApplicationContext(),getApplicationContext().getClass());
        intent.putExtra("message", (Serializable) message);
        intent.putExtra("isClicked",true);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),1,intent,PendingIntent.FLAG_IMMUTABLE);
        String contentTitle = "There is a message from %s!";

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            String channelID = "newMessage";
            String channelName = "New Message";
            String channelDescription = "There is a new message";
            int channelImportance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = notificationManager.getNotificationChannel(channelID);

            if (channel == null){
                channel = new NotificationChannel(channelID,channelName,channelImportance);
                channel.setDescription(channelDescription);
                notificationManager.createNotificationChannel(channel);
            }
            builder = new NotificationCompat.Builder(getApplicationContext(),channelID);
        }
        else{
            builder = new NotificationCompat.Builder(getApplicationContext());
            builder.setPriority(Notification.PRIORITY_DEFAULT);
        }

        builder.setContentTitle(String.format(contentTitle, message.getMessage_name()));
        builder.setContentText(message.getMessage());
        builder.setAutoCancel(true);
        builder.setSmallIcon(R.mipmap.ic_launcher_round);
        builder.setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(1,builder.build(),getForegroundServiceType());
        }else{
            notificationManager.notify(1,builder.build());
        }
    }
}

