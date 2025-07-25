package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.databinding.ContractDetailsBinding
import com.example.myapplication.data.ContractCalls
import com.example.myapplication.data.BlockChainCalls
import kotlinx.coroutines.launch
import org.web3j.protocol.core.methods.response.TransactionReceipt
import java.math.BigInteger

class ContractDetailActivity : AppCompatActivity() {

    private lateinit var binding: ContractDetailsBinding

    private fun fetchContractDetails(contractAddress: String) {
        val contractCalls = ContractCalls()
        lifecycleScope.launch {
            try {
                val data = contractCalls.getContractVariables(contractAddress)
                val ass = data["premio"]
                Log.d("DetailActivity", "Fetched contract data: $ass")
                // Aggiorna la UI con i dati freschi
                binding.isLiquidatoTextView.text = if (data["liquidato"] as Boolean) "Liquidato" else "Non Liquidato"
                binding.isAttivatoTextView.text = if (data["attivato"] as Boolean) "Attivato" else "Non Attivato"
                binding.isFundendTextView.text = if (data["funded"] as Boolean) "Fondato" else "Non Fondato"
            } catch (e: Exception) {
                Log.e("ContractDetailActivity", "Errore durante il fetch dei dettagli aggiornati", e)
            }
        }
    }

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
        binding.addressTextView.text = "Contract Address : ${address}" ?: "Address not available"
        binding.premioTextView.text = "Premium : ${premio}" ?: "Premium not available"
        binding.isLiquidatoTextView.text = if (isLiquidato) "Liquidated" else "Not Liquidated"
        binding.isAttivatoTextView.text = if (isAttivato) "Activated" else "Not Activated"
        binding.isFundendTextView.text = if (isFundend) "Funded" else "Not Funded"
        binding.addressAssicuratoTextView.text = "Ensured Address : ${addressAssicurato}" ?: "Ensured Address not available"
        binding.addressAssicuratoreTextView.text = "Ensurer Address : ${addressAssicuratore}" ?: "Ensurer Address not available"



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
                    val approvehash = blockChainCalls.approveTokenTransfer(
                        addressAssicuratosafe,
                        addressContractSafe,
                        premiotoBigInteger,
                        userRole ?: "",
                        "0x8821aFDa84d71988cf0b570C535FC502720B33DD" // zonia token address
                    )
                    val recipt = blockChainCalls.waitForReceipt(approvehash)
                    if (recipt.status == "0x1") {
                        Log.d("ContractDetailActivity", "ZONIA Token transfer approved successfully")
                    } else {
                        Log.e("ContractDetailActivity", "ZONIA Token transfer approval failed")
                        progression.visibility = View.GONE
                        return@launch
                    }
                } catch (e: Exception) {
                    Log.e("ContractDetailActivity", "Error during ZONIA token transfer process", e)
                }
                try {
                    val zoniarequesthash = contractCalls.requestZoniaData(addressContractSafe,addressAssicuratosafe)
                    val recipt = blockChainCalls.waitForReceipt(zoniarequesthash)
                    Log.d("ContractDetailActivity", "Recipt zonia call: $recipt")
                    if (recipt.status == "0x1") {
                        Log.d("ContractDetailActivity", "Zonia request successful")

                        val requestId = contractCalls.getContractVariables(addressContractSafe)["requestId"] as? String
                        Toast.makeText(this@ContractDetailActivity, "Zonia request successful with ID: $requestId", Toast.LENGTH_LONG).show()
                    }
                    else {
                        Log.e("ContractDetailActivity", "Zonia request failed")
                        progression.visibility = View.GONE
                        return@launch
                    }
                }
                catch (e: Exception) {
                    Log.e("ContractDetailActivity", "Error during zonia request process", e)
                    progression.visibility = View.GONE
                    return@launch
                }
                try {
                    Toast.makeText(this@ContractDetailActivity, "Contract Eligible for liquidation", Toast.LENGTH_LONG).show()
                    val liquidatehash =
                        contractCalls.liquidateContract(addressContractSafe, addressAssicuratosafe)
                    val recipt = blockChainCalls.waitForReceipt(liquidatehash)
                    Log.d("ContractDetailActivity", "Recipt: $recipt")
                    if (recipt.status == "0x1") {
                        Log.d("ContractDetailActivity", "Contract liquidated successfully")
                    } else {
                        Log.e("ContractDetailActivity", "Contract liquidation failed")
                        progression.visibility = View.GONE
                        return@launch
                    }
                } catch (e: Exception) {
                    Log.e("ContractDetailActivity", "Error during liquidation process", e)
                    progression.visibility = View.GONE
                    return@launch
                }
                fetchContractDetails(addressContractSafe)
                progression.visibility = View.GONE
            }
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
                    val approvehash = blockChainCalls.approveTokenTransfer(addressAssicuratosafe,addressContractSafe,activateamountsafe,userRole?:"","0xF9f3AE879C612D35a8D1CAa67e178f190a4a215f")
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
                    Log.d("ContractDetailActivity", "Recipt: $recipt")
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
                fetchContractDetails(addressContractSafe)
                progression.visibility = View.GONE
            }
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
                        userRole ?: "",
                        "0xF9f3AE879C612D35a8D1CAa67e178f190a4a215f"
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
                    Log.d("ContractDetailActivity", "Recipt: $recipt")
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
                fetchContractDetails(addressContractSafe)
                progression.visibility = View.GONE
            }
        }
    }
}