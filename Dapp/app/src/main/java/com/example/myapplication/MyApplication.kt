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
        val projectId = "579855a671e07abf026f809ed6bf5f3c" // projectidReown
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