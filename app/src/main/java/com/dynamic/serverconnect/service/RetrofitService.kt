package com.dynamic.serverconnect.service

import android.app.Application
import android.content.Context
import com.dynamic.serverconnect.SessionManager
import com.dynamic.serverconnect.StaticVariable
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Charles Raj I on 17/08/23.
 * @author Charles Raj I
 */

@Module
@InstallIn(SingletonComponent::class)
class RetrofitService @Inject constructor() {

//    @Inject
//    lateinit var application: Application

    @Provides
    @Singleton
    fun instance(@ApplicationContext context: Context): RestServiceInterface {

        val sessionManager = SessionManager(context as Application)
        val schema = sessionManager.getString(StaticVariable.SCHEMA)
        val server = sessionManager.getString(StaticVariable.SERVER)
        val port = sessionManager.getString(StaticVariable.PORT)
        val url = "$schema://$server:$port/"
        sessionManager.setString(StaticVariable.BASE_URL,url)

        val serviceInterface: RestServiceInterface by lazy {
            val httpLoggingInterceptor: HttpLoggingInterceptor = HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY)

            val okHttpClient: OkHttpClient = OkHttpClient()
                .newBuilder()
                .addInterceptor(httpLoggingInterceptor)
                .build()
            Retrofit
                .Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(url.trim())
                .client(okHttpClient)
                .build()
                .create(RestServiceInterface::class.java)
        }
        return serviceInterface
    }
}