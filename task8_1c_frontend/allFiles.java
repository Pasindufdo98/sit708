
// ChatActivity.java

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


// ChatAdapter.java

package com.example.llamachatapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<String> messages;
    private static final int VIEW_TYPE_USER = 0;
    private static final int VIEW_TYPE_BOT = 1;

    public ChatAdapter(List<String> messages) {
        this.messages = messages;
    }

    @Override
    public int getItemViewType(int position) {
        if (messages.get(position).startsWith("You: ")) {
            return VIEW_TYPE_USER;
        } else {
            return VIEW_TYPE_BOT;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_USER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_message, parent, false);
            return new UserMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bot_message, parent, false);
            return new BotMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String message = messages.get(position);

        if (holder instanceof UserMessageViewHolder) {
            ((UserMessageViewHolder) holder).txtUser.setText(message.replace("You: ", ""));
        } else {
            ((BotMessageViewHolder) holder).txtBot.setText(message.replace("Bot: ", ""));
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class UserMessageViewHolder extends RecyclerView.ViewHolder {
        TextView txtUser;

        public UserMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUser = itemView.findViewById(R.id.txtUserMessage);
        }
    }

    static class BotMessageViewHolder extends RecyclerView.ViewHolder {
        TextView txtBot;

        public BotMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            txtBot = itemView.findViewById(R.id.txtBotMessage);
        }
    }
}

// ChatApi.java

package com.example.llamachatapp;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ChatApi {
    @POST("/chat")
    Call<ResponseBody> sendMessage(@Body ChatRequest request);
}



// ChatRequest.java

package com.example.llamachatapp;

public class ChatRequest {
    private String user;
    private String userMessage;

    public ChatRequest(String user, String userMessage) {
        this.user = user;
        this.userMessage = userMessage;
    }

    public String getUser() {
        return user;
    }

    public String getUserMessage() {
        return userMessage;
    }
}

// ChatResponse.java

package com.example.llamachatapp;

public class ChatResponse {
    private String reply;

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }
}

// LoginActivity.java

package com.example.llamachatapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        Button btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            if (!username.isEmpty()) {
                Intent intent = new Intent(LoginActivity.this, ChatActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Please enter a username", Toast.LENGTH_SHORT).show();
            }
        });
    }
}