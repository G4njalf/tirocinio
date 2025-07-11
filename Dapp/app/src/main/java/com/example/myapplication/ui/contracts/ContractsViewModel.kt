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
import java.math.BigInteger


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
            _contracts.value = contractRepository.contractData.value
            return
        }
        viewModelScope.launch {
            try {
                val contractAddresses: List<String> = when (userRole) {
                    "cliente" -> contractCalls.getInsuranceContractsByInsured(userAddress)
                    "assicuratore" -> contractCalls.getAllInsuranceContracts()
                    else -> emptyList()
                }
                val contractList = mutableListOf<Contract>()
                for (address in contractAddresses){
                    val data = contractCalls.getContractVariables(address)
                    Log.d("data", "Contract data for $address: $data")
                    if (data["assicurato"].toString() != userAddress.lowercase() && (userRole == "cliente")){ // normalizzo userAddress perche me li da tutto minuscolo dall bc
                        Log.wtf("loadContracts", "Contract $address is not associated with the user address $userAddress")
                    }
                    val version = data["version"]?.toString() ?: "unknown"
                    if (version != "0.1" && false) { // bypasso il controllo della versione per ora
                        Log.i("loadContracts", "Contract $address is not version 0.1, skipping")
                        continue // skip contracts that are not version 1.0
                    }
                    contractRepository.addContract(
                        address,
                        (data["premio"] as BigInteger).toInt().toUInt(),
                        data["liquidato"] as Boolean,
                        data["attivato"] as Boolean,
                        data["funded"] as Boolean,
                        data["assicurato"] as String,
                        data["assicuratore"] as String
                    )
                    contractList.add(
                        Contract(
                            address,
                            (data["premio"] as BigInteger).toInt().toUInt(),
                            data["liquidato"] as Boolean,
                            data["attivato"] as Boolean,
                            data["funded"] as Boolean,
                            data["assicurato"] as String,
                            data["assicuratore"] as String
                        )
                    )
                }
                _contracts.postValue(contractList) // prendo i dati dalla copia locale per evitare che la repo non sia ancora aggioranta
            }
            catch (e: Exception) {
                Log.e("ContractsViewModel", "Error loading contract addresses", e)
            }
        }
    }
}