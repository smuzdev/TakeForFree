package com.smuzdev.takeforfree.fragments;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.smuzdev.takeforfree.R;
import com.smuzdev.takeforfree.activities.MainActivity;

public class DetailsFragment extends Fragment {

    TextView thingName, thingDescription, thingDiscoveryDate, thingDiscoveryPlace,
            thingPickupPoint, userName, userEmail, userPhone;
    Button btnUpdate, btnDelete;
    ImageView thingImage;
    String key = "";
    String imageUrl = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Details");

        thingName = view.findViewById(R.id.txtThingName);
        thingDescription = view.findViewById(R.id.txtThingDescription);
        thingDiscoveryDate = view.findViewById(R.id.txtThingDiscoveryDate);
        thingDiscoveryPlace = view.findViewById(R.id.txtThingDiscoveryPlace);
        thingPickupPoint = view.findViewById(R.id.txtThingPickupPoint);
        userName = view.findViewById(R.id.txtUserName);
        userEmail = view.findViewById(R.id.txtUserEmail);
        userPhone = view.findViewById(R.id.txtUserPhone);
        thingImage = view.findViewById(R.id.ivImage2);
        btnUpdate = view.findViewById(R.id.btnUpdate);
        btnDelete = view.findViewById(R.id.btnDelete);

        Bundle mBundle = this.getArguments();

        if (mBundle != null) {

            thingName.setText(mBundle.getString("ThingName"));
            thingDescription.setText(mBundle.getString("ThingDescription"));
            thingDiscoveryDate.setText(mBundle.getString("ThingDiscoveryDate"));
            thingDiscoveryPlace.setText(mBundle.getString("ThingDiscoveryPlace"));
            thingPickupPoint.setText(mBundle.getString("ThingPickupPoint"));
            userName.setText(mBundle.getString("UserName"));
            userPhone.setText(mBundle.getString("UserEmail"));
            userEmail.setText(mBundle.getString("UserPhone"));
            key = mBundle.getString("KeyValue");
            imageUrl = mBundle.getString("ThingImage");

            Glide.with(this)
                    .load(mBundle.getString("ThingImage"))
                    .into(thingImage);

        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putString("thingNameKey", thingName.getText().toString());
                bundle.putString("thingDescriptionKey", thingDescription.getText().toString());
                bundle.putString("thingDiscoveryDateKey", thingDiscoveryDate.getText().toString());
                bundle.putString("thingDiscoveryPlaceKey", thingDiscoveryPlace.getText().toString());
                bundle.putString("thingPickupPointKey", thingPickupPoint.getText().toString());
                bundle.putString("userNameKey", userName.getText().toString());
                bundle.putString("userPhoneKey", userPhone.getText().toString());
                bundle.putString("userEmailKey", userEmail.getText().toString());
                bundle.putString("oldImageUrl", imageUrl);
                bundle.putString("key", key);

                UpdateFragment updateFragment = new UpdateFragment();
                updateFragment.setArguments(bundle);
                getFragmentManager().beginTransaction()
                        .add(R.id.mainFragment, updateFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder
                        .setTitle("Delete")
                        .setMessage("Do you want to delete item?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Things");
                                FirebaseStorage storage = FirebaseStorage.getInstance();
                                StorageReference storageReference = storage.getReferenceFromUrl(imageUrl);

                                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        reference.child(key).removeValue();

                                    }

                                });

                                Toast.makeText(getActivity(), "Thing deleted", Toast.LENGTH_SHORT).show();
                                ThingsListFragment thingsListFragment = new ThingsListFragment();
                                getFragmentManager().beginTransaction()
                                        .replace(R.id.mainFragment, thingsListFragment)
                                        .commit();
                            }
                        })
                        .setNegativeButton("Cancel", null).create().show();
            }
        });
    }
}