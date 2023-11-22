package com.example.wander_app;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wander_app.models.VacationPicture;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class DetailsActivity extends AppCompatActivity {
    // Unique identifier
    private final String ID = "details";
    private final String TEST_ENDPOINT_TRIPADVISOR = "https://api.content.tripadvisor.com/api/v1/location/561514/photos?key=DA033993B5A145549DCEF7D9486BBE21&language=en";
    private final String TEST_ENDPOINT_TRIPADVISOR2 = "https://api.content.tripadvisor.com/api/v1/location/1872416/details?key=DA033993B5A145549DCEF7D9486BBE21&language=en&currency=USD";
    private final String TEST_ENDPOINT_TICKETMASTER = "https://app.ticketmaster.com/discovery/v2/events.json?classificationName=music&dmaId=324&apikey=IT8GmwGz5CUADjZbko6KsVwXUhrHFja3";
    // Used for fragments
    private ViewPager vp;
    private TabLayout tl;

    // Information needed for UI
    private ArrayList<VacationPicture> pictureGallery = new ArrayList<>();
    private JSONArray hours;
    private JSONObject ticketPrices;
    private String description;
    private String rating;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        vp = findViewById(R.id.viewpager);
        tl = findViewById(R.id.tab_layout);

        registerReceiver(apiReceiver, new IntentFilter(
                APIRequestService.Broadcast_id));

        defineEndpoints();
//        createGallery();
//        createHoursSchedule();
//        createPriceBoard();
//        createDescription();
//        createGoogleMaps();
    }

    private void defineEndpoints() {
        // TODO Remove defined points and make some based on coordinates
        ArrayList<String> urls = new ArrayList<>();
        urls.add(TEST_ENDPOINT_TRIPADVISOR);
        urls.add(TEST_ENDPOINT_TRIPADVISOR2);
        urls.add(TEST_ENDPOINT_TICKETMASTER);
        makeApiRequests(urls);
    }

    private void makeApiRequests(ArrayList<String> urls) {
        // TripAdvisor API Request Service (Pictures)
        Intent intentTA = new Intent(getBaseContext(), APIRequestService.class);
        intentTA.putExtra("callerID", ID);
        intentTA.putExtra("apiUrl", urls.get(0));
        intentTA.putExtra("apiType", "TripAdvisor_Pic");
        startService(intentTA);

        // TripAdvisor API Request Service (Description)
        Intent intentTA2 = new Intent(getBaseContext(), APIRequestService.class);
        intentTA2.putExtra("callerID", ID);
        intentTA2.putExtra("apiUrl", urls.get(1));
        intentTA2.putExtra("apiType", "TripAdvisor_Desc");
        startService(intentTA2);

        // TicketMaster API Request Service
//        Intent intentTM = new Intent(getBaseContext(), APIRequestService.class);
//        intentTM.putExtra("callerID", ID);
//        intentTM.putExtra("apiUrl", urls.get(2));
//        intentTA.putExtra("apiType", "TicketMaster");
//
//        startService(intentTM);
    }

    private void createGallery() {
        Log.v("DETAILS", "GALLERY CREATE!");
        ArrayList<Fragment> fragments = new ArrayList<>();
        for (VacationPicture vacP : pictureGallery) {
            ViewPagerItemFragment frag = ViewPagerItemFragment.getInstance(vacP);
            fragments.add(frag);
        }

        MyPagerAdapter pagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), fragments);
        vp.setAdapter(pagerAdapter);
        tl.setupWithViewPager(vp,true);
    }

    private void createHoursSchedule() {

    }

    private void createPriceBoard() {
        // ON HOLD
    }

    private void createDescription() {

    }

    private void createGoogleMaps() {

    }

    public void onClickBack(View view) {
        finish();
    }


    private BroadcastReceiver apiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            JSONObject response = null;
            Log.v("Receiver", "Message Received!");
            if (intent != null && intent.getStringExtra("callerID").equals(ID)) {
                try {
                    Log.v("Receiver", "ID MATCHES!");
                    response = new JSONObject(intent.getStringExtra("jsonObject"));
                    switch (intent.getStringExtra("apiType")) {
                        case "TripAdvisor_Pic":
                            Log.v("Receiver", "TripAdvisor_Pic!");
                            JSONArray data = response.getJSONArray("data");
                            for (int i = 0; i < Math.min(MyPagerAdapter.MAX_SIZE, data.length()); i++) {
                                JSONObject photo = data.getJSONObject(i);
                                JSONObject images = photo.getJSONObject("images");
                                JSONObject mediumImage = images.getJSONObject("large");
                                String imageUrl = mediumImage.getString("url");
                                String caption = photo.getString("caption");
                                if (caption.isEmpty()) {
                                    caption = "No caption available";
                                }
                                pictureGallery.add(new VacationPicture("Photo " + i + ": " + caption,imageUrl));
                            }
                            createGallery();
                            break;
                        case "TripAdvisor_Desc":
                            Log.v("Receiver", "TripAdvisor_Desc!");
                            JSONObject time = response.getJSONObject("hours");
                            hours = time.getJSONArray("weekday_text");
                            description = response.optString("description", "No description available");
                            rating = response.optString("rating", "No rating available");
                            break;
                        default:  // Ticketmaster
                            Log.v("Receiver", "Default!");
                            // TODO NEED TO FIGURE OUT IF TICKERMASTER IS ACTUALLY USABLE SINCE IT DOESNT SEEM TO BE
                            break;
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }


//                Log.v("Receiver", "API Returned!");
//                JSONObject response = null;
//                try {
//                    response = new JSONObject(intent.getStringExtra("jsonObject"));
//                    JSONArray jsonArray = response.getJSONArray("data");
//                    String mediumImageUrl = jsonArray.getJSONObject(0)
//                            .getJSONObject("images")
//                            .getJSONObject("medium")
//                            .getString("url")
//                            .trim();
//                    Log.v("Image","Medium Image URL: " + mediumImageUrl);
//                    Picasso.get().load(mediumImageUrl).into(testPic);
//                    Log.v("Image", "IMAGE LOADED!");
//                } catch (JSONException e) {
//                    throw new RuntimeException(e);
//                }
            }
        }
    };

}