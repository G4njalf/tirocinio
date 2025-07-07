package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ContractDetailsBinding

class ContractDetailActivity : AppCompatActivity() {

    private lateinit var binding: ContractDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i("ContractDetailActivity", "onCreate called")
        super.onCreate(savedInstanceState)

        // Inizializza il View Binding
        binding = ContractDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recupera i dati dall'Intent
        val title = intent.getStringExtra("contractTitle")
        val address = intent.getStringExtra("contractAddress")

        Log.i("ContractDetailActivity", "Title: $title, Address: $address")

        // Imposta i dati nelle TextView
        binding.titleTextView.text = title ?: "Dettagli del Contratto"
        binding.addressTextView.text = address ?: "Indirizzo non disponibile"
    }
}