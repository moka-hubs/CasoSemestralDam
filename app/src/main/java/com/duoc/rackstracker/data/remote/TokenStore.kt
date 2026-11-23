package com.duoc.rackstracker.data.remote

import android.content.Context

/**
 * Guarda el authToken de Xano para adjuntarlo a cada petición.
 * Se inicializa una sola vez desde MainActivity.
 */
object TokenStore {

    private const val PREFS_NAME = "racks_tracker_prefs"
    private const val KEY_TOKEN = "auth_token"

    private var prefs: android.content.SharedPreferences? = null

    fun attach(context: Context) {
        prefs = context.applicationContext.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )
    }

    var token: String?
        get() = prefs?.getString(KEY_TOKEN, null)
        set(value) {
            prefs?.edit()?.putString(KEY_TOKEN, value)?.apply()
        }

    fun limpiar() {
        token = null
    }
}
