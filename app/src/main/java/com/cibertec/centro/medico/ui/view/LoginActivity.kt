package com.cibertec.centro.medico.ui.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import com.cibertec.centro.medico.data.model.LoginRequest
import com.cibertec.centro.medico.databinding.ActivityLoginBinding
import com.cibertec.centro.medico.ui.viewmodel.AuthViewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val emailEditText = binding.edtMail
        val passwordEditText = binding.edtPassword
        val loginButton = binding.btnLogin


        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            loginUsuario(email, password)

            // Aquí puedes manejar la lógica de inicio de sesión
        }

    }

    private fun loginUsuario(email: String, password: String) {
        val loginRequest = LoginRequest(email, password)

        authViewModel.loginUser(loginRequest) { usuario, errorMessage ->
            if (usuario != null) {
                Log.d("LoginActivity", "Usuario logeado: ${usuario.usuarioId}, Rol: ${usuario.rol}, ${usuario.correoElectronico}")


                authViewModel.getUsuarioById(usuario.usuarioId) { usuario ->
                    if (usuario != null) {
                        Log.d("LoginActivity", "Usuario obtenido: ${usuario.usuarioId}, Rol: ${usuario.rol}, ${usuario.correoElectronico}")
                        val prefs = getSharedPreferences("sesion", MODE_PRIVATE)
                        prefs.edit {
                            putInt("usuarioId", usuario.usuarioId)
                                .putString("nombre", usuario.nombre)
                                .putString("apellido", usuario.apellido)
                                .putString("correo", usuario.correoElectronico)
                                .putString("rol", usuario.rol)
                        }
                        Log.d("LoginActivity", "Usuario logeado ${prefs.getInt("usuarioId", 0)}")

                    } else {
                        Log.e("LoginActivity", "No se pudo obtener el usuario por ID")
                    }
                }


                when (usuario.rol) {
                    "DOCTOR" -> {
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    }
                    "ADMINISTRADOR" -> {
                        startActivity(Intent(this@LoginActivity, AdminMainActivity::class.java))
                    }
                    else -> {
                        startActivity(Intent(this@LoginActivity, PacienteMainActivity::class.java))
                    }
                }
                finish() // Cierra la actividad de login para evitar volver con el botón atrás
                Toast.makeText(this@LoginActivity, "Bienvenido", Toast.LENGTH_SHORT).show()
            } else {
                // Manejo de errores
                val mensaje = errorMessage ?: "Error desconocido"
                Toast.makeText(this@LoginActivity, mensaje, Toast.LENGTH_SHORT).show()
                Log.e("LoginActivity", "Error en login: $errorMessage")
            }
        }
    }
}