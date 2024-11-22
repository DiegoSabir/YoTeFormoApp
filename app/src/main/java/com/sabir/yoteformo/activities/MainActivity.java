package com.sabir.yoteformo.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sabir.yoteformo.R;
import com.sabir.yoteformo.fragments.HomeFragment;
import com.sabir.yoteformo.fragments.SearchFragment;

public class MainActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private BottomNavigationView bnvMenu;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Meusflis");

        bnvMenu = findViewById(R.id.bnvMenu);

        userId = getIntent().getStringExtra("userId");

        Fragment homeFragment = new HomeFragment();
        Fragment searchFragment = new SearchFragment();

        Bundle bundle = new Bundle();
        bundle.putString("userId", userId);
        homeFragment.setArguments(bundle);

        if (savedInstanceState == null){
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, homeFragment).commit();
        }

        bnvMenu.setOnNavigationItemSelectedListener(item -> {
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (item.getItemId() == R.id.nav_home){
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                homeFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.fragment_container, homeFragment).commit();
            }
            else if (item.getItemId() == R.id.nav_search){
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                searchFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.fragment_container, searchFragment).commit();
            }
            return true;
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
}