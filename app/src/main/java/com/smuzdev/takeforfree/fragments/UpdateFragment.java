package com.smuzdev.takeforfree.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.smuzdev.takeforfree.CustomTextWatcher;
import com.smuzdev.takeforfree.R;
import com.smuzdev.takeforfree.activities.MainActivity;
import com.smuzdev.takeforfree.models.Thing;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.app.Activity.RESULT_OK;


public class UpdateFragment extends Fragment {

    ImageView thingImage;
    Uri uri;
    EditText txt_thingName, txt_thingDescription, txt_thingUseDuration,
            txt_thingPickupPoint;
    String txt_thingPublicationDate;
    String imageUrl;
    String key, oldImageUrl;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    String thingName, thingDescription, thingPublicationDate, thingUseDuration,
            thingPickupPoint, userName, userPhone, userEmail;
    MaterialButton updateButton, selectImageButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Update thing");

        thingImage = view.findViewById(R.id.iv_thingImage);
        txt_thingName = view.findViewById(R.id.txtThingName);
        txt_thingDescription = view.findViewById(R.id.txtThingDescription);
        txt_thingUseDuration = view.findViewById(R.id.txtThingUseDuration);
        txt_thingPickupPoint = view.findViewById(R.id.txtThingPickupPoint);
        selectImageButton = view.findViewById(R.id.select_image_button);
        updateButton = view.findViewById(R.id.update_button);
        updateButton.setEnabled(false);
        updateButton.getBackground().setAlpha(128);

        Bundle bundle = this.getArguments();
        if (bundle != null) {

            txt_thingName.setText(bundle.getString("thingNameKey"));
            txt_thingDescription.setText(bundle.getString("thingDescriptionKey"));
            txt_thingPublicationDate = bundle.getString("thingPublicationDateKey");
            txt_thingUseDuration.setText(bundle.getString("thingUseDurationKey"));
            txt_thingPickupPoint.setText(bundle.getString("thingPickupPointKey"));
            userName = bundle.getString("userNameKey");
            userPhone = bundle.getString("userPhoneKey");
            userEmail = bundle.getString("userEmailKey");
            key = bundle.getString("key");
            oldImageUrl = bundle.getString("oldImageUrl");
            Glide.with(this)
                    .load(bundle.getString("oldImageUrl"))
                    .into(thingImage);
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("Things");

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        EditText[] editTexts = {txt_thingName, txt_thingDescription, txt_thingUseDuration, txt_thingPickupPoint};
        CustomTextWatcher textWatcher = new CustomTextWatcher(editTexts, updateButton);
        for (EditText editText : editTexts) {
            editText.addTextChangedListener(textWatcher);
        }

        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                startActivityForResult(photoPicker, 1);
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thingName = txt_thingName.getText().toString().trim();
                thingDescription = txt_thingDescription.getText().toString().trim();
                thingPublicationDate = txt_thingPublicationDate;
                thingUseDuration = txt_thingUseDuration.getText().toString().trim();
                thingPickupPoint = txt_thingPickupPoint.getText().toString().trim();

                final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Thing uploading...");
                progressDialog.show();

                storageReference = FirebaseStorage.getInstance()
                        .getReference().child("ThingImage").child(uri.getLastPathSegment());
                storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isComplete()) ;
                        Uri urlImage = uriTask.getResult();
                        imageUrl = urlImage.toString();
                        uploadThing();
                        progressDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            uri = data.getData();
            thingImage.setImageURI(uri);

        } else Toast.makeText(getActivity(), "You haven't picked image", Toast.LENGTH_LONG).show();
    }

    public void uploadThing() {

        Thing thing = new Thing(
                thingName,
                thingDescription,
                thingPublicationDate,
                thingUseDuration,
                thingPickupPoint,
                imageUrl,
                userName,
                userPhone,
                userEmail
        );

        databaseReference.child(key).removeValue();
        databaseReference.child(thing.getThingName()).setValue(thing).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                StorageReference storageReferenceNew = FirebaseStorage.getInstance().getReferenceFromUrl(oldImageUrl);
                storageReferenceNew.delete();
                if (task.isSuccessful()) {
                    Toast.makeText(getActivity(), "Data Updated", Toast.LENGTH_SHORT).show();
                    ThingsListFragment thingsListFragment = new ThingsListFragment();
                    getFragmentManager().beginTransaction()
                            .replace(R.id.mainFragment, thingsListFragment)
                            .commit();
                }
            }
        });

    }

}