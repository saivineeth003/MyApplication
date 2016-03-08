package com.example.eshwanth.myapplication;

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
 * Created by Eshwanth on 11/25/2015.
 */

public class get_tom_class_Asynctask extends AsyncTask<Void, Void, String[]> {
    User user;
    ProgressDialog pDialog;
    public AsyncResponse delegate = null;
    public static final int CONNECTION_TIMEOUT = 1000 * 15;
    public static final String SERVER_ADDRESS = "http://my-world.ueuo.com/";
    String[] array = {};
    public get_tom_class_Asynctask(Context context,User user)
    {
        pDialog = new ProgressDialog(context);
        this.user = user;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // Showing progress dialog
        pDialog.setTitle("Loading courses...");
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
                + "get_tomorrow_class.php");


        try {
            post.setEntity(new UrlEncodedFormEntity(dataToSend));
            HttpResponse httpResponse = client.execute(post);

            HttpEntity entity = httpResponse.getEntity();
            String result = EntityUtils.toString(entity);
            JSONObject finalResult = new JSONObject(result);                //Get the jsonObject containing course details

            if(finalResult.length() > 0)
            {
                array = new String[finalResult.length()];

                for(int i=0 ; i < finalResult.length() ; i++)
                {

                    JSONObject obj1 = finalResult.getJSONObject(String.valueOf(i));     //Access each json Object using their key
                    array[i] = obj1.getString("course_name");                           //Get the value using the key for jObject
                }
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
        delegate.processFinish(array);
    }
}
