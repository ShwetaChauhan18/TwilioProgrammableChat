package com.example.shweta.twilioprogrammablechat.twilio

import com.twilio.chat.Channel

/**
 * Created by Shweta on 07-10-2018.
 */
interface LoadChannelListener {
    fun onChannelsFinishedLoading(channels: List<Channel>)
}