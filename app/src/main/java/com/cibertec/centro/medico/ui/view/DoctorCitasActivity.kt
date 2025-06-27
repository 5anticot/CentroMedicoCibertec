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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoctorCitasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        setupToolbar()
        setupRecyclerView()
        setupSpinnerEstado()
        setupObservers()
        setupListeners()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
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
            val doctorId = binding.etDoctorId.text.toString().toIntOrNull()
            val estadoSeleccionado = binding.spinnerEstado.selectedItem.toString()
            if (doctorId != null) {
                viewModel.cargarCitasDoctor(doctorId, estadoSeleccionado)
            } else {
                Toast.makeText(this, "Ingresa un ID de Doctor válido.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.fabCrearCita.setOnClickListener {
            val doctorId = binding.etDoctorId.text.toString().toIntOrNull()
            if (doctorId != null) {
                val intent = Intent(this, CrearCitaActivity::class.java).apply {
                    putExtra("DOCTOR_ID", doctorId)
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "Primero busca un ID de doctor para poder crear citas.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupObservers() {
        viewModel.citasDoctor.observe(this) { citas ->
            adapter.submitList(citas)
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
