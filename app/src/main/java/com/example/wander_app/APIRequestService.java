package com.example.wander_app;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class APIRequestService extends Service {
    public static final String Broadcast_id = "API_REQUEST";
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    // TODO: USE A QUEUE OBJECT TO BE ABLE TO ACCEPT MULTIPLE API REQUEST ENDPOINTS AT ONCE
    public APIRequestService() {}

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v("Service", "SERVICE STARTED");
        if (intent != null) {
            Log.v("Service", "INTENT NOT EMPTY!");
            final Request apiUrl = buildRequest(intent);
            String callerID = intent.getStringExtra("callerID");
            String apiType = intent.getStringExtra("apiType");
            String suggestionId = intent.getStringExtra("suggestionId");
            new APIRequestAsyncTask(callerID, apiType, suggestionId).execute(apiUrl);
           //  new DLTask().execute(urlPath,fileName);
        }

        return Service.START_STICKY;
    }

    private Request buildRequest(Intent intent) {
        Request req = null;
        if (intent.getStringExtra("header") != null) { // CHATGPT API ENDPOINT
            JSONObject messageJson = new JSONObject();
            try {
                messageJson.put("model", intent.getStringExtra("model"));
                messageJson.put("temperature", intent.getIntExtra("temp", 0));
                messageJson.put("prompt", intent.getStringExtra("prompt"));
                messageJson.put("max_tokens", intent.getIntExtra("max_tokens", 1000));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestBody requestBody = RequestBody.create(messageJson.toString(), JSON);

            req = new Request.Builder()
                    .url(intent.getStringExtra("apiUrl"))
                    .addHeader("Authorization", "Bearer " + intent.getStringExtra("apiKey"))
                    .post(requestBody)
                    .build();

        } else { // TRIP ADVISOR AND TICKET MASTER
            req = new Request.Builder()
                    .url(intent.getStringExtra("apiUrl"))
                    .build();
        }
        Log.v("SERVICE", "REQ BUILT");
        return req;
    }

    private class APIRequestAsyncTask extends AsyncTask<Request, Void, Void> {
        private OkHttpClient apiClient = new OkHttpClient();
        private String callerID;
        private String apiType;
        private String suggestionId;
        public APIRequestAsyncTask(String callerID, String apiType, String suggestionId) {
            this.callerID = callerID;
            this.apiType = apiType;
            this.suggestionId = suggestionId;

            Log.v("APIASYNC", "CONSTRUCT!");
        }
        @Override
        protected Void doInBackground(Request... apirequest) {
            Log.v("APIASYNC", "THREAD STARTED");
            try {
                makeRequest(apirequest[0]);
            } catch (IOException e) {
                Log.e("Error: ", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.d("POST", "onPostExecute");
            stopSelf();
        }

        private void makeRequest(Request apiPath) throws IOException {
            Log.d("Status","Starting req...");
            try {
                Response response = apiClient.newCall(apiPath).execute();
                JSONObject  jsonObject = null;
                if (response.isSuccessful()) {
                    Log.v("ASYNC","RESPONSE SUCCESS!");
                    jsonObject = new JSONObject(response.body().string());
                    broadcastResults(jsonObject);
                } else {
                    Log.d("Error","Error: " + response.message());
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("Error","Error: " +  e.getMessage());
            }
        }

        private void broadcastResults(JSONObject response) {
            Log.v("ASYNC", "BROADCAST!");
            Intent intent = new Intent(Broadcast_id);
            intent.putExtra("jsonObject", response.toString());
            intent.putExtra("callerID", callerID); // USED BY RECEIVERS TO MAKE SURE TO GET RIGHT RESPONSES
            intent.putExtra("apiType", apiType);
            intent.putExtra("suggestionId", suggestionId);
            sendBroadcast(intent);
        }
    }

}