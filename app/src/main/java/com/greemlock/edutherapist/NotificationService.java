package com.greemlock.edutherapist;

import static com.greemlock.edutherapist.App.CHANNEL_ID;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NotificationService extends Service {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ArrayList<ObjectMessage> messages;
    ObjectMessage lastMessage;
    private NotificationManagerCompat notificationManager;
    private NotificationCompat.Builder builder;
    int loopNumber;
    int id;
    boolean isMessageSent;
    String name;

    @Override
    public void onCreate() {

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("messages");
        name = SaveSharedPreferences.getPrefName(NotificationService.this);
        notificationManager = NotificationManagerCompat.from(getApplicationContext());
        messages = new ArrayList<>();
        lastMessage = new ObjectMessage();
        loopNumber = 0;
        id = 0;
        isMessageSent = false;
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
                if (!(messages.get(messages.size()-1).getMessage_id().equals(lastMessage.getMessage_id()))){
                    lastMessage = messages.get(messages.size()-1);


                    if(!lastMessage.getMessage_name().equals(name)) {
                        sendOnChannel();
                    }
                }
                loopNumber = loopNumber + 1;
                Log.e("loopNumber",String.valueOf(loopNumber));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        startForeground(9999,getNotification());
        return START_STICKY;
    }

    private Notification getNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("My Foreground Service")
                .setContentText("Running in the background")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        return builder.build();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public void sendOnChannel(){
        if(!isMessageSent){

            String contentTitle = lastMessage.getMessage_name();
            String contentText = lastMessage.getMessage();
            id = id + 1;
            Intent intent = new Intent(this,ChatActivity.class);
            PendingIntent pendingIntent =
                    PendingIntent.getActivity(getApplicationContext(),1,intent,PendingIntent.FLAG_IMMUTABLE);

            RemoteInput remoteInput = new RemoteInput.Builder("key_text_reply")
                    .setLabel("Reply")
                    .build();

            Intent resultIntent = new Intent(getApplicationContext(), DirectReplyReceiver.class);
            PendingIntent resultPendingIntent =
                    PendingIntent.getBroadcast(getApplicationContext(), 0, resultIntent,PendingIntent.FLAG_NO_CREATE);

            NotificationCompat.Action replyAction = new NotificationCompat.Action.Builder(
                    R.mipmap.ic_launcher,
                    "Reply",
                    resultPendingIntent
                    ).addRemoteInput(remoteInput).build();

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .addAction(replyAction)
                    .setContentTitle(contentTitle)
                    .setContentText(contentText)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .build();

            notificationManager.notify(id,notification);
            isMessageSent = true;
        }
        else{
            isMessageSent = false;
        }
    }
}
