package com.example.llamachatapp;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView rvMessages;
    private EditText etMessage;
    private ImageButton btnSend;
    private ChatAdapter chatAdapter;
    private ArrayList<String> messages;
    private ChatApi chatApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        rvMessages = findViewById(R.id.rvMessages);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);

        messages = new ArrayList<>();
        chatAdapter = new ChatAdapter(messages);
        rvMessages.setLayoutManager(new LinearLayoutManager(this));
        rvMessages.setAdapter(chatAdapter);


        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:5001/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        chatApi = retrofit.create(ChatApi.class);
        String username = getIntent().getStringExtra("username");

        btnSend.setOnClickListener(v -> {
            String userMessage = etMessage.getText().toString().trim();
            if (!userMessage.isEmpty()) {

                messages.add("You: " + userMessage);
                chatAdapter.notifyItemInserted(messages.size() - 1);
                etMessage.setText("");

                ChatRequest request = new ChatRequest(username, userMessage);
                chatApi.sendMessage(request).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            try {
                                String raw = response.body().string();
                                String botReply;

                                try {
                                    JSONObject json = new JSONObject(raw);
                                    if (json.has("reply")) {
                                        botReply = json.getString("reply");
                                    } else if (json.has("response")) {
                                        botReply = json.getString("response");
                                    } else {
                                        botReply = raw;
                                    }
                                } catch (Exception e) {
                                    botReply = raw; // fallback if not JSON
                                }


                                messages.add("Bot: " + botReply);
                                chatAdapter.notifyItemInserted(messages.size() - 1);
                                rvMessages.scrollToPosition(messages.size() - 1);

                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(ChatActivity.this, "Failed to read response", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ChatActivity.this, "Bot didn't reply", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(ChatActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
