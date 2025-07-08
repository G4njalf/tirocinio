package com.example.myapplication.ui.contracts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.BlockChainCalls
import com.example.myapplication.data.ContractCalls

class ContractsViewModel : ViewModel() {

    private val _contracts = MutableLiveData<List<Contract>>()
    val contracts: LiveData<List<Contract>> get() = _contracts

    private val blockChainCalls = BlockChainCalls()
    private val contractCalls = ContractCalls()


    fun loadContracts(){

    }



}