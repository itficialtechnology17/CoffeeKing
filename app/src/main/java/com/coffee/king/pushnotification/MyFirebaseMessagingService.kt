package com.coffee.king.pushnotification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import android.util.Log
import com.coffee.king.R
import com.coffee.king.activity.MainActivity
import com.coffee.king.responseCallback.ResponseStatusCode
import com.coffee.king.rest.ApiClient
import com.coffee.king.rest.ApiInterface
import com.coffee.king.utils.Utils
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.kotharirefrigeration.constant.ConstantKey
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyFirebaseMessagingService : FirebaseMessagingService() {
//    lateinit var context: Context

    private lateinit var remoteMessage: RemoteMessage
    override fun onMessageReceived(message: RemoteMessage) {

        Log.e("<>Notification Received", " onMessageReceived Called")

//        context = applicationContext
        this.remoteMessage = message!!

        if (message.data.contains("data")) {
            var jsonObject = JSONObject(message.data.get("data"))
            fireNotification()
        }
    }


    private lateinit var deviceId: String
    private lateinit var token: String
    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        token = p0
        Utils.setPref(applicationContext, ConstantKey.TOKEN_ID, token)
//        sendToken(token)
    }

    private fun sendToken(token: String) {
        val loginId=Utils.getPref(applicationContext,ConstantKey.KEY_LOGIN_ID,"")
        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.sendToken(token,loginId!!)

        call.enqueue(object : Callback<ResponseStatusCode> {
            override fun onResponse(
                call: Call<ResponseStatusCode>,
                response: Response<ResponseStatusCode>
            ) {

            }

            override fun onFailure(call: Call<ResponseStatusCode>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun fireNotification() {

        val idNotification = 2155321


        val mIntent = Intent(applicationContext, MainActivity::class.java)
        mIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, mIntent, PendingIntent.FLAG_CANCEL_CURRENT)

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = idNotification.toString()
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channelName = applicationContext.resources.getString(R.string.app_name)
        val channelDescription = applicationContext.resources.getString(R.string.app_name)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel(channelId, channelName, importance)
            mChannel.description = channelDescription;
            mChannel.enableVibration(true);
            notificationManager.createNotificationChannel(mChannel)
        }

        val builder = NotificationCompat.Builder(this, channelId)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setSmallIcon(R.drawable.notification_icon)
            builder.color = applicationContext.resources.getColor(R.color.colorTheme)
        } else {
            builder.setSmallIcon(R.drawable.notification_icon)
        }

        builder.setContentTitle(applicationContext.resources.getString(R.string.app_name))
        builder.setContentText("New wallpaper have arrived")

        val largeIcon = BitmapFactory.decodeResource(
            applicationContext.resources,
            R.mipmap.ic_launcher
        )
        builder.setSmallIcon(R.drawable.notification_icon);
        builder.setLargeIcon(largeIcon)
        builder.setAutoCancel(true)
        builder.setContentIntent(pendingIntent)
        builder.setChannelId(channelId)
        notificationManager.notify(idNotification, builder.build())
    }
}