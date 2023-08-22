package com.dynamic.serverconnect

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject

/**
 * Created by Charles Raj I on 17/08/23.
 * @author Charles Raj I
 */

class SessionManager @Inject constructor(private val application: Application) {

    private var sharedPreferences: SharedPreferences =
        application.getSharedPreferences("serverDetails", Context.MODE_PRIVATE)
    private var editor: SharedPreferences.Editor = sharedPreferences.edit()

    fun setString(key: String?, value: String?) {
        editor.putString(key, value)
        editor.commit()
    }

    fun getString(key: String?): String? {
        return sharedPreferences.getString(key, "")
    }

    fun setInt(key: String?, value: Int) {
        editor.putInt(key, value)
        editor.commit()
    }

    fun getInt(key: String?): Int {
        return sharedPreferences.getInt(key, 0)
    }
}