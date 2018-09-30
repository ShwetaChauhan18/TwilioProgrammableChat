package com.example.shweta.twilioprogrammablechat.twilio

/**
 * Created by Shweta on 30-09-2018.
 */
interface TaskCompletionListener<T, U> {
    fun onSuccess(t: T?)

    fun onError(u: U)
}