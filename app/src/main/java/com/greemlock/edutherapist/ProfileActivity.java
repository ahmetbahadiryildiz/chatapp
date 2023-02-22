package com.greemlock.edutherapist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        TextView tv_name = findViewById(R.id.tv_username);
        TextView tv_email = findViewById(R.id.tv_useremail);
        ImageView iv_photo = findViewById(R.id.iv_photo);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        tv_name.setText(user.getDisplayName());
        tv_email.setText(user.getEmail());
        iv_photo.setImageURI(user.getPhotoUrl());

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
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