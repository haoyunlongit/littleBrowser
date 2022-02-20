package com.art.maker.ad

import android.util.Log

@Suppress("unused", "MemberVisibilityCanBePrivate")
internal class Log {

    companion object {

        private const val TAG = "AdLib"
        internal var enabled = BuildConfig.DEBUG

        fun v(message: String) {
            v(TAG, message)
        }

        fun v(tag: String, message: String) {
            if (enabled) {
                Log.v(tag, message)
            }
        }

        fun i(message: String) {
            i(TAG, message)
        }

        fun i(tag: String, message: String) {
            if (enabled) {
                Log.i(tag, message)
            }
        }

        fun d(message: String) {
            d(TAG, message)
        }

        fun d(tag: String, message: String) {
            if (enabled) {
                Log.d(tag, message)
            }
        }

        fun w(message: String) {
            w(TAG, message)
        }

        fun w(tag: String, message: String) {
            if (enabled) {
                Log.w(tag, message)
            }
        }

        fun e(message: String) {
            e(TAG, message)
        }

        fun e(tag: String, message: String) {
            if (enabled) {
                Log.e(tag, message)
            }
        }

        fun e(tag: String, message: String, tr: Throwable?) {
            if (enabled) {
                Log.e(tag, message, tr)
            }
        }
    }
}