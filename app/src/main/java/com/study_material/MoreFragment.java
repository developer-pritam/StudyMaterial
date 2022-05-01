package com.study_material;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.study_material.auth.MainActivity;

public class MoreFragment extends Fragment {


    View view;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    boolean first = true;
    TextView userName;
    TextView userEmail;
    Button logoutBtn;
    public MoreFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (first) {
            view = inflater.inflate(R.layout.fragment_more, container, false);
            firebaseAuth = FirebaseAuth.getInstance();
            user = firebaseAuth.getCurrentUser();
            userName = view.findViewById(R.id.userName);
            userEmail = view.findViewById(R.id.userEmail);
            userName.setText(user.getDisplayName());
            userEmail.setText(user.getEmail());

            logoutBtn = view.findViewById(R.id.logOut);
            logoutBtn.setOnClickListener(v -> {
                firebaseAuth.signOut();
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);

                getActivity().finish();

            });
            first = false;
        }

        return view;
    }
}