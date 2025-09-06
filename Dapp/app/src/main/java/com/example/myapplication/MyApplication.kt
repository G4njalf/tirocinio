package com.example.myapplication

import android.app.Application
import android.util.Log
import com.reown.android.Core
import com.reown.android.CoreClient
import com.reown.android.relay.ConnectionType
import com.reown.appkit.client.AppKit
import com.reown.appkit.client.Modal
import com.reown.appkit.presets.AppKitChainsPresets
import com.reown.appkit.presets.AppKitChainsPresets.ethToken
import com.reown.appkit.utils.EthUtils


class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val connectionType = ConnectionType.AUTOMATIC
        val projectId = "579855a671e07abf026f809ed6bf5f3c" // Inserisci qui il Project ID
        val appMetaData = Core.Model.AppMetaData(
            name = "Kotlin.AppKit",
            description = "Kotlin AppKit Implementation",
            url = "kotlin.reown.com",
            icons = listOf("https://gblobscdn.gitbook.com/spaces%2F-LJJeCjcLrr53DcT1Ml7%2Favatar.png?alt=media"),
            redirect = "kotlin-modal-wc://request"
        )

        CoreClient.initialize(
            application = this,
            projectId = projectId,
            metaData = appMetaData,
            connectionType = connectionType,
            onError = { error -> Log.e("MyApplication",error.toString()) }
        )

        AppKit.initialize(
            init = Modal.Params.Init(CoreClient),
            onSuccess = {
                // Callback se inizializzazione ok
            },
            onError = { error ->
                Log.e("MyApplication",error.toString())
            }
        )

        val ethChains: Map<String, Modal.Model.Chain> = mapOf(
            "1" to Modal.Model.Chain(
                chainName = "Ethereum",
                chainNamespace = "eip155",
                chainReference = "1",
                requiredMethods = EthUtils.ethRequiredMethods,
                optionalMethods = EthUtils.ethOptionalMethods,
                events = EthUtils.ethEvents,
                token = ethToken,
                rpcUrl = "https://cloudflare-eth.com",
                blockExplorerUrl = "https://etherscan.io"
            ),
            "42161" to Modal.Model.Chain(
                chainName = "Arbitrum One",
                chainNamespace = "eip155",
                chainReference = "42161",
                requiredMethods = EthUtils.ethRequiredMethods,
                optionalMethods = EthUtils.ethOptionalMethods,
                events = EthUtils.ethEvents,
                token = ethToken,
                rpcUrl = "https://arb1.arbitrum.io/rpc",
                blockExplorerUrl = "https://arbiscan.io"
            ),
            "137" to Modal.Model.Chain(
                chainName = "Polygon",
                chainNamespace = "eip155",
                chainReference = "137",
                requiredMethods = EthUtils.ethRequiredMethods,
                optionalMethods = EthUtils.ethOptionalMethods,
                events = EthUtils.ethEvents,
                token = Modal.Model.Token("MATIC", "MATIC", 18),
                rpcUrl = "https://polygon-rpc.com",
                blockExplorerUrl = "https://polygonscan.com"
            ),
            "43114" to Modal.Model.Chain(
                chainName = "Avalanche",
                chainNamespace = "eip155",
                chainReference = "43114",
                requiredMethods = EthUtils.ethRequiredMethods,
                optionalMethods = EthUtils.ethOptionalMethods,
                events = EthUtils.ethEvents,
                token = Modal.Model.Token("Avalanche", "AVAX", 18),
                rpcUrl = "https://api.avax.network/ext/bc/C/rpc",
                blockExplorerUrl = "https://snowtrace.io"
            ),
            "56" to Modal.Model.Chain(
                chainName = "BNB Smart Chain",
                chainNamespace = "eip155",
                chainReference = "56",
                requiredMethods = EthUtils.ethRequiredMethods,
                optionalMethods = EthUtils.ethOptionalMethods,
                events = EthUtils.ethEvents,
                token = Modal.Model.Token("BNB", "BNB", 18),
                rpcUrl = "https://rpc.ankr.com/bsc",
                blockExplorerUrl = "https://bscscan.com"
            ),
            "10" to Modal.Model.Chain(
                chainName = "OP Mainnet",
                chainNamespace = "eip155",
                chainReference = "10",
                requiredMethods = EthUtils.ethRequiredMethods,
                optionalMethods = EthUtils.ethOptionalMethods,
                events = EthUtils.ethEvents,
                token = ethToken,
                rpcUrl = "https://mainnet.optimism.io",
                blockExplorerUrl = "https://explorer.optimism.io"
            ),
            "100" to Modal.Model.Chain(
                chainName = "Gnosis",
                chainNamespace = "eip155",
                chainReference = "100",
                requiredMethods = EthUtils.ethRequiredMethods,
                optionalMethods = EthUtils.ethOptionalMethods,
                events = EthUtils.ethEvents,
                token = Modal.Model.Token("Gnosis", "xDAI", 18),
                rpcUrl = "https://rpc.gnosischain.com",
                blockExplorerUrl = "https://blockscout.com/xdai/mainnet"
            ),
            "324" to Modal.Model.Chain(
                chainName = "zkSync Era",
                chainNamespace = "eip155",
                chainReference = "324",
                requiredMethods = EthUtils.ethRequiredMethods,
                optionalMethods = EthUtils.ethOptionalMethods,
                events = EthUtils.ethEvents,
                token = ethToken,
                rpcUrl = "https://mainnet.era.zksync.io",
                blockExplorerUrl = "https://explorer.zksync.io"
            ),
            "7777777" to Modal.Model.Chain(
                chainName = "Zora",
                chainNamespace = "eip155",
                chainReference = "7777777",
                requiredMethods = EthUtils.ethRequiredMethods,
                optionalMethods = EthUtils.ethOptionalMethods,
                events = EthUtils.ethEvents,
                token = ethToken,
                rpcUrl = "https://rpc.zora.energy",
                blockExplorerUrl = "https://explorer.zora.energy"
            ),
            "8453" to Modal.Model.Chain(
                chainName = "Base",
                chainNamespace = "eip155",
                chainReference = "8453",
                requiredMethods = EthUtils.ethRequiredMethods,
                optionalMethods = EthUtils.ethOptionalMethods,
                events = EthUtils.ethEvents,
                token = ethToken,
                rpcUrl = "https://mainnet.base.org",
                blockExplorerUrl = "https://basescan.org"
            ),
            "42220" to Modal.Model.Chain(
                chainName = "Celo",
                chainNamespace = "eip155",
                chainReference = "42220",
                requiredMethods = EthUtils.ethRequiredMethods,
                optionalMethods = EthUtils.ethOptionalMethods,
                events = EthUtils.ethEvents,
                token = Modal.Model.Token("CELO", "CELO", 18),
                rpcUrl = "https://forno.celo.org",
                blockExplorerUrl = "https://explorer.celo.org/mainnet"
            ),
            "1313161554" to Modal.Model.Chain(
                chainName = "Aurora",
                chainNamespace = "eip155",
                chainReference = "1313161554",
                requiredMethods = EthUtils.ethRequiredMethods,
                optionalMethods = EthUtils.ethOptionalMethods,
                events = EthUtils.ethEvents,
                token = ethToken,
                rpcUrl = "https://mainnet.aurora.dev",
                blockExplorerUrl = "https://aurorascan.dev"
            ),
            "11155111" to Modal.Model.Chain(
                chainName = "Sepolia",
                chainNamespace = "eip155",
                chainReference = "11155111",
                requiredMethods = EthUtils.ethRequiredMethods,
                optionalMethods = EthUtils.ethOptionalMethods,
                events = EthUtils.ethEvents,
                token = ethToken,
                rpcUrl = BuildConfig.INFURA_URL,
                blockExplorerUrl = "https://sepolia.etherscan.io"
            )
        )

        AppKit.setChains(ethChains.values.toList())

    }
}