package com.luv.link.network

import com.luv.link.network.services.ApiService
import com.luv.link.repositories.KtorRepository
import com.luv.link.repositories.NetworkRepository
import com.luv.link.repositories.RetrofitRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val BASE_URL = "https://your-api-url.com/"

    // Create a logging interceptor (optional for debugging)
    private val loggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // Log the request and response body
        }

    private val errorInterceptor =
        Interceptor { chain ->
            val response = chain.proceed(chain.request())

            // Check for error codes and handle them
            if (!response.isSuccessful) {
                // Handle errors, e.g., throw custom exceptions
                throw Exception("API call failed with code ${response.code}")
            }

            response
        }

    // use kator like this
    // val response: HttpResponse = client.get("https://ktor.io/")

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient
            .Builder()
            .connectTimeout(30, TimeUnit.SECONDS) // Connection timeout
            .readTimeout(30, TimeUnit.SECONDS) // Read timeout
            .writeTimeout(30, TimeUnit.SECONDS) // Write timeout
            .addInterceptor(loggingInterceptor) // Add logging interceptor
            .addInterceptor(errorInterceptor) // Add error interceptor
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // Use Gson converter for JSON parsing
            .client(okHttpClient) // Add the OkHttpClient
            .build()

    @Provides
    fun provideKtorClient(): HttpClient = HttpClient(CIO)

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)

    @Provides
    @Named("Retrofit")
    fun provideRetrofitRepository(apiService: ApiService): NetworkRepository =
        RetrofitRepository(
            apiService
        )

    @Provides
    @Named("Ktor")
    fun provideKtorRepository(client: HttpClient): NetworkRepository = KtorRepository(client)
}
