package com.smart.browser.little.util

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.*
import com.art.maker.util.Log
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue


object AdvertisingIdClient {
    /**
     * google ad id
     */
    var mGoogleAdId: String = "fcfbf386-7c6f-4cd4-8b92-ca003ddd9821"

    /**
     * 这个方法是耗时的，不能在主线程调用
     */
    @Throws(Exception::class)
    fun getGoogleAdId(context: Context): String? {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            return "Cannot call in the main thread, You must call in the other thread"
        }
        val pm: PackageManager = context.packageManager
        pm.getPackageInfo("com.android.vending", 0)
        val connection = AdvertisingConnection()
        val intent = Intent(
            "com.google.android.gms.ads.identifier.service.START"
        )
        intent.setPackage("com.google.android.gms")
        return if (context.bindService(intent, connection, Context.BIND_AUTO_CREATE)) {
            try {
                val adInterface = AdvertisingInterface(
                    connection.binder
                )
                adInterface.id
            } finally {
                context.unbindService(connection)
            }
        } else ""
    }

    private class AdvertisingConnection : ServiceConnection {
        var retrieved = false
        private val queue: LinkedBlockingQueue<IBinder> = LinkedBlockingQueue(1)
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            try {
                queue.put(service)
            } catch (localInterruptedException: InterruptedException) {
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {}

        @get:Throws(InterruptedException::class)
        val binder: IBinder
            get() {
                check(!retrieved)
                retrieved = true
                return queue.take()
            }
    }

    private class AdvertisingInterface(private val binder: IBinder) : IInterface {
        override fun asBinder(): IBinder {
            return binder
        }

        @get:Throws(RemoteException::class)
        val id: String?
            get() {
                val data = Parcel.obtain()
                val reply = Parcel.obtain()
                return try {
                    data.writeInterfaceToken("com.google.android.gms.ads.identifier.internal.IAdvertisingIdService")
                    binder.transact(1, data, reply, 0)
                    reply.readException()
                    reply.readString()
                } finally {
                    reply.recycle()
                    data.recycle()
                }
            }

        @Throws(RemoteException::class)
        fun isLimitAdTrackingEnabled(paramBoolean: Boolean): Boolean {
            val data = Parcel.obtain()
            val reply = Parcel.obtain()
            return try {
                data.writeInterfaceToken("com.google.android.gms.ads.identifier.internal.IAdvertisingIdService")
                data.writeInt(if (paramBoolean) 1 else 0)
                binder.transact(2, data, reply, 0)
                reply.readException()
                0 != reply.readInt()
            } finally {
                reply.recycle()
                data.recycle()
            }
        }
    }

    fun initAdId(application: Context) {
        Executors.newSingleThreadExecutor().execute {
            try {
                val adId = getGoogleAdId(application)
                Log.e("initAdId", "adId:$adId")
                if (!adId.isNullOrEmpty()) {
                    mGoogleAdId = adId
                }
            } catch (e: Exception) {
            }
        }
    }
}