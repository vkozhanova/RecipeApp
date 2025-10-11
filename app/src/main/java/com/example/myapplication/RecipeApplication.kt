package com.example.myapplication

import android.app.Application
import com.example.myapplication.di.AppContainer

class RecipeApplication: Application() {

    lateinit var appContainer: AppContainer

    override fun onCreate() {
        super.onCreate()

        appContainer = AppContainer(this)
    }
}