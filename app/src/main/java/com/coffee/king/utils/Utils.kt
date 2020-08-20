package com.coffee.king.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import android.preference.PreferenceManager
import android.provider.Settings
import android.text.format.DateFormat
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.kotharirefrigeration.constant.ConstantKey
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*


object Utils {

    fun isOnline(context: Context): Boolean {
        var connected = false
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        if (netInfo != null && netInfo.isConnectedOrConnecting) {
            connected = true
        } else if (netInfo != null && netInfo.isConnected
            && cm.activeNetworkInfo.isAvailable
        ) {
            connected = true
        } else if (netInfo != null && netInfo.isConnected) {
            try {
                val url = URL("http://www.google.com")
                val urlc = url
                    .openConnection() as HttpURLConnection
                urlc.connectTimeout = 3000
                urlc.connect()
                if (urlc.responseCode == 200) {
                    connected = true
                }
            } catch (e1: MalformedURLException) {
                e1.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        } else if (cm != null) {
            val netInfoAll = cm.allNetworkInfo
            for (ni in netInfoAll) {
                println("get network type :::" + ni.typeName)
                if ((ni.typeName.equals("WIFI", ignoreCase = true) || ni
                        .typeName.equals("MOBILE", ignoreCase = true))
                    && ni.isConnected && ni.isAvailable
                ) {
                    connected = true
                    if (connected) {
                        break
                    }
                }
            }
        }
        return connected
    }

    fun setPref(context: Context, key: String, value: String) {
        val editor =
            PreferenceManager.getDefaultSharedPreferences(context).edit().putString(key, value)
        editor.apply()
    }

    fun getPref(context: Context, key: String, value: String): String? {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, value)
    }

    fun setPref(context: Context, key: String, value: Boolean) {
        val editor =
            PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(key, value)
        editor.apply()
    }

    fun getPref(context: Context, key: String, value: Boolean): Boolean? {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, value)
    }

    @SuppressLint("HardwareIds")
    fun getSetDeviceIdFromDevice(context: Context) {
        val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        setPref(context, ConstantKey.DEVICE_ID, androidId)
    }






    fun checkPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= 23) {
            isReadStoragePermissionGranted(context) && isWriteStoragePermissionGranted(context)
        } else {
            true
        }
    }

    private fun isReadStoragePermissionGranted(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(
                    context, Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                true
            } else {
                ActivityCompat.requestPermissions(
                    context as Activity,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    3
                )
                false
            }
        } else {
            true
        }
    }

    private fun isWriteStoragePermissionGranted(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(
                    context, Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                true
            } else {
                ActivityCompat.requestPermissions(
                    context as Activity,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    3
                )
                false
            }
        } else {
            true
        }
    }

    fun checkCameraPermission(context: Context): Boolean {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }
        return true
    }


    fun getCurrentDate(): String {
        val c = Calendar.getInstance().time
        val df = SimpleDateFormat("dd-MMM-yyyy")
        return df.format(c)
    }

    fun getMonthNameFromNumber(monthNumber:Int):String{
        val cal = Calendar.getInstance()
        val month_date = SimpleDateFormat("MMMM")
        val monthnum = monthNumber
        cal[Calendar.MONTH] = monthnum
        val month_name = month_date.format(cal.time)
        return  month_name;
    }
    fun getDateFromString(dateString:String):String{
        val df = SimpleDateFormat("yyyy-MM-dd")
        val dateOfEvent = df.parse(dateString)
        val df1= SimpleDateFormat("dd-MMM-yyyy")
        return df1.format(dateOfEvent)
    }


    fun getServerDateFormat(dateString:String):String{
        val df = SimpleDateFormat("dd-MM-yyyy")
        val dateOfEvent = df.parse(dateString)
        val df1= SimpleDateFormat("yyyy-MM-dd")
        return df1.format(dateOfEvent)
    }

    fun getDateForCompare(dateString:String):String{
        val df = SimpleDateFormat("dd-MM-yyy")
        val dateOfEvent = df.parse(dateString)
        val df1= SimpleDateFormat("dd-MMM-yyyy")
        return df1.format(dateOfEvent)
    }



    fun getYesterdayDateString(): String {
        val df = SimpleDateFormat("dd-MMM-yyyy")
        val cal = Calendar.getInstance()
        cal.add(Calendar.DATE, -1)
        return df.format(cal.time)
    }


    fun getDayMonth(dateString: String): String {
        val format = SimpleDateFormat("yyyy-MM-dd")
        val date = format.parse(dateString)
        val day = DateFormat.format("dd", date) as String
        val monthString = DateFormat.format("MMM", date) as String
        val yearString = DateFormat.format("yyyy", date) as String
        return "$day\n$monthString $yearString"
    }

    fun getDay(dateString: String):String{
        val format = SimpleDateFormat("yyyy-MM-dd")
        val date = format.parse(dateString)
        val day = DateFormat.format("dd", date) as String
        return day
    }

    fun getMonthName(dateString: String):String{
        val format = SimpleDateFormat("yyyy-MM-dd")
        val date = format.parse(dateString)
        val monthString = DateFormat.format("MMMM", date) as String
        return monthString
    }

    fun isEmailValid(email: CharSequence): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun getDateCurrentTimeZone(timestamp: Long): String {
        try {
            val calendar = Calendar.getInstance()
            val tz = TimeZone.getDefault()
            calendar.timeInMillis = timestamp * 1000
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.timeInMillis))
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val currenTimeZone = calendar.time as Date
            return sdf.format(currenTimeZone)
        } catch (e: Exception) {
        }

        return ""
    }

    fun getCurrentDateAsDB(): String {
        val calendar = Calendar.getInstance(TimeZone.getDefault())

        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH) + 1
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        return "$currentYear-$currentMonth-$currentDay"
    }

}
