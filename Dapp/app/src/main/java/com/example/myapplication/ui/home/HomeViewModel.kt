package com.example.myapplication.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.data.CounterRepository
import com.fasterxml.jackson.annotation.JsonView
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.FunctionReturnDecoder
import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.Function
import org.web3j.abi.datatypes.Type
import org.web3j.abi.datatypes.Utf8String
import org.web3j.abi.datatypes.Address
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.request.Transaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.util.Log


class HomeViewModel(application: Application) : AndroidViewModel(application){


    private val _text2 = MutableLiveData<String>().apply {
        value = "ciao"
    }

    val text2: LiveData<String> = _text2

    /* provo a interagire con il nodo infura del mio contratto lottery */
    private val infuraurl = "https://sepolia.infura.io/v3/3e885576a998490992a7cdaa69e2ed2f"
    val web3 = Web3j.build(HttpService(infuraurl))
    val contractAddress = "0xf54521AAa69F19D6Fd2F1ccfeDd469DEE00dC1F3"
    val myAddress = "0x8C6b618aC0b1E69FA7FF02Ec2a8EB6caDC29bc86"

    fun fetchManagerAddress() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val function = Function(
                    "manager",
                    emptyList(),
                    listOf(object : TypeReference<Address>() {})
                )

                val encodedFunction = FunctionEncoder.encode(function)

                val ethCall = web3.ethCall(
                    Transaction.createEthCallTransaction(
                        myAddress,
                        contractAddress,
                        encodedFunction
                    ),
                    DefaultBlockParameterName.LATEST
                ).send()

                val output = FunctionReturnDecoder.decode(ethCall.result, function.outputParameters)
                val value = output[0].value as String

                withContext(Dispatchers.Main) {
                    // Aggiorna l'interfaccia utente con il valore ottenuto
                    _text2.value = "Manager address: $value"
                    Log.d("HomeViewModel", "Manager address: $value")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    // Gestisci l'errore
                    Log.e("HomeViewModel", "Errore: ${e.message}")
                    _text2.value = "Errore: ${e.message}"
                }
            }
        }
    }


    private val repository = CounterRepository(application.applicationContext)
    private var counter = repository.getCounter()





    private val _text = MutableLiveData<String>().apply {
        value = "Hai cliccato $counter volte"
    }
    val text: LiveData<String> = _text

    fun incrementaContatore() {
        counter++
        repository.setCounter(counter)
        _text.value = "Hai cliccato $counter volte"

    }

    fun resetContatore(){
        counter = 0
        repository.resetCounter()
        _text.value = "Hai cliccato $counter volte"
        fetchManagerAddress()
    }
}