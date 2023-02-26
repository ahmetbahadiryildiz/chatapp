package com.greemlock.edutherapist;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

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

    }
}