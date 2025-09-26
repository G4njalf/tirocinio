package com.example.myapplication.ui.contract_detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.data.BlockChainCalls
import com.example.myapplication.data.ContractCalls
import com.example.myapplication.databinding.FragmentContractDetailsBinding
import com.example.myapplication.databinding.FragmentHomeBinding
import com.example.myapplication.ui.chain_params.ChainParamsViewModel
import com.example.myapplication.ui.contract_detail.ContractDetailViewModel
import kotlinx.coroutines.launch
import java.math.BigInteger

class ContractDetailFragment : Fragment() {

    private var _binding: FragmentContractDetailsBinding? = null



    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?) : View {

        val args = ContractDetailFragmentArgs.fromBundle(requireArguments())

        val contractDetailViewModel =
            ViewModelProvider(this).get(ContractDetailViewModel::class.java)

        _binding = FragmentContractDetailsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        Log.i("ContractDetailActivity", "onCreate called")

        val contractCalls = ContractCalls()
        val blockChainCalls = BlockChainCalls()

        val usrAddress = contractDetailViewModel.userAddress

        val activateBtn = binding.activateContractbtn
        val liquidateBtn = binding.liquidateContractBtn
        val fundBtn = binding.fundContractbtn
        val progression = binding.progressBarDetail
        progression.visibility= View.GONE

        // Recupera i dati con safe args
        val address = args.contractAddress
        val premio = args.contractPremio
        val isLiquidato = args.contractIsLiquidato
        val isAttivato = args.contractIsAttivato
        val isFundend = args.contractIsFundend
        val addressAssicurato = args.contractAddressAssicurato
        val addressAssicuratore = args.contractAddressAssicuratore
        val requestId = args.contractRequestId

        Log.i("ContractDetailActivity", "Address: $address")

        // Imposta i dati nelle TextView
        binding.addressTextView.text = "Contract Address : $address" ?: "Address not available"
        binding.premioTextView.text = "Premium : $premio" ?: "Premium not available"
        binding.isLiquidatoTextView.text = if (isLiquidato) "Liquidated" else "Not Liquidated"
        binding.isAttivatoTextView.text = if (isAttivato) "Activated" else "Not Activated"
        binding.isFundendTextView.text = if (isFundend) "Funded" else "Not Funded"
        binding.addressAssicuratoTextView.text = "Client Address : $addressAssicurato" ?: "Ensured Address not available"
        binding.addressAssicuratoreTextView.text = "Ensurer Address : $addressAssicuratore" ?: "Ensurer Address not available"
        binding.requestIdTextView.text = "Request ID : $requestId" ?: "Request ID not available"


        if (contractDetailViewModel.userRole == "cliente") {
            activateBtn.visibility = if (!isAttivato and isFundend) View.VISIBLE else View.GONE
            liquidateBtn.visibility = if(!isLiquidato and isAttivato and isFundend) View.VISIBLE else View.GONE
            fundBtn.visibility = View.GONE
        }
        else if (contractDetailViewModel.userRole == "assicuratore") {
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
                progression.progress = 10
                try {
                    val approvehash = blockChainCalls.approveTokenTransfer(
                        addressAssicuratosafe,
                        addressContractSafe,
                        premiotoBigInteger,
                        contractDetailViewModel.userRole ?: "",
                        "0x8821aFDa84d71988cf0b570C535FC502720B33DD" // zonia token address
                    )
                    progression.progress = 25
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

                        val requestId = contractCalls.getContractVariables(usrAddress,addressContractSafe)["requestId"] as? String
                        Toast.makeText(requireContext(), "Zonia request successful with ID: $requestId", Toast.LENGTH_LONG).show()
                    }
                    else {
                        Log.e("ContractDetailActivity", "Zonia request failed")
                        progression.visibility = View.GONE
                        return@launch
                    }
                    progression.progress = 50
                }
                catch (e: Exception) {
                    Log.e("ContractDetailActivity", "Error during zonia request process", e)
                    progression.visibility = View.GONE
                    return@launch
                }
                try {
                    Toast.makeText(requireContext(), "Contract Eligible for liquidation", Toast.LENGTH_LONG).show()
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
                    progression.progress = 75
                } catch (e: Exception) {
                    Log.e("ContractDetailActivity", "Error during liquidation process", e)
                    progression.visibility = View.GONE
                    return@launch
                }
                contractDetailViewModel.fetchContractDetails(addressContractSafe)
                contractDetailViewModel.isLiquidato.observe(viewLifecycleOwner){ value ->
                    binding.isLiquidatoTextView.text = if (value) "Liquidated" else "Not Liquidated"
                }
                contractDetailViewModel.isAttivato.observe(viewLifecycleOwner){ value ->
                    binding.isAttivatoTextView.text = if (value) "Activated" else "Not Activated"
                }
                contractDetailViewModel.isFunded.observe(viewLifecycleOwner){ value ->
                    binding.isFundendTextView.text = if (value) "Funded" else "Not Funded"
                }
                progression.progress = 100
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
                progression.progress = 10
                try{
                    val approvehash = blockChainCalls.approveTokenTransfer(
                        addressAssicuratosafe,
                        addressContractSafe,
                        activateamountsafe,
                        contractDetailViewModel.userRole?:"",
                        "0xF9f3AE879C612D35a8D1CAa67e178f190a4a215f")
                    Log.d("ContractDetailActivity", "Approve hash: $approvehash")
                    val recipt = blockChainCalls.waitForReceipt(approvehash)
                    if (recipt.status == "0x1") {
                        Log.d("ContractDetailActivity", "Token transfer approved successfully")
                    } else {
                        Log.e("ContractDetailActivity", "Token transfer approval failed")
                        progression.visibility = View.GONE
                        return@launch
                    }
                    progression.progress = 50
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
                    progression.progress = 75
                }
                catch (e: Exception) {
                    Log.e("ContractDetailActivity", "Error during activation process", e)
                    progression.visibility = View.GONE
                }
                contractDetailViewModel.fetchContractDetails(addressContractSafe)
                progression.progress = 100
                progression.visibility = View.GONE
            }
        }


        binding.fundContractbtn.setOnClickListener {

            val prefs = requireContext().getSharedPreferences("chain_params_prefs", MODE_PRIVATE)
            val chp1 = prefs.getInt("chp1", 0)
            val chp2 = prefs.getInt("chp2", 0)
            val chp3 = prefs.getInt("chp3", 0)
            val chp4 = prefs.getInt("chp4", 0)

            Log.i("foundContractbtn", "chp1: $chp1, chp2: $chp2, chp3: $chp3, chp4: $chp4")

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
                progression.progress = 10
                try {
                    val approvehash = blockChainCalls.approveTokenTransfer(
                        addressAssicuratoresafe,
                        addressContractSafe,
                        premiotoBigInteger,
                        contractDetailViewModel.userRole ?: "",
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
                    progression.progress = 50
                } catch (e: Exception) {
                    Log.e("ContractDetailActivity", "Error during funding process", e)
                }
                try {
                    val fundhash = contractCalls.fundContract(usrAddress,addressContractSafe,chp1,chp2,chp3,chp4)
                    val recipt = blockChainCalls.waitForReceipt(fundhash)
                    Log.d("ContractDetailActivity", "Recipt: $recipt")
                    if (recipt.status == "0x1") {
                        Log.d("ContractDetailActivity", "Contract funded successfully")
                    } else {
                        Log.e("ContractDetailActivity", "Contract funding failed with recipt: $recipt")
                        progression.visibility = View.GONE
                        return@launch
                    }
                    progression.progress = 75
                } catch (e: Exception) {
                    Log.e("ContractDetailActivity", "Error during funding process", e)
                    progression.visibility = View.GONE
                }
                contractDetailViewModel.fetchContractDetails(addressContractSafe)
                progression.progress = 100

                progression.visibility = View.GONE
            }
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}