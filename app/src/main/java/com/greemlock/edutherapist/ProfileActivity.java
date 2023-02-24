package com.greemlock.edutherapist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.greemlock.edutherapist.Objects.User;

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
        iv_photo.setImageURI(user.getPhotoUrl());

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        Query findUserInfo = databaseReference.orderByChild("userUID").equalTo(user.getUid());
        findUserInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    User user1 = dataSnapshot.getValue(User.class);
                    ArrayList<String> list = user1.getUserFriends();
                    if (list != null){

                        for (String item: list){
                            Log.e("item",item);
                        }
                        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1,user1.getUserFriends());
                        list_friendsList.setAdapter(adapter);
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