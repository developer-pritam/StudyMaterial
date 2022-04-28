package com.study_material.auth;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.study_material.HomeActivity;
import com.study_material.R;

import java.util.HashMap;
import java.util.Map;

public class SignInWithGoogle {

    String TAG = "SMSD";
    FirebaseAuth mAuth;
    Fragment context;
    FirebaseFirestore db;
    MaterialCardView googleSignIn;

    public SignInWithGoogle(FirebaseAuth mAuth, Fragment context, MaterialCardView googleSignIn) {
        this.mAuth = mAuth;
        this.context = context;
        this.googleSignIn = googleSignIn;
    }

    void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        try {

                            FirebaseUser user = mAuth.getCurrentUser();

                            long creationTime = user.getMetadata().getCreationTimestamp();
                            long lastSignIn = user.getMetadata().getLastSignInTimestamp();
                            if (lastSignIn - creationTime < 1000) {
                                handleNewUser(user);
                            } else {
                                context.startActivity(new Intent(context.getActivity(), HomeActivity.class));
                            }


                        } catch (Exception e) {

                        }
                    } else {
                        Snackbar.make(googleSignIn, "Something went wrong", Snackbar.LENGTH_SHORT).show();
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                    }
                });
    }

    public void handleNewUser(FirebaseUser user) {

        db = FirebaseFirestore.getInstance();
        Map<String, String> userData = new HashMap<>();
        userData.put("name", user.getDisplayName());
        userData.put("email", user.getEmail());
        userData.put("UserID", user.getUid());

// Add a new document with a generated ID
        try {

            db.collection("users")
                    .document(user.getUid())
                    .set(userData)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        context.startActivity(new Intent(context.getActivity(), HomeActivity.class));
                    })

                    .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }


}
