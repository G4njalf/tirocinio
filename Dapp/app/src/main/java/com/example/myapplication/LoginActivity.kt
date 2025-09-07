package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.data.BlockChainCalls
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    //private val blockchainCalls = BlockChainCalls()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        /*getSharedPreferences("UserPrefs", MODE_PRIVATE).edit() {
            remove("session_topic")
            remove("session_pairingTopic")
            remove("user_address")
        }*/


        //val addressInput = findViewById<EditText>(R.id.addressEditText)
        val btnCliente = findViewById<Button>(R.id.btn_cliente)
        val btnAssicuratore = findViewById<Button>(R.id.btn_assicuratore)

        btnCliente.setOnClickListener {
            saveUserRole("cliente")
            navigateToMain()
        }

        btnAssicuratore.setOnClickListener {
            saveUserRole("assicuratore")
            navigateToMain()
        }
    }

    private fun saveUserRole(role: String) {
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("user_role", role)
        //editor.putString("user_address", address)
        editor.apply()
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}