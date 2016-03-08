package com.example.eshwanth.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.Objects;


public class Register extends AppCompatActivity implements View.OnClickListener{
    EditText etUsername, etPassword;
    Button bRegister;
    UserLocalStore userLocalStore;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Spinner dropdown = (Spinner)findViewById(R.id.spinner);     //Display elements in spinner using array adapter
        String[] items = new String[]{ "Student" , "Professor"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        bRegister = (Button) findViewById(R.id.bRegister);
        userLocalStore = new UserLocalStore(this);

        bRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bRegister:
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                pw_hash password_hash = new pw_hash();                      //password hashing
                password = password_hash.hash(password);
                Spinner mySpinner=(Spinner) findViewById(R.id.spinner);
                String type = mySpinner.getSelectedItem().toString();       //Get value from spinner
                user = new User(username, password,type);

                registerUser(user);

                break;
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, 9000).show();
            } else {
                Log.i("MainActivity:", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private void registerUser(final User user) {
        ServerRequests serverRequest = new ServerRequests(this);
        serverRequest.storeUserDataInBackground(user, new GetUserCallback() {
            @Override
            public void done(User returnedUser) {
                userLocalStore.storeUserData(user);                         //Store user details in shared preferences
                userLocalStore.setUserLoggedIn(true);

                if(checkPlayServices()) {

                    tokenLocalStore tokenLocalStore;
                    tokenLocalStore = new tokenLocalStore(Register.this);

                    if(Objects.equals(tokenLocalStore.returnToken(), ""))
                    {

                        Intent intent = new Intent(Register.this, RegistrationIntentService.class);
                        startService(intent);
                    }

                }


                if(Objects.equals(returnedUser.user_type, "Student"))
                    startActivity(new Intent(Register.this, MainActivity.class));
                else
                    startActivity(new Intent(Register.this, profMainActivity.class));
                finish();
            }
        });
    }
}
