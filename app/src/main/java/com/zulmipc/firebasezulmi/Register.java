package com.zulmipc.firebasezulmi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Register extends AppCompatActivity implements View.OnClickListener {
    EditText txtUsername, txtEmail, txtPassword;
    TextView txtLogin;
    Button btnReg;
    FirebaseAuth firebaseAuth;
    DatabaseReference reference;

    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            System.out.println("Sudah login");
            Intent intent = new Intent(Register.this, Home.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        setContentView(R.layout.activity_register);


        firebaseAuth = FirebaseAuth.getInstance();

        txtUsername = (EditText) findViewById(R.id.txtUsername);
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        btnReg = (Button) findViewById(R.id.btnRegister);
        txtLogin = (TextView) findViewById(R.id.txtLogin);

        btnReg.setOnClickListener(this);
        txtLogin.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        final String Username = txtUsername.getText().toString().trim();
        String Email = txtEmail.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();

        if (view == btnReg) {
            if (TextUtils.isEmpty(Username) || TextUtils.isEmpty(Email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Semua harus diisi", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Register.this, "Proses Daftar...", Toast.LENGTH_SHORT).show();
                registerUser(Username, Email, password);
            }
        }

        if (view == txtLogin) {
            Intent intent = new Intent(Register.this, Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void registerUser(final String username, String email, String password) {


        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    assert firebaseUser != null;
                    String userId = firebaseUser.getUid();

                    reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("id", userId);
                    hashMap.put("username", username);
                    hashMap.put("imageURL", "default");
                    hashMap.put("status", "offline");
                    hashMap.put("search", username.toLowerCase());

                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(Register.this, Home.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                } else {
                    Toast.makeText(Register.this, "Gagal", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
