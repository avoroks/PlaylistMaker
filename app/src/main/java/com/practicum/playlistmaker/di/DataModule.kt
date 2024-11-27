package com.practicum.playlistmaker.di

import android.content.Context
import android.media.MediaPlayer
import androidx.room.Room
import com.google.gson.Gson
import com.practicum.playlistmaker.data.media.db.AppDatabase
import com.practicum.playlistmaker.data.search.sharedPrefs.SearchHistory
import com.practicum.playlistmaker.data.search.network.ItunesApi
import com.practicum.playlistmaker.data.search.network.NetworkClient
import com.practicum.playlistmaker.data.search.network.RetrofitNetworkClient
import com.practicum.playlistmaker.data.settings.sharedPrefs.ApplicationTheme
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    single<ItunesApi> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ItunesApi::class.java)
    }

    single {
        androidContext()
            .getSharedPreferences("PLAYLIST_MAKER_PREFERENCES", Context.MODE_PRIVATE)
    }

    factory { Gson() }

    single {
        SearchHistory(get(), get())
    }

    single<NetworkClient> {
        RetrofitNetworkClient(get())
    }

    single {
        ApplicationTheme(get())
    }

    single {
        MediaPlayer()
    }

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.playlistmaker")
            .fallbackToDestructiveMigration()
            .build()
    }
}