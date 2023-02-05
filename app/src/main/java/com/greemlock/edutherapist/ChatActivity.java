package com.greemlock.edutherapist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("messages");

        String name = SaveSharedPreferences.getPrefName(ChatActivity.this);
        Button button = findViewById(R.id.buttonChat);
        EditText editText = findViewById(R.id.editTestMessage);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewChat);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<ObjectMessage> messageList = new ArrayList();

        Toast.makeText(this, name, Toast.LENGTH_SHORT).show();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    ObjectMessage objectMessage = dataSnapshot.getValue(ObjectMessage.class);
                    boolean isInList = false;
                    for(ObjectMessage oldMessage:messageList){
                        if(oldMessage.getMessage_id().equals(objectMessage.getMessage_id())){
                            isInList = true;
                            break;
                        }
                    }
                    if (!isInList){messageList.add(objectMessage);}
                }
                MessageRecyclerAdapter messageRecyclerAdapter = new MessageRecyclerAdapter(ChatActivity.this,messageList);
                recyclerView.setAdapter(messageRecyclerAdapter);
                recyclerView.scrollToPosition(messageList.size()-1);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        button.setOnClickListener(view -> {
            String message = editText.getText().toString();

            if(!message.equals("")){
                Date currentDate = Calendar.getInstance().getTime();
                ObjectMessage newMessage = new ObjectMessage("",name,message,currentDate.toString());

                databaseReference.push().setValue(newMessage);

                Query addMessageId = databaseReference.orderByChild("message").equalTo(message);
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
                editText.setText("");
            }
            else{
                Toast.makeText(this, "You cannot send blank message yarrağam...", Toast.LENGTH_SHORT).show();
            }
        });
    }
}