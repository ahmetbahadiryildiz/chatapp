package com.greemlock.edutherapist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.greemlock.edutherapist.Adapter.ViewPagerAdapter;
import com.greemlock.edutherapist.Fragment.LoginFragment;
import com.greemlock.edutherapist.Fragment.RegisterFragment;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new LoginFragment(),"Login");
        adapter.addFragment(new RegisterFragment(),"Register");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            loginActivity(currentUser);
        }
    }

    public void loginActivity(FirebaseUser user){

        Intent intent = new Intent(getApplicationContext(), NotificationService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        }
        else{
            startService(intent);
        }

        Intent intentChat = new Intent(this, ProfileActivity.class);
        intentChat.putExtra("currentUser",user);
        startActivity(intentChat);
    }
}