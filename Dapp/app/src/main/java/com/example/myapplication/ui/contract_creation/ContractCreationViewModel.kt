package com.example.myapplication.ui.contract_creation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Log
import com.example.myapplication.data.BlockChainCalls
import com.example.myapplication.data.ContractCalls
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.web3j.abi.datatypes.generated.Uint256

class ContractCreationViewModel : ViewModel() {

    private val blockChainCalls = BlockChainCalls()
    private val contractCalls = ContractCalls()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    // Function to create a contract [poi chiamo getallContracts per prendere tutti i contratti e ritorno l ultimo]
    // funziona se sono l unico alla volta che crea un contratto
    fun createContract(address: String, premio: UInt) {
        _isLoading.value = true
        Log.d("ContractCreationViewModel", "Creating contract for: ${address}, Premio: $premio")
        if (premio <= 0u) {
            Log.e("ContractCreationViewModel", "Premio must be greater than zero")
            _isLoading.value = false
            return
        }
        viewModelScope.launch {
            var isValid = false
            var contractAddress = ""
            var txHash = ""
            try {
                isValid = blockChainCalls.isWalletAddressValid(address)
                Log.d("ContractCreationViewModel", "Address validation result: $isValid")
            }
            catch (e: Exception) {
                _isLoading.value = false
                Log.e("ContractCreationViewModel", "Error validating address: $address", e)
                return@launch
            }
            if (isValid) {
                try {
                    txHash = contractCalls.createNewContract(address, Uint256(premio.toLong()))
                    Log.d("ContractCreationViewModel", "Contract created transaction Hash: $txHash")

                } catch (e: Exception) {
                    _isLoading.value = false
                    Log.e("ContractCreationViewModel", "Error creating contract", e)
                }
                try {
                    val receipt = blockChainCalls.waitForReceipt(txHash)
                    if (receipt.status == "0x1") {
                        Log.d("ContractCreationViewModel", "Transaction successful: $txHash")
                        contractAddress = contractCalls.getAllInsuranceContracts().lastOrNull() ?: ""
                        Log.d("ContractCreationViewModel", "Last contract address: $contractAddress")
                    } else {
                        Log.e("ContractCreationViewModel", "Transaction failed: $txHash")
                    }
                }
                catch (e: Exception) {
                    _isLoading.value = false
                    Log.e("ContractCreationViewModel", "Error fetching last contract address", e)
                }
                try {
                    val contractData = contractCalls.getContractVariables(contractAddress)
                    Log.d("ContractCreationViewModel", "Contract data: $contractData")
                }
                catch (e: Exception) {
                    _isLoading.value = false
                    Log.e("ContractCreationViewModel", "Error fetching contract data", e)
                }
            } else {
                Log.e("ContractCreationViewModel", "Invalid address: $address")
            }
            _isLoading.value = false
        }
    }



}