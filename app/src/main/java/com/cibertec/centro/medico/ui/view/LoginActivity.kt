package com.cibertec.centro.medico.ui.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.cibertec.centro.medico.data.model.LoginRequest
import com.cibertec.centro.medico.databinding.ActivityLoginBinding
import com.cibertec.centro.medico.ui.viewmodel.AuthViewModel
import com.cibertec.centro.medico.utils.SessionManager

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sessionManager: SessionManager
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar SessionManager
        sessionManager = SessionManager(this)

        setupUI()
    }

    private fun setupUI() {
        val emailEditText = binding.edtMail
        val passwordEditText = binding.edtPassword
        val loginButton = binding.btnLogin

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (validateInput(email, password)) {
                loginUsuario(email, password)
            }
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            binding.edtMail.error = "El email es requerido"
            return false
        }

        if (password.isEmpty()) {
            binding.edtPassword.error = "La contraseña es requerida"
            return false
        }

        return true
    }

    private fun loginUsuario(email: String, password: String) {
        val loginRequest = LoginRequest(email, password)

        authViewModel.loginUser(loginRequest) { usuario, errorMessage ->
            if (usuario != null) {
                Log.d("LoginActivity", "Usuario logeado: ${usuario.usuarioId}, Rol: ${usuario.rol}, ${usuario.correoElectronico}")

                // Obtener información completa del usuario
                authViewModel.getUsuarioById(usuario.usuarioId) { usuarioCompleto ->
                    if (usuarioCompleto != null) {
                        sessionManager.saveSession(
                            usuarioId = usuarioCompleto.usuarioId,
                            nombre = usuarioCompleto.nombre.toString(),
                            apellido = usuarioCompleto.apellido.toString(),
                            email = usuarioCompleto.correoElectronico.toString(),
                            rol = usuario.rol.toString(),
                            telefono = usuarioCompleto.telefono.toString(),
                            especialidadId = usuarioCompleto.especialidadId,
                            especialidad = usuarioCompleto.especialidad?.toString()
                        )

                        Log.d("LoginActivity", "Sesión guardada - Usuario: ${sessionManager.getUsuarioId()} ${sessionManager.getUsuarioNombre()} ${sessionManager.getUsuarioRol()}")

                        // Redirigir según el rol
                            val userRole = sessionManager.getUsuarioRol()
                            val intent = when (userRole) {
                                "DOCTOR" -> Intent(this, DoctorCitasActivity::class.java)
                                "ADMINISTRADOR" -> Intent(this, AdminMainActivity::class.java)
                                else -> Intent(this, PacienteMainActivity::class.java)
                            }

                            // Limpiar el stack de actividades para evitar volver al login
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)

                        Toast.makeText(this@LoginActivity, "Bienvenido ${usuarioCompleto.nombre}", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.e("LoginActivity", "No se pudo obtener el usuario completo por ID")
                        Toast.makeText(this@LoginActivity, "Error al obtener datos del usuario", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                // Manejo de errores
                val mensaje = errorMessage ?: "Error desconocido"
                Toast.makeText(this@LoginActivity, mensaje, Toast.LENGTH_SHORT).show()
                Log.e("LoginActivity", "Error en login: $errorMessage")
            }
        }
    }

}