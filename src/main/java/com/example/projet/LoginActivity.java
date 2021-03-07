package com.example.projet;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projet.model.User;
import com.example.projet.dao.UserDao;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {

    private UserDao UserDao;
    public static User myUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState == null){
            setContentView(R.layout.login_activity);
            final Button loginButton = findViewById(R.id.login);

            loginButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    final EditText passwordTextView = findViewById(R.id.Password);

                            Intent intent = new Intent(getBaseContext(), MainActivity.class);
                            intent.putExtra("Login", loginButton.getText().toString());
                            intent.putExtra("Password", passwordTextView.getText().toString());
                            startActivity(intent);
                }
            });
        }
    }
}
