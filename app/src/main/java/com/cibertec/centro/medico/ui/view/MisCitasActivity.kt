package com.cibertec.centro.medico.ui.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.cibertec.centro.medico.data.model.MiCita
import com.cibertec.centro.medico.databinding.ActivityMisCitasBinding
import com.cibertec.centro.medico.ui.adapter.MisCitasAdapter
import com.cibertec.centro.medico.ui.viewmodel.MisCitasViewModel


class MisCitasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMisCitasBinding
    private val viewModel: MisCitasViewModel by viewModels()
    private lateinit var adapter: MisCitasAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMisCitasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        setupToolbar()
        setupRecyclerView()
        setupObservers()
        setupListeners()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Opcional: para botón de atrás
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun setupRecyclerView() {
        adapter = MisCitasAdapter { cita ->
            mostrarDialogoCancelacion(cita)
        }
        binding.rvMisCitas.adapter = adapter
    }

    private fun setupListeners() {
        binding.btnBuscarCitas.setOnClickListener {
            val pacienteId = binding.etPacienteId.text.toString().toIntOrNull()
            if (pacienteId != null) {
                viewModel.cargarMisCitas(pacienteId)
            } else {
                Toast.makeText(this, "Por favor, ingresa un ID de paciente válido.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupObservers() {
        viewModel.misCitas.observe(this) { citas ->
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

    private fun mostrarDialogoCancelacion(cita: MiCita) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar Cancelación")
            .setMessage("¿Estás seguro de que deseas cancelar tu cita con el Dr. ${cita.nombreDoctor} ${cita.apellidoDoctor}?")
            .setPositiveButton("Sí, Cancelar") { _, _ ->
                viewModel.cancelarCita(cita.citaId)
            }
            .setNegativeButton("No", null)
            .show()
    }
}
