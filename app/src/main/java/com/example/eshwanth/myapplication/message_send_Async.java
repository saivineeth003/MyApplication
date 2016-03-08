package com.example.eshwanth.myapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import java.util.ArrayList;

import static org.apache.http.params.HttpConnectionParams.setConnectionTimeout;
import static org.apache.http.params.HttpConnectionParams.setSoTimeout;

/**
 * Created by Eshwanth on 11/1/2015.
 */


public class message_send_Async extends AsyncTask<Void, Void, String[]> {
    User user;
    ProgressDialog pDialog;
    String course_id,msg;
    public AsyncResponse result = null;
    public static final int CONNECTION_TIMEOUT = 1000 * 15;
    public static final String SERVER_ADDRESS = "http://my-world.ueuo.com/";
    String[] array = new String[1];
    AlertDialog.Builder builder;
    public message_send_Async(Context context,User user,String course_id,String msg)
    {
        pDialog = new ProgressDialog(context);
        this.user = user;
        this.course_id = course_id;
        this.msg = msg;
        builder = new AlertDialog.Builder(context);
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // Showing progress dialog
        pDialog.setTitle("Sending Request...");
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();
    }

    @Override
    protected String[] doInBackground(Void... params) {
        ArrayList<NameValuePair> dataToSend = new ArrayList<>();
        dataToSend.add(new BasicNameValuePair("username", user.username));
        dataToSend.add(new BasicNameValuePair("course_id", course_id));
        dataToSend.add(new BasicNameValuePair("msg", msg));

        HttpParams httpRequestParams = new BasicHttpParams();
        setConnectionTimeout(httpRequestParams,
                CONNECTION_TIMEOUT);
        setSoTimeout(httpRequestParams,
                CONNECTION_TIMEOUT);

        HttpClient client = new DefaultHttpClient(httpRequestParams);
        HttpPost post = new HttpPost(SERVER_ADDRESS
                + "message_send_query.php");


        try {
            post.setEntity(new UrlEncodedFormEntity(dataToSend));
            HttpResponse httpResponse = client.execute(post);

            if(httpResponse.getStatusLine().getStatusCode() > 0)
            {
                array[0] = (String.valueOf(httpResponse.getStatusLine().getStatusCode()));
            }
            else
            {
                array[0] = "-1";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return array;
    }

    @Override
    protected void onPostExecute(String[] status) {
        super.onPostExecute(status);
        pDialog.dismiss();
        result.processFinish(status);
    }
}


