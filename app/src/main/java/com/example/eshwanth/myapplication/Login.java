package com.example.eshwanth.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.Objects;


public class Login extends AppCompatActivity implements View.OnClickListener {

    Button bLogin;
    TextView registerLink;
    EditText etUsername, etPassword;
    UserLocalStore userLocalStore;
    int Network_State = -1;

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(!isNetworkAvailable())
        {
            Network_State = 0;
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Login.this);
            dialogBuilder.setMessage("No internet Connection.");
            dialogBuilder.setPositiveButton("Ok", null);
            dialogBuilder.show();
            return;
        }

        else
        {
            Network_State = 1;
        }

        Spinner dropdown = (Spinner)findViewById(R.id.spinner);
        String[] items = new String[]{ "Student" , "Professor"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        bLogin = (Button) findViewById(R.id.bLogin);
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        registerLink = (TextView) findViewById(R.id.tvRegisterLink);

        bLogin.setOnClickListener(this);
        registerLink.setOnClickListener(this);

        userLocalStore = new UserLocalStore(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bLogin:

                if(Network_State == 0)
                {
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Login.this);
                    dialogBuilder.setMessage("No internet Connection.");
                    dialogBuilder.setPositiveButton("Okay", null);
                    dialogBuilder.show();
                    break;
                }

                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                pw_hash password_hash = new pw_hash();
                password = password_hash.hash(password);
                Spinner mySpinner=(Spinner) findViewById(R.id.spinner);
                String type = mySpinner.getSelectedItem().toString();

                User user = new User(username, password,type);
                authenticate(user);

                break;

            case R.id.tvRegisterLink:
                if(Network_State == 0)
                {
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Login.this);
                    dialogBuilder.setMessage("No internet Connection.");
                    dialogBuilder.setPositiveButton("Okay", null);
                    dialogBuilder.show();
                    break;
                }
                Intent registerIntent = new Intent(Login.this, Register.class);
                startActivity(registerIntent);
                break;
        }
    }

    private void authenticate(User user) {
        ServerRequests serverRequest = new ServerRequests(this);
        serverRequest.fetchUserDataAsyncTask(user, new GetUserCallback() {
            @Override
            public void done(User returnedUser) {
                if (returnedUser == null) {
                    showErrorMessage();
                } else {
                    logUserIn(returnedUser);
                }
            }
        });
    }

    private void showErrorMessage() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Login.this);
        dialogBuilder.setMessage("Incorrect user details.Try again.");
        dialogBuilder.setPositiveButton("Ok", null);
        dialogBuilder.show();
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, 9000).show();
            } else {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Login.this);
                dialogBuilder.setMessage("This device is not supported.");
                dialogBuilder.setPositiveButton("Ok", null);
                dialogBuilder.show();
                finish();
            }
            return false;
        }
        return true;
    }

    private void logUserIn(User returnedUser) {
        userLocalStore.storeUserData(returnedUser);
        userLocalStore.setUserLoggedIn(true);
        if(checkPlayServices()) {

            tokenLocalStore tokenLocalStore;
            tokenLocalStore = new tokenLocalStore(this);

            if(Objects.equals(tokenLocalStore.returnToken(), ""))
            {
                Intent intent = new Intent(Login.this, RegistrationIntentService.class);
                startService(intent);
            }

        }
        if(Objects.equals(returnedUser.user_type, "Student"))
            startActivity(new Intent(this, MainActivity.class));
        else
            startActivity(new Intent(this, profMainActivity.class));
        finish();
    }
}
