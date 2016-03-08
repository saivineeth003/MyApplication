package com.example.eshwanth.myapplication;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

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
 * Created by Eshwanth on 11/27/2015.
 */
public class RemoteFetchService extends Service {

    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    public static ArrayList<ListItem> listItemList;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void populateWidget() {

        Intent widgetUpdateIntent = new Intent();
        widgetUpdateIntent.setAction(app_widget.DATA_FETCHED);
        widgetUpdateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        sendBroadcast(widgetUpdateIntent);
        this.stopSelf();
    }

    private void performAsyncTask() {

        UserLocalStore userLocalStore = new UserLocalStore(this);
        final User user = userLocalStore.getLoggedInUser();

        final int CONNECTION_TIMEOUT = 1000 * 15;
        final String SERVER_ADDRESS = "http://my-world.ueuo.com/";

        new AsyncTask<Void, Void, Void>() {

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
                        + "get_tomorrow_class.php");


                try {
                    post.setEntity(new UrlEncodedFormEntity(dataToSend));
                    HttpResponse httpResponse = client.execute(post);

                    HttpEntity entity = httpResponse.getEntity();
                    String result = EntityUtils.toString(entity);
                    JSONObject finalResult = new JSONObject(result);                //Get the jsonObject containing course details

                    if (finalResult.length() > 0) {

                        for (int i = 0; i < finalResult.length(); i++) {
                            ListItem listItem = new ListItem();
                            JSONObject obj1 = finalResult.getJSONObject(String.valueOf(i));     //Access each json Object using their key
                            listItem.heading = obj1.getString("courses");                           //Get the value using the key for jObject
                            listItem.content = obj1.getString("Time");
                            listItemList.add(listItem);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void array) {
                super.onPostExecute(array);

                populateWidget();
            }

        }.execute(null, null, null);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.hasExtra(AppWidgetManager.EXTRA_APPWIDGET_ID))
            appWidgetId = intent.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        performAsyncTask();
        return super.onStartCommand(intent, flags, startId);
    }
}
