package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

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
        editor.apply()
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}