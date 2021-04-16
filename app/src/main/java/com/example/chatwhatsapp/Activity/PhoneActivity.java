package com.example.chatwhatsapp.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.chatwhatsapp.databinding.ActivityPhoneBinding;
import com.google.firebase.auth.FirebaseAuth;

public class PhoneActivity extends AppCompatActivity {

    ActivityPhoneBinding binding;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhoneBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser() != null){
            Intent intent = new Intent(PhoneActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        getSupportActionBar().hide();

        binding.phonebox.requestFocus();

        binding.continuebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PhoneActivity.this, Otpactivity.class);
                intent.putExtra("phoneNumber" , binding.phonebox.getText().toString());
                startActivity(intent);

            }
        });
    }
}