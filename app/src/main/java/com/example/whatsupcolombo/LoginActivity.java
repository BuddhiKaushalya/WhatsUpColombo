package com.example.whatsupcolombo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText Username;
    private EditText Password;
    private Button Login;
    private TextView SignUp;
    private FirebaseAuth firebaseAuth; //import auth libs of firebase
    private ProgressDialog progressDialog;
    private TextView ForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_login);
        setUpUI();


        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        //check if a user is alredy loggedin or not
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            //destroys the activity
            finish();
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        }
        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
            }
        });

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate(Username.getText().toString(), Password.getText().toString());
            }
        });

        ForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class));
            }
        });
    }

    private void setUpUI() {
        Username = (EditText) findViewById(R.id.etName);
        Password = (EditText) findViewById(R.id.etPassword);
        Login = (Button) findViewById(R.id.btnLogin);
        SignUp = (TextView) findViewById(R.id.tvRegister);
        ForgotPassword = (TextView) findViewById(R.id.tvForegetPW);
    }

    private void validate(String userNmae, String userPassword) {
        //create message until the verification is done
        progressDialog.setMessage("please wait");
        //show message
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(userNmae, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //remove message as teh verification is done
                    progressDialog.dismiss();
                    checkEmailVerification();

                } else {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkEmailVerification() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Boolean EmailFlag = firebaseUser.isEmailVerified();
        if (EmailFlag) {
            //access in if verified
            finish();
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        } else {
            //ask user to be verified and singed out
            Toast.makeText(LoginActivity.this, "Please Verify your Email", Toast.LENGTH_LONG).show();
            firebaseAuth.signOut();
        }
    }
}
