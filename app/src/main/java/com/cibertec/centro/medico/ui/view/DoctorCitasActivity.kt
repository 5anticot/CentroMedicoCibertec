package com.cibertec.centro.medico.ui.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.cibertec.centro.medico.databinding.ActivityDoctorCitasBinding
import com.cibertec.centro.medico.ui.adapter.DoctorCitasAdapter
import com.cibertec.centro.medico.ui.viewmodel.DoctorViewModel

class DoctorCitasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDoctorCitasBinding
    private val viewModel: DoctorViewModel by viewModels()
    private lateinit var adapter: DoctorCitasAdapter
    private var doctorId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoctorCitasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtener doctorId dentro de onCreate
        obtenerDoctorId()

        setupToolbar()
        setupRecyclerView()
        setupSpinnerEstado()
        setupObservers()
        setupListeners()

        // Cargar citas del doctor automáticamente
        if (doctorId > 0) {
            viewModel.cargarCitasDoctor(doctorId, "TODOS")
        } else {
            Toast.makeText(this, "Error: No se pudo obtener el ID del doctor", Toast.LENGTH_LONG).show()
        }
    }

    private fun obtenerDoctorId() {
        val prefs = getSharedPreferences("sesion", MODE_PRIVATE)
        doctorId = prefs.getInt("usuarioId", 0)

        // Si tienes un campo de texto para mostrar/editar el ID del doctor
        if (::binding.isInitialized) {
            binding.etDoctorId?.setText(doctorId.toString())
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = "Mis Citas como Doctor"
        }
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
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

    // Si no necesitas el campo de texto, simplifica así:
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
            // Usar el doctorId obtenido de SharedPreferences o del campo de texto
            val idDoctor = if (doctorId > 0) {
                doctorId
            } else {
                binding.etDoctorId?.text?.toString()?.toIntOrNull() ?: 0
            }

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

            // Mostrar mensaje si no hay citas
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
}