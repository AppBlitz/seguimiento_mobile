package co.edu.uniquindio.games

import android.app.Application
import co.edu.uniquindio.games.core.di.AppModule

class PokeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppModule.init(this)
    }
}