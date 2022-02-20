package com.example.android2project;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android2project.R;
import com.example.android2project.activities.ChatActivity;
import com.example.android2project.adapters.RecentConversationsAdapter;
import com.example.android2project.listeners.ConversionListener;
import com.example.android2project.models.ChatMessage;
import com.example.android2project.models.chatUser;
import com.example.android2project.utilities.Constants;
import com.example.android2project.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class fragment2_chat extends Fragment implements ConversionListener {

    private PreferenceManager preferenceManager;
    private List<ChatMessage> conversations;
    private RecentConversationsAdapter conversationsAdapter;
    private FirebaseFirestore database;

    private DocumentReference documentReference;

    RecyclerView recyclerView;
    ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.ram_activity_main,container,false);

        recyclerView = rootView.findViewById(R.id.conversationsRecyclerView);
        progressBar = rootView.findViewById(R.id.progressBarr);

        return rootView;

    }

    public void onPause() {
        super.onPause();
        documentReference.update(Constants.KEY_AVAILABILITY,0);
    }

    public void onResume() {
        super.onResume();
        init();
        loadUserDetails();
        getToken();
        listenConversations();
        documentReference.update(Constants.KEY_AVAILABILITY,1);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        preferenceManager = new PreferenceManager(getActivity());
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        documentReference = database.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID));



    }

    private void init() {
        conversations =new ArrayList<>();
        conversationsAdapter = new RecentConversationsAdapter(conversations, this);
        recyclerView.setAdapter(conversationsAdapter);
        database = FirebaseFirestore.getInstance();
    }

    private void loadUserDetails() {
        byte[] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE),Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    // Too many times...
    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    private void listenConversations() {
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID,preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_RECEIVER_ID,preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {

       if(error != null){
           return;
       }
       if(value!= null) {
           for(DocumentChange documentChange : value.getDocumentChanges()) {
               if(documentChange.getType() == DocumentChange.Type.ADDED) {
                   String senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                   String receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                   ChatMessage chatMessage = new ChatMessage();
                   chatMessage.senderId = senderId;
                   chatMessage.receiverId = receiverId;
                   if(preferenceManager.getString(Constants.KEY_USER_ID).equals(senderId)) {
                       chatMessage.conversionImage = documentChange.getDocument().getString(Constants.KEY_RECEIVER_IMAGE);
                       chatMessage.conversionName = documentChange.getDocument().getString(Constants.KEY_RECEIVER_NAME);
                       chatMessage.conversionId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                   } else {
                       chatMessage.conversionImage = documentChange.getDocument().getString(Constants.KEY_SENDER_IMAGE);
                       chatMessage.conversionName = documentChange.getDocument().getString(Constants.KEY_SENDER_NAME);
                       chatMessage.conversionId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                   }
                   chatMessage.message = documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE);
                   chatMessage.dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                   conversations.add(chatMessage);
               } else if (documentChange.getType() == DocumentChange.Type.MODIFIED) {
                   for(int i = 0; i< conversations.size(); i++) {
                       String senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                       String receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                       if(conversations.get(i).senderId.equals(senderId) && conversations.get(i).receiverId.equals(receiverId)) {
                           conversations.get(i).message = documentChange.getDocument().getString((Constants.KEY_LAST_MESSAGE));
                           conversations.get(i).dateObject = documentChange.getDocument().getDate((Constants.KEY_TIMESTAMP));
                           break;
                       }
                   }
               }
           }
           Collections.sort(conversations, (obj1, obj2) -> obj1.dateObject.compareTo(obj2.dateObject));
           conversationsAdapter.notifyDataSetChanged();
           recyclerView.smoothScrollToPosition(0);
           recyclerView.setVisibility(View.VISIBLE);
           progressBar.setVisibility(View.GONE);
       }
    };

    private void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    private void updateToken (String token) {
        preferenceManager.putString(Constants.KEY_FCM_TOKEN,token);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLECTION_USERS).document(
                        preferenceManager.getString(Constants.KEY_USER_ID)
                );
        documentReference.update(Constants.KEY_FCM_TOKEN, token)
//                .addOnSuccessListener(unused -> showToast("Token updated successfully"))
                .addOnFailureListener(e -> showToast("Unable to update token"));
    }


    @Override
    public void onConversionClicked(chatUser user) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);
    }
}