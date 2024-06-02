package gr.android.survey.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import gr.android.survey.data.networkCalls.SurveyApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) //specifying that these dependencies should be available in the singleton component of Dagger-Hilt
object NetworkModule {

    //Creates an instance of HttpLoggingInterceptor,
    // which is used for logging HTTP requests and responses
    @Singleton
    @Provides
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor()

    //satisfies provide RetrofitInstance
    //Provides an instance of OkHttpClient, which is configured with a read timeout,
    // connect timeout, and the logging interceptor created above
    @Singleton
    @Provides
    fun provideHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
                .readTimeout(15, TimeUnit.SECONDS)
                .connectTimeout(15, TimeUnit.SECONDS)
                .build()
    }

    //returns gsonConverterFactory to provideRetrofit
    //Creates an instance of GsonConverterFactory,
    // which is used to serialize and deserialize JSON using Gson
    @Singleton
    @Provides
    fun provideConverterFactory(): GsonConverterFactory {
        return GsonConverterFactory.create()
    }

    //the same as every retrofit builder in order to get our data
    @Singleton
    @Provides
    fun provideRetrofitInstance(
            okHttpClient: OkHttpClient,
            gsonConverterFactory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
                .baseUrl("https://xm-assignment.web.app")
                .client(okHttpClient)
                .addConverterFactory(gsonConverterFactory)
                .build()
    }

    // Builds and returns a Retrofit instance configured with the base

    //this will give the api instance in our RemoteDataSource
    //singleton means we re going to have only one instance of this
    //we're using application scope for this API
    //Provide is if instances must be created with the builder pattern.
    //or if you don't own the class because it comes from external library (Retrofit, Room etc)
    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): SurveyApi {
        return retrofit.create(SurveyApi::class.java)
    }

}