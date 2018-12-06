package com.example.shweta.twilioprogrammablechat

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.example.shweta.twilioprogrammablechat.adapter.ChannelAdapter
import com.example.shweta.twilioprogrammablechat.common.BaseActivityMVVM
import com.example.shweta.twilioprogrammablechat.common.PermissionUtil
import com.example.shweta.twilioprogrammablechat.common.Utils
import com.example.shweta.twilioprogrammablechat.databinding.ActivityChannelListBinding
import com.example.shweta.twilioprogrammablechat.twilio.ChannelManager
import com.example.shweta.twilioprogrammablechat.twilio.ChatClientManager
import com.example.shweta.twilioprogrammablechat.twilio.LoadChannelListener
import com.example.shweta.twilioprogrammablechat.twilio.TaskCompletionListener
import com.twilio.chat.Channel
import com.twilio.chat.ChatClient
import com.twilio.chat.ChatClientListener
import com.twilio.chat.ErrorInfo
import com.twilio.chat.StatusListener

class ChannelListActivity : BaseActivityMVVM<ActivityChannelListBinding>(), ChatClientListener {

    private lateinit var mChatClientManager: ChatClientManager
    private lateinit var mChannelManager: ChannelManager
    private lateinit var channelAdapter: ChannelAdapter
    private lateinit var channels: List<Channel>

    companion object {
        const val EXTRA_NEW_CHANNEL_URL = "EXTRA_NEW_CHANNEL_URL"
        const val EXTRA_IS_GROUP_CHAT = "EXTRA_IS_GROUP_CHAT"
        const val REQ_INTENT_CHAT_DETAIL_STUDY = 0x41
    }

    override fun onDestroy() {
        super.onDestroy()
        Handler().post {
            mChatClientManager.shutdown()
            MyApplication.instance.clientManager.setChatClient(null)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAndBindContentView(savedInstanceState, R.layout.activity_channel_list)
    }

    override fun initialization() {
        val permissionUtil = PermissionUtil()
        if (permissionUtil.checkMarshMellowPermission()) {
            if (!(permissionUtil.verifyPermissions(this, permissionUtil.getStoragePermissions()))) {
                ActivityCompat.requestPermissions(this, permissionUtil.getStoragePermissions(), PermissionUtil.INTENT_REQUEST_STORAGE_PERMISSION)
            }
        }
        setToolbar()
        setUpListeners()
        setRecyclerView()
        setUpChannelListAdapter()
    }

    private fun setToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = null

        binding.toolbarTitle.text = "Message"
    }

    override fun onResume() {
        super.onResume()
        mChannelManager = ChannelManager.getInstance()
        checkTwilioClient()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PermissionUtil.INTENT_REQUEST_STORAGE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                // permission was not granted
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0]))
                    PermissionUtil.showPermissionDialog(this, getString(R.string.permission_from_setting_storage))
            }
        }
    }

    private fun setRecyclerView() {
        // itemClick = this
        channels = ArrayList()
        channelAdapter = ChannelAdapter(this, channels as ArrayList<Channel>)
        binding.recyclerGroupChannelList.layoutManager = LinearLayoutManager(this)
        binding.recyclerGroupChannelList.adapter = channelAdapter

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

    // Sets up channel list adapter
    private fun setUpChannelListAdapter() {
        channelAdapter.setOnItemClickListener(object : ChannelAdapter.OnItemClickListener {
            override fun onItemClick(channel: Channel, chatName: String) {
                //setChannel()
                joinChannel(channel)
                //deleteCurrentChannel(channel)
            }
        })

        channelAdapter.setOnItemLongClickListener(object : ChannelAdapter.OnItemLongClickListener {
            override fun onItemLongClick(channel: Channel) {
                showChannelOptionsDialog(channel)
            }
        })
    }

    private fun joinChannel(selectedChannel: Channel) {
        runOnUiThread(object : Runnable {
            override fun run() {
                val intent = Intent(this@ChannelListActivity, ChatChannelTwilioActivity::class.java)
                intent.putExtra(EXTRA_NEW_CHANNEL_URL, selectedChannel)
                intent.putExtra(EXTRA_IS_GROUP_CHAT, false)
                startActivityForResult(intent, REQ_INTENT_CHAT_DETAIL_STUDY)
            }
        })
    }

    private fun populateChannels() {
        mChannelManager.setChannelListener(this, 0)
        mChannelManager.populateChannels(object : LoadChannelListener {
            override fun onChannelsFinishedLoading(channels: List<Channel>) {
                channelAdapter.setChannels(channels as ArrayList<Channel>)
                binding.swipeLayoutGroupChannelList.isRefreshing = false
                Utils.setLog("Remove Message identifier")

                binding.txtNoRecordFound.visibility = if (channelAdapter.itemCount > 0) View.GONE else View.VISIBLE
            }
        })
        if (binding.swipeLayoutGroupChannelList.isRefreshing) {
            binding.swipeLayoutGroupChannelList.isRefreshing = false
        }
    }

    private fun refreshChannels() {
        mChannelManager.populateChannels(object : LoadChannelListener {
            override fun onChannelsFinishedLoading(channels: List<Channel>) {
                runOnUiThread(object : Runnable {
                    override fun run() {
                        channelAdapter.setChannels(channels as ArrayList<Channel>)
                        binding.swipeLayoutGroupChannelList.isRefreshing = false

                        binding.txtNoRecordFound.visibility = if (channelAdapter.itemCount > 0) View.GONE else View.VISIBLE
                    }
                })
            }
        })

        if (binding.swipeLayoutGroupChannelList.isRefreshing) {
            binding.swipeLayoutGroupChannelList.isRefreshing = false

        }
    }

    private fun setUpListeners() {
        binding.swipeLayoutGroupChannelList.setOnRefreshListener {
            if (mChatClientManager.getChatClient() != null) {
                binding.swipeLayoutGroupChannelList.isRefreshing = true
                if (mChatClientManager.getChatClient() == null)
                    initializeClient()
                else
                    refreshChannels()

            } else {
                binding.swipeLayoutGroupChannelList.isRefreshing = false
            }
        }
    }

    private fun showChannelOptionsDialog(channel: Channel) {
        val options: Array<String> = arrayOf(getString(R.string.leave_channel_alert)/*, getString(R.string.push_on)*/)

        val builder = AlertDialog.Builder(this)

        builder.setTitle("Alert")
                .setMessage(getString(R.string.leave_channel) + " " + channel.friendlyName + "?")
                .setPositiveButton(getString(R.string.btn_yes)) { dialog, which -> deleteCurrentChannel(channel) }
                .setNegativeButton(getString(R.string.btn_cancel), null)
                .create().show()
        // builder.create().show()
    }

    private fun deleteCurrentChannel(selectedChannel: Channel) {
        //val currentChannel = chatFragment.getCurrentChannel()
        showProgressDialog(this)

        mChannelManager.deleteChannelWithHandler(selectedChannel, object : StatusListener() {
            override fun onSuccess() {
                hideProgressDialog()
            }

            override fun onError(errorInfo: ErrorInfo?) {
                //showAlertWithMessage(getStringResource(R.string.message_deletion_forbidden))
            }
        })

    }

    // delete channel thread from one side
    private fun leaveGroup(channel: Channel) {

        channel.leave(object : StatusListener() {
            override fun onSuccess() {
                hideProgressDialog()
                refreshChannels()
            }
        })
    }

    //=============================================================
    // ChannelListener
    //=============================================================

    override fun onChannelDeleted(channel: Channel?) {
        Utils.setLog("Channel Deleted")
        channelAdapter.deleteChannel(channel!!)
        binding.txtNoRecordFound.visibility = if (channelAdapter.itemCount > 0) View.GONE else View.VISIBLE
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
        channelAdapter.addChannel(channel!!)
        refreshChannels()
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
