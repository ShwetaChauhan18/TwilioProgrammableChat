package com.example.shweta.twilioprogrammablechat.twilio

import android.content.Context
import com.twilio.chat.CallbackListener
import com.twilio.chat.ChatClient
import com.twilio.chat.ErrorInfo

/**
 * Created by Shweta on 30-09-2018.
 */
class ChatClientBuilder : CallbackListener<ChatClient> {

    private val context: Context
    private var buildListener: TaskCompletionListener<ChatClient, String>? = null

    constructor(context: Context) {
        this.context = context
    }

    fun build(token: String, listener: TaskCompletionListener<ChatClient, String>) {
        val props = ChatClient.Properties.Builder().setRegion("us1")
                .createProperties()

        this.buildListener = listener
        ChatClient.create(context.applicationContext, token, props, this)
    }

    override fun onSuccess(chatClient: ChatClient?) {
        this.buildListener?.onSuccess(chatClient)
    }

    override fun onError(errorInfo: ErrorInfo?) {
        this.buildListener!!.onError(errorInfo!!.message)
    }
}