package com.example.gps_memories.controller.Register_Login;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.gps_memories.R;
import com.example.gps_memories.controller.HomeActivity;
import com.example.gps_memories.model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {

    protected EditText usernameEditText;
    protected EditText emailEditText;
    protected EditText passwordEditText;
    protected EditText passwordconfirmEditText;
    protected Button registerbtn;
    protected ProgressBar progressbar;
    protected CircleImageView profileImageprofile;

    FirebaseUser firebaseUser;

    DatabaseReference reference;

    StorageReference storageReference;
    private static final int image_request = 1;
    private Uri imageuri;
    private StorageTask uploadtask;

    String user_photo;


    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseAuthSettings firebaseAuthSettings = firebaseAuth.getFirebaseAuthSettings();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_register);
        initView();





        progressbar.setVisibility(View.GONE);



        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        reference = FirebaseDatabase.getInstance().getReference("Users");


        storageReference = FirebaseStorage.getInstance().getReference("uploads");


        profileImageprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenImage();
            }
        });


        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (usernameEditText.getText().toString().trim().equals("")) {

                    usernameEditText.setError("required");

                } else if (emailEditText.getText().toString().trim().equals("")) {

                    emailEditText.setError("required");

                } else if (passwordEditText.getText().toString().trim().equals("")) {

                    passwordEditText.setError("required");

                } else if (passwordconfirmEditText.getText().toString().trim().equals("")) {

                    passwordconfirmEditText.setError("required");

                } else if (passwordEditText.getText().length() < 6) {

                    passwordconfirmEditText.setError("It's so weak");

                } else if (!passwordEditText.getText().toString().equals(passwordconfirmEditText.getText().toString())) {

                    passwordconfirmEditText.setError("It doesn't match password");

                } else {


                    register(usernameEditText.getText().toString(), emailEditText.getText().toString(),
                            passwordEditText.getText().toString(),user_photo);


                }


            }
        });


    }


    private void register(final String username, final String email, String password, final String user_photo) {


        progressbar.setVisibility(View.VISIBLE);
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                            String userid = firebaseUser.getUid();

                            String user_photo2;

                            if(user_photo.equals("")){

                                user_photo2 = "default";

                            }else {

                                user_photo2=user_photo;

                            }

                            User user = new User(userid, username, email, user_photo2);



                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        progressbar.setVisibility(View.GONE);

                                        Toast.makeText(RegisterActivity.this, "Account created successfully", Toast.LENGTH_SHORT).show();


                                        Intent intent = new Intent(RegisterActivity.this
                                                , HomeActivity.class);


                                        startActivity(intent);


                                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);


                                        finish();


                                    } else {

                                        progressbar.setVisibility(View.GONE);
                                        Toast.makeText(RegisterActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });


                        } else {

                            progressbar.setVisibility(View.GONE);
                            Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });


    }





    private void OpenImage() {


        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, image_request);


    }


    private String getFileExtension(Uri uri) {


        ContentResolver contentResolver = RegisterActivity.this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));


    }


    private void uploadImage() {

        final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setMessage("Uploading");
        progressDialog.show();


        if (imageuri != null) {

            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(imageuri));


            uploadtask = fileReference.putFile(imageuri);

            uploadtask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {

                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {


                    if (!task.isSuccessful()) {

                        throw task.getException();

                    }


                    return fileReference.getDownloadUrl();


                }
            }).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {

                    if (task.isSuccessful()) {
                        Uri downloadUri = (Uri) task.getResult();

                        String auri = downloadUri.toString();

                        user_photo = auri;

                        Glide.with(RegisterActivity.this).load(auri).into(profileImageprofile);

                        progressDialog.dismiss();


                    } else {

                        Toast.makeText(RegisterActivity.this, "Failed", Toast.LENGTH_SHORT).show();

                        progressDialog.dismiss();

                    }


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();


                }
            });


        } else {


            Toast.makeText(RegisterActivity.this, "No image selected", Toast.LENGTH_SHORT).show();

        }


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == image_request && resultCode == RESULT_OK

                && data != null && data.getData() != null
        ) {

            imageuri = data.getData();

            if (uploadtask != null && uploadtask.isInProgress()) {


                Toast.makeText(RegisterActivity.this, "Upload in progress", Toast.LENGTH_SHORT).show();


            } else {

                uploadImage();

            }


        }
    }











    private void initView() {
        usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        passwordconfirmEditText = (EditText) findViewById(R.id.passwordconfirmEditText);
        registerbtn = (Button) findViewById(R.id.registerbtn);
        progressbar = (ProgressBar) findViewById(R.id.progressbar);
        profileImageprofile = (CircleImageView) findViewById(R.id.profile_imagefragmentprofile);
    }
}
