package com.example.shweta.twilioprogrammablechat.common

import android.content.Context
import android.content.SharedPreferences
import com.example.shweta.twilioprogrammablechat.MyApplication

class SessionManager private constructor(context: Context) {

    internal var pref: SharedPreferences
    internal var editor: SharedPreferences.Editor
    internal var context: Context
    internal var PRIVATE_MODE = 0

    companion object {
        val KEY_USERNAME = "username"
        private val PREF_NAME = "TWILIOCHAT"
        private val IS_LOGGED_IN = "IsLoggedIn"
        val instance = SessionManager(MyApplication.instance)
    }

    val userDetails: HashMap<String, String>
        get() {
            val user = HashMap<String, String>()
            user.put(KEY_USERNAME, pref.getString(KEY_USERNAME, null))
            return user
        }
    val username: String
        get() {
            return pref.getString(KEY_USERNAME, null)
        }
    val isLoggedIn: Boolean
        get() {
            return pref.getBoolean(IS_LOGGED_IN, false)
        }

    init {
        this.context = context
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = pref.edit()
    }

    fun createLoginSession(username: String) {
        editor.putBoolean(IS_LOGGED_IN, true)
        editor.putString(KEY_USERNAME, username)
        // commit changes
        editor.commit()
    }

    fun logoutUser() {
        editor = editor.clear()
        editor.commit()
    }
}