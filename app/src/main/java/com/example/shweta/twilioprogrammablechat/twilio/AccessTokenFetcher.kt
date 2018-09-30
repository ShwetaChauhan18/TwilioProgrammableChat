package com.example.shweta.twilioprogrammablechat.twilio

import android.content.Context
import android.util.Log
import org.json.JSONObject

/**
 * Created by Shweta on 30-09-2018.
 */
class AccessTokenFetcher {
    private var context: Context

    constructor(context: Context) {
        this.context = context
    }

    fun fetch(listener: TaskCompletionListener<String, String>) {

        val map = HashMap<String, Any>()
        /*map[APIConstant.TwilioLogin.DEVICE_ID] = Utils.getDeviceID(context)
        map[APIConstant.TwilioLogin.APP_NAME] = "TwilioChatDemo"
        map[APIConstant.TwilioLogin.IDENTITY] = profile.firstName+" "+profile.lastName*/

        /*RetrofitAdapter.createRetroServiceWithSessionToken(context)
                .getToken(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ responseStatus -> onSuccess(listener, responseStatus) }, this::onError)*/
    }

    private fun onSuccess(listener: TaskCompletionListener<String, String>) {

    }

    private fun onError(throwable: Throwable) {

    }

}