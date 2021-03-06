package com.smuzdev.takeforfree.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.android.material.navigation.NavigationView;
import com.smuzdev.takeforfree.R;
import com.smuzdev.takeforfree.fragments.ThingsListFragment;
import com.smuzdev.takeforfree.fragments.UploadFragment;

public class MainActivity extends AppCompatActivity /*implements Postman*/ {

    GridLayoutManager gridLayoutManager;
    ThingsListFragment thingsListFragment;
    RecyclerView mRecyclerView;
    Context context = this;
    private FrameLayout listContainer;
    private FrameLayout detailsContainer;

    Toolbar toolbar;
    NavigationView navigationView;
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("CheckLC", "onCreateActivity");

        thingsListFragment = new ThingsListFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.mainFragment, thingsListFragment)
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("CheckLC", "onResumeActivity");
    }

    @Override
    protected void onStart() {
        super.onStart();
//        mRecyclerView = findViewById(R.id.recyclerView);
//        mRecyclerView.setLayoutManager(gridLayoutManager);
        Log.i("CheckLC", "onStartActivity");

        //Drawer
        // <---- ----->
        toolbar = findViewById(R.id.main_toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.app_name,
                R.string.app_name
        );

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
//                    case R.id.show_list_view:
//                        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//                        break;
//                    case R.id.show_table_view:
//                        gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
//                        mRecyclerView.setLayoutManager(gridLayoutManager);
//                        break;
                    case R.id.upload_thing:
                        UploadFragment uploadFragment = new UploadFragment();
                        getSupportFragmentManager().beginTransaction()
                                .add(R.id.mainFragment, uploadFragment)
                                .addToBackStack(null)
                                .commit();
                        break;
                }
                return true;
            }
        });
        // <---- ----->
    }

//    @Override
//    public void fragmentMail(GridLayoutManager fragmentGridLayoutManager) {
//        gridLayoutManager = fragmentGridLayoutManager;
//    }

}