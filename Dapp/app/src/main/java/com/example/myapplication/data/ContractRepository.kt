package com.example.myapplication.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.ui.contracts.Contract


// REPOSITORY PER MANTENERE IN MEMORIA I CONTRATTI DURANTE LA SESSIONE

class ContractRepository {

    private val _contractAddresses = MutableLiveData<List<Contract>>(emptyList())
    val contractAddresses: LiveData<List<Contract>> get() = _contractAddresses

    fun addContract(address: String,
                    premio: UInt,
                    isLiquidato: Boolean,
                    isAttivato: Boolean,
                    isFundend: Boolean,
                    addressAssicurato: String,
                    addressAssicuratore: String) {
        val currentContracts = _contractAddresses.value ?: emptyList()
        _contractAddresses.value = currentContracts + Contract(address, premio,
            isLiquidato, isAttivato, isFundend, addressAssicurato, addressAssicuratore)    }
}