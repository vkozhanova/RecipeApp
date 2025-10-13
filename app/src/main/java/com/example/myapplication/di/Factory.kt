package com.example.myapplication.di

interface Factory<T> {
    fun create(): T
}