package com.cibertec.centro.medico.ui.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.cibertec.centro.medico.R
import com.cibertec.centro.medico.databinding.ActivityDoctorCitasBinding
import com.cibertec.centro.medico.ui.adapter.DoctorCitasAdapter
import com.cibertec.centro.medico.ui.viewmodel.DoctorViewModel
import com.cibertec.centro.medico.utils.SessionManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class DoctorCitasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDoctorCitasBinding
    private val viewModel: DoctorViewModel by viewModels()
    private lateinit var adapter: DoctorCitasAdapter
    private lateinit var sessionManager: SessionManager
    val doctorId: Int
        get() = sessionManager.getUsuarioId()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoctorCitasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        // Obtener doctorId dentro de onCreate

        setupToolbar()
        setupRecyclerView()
        setupSpinnerEstado()
        setupObservers()
        setupListeners()

        if (doctorId > 0) {
            viewModel.cargarCitasDoctor(doctorId, "TODOS")
        } else {
            Toast.makeText(this, "Error: No se pudo obtener el ID del doctor", Toast.LENGTH_LONG).show()
        }

        configurarMenuOpciones()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(false)
            setDisplayShowHomeEnabled(false)
            title = "Mis Citas como Doctor"
        }
    }

    private fun setupRecyclerView() {
        adapter = DoctorCitasAdapter()
        binding.rvDoctorCitas.adapter = adapter
    }

    private fun setupSpinnerEstado() {
        val estados = arrayOf("TODOS", "DISPONIBLE", "RESERVADA", "CANCELADA", "ATENDIDA")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, estados)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerEstado.adapter = spinnerAdapter
    }

    private fun setupListeners() {
        binding.btnBuscarDoctorCitas.setOnClickListener {
            val estadoSeleccionado = binding.spinnerEstado.selectedItem.toString()
            if (doctorId > 0) {
                viewModel.cargarCitasDoctor(doctorId, estadoSeleccionado)
            } else {
                Toast.makeText(this, "Error: ID de doctor no válido", Toast.LENGTH_SHORT).show()
            }
        }

        binding.fabCrearCita?.setOnClickListener {
            val idDoctor = doctorId

            if (idDoctor > 0) {
                val intent = Intent(this, CrearCitaActivity::class.java).apply {
                    putExtra("DOCTOR_ID", idDoctor)
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "Error: ID de doctor no válido para crear cita", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupObservers() {
        viewModel.citasDoctor.observe(this) { citas ->
            adapter.submitList(citas)

            if (citas.isEmpty()) {
                Toast.makeText(this, "No se encontraron citas", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.toastMessage.observe(this) { message ->
            if (message.isNotEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun configurarMenuOpciones() {
        val btnMenu = findViewById<ImageButton>(R.id.btnMenu) // Asegúrate de tener un botón en tu layout

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
            sessionManager.clearSession()
            Toast.makeText(this, "Sesión cerrada exitosamente", Toast.LENGTH_SHORT).show()
            redirectToLogin()
        } catch (e: Exception) {
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
