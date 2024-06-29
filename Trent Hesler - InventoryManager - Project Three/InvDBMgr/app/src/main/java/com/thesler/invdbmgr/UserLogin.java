package com.thesler.invdbmgr;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.thesler.invdbmgr.util.CredDBMgr;

import java.security.NoSuchAlgorithmException;

public class UserLogin extends AppCompatActivity implements TextWatcher {
    Button buttonLogin;
    Button buttonRegister;
    EditText usernameText;
    EditText passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_login);

        //Bind visual elements so we can use them in functions
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonRegister = findViewById(R.id.buttonRegister);
        usernameText = findViewById(R.id.userNameEntry);
        passwordText = findViewById(R.id.passwordEntry);

        //Attach a TextWatcher to username and password fields,
        //so we can see when the text changes
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            usernameText.addTextChangedListener(this);
            passwordText.addTextChangedListener(this);
            return insets;
        });
    }

    public void Register(View view) throws NoSuchAlgorithmException {
        CredDBMgr pwDB = new CredDBMgr(this);

        //Validate username and password for new user
        if(pwDB.register(this,usernameText.getText().toString(),passwordText.getText().toString())) {
            Toast.makeText(UserLogin.this, "Registration successful!", Toast.LENGTH_SHORT).show();

            //If this is the first time a user is using this app, ask for SMS permission
            String smsPermission = android.Manifest.permission.SEND_SMS;
            if (ContextCompat.checkSelfPermission(this, smsPermission) != PackageManager.PERMISSION_GRANTED &&
            !ActivityCompat.shouldShowRequestPermissionRationale(this, smsPermission )) {
                ActivityCompat.requestPermissions(this, new String[] { smsPermission }, 0);
            }

            //Start the main Inventory Management activity
            startActivity(new Intent(UserLogin.this, MainActivity.class));
        }
    }

    public void Login(View view) throws NoSuchAlgorithmException {
        CredDBMgr pwDB = new CredDBMgr(this);

        //Validate username and password against those stored in database
        if(pwDB.passwordMatches(this,usernameText.getText().toString(),passwordText.getText().toString())) {
            Toast.makeText(UserLogin.this, "Login successful!", Toast.LENGTH_SHORT).show();

            //Start the main Inventory Management activity
            startActivity(new Intent(UserLogin.this, MainActivity.class));
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //Do nothing
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //Enable/disable buttons based on whether username and password are
        //sufficiently long or not
        if(passwordText.getText().length() < 6 || usernameText.getText().length() < 4) {
            buttonLogin.setEnabled(false);
            buttonRegister.setEnabled(false);
        } else {
            buttonLogin.setEnabled(true);
            buttonRegister.setEnabled(true);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        //Do nothing
    }
}