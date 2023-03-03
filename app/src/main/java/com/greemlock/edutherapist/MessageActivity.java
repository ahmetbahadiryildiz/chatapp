package com.greemlock.edutherapist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.greemlock.edutherapist.Adapter.MessageRecyclerAdapter;
import com.greemlock.edutherapist.Adapter.MessageRecyclerAdapterDM;
import com.greemlock.edutherapist.Objects.ObjectMessage;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        String friendUID = getIntent().getStringExtra("friendUID");
        String friendName = getIntent().getStringExtra("friendName");

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("  "+friendName);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("profilePhotos/"+friendUID);
        try{
            File file = File.createTempFile("images","jpg");
            storageReference.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    String filePath = file.getPath();
                    Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                    BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
                    getSupportActionBar().setIcon(bitmapDrawable);

                }
            });
        }catch (Exception ex){}

        RecyclerView recyclerView = findViewById(R.id.recyclerViewChat);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("messages");

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();

        ArrayList<ObjectMessage> messageList = new ArrayList();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    ObjectMessage objectMessage = dataSnapshot.getValue(ObjectMessage.class);
                    if (objectMessage.getMessage_receiver_uid().equals(friendUID) && objectMessage.getMessage_uid().equals(uid)){
                        messageList.add(objectMessage);
                    }else if(objectMessage.getMessage_receiver_uid().equals(uid) && objectMessage.getMessage_uid().equals(friendUID)){
                        messageList.add(objectMessage);
                    }


                }
                MessageRecyclerAdapterDM messageRecyclerAdapter = new MessageRecyclerAdapterDM(MessageActivity.this,messageList);
                recyclerView.setAdapter(messageRecyclerAdapter);
                if (messageList.size() > 0){
                    recyclerView.scrollToPosition(messageList.size()-1);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        Button button = findViewById(R.id.buttonChat);
        EditText editText = findViewById(R.id.editTestMessage);
        button.setOnClickListener(view -> {
            String message = editText.getText().toString();

            if(!message.equals("")){
                Date currentDate = Calendar.getInstance().getTime();
                ObjectMessage newMessage = new ObjectMessage("", user.getUid(),friendUID, user.getDisplayName(), message,currentDate.toString());

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
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
}