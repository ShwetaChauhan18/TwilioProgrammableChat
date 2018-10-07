package com.example.shweta.twilioprogrammablechat.twilio

import com.twilio.chat.Channel
import com.twilio.chat.ErrorInfo

/**
 * Created by Shweta on 07-10-2018.
 */
interface StatusListenerCustom {
    fun onSuccess(channel: Channel)

    fun onError(errorInfo: ErrorInfo) {}
}