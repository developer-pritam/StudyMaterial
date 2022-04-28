package com.study_material.auth;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);

    }

    SignUpFragment signUpFragment;
    LoginFragment loginFragment;

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            if (signUpFragment == null)
                signUpFragment = new SignUpFragment();
            return signUpFragment;
        } else {
            if (loginFragment == null)
                loginFragment = new LoginFragment();
            return loginFragment;
        }

    }


    @Override
    public int getItemCount() {
        return 2;
    }
}
