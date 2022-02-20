package com.art.maker.util

import android.app.Application
import android.content.SharedPreferences
import android.util.Log

/**
 * SharedPreferences工具类.
 *
 * @author yushaojian
 * @date 2021-02-05 09:54
 */
object SPUtils {

    private lateinit var mPrefs: SharedPreferences

    /**
     * 初始化，应该在所有其他方法调用之前调用
     */
    fun init(application: Application, name: String) {
        mPrefs = application.getSharedPreferences(name, 0)
    }

    operator fun contains(key: String): Boolean {
        return safeRun(false) { mPrefs.contains(key) }
    }

    fun removeKey(key: String): SPUtils {
        return safeRun(this) {
            mPrefs.edit().remove(key).apply()
            this
        }
    }

    fun putBoolean(key: String, value: Boolean): SPUtils {
        return safeRun(this) {
            mPrefs.edit().putBoolean(key, value).apply()
            this
        }
    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return safeRun(defaultValue) { mPrefs.getBoolean(key, defaultValue) }
    }

    fun putInt(key: String, value: Int): SPUtils {
        return safeRun(this) {
            mPrefs.edit().putInt(key, value).apply()
            this
        }
    }

    fun getInt(key: String, defaultValue: Int): Int {
        return safeRun(defaultValue) { mPrefs.getInt(key, defaultValue) }
    }

    fun putLong(key: String, value: Long): SPUtils {
        return safeRun(this) {
            mPrefs.edit().putLong(key, value).apply()
            this
        }
    }

    fun getLong(key: String, defaultValue: Long): Long {
        return safeRun(defaultValue) { mPrefs.getLong(key, defaultValue) }
    }

    fun putString(key: String, value: String?): SPUtils {
        return safeRun(this) {
            mPrefs.edit().putString(key, value).apply()
            this
        }
    }

    fun getString(key: String, defaultValue: String?): String? {
        return safeRun(defaultValue) { mPrefs.getString(key, defaultValue) }
    }

    private fun <T> safeRun(defaultValue: T, function: () -> T): T {
        if (!::mPrefs.isInitialized) {
            Log.e("SPUtils", "init SHOULD called before any other functions!")
            return defaultValue
        }
        return function()
    }

}