package com.coffee.king.customclass

import android.app.Application
import com.coffee.king.R
import com.google.firebase.FirebaseApp
import com.kotharirefrigeration.constant.ConstantValue

class AppControl : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        ConstantValue.BASE_URL=resources.getString(R.string.base_url)
    }

}
