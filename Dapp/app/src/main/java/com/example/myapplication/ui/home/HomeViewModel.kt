package com.example.myapplication.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.data.ContractRepository
import com.example.myapplication.data.ContractCalls
import android.util.Log
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.myapplication.data.BlockChainCalls
import com.example.myapplication.ui.contracts.Contract
import java.math.BigInteger


class HomeViewModel(application: Application) : AndroidViewModel(application){

    private val editor = application.getSharedPreferences("UserPrefs", MODE_PRIVATE)
    private val userRole = editor.getString("user_role", null)

    private fun getUserAddress(): String {
        val prefs = getApplication<Application>().getSharedPreferences("UserPrefs", MODE_PRIVATE)
        return prefs.getString("user_address", "") ?: ""
    }

    private val _totalLiquidated = MutableLiveData<Int>().apply {
        value = 0
    }

    val totalLiquidated: LiveData<Int> = _totalLiquidated

    private val _userRoleText = MutableLiveData<String>().apply {
        value = userRole
    }

    val userRoleText: LiveData<String> = _userRoleText

    private val _text2 = MutableLiveData<String>().apply {
        value = "Token Balance: fetching..."
    }

    val text2: LiveData<String> = _text2

    private val contractCalls = ContractCalls()
    private val blockChainCalls = BlockChainCalls()

    private val _contracts = MutableLiveData<List<Contract>>()
    val contracts: LiveData<List<Contract>> get() = _contracts

    private val contractRepository = ContractRepository()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading


    fun getDataFromSepolia(){
        Log.d("Role", "User role is: $userRole , User address is: ${getUserAddress()}")
        viewModelScope.launch {
            try {
                val balance = contractCalls.getTokenBalance(getUserAddress()?: "")
                _text2.value = "Token Balance: $balance MTK"
            }
            catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching token balance", e)
                _text2.value = "Error fetching balance"
            }
        }

    }

    fun mintTokens(){
        Log.d("HomeViewModel", "Minting tokens for user address: ${getUserAddress()}")
        viewModelScope.launch {
            try {
                val hashmint = blockChainCalls.mintTokens(getUserAddress() ?: "", BigInteger.valueOf(100000)) // Minting 100000 tokens
                val recipt = blockChainCalls.waitForReceipt(hashmint)
                if (recipt.status == "0x1") {
                    Log.d("HomeViewModel", "Tokens minted successfully: $hashmint")
                } else {
                    Log.e("HomeViewModel", "Token minting failed: $hashmint")
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error minting tokens", e)
            }
        }
    }

    fun loadContracts(){
        Log.d("HomeViewModel","user address load contracts :"+getUserAddress())
        _totalLiquidated.value = 0
        _isLoading.value  = true
        if (contractRepository.contractData.value?.isNotEmpty() == true && false) {
            // If contracts are already loaded, no need to fetch again
            // secondo me non serve perche e meglio controllare sempre se ci sono nuovi contratti
            _contracts.value = contractRepository.contractData.value
            Log.d("ContractsViewModel", "Contracts already loaded, skipping fetch")
            _isLoading.value = false
            return
        }
        viewModelScope.launch {
            try {
                val contractAddresses: List<String> = when (userRole) {
                    "cliente" -> contractCalls.getInsuranceContractsByInsured(getUserAddress(),getUserAddress())
                    "assicuratore" -> contractCalls.getAllInsuranceContracts(getUserAddress())
                    else -> emptyList()
                }
                val contractList = mutableListOf<Contract>()
                for (address in contractAddresses){
                    val data = contractCalls.getContractVariables(getUserAddress(),address)
                    Log.d("data", "Contract data for $address: $data")
                    if (data["assicurato"].toString() != getUserAddress().lowercase() && (userRole == "cliente")){ // normalizzo userAddress perche me li da tutto minuscolo dall bc
                        Log.wtf("loadContracts", "Contract $address is not associated with the user address ${getUserAddress()}")
                    }
                    val version = data["version"]?.toString() ?: "unknown"
                    if (version != "0.1" && false) { // bypasso il controllo della versione per ora
                        Log.i("loadContracts", "Contract $address is not version 0.1, skipping")
                        continue // skip contracts that are not version 1.0
                    }
                    // mi salvo la somma dei premi dei contratti liquidati
                    if(data["liquidato"] as Boolean){
                        val premio = (data["premio"] as BigInteger).toInt()
                        _totalLiquidated.value = (_totalLiquidated.value ?: 0) + premio
                    }
                    contractRepository.addContract(
                        address,
                        (data["premio"] as BigInteger).toInt().toUInt(),
                        data["liquidato"] as Boolean,
                        data["attivato"] as Boolean,
                        data["funded"] as Boolean,
                        data["assicurato"] as String,
                        data["assicuratore"] as String,
                        data["requestId"] as String
                    )
                    contractList.add(
                        Contract(
                            address,
                            (data["premio"] as BigInteger).toInt().toUInt(),
                            data["liquidato"] as Boolean,
                            data["attivato"] as Boolean,
                            data["funded"] as Boolean,
                            data["assicurato"] as String,
                            data["assicuratore"] as String,
                            data["requestId"] as String
                        )
                    )
                }
                _contracts.postValue(contractList) // prendo i dati dalla copia locale per evitare che la repo non sia ancora aggioranta
            }
            catch (e: Exception) {
                Log.e("HomeViewModel", "Error loading contract addresses", e)
                _contracts.postValue(emptyList())
            }
            _isLoading.value = false
        }
    }




}