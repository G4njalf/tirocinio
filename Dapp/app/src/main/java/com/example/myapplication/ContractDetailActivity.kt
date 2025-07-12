package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.databinding.ContractDetailsBinding
import com.example.myapplication.data.ContractCalls
import com.example.myapplication.data.BlockChainCalls
import kotlinx.coroutines.launch
import java.math.BigInteger

class ContractDetailActivity : AppCompatActivity() {

    private lateinit var binding: ContractDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i("ContractDetailActivity", "onCreate called")
        super.onCreate(savedInstanceState)

        // Inizializza il View Binding
        binding = ContractDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val contractCalls = ContractCalls()
        val blockChainCalls = BlockChainCalls()

        val editor = application.getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val userRole = editor.getString("user_role", null)
        val userAddress = editor.getString("user_address", null)

        val activateBtn = binding.activateContractbtn
        val liquidateBtn = binding.liquidateContractBtn
        val fundBtn = binding.fundContractbtn
        val progression = binding.progressBarDetail
        progression.visibility= View.GONE

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
        binding.addressTextView.text = "Indirizzo del contratto : ${address}" ?: "Indirizzo non disponibile"
        binding.premioTextView.text = "Premio : ${premio}" ?: "Premio non disponibile"
        binding.isLiquidatoTextView.text = if (isLiquidato) "Liquidato" else "Non Liquidato"
        binding.isAttivatoTextView.text = if (isAttivato) "Attivato" else "Non Attivato"
        binding.isFundendTextView.text = if (isFundend) "Fondato" else "Non Fondato"
        binding.addressAssicuratoTextView.text = "Indirizzo assicurato : ${addressAssicurato}" ?: "Indirizzo Assicurato non disponibile"
        binding.addressAssicuratoreTextView.text = "Indirizzo assicuratore : ${addressAssicuratore}" ?: "Indirizzo Assicuratore non disponibile"



        if (userRole == "cliente") {
            activateBtn.visibility = if (!isAttivato and isFundend) View.VISIBLE else View.GONE
            liquidateBtn.visibility = if(!isLiquidato and isAttivato and isFundend) View.VISIBLE else View.GONE
            fundBtn.visibility = View.GONE
        }
        else if (userRole == "assicuratore") {
            activateBtn.visibility = View.GONE
            liquidateBtn.visibility = View.GONE
            fundBtn.visibility = if (!isFundend) View.VISIBLE else View.GONE
        } else {
            activateBtn.visibility = View.GONE
            liquidateBtn.visibility = View.GONE
            fundBtn.visibility = View.GONE
        }

        // Gestione dei click sui bottoni

        binding.liquidateContractBtn.setOnClickListener {
            Log.i("ContractDetailActivity", "Liquidate button clicked")
            val addressAssicuratosafe = addressAssicurato ?: ""
            val premiotoBigInteger = premio?.toBigIntegerOrNull() ?: BigInteger.ZERO
            val addressContractSafe = address ?: ""
            if (addressAssicuratosafe.isEmpty() || premiotoBigInteger == BigInteger.ZERO
                || addressContractSafe.isEmpty()) {
                Log.wtf("ContractDetailActivity", "Invalid addresses or premio")
                return@setOnClickListener
            }
            lifecycleScope.launch {
                progression.visibility = View.VISIBLE
                try {
                    val liquidatehash = contractCalls.liquidateContract(addressContractSafe, addressAssicuratosafe)
                    val recipt = blockChainCalls.waitForReceipt(liquidatehash)
                    if (recipt.status == "0x1") {
                        Log.d("ContractDetailActivity", "Contract liquidated successfully")
                    } else {
                        Log.e("ContractDetailActivity", "Contract liquidation failed")
                        progression.visibility = View.GONE
                        return@launch
                    }
                }
                catch (e: Exception) {
                    Log.e("ContractDetailActivity", "Error during liquidation process", e)
                    progression.visibility = View.GONE
                }
            }
            progression.visibility = View.GONE
        }

        binding.activateContractbtn.setOnClickListener {
            Log.i("ContractDetailActivity", "Activate button clicked")
            val addressAssicuratosafe = addressAssicurato ?: ""
            val premiotoBigInteger = premio?.toBigIntegerOrNull() ?: BigInteger.ZERO
            val activateamountsafe = premiotoBigInteger * BigInteger.valueOf(5) / BigInteger.valueOf(100)
            val addressContractSafe = address ?: ""
            lifecycleScope.launch {
                progression.visibility = View.VISIBLE
                try{
                    val approvehash = blockChainCalls.approveTokenTransfer(addressAssicuratosafe,addressContractSafe,activateamountsafe,userRole?:"")
                    val recipt = blockChainCalls.waitForReceipt(approvehash)
                    if (recipt.status == "0x1") {
                        Log.d("ContractDetailActivity", "Token transfer approved successfully")
                    } else {
                        Log.e("ContractDetailActivity", "Token transfer approval failed")
                        progression.visibility = View.GONE
                        return@launch
                    }
                }
                catch (e: Exception) {
                    Log.e("ContractDetailActivity", "Error during activation process", e)
                }
                try {
                    val activateHash = contractCalls.activateContract(addressContractSafe,addressAssicuratosafe)
                    val recipt = blockChainCalls.waitForReceipt(activateHash)
                    if (recipt.status == "0x1") {
                        Log.d("ContractDetailActivity", "Contract activated successfully")
                    } else {
                        Log.e("ContractDetailActivity", "Contract activation failed")
                        progression.visibility = View.GONE
                        return@launch
                    }
                }
                catch (e: Exception) {
                    Log.e("ContractDetailActivity", "Error during activation process", e)
                    progression.visibility = View.GONE
                }
            }
            progression.visibility = View.GONE
        }


        binding.fundContractbtn.setOnClickListener {
            Log.i("ContractDetailActivity", "Found button clicked")
            val addressAssicuratoresafe = addressAssicuratore ?: ""
            val premiotoBigInteger = premio?.toBigIntegerOrNull() ?: BigInteger.ZERO
            val addressContractSafe = address ?: ""
            Log.d("ContractDetailActivity", "Assicuratore: $addressAssicuratoresafe, Premio: $premiotoBigInteger, Contract: $addressContractSafe")
            if (addressAssicuratoresafe.isEmpty() || premiotoBigInteger == BigInteger.ZERO
                || addressContractSafe.isEmpty()
            ) {
                Log.wtf("ContractDetailActivity", "Invalid addresses or premio")
                return@setOnClickListener
            }
            lifecycleScope.launch {
                progression.visibility = View.VISIBLE
                try {
                    val approvehash = blockChainCalls.approveTokenTransfer(
                        addressAssicuratoresafe,
                        addressContractSafe,
                        premiotoBigInteger,
                        userRole ?: ""
                    )
                    val recipt = blockChainCalls.waitForReceipt(approvehash)
                    if (recipt.status == "0x1") {
                        Log.d("ContractDetailActivity", "Token transfer approved successfully")
                    } else {
                        Log.e("ContractDetailActivity", "Token transfer approval failed")
                        progression.visibility = View.GONE
                        return@launch
                    }
                } catch (e: Exception) {
                    Log.e("ContractDetailActivity", "Error during funding process", e)
                }
                try {
                    val fundhash = contractCalls.fundContract(addressContractSafe)
                    val recipt = blockChainCalls.waitForReceipt(fundhash)
                    if (recipt.status == "0x1") {
                        Log.d("ContractDetailActivity", "Contract funded successfully")
                    } else {
                        Log.e("ContractDetailActivity", "Contract funding failed with recipt: $recipt")
                        progression.visibility = View.GONE
                        return@launch
                    }
                } catch (e: Exception) {
                    Log.e("ContractDetailActivity", "Error during funding process", e)
                    progression.visibility = View.GONE
                }
            }
            progression.visibility = View.GONE
        }
    }
}