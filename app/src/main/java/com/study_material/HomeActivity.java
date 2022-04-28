package com.study_material;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.study_material.HomeFeed.HomeFragment;
import com.study_material.upload.UploadFragment;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "SMSD";
    private FirebaseAuth mAuth;
    NavigationBarView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_StudyMaterial);
        setContentView(R.layout.activity_home);
        mAuth = FirebaseAuth.getInstance();

        bottomNavigationView = findViewById(R.id.bottomNavigation);

        bottomNavigationView.setOnItemSelectedListener(this::navigationHandler);
        bottomNavigationView.setSelectedItemId(R.id.home_menu);

    }


    
    Fragment homeFragment, favoritesFragment, uploadFragment, downloadsFragments, moreFragments;
    public boolean navigationHandler(MenuItem item) {

        if (item.getItemId() == R.id.home_menu) {
            if (homeFragment == null)
                homeFragment = new HomeFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.mainFragmentContainer, homeFragment).commit();

        } else if (item.getItemId() == R.id.favorite_menu) {
            if (favoritesFragment == null) {
                favoritesFragment = new FavoritesFragment();
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.mainFragmentContainer, favoritesFragment).commit();
            return true;
        } else if (item.getItemId() == R.id.add_menu) {
            if (uploadFragment == null)
                uploadFragment = new UploadFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.mainFragmentContainer, uploadFragment).commit();
            return true;

        } else if (item.getItemId() == R.id.downloads_menu) {
            if (downloadsFragments == null)
                downloadsFragments = new DownloadsFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.mainFragmentContainer, downloadsFragments).commit();
            return true;

        } else if (item.getItemId() == R.id.more_menu) {
            if (moreFragments == null)
                moreFragments = new MoreFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.mainFragmentContainer, moreFragments).commit();
            return true;

        }

        return true;
    }

}