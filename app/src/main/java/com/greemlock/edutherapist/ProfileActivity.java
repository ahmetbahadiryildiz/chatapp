package com.greemlock.edutherapist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
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
import com.greemlock.edutherapist.Objects.User;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        TextView tv_name = findViewById(R.id.tv_username);
        TextView tv_email = findViewById(R.id.tv_useremail);
        ImageView iv_photo = findViewById(R.id.iv_photo);
        ListView list_friendsList = findViewById(R.id.list_friendsList);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        tv_name.setText(user.getDisplayName());
        tv_email.setText(user.getEmail());

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("profilePhotos/"+ user.getUid());
        try {
            File file = File.createTempFile("images","jpg");
            storageReference.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    String s_file_path = file.getPath();
                    Bitmap bm = BitmapFactory.decodeFile(s_file_path);
                    iv_photo.setImageBitmap(bm);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Bitmap bm = null;
                    iv_photo.setImageResource(R.drawable.ic_baseline_person_24);
                }
            });
        }
        catch (Exception e){
            Log.e("hata",e.getMessage());

        }


        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        Query findUserInfo = databaseReference.orderByChild("userUID").equalTo(user.getUid());
        findUserInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    User user1 = dataSnapshot.getValue(User.class);
                    ArrayList<String> list = new ArrayList<>();
                    if(user1.getUserFriends() != null){
                        for(String item : user1.getUserFriends()){
                            Query findFriend = databaseReference.orderByChild("userUID").equalTo(item);
                            findFriend.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot1: snapshot.getChildren()){
                                        User friend = dataSnapshot1.getValue(User.class);
                                        list.add(friend.getUserDisplayName());
                                    }if (list != null){
                                        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1,list);
                                        list_friendsList.setAdapter(adapter);

                                        list_friendsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                                String friendUID = user1.getUserFriends().get(i);
                                                String friendName = list.get(i);
                                                Intent intentGoDirectMessages = new Intent(ProfileActivity.this,MessageActivity.class);
                                                intentGoDirectMessages.putExtra("friendUID",friendUID);
                                                intentGoDirectMessages.putExtra("friendName",friendName);
                                                startActivity(intentGoDirectMessages);
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {}
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settingsItem:
                Intent intent =new Intent(this,EditActivity.class);
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
        getMenuInflater().inflate(R.menu.actionbar_profile,menu);
        return super.onCreateOptionsMenu(menu);
    }
}