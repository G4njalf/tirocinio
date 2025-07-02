package com.example.myapplication.data

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
import org.bouncycastle.util.test.FixedSecureRandom.BigInteger
import org.web3j.abi.datatypes.Bool
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.abi.datatypes.DynamicArray

class DynamicAddressArray(addresses: List<Address>) : DynamicArray<Address>(Address::class.java, addresses)

private val infuraurl ="https://sepolia.infura.io/v3/3e885576a998490992a7cdaa69e2ed2f"
private val web3 = Web3j.build(HttpService(infuraurl))
private val myAddress = "0x8C6b618aC0b1E69FA7FF02Ec2a8EB6caDC29bc86"
private val tokenAddress = "0xF9f3AE879C612D35a8D1CAa67e178f190a4a215f"
private val factoryAddress = "0xa133327Baa93455433bC8Ea547B77704f84042a7"
private val privateKeyAssicuratore = "REMOVED" // non ci sono soldi veri non rubatemi i fondi :D
private val privateKeyAssicurato = "REMOVED" // non ci sono soldi veri non rubatemi i fondi :D

class BlockChainCalls{
    // Function to verify if the address is a valid Ethereum Sepolia address

    suspend fun isWalletAddressValid(address: String): Boolean = withContext(Dispatchers.IO) {
        Log.d("BlockChainCalls", "Checking if address is valid: $address")
        if (!address.matches(Regex("^0x[a-fA-F0-9]{40}$"))) {
            Log.e("BlockChainCalls", "Invalid Ethereum address format: $address")
            return@withContext false
        }
        return@withContext try {
            Log.d("BlockChainCalls", "Fetching balance for address: $address")
            val balance = web3.ethGetBalance(address, DefaultBlockParameterName.LATEST)
                .send()
                .balance
            Log.d("BlockChainCalls", "Balance for address $address: $balance")
            true
        } catch (e: Exception) {
            Log.e("BlockChainCalls", "Error fetching balance for address: $address", e)
            false
        }
    }
}


class ContractCalls {

    // Function to get the token balance of the user's address

    suspend fun getTokenBalance(): String = withContext(Dispatchers.IO) {
        val function = Function(
            "balanceOf",
            listOf(Address(myAddress)),
            listOf(TypeReference.create(org.web3j.abi.datatypes.generated.Uint256::class.java))
        )

        val encodedFunction = FunctionEncoder.encode(function)

        val response = web3.ethCall(
            Transaction.createEthCallTransaction(myAddress, tokenAddress, encodedFunction),
            DefaultBlockParameterName.LATEST
        ).send()

        val decoded = FunctionReturnDecoder.decode(response.value, function.outputParameters)

        return@withContext decoded[0].value.toString()
    }

    // Function to create a new insurance contract from the factory contract

    suspend fun createNewContract(addrAssicurato: String, premio: Uint256): String = withContext(Dispatchers.IO){

        val credentials = org.web3j.crypto.Credentials.create(privateKeyAssicuratore)


        val function = Function(
            "createInsurance",
            listOf(
                Address(addrAssicurato),
                org.web3j.abi.datatypes.generated.Uint256(premio.value)
                ), // inputs
            listOf(
                TypeReference.create(org.web3j.abi.datatypes.Address::class.java) // output type
            )
        )

        val encodedFunction = FunctionEncoder.encode(function)

        val response = web3.ethCall(
            Transaction.createEthCallTransaction(myAddress, factoryAddress, encodedFunction),
            DefaultBlockParameterName.LATEST
        ).send()

        val decoded = FunctionReturnDecoder.decode(response.value, function.outputParameters)

        return@withContext decoded[0].value.toString()
    }

    // Function to get the address of the insurance contract created by the factory contract

    suspend fun getAllInsuranceContracts(): List<String> = withContext(Dispatchers.IO) {
        val function = Function(
            "getAllInsuranceContracts",
            emptyList(), // no inputs
            listOf(TypeReference.create(DynamicAddressArray::class.java)) // output type
        )

        val encodedFunction = FunctionEncoder.encode(function)

        val response = web3.ethCall(
            Transaction.createEthCallTransaction(myAddress, factoryAddress, encodedFunction),
            DefaultBlockParameterName.LATEST
        ).send()

        val decoded = FunctionReturnDecoder.decode(response.value, function.outputParameters)
        val addressList = decoded[0].value as List<*>
        return@withContext addressList
            .map { it as Address }
            .map { it.value }

    }

    // Function to get the variables of a specific insurance contract

    suspend fun getContractVariables(contractAddress: String): Map<String, Any> = withContext(Dispatchers.IO) {
        val assicuratoreFunction = Function(
            "assicuratore",
            emptyList(),
            listOf(TypeReference.create(Address::class.java))
        )

        val assicuratoFunction = Function(
            "assicurato",
            emptyList(),
            listOf(TypeReference.create(Address::class.java))
        )

        val premioFunction = Function(
            "premio",
            emptyList(),
            listOf(TypeReference.create(Uint256::class.java))
        )

        val liquidatoFunction = Function(
            "liquidato",
            emptyList(),
            listOf(TypeReference.create(org.web3j.abi.datatypes.Bool::class.java))
        )

        val attivatoFunction = Function(
            "attivato",
            emptyList(),
            listOf(TypeReference.create(org.web3j.abi.datatypes.Bool::class.java))
        )

        val fundedFunction = Function(
            "funded",
            emptyList(),
            listOf(TypeReference.create(org.web3j.abi.datatypes.Bool::class.java))
        )

        val tokenFunction = Function(
            "token",
            emptyList(),
            listOf(TypeReference.create(Address::class.java))
        )

        val functions = listOf(
            "assicuratore" to assicuratoreFunction,
            "assicurato" to assicuratoFunction,
            "premio" to premioFunction,
            "liquidato" to liquidatoFunction,
            "attivato" to attivatoFunction,
            "funded" to fundedFunction,
            "token" to tokenFunction
        )

        val results = mutableMapOf<String, Any>()

        for ((name, function) in functions) {
            val encodedFunction = FunctionEncoder.encode(function)
            val response = web3.ethCall(
                Transaction.createEthCallTransaction(myAddress, contractAddress, encodedFunction),
                DefaultBlockParameterName.LATEST
            ).send()

            val decoded = FunctionReturnDecoder.decode(response.value, function.outputParameters)
            if (decoded.isNotEmpty()) {
                results[name] = decoded[0].value
            } else {
                Log.e("ContractCalls", "Decoded response for function $name is empty")
            }
        }

        return@withContext results
    }

    // Function to fund the insurance contract ( l assicuratore deve inviare i fondi al contratto)

    suspend fun fundContract(contractAddress: String): Boolean = withContext(Dispatchers.IO){
        val function = Function(
            "fundContract",
            emptyList(),
            emptyList() // output type
        )
        val encodedFunction = FunctionEncoder.encode(function)
        val response = web3.ethCall(
            Transaction.createEthCallTransaction(myAddress, contractAddress, encodedFunction),
            DefaultBlockParameterName.LATEST
        ).send()

        return@withContext true
    }

    // Function to pay the ensured

    suspend fun liquidateContract(contractAddress: String) : Boolean = withContext(Dispatchers.IO) {
        val function = Function(
            "liquidazione",
            emptyList(),
            emptyList() // output type
        )

        val encodedFunction = FunctionEncoder.encode(function)

        val response = web3.ethCall(
            Transaction.createEthCallTransaction(myAddress, contractAddress, encodedFunction),
            DefaultBlockParameterName.LATEST
        ).send()

        return@withContext true
    }

    // Function to activate the insurance contract

    suspend fun activateContract(contractAddress: String, insuredAddress: String): Boolean = withContext(Dispatchers.IO) {
        val function = Function(
            "activateContract",
            emptyList(),
            emptyList() // output type
        )

        val encodedFunction = FunctionEncoder.encode(function)

        val response = web3.ethCall(
            Transaction.createEthCallTransaction(insuredAddress, contractAddress, encodedFunction),
            DefaultBlockParameterName.LATEST
        ).send()

        return@withContext true
    }
}