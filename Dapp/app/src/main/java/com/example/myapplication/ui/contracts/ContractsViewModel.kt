package com.example.myapplication.ui.contracts

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.BlockChainCalls
import com.example.myapplication.data.ContractCalls
import kotlinx.coroutines.launch
import com.example.myapplication.data.ContractRepository

class ContractsViewModel (application: Application) : AndroidViewModel(application){

    private val editor = application.getSharedPreferences("UserPrefs", MODE_PRIVATE)
    private val userRole = editor.getString("user_role", null)
    private val userAddress = editor.getString("user_address", null) ?: ""

    private val _contracts = MutableLiveData<List<Contract>>()
    val contracts: LiveData<List<Contract>> get() = _contracts

    private val blockChainCalls = BlockChainCalls()
    private val contractCalls = ContractCalls()

    private val contractRepository = ContractRepository()


    fun loadContracts(){
        if (contractRepository.contractAddresses.value?.isNotEmpty() == true) {
            // If contracts are already loaded, no need to fetch again
            return
        }
        viewModelScope.launch {
            try {
                val contractAddresses = contractCalls.getInsuranceContractsByInsured(userAddress)
                for (address in contractAddresses){
                    val data = contractCalls.getContractVariables(address)
                    if (data["assicurato"].toString() != userAddress){
                        Log.wtf("loadContracts", "Contract $address is not associated with the user address $userAddress")
                    }
                    contractRepository.addContract(
                        address,
                        data["premio"] as UInt,
                        data["liquidato"] as Boolean,
                        data["attivato"] as Boolean,
                        data["funded"] as Boolean,
                        data["assicurato"] as String,
                        data["assicuratore"] as String
                    )
                }
            }
            catch (e: Exception) {
                Log.e("ContractsViewModel", "Error loading contract addresses", e)
            }
        }
    }
}