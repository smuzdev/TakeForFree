package com.smuzdev.takeforfree.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.smuzdev.takeforfree.Adapter;
import com.smuzdev.takeforfree.R;
import com.smuzdev.takeforfree.models.Thing;

import java.util.ArrayList;
import java.util.List;

public class ThingsListFragment extends Fragment /*implements Postman*/ {

    RecyclerView mRecyclerView;
    List<Thing> thingList;
    ProgressDialog progressDialog;
    EditText txt_search;
    GridLayoutManager gridLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_things_list, container, false);

        Log.i("CheckLC", "onCreateView");
//      ((Postman) getActivity()).fragmentMail(fragmentGridLayoutManager);

        mRecyclerView = view.findViewById(R.id.recyclerView);
        txt_search = view.findViewById(R.id.txt_searchText);

        gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading items...");
        thingList = new ArrayList<>();

        final Adapter myAdapter = new Adapter(getContext(), thingList);
        mRecyclerView.setAdapter(myAdapter);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Things");
        progressDialog.show();
        ValueEventListener eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                thingList.clear();

                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {

                    Thing thing = itemSnapshot.getValue(Thing.class);
                    thing.setKey(itemSnapshot.getKey());
                    thingList.add(thing);
                }
                myAdapter.notifyDataSetChanged();
                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });

        txt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }

            private void filter(String text) {

                ArrayList<Thing> filterList = new ArrayList<>();
                for (Thing thing : thingList) {

                    if (thing.getThingName().toLowerCase().contains(text.toLowerCase())) {
                        filterList.add(thing);
                    }

                }

                myAdapter.filteredList(filterList);

            }
        });

        registerForContextMenu(mRecyclerView);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("CheckLC", "onResume");
        gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        mRecyclerView.setLayoutManager(gridLayoutManager);
    }

    @Override
    public boolean onContextItemSelected(@NonNull final MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                Bundle bundle = new Bundle();
                bundle.putString("thingNameKey", thingList.get(item.getGroupId()).getThingName());
                bundle.putString("thingDescriptionKey", thingList.get(item.getGroupId()).getThingDescription());
                bundle.putString("thingPublicationDateKey", thingList.get(item.getGroupId()).getThingPublicationDate());
                bundle.putString("thingUseDurationKey", thingList.get(item.getGroupId()).getThingUseDuration());
                bundle.putString("thingPickupPointKey", thingList.get(item.getGroupId()).getThingPickupPoint());
                bundle.putString("userNameKey", thingList.get(item.getGroupId()).getUserName());
                bundle.putString("userPhoneKey", thingList.get(item.getGroupId()).getUserPhone());
                bundle.putString("userEmailKey", thingList.get(item.getGroupId()).getUserEmail());
                bundle.putString("oldImageUrl", thingList.get(item.getGroupId()).getThingImage());
                bundle.putString("key", thingList.get(item.getGroupId()).getKey());

                UpdateFragment updateFragment = new UpdateFragment();
                updateFragment.setArguments(bundle);
                getFragmentManager().beginTransaction()
                        .add(R.id.mainFragment, updateFragment)
                        .addToBackStack(null)
                        .commit();
                return true;
            case 2:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder
                        .setTitle("Delete")
                        .setMessage("Do you want to delete item?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Things");
                                FirebaseStorage storage = FirebaseStorage.getInstance();
                                StorageReference storageReference = storage.getReferenceFromUrl(thingList.get(item.getGroupId()).getThingImage());

                                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        reference.child(thingList.get(item.getGroupId()).getKey()).removeValue();
                                        Toast.makeText(getActivity(), "Thing deleted", Toast.LENGTH_SHORT).show();
                                        ThingsListFragment thingsListFragment = new ThingsListFragment();
                                        getFragmentManager().beginTransaction()
                                                .replace(R.id.mainFragment, thingsListFragment)
                                                .commit();
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Cancel", null).create().show();
                return true;
            default:
                super.onContextItemSelected(item);
        }
        return false;
    }

//    @Override
//    public void fragmentMail(GridLayoutManager fragmentGridLayoutManager) { }
//
//    public void btn_uploadActivity(View view) {
//
//        startActivity(new Intent(getActivity(), UploadActivity.class));
//
//    }

}