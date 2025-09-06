package com.example.myapplication

import android.os.Bundle
import android.view.Menu
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityMainBinding
import android.util.Log
import android.view.MenuItem
import android.content.Intent
import android.net.Uri
import android.widget.TextView
import androidx.core.net.toUri
import com.reown.android.Core
import com.reown.android.CoreClient
import com.reown.appkit.client.AppKit
import com.reown.appkit.client.Modal
import com.reown.appkit.client.models.Session
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.data.BlockChainCalls
import kotlinx.coroutines.launch
import java.math.BigInteger

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private var dummyBool = false

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i("MainActivity", "onCreate called")
        super.onCreate(savedInstanceState)

        // Recupera il ruolo dell'utente
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val userRole = sharedPreferences.getString("user_role", "cliente")

        // Controlla il ruolo e carica il layout principale
        if (userRole == "cliente") {
            // Carica il layout per il cliente
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)
            val roleTextView = binding.navView.getHeaderView(0).findViewById<TextView>(R.id.roleTextView)
            roleTextView.text = "Hello, Client"
            val addressTextViewNav = binding.navView.getHeaderView(0).findViewById<TextView>(R.id.addressTextViewNav)
            addressTextViewNav.text = sharedPreferences.getString("user_address", "Indirizzo non disponibile")
            setSupportActionBar(binding.appBarMain.toolbar)

        } else if (userRole == "assicuratore") {
            // Carica il layout per l'assicuratore
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)
            val roleTextView = binding.navView.getHeaderView(0).findViewById<TextView>(R.id.roleTextView)
            roleTextView.text = "Hello, Ensurer"
            val addressTextViewNav = binding.navView.getHeaderView(0).findViewById<TextView>(R.id.addressTextViewNav)
            addressTextViewNav.text = sharedPreferences.getString("user_address", "Indirizzo non disponibile")

            setSupportActionBar(binding.appBarMain.toolbar)

        }

        // Configura il Navigation Drawer
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_contract_creation, R.id.nav_settings
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        if (userRole == "cliente"){
            val menu = navView.menu
            menu.findItem(R.id.nav_contract_creation).isVisible = false
        }



    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                // Esegui l'azione di logout
                val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.clear() // Rimuove tutti i dati salvati
                editor.apply()

                // Torna alla LoginActivity
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish() // Chiude la MainActivity
                true
            }
            R.id.action_connect -> {

                /*val pairings = CoreClient.Pairing.getPairings()
                Log.d("MainActivity", "Existing pairings: ${pairings}")
                val pairing = if (pairings.isNotEmpty()) {
                    pairings.first() // Usa il primo pairing disponibile
                } else {
                    CoreClient.Pairing.create() ?: throw IllegalStateException("Failed to create pairing")
                }*/
                if (getSharedPreferences("UserPrefs", MODE_PRIVATE).getString("session_topic", null) != null){

                    //check if the data are still valid

                    val session = AppKit.getSession()

                    if(session == null){
                        Log.i("MainActivity", "No active session found, clearing saved session data")
                        //clear the saved data
                        getSharedPreferences("UserPrefs", MODE_PRIVATE).edit() {
                            remove("session_topic")
                            remove("session_pairingTopic")
                            remove("user_address")
                        }
                    }
                    else{
                        Log.i("MainActivity", "Active session found, no need to connect")
                        return true
                    }
                }

                val pairing = CoreClient.Pairing.create() ?: throw IllegalStateException("Failed to create pairing")

                val getPairing = CoreClient.Pairing.getPairings()

                Log.d("MainActivity", "get pairing: $getPairing")

                val uri = "metamask://wc?uri=" + Uri.encode(pairing.uri)
                Log.d("MainActivity", "Opening MetaMask with URI: $uri")
                val intent = Intent(Intent.ACTION_VIEW,uri.toUri())
                startActivity(intent)

                AppKit.connect(
                    connectParams = Modal.Params.ConnectParams(
                        sessionNamespaces = mapOf(
                            "eip155" to Modal.Model.Namespace.Proposal(
                                chains = listOf("eip155:11155111"),
                                methods = listOf(
                                    "eth_sendTransaction",
                                    "personal_sign",
                                    "eth_sign",
                                    "eth_accounts",
                                    "eth_call"),
                                events = listOf("chainChanged", "accountsChanged")
                            )
                        ),

                        pairing = pairing
                    ),
                    onSuccess = { session: String ->
                        Log.i("MainActivity", "Connected: $session")
                        dummyBool = true
                    },
                    onError = { error: Modal.Model.Error ->
                        Log.e("MainActivity", "Connection failed: ${error.throwable.message}", error.throwable)
                    }
                )
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        val session = AppKit.getSession()
        Log.d("MainActivity", "Current session: $session")
        val account = AppKit.getAccount()
        Log.d("MainActivity", "Current account: $account")
        val pairings = CoreClient.Pairing.getPairings()
        Log.d("MainActivity", "Current pairings: $pairings")

        if (session != null) {
            when (session) {
                is Session.WalletConnectSession -> {
                    val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                    sharedPreferences.edit() {
                        putString("session_pairingTopic", session.pairingTopic)
                        putString("session_topic", session.topic)
                    }
                    Log.d("MainActivity", "Wallet connect session saved with topic: ${session.topic}")
                }

                else -> {
                    Log.w("MainActivity", "Coinbase Wallet session not handled")
                }
            }
        }
        if (account != null) {
            val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
            sharedPreferences.edit() {
                putString("user_address", account.address)
            }
            Log.d("MainActivity", "Account address saved: ${account.address}")
        } else {
            Log.w("MainActivity", "No account available")
        }



    }
}