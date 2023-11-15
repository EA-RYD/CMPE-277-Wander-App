package com.example.wander_app;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.View;
import android.os.Bundle;

import com.example.wander_app.databinding.ActivityMainBinding;
import com.tutorial.chatgptapp.ChatGptRepository;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        binding.btnSendRequest.setOnClickListener(v->{
            viewModel.updateMessage(binding.etMessage.getText().toString());
        });
        viewModel.getChatResponse().observe(this, response -> {
            Log.i("MainActivity", "onCreate: "+response);
//            binding.tvResponse.setText(chatResponse.getResponse());
        });
}}