package com.example.shweta.twilioprogrammablechat;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.example.shweta.twilioprogrammablechat.databinding.ActivityChatChannelTwilioBinding;
import com.example.shweta.twilioprogrammablechat.twilio.ChannelManager;
import com.example.shweta.twilioprogrammablechat.twilio.ChatClientManager;
import com.example.shweta.twilioprogrammablechat.twilio.TaskCompletionListener;
import com.twilio.chat.Channel;

public class ChatChannelTwilioActivity extends AppCompatActivity {

    private ActivityChatChannelTwilioBinding binding;
    private Channel mChannel;
    private String mChannelSid;
    private ChannelManager mChannelManager;
    private ChatClientManager mChatClientManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat_channel_twilio);
        binding.setGroupChatChannel(this);
    }

    private void checkTwilioClient() {
        mChatClientManager = MyApplication.Companion.getInstance().getClientManager();
        if (mChatClientManager.getChatClient() == null)
            initializeClient();
        else
            init();

    }

    private void initializeClient() {
        mChatClientManager.connectClient(new TaskCompletionListener<Void, String>() {
            @Override
            public void onSuccess(@NonNull Void aVoid) {
                init();
            }

            @Override
            public void onError(String s) {
                // Utils.showSnackBar(binding., getString(R.string.client_connection_error))
            }
        });
    }

    private void init() {

    }

}
