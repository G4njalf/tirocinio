package com.example.myapplication.ui.contract_detail

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.ContractCalls
import kotlinx.coroutines.launch

class ContractDetailViewModel(application: Application) : AndroidViewModel(application) {

    val editor = application.getSharedPreferences("UserPrefs", MODE_PRIVATE)
    val userRole = editor.getString("user_role", null)
    val userAddress = editor.getString("user_address" , null) ?: ""

    private val _userRoleText = MutableLiveData<String>().apply {
        value = userRole
    }

    val isLiquidato = MutableLiveData<Boolean>()
    val isAttivato = MutableLiveData<Boolean>()
    val isFunded = MutableLiveData<Boolean>()


    fun fetchContractDetails(contractAddress: String) {
        val contractCalls = ContractCalls()
        viewModelScope.launch {
            try {
                val data = contractCalls.getContractVariables(userAddress,contractAddress)
                // Aggiorna la UI con i dati freschi
                isLiquidato.postValue(data["liquidato"] as Boolean)
                isAttivato.postValue(data["attivato"] as Boolean)
                isFunded.postValue(data["funded"] as Boolean)
            } catch (e: Exception) {
                Log.e("ContractDetailActivity", "Errore durante il fetch dei dettagli aggiornati", e)
            }
        }
    }

    fun activateContract(){

    }

    fun foundContract(){

    }

    fun liquidateContract(){

    }

}