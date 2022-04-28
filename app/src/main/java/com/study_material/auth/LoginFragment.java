package com.study_material.auth;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.transition.Hold;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.study_material.HomeActivity;
import com.study_material.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;

public class LoginFragment extends Fragment {

    private static final String TAG = "SMSD";
    private static final int RC_SIGN_IN = 123;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    TextInputLayout emailLayout, passwordLayout, forgetPasswordText;
    Button logInSubmit, forgetPasswordBtn;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog pd;
    private FirebaseAuth mAuth;
    View view;
    BottomSheetDialog bottomSheetDialog;
    TextView forgetPassword;
    CommunicationInterface communicationInterface = (MainActivity) getActivity();
    FirebaseFirestore db;
    MaterialCardView googleSignIn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_login, container, false);
        handleForgetPassword();
        mAuth = FirebaseAuth.getInstance();
        logInSubmit = view.findViewById(R.id.logInSubmit);

        logInSubmit.setOnClickListener(v -> {
            logInHandler();
        });
        communicationInterface = (MainActivity) getActivity();
        pd = new ProgressDialog(getActivity());
        googleSignIn = view.findViewById(R.id.googleLogin);
        googleSignIn.setOnClickListener(this::callGoogleSignIn);

        return view;

    }

    private void logInHandler() {
        emailLayout = view.findViewById(R.id.emailText);
        passwordLayout = view.findViewById(R.id.passwordText);

        String email = emailLayout.getEditText().getText().toString().trim();
        String password = passwordLayout.getEditText().getText().toString();

        if (!email.matches(emailPattern) || email.length() < 4) {

            emailLayout.setError("Please enter a valid email-id");
        } else if (password.length() < 7) {
            emailLayout.setError(null);
            passwordLayout.setError("Invalid Password");
        } else {
            emailLayout.setError(null);
            passwordLayout.setError(null);

            pd.setTitle("Please Wait");
            pd.setMessage("Verifying credentials...");
            pd.setCancelable(false);

            View view = getActivity().getCurrentFocus();
            if (view != null) {
                InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }


            pd.show();


            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
//                                updateUI(user);
                            communicationInterface.emailVerify();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(getActivity(), task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//                                updateUI(null);
                        }
                        pd.cancel();
                    });

        }
    }

    public void handleForgetPassword() {
        forgetPassword = view.findViewById(R.id.forgetPassword);
        forgetPassword.setOnClickListener(v -> {
            bottomSheetDialog = new BottomSheetDialog(requireContext());
            bottomSheetDialog.setContentView(R.layout.fragment_forget_password);
            try {
                forgetPasswordBtn = bottomSheetDialog.findViewById(R.id.forgetPasswordBtn);
                forgetPasswordText = bottomSheetDialog.findViewById(R.id.forgetEmailText);
                forgetPasswordBtn.setOnClickListener(fp -> {
                    passwordReset(forgetPasswordText);
                });

                ImageView close = bottomSheetDialog.findViewById(R.id.closeBottomDrawable);
                close.setOnClickListener(vv -> {
                    bottomSheetDialog.hide();
                });
            } catch (Exception e) {
                Log.e("SMSD", "E", e);
            }
            bottomSheetDialog.show();
        });
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

    public void passwordReset(TextInputLayout emailLayout) {
        String mail = emailLayout.getEditText().getText().toString().trim();
        pd.setTitle("Please wait");
        pd.setMessage("Sending password reset mail.");
        pd.setCancelable(false);
        if (!mail.matches(emailPattern) || mail.length() < 4) {
            emailLayout.setError("Please enter a valid emailID");
        } else {
            emailLayout.setError(null);

            mAuth.sendPasswordResetEmail(mail)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                            Toast.makeText(getActivity(), "Password reset mail sent", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                        pd.cancel();

                    });
        }
    }

    protected interface CommunicationInterface {
        void emailVerify();
    }

}