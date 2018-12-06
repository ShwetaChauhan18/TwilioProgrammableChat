package com.example.shweta.twilioprogrammablechat.twilio

import com.example.shweta.twilioprogrammablechat.MyApplication
import com.example.shweta.twilioprogrammablechat.R
import com.twilio.chat.Channel

/**
 * Created by Shweta on 30-09-2018.
 */
class CustomChannelComparator: Comparator<Channel>  {
    private val defaultChannelName: String

    constructor(){
        defaultChannelName = MyApplication.instance.resources.getString(R.string.default_channel_name)
    }

    override fun compare(lhs: Channel?, rhs: Channel?): Int {
        if (lhs?.friendlyName!!.contentEquals(defaultChannelName)) {
            return -100
        } else if (rhs?.friendlyName!!.contentEquals(defaultChannelName)) {
            return 100
        }
        return lhs.friendlyName?.toLowerCase()!!.compareTo(rhs.friendlyName.toLowerCase())
    }
}