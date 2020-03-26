package com.example.mychat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.mychat.Activities.PostActivity;
import com.example.mychat.Adapters.PostAdapter;
import com.example.mychat.Models.PostModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity implements ValueEventListener
{
    private static final String TAG = "HomeActivity";

    private MaterialSearchView searchView;
    private List<PostModel> postModelList;
    private RecyclerView mRecyclerView;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
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
        mRecyclerView = findViewById(R.id.home_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);

        postModelList = new ArrayList<>();

        FirebaseDatabase
                .getInstance()
                .getReference("Posts")
                .addValueEventListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.action_bar_menu, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {

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
    public void onBackPressed()
    {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
    {
        for (DataSnapshot parent : dataSnapshot.getChildren())
        {
            Log.d(TAG, "onDataChange: Parent Children : " + parent);
            for (DataSnapshot children : parent.getChildren())
            {
                PostModel postModel = children.getValue(PostModel.class);
//                postModelList.add(postModel);
                postModelList.add(0, postModel);
                Log.d(TAG, "onDataChange: ----------------> : username " + postModel.getUserName());

            }
        }

        PostAdapter adapter = new PostAdapter(HomeActivity.this, postModelList);
        mRecyclerView.setAdapter(adapter);


    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError)
    {

    }
}
