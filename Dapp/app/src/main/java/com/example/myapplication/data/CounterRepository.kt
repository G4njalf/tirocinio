package com.example.myapplication.data

import android.content.Context

class CounterRepository(context: Context) {

    private val prefs = context.getSharedPreferences("counter_prefs", Context.MODE_PRIVATE)

    fun getCounter(): Int {
        return prefs.getInt("counter", 0)
    }

    fun setCounter(value: Int) {
        prefs.edit().putInt("counter", value).apply()
    }

    fun resetCounter() {
        setCounter(0)
    }
}