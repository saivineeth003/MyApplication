package com.example.eshwanth.myapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;

import static org.apache.http.params.HttpConnectionParams.setConnectionTimeout;
import static org.apache.http.params.HttpConnectionParams.setSoTimeout;

/**
 * Created by Eshwanth on 11/8/2015.
 */
public class user_validity extends AsyncTask<Void, Void, String[]> {
    User user;
    String x;
    ProgressDialog pDialog;
    public AsyncResponse delegate = null;
    public static final int CONNECTION_TIMEOUT = 1000 * 15;
    public static final String SERVER_ADDRESS = "http://my-world.ueuo.com/";
    String[] array = {};
    AlertDialog.Builder dialogBuilder;
    public user_validity(Context context,User user)
    {
        pDialog = new ProgressDialog(context);
        this.user = user;
        dialogBuilder = new AlertDialog.Builder(context);
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // Showing progress dialog
        pDialog.setTitle("Validating user name...");
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();
    }

    @Override
    protected String[] doInBackground(Void... params) {
        ArrayList<NameValuePair> dataToSend = new ArrayList<>();
        dataToSend.add(new BasicNameValuePair("username", user.username));
        dataToSend.add(new BasicNameValuePair("user_type", user.user_type));

        HttpParams httpRequestParams = new BasicHttpParams();
        setConnectionTimeout(httpRequestParams,
                CONNECTION_TIMEOUT);
        setSoTimeout(httpRequestParams,
                CONNECTION_TIMEOUT);

        HttpClient client = new DefaultHttpClient(httpRequestParams);
        HttpPost post = new HttpPost(SERVER_ADDRESS
                + "user_validity.php");


        try {
            post.setEntity(new UrlEncodedFormEntity(dataToSend));
            HttpResponse httpResponse = client.execute(post);

            HttpEntity entity = httpResponse.getEntity();
            String result = EntityUtils.toString(entity);
            x = result;
            JSONObject finalResult = new JSONObject(result);                //Get the jsonObject containing course details

            if(finalResult.length() > 0)
            {
                array = new String[finalResult.length()];
                JSONObject obj1 = finalResult.getJSONObject("0");     //Access each json Object using their key
                array[0] = obj1.getString("success");
                array[1] = obj1.getString("msg");
    }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return array;
    }

    @Override
    protected void onPostExecute(String[] array) {
        super.onPostExecute(array);
        pDialog.dismiss();
        dialogBuilder.setMessage(x);
        dialogBuilder.setPositiveButton("Ok", null);
        dialogBuilder.show();
        delegate.processFinish(array);
    }
}

