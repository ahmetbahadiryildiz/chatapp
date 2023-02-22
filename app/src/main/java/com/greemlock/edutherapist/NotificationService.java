package com.greemlock.edutherapist;

import static com.greemlock.edutherapist.App.CHANNEL_ID;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.greemlock.edutherapist.Objects.ObjectMessage;
import com.greemlock.edutherapist.Objects.User;

import java.util.ArrayList;

public class NotificationService extends Service {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ArrayList<ObjectMessage> messages;
    ObjectMessage lastMessage;
    int loopNumber;
    int id;
    boolean isMessageSent;
    String name;
    ChatActivity chatActivity = new ChatActivity();

    @Override
    public void onCreate() {

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("messages");
        name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
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

                if (messages.size()>0){
                    if (!(messages.get(messages.size()-1).getMessage_id().equals(lastMessage.getMessage_id()))){
                        lastMessage = messages.get(messages.size()-1);

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String messageSender = lastMessage.getMessage_uid();

                        if(!messageSender.equals(user.getUid())) {
                            sendOnChannel();
                        }

                    }
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        startForeground(9999,getNotification());
        return START_STICKY;
    }

    private Notification getNotification() {
        Intent intent = new Intent(getApplicationContext(),ChatActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(getApplicationContext(),1,intent,PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentTitle(String.format("Dear %s. You logged in successfully",name))
                .setContentText("Now you can get messages even if the app is closed.")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        return builder.build();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void sendReply(Context context, String contentTitle, String contentText, int id){

        String name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

        if(!(contentText.equals(name))){
            Intent intent = new Intent(context,ChatActivity.class);
            @SuppressLint("InlinedApi") PendingIntent pendingIntent =
                    PendingIntent.getActivity(context,1,intent,PendingIntent.FLAG_IMMUTABLE);

            RemoteInput remoteInput = new RemoteInput.Builder("key_text_reply")
                    .setLabel("Reply")
                    .build();
            Intent resultIntent = new Intent(context, DirectReplyReceiver.class);
            @SuppressLint("InlinedApi") PendingIntent resultPendingIntent =
                    PendingIntent.getBroadcast(context,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_MUTABLE);
            NotificationCompat.Action replyAction = new NotificationCompat.Action.Builder(
                    R.mipmap.ic_launcher,
                    "Reply",
                    resultPendingIntent
            ).addRemoteInput(remoteInput).build();

            Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .addAction(replyAction)
                    .setContentTitle(contentTitle)
                    .setContentText(contentText)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .build();
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(id,notification);
        }

    }

    public void sendOnChannel(){
        if(!isMessageSent){
            String contentTitle = lastMessage.getMessage_name();
            String contentText = lastMessage.getMessage();
            id = id + 1;
            sendReply(getApplicationContext(),contentTitle,contentText,id);
            isMessageSent = true;
        }
        else{
            isMessageSent = false;
        }
    }
}
