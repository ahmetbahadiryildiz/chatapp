package com.greemlock.edutherapist.Fragment;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.greemlock.edutherapist.ChatActivity;
import com.greemlock.edutherapist.MainActivity;
import com.greemlock.edutherapist.NotificationService;
import com.greemlock.edutherapist.ProfileActivity;
import com.greemlock.edutherapist.R;

import java.util.HashMap;
import java.util.Map;

public class LoginFragment extends Fragment {

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText et_email = getActivity().findViewById(R.id.et_email);
        EditText et_password = getActivity().findViewById(R.id.et_password);
        Button button_login = getActivity().findViewById(R.id.button_login);
        TextView tv_resetPassword = getActivity().findViewById(R.id.tv_resetPassword);

        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();

                String email = et_email.getText().toString();
                String password = et_password.getText().toString();

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    if (user.isEmailVerified()){
                                        loginActivity(user);
                                    }else{
                                        Toast.makeText(getActivity(), "Please verify your account!", Toast.LENGTH_SHORT).show();
                                    }

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(getActivity(), task.getException().getLocalizedMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        tv_resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewContactDialog();
            }
        });

    }

    private void createNewContactDialog() {
        dialogBuilder = new AlertDialog.Builder(getActivity());
        final View contactPopupView = getLayoutInflater().inflate(R.layout.pop_up_reset_password,null);

        EditText et_email = contactPopupView.findViewById(R.id.et_email);
        Button b_resetPassword = contactPopupView.findViewById(R.id.b_resetPassword);

        dialogBuilder.setView(contactPopupView);
        dialog = dialogBuilder.create();
        dialog.show();

        b_resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user_mail = et_email.getText().toString();
                mAuth.sendPasswordResetEmail(user_mail);
                Toast.makeText(getActivity(), "Reset Mail is sent to your email.", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });
    }

    public void loginActivity(FirebaseUser user){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        Query setOnline = databaseReference.orderByChild("userUID").equalTo(user.getUid());
        setOnline.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    String key = dataSnapshot.getKey();
                    Map<String,Object> stringObjectMap = new HashMap<>();
                    stringObjectMap.put("userIsOnline",true);
                    databaseReference.child(key).updateChildren(stringObjectMap);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        Intent intent = new Intent(getActivity(), NotificationService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getActivity().startForegroundService(intent);
        }
        else{
            getActivity().startService(intent);
        }

        Intent intentChat = new Intent(getActivity(), ProfileActivity.class);
        intentChat.putExtra("currentUser",user);
        startActivity(intentChat);
    }
}
