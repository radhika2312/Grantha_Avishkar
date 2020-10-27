package com.example.grantha;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class registration extends AppCompatActivity {

    public static final String TAG ="TAG";
    Button signup,login;
    private EditText name,username,email,password,interest;

    private ProgressDialog progressDialog;
    String userID;
    private FirebaseAuth fAuth;
    private DatabaseReference mRootRef;

    private TextInputLayout nameLay,userLay,emaillay,pLay,interestLay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        signup = findViewById(R.id.button);
        name = findViewById(R.id.editText);
        username = findViewById(R.id.editText2);
        email= findViewById(R.id.editText3);
        password = findViewById(R.id.editText4);
        login = findViewById(R.id.button5);
        interest=findViewById(R.id.editText5);
        nameLay=findViewById(R.id.name_text_input1);
        userLay=findViewById(R.id.name_text_input2);
        emaillay=findViewById(R.id.name_text_input3);
        pLay=findViewById(R.id.name_text_input4);
        interestLay=findViewById(R.id.name_text_input5);



        fAuth=FirebaseAuth.getInstance();
        mRootRef= FirebaseDatabase.getInstance().getReference();
	/*	if(fAuth.getCurrentUser()!=null)
		{
			startActivity(new Intent(getApplicationContext(),Home.class));
             finish();
		}
	 */

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String n = name.getText().toString();
                final String u_id = username.getText().toString();
                final String e_id = email.getText().toString();
                String pass = password.getText().toString();
                final String i=interest.getText().toString();

                if (n.isEmpty()) {
                    nameLay.setError("Enter valid name!");
                    name.requestFocus();
                    return;
                }
                if (u_id.isEmpty()) {
                    userLay.setError("Enter valid name!");
                    username.requestFocus();
                    return;
                }

                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(e_id).matches() || e_id.isEmpty()) {
                    emaillay.setError("Enter Valid E-mail!");
                    email.requestFocus();
                    return;
                }
                if (pass.isEmpty() || pass.length() < 6) {
                    pLay.setError("Enter at least 6 length password!");
                    password.requestFocus();
                    return;
                }
                else {
                    progressDialog.setMessage("Registering...");
                    progressDialog.show();
                }

                fAuth.createUserWithEmailAndPassword(e_id,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            // send verification link

                            final FirebaseUser fuser = fAuth.getCurrentUser();
                            fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(registration.this, "Verification Email Has been Sent.", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();

                                    userID = fAuth.getCurrentUser().getUid();

                                    mRootRef.child("invite").child("demo").setValue(u_id);
                                    HashMap<String,Object> map = new HashMap<>();
                                    map.put("id",fAuth.getCurrentUser().getUid());
                                    map.put("name",n);
                                    map.put("username",u_id);
                                    map.put("email",e_id);
                                    map.put("bio","");
                                    map.put("imageurl","default");
                                    map.put("interest",i);
                                    map.put("status","offline");
                                    mRootRef.child("Users").child(userID).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "onSuccess: user Profile is created for "+ userID);
                                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "onFailure: " + e.toString());
                                        }
                                    });

                                }

                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: Email not sent " + e.getMessage());
                                }
                            });



                        }else {
                            Toast.makeText(registration.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
            }
        });



        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });


    }
}

