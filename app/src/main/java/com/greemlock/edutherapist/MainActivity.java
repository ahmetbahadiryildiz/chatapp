package com.greemlock.edutherapist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String name = SaveSharedPreferences.getPrefName(this);

        if (!name.equals("")){
            loginActivity();
        }

        Button buttonLogin = findViewById(R.id.buttonLogin);
        EditText editTextName = findViewById(R.id.editTextTextPersonName);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextName.getText().toString();
                SaveSharedPreferences.setPrefName(MainActivity.this,name);

                loginActivity();

            }
        });

    }

    public void loginActivity(){

        Intent intent = new Intent(this,NotificationService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        }
        else{
            startService(intent);
        }

        Intent intentChat = new Intent(MainActivity.this, ChatActivity.class);
        startActivity(intentChat);
    }
}