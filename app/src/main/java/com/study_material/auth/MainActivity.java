package com.study_material.auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.study_material.HomeActivity;
import com.study_material.R;

public class MainActivity extends AppCompatActivity implements LoginFragment.CommunicationInterface {
    String TAG = "SMSD";
    ViewPager2 viewPager2;
    ViewPagerAdapter adapter;
    Button login, signUp, resendMail;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_StudyMaterial);
        setContentView(R.layout.activity_main);

        resendMail = findViewById(R.id.resendVerificationMail);

        //Initializing Firebase and getting current User
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUser.reload().addOnSuccessListener(unused -> {
                currentUser = mAuth.getCurrentUser();
                //Handling UI

            });
        }
        handleUI(currentUser);

        resendMail.setOnClickListener(v -> {
            currentUser.sendEmailVerification();
            Toast.makeText(this, "Verification mail sent, Please check your mail", Toast.LENGTH_SHORT).show();
        });

    }


    public void handleUI(FirebaseUser userDetail) {
        if (userDetail != null) {

            if (userDetail.isEmailVerified() || userDetail.isAnonymous()) {
                startActivity(new Intent(this, HomeActivity.class));
            } else {
               // Toast.makeText(this, "Email not verified", Toast.LENGTH_SHORT).show();
                //If Email Not Verified

                String labelText = "Email verification link has sent to your email id - <b>" + userDetail.getEmail() + "</b>" ;
                TextView emailLabel = findViewById(R.id.emailVerificationLabel);
                emailLabel.setText(Html.fromHtml(labelText));

                Button verifyMail  = findViewById(R.id.verifyMail);
                verifyMail.setOnClickListener(v->{
                    handleUI(currentUser);
                });

                TextView logoutText = findViewById(R.id.emailVerifyLogout);
                logoutText.setOnClickListener(v->{
                    mAuth.signOut();
                    handleUI(mAuth.getCurrentUser());
                });

                findViewById(R.id.loginLayout).setVisibility(View.GONE);
                findViewById(R.id.splash).setVisibility(View.GONE);
                findViewById(R.id.email_verify).setVisibility(View.VISIBLE);
            }

        } else {
            //If User not logged in
            findViewById(R.id.splash).setVisibility(View.GONE);
            findViewById(R.id.email_verify).setVisibility(View.GONE);

            findViewById(R.id.loginLayout).setVisibility(View.VISIBLE);
            handleLoginUI();
        }

    }

    public void handleLoginUI() {
        viewPager2 = findViewById(R.id.viewPager);
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), getLifecycle());
        viewPager2.setAdapter(adapter);
        login = findViewById(R.id.logIn);
        signUp = findViewById(R.id.signUp);

        login.setBackgroundColor(Color.alpha(0));
        login.setTextColor(getResources().getColor(R.color.purple_500));
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == 1) {
                    setLogin(null);
                } else {
                    setSignUp(null);
                }
            }
        });


    }

    public void setLogin(View view) {
        login.setBackgroundColor(getResources().getColor(R.color.purple_500));
        signUp.setBackgroundColor(Color.alpha(0));
        login.setTextColor(getResources().getColor(R.color.white));
        signUp.setTextColor(getResources().getColor(R.color.purple_500));
        if (view != null) {
            viewPager2.setCurrentItem(1);
        }
    }

    public void setSignUp(View view) {
        signUp.setBackgroundColor(getResources().getColor(R.color.purple_500));
        login.setBackgroundColor(Color.alpha(0));
        signUp.setTextColor(getResources().getColor(R.color.white));
        login.setTextColor(getResources().getColor(R.color.purple_500));

        if (view != null) {
            viewPager2.setCurrentItem(0);
        }
    }

    @Override
    public void emailVerify() {
        currentUser = mAuth.getCurrentUser();
        //  user.sendEmailVerification();
        handleUI(currentUser);


        //  Toast.makeText(this, "Email Verify h", Toast.LENGTH_SHORT).show();
    }
}