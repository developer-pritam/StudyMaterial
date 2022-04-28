package com.study_material.auth;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.study_material.HomeActivity;
import com.study_material.R;

import java.util.HashMap;
import java.util.Map;

public class SignUpFragment extends Fragment {


    public SignUpFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private static final String TAG = "SMSD";
    private static final int RC_SIGN_IN = 124;

    View view;
    MaterialCardView googleSignIn;
    FirebaseAuth mAuth;
    TextInputLayout nameLayout, emailLayout, passwordLayout;
    Button signupSubmit;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    FirebaseFirestore db;
    ProgressDialog pd;
    TextView skipBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        mAuth = FirebaseAuth.getInstance();
        pd = new ProgressDialog(getActivity());

        googleSignIn = view.findViewById(R.id.googleSignUp);
        googleSignIn.setOnClickListener(this::callGoogleSignIn);
        signupSubmit = view.findViewById(R.id.signUpSubmit);
        skipBtn = view.findViewById(R.id.skipLabel);
        skipBtn.setOnClickListener(v -> handleSkip());

        signupSubmit.setOnClickListener(v -> {
            submitHandler();
        });


        return view;
    }

    private void handleSkip() {
        pd.setTitle("Please Wait");
        pd.setMessage("Signing-up...");
        pd.setCancelable(false);
        pd.show();
        mAuth.signInAnonymously()
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInAnonymously:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            communication = (MainActivity) getActivity();
                            pd.cancel();
                            communication.emailVerify();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInAnonymously:failure", task.getException());
                            Toast.makeText(getActivity(), task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    LoginFragment.CommunicationInterface communication;
    String name;
    public void submitHandler() {
        nameLayout = view.findViewById(R.id.nameText);
        emailLayout = view.findViewById(R.id.emailText);
        passwordLayout = view.findViewById(R.id.passwordText);

         name = nameLayout.getEditText().getText().toString().trim();
        String email = emailLayout.getEditText().getText().toString().trim();
        String password = passwordLayout.getEditText().getText().toString();
        Log.d(TAG, email + "xc");
        if (name.length() < 3) {
            nameLayout.setError("Please enter a valid name");
        } else if (!email.matches(emailPattern) || email.length() < 4) {
            nameLayout.setError(null);

            emailLayout.setError("Please enter a valid email-id");
        } else if (password.length() < 7) {
            nameLayout.setError(null);
            emailLayout.setError(null);
            passwordLayout.setError("Please enter password with more than 6 characters");
        } else {
            nameLayout.setError(null);
            emailLayout.setError(null);
            passwordLayout.setError(null);

            pd.setTitle("Please Wait");
            pd.setMessage("Signing-up...");
            pd.setCancelable(false);

            View view = getActivity().getCurrentFocus();
            if (view != null) {
                InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }


            pd.show();


            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            handleNewUser(user);
                            user.sendEmailVerification();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build();
                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(task1 -> {
                                        if (!task1.isSuccessful()) {
                                            Toast.makeText(getActivity(), " Failed to add name, Please update it in profile.", Toast.LENGTH_SHORT).show();
                                        }
                                        //   communication.emailVerify();


                                    });
                            pd.cancel();
                        } else {
                            // If sign in fails, display a message to the user.
                            pd.cancel();
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getActivity(), task.getException().getLocalizedMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

        }


    }

    public void handleNewUser(FirebaseUser user) {

        db = FirebaseFirestore.getInstance();
        Map<String, String> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("email", user.getEmail());
        userData.put("UserID", user.getUid());

// Add a new document with a generated ID
        try {

            db.collection("users")
                    .document(user.getUid())
                    .set(userData)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
//                        startActivity(new Intent(context.getActivity(), HomeActivity.class));
                        communication = (MainActivity) getActivity();

                        assert communication != null;
                        communication.emailVerify();
                    })
                    .addOnFailureListener(e -> {
                        Log.w(TAG, "Error writing document", e);
                        Toast.makeText(getActivity(), "Some thing went wrong", Toast.LENGTH_SHORT).show();
                        communication = (MainActivity) getActivity();

                        assert communication != null;
                        communication.emailVerify();

                    });

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }


    public void callGoogleSignIn(View v) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                SignInWithGoogle sGoogle = new SignInWithGoogle(mAuth, this, googleSignIn);

                sGoogle.firebaseAuthWithGoogle(account.getIdToken());


            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

}