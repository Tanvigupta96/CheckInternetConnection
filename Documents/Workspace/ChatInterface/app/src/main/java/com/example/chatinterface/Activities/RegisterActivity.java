package com.example.chatinterface.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatinterface.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class RegisterActivity extends AppCompatActivity {
    private Button createAccountButton;
    private EditText userPassword,userEmail;
    private TextView alreadyHaveAccountLink;
    private FirebaseAuth mauth;
    private ProgressDialog loadingBar;


    private DatabaseReference rootRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mauth=FirebaseAuth.getInstance();
        rootRef= FirebaseDatabase.getInstance().getReference();



        initializeFields();


        alreadyHaveAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToLoginActivity();

            }
        });


        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewAccount();
            }

            
        });
    }

    private void createNewAccount() {
        String email=userEmail.getText().toString();
        String password=userPassword.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter email..",Toast.LENGTH_SHORT).show();

        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter Password..",Toast.LENGTH_SHORT).show();

        }


        else{
            loadingBar.setTitle("Creating new Account");
            loadingBar.setMessage("please wait, while we are creating new account for you...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mauth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()){

                        String deviceToken = FirebaseInstanceId.getInstance().getToken();

                        String currentUserId=mauth.getCurrentUser().getUid();

                        rootRef.child("Users").child(currentUserId).setValue("");

                        rootRef.child("Users").child(currentUserId).child("device_token").setValue(deviceToken);


                        sendUserToMainActivity();
                        Toast.makeText(RegisterActivity.this,"Account Created Successfully..",Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                    else{
                      String message=task.getException().toString();
                      Toast.makeText(RegisterActivity.this,"Error:" + message,Toast.LENGTH_SHORT).show();
                      loadingBar.dismiss();

                    }

                }
            });
        }



    }

    private void initializeFields() {
        createAccountButton=findViewById(R.id.register_button);
        userEmail=findViewById(R.id.register_email);
        userPassword=findViewById(R.id.register_password);
        alreadyHaveAccountLink=findViewById(R.id.already_have_account_link);
        loadingBar=new ProgressDialog(RegisterActivity.this,R.style.MyAlertDialogStyle);
    }

    private void sendUserToLoginActivity() {
        Intent loginIntent=new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(loginIntent);
    }

    private void sendUserToMainActivity() {
        Intent mainIntent=new Intent(RegisterActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

}
