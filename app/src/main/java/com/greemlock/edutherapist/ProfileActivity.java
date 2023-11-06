package com.greemlock.edutherapist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.sendbird.calls.AcceptParams;
import com.sendbird.calls.DirectCall;
import com.sendbird.calls.SendBirdCall;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private static final String[] MANDATORY_PERMISSIONS = {"android.permission.CAMERA", "android.permission.RECORD_AUDIO", "android.permission.WRITE_EXTERNAL_STORAGE"};
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        checkPermissions();
        if (getIntent().getBooleanExtra(NotificationCompat.CATEGORY_STATUS, false)) {
            showPopup();
        }


        ConstraintLayout layoutProfile = findViewById(R.id.layout_profile);
        TextView tv_name = layoutProfile.findViewById(R.id.tv_username);
        TextView tv_email = layoutProfile.findViewById(R.id.tv_useremail);
        ImageView iv_photo = layoutProfile.findViewById(R.id.iv_photo);
        ListView list_friendsList = findViewById(R.id.list_friendsList);

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
                    list.add("Enter the chat room");
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
                                                if (i == 0){
                                                    Intent goToChatRoom = new Intent(ProfileActivity.this,ChatActivity.class);
                                                    startActivity(goToChatRoom);
                                                }else{
                                                    String friendUID = user1.getUserFriends().get(i-1);
                                                    String friendName = list.get(i);
                                                    Intent intentGoDirectMessages = new Intent(ProfileActivity.this,MessageActivity.class);
                                                    intentGoDirectMessages.putExtra("friendUID",friendUID);
                                                    intentGoDirectMessages.putExtra("friendName",friendName);
                                                    startActivity(intentGoDirectMessages);
                                                }
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

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ProfileActivity.this);
                final View contactPopupView = getLayoutInflater().inflate(R.layout.pop_up_log_out,null);

                Button b_yes = contactPopupView.findViewById(R.id.b_yes);
                Button b_no = contactPopupView.findViewById(R.id.b_no);

                dialogBuilder.setView(contactPopupView);
                AlertDialog dialog = dialogBuilder.create();
                dialog.show();

                b_no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();
                    }
                });
                b_yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getApplicationContext(), "You logged out!", Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
                        Query setOnline = databaseReference.orderByChild("userUID").equalTo(user.getUid());
                        setOnline.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                                    String key = dataSnapshot.getKey();
                                    Map<String,Object> stringObjectMap = new HashMap<>();
                                    stringObjectMap.put("userIsOnline",false);
                                    databaseReference.child(key).updateChildren(stringObjectMap);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {}
                        });

                        Intent stopService = new Intent(ProfileActivity.this, NotificationService.class);
                        stopService(stopService);
                        finish();
                        dialog.cancel();
                    }
                });
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_profile,menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void checkPermissions() {
        ArrayList<String> deniedPermissions = new ArrayList<>();
        for (String permission : MANDATORY_PERMISSIONS) {
            if (checkCallingOrSelfPermission(permission)
                    != PackageManager.PERMISSION_GRANTED) {
                deniedPermissions.add(permission);
            }
        }
        if (deniedPermissions.size() > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(deniedPermissions.toArray(new String[0]),
                        REQUEST_PERMISSIONS_REQUEST_CODE);
            } else {
                android.util.Log.e("VideoChat",
                        "[VideoChatActivity] PERMISSION_DENIED");
            }
        }
    }

    private void showPopup(){

        String callID = null;

        callID = getIntent().getStringExtra("callID");
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        View contactPopupView = getLayoutInflater().inflate(R.layout.pop_up_incoming_call, null);

        Log.e("CALL ID", callID);

        DirectCall call = SendBirdCall.getCall(callID);
        String callerID = call.getCaller().getUserId();

        dialogBuilder.setView(contactPopupView);
        AlertDialog dialog = dialogBuilder.create();

        TextView textViewCaller = contactPopupView.findViewById(R.id.textViewCallee);
        ImageView imageViewCaller = contactPopupView.findViewById(R.id.imageViewCaller);

        ImageButton imageButtonAccept = contactPopupView.findViewById(R.id.imageButtonAccept);
        imageButtonAccept.setOnClickListener(view -> {

            call.accept(new AcceptParams());
            dialog.cancel();
            Intent intent = new Intent(getApplicationContext(), VideoCallActivity.class);
            intent.putExtra("callID", call.getCallId());
            startActivity(intent);
        });

        ImageButton imageButtonDecline = contactPopupView.findViewById(R.id.imageButtonDecline);
        imageButtonDecline.setOnClickListener(view ->{
            call.end();
            dialog.cancel();
        });

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        Query getUserUID = databaseReference.orderByChild("userUID").endAt(callerID);
        getUserUID.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    User user1 = dataSnapshot.getValue(User.class);
                    textViewCaller.setText(user1.getUserDisplayName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        StorageReference reference = FirebaseStorage.getInstance().getReference();
        StorageReference storageReference = reference.child("profilePhotos/"+ callerID);

        try {
            File file = File.createTempFile("images","jpg");
            String str = callID;
            try {
                storageReference.getFile(file).addOnSuccessListener(taskSnapshot -> {
                    imageViewCaller.setImageBitmap(BitmapFactory.decodeFile(file.getPath()));
                }).addOnFailureListener(e -> {
                    imageViewCaller.setImageResource(R.drawable.ic_baseline_person_24);
                });
            }
            catch (Exception e){
                Log.e("error",e.getLocalizedMessage());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        dialog.show();
    }
}