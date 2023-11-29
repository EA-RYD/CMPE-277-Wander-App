package com.example.wander_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.Room;

import com.bumptech.glide.Glide;

import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.AttributeSet;
import android.util.Log;
import android.os.Bundle;
import android.Manifest;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wander_app.databinding.ActivityMainBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    FusedLocationProviderClient fusedLocationProviderClient;

    private ActivityMainBinding binding;
    private MainViewModel viewModel;

    private String tripAdvisorKey = "DA033993B5A145549DCEF7D9486BBE21";

    private final String ID = "main";
    private String nameLocation = "";
    private String addressLocation = "";

    private ImageView[] imageViews;
    private TextView[] textViews;
    private ProgressBar loadIndicator;

    private final String TRIP_ADVISOR_LOCATION_ENDPOINT = "https://api.content.tripadvisor.com/api/v1/location";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        registerReceiver(apiReceiver, new IntentFilter(
                APIRequestService.Broadcast_id));

        Database db = Room.databaseBuilder(
                this,
                Database.class,
                "itinerary"
        ).build();


        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        loadIndicator = findViewById(R.id.loadingBar);


        imageViews = new ImageView[]{
                (binding.ivList01),
                (binding.ivList02),
                (binding.ivList03),
                (binding.ivList04),
                (binding.ivList05),
                (binding.ivList06)
        };

        textViews = new TextView[]{
                (binding.tvList01Name),
                (binding.tvList02Name),
                (binding.tvList03Name),
                (binding.tvList04Name),
                (binding.tvList05Name),
                (binding.tvList06Name)
        };

        Button[] buttons = new Button[]{
                binding.btList01,
                binding.btList02,
                binding.btList03,
                binding.btList04,
                binding.btList05,
                binding.btList06
        };

        for (Button button : buttons) {
            button.setOnClickListener(v -> {
                // Handle button click
                onButtonClick(v);
            });
        }


        binding.btnSendRequest.setOnClickListener(v -> {
            loadIndicator.setVisibility(View.VISIBLE);
            viewModel.updateMessage(binding.etLocation.getText().toString());
            String preferenceText = binding.etPreference.getText().toString();
            if (!preferenceText.isEmpty()) {
                viewModel.updateMessage(preferenceText);
            }
            viewModel.sendRequest();
        });

        binding.btnCurrentLocation.setOnClickListener(v -> {
            Log.i("MainActivity", "onCreate: " + "Button Clicked");
            FindLocation();
        });

        binding.btnCalendar.setOnClickListener(v -> {
            showDatePickerDialog();
        });

        binding.btSaveToItinerary.setOnClickListener(v -> {
            List<ItineraryItem> items = new ArrayList<>();
            for (int i = 0; i < 6; i++) {
                Integer suggestionId = i;
                String locationId = viewModel.getTaPhotoResult().getValue().get(i).getLocationId();
                String responseString = viewModel.getTaPhotoResult().getValue().get(i).getResponseString();
                String imageUrl = viewModel.getTaPhotoResult().getValue().get(i).getImgUrl();
                String locationName = viewModel.getSuggestionList().getValue().getSuggestions().get(i).getName();
                String description = viewModel.getSuggestionList().getValue().getSuggestions().get(i).getDescription();
                ItineraryItem itineraryItem = new ItineraryItem(suggestionId, locationId, responseString, locationName, description, imageUrl, 0);
                items.add(itineraryItem);
            }

            if (items != null) {
                saveItineraryItems(db, items);
            }
            Log.i(">>MainActivity", "onCreate: " + "Itinerary saved");
        });

        binding.btnLoadItinerary.setOnClickListener(v -> {
            fetchLastInsertedItineraryItems(db);
        });

        viewModel.getResponse().observe(this, response -> {
            Log.i("MainActivity", "onCreate: " + response);
        });

        viewModel.getSuggestionList().observe(this, suggestionList -> {
            Log.i("MainActivity", "onCreate: " + suggestionList);
            binding.tvList01Description.setText(suggestionList.getSuggestions().get(0).getDescription());
            binding.tvList01Name.setText(suggestionList.getSuggestions().get(0).getName());
            binding.tvList02Description.setText(suggestionList.getSuggestions().get(1).getDescription());
            binding.tvList02Name.setText(suggestionList.getSuggestions().get(1).getName());
            binding.tvList03Description.setText(suggestionList.getSuggestions().get(2).getDescription());
            binding.tvList03Name.setText(suggestionList.getSuggestions().get(2).getName());
            binding.tvList04Description.setText(suggestionList.getSuggestions().get(3).getDescription());
            binding.tvList04Name.setText(suggestionList.getSuggestions().get(3).getName());
            binding.tvList05Description.setText(suggestionList.getSuggestions().get(4).getDescription());
            binding.tvList05Name.setText(suggestionList.getSuggestions().get(4).getName());
            binding.tvList06Description.setText(suggestionList.getSuggestions().get(5).getDescription());
            binding.tvList06Name.setText(suggestionList.getSuggestions().get(5).getName());
            binding.llSuggestions.setVisibility(View.VISIBLE);

//            reset image and search results
            viewModel.getTaSearchResult().getValue().getSearchItems().clear();
            for (ImageView imageView : imageViews) {
                imageView.setImageResource(R.drawable.default_picture);
            }
            //make search request for each suggestion
            for (int i = 0; i < suggestionList.getSuggestions().size(); i++) {
                try {
                    String streetAddress = suggestionList.getSuggestions().get(i).getStreetAddress();
                    String latitude = suggestionList.getSuggestions().get(i).getLatitude();
                    String longitude = suggestionList.getSuggestions().get(i).getLongitude();
                    makeSearchRequest(longitude, latitude, streetAddress, String.valueOf(i));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });

//        viewModel.getLoadedSuggestionList().observe(this, suggestionList -> {
//            Log.i("MainActivity", "onCreate: " + suggestionList);
//            binding.tvList01Description.setText(suggestionList.get(0).getDescription());
//            binding.tvList01Name.setText(suggestionList.get(0).getName());
//            binding.tvList02Description.setText(suggestionList.get(1).getDescription());
//            binding.tvList02Name.setText(suggestionList.get(1).getName());
//            binding.tvList03Description.setText(suggestionList.get(2).getDescription());
//            binding.tvList03Name.setText(suggestionList.get(2).getName());
//            binding.tvList04Description.setText(suggestionList.get(3).getDescription());
//            binding.tvList04Name.setText(suggestionList.get(3).getName());
//            binding.tvList05Description.setText(suggestionList.get(4).getDescription());
//            binding.tvList05Name.setText(suggestionList.get(4).getName());
//            binding.tvList06Description.setText(suggestionList.get(5).getDescription());
//            binding.tvList06Name.setText(suggestionList.get(5).getName());
//            binding.llSuggestions.setVisibility(View.VISIBLE);
//
//            for (int i = 0; i < 6; i++) {
//                String imgUrl = viewModel.getLoadedSuggestionList().getValue().get(i).getImg();
//                Log.i(">>MainActivity", "Loaded img url: " + imgUrl);
//                if (imgUrl != null && !imgUrl.trim().isEmpty()) {
//                    Glide.with(getBaseContext()).load(imgUrl).into(imageViews[i]);
//                } else{
//                    Log.i(">>MainActivity", "onCreate: " + "No image url found");
//                    imageViews[i].setImageResource(R.drawable.default_picture);
//                }
//            }
//        });


        viewModel.getTaSearchResult().observe(this, taSearchResult -> {
            Log.i(">>MainActivity", "TA Search Result updated");
            for (TASearchItem item : taSearchResult.getSearchItems()) {
                Log.i(">>MainActivity", "TA Search Item: " + item.toString());
            }
            if (taSearchResult.getSearchItems().size() == 6) {
                for (int i = 0; i < taSearchResult.getSearchItems().size(); i++) {
                    TASearchItem item = taSearchResult.getSearchItems().get(i);
                    String locationId = item.getLocationId();
//                taPhotoItems[i].setLocationId(locationId);
                    String taPicEndpoint = createTaEndpoint("photos", locationId);
                    Log.i(">>MainActivity", "Call TA Pic Endpoint: " + taPicEndpoint);
                    Intent intentTA = new Intent(getBaseContext(), APIRequestService.class);
                    intentTA.putExtra("callerID", ID);
                    intentTA.putExtra("apiUrl", taPicEndpoint);
                    intentTA.putExtra("apiType", "photos");
                    intentTA.putExtra("suggestionId", item.getSuggestionId());
                    startService(intentTA);
                }
            }
        });

        viewModel.getItinerary().observe(this, itineraryItems -> {
            viewModel.transformItineraryToSuggestionList(itineraryItems);
            viewModel.transformItineraryToTaPhotoResult(itineraryItems);
            createItineraryCard();
        });

        viewModel.getLocation().observe(this, location -> {
            Log.i("MainActivity", "onCreate: " + location);
            binding.etLocation.setText(location);
        });

    }

    private void FindLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.i("MainActivity", "FindLocation: " + "Permission Granted");
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location == null) {
                        Log.i("MainActivity", "onSuccess: " + "Location is null");
                        return;
                    }
                    Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                    try {
                        List<Address> addresslist = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        viewModel.setLocation(addresslist.get(0).getLocality() + ", " + addresslist.get(0).getCountryName());
                        Log.i("MainActivity", "onSuccess: " + addresslist.get(0).getLocality() + ", " + addresslist.get(0).getCountryName());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } else {
            askForLocationPermission();
        }


    }

    private void askForLocationPermission() {
        Log.i("MainActivity", "AskForPermission: " + "Asking for user location permission");
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
    }


    private void showDatePickerDialog() {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        monthOfYear += 1;
                        if (monthOfYear > Calendar.DECEMBER) {
                            monthOfYear = Calendar.JANUARY;
                            year += 1;
                        }
                        Log.i(">>MainActivity", "onDateSet: " + dayOfMonth + "-" + monthOfYear + "-" + year);
                    }
                }, year, month, day);

        datePickerDialog.show();
    }



    private void onButtonClick(View v) {
        String btnId = String.valueOf(v.getId());

        if (btnId.length() >= 1) {
            String lastChar = btnId.substring(btnId.length() - 1);
            try {
                int id = Integer.parseInt(lastChar);
                Log.i(">>MainActivity", "Clicked Button ID: " + id);
                // Open details activity
                Intent detailsIntent = new Intent(MainActivity.this, DetailsActivity.class);
                String tempId = viewModel.getTaPhotoResult().getValue().get(id - 2).getLocationId();
                String tempResp = viewModel.getTaPhotoResult().getValue().get(id - 2).getResponseString();
                Log.i(">>MainActivity", "onButtonClick: location_id " + viewModel.getTaPhotoResult().getValue().get(id - 2).getLocationId());
                Log.i(">>MainActivity", "onButtonClick: response_string " + viewModel.getTaPhotoResult().getValue().get(id - 2).getResponseString());

                if (!tempId.isEmpty()) {
                    detailsIntent.putExtra("locationId", viewModel.getTaPhotoResult().getValue().get(id - 2).getLocationId());
                    detailsIntent.putExtra("responseString", viewModel.getTaPhotoResult().getValue().get(id - 2).getResponseString());
                    startActivity(detailsIntent);
                } else {
                    Toast.makeText(this, "No details available!", Toast.LENGTH_SHORT).show();
                }

            } catch (NumberFormatException e) {
                Log.i(">>MainActivity", "onButtonClick: " + "Error parsing button ID");
            }
        } else {
            Log.i(">>MainActivity", "onButtonClick: " + "Error parsing button ID");
        }

    }


    private void makeSearchRequest(String longitude, String latitude, String street, String suggestionId) throws UnsupportedEncodingException {
        // Uses ChatGpt coordinates and street address to find location with lowest distance from target
        Intent intentTA = new Intent(getBaseContext(), APIRequestService.class);

        // Making endpoint for search from coordinates
        String encodedAddress = encodeAddress(street);
        String searchEP = TRIP_ADVISOR_LOCATION_ENDPOINT + "/" + "nearby_search?latLong=" + coordEncoder(latitude + ", " + longitude) + "&key="
                + tripAdvisorKey + "&address=" + encodedAddress + "&language=en";
        Log.i(">>MainActivity", "makeSearchRequest: " + searchEP);
        intentTA.putExtra("callerID", ID);
        intentTA.putExtra("apiUrl", searchEP);
        intentTA.putExtra("apiType", "TripAdvisor_Search");
        intentTA.putExtra("suggestionId", suggestionId.toString());

//         Make request
        startService(intentTA);
    }


    private BroadcastReceiver apiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            JSONObject response = null;
            Log.v(">>Receiver", "TA Message Received!");
            if (intent != null && intent.getStringExtra("callerID").equals(ID)) {
                try {
                    response = new JSONObject(intent.getStringExtra("jsonObject"));
                    String suggestionId = intent.getStringExtra("suggestionId");
                    String apiType = intent.getStringExtra("apiType");
                    Log.i(">>Receiver", "Suggestion ID: " + suggestionId);
                    if (apiType.equals("TripAdvisor_Search")) {
                        Log.i(">>Receiver", "Search Response: " + response);
                        TASearchItem taSearchItem = new TASearchItem(suggestionId, "0000");
                        if (response.has("data")) {
                            JSONArray dataArray = response.getJSONArray("data");
                            if (dataArray.length() > 0) {
                                JSONObject data = null;
                                // New logic to find the matching name
                                String textViewText = textViews[Integer.parseInt(suggestionId)].getText().toString();
                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject tempObj = dataArray.getJSONObject(i);
                                    String name = tempObj.getString("name");
                                    if (tempObj.has("name") && (name.equals(textViewText) || name.contains(textViewText) || textViewText.contains(name))) {
                                        Log.i(">>Receiver", "Found matching name: " + tempObj.getString("name"));
                                        data = tempObj;
                                        break;
                                    }
                                }

                                // Default to first item if no match is found
                                if (data == null) {
                                    data = dataArray.getJSONObject(0);
                                }

                                Log.i(">>Receiver", "Search Data: " + data.toString());
                                if (data.has("location_id")) {
                                    String locationId = data.getString("location_id");
                                    //update location id in the taPhotoResult
                                    viewModel.updatePhotoItemLocationId(Integer.parseInt(suggestionId), locationId);
                                    for (TAPhotoItem item : viewModel.getTaPhotoResult().getValue()) {
                                        Log.d(">>PhotoItemLog", item.toString());
                                    }

                                    taSearchItem.setLocationID(locationId);
                                    viewModel.addSearchItem(taSearchItem);
                                } else {
                                    viewModel.addSearchItem(taSearchItem);
                                }
                            } else {
                                taSearchItem.setLocationID("");
                                viewModel.addSearchItem(taSearchItem);
                            }
                        }

                        Log.i(">>Receiver", "taSearchResult " + viewModel.getTaSearchResult().getValue().toString());
                    }
                    if (apiType.equals("photos")) {
                        //                    taPhotoItems[Integer.parseInt(suggestionId)].setResponseString(response.toString());
                        Log.i(">>Receiver", "Photo Response: " + response);
                        //update photo item response string
                        viewModel.updatePhotoItemResponseString(Integer.parseInt(suggestionId), response.toString());
                        for (TAPhotoItem item : viewModel.getTaPhotoResult().getValue()) {
                            Log.d(">>PhotoItemLog", item.toString());
                        }
                        if (response.has("data")) {
                            JSONArray dataArray = response.getJSONArray("data");
                            JSONObject data = dataArray.getJSONObject(0);

                            if (data.has("images")) {
                                JSONObject images = data.getJSONObject("images");
                                if (images.has("medium")) {
                                    loadIndicator.setVisibility(View.INVISIBLE);
                                    JSONObject mediumImage = images.getJSONObject("medium");
                                    String imageUrl = mediumImage.getString("url");
                                    viewModel.updatePhotoItemImgUrl(Integer.parseInt(suggestionId), imageUrl);
                                    Log.i(">>Receiver", "Image URL: " + imageUrl);
                                    Glide.with(getBaseContext()).load(imageUrl).into(imageViews[Integer.parseInt(suggestionId)]);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

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

    public static String encodeAddress(String address) {
        try {
            // Encode the address using UTF-8 encoding
            return URLEncoder.encode(address, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String createTaEndpoint(String endpointType, String locationId) {
        return TRIP_ADVISOR_LOCATION_ENDPOINT + "/" + locationId + "/" + endpointType + "?key=" + tripAdvisorKey + "&language=en";
    }

    private void saveItineraryItems(Database database, List<ItineraryItem> items) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (ItineraryItem item : items) {
                    database.itineraryItemDao().insert(item);
                }
                viewModel.updateItinerary(items);
            }
        }).start();
    }

    public void fetchLastInsertedItineraryItems(Database database) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<ItineraryItem> items = database.itineraryItemDao().getLastInsertedItineraryItems();
                Log.i(">>MainActivity", "fetchLastInsertedItineraryItems: " + items.toString());
                viewModel.updateItinerary(items);
            }
        }).start();
    }

    private void createItineraryCard() {
        CardView cv = findViewById(R.id.cvItinerary);
        ArrayList itineraryItems = new ArrayList(viewModel.getItinerary().getValue());
        Log.i(">>MainActivity", "createItineraryCard: " + itineraryItems.size() );
        for (Object item : itineraryItems) {
            Log.i(">>MainActivity", "createItineraryCard: " + item.toString());
        }
        ItineraryListAdapter listAdapter = new ItineraryListAdapter(this, itineraryItems);
        ListView listView = findViewById(R.id.lvItineraryList);
        listView.setAdapter(listAdapter);
        cv.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;

    }

}
