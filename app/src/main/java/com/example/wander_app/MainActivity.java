package com.example.wander_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;
import android.os.Bundle;
import android.Manifest;

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
            viewModel.updateMessage(binding.etMessage.getText().toString());
        });

        binding.btnCurrentLocation.setOnClickListener(v -> {
            Log.i("MainActivity", "onCreate: " + "Button Clicked");
            FindLocation();
        });


        viewModel.getChatResponse().observe(this, response -> {
            Log.i("MainActivity", "onCreate: " + response);
//            binding.tvResponse.setText(chatResponse.getResponse());
        });

        viewModel.getLocation().observe(this, location -> {
            Log.i("MainActivity", "onCreate: " + location);
            binding.etMessage.setText(location);
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
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } else {
            AskforPermission();
        }


    }

    private void AskforPermission() {
        Log.i("MainActivity", "AskForPermission: " + "Asking for permission");
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
    }
    }
