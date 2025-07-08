package com.example.myapplication.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.data.ContractRepository
import com.example.myapplication.data.ContractCalls
import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


class HomeViewModel(application: Application) : AndroidViewModel(application){

    private val editor = application.getSharedPreferences("UserPrefs", Application.MODE_PRIVATE)

    private val userRole = editor.getString("user_role", null)

    private val _text2 = MutableLiveData<String>().apply {
        value = userRole
    }

    val text2: LiveData<String> = _text2

    private val contractCalls = ContractCalls()



    fun getDataFromSepolia(){
        viewModelScope.launch {
            try {
                val balance = contractCalls.getTokenBalance()
                _text2.value = "Balance: $balance"
            }
            catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching token balance", e)
                _text2.value = "Error fetching balance"
            }
        }

    }




}