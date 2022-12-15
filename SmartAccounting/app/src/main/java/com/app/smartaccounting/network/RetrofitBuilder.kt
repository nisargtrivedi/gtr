package com.app.smartaccounting.network

import com.app.smartaccounting.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitBuilder {

    const val CONTENT_TYPE = "multipart/form-data"

    const val BASE_URL = "https://gtr-kw.com/api/v1/"
    const val BASE_URL_IMAGE = "https://gtr-kw.com"

    private fun getRetrofit(): Retrofit {

        //Start Region
        //For log in Logcat
        val interceptor = Interceptor { chain ->
            var request = chain.request()
            val builder = request.newBuilder().addHeader("Cache-Control", "no-cache")
            request = builder.build()
            chain.proceed(request)
        }
        val logging = HttpLoggingInterceptor()
        // set your desired log level
        if(BuildConfig.DEBUG)
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        else
            logging.setLevel(HttpLoggingInterceptor.Level.NONE)

        val client = OkHttpClient.Builder()
        client.addInterceptor(logging)
        client.addInterceptor(interceptor)
        client.connectTimeout(60, TimeUnit.SECONDS)
        client.readTimeout(60, TimeUnit.SECONDS)
        //End Region

        return Retrofit.Builder()
            .client(client.build())
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: APIInterface = getRetrofit().create(APIInterface::class.java)
}