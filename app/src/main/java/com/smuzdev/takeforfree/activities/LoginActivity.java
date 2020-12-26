package com.smuzdev.takeforfree.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.smuzdev.takeforfree.R;
import com.smuzdev.takeforfree.fragments.LoginFragment;

public class LoginActivity extends AppCompatActivity {

    LoginFragment loginFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginFragment = new LoginFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.authorizationFragment, loginFragment)
                .commit();
    }
}