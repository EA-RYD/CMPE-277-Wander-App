package com.example.wander_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;
import android.os.Bundle;
import android.Manifest;
import android.view.View;
import android.widget.DatePicker;

import com.example.wander_app.databinding.ActivityMainBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    FusedLocationProviderClient fusedLocationProviderClient;

    private ActivityMainBinding binding;
    private MainViewModel viewModel;

    private String tripAdvisorKey = "DA033993B5A145549DCEF7D9486BBE21";
    private final String testCoords = "35.71007° N, 139.81065° W";
    private String idLocation;
    private String nameLocation = "test";
    private String addressLocation = "test";

    private final String TRIP_ADVISOR_LOCATION_ENDPOINT = "https://api.content.tripadvisor.com/api/v1/location";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        binding.btnSendRequest.setOnClickListener(v -> {
            viewModel.updateMessage(binding.etLocation.getText().toString());
            viewModel.updateMessage(binding.etPreference.getText().toString());
            viewModel.sendRequest();
        });

        binding.btnCurrentLocation.setOnClickListener(v -> {
            Log.i("MainActivity", "onCreate: " + "Button Clicked");
            FindLocation();
        });

        binding.btnCalendar.setOnClickListener(v -> {
            showDatePickerDialog();
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
            binding.svSuggestions.setVisibility(View.VISIBLE);


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

    private void checkAudioPermission() {
        Log.i("MainActivity", "AskForPermission: " + "Asking for audio permission");
        requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},1);

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

    public void onClickButton (View view) {
        Intent detailsIntent = new Intent(MainActivity.this, DetailsActivity.class);
        startActivity(detailsIntent);
    }

//    private void makeSearchRequest() throws UnsupportedEncodingException {
//        // Uses Chatgpt coordinates to find location with lowest distance from target
//        // TODO replace testcoords
//        Intent intentTA = new Intent(getBaseContext(), APIRequestService.class);
//
//        // Making endpoint for search from coordinates
//        String encodedAddress = "";
//        String searchEP = TRIP_ADVISOR_LOCATION_ENDPOINT + "/" + "nearby_search?latLong=" + coordEncoder(testCoords) + "&key="
//                + tripAdvisorKey + "&address=" + encodedAddress + "&language=en";
//
//        intentTA.putExtra("callerID", ID);
//        intentTA.putExtra("apiUrl", searchEP);
//        intentTA.putExtra("apiType", "TripAdvisor_Search");
//
//        // Make request
//        startService(intentTA);
//    }

}
