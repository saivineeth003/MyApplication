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

import java.util.ArrayList;

import static org.apache.http.params.HttpConnectionParams.setConnectionTimeout;
import static org.apache.http.params.HttpConnectionParams.setSoTimeout;

/**
 * Created by Eshwanth on 11/1/2015.
 */

public class logout_clear_regid extends AsyncTask<Void, Void, Void> {
    User user;
    ProgressDialog pDialog;
    public static final int CONNECTION_TIMEOUT = 1000 * 15;
    public static final String SERVER_ADDRESS = "http://my-world.ueuo.com/";
    public logout_clear_regid(Context context,User user)
    {
        pDialog = new ProgressDialog(context);
        this.user = user;
    }

    @Override
    protected Void doInBackground(Void... params) {
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
                + "logout_clear_id.php");


        try {
            post.setEntity(new UrlEncodedFormEntity(dataToSend));
            HttpResponse httpResponse = client.execute(post);

            HttpEntity entity = httpResponse.getEntity();
            String result = EntityUtils.toString(entity);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
