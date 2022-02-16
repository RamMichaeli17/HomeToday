package com.example.android2project.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.android2project.databinding.ActivityChatBinding;
import com.example.android2project.models.User;
import com.example.android2project.utilities.Constants;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private User receiverUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
        loadReceiverDetails();
    }


    private void loadReceiverDetails() {
        receiverUser = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        binding.textName.setText(receiverUser.name);
    }

    private void setListeners() {
        binding.imageBack.setOnClickListener(v-> onBackPressed());
    }
}