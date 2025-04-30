package com.example.myapplication.ui.contract_creation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ContractCreationViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Contract Creation Fragment"
    }
    val text: LiveData<String> = _text
}