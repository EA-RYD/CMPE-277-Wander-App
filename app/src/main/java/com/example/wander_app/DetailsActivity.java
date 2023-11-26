package com.example.wander_app;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wander_app.models.Coordinates;
import com.example.wander_app.models.DailyWeatherItem;
import com.example.wander_app.models.UserReview;
import com.example.wander_app.models.VacationPicture;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

public class DetailsActivity extends AppCompatActivity {
    private Coordinates locationCoords;
    // Unique identifier
    private final String ID = "details";

    // TODO DONT HARDCODE KEY, GET COORDS FROM MAINACTIVITY
    private String tripAdvisorKey = "DA033993B5A145549DCEF7D9486BBE21";
    private final String testCoords = "35.71007° N, 139.81065° W";
    private String idLocation;
    private String nameLocation = "test";
    private String addressLocation = "test";

    private final String TRIP_ADVISOR_LOCATION_ENDPOINT = "https://api.content.tripadvisor.com/api/v1/location";

    // Used for fragments
    private ViewPager vp;
    private TabLayout tl;

    // Information needed for UI
    private ArrayList<VacationPicture> pictureGallery = new ArrayList<>();
    private JSONArray hours;
    private ArrayList<DailyWeatherItem> predictedWeather = new ArrayList<>();
    private ArrayList<UserReview> recentReviews = new ArrayList<>();
    public static final int MAX_REVIEWS = 5;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        vp = findViewById(R.id.viewpager);
        tl = findViewById(R.id.tab_layout);

        registerReceiver(apiReceiver, new IntentFilter(
                APIRequestService.Broadcast_id));

        Intent pastIntent = getIntent(); // gets intent that started this activity
        pastIntent.getStringExtra("latitude"); // Should be in format of {37.3983° N}
        pastIntent.getStringExtra("longitude");

        try {
            makeSearchRequest();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private void makeSearchRequest() throws UnsupportedEncodingException {
        // Uses Chatgpt coordinates to find location with lowest distance from target
        // TODO replace testcoords
        Intent intentTA = new Intent(getBaseContext(), APIRequestService.class);

        // Making endpoint for search from coordinates
        String encodedAddress = "";
        String searchEP = TRIP_ADVISOR_LOCATION_ENDPOINT + "/" + "nearby_search?latLong=" + coordEncoder(testCoords) + "&key="
                + tripAdvisorKey + "&address=" + encodedAddress + "&language=en";

        intentTA.putExtra("callerID", ID);
        intentTA.putExtra("apiUrl", searchEP);
        intentTA.putExtra("apiType", "TripAdvisor_Search");

        // Make request
        startService(intentTA);
    }

    private String addressEncoder(String address) {
        return "";
    }

    private String coordEncoder(String coords) {
        String cleanedCoordinates = coords.replaceAll("[^0-9,.\\-]", "").replaceAll("\\s", "");

        // Remove trailing commas
        cleanedCoordinates = cleanedCoordinates.replaceAll(",$", "");

        try {
            String encodedCoordinates = URLEncoder.encode(cleanedCoordinates, "UTF-8");
            System.out.println("Encoded Coordinates: " + encodedCoordinates);
            return encodedCoordinates;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void createApiRequests() {
        // TODO Replace testcoords
        String taPicEndpoint = createTaEndpoint("photos");
        String taDetEndpoint = createTaEndpoint("details");
        String taRevEndpoint = createTaEndpoint("reviews");

        String[] parts = testCoords.split(", ");
        String latStr = parts[0];
        String lonStr = parts[1];

        String latitude = latStr.substring(0, latStr.length() - 3);
        String longitude = lonStr.substring(0, lonStr.length() - 3);

        String weatherApi = "https://api.open-meteo.com/v1/forecast?latitude=" + latitude
            + "&longitude=" + longitude + "&current=temperature_2m,rain,showers&daily=weather_code,temperature_2m_max," +
                "temperature_2m_min,apparent_temperature_max,apparent_temperature_min,daylight_duration,precipitation_sum," +
                "precipitation_probability_max,wind_speed_10m_max&temperature_unit=fahrenheit&wind_speed_unit=" +
                "mph&precipitation_unit=inch";
        Log.v("GenApi", weatherApi);
        ArrayList<String> endpoints = new ArrayList<>(Arrays.asList(taPicEndpoint, taDetEndpoint, taRevEndpoint, weatherApi));
        executeApiRequests(endpoints);
    }

    private String createTaEndpoint(String endpointType) {
        return TRIP_ADVISOR_LOCATION_ENDPOINT + "/" + idLocation + "/" + endpointType + "?key=" + tripAdvisorKey + "&language=en";
    }

    private void executeApiRequests(ArrayList<String> urls) {
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

        // TripAdvisor API Request Service (Reviews)
        Intent intentTA3 = new Intent(getBaseContext(), APIRequestService.class);
        intentTA3.putExtra("callerID", ID);
        intentTA3.putExtra("apiUrl", urls.get(2));
        intentTA3.putExtra("apiType", "TripAdvisor_Reviews");
        startService(intentTA3);

        // Weather API
        Intent intentTA4 = new Intent(getBaseContext(), APIRequestService.class);
        intentTA4.putExtra("callerID", ID);
        intentTA4.putExtra("apiUrl", urls.get(3));
        intentTA4.putExtra("apiType", "Weather");
        startService(intentTA4);
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

    private void createHoursSchedule() throws JSONException {
        StringBuilder hoursText = new StringBuilder();
        for (int i = 0; i < hours.length(); i++) {
            hoursText.append(hours.get(i));
            if (i != hours.length() - 1) {
                hoursText.append("\n");
            }
        }
        TextView hourCard =  findViewById(R.id.hourBody);
        hourCard.setText(hoursText.toString());
    }

    private void createReviewsCard() {
        ReviewListAdapter listAdapter = new ReviewListAdapter(this, recentReviews);
        ListView listView = findViewById(R.id.reviewList);
        listView.setAdapter(listAdapter);
        CardView cv = findViewById(R.id.cardView2);
        cv.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
    }

    private void createWeatherCard() {
        WeatherListAdapter listAdapter = new WeatherListAdapter(this, predictedWeather);
        ListView listView = findViewById(R.id.weatherList);
        listView.setAdapter(listAdapter);
        CardView cv = findViewById(R.id.priceCard);
        cv.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
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
                                pictureGallery.add(new VacationPicture("Caption: " + caption, imageUrl));
                            }
                            createGallery();
                            break;
                        case "TripAdvisor_Desc":
                            Log.v("Receiver", "TripAdvisor_Desc!");
                            JSONObject time = response.getJSONObject("hours");
                            hours = time.getJSONArray("weekday_text");
                            RatingBar totalRating = findViewById(R.id.totalRating);
                            totalRating.setRating((float) response.getDouble("rating"));
                            TextView galleryTitle = findViewById(R.id.galleryText);
                            galleryTitle.setText("Details: " + response.getString("name"));
                            createHoursSchedule();
                            break;
                        case "TripAdvisor_Reviews":
                            Log.v("Receiver", "TripAdvisor_Reviews!");
                            JSONArray reviewsArray = response.getJSONArray("data");

                            for (int i = 0; i < Math.min(reviewsArray.length(), MAX_REVIEWS); i++) {
                                JSONObject review = reviewsArray.getJSONObject(i);
                                JSONObject user = review.getJSONObject("user");
                                Log.v("Receiver", user.toString());
                                String text = review.getString("text");
                                int rating = review.getInt("rating");
                                String avatarUrl = user.getJSONObject("avatar").getString("small");
                                String username = user.getString("username");
                                String revDate = review.getString("published_date");
                                String date = review.optString("travel_date", revDate.substring(0, revDate.indexOf('T')));

                                recentReviews.add(new UserReview(text, rating, username, avatarUrl, date));
                            }

                            createReviewsCard();
                            break;
                        case "TripAdvisor_Search":
                            try {
                                JSONArray dataArr = response.getJSONArray("data");

                                double minDistance = Double.MAX_VALUE;
                                String closestLocationId = null;

                                for (int i = 0; i < dataArr.length(); i++) {
                                    JSONObject location = dataArr.getJSONObject(i);
                                    String locationId = location.getString("location_id");
                                    String distanceStr = location.getString("distance");

                                    double distance = Double.parseDouble(distanceStr);

                                    String locationName = location.getString("name");
                                    if (locationName.equalsIgnoreCase(nameLocation) || (distance < minDistance)) {
                                        minDistance = distance;
                                        closestLocationId = locationId;
                                    }
                                }

                                if (closestLocationId != null) {
                                    Log.d("LocationProcessor", "Closest Location ID: " + closestLocationId);
                                    idLocation = closestLocationId;
                                } else {
                                    Log.d("LocationProcessor", "No matching or closest location found.");
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            createApiRequests();
                            break;
                        case "Weather":
                            Log.v("Receiver", "Weather!");
                            try {;
                                JSONArray dailyArray = response.getJSONObject("daily").getJSONArray("time");

                                for (int i = 0; i < dailyArray.length(); i++) {
                                    String day = dailyArray.getString(i);
                                    String degreesMax = response.getJSONObject("daily").getJSONArray("temperature_2m_max").getString(i);
                                    String precipitation = response.getJSONObject("daily").getJSONArray("precipitation_sum").getString(i);
                                    String windSpeedMax = response.getJSONObject("daily").getJSONArray("wind_speed_10m_max").getString(i);

                                    DailyWeatherItem dailyWeatherItem = new DailyWeatherItem(day, degreesMax, precipitation, windSpeedMax);
                                    predictedWeather.add(dailyWeatherItem);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            createWeatherCard();
                            break;
                        default:
                            Log.v("Receiver", "Default!");
                            break;
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    };

}