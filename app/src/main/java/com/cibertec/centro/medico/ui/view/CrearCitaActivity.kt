package com.cibertec.centro.medico.ui.view

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.cibertec.centro.medico.databinding.ActivityCrearCitaBinding
import com.cibertec.centro.medico.ui.viewmodel.DoctorViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class CrearCitaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCrearCitaBinding
    private val viewModel: DoctorViewModel by viewModels() // Reutilizamos el ViewModel
    private var doctorId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrearCitaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        doctorId = intent.getIntExtra("DOCTOR_ID", -1)
        if (doctorId == -1) {
            Toast.makeText(this, "Error: No se recibió el ID del Doctor.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        setupToolbar()
        setupListeners()
        setupObservers()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Crear Nueva Cita"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun setupListeners() {
        binding.etFechaCita.setOnClickListener { mostrarDatePicker() }
        binding.etHoraCita.setOnClickListener { mostrarTimePicker() }

        binding.btnGuardarCita.setOnClickListener {
            val fecha = binding.etFechaCita.text.toString()
            val hora = binding.etHoraCita.text.toString()
            if (fecha.isNotEmpty() && hora.isNotEmpty()) {
                // La API espera formato 'yyyy-MM-dd' y 'HH:mm:ss'
                viewModel.crearCita(doctorId, fecha, "$hora:00")
            } else {
                Toast.makeText(this, "Debes seleccionar fecha y hora", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupObservers() {
        viewModel.citaCreada.observe(this) { creada ->
            if (creada) {
                // Finalizar la actividad y volver a la lista anterior
                finish()
                viewModel.onCitaCreadaHandled() // Resetear el estado para no volver a entrar aquí
            }
        }
        viewModel.toastMessage.observe(this) { message ->
            if (message.isNotEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun mostrarDatePicker() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)
                // Usamos yyyy-MM-dd que es un formato estándar y compatible con SQL
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                binding.etFechaCita.setText(dateFormat.format(selectedDate.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() // No permitir fechas pasadas
        datePickerDialog.show()
    }

    private fun mostrarTimePicker() {
        val calendar = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                val selectedTime = Calendar.getInstance()
                selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                selectedTime.set(Calendar.MINUTE, minute)
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                binding.etHoraCita.setText(timeFormat.format(selectedTime.time))
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true // Formato de 24 horas
        )
        timePickerDialog.show()
    }
}
