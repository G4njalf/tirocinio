package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.data.BlockChainCalls
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private val blockchainCalls = BlockChainCalls()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val addressInput = findViewById<EditText>(R.id.addressEditText)
        val btnCliente = findViewById<Button>(R.id.btn_cliente)
        val btnAssicuratore = findViewById<Button>(R.id.btn_assicuratore)

        btnCliente.setOnClickListener {
            val address = addressInput.text.toString()
            lifecycleScope.launch {
                if (blockchainCalls.isWalletAddressValid(address)) {
                    saveUserRole("cliente", address)
                    navigateToMain()
                }
                else{
                    Toast.makeText(this@LoginActivity, "Indirizzo non valido", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnAssicuratore.setOnClickListener {
            val address = addressInput.text.toString()
            lifecycleScope.launch {
                if (blockchainCalls.isWalletAddressValid(address)) {
                    saveUserRole("assicuratore", address)
                    navigateToMain()
                }
                else{
                    Toast.makeText(this@LoginActivity, "Indirizzo non valido", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun saveUserRole(role: String, address: String) {
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("user_role", role)
        editor.putString("user_address", address)
        editor.apply()
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}