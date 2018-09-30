package com.example.shweta.twilioprogrammablechat.twilio

import com.twilio.chat.*
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by Shweta on 30-09-2018.
 */
class ChannelExtractor {

    fun extractAndSortFromChannelDescriptor(paginator: Paginator<ChannelDescriptor>,
                                            listener: TaskCompletionListener<List<Channel>, String>) {

        extractFromChannelDescriptor(paginator, object : TaskCompletionListener<List<Channel>, String> {
            override fun onSuccess(channels: List<Channel>?) {
               // Collections.sort(channels, CustomChannelComparator())
                listener.onSuccess(channels)
            }

            override fun onError(s: String) {
                listener.onError(s)
            }
        })
    }

    private fun extractFromChannelDescriptor(paginator: Paginator<ChannelDescriptor>,
                                             listener: TaskCompletionListener<List<Channel>, String>) {

        val channels = ArrayList<Channel>()
        val channelDescriptorCount = AtomicInteger(paginator.items.size)
        for (channelDescriptor in paginator.items) {
            channelDescriptor.getChannel(object : CallbackListener<Channel>() {
                override fun onSuccess(channel: Channel) {
                    channels.add(channel)
                    val channelDescriptorsLeft = channelDescriptorCount.decrementAndGet()
                    if (channelDescriptorsLeft == 0) {
                        listener.onSuccess(channels)
                    }
                }

                override fun onError(errorInfo: ErrorInfo?) {
                    listener.onError(errorInfo!!.message)
                }
            })
        }
    }
}