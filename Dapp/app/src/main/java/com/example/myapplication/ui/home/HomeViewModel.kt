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
import java.math.BigInteger


class HomeViewModel(application: Application) : AndroidViewModel(application){

    private val editor = application.getSharedPreferences("UserPrefs", MODE_PRIVATE)
    private val userRole = editor.getString("user_role", null)
    private val userAddress = editor.getString("user_address", null)

    private val _text2 = MutableLiveData<String>().apply {
        value = userRole
    }

    val text2: LiveData<String> = _text2

    private val contractCalls = ContractCalls()
    private val blockChainCalls = BlockChainCalls()


    fun getDataFromSepolia(){
        Log.d("Role", "User role is: $userRole , User address is: $userAddress")
        viewModelScope.launch {
            try {
                val balance = contractCalls.getTokenBalance(userAddress?: "")
                _text2.value = "Balance: $balance"
            }
            catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching token balance", e)
                _text2.value = "Error fetching balance"
            }
        }

    }

    fun mintTokens(){
        Log.d("HomeViewModel", "Minting tokens for user address: $userAddress")
        viewModelScope.launch {
            try {
                val hashmint = blockChainCalls.mintTokens(userAddress ?: "", BigInteger.valueOf(100000)) // Minting 100000 tokens
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




}