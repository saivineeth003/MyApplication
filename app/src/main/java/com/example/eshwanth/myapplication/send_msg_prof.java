package com.example.eshwanth.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class send_msg_prof extends AppCompatActivity implements View.OnClickListener, AsyncResponse {

    UserLocalStore userLocalStore;
    AlertDialog.Builder builder;
    Button btn,log_out;
    EditText msg_id;
    int status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_msg_prof);

        btn = (Button) findViewById(R.id.btn);
        log_out = (Button) findViewById(R.id.log_out);
        msg_id = (EditText) findViewById(R.id.msg_bar);
        userLocalStore = new UserLocalStore(this);

        btn.setOnClickListener(this);
        log_out.setOnClickListener(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn:
                User user = userLocalStore.getLoggedInUser();
                String msg = msg_id.getText().toString();
                String course_id = "CS3033";
                message_send_Async q = new message_send_Async(this,user,course_id,msg);
                q.execute();
                q.result = this;
                break;

            case R.id.log_out:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(true);
                builder.setIcon(R.drawable.warning);
                builder.setTitle("Confirmation");
                builder.setMessage("Log out ?");
                builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        userLocalStore.setUserLoggedIn(false);
                        userLocalStore.clearUserData();
                        Intent loginIntent = new Intent(send_msg_prof.this, Login.class);
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
        }
    }


    @Override
    public void processFinish(String[] delegate) {
        if(!delegate[0].equals("-1"))
        {
            builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setTitle("Success !");
            builder.setMessage("Message sent.");
            builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            builder.show();
        }
        else
        {
            builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setTitle("Error !");
            builder.setMessage("Message not sent.");
            builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            builder.show();
        }

    }
}
