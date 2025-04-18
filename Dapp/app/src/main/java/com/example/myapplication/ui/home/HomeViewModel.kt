package com.example.myapplication.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.CounterRepository


class HomeViewModel(application: Application) : AndroidViewModel(application){

    private val repository = CounterRepository(application.applicationContext)

    private var counter = repository.getCounter()

    private val _text = MutableLiveData<String>().apply {
        value = "Hai cliccato $counter volte"
    }
    val text: LiveData<String> = _text

    fun incrementaContatore() {
        counter++
        repository.setCounter(counter)
        _text.value = "Hai cliccato $counter volte"
    }

    fun resetContatore(){
        counter = 0
        repository.resetCounter()
        _text.value = "Hai cliccato $counter volte"
    }
}