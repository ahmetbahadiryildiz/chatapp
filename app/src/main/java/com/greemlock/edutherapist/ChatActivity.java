package com.greemlock.edutherapist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("messages");

        Button button = findViewById(R.id.buttonChat);
        EditText editText = findViewById(R.id.editTestMessage);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        RecyclerView recyclerView = findViewById(R.id.recyclerViewChat);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        button.setOnClickListener(view -> {
            String message = editText.getText().toString();

            if(!message.equals("")){
                Date currentDate = Calendar.getInstance().getTime();
                ObjectMessage newMessage = new ObjectMessage("", user.getUid(),"", user.getDisplayName(), message,currentDate.toString());

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
                Toast.makeText(this, "You cannot send blank message...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {

        RecyclerView recyclerView = findViewById(R.id.recyclerViewChat);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("messages");

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<ObjectMessage> messageList = new ArrayList();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    ObjectMessage objectMessage = dataSnapshot.getValue(ObjectMessage.class);
                    if (objectMessage.getMessage_receiver_uid().equals("")){
                        messageList.add(objectMessage);
                    }

                }
                MessageRecyclerAdapter messageRecyclerAdapter = new MessageRecyclerAdapter(ChatActivity.this,messageList);
                recyclerView.setAdapter(messageRecyclerAdapter);
                if (messageList.size() > 0){
                    recyclerView.scrollToPosition(messageList.size()-1);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        super.onResume();
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profileItem:
                Intent intent =new Intent(this,ProfileActivity.class);
                startActivity(intent);
                return true;
            case android.R.id.home:
                this.finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar,menu);
        return super.onCreateOptionsMenu(menu);
    }


}