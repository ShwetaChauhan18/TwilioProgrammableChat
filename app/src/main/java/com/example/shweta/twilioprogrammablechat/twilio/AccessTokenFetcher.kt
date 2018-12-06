package com.example.shweta.twilioprogrammablechat.twilio

import android.content.Context
import android.provider.Settings
import android.util.Log
import com.example.shweta.twilioprogrammablechat.RetrofitAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import org.json.JSONObject

/**
 * Created by Shweta on 30-09-2018.
 */
class AccessTokenFetcher(private var context: Context) {

    fun fetch(listener: TaskCompletionListener<String, String>) {

        var map = HashMap<String, Any>()
        map["deviceId"] = getDeviceID(context)
        map["app_name"] = "TwilioChatDemo"
        map["identity"] = "Hello"  // get username from logged in user

        RetrofitAdapter.createRetroServiceWithSessionToken(context)
                .getToken(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ responseStatus -> onSuccess(listener, responseStatus) }, this::onError)
    }

    private fun onSuccess(listener: TaskCompletionListener<String, String>, responseBodyStatus: ResponseBody) {
        val response = responseBodyStatus.string()
        val jsonObject = JSONObject(response)
        Log.e("TOKEN", jsonObject.getString("token"))
        //Utils.setLog("FCM SID"+jsonObject.getString("token"))
        listener.onSuccess(jsonObject.getString("token"))
    }

    private fun onError(throwable: Throwable) {

    }

    fun getDeviceID(mContext: Context): String {
        return Settings.Secure.getString(mContext.contentResolver,
                Settings.Secure.ANDROID_ID)
    }
}