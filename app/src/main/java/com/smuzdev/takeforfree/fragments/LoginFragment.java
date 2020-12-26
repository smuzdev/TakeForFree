package com.smuzdev.takeforfree.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.smuzdev.takeforfree.LoginActivity;
import com.smuzdev.takeforfree.MainActivity;
import com.smuzdev.takeforfree.R;


public class LoginFragment extends Fragment {

    private static final String TAG = "EmailPassword";
    private FirebaseAuth mAuth;
    EditText etPassword;
    EditText etEmail;
    MaterialButton signInButton;
    TextView linkSignUp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        mAuth = FirebaseAuth.getInstance();
        etEmail = view.findViewById(R.id.input_email);
        etPassword = view.findViewById(R.id.input_password);
        signInButton = view.findViewById(R.id.btn_login);
        linkSignUp = view.findViewById(R.id.link_sign_up);

        mAuth = FirebaseAuth.getInstance();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (email.isEmpty()) {
                    etEmail.setError("Required");
                    etEmail.requestFocus();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    etEmail.setError("Please enter the valid email");
                    etEmail.requestFocus();
                    return;
                }

                if (password.isEmpty()) {
                    etPassword.setError("Required");
                    etPassword.requestFocus();
                    return;
                }

                if (password.length() < 6) {
                    etPassword.setError("Min password length is 6 characters");
                    etPassword.requestFocus();
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            startActivity(new Intent(getActivity(), MainActivity.class));
                        } else {
                            Toast.makeText(getActivity(), "Failed to login! Incorrect login or password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }

        });

        linkSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegistrationFragment registrationFragment = new RegistrationFragment();
                FragmentManager fragmentManager = ((LoginActivity) getActivity()).getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.authorizationFragment, registrationFragment)
                        .commit();
            }
        });

    }

}