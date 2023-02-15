package com.greemlock.edutherapist.Fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.greemlock.edutherapist.ChatActivity;
import com.greemlock.edutherapist.MainActivity;
import com.greemlock.edutherapist.NotificationService;
import com.greemlock.edutherapist.Objects.User;
import com.greemlock.edutherapist.R;

public class RegisterFragment extends Fragment {

    private FirebaseAuth mAuth;

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

        mAuth = FirebaseAuth.getInstance();

        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String user_name = et_name.getText().toString();
                String user_email = et_email.getText().toString();
                String user_password = et_password.getText().toString();

                mAuth.createUserWithEmailAndPassword(user_email, user_password)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(user_name)
                                        .build();
                                FirebaseUser firebaseUser = authResult.getUser();
                                firebaseUser.updateProfile(userProfileChangeRequest);
                            }
                        })
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    FirebaseUser user = mAuth.getCurrentUser();
                                    user.sendEmailVerification();
                                    Toast.makeText(getActivity(), String.format("The verification link is sent to your email!"), Toast.LENGTH_SHORT).show();

                                    et_email.setText("");
                                    et_password.setText("");
                                    et_name.setText("");

                                } else {
                                    Toast.makeText(getActivity(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

    }

    public void loginActivity(FirebaseUser user){

        Intent intent = new Intent(getActivity(), NotificationService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getActivity().startForegroundService(intent);
        }
        else{
            getActivity().startService(intent);
        }

        Intent intentChat = new Intent(getActivity(), ChatActivity.class);
        intentChat.putExtra("currentUser",user);
        startActivity(intentChat);
    }
}
