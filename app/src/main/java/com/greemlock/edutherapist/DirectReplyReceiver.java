package com.greemlock.edutherapist;

import android.app.RemoteInput;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.greemlock.edutherapist.Adapter.MessageRecyclerAdapter;
import com.greemlock.edutherapist.Objects.ObjectMessage;
import com.greemlock.edutherapist.Objects.User;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DirectReplyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        int id = intent.getIntExtra("id",1);

        FirebaseUser name = FirebaseAuth.getInstance().getCurrentUser();
        Date currentDate = Calendar.getInstance().getTime();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("messages");

        ObjectMessage newMessage = new ObjectMessage("",name.getUid(), name.getDisplayName(),remoteInput.getCharSequence("key_text_reply").toString(),currentDate.toString());
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
        ChatActivity chatActivity = new ChatActivity();

        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("users");
        Query findUserName = databaseReference1.orderByChild("userUID").equalTo(newMessage.getMessage_uid());

        findUserName.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    NotificationService.sendReply(context,dataSnapshot.getValue(User.class).getUserDisplayName(),newMessage.getMessage(),id);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancelAll();
    }
}
