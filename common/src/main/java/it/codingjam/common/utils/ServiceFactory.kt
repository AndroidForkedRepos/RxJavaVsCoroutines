package it.codingjam.common.utils

import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import it.codingjam.common.StackOverflowServiceCoroutines
import it.codingjam.common.StackOverflowServiceRx
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.CallAdapter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceFactory {
    fun createOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
                .addInterceptor { chain ->
                    var request = chain.request()
                    val url = request.url().newBuilder()
                            .addQueryParameter("site", "stackoverflow")
                            .addQueryParameter("key", "fruiv4j48P0HjSJ8t7a8Gg((")
                            .build()
                    request = request.newBuilder().url(url).build()
                    chain.proceed(request)
                }
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                .build()
    }

    inline fun <reified T> createService(callAdapter: CallAdapter.Factory? = null): T {
        val gson = GsonBuilder().create()
        return Retrofit.Builder()
                .baseUrl("https://api.stackexchange.com/2.2/")
                .apply {
                    callAdapter?.let { addCallAdapterFactory(it) }
                }
                .client(createOkHttpClient())
                .addConverterFactory(DenvelopingConverter(gson))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build().create(T::class.java)
    }

    val rx = createService<StackOverflowServiceRx>(RxJava2CallAdapterFactory.create())

    val coroutines = createService<StackOverflowServiceCoroutines>()
}