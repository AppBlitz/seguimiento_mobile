package co.edu.uniquindio.games.core.di

import android.content.Context
import co.edu.uniquindio.games.core.util.NetworkMonitor
import co.edu.uniquindio.games.data.remote.PokeApiService
import co.edu.uniquindio.games.data.repository.PokemonRepositoryImpl
import co.edu.uniquindio.games.domain.repository.PokemonRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object AppModule {

    private const val BASE_URL = "https://pokeapi.co/api/v2/"

    lateinit var networkMonitor: NetworkMonitor
        private set

    fun init(context: Context) {
        if (!::networkMonitor.isInitialized) {
            networkMonitor = NetworkMonitor(context.applicationContext)
        }
    }

    private val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    val pokeApiService: PokeApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PokeApiService::class.java)
    }

    val pokemonRepository: PokemonRepository by lazy {
        PokemonRepositoryImpl(pokeApiService)
    }
}