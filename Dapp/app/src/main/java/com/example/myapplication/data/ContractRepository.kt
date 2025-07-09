package com.example.myapplication.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.ui.contracts.Contract


// REPOSITORY PER MANTENERE IN MEMORIA I CONTRATTI DURANTE LA SESSIONE

class ContractRepository {


    private val _contractData = MutableLiveData<List<Contract>>(emptyList())
    val contractData: LiveData<List<Contract>> get() = _contractData

    private val _contractAddresses = MutableLiveData<List<String>>(emptyList())
    val contractAddresses: LiveData<List<String>> get() = _contractAddresses

    fun addContract(address: String,
                    premio: UInt,
                    isLiquidato: Boolean,
                    isAttivato: Boolean,
                    isFundend: Boolean,
                    addressAssicurato: String,
                    addressAssicuratore: String) {
        val currentContracts = _contractData.value ?: emptyList()
        _contractData.value = currentContracts + Contract(address, premio,
            isLiquidato, isAttivato, isFundend, addressAssicurato, addressAssicuratore)
    }

    fun addContractAddress(address: String) {
        val currentAddresses = _contractAddresses.value ?: emptyList()
        _contractAddresses.value = currentAddresses + address
    }
}