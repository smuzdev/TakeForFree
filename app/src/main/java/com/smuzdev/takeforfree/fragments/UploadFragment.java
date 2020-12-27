package com.smuzdev.takeforfree.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.smuzdev.takeforfree.CustomTextWatcher;
import com.smuzdev.takeforfree.R;
import com.smuzdev.takeforfree.activities.MainActivity;
import com.smuzdev.takeforfree.models.Thing;
import com.smuzdev.takeforfree.models.User;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import static android.app.Activity.RESULT_OK;

public class UploadFragment extends Fragment {

    Uri uri;
    User user;
    FirebaseUser FB_user;
    Button selectImageButton;
    String txt_thingDiscoveryDate;
    EditText txt_thingName, txt_thingDescription, txt_thingDiscoveryPlace, txt_thingPickupPoint;
    ImageView thingImage;
    String imageUrl, userName, userEmail, userPhone;
    MaterialButton uploadButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Upload new thing");

        thingImage = view.findViewById(R.id.iv_thingImage);
        txt_thingName = view.findViewById(R.id.txtThingName);
        txt_thingDescription = view.findViewById(R.id.txtThingDescription);
        txt_thingDiscoveryPlace = view.findViewById(R.id.txtThingDiscoveryPlace);
        txt_thingPickupPoint = view.findViewById(R.id.txtThingPickupPoint);
        selectImageButton = view.findViewById(R.id.select_image_button);
        uploadButton = view.findViewById(R.id.uploadButton);
        uploadButton.setEnabled(false);
        uploadButton.getBackground().setAlpha(128);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                startActivityForResult(photoPicker, 1);
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        EditText[] editTexts = {txt_thingName, txt_thingDescription, txt_thingDiscoveryPlace, txt_thingPickupPoint};
        CustomTextWatcher textWatcher = new CustomTextWatcher(editTexts, uploadButton);
        for (EditText editText : editTexts) {
            editText.addTextChangedListener(textWatcher);
        }

        FB_user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                    System.out.println(itemSnapshot.getKey());
                    if (itemSnapshot.getKey().equals(FB_user.getUid())) {
                        user = itemSnapshot.getValue(User.class);
                        userName = user.getName();
                        userEmail = user.getEmail();
                        userPhone = user.getPhone();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
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

    public void uploadImage() {

        StorageReference storageReference = FirebaseStorage.getInstance()
                .getReference().child("ThingImage").child(uri.getLastPathSegment() + System.currentTimeMillis());

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Thing Uploading...");
        progressDialog.show();

        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete()) ;
                Uri urlImage = uriTask.getResult();
                imageUrl = urlImage.toString();
                uploadThing();
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "Image uploaded!", Toast.LENGTH_LONG).show();
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void uploadThing() {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDate localDate = LocalDate.now();
        txt_thingDiscoveryDate = dtf.format(localDate);

        Thing thing = new Thing(
                txt_thingName.getText().toString(),
                txt_thingDescription.getText().toString(),
                txt_thingDiscoveryDate,
                txt_thingDiscoveryPlace.getText().toString(),
                txt_thingPickupPoint.getText().toString(),
                imageUrl,
                userName,
                userPhone,
                userEmail
        );

        FirebaseDatabase.getInstance().getReference("Things")
                .child(thing.getThingName()).setValue(thing).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    Toast.makeText(getActivity(), "New thing uploaded!", Toast.LENGTH_LONG).show();
                    ThingsListFragment thingsListFragment = new ThingsListFragment();
                    getFragmentManager().beginTransaction()
                            .replace(R.id.mainFragment, thingsListFragment)
                            .addToBackStack(null)
                            .commit();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}
