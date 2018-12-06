package com.example.shweta.twilioprogrammablechat

import android.content.Context
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitAdapter {

    companion object {

        private lateinit var mApiServices: APIServices

        /**
         *  Bind Retrofit adapter With Token for API call
         */
        fun createRetroServiceWithSessionToken(mContext: Context): APIServices {

            val interceptor = Interceptor { chain ->
                val newRequest: Request = chain.request().newBuilder()
                        .addHeader("Authorization", "9a14c8be13e864e843bfe98f55f9bd0ab083864d")
                        .addHeader("Content-Type", "application/json")
                        .method(chain.request().method(), chain.request().body())
                        .build()

                chain.proceed(newRequest)
            }

            val interceptor1 = HttpLoggingInterceptor()

            if (BuildConfig.DEBUG)
                interceptor1.level = HttpLoggingInterceptor.Level.BODY
            else
                interceptor1.level = HttpLoggingInterceptor.Level.NONE

            val builder = OkHttpClient.Builder()
            builder.followRedirects(false)
            builder.addInterceptor(interceptor1)
            builder.addInterceptor(interceptor)
            builder.connectTimeout(1, TimeUnit.MINUTES)
            builder.readTimeout(1, TimeUnit.MINUTES)
            builder.writeTimeout(1, TimeUnit.MINUTES)

            val client = builder.build()

            val retrofit = Retrofit.Builder()
                    .baseUrl("http://34.208.111.138:5050/v2/")
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()
            mApiServices = retrofit.create(APIServices::class.java)

            return mApiServices
        }

    }
}