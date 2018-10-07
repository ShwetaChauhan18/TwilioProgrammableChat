package com.example.shweta.twilioprogrammablechat

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.shweta.twilioprogrammablechat.common.BaseActivityMVVM
import com.example.shweta.twilioprogrammablechat.common.Utils
import com.example.shweta.twilioprogrammablechat.databinding.ActivityLoginBinding
import com.example.shweta.twilioprogrammablechat.twilio.ChatClientManager
import com.example.shweta.twilioprogrammablechat.twilio.MyApplication
import com.example.shweta.twilioprogrammablechat.twilio.TaskCompletionListener

class LoginActivity : BaseActivityMVVM<ActivityLoginBinding>() {

    private lateinit var mClientManager: ChatClientManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    override fun initialization() {
        mClientManager = MyApplication.instance.clientManager
    }

    private fun initializeTwilioChatClient() {
        mClientManager.connectClient(object:TaskCompletionListener<Void,String>{
            override fun onSuccess(t: Void?) {

            }

            override fun onError(errorMessage: String) {
                //Utils.showSnackBar(binding.rootView, "Error in client initialization: $errorMessage")
            }

        })
    }
}
