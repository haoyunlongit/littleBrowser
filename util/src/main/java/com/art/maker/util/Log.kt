package com.art.maker.util

import android.util.Log

@Suppress("unused", "MemberVisibilityCanBePrivate")
class Log {

    companion object {

        var tag = "LogUtil"
        var enabled = BuildConfig.DEBUG

        fun v(message: String) {
            v(tag, message)
        }

        fun v(tag: String, message: String) {
            if (enabled) {
                Log.v(tag, message)
            }
        }

        fun i(message: String) {
            i(tag, message)
        }

        fun i(tag: String, message: String) {
            if (enabled) {
                Log.i(tag, message)
            }
        }

        fun d(message: String) {
            d(tag, message)
        }

        fun d(tag: String, message: String) {
            if (enabled) {
                Log.d(tag, message)
            }
        }

        fun w(message: String) {
            w(tag, message)
        }

        fun w(tag: String, message: String) {
            if (enabled) {
                Log.w(tag, message)
            }
        }

        fun e(message: String) {
            e(tag, message)
        }

        fun e(throwable: Throwable) {
            e(tag, throwable)
        }

        fun e(tag: String, message: String) {
            if (enabled) {
                Log.e(tag, message)
            }
        }

        fun e(tag: String, throwable: Throwable) {
            if (enabled) {
                Log.e(tag, throwable.message, throwable)
            }
        }

        fun e(tag: String, message: String, tr: Throwable?) {
            if (enabled) {
                Log.e(tag, message, tr)
            }
        }
    }
}