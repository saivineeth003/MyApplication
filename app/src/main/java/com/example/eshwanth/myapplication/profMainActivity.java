package com.example.eshwanth.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


public class profMainActivity extends AppCompatActivity implements AsyncResponse,View.OnClickListener{

    UserLocalStore userLocalStore;
    TextView etUsername;
    Button send;
    Button bLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prof_main);

        etUsername = (TextView) findViewById(R.id.etUsername);
        send = (Button) findViewById(R.id.send);
        bLogout = (Button) findViewById(R.id.bLogout);

        send.setOnClickListener(this);
        bLogout.setOnClickListener(this);
        userLocalStore = new UserLocalStore(this);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.bLogout:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(true);
                builder.setIcon(R.drawable.warning);
                builder.setTitle("Confirmation");
                builder.setMessage("Log out ?");
                builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        userLocalStore.setUserLoggedIn(false);
                        userLocalStore.clearUserData();
                        Intent loginIntent = new Intent(profMainActivity.this, Login.class);
                        startActivity(loginIntent);
                    }
                });
                builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                builder.show();
                break;

            case R.id.send:
                Intent intent = new Intent(this, send_msg_prof.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (authenticate()) {

            User user = userLocalStore.getLoggedInUser();
            displayUserDetails();
            queryAsyncTask q = new queryAsyncTask(this,user);
            q.execute();
            q.delegate = this;
        }
    }

    private boolean authenticate() {
        if (userLocalStore.getLoggedInUser() == null) {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            return false;
        }
        return true;
    }

    private void displayUserDetails() {
        User user = userLocalStore.getLoggedInUser();
        etUsername.setText(user.username);
    }

    @Override
    public void processFinish(String[] delegate) {

        int len = delegate.length;
        String[] count = new String[len];
        for(int i = 0; i < len ; i++)
            count[i] = String.valueOf(i);

        String topic = "Your Courses";

        CustomList adapter = new CustomList(profMainActivity.this, delegate, count, topic);

        ListView listView = (ListView) findViewById(R.id.list);
        TextView v = new TextView(this);
        v.setText("Your Courses :");
        v.setBackgroundColor(Color.GRAY);
        v.setHeight(75);
        listView.addHeaderView(v);

        listView.setAdapter(adapter);
    }
}
