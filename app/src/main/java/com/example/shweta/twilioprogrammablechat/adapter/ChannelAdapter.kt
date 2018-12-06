package com.example.shweta.twilioprogrammablechat.adapter

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.shweta.twilioprogrammablechat.R
import com.example.shweta.twilioprogrammablechat.databinding.ListItemChannelListBinding
import com.example.shweta.twilioprogrammablechat.twilio.CustomChannelComparator
import com.twilio.chat.CallbackListener
import com.twilio.chat.Channel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Shweta on 20-10-2018.
 */
class ChannelAdapter : RecyclerView.Adapter<ChannelAdapter.ViewHolder> {

    private var mContext: Context
    private var mChannels: ArrayList<Channel>

    private lateinit var mItemClickListener: OnItemClickListener
    private lateinit var mItemLongClickListener: OnItemLongClickListener


    constructor(context: Context, channels: ArrayList<Channel>) {
        this.mContext = context
        this.mChannels = channels
    }

    interface OnItemClickListener {
        fun onItemClick(channel: Channel, chatName: String)
    }

    interface OnItemLongClickListener {
        fun onItemLongClick(channel: Channel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ListItemChannelListBinding>(layoutInflater,
                R.layout.list_item_channel_list, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return mChannels.size
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mItemClickListener = listener
    }

    fun setOnItemLongClickListener(listener: OnItemLongClickListener) {
        mItemLongClickListener = listener
    }

    fun setChannels(channels: ArrayList<Channel>) {
        this.mChannels = channels
        Collections.sort(this.mChannels, CustomChannelComparator())
        notifyDataSetChanged()
    }

    fun addChannel(channel: Channel) {
        this.mChannels.add(channel)
        Collections.sort(this.mChannels, CustomChannelComparator())
        notifyDataSetChanged()
    }

    fun deleteChannel(channel: Channel) {
        this.mChannels.remove(channel)
        notifyDataSetChanged()
    }

    fun changeChannel(channel: Channel) {
        notifyItemChanged(this.mChannels.indexOf(channel))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val channel = mChannels[position]
        holder.bind(mContext, channel, mItemClickListener, mItemLongClickListener)
    }


    class ViewHolder(binding: ListItemChannelListBinding) : RecyclerView.ViewHolder(binding.root) {
        private val mBinding: ListItemChannelListBinding = binding

        fun bind(context: Context, channel: Channel, clickListener: OnItemClickListener, longClickListener: OnItemLongClickListener) {
            mBinding.textGroupChannelName.text = channel.friendlyName

            channel.getUnconsumedMessagesCount(object : CallbackListener<Long>() {
                override fun onSuccess(count: Long?) {


                    Log.d("Unconsumed message :${count}","")
                    if (count.toString() == "0") {
                        mBinding.textGroupChannelListUnreadCount.visibility = View.INVISIBLE

                    } else {
                        mBinding.textGroupChannelListUnreadCount.visibility = View.VISIBLE
                        mBinding.textGroupChannelListUnreadCount.text = count.toString()

                    }

                }
            })

            Glide.with(context)
                    .load("")
                    .asBitmap()
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .dontAnimate()
                    .override(200, 200)
                    .placeholder(ContextCompat.getDrawable(context, R.drawable.ic_camera_icon))
                    .into(mBinding.imgProfileUser)

            if (clickListener != null) {
                itemView.setOnClickListener {
                    clickListener.onItemClick(channel,
                            mBinding.textGroupChannelName.text.toString())
                }
            }

            if (longClickListener != null) {
                itemView.setOnLongClickListener {
                    longClickListener.onItemLongClick(channel)

                    // return true if the callback consumed the long click
                    true
                }
            }


            if (channel.lastMessageIndex != null) {
                mBinding.textGroupChannelListMessage.visibility = View.VISIBLE
                //if (channel.lastMessageDate != null)
                mBinding.chatdate.text = formatTime(channel.lastMessageDate)
                mBinding.textGroupChannelListMessage.text = channel.lastMessageIndex.toString()
            } else {
                mBinding.chatdate.visibility = View.INVISIBLE
                mBinding.textGroupChannelListMessage.visibility = View.INVISIBLE
            }

            //mBinding.containerTypingIndicator!!.typingIndicatorLayout.visibility = View.GONE

            mBinding.executePendingBindings()
        }

        fun formatTime(timeInMillis: Date): String {
            val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            return dateFormat.format(timeInMillis)
        }

    }
}