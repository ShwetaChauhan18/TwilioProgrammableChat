package com.example.shweta.twilioprogrammablechat.twilio

import android.content.Context
import com.example.shweta.twilioprogrammablechat.common.Utils
import com.twilio.accessmanager.AccessManager
import com.twilio.chat.ChatClient
import com.twilio.chat.ChatClientListener

/**
 * Created by Shweta on 30-09-2018.
 */
class ChatClientManager : AccessManager.Listener, AccessManager.TokenUpdateListener {

    private var mContext: Context
    private lateinit var mChatClient: ChatClient
    private lateinit var mAccessManager: AccessManager
    private var mAccessTokenFetcher: AccessTokenFetcher
    private var mChatClientBuilder: ChatClientBuilder

    constructor(context: Context) {
        this.mContext = context
        this.mAccessTokenFetcher = AccessTokenFetcher(this.mContext)
        this.mChatClientBuilder = ChatClientBuilder(this.mContext)
    }

    fun setClientListener(listener: ChatClientListener) {
        if (this.mChatClient != null) {
            this.mChatClient.setListener(listener)
        }
    }

    fun removeClientListener(){
        if(this.mChatClient!=null){
            this.mChatClient?.removeListener()
        }
    }

    fun getChatClient(): ChatClient? {
        if (this.mChatClient != null)
            return this.mChatClient
        else
            return null
    }

    fun setChatClient(client: ChatClient?) {
        if (client != null)
            this.mChatClient = client
    }

    fun connectClient(listener: TaskCompletionListener<Void, String>) {
        ChatClient.setLogLevel(android.util.Log.DEBUG)

        mAccessTokenFetcher.fetch(object : TaskCompletionListener<String, String> {
            override fun onSuccess(token: String?) {
                createAccessManager(token!!)
                buildClient(token, listener)
            }

            override fun onError(message: String) {
                listener.onError(message)
            }
        })
    }

    private fun buildClient(token: String, listener: TaskCompletionListener<Void, String>) {
        mChatClientBuilder.build(token, object : TaskCompletionListener<ChatClient, String> {
            override fun onSuccess(chatClient: ChatClient?) {
                this@ChatClientManager.mChatClient = chatClient!!
                listener.onSuccess(null)
            }

            override fun onError(message: String) {
                listener.onError(message)
            }

        })
    }

    private fun createAccessManager(token: String) {
        this.mAccessManager = AccessManager(token, this)
        mAccessManager.addTokenUpdateListener(this)
    }

    override fun onTokenExpired(accessManager: AccessManager?) {
        Utils.setLog("Token expired....")
    }

    override fun onTokenWillExpire(accessManager: AccessManager?) {

    }

    override fun onError(accessManager: AccessManager?, s: String?) {
        Utils.setLog("Error token error: $s")
    }

    override fun onTokenUpdated(s: String?) {
        Utils.setLog("Error token updated..$s")
    }

    fun shutdown() {
        /*if (mChatClient != null) {
            mChatClient.shutdown()
        }*/
    }
}