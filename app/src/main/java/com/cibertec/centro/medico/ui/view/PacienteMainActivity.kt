package com.cibertec.centro.medico.ui.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.cibertec.centro.medico.R
import com.cibertec.centro.medico.data.network.RetrofitClient
import com.cibertec.centro.medico.databinding.ActivityPacienteMainBinding
import com.cibertec.centro.medico.utils.SessionManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch

class PacienteMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPacienteMainBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPacienteMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar SessionManager
        sessionManager = SessionManager(this)

        setupToolbar()
        setupViewPager()
        loadUserData()
        obtenerUsuarioPorId(sessionManager.getUsuarioId())
        configurarMenuOpciones()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = "Portal del Paciente"
            setDisplayHomeAsUpEnabled(false) // No mostrar botón de navegación hacia atrás
        }
    }

    private fun configurarMenuOpciones() {
        val btnMenu = findViewById<ImageButton>(R.id.btnMenu)
        btnMenu.setOnClickListener { view ->
            mostrarMenuOpciones(view)
        }
    }

    private fun mostrarMenuOpciones(anchorView: View) {
        val popup = PopupMenu(this, anchorView)
        popup.menuInflater.inflate(R.menu.menu_opciones, popup.menu)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_cerrar_sesion -> {
                    confirmarCerrarSesion()
                    true
                }
                else -> false
            }
        }
        popup.show()
    }


    private fun setupViewPager() {
        val tabLayout = binding.tabLayout
        val viewPager = binding.viewPager

        val adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 2

            override fun createFragment(position: Int): Fragment {
                Log.d("PacienteMainActivity", "Creando fragment en posición: $position")
                return when (position) {
                    0 -> {
                        Log.d("PacienteMainActivity", "Creando MisCitasFragment")
                        MisCitasFragment()
                    }
                    1 -> {
                        Log.d("PacienteMainActivity", "Creando CitasDisponiblesFragment")
                        CitasDisponiblesFragment()
                    }
                    else -> {
                        Log.e("PacienteMainActivity", "Posición inválida: $position")
                        throw IllegalArgumentException("Invalid position: $position")
                    }
                }
            }
        }

        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Mis Citas"
                1 -> "Citas Disponibles"
                else -> "Tab $position"
            }
        }.attach()
    }

    private fun loadUserData() {
        val nombreUsuario = sessionManager.getUsuarioNombre()
        val apellidoUsuario = sessionManager.getUsuarioApellido()
        val usuarioId = sessionManager.getUsuarioId()

        Log.d("PacienteMainActivity", "Datos de sesión - ID: $usuarioId, Nombre: $nombreUsuario")

        // Mostrar nombre completo si está disponible
        val nombreCompleto = if (!apellidoUsuario.isNullOrEmpty()) {
            "$nombreUsuario $apellidoUsuario"
        } else {
            nombreUsuario ?: "Usuario"
        }

        binding.txtNombrePaciente.text = nombreCompleto
    }

    private fun obtenerUsuarioPorId(usuarioId: Int) {
        if (usuarioId <= 0) {
            Log.e("PacienteMainActivity", "ID de usuario no válido: $usuarioId")
            return
        }

        lifecycleScope.launch {
            try {
                Log.d("PacienteMainActivity", "Obteniendo usuario actualizado con ID: $usuarioId")

                val response = RetrofitClient.instance.getUsuarioById(usuarioId)

                if (response.isSuccessful) {
                    val usuario = response.body()
                    Log.d("PacienteMainActivity", "Usuario actualizado recibido: $usuario")

                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = "Error ${response.code()}: ${response.message()}" +
                            if (errorBody != null) " - $errorBody" else ""
                    Log.e("PacienteMainActivity", "Error al obtener el usuario: $errorMessage")

                    // Solo mostrar error si no es un 404 (usuario no encontrado puede ser normal)
                    if (response.code() != 404) {
                        Toast.makeText(this@PacienteMainActivity,
                            "Error al cargar datos actualizados del usuario",
                            Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("PacienteMainActivity", "Error de red: ${e.message}", e)
                Toast.makeText(this@PacienteMainActivity,
                    "Error de conexión al actualizar datos",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun confirmarCerrarSesion() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Cerrar Sesión")
            .setMessage("¿Estás seguro de que quieres cerrar la sesión?")
            .setPositiveButton("Cerrar Sesión") { _, _ ->
                logout()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun logout() {
        try {
            // Limpiar sesión
            sessionManager.clearSession()
            Log.d("PacienteMainActivity", "Sesión cerrada exitosamente")
            Toast.makeText(this, "Sesión cerrada exitosamente", Toast.LENGTH_SHORT).show()
            redirectToLogin()
        } catch (e: Exception) {
            Log.e("PacienteMainActivity", "Error al cerrar sesión: ${e.message}", e)
            Toast.makeText(this, "Error al cerrar sesión", Toast.LENGTH_SHORT).show()
        }
    }

    private fun redirectToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

}