package com.cibertec.centro.medico.ui.view

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.cibertec.centro.medico.data.model.RegisterRequest
import com.cibertec.centro.medico.databinding.ActivityRegisterBinding
import com.cibertec.centro.medico.ui.viewmodel.AuthViewModel

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val emailEditText = binding.edtMail
        val passwordEditText = binding.edtPassword
        val nameEditText = binding.edtName
        val lastNameEditText = binding.edtLastName
        val telfEditText = binding.edtTelf
        val passConfEditText = binding.edtConfPass
        val btnRegister = binding.btnRegister

        btnRegister.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val name = nameEditText.text.toString()
            val lastName = lastNameEditText.text.toString()
            val telf = telfEditText.text.toString()
            val passConf = passConfEditText.text.toString()

            if (email.isBlank() || password.isBlank() || name.isBlank() ||
                lastName.isBlank() || telf.isBlank()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != passConf) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password == passConf) {
                Log.d("RegisterActivity", "Enviando: email=$email, nombre=$name, apellido=$lastName, telefono=$telf")
                registerUsuario(email, password, "PACIENTE", name, lastName, telf)

            } else {
                // Mostrar un mensaje de error si las contraseñas no coinciden
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            }


        }

    }

    private fun registerUsuario(email: String, password: String, rol: String, name: String, lastName: String, telf: String) {
        val registerRequest = RegisterRequest(email, password, rol, name, lastName, telf)

        authViewModel.registerUser(registerRequest) { success, errorMessage ->
            if (success) {  // Cambio: usar success en lugar de response != null
                Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                if (errorMessage != null) {
                    Log.e("RegisterActivity", "Error: $errorMessage")
                    Toast.makeText(this, "Error al registrar: $errorMessage", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Error desconocido al registrar", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

}
