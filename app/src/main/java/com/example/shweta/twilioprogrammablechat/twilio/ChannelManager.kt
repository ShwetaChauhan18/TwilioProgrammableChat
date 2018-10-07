package com.example.shweta.twilioprogrammablechat.twilio

import android.os.Handler
import android.os.Looper
import com.twilio.chat.*
import org.json.JSONObject
import java.util.*

/**
 * Created by Shweta on 07-10-2018.
 */
class ChannelManager : ChatClientListener {

    companion object {
        private var channelManager = ChannelManager()

        fun getInstance(): ChannelManager {
            return channelManager
        }
    }


    private lateinit var mChannelsObject: Channels
    private var mChatClientManager: ChatClientManager? = null
    private lateinit var generalChannel: Channel
    private var defaultChannelName: String
    private var defaultChannelUniqueName: String
    /*private var listenerMessage: ChatClientListener?
    private var listenerStudy: ChatClientListener?
    private var listenerPlay: ChatClientListener?
    private var listenerCar: ChatClientListener?*/
    private var listener: ChatClientListener?
    private val handler: Handler
    private var channels: ArrayList<Channel>
    private var isRefreshingChannels: Boolean = false
    private var channelExractor: ChannelExtractor
    internal val mChannelHandlers: HashMap<String, LoadChannelListener> = HashMap()

    constructor() {
        this.mChatClientManager = MyApplication.instance.clientManager
        this.channelExractor = ChannelExtractor()
        this.listener = this
        channels = ArrayList()
        handler = setupListenerHandler()
        defaultChannelName = "General Channel"
        defaultChannelUniqueName = "general"
    }

    fun getChannels(): List<Channel> {
        return this.channels
    }

    fun getDefaultChannelName(): String {
        return this.defaultChannelName
    }

    fun populateChannels(identifier: String?, listener: LoadChannelListener) {
        if (this.mChatClientManager == null || mChannelHandlers.containsKey(identifier)) {
            return
        }
        this.isRefreshingChannels = true
        addChannelHandler(identifier, listener)

        //handler.post {
        mChannelsObject = mChatClientManager?.getChatClient()!!.channels

        mChannelsObject.getUserChannelsList(object : CallbackListener<Paginator<ChannelDescriptor>>() {
            override fun onSuccess(channelDescriptorPaginator: Paginator<ChannelDescriptor>) {
                //this@ChannelManager.isRefreshingChannels = false
                extractChannelsFromPaginatorAndPopulate(channelDescriptorPaginator, mChannelHandlers.get(identifier)!!)
            }
        })
        //}
    }

    fun getChannelWithUniqueName(channelSidOrUniqueName: String, statusListener: StatusListenerCustom) {
        if (this.mChatClientManager == null /*|| this.isRefreshingChannels*/) {
            return
        }
        //this.isRefreshingChannels = true
        handler.post {
            mChannelsObject = mChatClientManager?.getChatClient()!!.channels

            mChannelsObject.getChannel(channelSidOrUniqueName, object : CallbackListener<Channel>() {
                override fun onSuccess(channel: Channel?) {
                    statusListener.onSuccess(channel!!)
                }

                override fun onError(errorInfo: ErrorInfo?) {
                    super.onError(errorInfo)
                    statusListener.onError(errorInfo!!)
                }

            })
        }
    }


    private fun extractChannelsFromPaginatorAndPopulate(channelsPaginator: Paginator<ChannelDescriptor>,
                                                        listener: LoadChannelListener) {
        (this@ChannelManager.channels as ArrayList<Channel>).clear()
        channelExractor.extractAndSortFromChannelDescriptor(channelsPaginator,
                object : TaskCompletionListener<List<Channel>, String> {
                    override fun onSuccess(channels: List<Channel>?) {
                        (this@ChannelManager.channels as ArrayList<Channel>).addAll(channels as ArrayList<Channel>)
                        Collections.sort(this@ChannelManager.channels, CustomChannelComparator())
                        this@ChannelManager.isRefreshingChannels = false
                        mChatClientManager?.setClientListener(this@ChannelManager)
                        listener.onChannelsFinishedLoading(this@ChannelManager.channels)
                    }

                    override fun onError(errorText: String) {
                        println("Error populating channels: $errorText")
                    }
                })
    }

    fun createChannelWithName(name: String, uniqueName: String, mJsonobject: JSONObject, handler: StatusListener) {
        this.mChannelsObject
                .channelBuilder()
                .withFriendlyName(name)
                .withType(Channel.ChannelType.PRIVATE)
                .withUniqueName(uniqueName)
                .withAttributes(mJsonobject)
                .build(object : CallbackListener<Channel>() {
                    override fun onSuccess(newChannel: Channel) {
                        handler.onSuccess()
                    }

                    override fun onError(errorInfo: ErrorInfo?) {
                        handler.onError(errorInfo)
                    }
                })
    }

    fun createChannelWithNameUnique(name: String, uniqueName: String, mJsonobject: JSONObject, handler: StatusListenerCustom) {
        this.mChannelsObject
                .channelBuilder()
                .withFriendlyName(name)
                .withType(Channel.ChannelType.PRIVATE)
                .withUniqueName(uniqueName)
                .withAttributes(mJsonobject)
                .build(object : CallbackListener<Channel>() {
                    override fun onSuccess(newChannel: Channel) {
                        handler.onSuccess(newChannel)
                    }

                    override fun onError(errorInfo: ErrorInfo?) {
                        handler.onError(errorInfo!!)
                    }
                })
    }

    fun joinOrCreateGeneralChannelWithCompletion(listener: StatusListener) {
        mChannelsObject.getChannel(defaultChannelUniqueName, object : CallbackListener<Channel>() {
            override fun onSuccess(channel: Channel?) {
                this@ChannelManager.generalChannel = channel!!
                if (channel != null) {
                    joinGeneralChannelWithCompletion(listener)
                } else {
                    createGeneralChannelWithCompletion(listener)
                }
            }
        })
    }

    private fun joinGeneralChannelWithCompletion(listener: StatusListener) {
        this.generalChannel.join(object : StatusListener() {
            override fun onSuccess() {
                listener.onSuccess()
            }

            override fun onError(errorInfo: ErrorInfo?) {
                listener.onError(errorInfo)
            }
        })
    }

    private fun createGeneralChannelWithCompletion(listener: StatusListener) {
        this.mChannelsObject
                .channelBuilder()
                .withFriendlyName(defaultChannelName)
                .withUniqueName(defaultChannelUniqueName)
                .withType(Channel.ChannelType.PUBLIC)
                .build(object : CallbackListener<Channel>() {
                    override fun onSuccess(channel: Channel) {
                        this@ChannelManager.generalChannel = channel
                        this@ChannelManager.channels.add(channel)
                        joinGeneralChannelWithCompletion(listener)
                    }

                    override fun onError(errorInfo: ErrorInfo?) {
                        listener.onError(errorInfo)
                    }
                })
    }

    fun deleteChannelWithHandler(channel: Channel, handler: StatusListener) {
        channel.destroy(handler)
    }

    fun addChannelHandler(identifier: String?, handler: LoadChannelListener?) {
        if (identifier != null && identifier.isNotEmpty() && handler != null) {
            mChannelHandlers.put(identifier, handler)
            //this.listener = handler
        }
    }

    fun removeChannelHandler(identifier: String?) {
        if (mChannelHandlers.containsKey(identifier)) mChannelHandlers.remove(identifier) as LoadChannelListener
    }

    fun setChannelListener(listener: ChatClientListener, flag: Int) {
        this.listener = listener
    }

    fun removeChannelListener(flag: Int) {
        mChatClientManager?.removeClientListener()
    }

    //=============================================================
    // ChannelListener
    //=============================================================

    override fun onChannelDeleted(channel: Channel?) {
        if (listener != null) {
            this.listener?.onChannelDeleted(channel)
        }
        /*if(this.listenerMessage!=null)
            this.listenerMessage?.onChannelDeleted(channel)
        if(this.listenerStudy!=null)
            this.listenerStudy?.onChannelDeleted(channel)
        if(this.listenerPlay!=null)
            this.listenerPlay?.onChannelDeleted(channel)
        if(this.listenerCar!=null)
            this.listenerCar?.onChannelDeleted(channel)*/
    }

    override fun onInvitedToChannelNotification(s: String?) {

    }

    override fun onClientSynchronization(synchronizationStatus: ChatClient.SynchronizationStatus?) {

    }

    override fun onNotificationSubscribed() {

    }

    override fun onUserSubscribed(user: User?) {

    }

    override fun onChannelUpdated(channel: Channel?, updateReason: Channel.UpdateReason?) {
        if (listener != null) {
            listener?.onChannelUpdated(channel, updateReason)
        }
        /*if(this.listenerMessage!=null)
            this.listenerMessage?.onChannelUpdated(channel, updateReason)
        if(this.listenerStudy!=null)
            this.listenerStudy?.onChannelUpdated(channel, updateReason)
        if(this.listenerPlay!=null)
            this.listenerPlay?.onChannelUpdated(channel, updateReason)
        if(this.listenerCar!=null)
            this.listenerCar?.onChannelUpdated(channel, updateReason)*/
    }

    override fun onRemovedFromChannelNotification(s: String?) {

    }

    override fun onNotificationFailed(errorInfo: ErrorInfo?) {

    }

    override fun onChannelJoined(channel: Channel?) {

    }

    override fun onChannelAdded(channel: Channel?) {
        if (listener != null) {
            listener?.onChannelAdded(channel)
        }
        /*if(this.listenerMessage!=null)
            this.listenerMessage?.onChannelAdded(channel)
        if(this.listenerStudy!=null)
            this.listenerStudy?.onChannelAdded(channel)
        if(this.listenerPlay!=null)
            this.listenerPlay?.onChannelAdded(channel)
        if(this.listenerCar!=null)
            this.listenerCar?.onChannelAdded(channel)*/
    }

    override fun onChannelSynchronizationChange(channel: Channel?) {
        if (listener != null) {
            listener?.onChannelSynchronizationChange(channel)
        }
        /*if(this.listenerMessage!=null)
            this.listenerMessage?.onChannelSynchronizationChange(channel)
        if(this.listenerStudy!=null)
            this.listenerStudy?.onChannelSynchronizationChange(channel)
        if(this.listenerPlay!=null)
            this.listenerPlay?.onChannelSynchronizationChange(channel)
        if(this.listenerCar!=null)
            this.listenerCar?.onChannelSynchronizationChange(channel)*/
    }

    override fun onUserUnsubscribed(user: User?) {

    }

    override fun onAddedToChannelNotification(s: String?) {

    }

    override fun onChannelInvited(channel: Channel?) {

    }

    override fun onNewMessageNotification(s: String?, s1: String?, long: Long) {
        if (listener != null) {
            listener?.onNewMessageNotification(s, s1, long)
        }
        /*if(this.listenerMessage!=null)
            this.listenerMessage?.onNewMessageNotification(s, s1, long)
        if(this.listenerStudy!=null)
            this.listenerStudy?.onNewMessageNotification(s, s1, long)
        if(this.listenerPlay!=null)
            this.listenerPlay?.onNewMessageNotification(s, s1, long)
        if(this.listenerCar!=null)
            this.listenerCar?.onNewMessageNotification(s, s1, long)*/
    }

    override fun onConnectionStateChange(connectionState: ChatClient.ConnectionState?) {

    }

    override fun onError(errorInfo: ErrorInfo?) {
        if (listener != null) {
            listener?.onError(errorInfo)
        }
        /*if(this.listenerMessage!=null)
            this.listenerMessage?.onError(errorInfo)
        if(this.listenerStudy!=null)
            this.listenerStudy?.onError(errorInfo)
        if(this.listenerPlay!=null)
            this.listenerPlay?.onError(errorInfo)
        if(this.listenerCar!=null)
            this.listenerCar?.onError(errorInfo)*/
    }

    override fun onUserUpdated(user: User?, updateReason: User.UpdateReason?) {

    }

    private fun setupListenerHandler(): Handler {
        var looper: Looper
        val handler: Handler
        if (Looper.myLooper() != null) {
            looper = Looper.myLooper()
            handler = Handler(looper)
        } else if (Looper.getMainLooper() != null) {
            looper = Looper.getMainLooper()
            handler = Handler(looper)
        } else {
            throw IllegalArgumentException("Channel Listener must have a Looper.")
        }
        return handler
    }

    private fun getListener(): ChatClientListener? {
        val var1 = mChannelHandlers.values.iterator()
        while (var1.hasNext()) {
            var handler = var1.next() as ChatClientListener
            return handler
        }
        return null
    }
}