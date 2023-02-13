package com.greemlock.edutherapist;

import android.app.NotificationManager;
import android.app.RemoteInput;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DirectReplyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        int id = intent.getIntExtra("id",1);

        String name = SaveSharedPreferences.getPrefName(context);
        Date currentDate = Calendar.getInstance().getTime();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("messages");

        ObjectMessage newMessage = new ObjectMessage("",name,remoteInput.getCharSequence("key_text_reply").toString(),currentDate.toString());
        databaseReference.push().setValue(newMessage);

        Query addMessageId = databaseReference.orderByChild("message").equalTo(newMessage.getMessage());
        addMessageId.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){

                    ObjectMessage objectMessage = dataSnapshot.getValue(ObjectMessage.class);
                    if (objectMessage.getMessage_id() == ""){

                        String messageKey = dataSnapshot.getKey();
                        Map<String,Object> stringObjectMap = new HashMap<>();
                        stringObjectMap.put("message_id",messageKey);
                        databaseReference.child(messageKey).updateChildren(stringObjectMap);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        NotificationService.sendReply(context,newMessage.getMessage_name(),newMessage.getMessage(),id);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(id);
    }
}
