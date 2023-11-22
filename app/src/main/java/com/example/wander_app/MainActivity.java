package com.example.wander_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;
import android.os.Bundle;
import android.Manifest;
import android.view.View;

import com.example.wander_app.databinding.ActivityMainBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    FusedLocationProviderClient fusedLocationProviderClient;

    private ActivityMainBinding binding;
    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        binding.btnSendRequest.setOnClickListener(v -> {
            viewModel.updateMessage(binding.etLocation.getText().toString());
        });

        binding.btnCurrentLocation.setOnClickListener(v -> {
            Log.i("MainActivity", "onCreate: " + "Button Clicked");
            FindLocation();
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

    public void onClickButton (View view) {
        Intent detailsIntent = new Intent(MainActivity.this, DetailsActivity.class);
        startActivity(detailsIntent);
    }
}
