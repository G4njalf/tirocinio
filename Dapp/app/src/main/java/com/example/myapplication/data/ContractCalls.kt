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
import org.web3j.abi.datatypes.Bool
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.abi.datatypes.DynamicArray
import org.web3j.crypto.RawTransaction
import org.web3j.crypto.TransactionEncoder
import org.web3j.utils.Numeric
import java.math.BigInteger
import org.web3j.protocol.core.methods.response.TransactionReceipt
import kotlinx.coroutines.delay
import io.github.cdimascio.dotenv.dotenv

class DynamicAddressArray(addresses: List<Address>) : DynamicArray<Address>(Address::class.java, addresses)

val dotenv = dotenv()
private val infuraurl = dotenv["INFURA_URL"]

private val web3 = Web3j.build(HttpService(infuraurl))
//HARDCODE DA TOGLIERE PRIMA O POI
private val myAddressOLD = "0x8C6b618aC0b1E69FA7FF02Ec2a8EB6caDC29bc86"
private val myAddress = "0x5F09F774d9725b4eA6B4A9a356BaDf54760374bc"
private val tokenOwnerAddress = "0x8C6b618aC0b1E69FA7FF02Ec2a8EB6caDC29bc86"
private val mytokenAddress = "0xF9f3AE879C612D35a8D1CAa67e178f190a4a215f"
private val factoryAddressOLD = "0xAc12bd15e865e156bC712aeeaC6E6092b53BA6D3" // vecchio che va
private val factoryAddress = "0xFAD9CF31f457b0880fA6E1C612F988ab69c53317"
private val privateKeyAssicuratore = dotenv["PRIVATE_KEY_ASSICURATORE"]
private val privateKeyAssicurato = dotenv["PRIVATE_KEY_ASSICURATO"]


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

    suspend fun waitForReceipt(txHash: String): TransactionReceipt = withContext(Dispatchers.IO) {
        while (true) {
            val receiptOptional = web3.ethGetTransactionReceipt(txHash).send().transactionReceipt
            if (receiptOptional.isPresent) {
                return@withContext receiptOptional.get()
            }
            delay(3000) // aspetta 3 secondi
        }
        throw Exception("Transaction receipt not found") // Gestione del caso in cui il ciclo non restituisce mai un valore
    }

    suspend fun approveTokenTransfer(userAddress: String,spenderAddress: String, amount: BigInteger, role: String, tokenAddress: String )
    : String = withContext(Dispatchers.IO) {

        val function = Function(
            "approve",
            listOf(Address(spenderAddress), Uint256(amount)),
            listOf(TypeReference.create(org.web3j.abi.datatypes.Bool::class.java))
        )

        val encodedFunction = FunctionEncoder.encode(function)

        var credentials = org.web3j.crypto.Credentials.create(privateKeyAssicuratore)
        if (role == "cliente"){
            credentials = org.web3j.crypto.Credentials.create(privateKeyAssicurato)
        }
        val nonce = web3.ethGetTransactionCount(userAddress, DefaultBlockParameterName.PENDING)
            .send().transactionCount

        val baseGasPrice = web3.ethGasPrice().send().gasPrice
        val gasPrice = baseGasPrice.multiply(BigInteger.valueOf(120)).divide(BigInteger.valueOf(100)) // 20% more than the base gas price
        val gasLimit = BigInteger.valueOf(1_500_000L) // per ora cosi

        val transaction = RawTransaction.createTransaction(
            nonce,
            gasPrice,
            gasLimit,
            tokenAddress,
            encodedFunction
        )

        Log.d("","chainId: ${web3.ethChainId().send().chainId.toLong()}")

        val signedTransaction = TransactionEncoder.signMessage(
            transaction,
            web3.ethChainId().send().chainId.toLong(), // Sepolia chain ID
            credentials
        )

        val hexValue = Numeric.toHexString(signedTransaction)

        val response = web3.ethSendRawTransaction(hexValue).send()

        if (response.hasError()) {
            Log.e("approveTokenTransfer", "Transaction error: ${response.error.message}")
            throw Exception("Token approval failed: ${response.error.message}")
        }

        return@withContext response.transactionHash
    }

    suspend fun mintTokens(reciver: String, amount: BigInteger) : String = withContext(Dispatchers.IO) {

        val function = Function(
            "mint",
            listOf(Address(reciver), Uint256(amount)),
            emptyList()
        )

        val encodedFunction = FunctionEncoder.encode(function)

        val credentials = org.web3j.crypto.Credentials.create(privateKeyAssicuratore)
        val nonce = web3.ethGetTransactionCount(tokenOwnerAddress, DefaultBlockParameterName.PENDING)
            .send().transactionCount

        val baseGasPrice = web3.ethGasPrice().send().gasPrice
        val gasPrice = baseGasPrice.multiply(BigInteger.valueOf(120)).divide(BigInteger.valueOf(100)) // 20% more than the base gas price
        val gasLimit = BigInteger.valueOf(1_500_000L) // per ora cosi

        val transaction = RawTransaction.createTransaction(
            nonce,
            gasPrice,
            gasLimit,
            mytokenAddress,
            encodedFunction
        )

        val signedTransaction = TransactionEncoder.signMessage(
            transaction,
            web3.ethChainId().send().chainId.toLong(), // Sepolia chain ID
            credentials
        )

        val hexValue = Numeric.toHexString(signedTransaction)

        val response = web3.ethSendRawTransaction(hexValue).send()

        if (response.hasError()) {
            Log.e("mintTokens", "Transaction error: ${response.error.message}")
            throw Exception("Token minting failed: ${response.error.message}")
        }

        return@withContext response.transactionHash
    }

}


class ContractCalls {

    // Function to get the token balance of the user's address

    suspend fun getTokenBalance(address: String): String = withContext(Dispatchers.IO) {
        val function = Function(
            "balanceOf",
            listOf(Address(address)),
            listOf(TypeReference.create(org.web3j.abi.datatypes.generated.Uint256::class.java))
        )

        val encodedFunction = FunctionEncoder.encode(function)

        val response = web3.ethCall(
            Transaction.createEthCallTransaction(address, mytokenAddress, encodedFunction),
            DefaultBlockParameterName.LATEST
        ).send()

        val decoded = FunctionReturnDecoder.decode(response.value, function.outputParameters)

        return@withContext decoded[0].value.toString()
    }

    // Function to create a new insurance contract from the factory contract

    suspend fun createNewContract(addrAssicurato: String, premio: Uint256): String = withContext(Dispatchers.IO){

        val credentials = org.web3j.crypto.Credentials.create(privateKeyAssicuratore)

        val nonce = web3.ethGetTransactionCount(myAddress, DefaultBlockParameterName.PENDING)
            .send().transactionCount

        val basegasPrice = web3.ethGasPrice().send().gasPrice
        val gasPrice = basegasPrice.multiply(BigInteger.valueOf(120)).divide(BigInteger.valueOf(100)) // 20% more than the base gas price
        val gasLimit = BigInteger.valueOf(1_500_000L) // per ora cosi

        val function = Function(
            "createInsurance",
            listOf(
                Address(addrAssicurato),
                Uint256(premio.value)
                ), // inputs
            listOf(
                TypeReference.create(org.web3j.abi.datatypes.Address::class.java) // output type
            )
        )

        val encodedFunction = FunctionEncoder.encode(function)

        val transaction = RawTransaction.createTransaction(
            nonce,
            gasPrice,
            gasLimit,
            factoryAddress,
            encodedFunction
        )

        Log.d("","chainId: ${web3.ethChainId().send().chainId.toLong()}")

        val signedTransaction = TransactionEncoder.signMessage(
            transaction,
            web3.ethChainId().send().chainId.toLong(), // Sepolia chain ID
            credentials
        )

        val hexValue = Numeric.toHexString(signedTransaction)

        val response = web3.ethSendRawTransaction(hexValue).send()

        if (response.hasError()) {
            Log.e("createNewContract", "Transaction error: ${response.error.message}")
            throw Exception("Smart contract creation failed: ${response.error.message}")
        }

        val txHash = response.transactionHash
        Log.d("createNewContract", "Tx hash: $txHash")

        return@withContext txHash
    }

    // Function to get the address of the insurance contract created by the factory contract

    suspend fun getAllInsuranceContracts(): List<String> = withContext(Dispatchers.IO) {
        val function = Function(
            "getAllInsuranceContracts",
            emptyList(), // no inputs
            listOf(object : TypeReference<DynamicArray<Address>>() {}) // output type
        )

        val encodedFunction = FunctionEncoder.encode(function)

        val response = web3.ethCall(
            Transaction.createEthCallTransaction(myAddress, factoryAddress, encodedFunction),
            DefaultBlockParameterName.LATEST
        ).send()

        val decoded = FunctionReturnDecoder.decode(response.value, function.outputParameters)
        val addressList = decoded[0].value as List<*>
        Log.d("getAllInsuranceContracts", "Decoded addresses: $addressList")
        return@withContext addressList
            .map { it as Address }
            .map { it.value }

    }

    suspend fun getInsuranceContractsByInsured(insuredAddress: String) : List<String> = withContext(Dispatchers.IO) {
        val function = Function(
            "getInsuranceContractsByInsured",
            listOf(Address(insuredAddress)),
            listOf(object : TypeReference<DynamicArray<Address>>() {})
        )

        val encodedFunction = FunctionEncoder.encode(function)

        val response = web3.ethCall(
            Transaction.createEthCallTransaction(myAddress, factoryAddress, encodedFunction),
            DefaultBlockParameterName.LATEST
        ).send()

        val decoded = FunctionReturnDecoder.decode(response.value, function.outputParameters)
        val addressList = decoded[0].value as List<*>
        Log.d("getInsuranceContractsByInsured", "Decoded addresses: $addressList")
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

        val versionFunction = Function(
            "version",
            emptyList(),
            listOf(TypeReference.create(Utf8String::class.java))
        )

        val gateFunction = Function(
            "gate",
            emptyList(),
            listOf(TypeReference.create(Address::class.java))
        )

        val requestIdFunction = Function(
            "requestId",
            emptyList(),
            listOf(TypeReference.create(org.web3j.abi.datatypes.generated.Bytes32::class.java))
        )

        val functions = listOf(
            "assicuratore" to assicuratoreFunction,
            "assicurato" to assicuratoFunction,
            "premio" to premioFunction,
            "liquidato" to liquidatoFunction,
            "attivato" to attivatoFunction,
            "funded" to fundedFunction,
            "token" to tokenFunction,
            "version" to versionFunction,
            "gate" to gateFunction,
            "requestId" to requestIdFunction
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
                val rawValue = decoded[0].value
                results[name] = when (rawValue) {
                    is ByteArray -> "0x" + rawValue.joinToString("") { "%02x".format(it) } // handle bytes32
                    else -> rawValue
                }
            } else {
                Log.e("ContractCalls", "Decoded response for function $name is empty")
            }
        }

        return@withContext results
    }

    // Function to fund the insurance contract ( l assicuratore deve inviare i fondi al contratto)

    suspend fun fundContract(contractAddress: String): String = withContext(Dispatchers.IO){

        val credentials = org.web3j.crypto.Credentials.create(privateKeyAssicuratore)

        val nonce = web3.ethGetTransactionCount(myAddress, DefaultBlockParameterName.PENDING)
            .send().transactionCount

        val basegasPrice = web3.ethGasPrice().send().gasPrice
        val gasPrice = basegasPrice.multiply(BigInteger.valueOf(120)).divide(BigInteger.valueOf(100)) // 20% more than the base gas price
        val gasLimit = BigInteger.valueOf(1_500_000L) // per ora cosi

        val function = Function(
            "fundContract",
            emptyList(),
            emptyList() // output type
        )
        val encodedFunction = FunctionEncoder.encode(function)

        val transaction = RawTransaction.createTransaction(
            nonce,
            gasPrice,
            gasLimit,
            contractAddress,
            encodedFunction
        )

        val signedTransaction = TransactionEncoder.signMessage(
            transaction,
            web3.ethChainId().send().chainId.toLong(), // Sepolia chain ID
            credentials
        )

        val hexValue = Numeric.toHexString(signedTransaction)

        val response = web3.ethSendRawTransaction(hexValue).send()

        Log.d("fundContract", "Transaction sent: $hexValue, txHash: ${response.transactionHash}")

        if (response.hasError()) {
            Log.e("fundContract", "Transaction error: ${response.error.message}")
            throw Exception("Funding contract failed: ${response.error.message}")
        }

        val txHash = response.transactionHash

        return@withContext txHash
    }

    // Function to pay the ensured

    suspend fun liquidateContract(contractAddress: String , callerAddress: String) : String = withContext(Dispatchers.IO) {

        val credentials = org.web3j.crypto.Credentials.create(privateKeyAssicurato)

        val nonce = web3.ethGetTransactionCount(callerAddress, DefaultBlockParameterName.PENDING)
            .send().transactionCount

        val basegasPrice = web3.ethGasPrice().send().gasPrice
        val gasPrice = basegasPrice.multiply(BigInteger.valueOf(120)).divide(BigInteger.valueOf(100)) // 20% more than the base gas price
        val gasLimit = BigInteger.valueOf(1_500_000L) // per ora cosi

        val function = Function(
            "liquidazione",
            emptyList(),
            emptyList() // output type
        )

        val encodedFunction = FunctionEncoder.encode(function)

        val transaction = RawTransaction.createTransaction(
            nonce,
            gasPrice,
            gasLimit,
            contractAddress,
            encodedFunction
        )

        val signedTransaction = TransactionEncoder.signMessage(
            transaction,
            web3.ethChainId().send().chainId.toLong(), // Sepolia chain ID
            credentials
        )

        val hexValue = Numeric.toHexString(signedTransaction)

        val response = web3.ethSendRawTransaction(hexValue).send()

        if (response.hasError()) {
            Log.e("createNewContract", "Transaction error: ${response.error.message}")
            throw Exception("Smart contract creation failed: ${response.error.message}")
        }

        val txHash = response.transactionHash

        return@withContext txHash
    }

    // Function to activate the insurance contract

    suspend fun activateContract(contractAddress: String, insuredAddress: String): String = withContext(Dispatchers.IO) {

        val credentials = org.web3j.crypto.Credentials.create(privateKeyAssicurato)

        val nonce = web3.ethGetTransactionCount(insuredAddress, DefaultBlockParameterName.PENDING)
            .send().transactionCount

        val basegasPrice = web3.ethGasPrice().send().gasPrice
        val gasPrice = basegasPrice.multiply(BigInteger.valueOf(120)).divide(BigInteger.valueOf(100)) // 20% more than the base gas price
        val gasLimit = BigInteger.valueOf(1_500_000L) // per ora cosi

        val function = Function(
            "activateContract",
            emptyList(),
            emptyList() // output type
        )

        val encodedFunction = FunctionEncoder.encode(function)

        val transaction = RawTransaction.createTransaction(
            nonce,
            gasPrice,
            gasLimit,
            contractAddress,
            encodedFunction
        )

        val signedTransaction = TransactionEncoder.signMessage(
            transaction,
            web3.ethChainId().send().chainId.toLong(), // Sepolia chain ID
            credentials
        )

        val hexValue = Numeric.toHexString(signedTransaction)

        val response = web3.ethSendRawTransaction(hexValue).send()

        if (response.hasError()) {
            Log.e("createNewContract", "Transaction error: ${response.error.message}")
            throw Exception("Smart contract creation failed: ${response.error.message}")
        }

        val txHash = response.transactionHash

        return@withContext txHash
    }

    // RICHIEDE I DATI ZONIA

    suspend fun requestZoniaData(contractAddress: String,insuredAddress: String) : String = withContext(Dispatchers.IO) {

        val credentials = org.web3j.crypto.Credentials.create(privateKeyAssicurato)

        val nonce = web3.ethGetTransactionCount(insuredAddress, DefaultBlockParameterName.PENDING)
            .send().transactionCount

        val basegasPrice = web3.ethGasPrice().send().gasPrice
        val gasPrice = basegasPrice.multiply(BigInteger.valueOf(120)).divide(BigInteger.valueOf(100)) // 20% more than the base gas price
        val gasLimit = BigInteger.valueOf(1_500_000L) // per ora cosi

        val function = Function(
            "requestZoniaData",
            emptyList(),
            emptyList() // output type
        )

        val encodedFunction = FunctionEncoder.encode(function)

        val transaction = RawTransaction.createTransaction(
            nonce,
            gasPrice,
            gasLimit,
            contractAddress,
            encodedFunction
        )

        val signedTransaction = TransactionEncoder.signMessage(
            transaction,
            web3.ethChainId().send().chainId.toLong(), // Sepolia chain ID
            credentials
        )

        val hexValue = Numeric.toHexString(signedTransaction)

        val response = web3.ethSendRawTransaction(hexValue).send()

        if (response.hasError()) {
            Log.e("createNewContract", "Transaction error: ${response.error.message}")
            throw Exception("Smart contract creation failed: ${response.error.message}")
        }

        val txHash = response.transactionHash

        return@withContext txHash
    }
}

