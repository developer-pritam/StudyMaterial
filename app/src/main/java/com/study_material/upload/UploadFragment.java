package com.study_material.upload;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.study_material.R;
import com.study_material.upload.CourseModel;

import java.util.ArrayList;
import java.util.HashMap;

public class UploadFragment extends Fragment {


    private static final String TAG = "SMSD";
    FirebaseFirestore db;
    FirebaseAuth auth;
    FirebaseStorage storageFB;
    StorageReference storageRef;
    View view;

    FirebaseUser currentUser;
    boolean first = true;
    AutoCompleteTextView categoryItems;
    AutoCompleteTextView branchItems;
    AutoCompleteTextView subjectItems;
    MaterialCardView pickFile;
    String fileName;
    int fileSize;
    Uri fileUri;
    TextView filePickerLabel;
    ArrayAdapter courseAdapter;
    ArrayAdapter branchAdapter;
    ArrayAdapter subjectAdapter;
    ArrayList<CourseModel> courseList = new ArrayList<>();
    ArrayList<CourseModel> branchList = new ArrayList<>();
    ArrayList<CourseModel> subjectList = new ArrayList<>();
    TextInputLayout addCourse;
    TextInputLayout addBranch;
    TextInputLayout addSubject;
    TextInputLayout branchLayout;
    TextInputLayout subjectLayout;
    TextInputLayout shortDesc;
    TextInputLayout longDesc;
    Button submitBtn;
    String courseSelected;
    String branchSelected;
    String subjectSelected;

    String courseSelectedName;
    String branchSelectedName;
    String subjectSelectedName;


    ProgressDialog pd;
    Uri downloadUri;

    public UploadFragment() {
        // Required empty public constructor
    }

    static String getAlphaNumericString(int n) {

        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index = (int) (AlphaNumericString.length() * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        if (first) {
            auth = FirebaseAuth.getInstance();
            currentUser = auth.getCurrentUser();
            db = FirebaseFirestore.getInstance();
            storageFB = FirebaseStorage.getInstance();
            storageRef = storageFB.getReference();
            getCourses();
            view = inflater.inflate(R.layout.fragment_upload, container, false);
            first = false;
            branchLayout = view.findViewById(R.id.branchDropDown);
            subjectLayout = view.findViewById(R.id.subjectDropDown);
            filePickerLabel = view.findViewById(R.id.filePickerLabel);
            pickFile = view.findViewById(R.id.pickFile);
            submitBtn = view.findViewById(R.id.submitUpload);
            longDesc = view.findViewById(R.id.longDesc);
            shortDesc = view.findViewById(R.id.shortDesc);
            categoryItems = view.findViewById(R.id.categoryItems);
            branchItems = view.findViewById(R.id.branchItems);
            subjectItems = view.findViewById(R.id.subjectItems);
            addCourse = view.findViewById(R.id.addNewCourse);
            addSubject = view.findViewById(R.id.newSubjects);
            addBranch = view.findViewById(R.id.newBranch);
            Log.d(TAG, "Test");

            courseAdapter = new ArrayAdapter(getContext(), R.layout.list_item, courseList);
            subjectAdapter = new ArrayAdapter(getContext(), R.layout.list_item, subjectList);
            branchAdapter = new ArrayAdapter(getContext(), R.layout.list_item, branchList);
            categoryItems.setOnItemClickListener((parent, view, position, id) -> handleOnCategorySelection(position));
            branchItems.setOnItemClickListener((parent, view, position, id) -> handleOnBranchSelection(position));
            subjectItems.setOnItemClickListener((parent, view, position, id) -> handleOnSubjectSelection(position));
            pickFile.setOnClickListener(v -> callFilePicker());
            submitBtn.setOnClickListener(v -> onSubmitHandler());

            addCourse.getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() != 0) {
                        courseSelectedName = s.toString();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            addBranch.getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() != 0) {
                        branchSelectedName = s.toString();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            addSubject.getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() != 0) {
                        subjectSelectedName = s.toString();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

        }
        categoryItems.setAdapter(courseAdapter);
        subjectItems.setAdapter(subjectAdapter);
        branchItems.setAdapter(branchAdapter);


        return view;
    }

    private void onSubmitHandler() {
        if (currentUser.isAnonymous()) {
            Snackbar.make(view, "Please sign-in to upload files", Snackbar.LENGTH_SHORT).setAnchorView(R.id.bottomNavigation).show();
        } else if (courseSelectedName == null) {
            Snackbar.make(view, "Please select a course", Snackbar.LENGTH_SHORT).setAnchorView(R.id.bottomNavigation).show();
        } else if (branchSelectedName == null) {
            Snackbar.make(view, "Please select a branch", Snackbar.LENGTH_SHORT).setAnchorView(R.id.bottomNavigation).show();
        } else if (subjectSelectedName == null) {
            Snackbar.make(view, "Please select a subject", Snackbar.LENGTH_SHORT).setAnchorView(R.id.bottomNavigation).show();
        } else if (shortDesc.getEditText().getText().length() < 5) {
            Snackbar.make(view, "Please write valid title", Snackbar.LENGTH_SHORT).setAnchorView(R.id.bottomNavigation).show();
        } else if (fileName == null) {
            Snackbar.make(view, "Please add a notes", Snackbar.LENGTH_SHORT).setAnchorView(R.id.bottomNavigation).show();

        } else {
            callUploadFile();
            pd = new ProgressDialog(getActivity());
            pd.setTitle("Please wait...");
            pd.setMessage("Uploading file to server");
            pd.setCancelable(true);
            pd.show();
            Log.d(TAG, fileName + fileUri);
        }

    }

    private void callUploadFile() {


        //   StorageReference fileNameFB = storageRef.child(fileName);
        //   Uri file = Uri.fromFile(new File("path/to/images/rivers.jpg"));
        StorageReference notesRef = storageRef.child("Notes/" + getAlphaNumericString(8) + "__" + fileName);
        UploadTask uploadTask = notesRef.putFile(fileUri);


        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return notesRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    downloadUri = task.getResult();
                    Log.d(TAG, downloadUri.toString());


                    pd.setMessage("Uploading metadata to database");

                    callUploadData();
                } else {
                    // Handle failures
                    // ...
                }
            }
        });

// Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads

                Log.e(TAG, "Failed", exception);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                Log.d(TAG, "Success");
                Log.d(TAG, taskSnapshot.getMetadata().getPath());

            }
        });
    }

    private void callUploadData() {

        HashMap<String, Object> uploadDada = new HashMap<>();
        uploadDada.put("userUID", currentUser.getUid());
        uploadDada.put("uri", downloadUri.toString());
        uploadDada.put("branch", branchSelectedName);
        uploadDada.put("course", courseSelectedName);
        uploadDada.put("subject", subjectSelectedName);
        uploadDada.put("title", shortDesc.getEditText().getText().toString());
        uploadDada.put("fileName", fileName);
        uploadDada.put("userName", currentUser.getDisplayName());
        uploadDada.put("uploadedOn", Timestamp.now());
        uploadDada.put("verified", true);


        db.collection("notes")
                .add(uploadDada)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {

                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        pd.cancel();
                        Toast.makeText(getContext(), "Notes uploaded Successfully", Toast.LENGTH_SHORT).show();

                        //    Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.cancel();
                        Log.w(TAG, "Error adding document", e);
                    }
                });

    }

    private void handleOnSubjectSelection(int position) {
        CourseModel item = (CourseModel) subjectAdapter.getItem(position);
        subjectSelected = item.getId();
        subjectSelectedName = item.getName();

    }

    private void handleOnBranchSelection(int position) {
        CourseModel item = (CourseModel) branchAdapter.getItem(position);
        branchSelected = item.getId();
        branchSelectedName = item.getName();

        addBranch.setVisibility(View.GONE);
        addSubject.setVisibility(View.GONE);
        subjectLayout.setVisibility(View.VISIBLE);
        if (item.getName().equals("None of the above")) {
            addBranch.setVisibility(View.VISIBLE);
            addSubject.setVisibility(View.VISIBLE);
            subjectLayout.setVisibility(View.GONE);
        } else {
            getSubject(item);
        }

    }

    private void handleOnCategorySelection(int position) {
        addCourse.setVisibility(View.GONE);
        addBranch.setVisibility(View.GONE);
        addSubject.setVisibility(View.GONE);


        branchLayout.setVisibility(View.VISIBLE);
        subjectLayout.setVisibility(View.VISIBLE);

        CourseModel item = (CourseModel) courseAdapter.getItem(position);
        courseSelected = item.getId();
        courseSelectedName = item.getName();
        if (item.getName().equals("None of the above")) {
            addCourse.setVisibility(View.VISIBLE);
            addBranch.setVisibility(View.VISIBLE);
            addSubject.setVisibility(View.VISIBLE);
            branchLayout.setVisibility(View.GONE);
            subjectLayout.setVisibility(View.GONE);

        } else {
            getBranches(item);
        }
    }

    private void getCourses() {
        db.collection("course").whereEqualTo("verified", true)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getString("name"));
                            courseList.add(new CourseModel(document.getString("name"), document.getId()));
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                    courseList.add(new CourseModel("None of the above", null));
                    courseAdapter.notifyDataSetChanged();

                });

    }

    private void getBranches(CourseModel course) {

        db.collection("branch").whereArrayContains("courses", course.getId())
                .get()
                .addOnCompleteListener(task -> {
                    branchList.clear();
                    Log.d(TAG, "Get Data");

                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            branchList.add(new CourseModel(document.getString("name"), document.getId()));
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                    branchList.add(new CourseModel("None of the above", null));
                    branchAdapter.notifyDataSetChanged();
                });

    }

    private void getSubject(CourseModel branch) {

        db.collection("subject").whereArrayContains("branches", branch.getId())
                .get()
                .addOnCompleteListener(task -> {
                    subjectList.clear();
                    Log.d(TAG, "Get Data");

                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            subjectList.add(new CourseModel(document.getString("name"), document.getId()));
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                    subjectList.add(new CourseModel("None of the above", null));
                    subjectAdapter.notifyDataSetChanged();
                });

    }

    private void callFilePicker() {

        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select PDF file"), 22);
    }

    @SuppressLint("Range")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String tempSize;

        if (requestCode == 22) {
            if (data != null) {
                if (data.getDataString().startsWith("content://")) {
                    Cursor cursor = getActivity().getContentResolver().query(data.getData(), null, null, null, null);

                    if (cursor != null && cursor.moveToFirst()) {
                        fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                        tempSize = cursor.getString(cursor.getColumnIndex(OpenableColumns.SIZE));
                        fileSize = Integer.parseInt(tempSize) / 1024;
                        Log.d(TAG, fileName + " " + fileSize);
                        if (fileSize > 1024 * 20) {
                            Toast.makeText(getActivity(), "File size is too high", Toast.LENGTH_SHORT).show();
                            fileName = null;
                            fileUri = null;
                            filePickerLabel.setText("Pick a file");
                            ImageView img = view.findViewById(R.id.filePickerImage);
                            img.setImageResource(R.drawable.ic_upload);
                        } else {
                            filePickerLabel.setText(fileName);
                            ImageView img = view.findViewById(R.id.filePickerImage);
                            img.setImageResource(R.drawable.ic_picture_as_pdf);
                            fileUri = data.getData();
                        }


                    }
                }

            } else {
                Toast.makeText(getContext(), "File not selected", Toast.LENGTH_SHORT).show();
            }


        }

    }
}