package com.example.myapplication.ui.gallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import android.util.Log
import java.io.IOException

class GalleryViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is gallery Fragment"
    }
    val text: LiveData<String> = _text

    fun getFromMongo(){
        val client = OkHttpClient()
        val url = "http://192.168.1.15:8800/api/contracts/getContract"
        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("HTTP", "Errore nella chiamata: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let { responseBody ->
                    Log.d("HTTP", "Risposta ricevuta: $responseBody")
                    // TODO: puoi fare parsing JSON e usare abi + bytecode
                }
            }
        })

        /* allora da telefono fisico le richieste vanno fatte all ip del pc
        * entrambi devono essere connessi alla stessa rete!!
        * da telefono virtuale a 10.0.2.2, vanno abilitate le chiamate http
        * da network_security_config.xml*/
    }
}