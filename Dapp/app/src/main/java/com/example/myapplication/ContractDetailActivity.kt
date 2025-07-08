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
        val address = intent.getStringExtra("contractAddress")
        val premio = intent.getStringExtra("contractPremio")
        val isLiquidato = intent.getBooleanExtra("contractIsLiquidato", false)
        val isAttivato = intent.getBooleanExtra("contractIsAttivato", false)
        val isFundend = intent.getBooleanExtra("contractIsFundend", false)
        val addressAssicurato = intent.getStringExtra("contractAddressAssicurato")
        val addressAssicuratore = intent.getStringExtra("contractAddressAssicuratore")

        Log.i("ContractDetailActivity", "Title: $title, Address: $address")

        // Imposta i dati nelle TextView
        binding.addressTextView.text = address ?: "Indirizzo non disponibile"
        binding.premioTextView.text = premio ?: "Premio non disponibile"
        binding.isLiquidatoTextView.text = if (isLiquidato) "Liquidato" else "Non Liquidato"
        binding.isAttivatoTextView.text = if (isAttivato) "Attivato" else "Non Attivato"
        binding.isFundendTextView.text = if (isFundend) "Fondato" else "Non Fondato"
        binding.addressAssicuratoTextView.text = addressAssicurato ?: "Indirizzo Assicurato non disponibile"
        binding.addressAssicuratoreTextView.text = addressAssicuratore ?: "Indirizzo Assicuratore non disponibile"
        
    }
}