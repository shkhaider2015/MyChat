package com.example.mychat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.mychat.Activities.PostActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.Objects;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";

    private MaterialSearchView searchView;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Objects.requireNonNull(getSupportActionBar()).show();
        init();
        handleSearch();

    }

    private void init()
    {
        mAuth = FirebaseAuth.getInstance();
        searchView = findViewById(R.id.home_search_view);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_menu, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        boolean tmp = false;

        switch (item.getItemId())
        {
            case R.id.menu_logout:
                startActivity(new Intent(HomeActivity.this, SiginActivity.class));
                tmp = true;
                mAuth.signOut();
                finish();
                break;
            case R.id.menu_update_profile:
                startActivity(new Intent(HomeActivity.this, UpdateActivity.class));
                tmp = true;
                break;
            case R.id.menu_create_post:
                startActivity(new Intent(HomeActivity.this, PostActivity.class));
                tmp = true;
                break;
        }

        return  tmp;
    }

    private void handleSearch()
    {
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(HomeActivity.this, "On Query Text Submit", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onQueryTextSubmit: clicked");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Toast.makeText(HomeActivity.this, "On Query Text Change", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onQueryTextChange: clicked");
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                Toast.makeText(HomeActivity.this, "On Search View shown", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onSearchViewShown: clicked");
            }

            @Override
            public void onSearchViewClosed() {
                Toast.makeText(HomeActivity.this, "On Search View Close", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onSearchViewClosed: clicked");

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }
}
