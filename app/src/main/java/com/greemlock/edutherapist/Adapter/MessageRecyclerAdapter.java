package com.greemlock.edutherapist.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

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
import com.greemlock.edutherapist.ChatActivity;
import com.greemlock.edutherapist.Objects.ObjectMessage;
import com.greemlock.edutherapist.Objects.User;
import com.greemlock.edutherapist.R;

import java.io.File;
import java.lang.invoke.ConstantCallSite;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<ObjectMessage> messages;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    Bitmap bm;
    boolean isfriend;

    public MessageRecyclerAdapter(Context context, List<ObjectMessage> messages){
        this.context = context;
        this.messages = messages;
    }

    public class ViewHolder0 extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView message;
        public TextView date;
        public ImageView imageView;
        public ConstraintLayout constraintLayout;

        public ViewHolder0(@NonNull View itemView){
            super(itemView);
            name = itemView.findViewById(R.id.name);
            message = itemView.findViewById(R.id.message);
            date = itemView.findViewById(R.id.date);
            imageView = itemView.findViewById(R.id.imageView);
            constraintLayout = itemView.findViewById(R.id.constraintLayout);
        }
    }

    public class ViewHolder1 extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView message;
        public TextView date;
        public ImageView imageView;
        public ConstraintLayout constraintLayout;

        public ViewHolder1(@NonNull View itemView){
            super(itemView);
            name = itemView.findViewById(R.id.name);
            message = itemView.findViewById(R.id.message);
            date = itemView.findViewById(R.id.date);
            imageView = itemView.findViewById(R.id.imageView);
            constraintLayout = itemView.findViewById(R.id.constraintLayout);
        }
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView;
        switch (viewType) {
            case 0:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.message_layout_recycler,parent,false);
                return new ViewHolder0(itemView);
            case 1:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.message_layout_recycler_my_messages,parent,false);
                return new ViewHolder1(itemView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final ObjectMessage objectMessage = messages.get(position);

        switch (holder.getItemViewType()) {
            case 0:

                ViewHolder0 viewHolder0 = (ViewHolder0)holder;
                viewHolder0.name.setText(objectMessage.getMessage_name());

                FirebaseStorage fb_storage = FirebaseStorage.getInstance();
                StorageReference s_reference = fb_storage.getReference();
                StorageReference sr_offer_company_photo = s_reference.child("profilePhotos/" + objectMessage.getMessage_uid());


                try {
                    File file = File.createTempFile("images","jpg");
                    sr_offer_company_photo.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            String s_file_path = file.getPath();
                            bm = BitmapFactory.decodeFile(s_file_path);
                            viewHolder0.imageView.setImageBitmap(bm);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            bm = null;
                            viewHolder0.imageView.setImageResource(R.drawable.ic_baseline_person_24);
                        }
                    });
                }
                catch (Exception e){
                    Log.e("hata",e.getMessage());

                }

                viewHolder0.message.setText(objectMessage.getMessage());
                viewHolder0.date.setText(objectMessage.getMessage_date());
                viewHolder0.constraintLayout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        createNewContactDialog(objectMessage.getMessage_name(),bm,objectMessage.getMessage_uid());
                        return false;

                    }
                });

                break;

            case 1:
                ViewHolder1 viewHolder1 = (ViewHolder1)holder;
                viewHolder1.name.setText(user.getDisplayName());
                viewHolder1.message.setText(objectMessage.getMessage());
                viewHolder1.date.setText(objectMessage.getMessage_date());

                fb_storage = FirebaseStorage.getInstance();
                s_reference = fb_storage.getReference();
                sr_offer_company_photo = s_reference.child("profilePhotos/" + user.getUid());

                try {
                    File file = File.createTempFile("images","jpg");
                    sr_offer_company_photo.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            String s_file_path = file.getPath();
                            Bitmap bm = BitmapFactory.decodeFile(s_file_path);
                            viewHolder1.imageView.setImageBitmap(bm);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            viewHolder1.imageView.setImageResource(R.drawable.ic_baseline_person_24);
                        }
                    });
                }
                catch (Exception e){}
        }
    }

    @SuppressLint("ResourceAsColor")
    private void createNewContactDialog(String name, Bitmap bitmap, String uid) {
        dialogBuilder = new AlertDialog.Builder(context);
        final View contactPopupView = LayoutInflater.from(context).inflate(R.layout.pop_up_add_friend,null);

        TextView tv_name = contactPopupView.findViewById(R.id.tv_name);
        ImageView iv_profilePhoto = contactPopupView.findViewById(R.id.iv_profilePhoto);
        Button b_addFriend = contactPopupView.findViewById(R.id.b_addFriend);

        tv_name.setText(name);

        StorageReference s_reference = FirebaseStorage.getInstance().getReference();
        StorageReference sr_offer_company_photo = s_reference.child("profilePhotos/" + uid);

        try {
            File file = File.createTempFile("images", "jpg");
            sr_offer_company_photo.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    String s_file_path = file.getPath();
                    Bitmap bm = BitmapFactory.decodeFile(s_file_path);
                    iv_profilePhoto.setImageBitmap(bm);

                }
            });
        }
        catch (Exception ex){}

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        Query isFriend = databaseReference.orderByChild("userUID").equalTo(user.getUid());
        isFriend.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    User userInfo = dataSnapshot.getValue(User.class);
                    if (userInfo.getUserFriends() != null){
                        for(String friendUID : userInfo.getUserFriends()){
                            if(uid.equals(friendUID)){
                                b_addFriend.setVisibility(View.INVISIBLE);
                                Toast.makeText(context, "You are already friends with this person", Toast.LENGTH_SHORT).show();
                                b_addFriend.setClickable(false);
                                return;
                            }
                        }
                    }

                    b_addFriend.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String messageKey = dataSnapshot.getKey();

                            ArrayList<String> list = userInfo.getUserFriends();
                            ArrayList<String> newList;
                            if(list == null){
                                newList = new ArrayList<>();
                            }else{
                                newList = list;
                            }

                            newList.add(uid);
                            Map<String,Object> map = new HashMap<>();
                            map.put("userFriends",newList);
                            try {
                                databaseReference.child(messageKey).updateChildren(map);
                                Toast.makeText(context, "You added as a friend successfully.", Toast.LENGTH_SHORT).show();
                            }catch (Exception e){
                                Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }

                            dialog.cancel();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        dialogBuilder.setView(contactPopupView);
        dialog = dialogBuilder.create();
        dialog.show();

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        final ObjectMessage objectMessage = messages.get(position);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userUid = user.getUid();

        if(userUid.equals(objectMessage.getMessage_uid())){
            return 1;
        }
        else{
            return 0;
        }
    }

}
