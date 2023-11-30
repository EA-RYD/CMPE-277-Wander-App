package com.example.wander_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;
import android.os.Bundle;
import android.Manifest;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    FusedLocationProviderClient fusedLocationProviderClient;

    private ActivityMainBinding binding;
    private MainViewModel viewModel;

    private String tripAdvisorKey = "DA033993B5A145549DCEF7D9486BBE21";

    private final String ID = "main";
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
//        boolean result = this.deleteDatabase("itinerary");
//        Log.i(">>MainActivity", "onCreate: Delete Database " + result);
        Database db = Room.databaseBuilder(
                this,
                Database.class,
                "itinerary"
        ).build();


        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        loadIndicator = findViewById(R.id.loadingBar);


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

        binding.btAddToItinerary.setOnClickListener(v -> {
            List<ItineraryItem> items = new ArrayList<>();
            ArrayAdapter<Suggestion> adapter = (ArrayAdapter<Suggestion>) binding.lvSuggestionList.getAdapter();
            for (int i = 0; i < adapter.getCount(); i++) {
                Suggestion item = adapter.getItem(i);
                if ((item != null) && item.isChecked()) {
                    Integer suggestionId = i;
                    String locationId = viewModel.getTaPhotoResult().getValue().get(i).getLocationId();
                    String responseString = viewModel.getTaPhotoResult().getValue().get(i).getResponseString();
                    String imageUrl = viewModel.getTaPhotoResult().getValue().get(i).getImgUrl();
                    String locationName = viewModel.getSuggestionList().getValue().getSuggestions().get(i).getName();
                    String description = viewModel.getSuggestionList().getValue().getSuggestions().get(i).getDescription();
                    String address = viewModel.getSuggestionList().getValue().getSuggestions().get(i).getAddress();
                    ItineraryItem itineraryItem = new ItineraryItem(suggestionId, locationId, responseString, locationName, description, imageUrl, address, 0);
                    items.add(itineraryItem);
                }
            }

            viewModel.addItemsToItinerary(items);
            Log.i(">>MainActivity", "onCreate: " + "Itinerary updated");
        });

        binding.btnLoadItinerary.setOnClickListener(v -> {
            loadItineraryItems(db);
        });

        binding.btnSaveToPhone.setOnClickListener(v -> {
            saveItineraryItems(db);
        });

        viewModel.getResponse().observe(this, response -> {
            Log.i("MainActivity", "onCreate: " + response);
        });

        viewModel.getRawSuggestionList().observe(this, suggestionList -> {
            Log.i("MainActivity", "onCreate: " + suggestionList);
            viewModel.getSuggestionList().postValue(suggestionList);
            makeTASearchApiCalls(suggestionList);

            //Handler to post a delayed api call to get TA photos
            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            ArrayList<TASearchItem> items = (ArrayList<TASearchItem>) viewModel.getTaSearchResult().getValue().getSearchItems();
                            makeTAPhotoApiCalls(items);
                        }
                    },
                    15000);

        });

        viewModel.getSuggestionList().observe(this, suggestionList -> {
            Log.i("MainActivity", "onCreate: SuggestionList Updated: " + suggestionList);
            createSuggestionListCard(suggestionList);
        });


        viewModel.getItinerary().observe(this, itineraryItems -> {
            if (itineraryItems.size() > 0) {
                binding.btnSaveToPhone.setEnabled(true);
                binding.btnSendItineraryEmail.setEnabled(true);
            }
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

    private BroadcastReceiver apiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            JSONObject response = null;
            Log.i(">>Receiver", "TA Message Received!");
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
                                String locationName = viewModel.getSuggestionList().getValue().getSuggestions().get(Integer.parseInt(suggestionId)).getName();
                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject tempObj = dataArray.getJSONObject(i);
                                    String name = tempObj.getString("name");
                                    if (tempObj.has("name") && (name.equals(locationName) || name.contains(locationName) || locationName.contains(name))) {
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
                                    viewModel.updateImgUrlToSuggestion(Integer.parseInt(suggestionId), imageUrl);

                                    Log.i(">>Receiver", "Image URL: " + imageUrl);
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

    private void saveItineraryItems(Database database) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                database.itineraryItemDao().deleteAll();
                for (ItineraryItem item : viewModel.getItinerary().getValue()) {
                    database.itineraryItemDao().insert(item);
                }
            }
        }).start();
    }

    public void loadItineraryItems(Database database) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<ItineraryItem> items = database.itineraryItemDao().getItineraryItems();
                Log.i(">>MainActivity", "fetchLastInsertedItineraryItems: " + items.toString());
                viewModel.getItinerary().postValue(items);
            }
        }).start();
    }


    private void createItineraryCard() {
        CardView cv = findViewById(R.id.cvItinerary);
        ArrayList itineraryItems = new ArrayList(viewModel.getItinerary().getValue());
        Log.i(">>MainActivity", "createItineraryCard: " + itineraryItems.size());
        for (Object item : itineraryItems) {
            Log.i(">>MainActivity", "createItineraryCard: " + item.toString());
        }
        ItineraryListAdapter.OnDeleteListener listener = new ItineraryListAdapter.OnDeleteListener() {
            @Override
            public void onDelete(int position) {
                Log.i(">>MainActivity", "onDeleteButtonClick: " + "Delete button clicked" + position);
                viewModel.deleteItineraryItem(position);
            }
        };
        ItineraryListAdapter listAdapter = new ItineraryListAdapter(this, itineraryItems, listener);
        ListView listView = findViewById(R.id.lvItineraryList);
        listView.setAdapter(listAdapter);
        Integer listViewHeight = getTotalHeightOfListView(listView);
        listView.getLayoutParams().height = listViewHeight;
        cv.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;


    }

    private void createSuggestionListCard(SuggestionList suggestionList) {
        CardView cv = findViewById(R.id.cvSuggestionList);
        ArrayList suggestionItems = new ArrayList(suggestionList.getSuggestions());
//        ArrayList taPhotoItems = new ArrayList(viewModel.getTaPhotoResult().getValue());
        Log.i(">>MainActivity", "createSuggestionListCard: " + suggestionItems.size());
        SuggestionListAdapter.OnDetailsListener detailsListener = new SuggestionListAdapter.OnDetailsListener() {
            @Override
            public void onDetails(int position) {
                Intent detailsIntent = new Intent(MainActivity.this, DetailsActivity.class);
                String tempId = viewModel.getTaPhotoResult().getValue().get(position).getLocationId();
                String tempResp = viewModel.getTaPhotoResult().getValue().get(position).getResponseString();

                if (!tempId. isEmpty()) {
                    detailsIntent. putExtra("locationId", viewModel.getTaPhotoResult().getValue().get(position) .getLocationId());
                    detailsIntent.putExtra("responseString", viewModel. getTaPhotoResult(). getValue() .get(position).getResponseString());
                    startActivity(detailsIntent);
                } else {
                    Toast.makeText(MainActivity.this, "No details available!", Toast.LENGTH_SHORT).show();
                    Log.i(">>MainActivity", "onDetails: " + "No details available!");
                }
            }
        };
        SuggestionListAdapter listAdapter = new SuggestionListAdapter(this, suggestionItems, detailsListener);
        ListView listView = findViewById(R.id.lvSuggestionList);
        listView.setAdapter(listAdapter);
        Integer listViewHeight = getTotalHeightOfListView(listView);
        listView.getLayoutParams().height = listViewHeight;
        cv.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
    }

    public static int getTotalHeightOfListView(ListView listView) {
        Log.i(">>MainActivity", "getTotalHeightOfListView: ");
        ListAdapter adapter = listView.getAdapter();
        if (adapter == null) {
            return 0;
        }

        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, listView);
            listItem.measure(
                    View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            );
            totalHeight += listItem.getMeasuredHeight();
        }

        // Add the height of the dividers
        totalHeight += (listView.getDividerHeight() * (adapter.getCount() - 1));

        return totalHeight;
    }

    private void makeTASearchApiCalls(SuggestionList suggestionList) {
        viewModel.getTaSearchResult().getValue().getSearchItems().clear();
        viewModel.resetPhotoResult();
        for (int i = 0; i < suggestionList.getSuggestions().size(); i++) {
            Suggestion item = suggestionList.getSuggestions().get(i);
            String longitude = item.getLongitude();
            String latitude = item.getLatitude();
            String street = item.getStreetAddress();
            String suggestionId = String.valueOf(i);
            try {
                makeSearchRequest(longitude, latitude, street, suggestionId);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    private void makeTAPhotoApiCalls(ArrayList<TASearchItem> searchItems) {
        for (int i = 0; i < searchItems.size(); i++) {
            TASearchItem item = searchItems.get(i);
            String locationId = item.getLocationId();
            String suggestionId = item.getSuggestionId();
            String taPicEndpoint = createTaEndpoint("photos", locationId);
            Log.i(">>MainActivity", "Call TA Pic Endpoint: " + taPicEndpoint);
            Intent intentTA = new Intent(getBaseContext(), APIRequestService.class);
            intentTA.putExtra("callerID", ID);
            intentTA.putExtra("apiUrl", taPicEndpoint);
            intentTA.putExtra("apiType", "photos");
            intentTA.putExtra("suggestionId", suggestionId);
            startService(intentTA);
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

}
