package com.example.shweta.twilioprogrammablechat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.example.shweta.twilioprogrammablechat.common.BaseActivityMVVM
import com.example.shweta.twilioprogrammablechat.common.SessionManager
import com.example.shweta.twilioprogrammablechat.databinding.ActivityLoginBinding
import com.example.shweta.twilioprogrammablechat.twilio.ChatClientManager
import com.example.shweta.twilioprogrammablechat.twilio.TaskCompletionListener
import kotlinx.android.synthetic.main.toolbar_activity.toolbar
import kotlinx.android.synthetic.main.toolbar_activity.toolbar_title

class LoginActivity : BaseActivityMVVM<ActivityLoginBinding>() {

    private lateinit var mClientManager: ChatClientManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAndBindContentView(savedInstanceState, R.layout.activity_login)
        binding.login = this

        mClientManager = MyApplication.instance.clientManager
        setToolbar()

    }

    override fun initialization() {
        mClientManager = MyApplication.instance.clientManager
    }

    private fun setToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = null

        toolbar_title.text = "Login"
    }

    fun onClickLogin() {
        val idChosen = binding.edtUserName.text.toString()

        SessionManager.instance.createLoginSession(idChosen)
        initializeTwilioChatClient()

    }

    private fun initializeTwilioChatClient() {
        mClientManager.connectClient(object : TaskCompletionListener<Void, String> {
            override fun onSuccess(t: Void?) {
                Log.d("Success",".....Sucessssssss")
                showChannelChatActivity()
                //SharedPrefsUtils.setBooleanPreference(this@LoginActivity, SharedPrefsUtils.PREFERENCE_KEY_TWILIO_CLIENT_CONNECTED, true)
            }

            override fun onError(errorMessage: String) {
                Log.e("ClientError", "Error in initialization: $errorMessage")
            }
        })
    }

    private fun showChannelChatActivity() {
        val launchIntent = Intent()
        launchIntent.setClass(applicationContext, ChannelListActivity::class.java)
        startActivity(launchIntent)
        finish()
    }
}
