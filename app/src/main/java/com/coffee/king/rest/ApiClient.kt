package com.coffee.king.rest

import com.kotharirefrigeration.constant.ConstantValue
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object ApiClient {

    private var retrofit: Retrofit? = null

    val okHttpClient = OkHttpClient.Builder()
        .readTimeout(100, TimeUnit.SECONDS)
        .connectTimeout(100, TimeUnit.SECONDS)
        .build()

    val client: Retrofit
        get() {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(ConstantValue.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build()
            }
            return retrofit!!
        }
}