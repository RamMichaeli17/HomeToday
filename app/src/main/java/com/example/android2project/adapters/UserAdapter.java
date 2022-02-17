package com.example.android2project.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android2project.databinding.ItemContainerUserBinding;
import com.example.android2project.listeners.UserListener;
import com.example.android2project.models.chatUser;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final List<chatUser> chatUsers;
    private final UserListener userListener;

    public UserAdapter(List<chatUser> chatUsers, UserListener userListener) {
        this.chatUsers = chatUsers;
        this.userListener = userListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerUserBinding itemContainerUserBinding = ItemContainerUserBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new UserViewHolder(itemContainerUserBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.setUserData(chatUsers.get(position));

    }

    @Override
    public int getItemCount() {
        return chatUsers.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        ItemContainerUserBinding binding;

        UserViewHolder(ItemContainerUserBinding itemContainerUserBinding) {
            super(itemContainerUserBinding.getRoot());
            binding = itemContainerUserBinding;
        }

        void setUserData(chatUser chatUser) {
            binding.textName.setText(chatUser.name);
            binding.textEmail.setText(chatUser.email);
            binding.imageProfile.setImageBitmap(getUserImage(chatUser.image));
            binding.getRoot().setOnClickListener(v-> userListener.onUserClicked(chatUser));

        }
    }

    private Bitmap getUserImage(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0, bytes.length);
    }
}
