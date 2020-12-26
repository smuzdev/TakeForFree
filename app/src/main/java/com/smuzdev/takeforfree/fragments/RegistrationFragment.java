package com.smuzdev.takeforfree.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.google.firebase.database.FirebaseDatabase;
import com.smuzdev.takeforfree.activities.LoginActivity;
import com.smuzdev.takeforfree.R;
import com.smuzdev.takeforfree.models.User;


public class RegistrationFragment extends Fragment {

    private FirebaseAuth mAuth;
    EditText etPassword;
    EditText etEmail;
    EditText etPhone;
    EditText etName;
    MaterialButton signUpButton;
    TextView linkSignIn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registration, container, false);

        mAuth = FirebaseAuth.getInstance();
        etEmail = view.findViewById(R.id.input_email);
        etPassword = view.findViewById(R.id.input_password);
        etName = view.findViewById(R.id.input_name);
        etPhone = view.findViewById(R.id.input_mobile);
        signUpButton = view.findViewById(R.id.btn_signup);
        signUpButton = view.findViewById(R.id.btn_signup);
        linkSignIn = view.findViewById(R.id.link_sign_in);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                final String name = etName.getText().toString().trim();
                final String phone = etPhone.getText().toString().trim();

                if (name.isEmpty()) {
                    etName.setError("Required");
                    etName.requestFocus();
                    return;
                }
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

                if (phone.isEmpty()) {
                    etPhone.setError("Required");
                    etPhone.requestFocus();
                    return;
                }
                if (!Patterns.PHONE.matcher(phone).matches()) {
                    etPhone.setError("Please enter the valid email");
                    etPhone.requestFocus();
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

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    User user = new User(name, phone, email);

                                    FirebaseDatabase.getInstance().getReference("Users")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getActivity(), "New user successfully registered!", Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(getActivity(), "Something wrong", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }
                            }
                        });
            }
        });

        linkSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginFragment loginFragment = new LoginFragment();
                FragmentManager fragmentManager = ((LoginActivity) getActivity()).getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.authorizationFragment, loginFragment)
                        .commit();
            }
        });
    }

}