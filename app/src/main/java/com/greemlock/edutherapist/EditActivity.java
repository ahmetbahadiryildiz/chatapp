package com.greemlock.edutherapist;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.greemlock.edutherapist.Objects.User;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class EditActivity extends AppCompatActivity {

    public static final int RESULT_LOAD_IMAGE = 1;
    public static final int RESULT_CROP_IMAGE = 3;

    Uri profile_photo;
    private FirebaseAuth mAuth;
    ImageView imageView;
    Bitmap croppedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        EditText et_name = findViewById(R.id.et_name_register);

        Button button_register = findViewById(R.id.buttonEdit);
        Button b_addPhoto = findViewById(R.id.b_addPhoto);
        imageView = findViewById(R.id.iv_photo);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        et_name.setText(user.getDisplayName());

        b_addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RESULT_LOAD_IMAGE);
            }
        });
        mAuth = FirebaseAuth.getInstance();
        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserProfileChangeRequest request;
                if (croppedImage == null){
                    request = new UserProfileChangeRequest.Builder()
                            .setDisplayName(et_name.getText().toString())
                            .build();
                }else{
                    request = new UserProfileChangeRequest.Builder()
                            .setDisplayName(et_name.getText().toString())
                            .setPhotoUri(getImageUri(getApplicationContext(),croppedImage))
                            .build();

                }
                user.updateProfile(request)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    User changedUser = new User(user.getUid(),et_name.getText().toString(), user.getEmail());
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
                                    Query changeData = databaseReference.orderByChild("userUID").equalTo(user.getUid());
                                    changeData.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot dataSnapshot: snapshot.getChildren()){

                                                String key = dataSnapshot.getKey();
                                                Map<String,Object> stringObjectMap = new HashMap<>();
                                                stringObjectMap.put("userDisplayName",et_name.getText().toString());
                                                databaseReference.child(key).updateChildren(stringObjectMap);

                                                if (croppedImage != null){
                                                    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                                                    StorageReference storageReferenceProfile   = storageReference.child("profilePhotos/" + user.getUid());
                                                    storageReferenceProfile.putFile(getImageUri(getApplicationContext(),croppedImage));
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                                    Log.d("updateStatus", "User profile updated.");
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });

                if (croppedImage != null){

                    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                    StorageReference storageReferenceProfile   = storageReference.child("profilePhotos/" + user.getUid());
                    storageReferenceProfile.putFile(getImageUri(getApplicationContext(),croppedImage));

                }
            }
        });
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == RESULT_LOAD_IMAGE) {

            if (data != null){
                profile_photo = data.getData();
                Intent cropIntent = new Intent("com.android.camera.action.CROP");
                cropIntent.setDataAndType(profile_photo, "image/*");
                cropIntent.putExtra("crop", "true");
                cropIntent.putExtra("aspectX", 1);
                cropIntent.putExtra("aspectY", 1);
                cropIntent.putExtra("outputX", 256);
                cropIntent.putExtra("outputY", 256);
                cropIntent.putExtra("return-data", true);
                startActivityForResult(cropIntent, RESULT_CROP_IMAGE);
            }

        }
        if (requestCode == RESULT_CROP_IMAGE){
            if (data != null){
                Bundle extras = data.getExtras();
                croppedImage = extras.getParcelable("data");
                imageView.setImageBitmap(croppedImage);
            }
        }
    }
}