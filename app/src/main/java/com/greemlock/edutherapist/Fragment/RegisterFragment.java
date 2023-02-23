package com.greemlock.edutherapist.Fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.greemlock.edutherapist.ChatActivity;
import com.greemlock.edutherapist.MainActivity;
import com.greemlock.edutherapist.NotificationService;
import com.greemlock.edutherapist.Objects.User;
import com.greemlock.edutherapist.R;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class RegisterFragment extends Fragment {

    public static final int RESULT_LOAD_IMAGE = 1;
    public static final int RESULT_CROP_IMAGE = 3;

    Uri profile_photo;
    private FirebaseAuth mAuth;
    ImageView imageView;
    Bitmap croppedImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText et_name = getActivity().findViewById(R.id.et_name_register);
        EditText et_email = getActivity().findViewById(R.id.et_email_register);
        EditText et_password = getActivity().findViewById(R.id.et_password_register);

        Button button_register = getActivity().findViewById(R.id.button);
        Button b_addPhoto = getActivity().findViewById(R.id.b_addPhoto);
        imageView = getActivity().findViewById(R.id.iv_photo);

        mAuth = FirebaseAuth.getInstance();

        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    String user_name = et_name.getText().toString();
                    String user_email = et_email.getText().toString();
                    String user_password = et_password.getText().toString();

                    mAuth.createUserWithEmailAndPassword(user_email, user_password)
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    UserProfileChangeRequest request;
                                    if (croppedImage == null){
                                        request = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(user_name)
                                                .build();
                                    }else{
                                        request = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(user_name)
                                                .setPhotoUri(getImageUri(getActivity(),croppedImage))
                                                .build();

                                    }
                                    FirebaseUser firebaseUser = authResult.getUser();
                                    firebaseUser.updateProfile(request);
                                }
                            })
                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        user.sendEmailVerification();

                                        User newUser = new User(user.getUid(),user_name,user_email, new ArrayList<>());
                                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
                                        databaseReference.push().setValue(newUser);

                                        if (croppedImage != null){

                                            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                                            StorageReference storageReferenceProfile   = storageReference.child("profilePhotos/" + user.getUid());
                                            storageReferenceProfile.putFile(getImageUri(getActivity(),croppedImage));

                                        }

                                        Toast.makeText(getActivity(), String.format("The verification link is sent to your email!"), Toast.LENGTH_SHORT).show();

                                        et_email.setText("");
                                        et_password.setText("");
                                        et_name.setText("");
                                        imageView.setImageDrawable(null);

                                    } else {
                                        Toast.makeText(getActivity(), task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }catch (Exception e){
                    Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                }


            }
        });

        b_addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RESULT_LOAD_IMAGE);

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
