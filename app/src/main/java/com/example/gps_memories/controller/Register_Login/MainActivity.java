package com.example.gps_memories.controller.Register_Login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gps_memories.R;
import com.example.gps_memories.controller.HomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    protected Button registerbtn;
    protected Button loginbtn;

    String username;


    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_main);
        initView();

        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(MainActivity.this, RegisterActivity.class);


                startActivity(intent);


                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);


            }
        });


        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(MainActivity.this, LoginActivity.class);


                startActivity(intent);


                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

            }
        });





    }



    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // No user is signed in
        } else {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            intent.putExtra("username",username);
            startActivity(intent);
        }
    }


    private void initView() {
        registerbtn = (Button) findViewById(R.id.registerbtn);
        loginbtn = (Button) findViewById(R.id.loginbtn);
    }
}
