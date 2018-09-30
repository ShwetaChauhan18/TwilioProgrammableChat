package com.example.shweta.twilioprogrammablechat.common

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.Window
import com.example.shweta.twilioprogrammablechat.R

/**
 * Created by Shweta on 30-09-2018.
 */
open class Utils {
    companion object {
        /**
         *  Set Log
         *  @param log
         * */
        fun setLog(log: String) {
            /*if (BuildConfig.DEBUG)
                Log.e(TAG, log)*/
        }

        /**
         *  Show Dialog
         *  @param context
         */
        fun showProgress(context: Context): Dialog {
            val pd = Dialog(context)
            val view = LayoutInflater.from(context).inflate(R.layout.progress_view, null)
            pd.requestWindowFeature(Window.FEATURE_NO_TITLE)
            pd.window?.setBackgroundDrawableResource(android.R.color.transparent)
            pd.setCancelable(false)
            pd.setContentView(view)
            return pd

        }

    }
}