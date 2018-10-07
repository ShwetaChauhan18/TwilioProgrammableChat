package com.example.shweta.twilioprogrammablechat

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.shweta.twilioprogrammablechat.common.BaseActivityMVVM
import com.example.shweta.twilioprogrammablechat.common.Utils
import com.example.shweta.twilioprogrammablechat.databinding.ActivityChannelListBinding
import com.example.shweta.twilioprogrammablechat.twilio.*
import com.twilio.chat.Channel
import com.twilio.chat.ChatClient
import com.twilio.chat.ChatClientListener
import com.twilio.chat.ErrorInfo

class ChannelListActivity : BaseActivityMVVM<ActivityChannelListBinding>(), ChatClientListener {

    private lateinit var mChatClientManager: ChatClientManager
    private lateinit var mChannelManager: ChannelManager
    private lateinit var channels: List<Channel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_channel_list)
    }

    override fun initialization() {

    }

    override fun onResume() {
        super.onResume()
        mChannelManager = ChannelManager.getInstance()
    }

    private fun checkTwilioClient() {
        mChatClientManager = MyApplication.instance.clientManager
        if (mChatClientManager.getChatClient() == null)
            initializeClient()
        else {
            populateChannels()
            //setChannels()
        }
    }

    private fun initializeClient() {
        mChatClientManager.connectClient(object : TaskCompletionListener<Void, String> {
            override fun onSuccess(mVoid: Void?) {
                populateChannels()
                //setChannels()
            }

            override fun onError(errorMessage: String) {
                //Utils.showSnackBar(binding.rootView, getString(R.string.client_connection_error))
            }
        })
    }

    private fun populateChannels() {
        mChannelManager.setChannelListener(this, 0)
        mChannelManager.populateChannels("Message", object : LoadChannelListener {
            override fun onChannelsFinishedLoading(channels: List<Channel>) {

                /*mMessageTwilioAdapter.setChannels(channels as ArrayList<Channel>, APIConstant.ChannelType.chat)
                //binding.swipeLayoutGroupChannelList.isRefreshing = false
                mChannelManager.removeChannelHandler("Message")
                Utils.setLog("Remove Message identifier")

                binding.txtNoRecordFound.visibility = if (channels.isNotEmpty()) View.GONE else View.VISIBLE*/
            }
        })

        /*if (binding.swipeLayoutGroupChannelList.isRefreshing) {
            binding.swipeLayoutGroupChannelList.isRefreshing = false
        }*/

        /*this@GroupsStudyFragment.mChannelManager
                .joinOrCreateGeneralChannelWithCompletion(object:StatusListener(){
                    override fun onSuccess() {
                        runOnUiThread(object : Runnable {
                            override fun run() {
                                mChannelAdapter.notifyDataSetChanged()
                            }
                        })
                    }

                    override fun onError(errorInfo: ErrorInfo?) {
                        super.onError(errorInfo)
                    }

                })*/

    }

    private fun refreshChannels() {
       /* mChannelManager.populateChannels("Message", object : LoadChannelListener {
            override fun onChannelsFinishedLoading(channels: List<Channel>) {
                activity.runOnUiThread(object : Runnable {
                    override fun run() {
                        mMessageTwilioAdapter.setChannels(channels as ArrayList<Channel>, APIConstant.ChannelType.chat)
                        mChannelManager.removeChannelHandler("Message")
                        //binding.swipeLayoutGroupChannelList.isRefreshing = false

                        binding.txtNoRecordFound.visibility = if (mMessageTwilioAdapter.itemCount > 0) View.GONE else View.VISIBLE
                    }
                })

                if (binding.swipeLayoutGroupChannelList.isRefreshing) {
                    binding.swipeLayoutGroupChannelList.isRefreshing = false
                }*/

                /*this@GroupsStudyFragment.mChannelManager
                        .joinOrCreateGeneralChannelWithCompletion(object:StatusListener(){
                            override fun onSuccess() {
                                runOnUiThread(object : Runnable {
                                    override fun run() {
                                        mChannelAdapter.notifyDataSetChanged()
                                    }
                                })
                            }

                            override fun onError(errorInfo: ErrorInfo?) {
                                super.onError(errorInfo)
                            }

                        })*/
           /* }

        })*/
    }

    private fun setUpListeners() {
        /*binding.swipeLayoutGroupChannelList.setOnRefreshListener {
            if (mChatClientManager.getChatClient() != null) {
                if (mSearchText == "") {
                    binding.swipeLayoutGroupChannelList.isRefreshing = true
                    if (mChatClientManager.getChatClient() == null)
                        initializeClient()
                    else
                        refreshChannels()
                } else {
                    binding.swipeLayoutGroupChannelList.isRefreshing = false
                }
            }else{
                binding.swipeLayoutGroupChannelList.isRefreshing = false
            }
        }*/
    }


    //=============================================================
    // ChannelListener
    //=============================================================

    override fun onChannelDeleted(channel: Channel?) {
        Utils.setLog("Channel Deleted")
        Handler().postDelayed({
            //refreshChannels()
        }, 500)
    }

    override fun onInvitedToChannelNotification(p0: String?) {

    }

    override fun onClientSynchronization(p0: ChatClient.SynchronizationStatus?) {

    }

    override fun onNotificationSubscribed() {

    }

    override fun onUserSubscribed(user: com.twilio.chat.User?) {

    }

    override fun onChannelUpdated(channel: Channel?, updateReason: Channel.UpdateReason?) {
        Utils.setLog("Channel updated")
        //refreshChannels()
        refreshChannels()
    }

    override fun onRemovedFromChannelNotification(s: String?) {

    }

    override fun onNotificationFailed(errorInfo: ErrorInfo?) {

    }

    override fun onChannelJoined(channel: Channel?) {
        Utils.setLog("Channel joined")
    }

    override fun onChannelAdded(channel: Channel?) {
        Utils.setLog("Channel added")
        //refreshChannels()
        //mMessageTwilioAdapter.addChannel(channel!!)
    }

    override fun onChannelSynchronizationChange(channel: Channel?) {

    }

    override fun onUserUnsubscribed(user: com.twilio.chat.User?) {

    }

    override fun onAddedToChannelNotification(s: String?) {

    }

    override fun onChannelInvited(channel: Channel?) {

    }

    override fun onNewMessageNotification(s: String?, s1: String?, long: Long) {
        Utils.setLog("NOTIFICATION $s $s1")
        refreshChannels()
    }

    override fun onConnectionStateChange(connectionState: ChatClient.ConnectionState?) {

    }

    override fun onError(errorInfo: ErrorInfo?) {

    }

    override fun onUserUpdated(user: com.twilio.chat.User?, updateReason: com.twilio.chat.User.UpdateReason?) {

    }

}
