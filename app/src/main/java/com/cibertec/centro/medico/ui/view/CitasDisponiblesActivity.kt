package com.cibertec.centro.medico.ui.view

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.cibertec.centro.medico.data.model.CitaDisponible
import com.cibertec.centro.medico.data.model.Especialidad
import com.cibertec.centro.medico.databinding.ActivityCitasDisponiblesBinding
import com.cibertec.centro.medico.ui.adapter.CitasDisponiblesAdapter
import com.cibertec.centro.medico.ui.viewmodel.CitasDisponiblesViewModel
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*


class CitasDisponiblesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCitasDisponiblesBinding
    private val viewModel: CitasDisponiblesViewModel by viewModels()
    private lateinit var adapter: CitasDisponiblesAdapter

    private var selectedEspecialidadId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCitasDisponiblesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupObservers()
        setupListeners()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun setupRecyclerView() {
        adapter = CitasDisponiblesAdapter { cita ->
            mostrarDialogoReserva(cita)
        }
        binding.rvCitasDisponibles.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.citasDisponibles.observe(this) { citas ->
            adapter.submitList(citas)
        }

        viewModel.especialidades.observe(this) { especialidades ->
            setupSpinner(especialidades)
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

    private fun setupSpinner(especialidades: List<Especialidad>) {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            especialidades.map { it.nombre }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerEspecialidad.adapter = adapter

        binding.spinnerEspecialidad.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // --- CORRECCIÓN AQUÍ ---
                // Simplemente obtenemos el ID de la especialidad en la posición seleccionada.
                // Si es la primera ("Todas"), su ID será 0.
                if (especialidades.isNotEmpty()) {
                    selectedEspecialidadId = especialidades[position].especialidadId
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedEspecialidadId = null // O 0, dependiendo de tu lógica de "por defecto"
            }
        }
    }

    private fun setupListeners() {
        // Listeners para los campos de fecha
        binding.etFechaInicio.setOnClickListener { mostrarDatePicker(it as com.google.android.material.textfield.TextInputEditText) }
        binding.etFechaFin.setOnClickListener { mostrarDatePicker(it as com.google.android.material.textfield.TextInputEditText) }

        // Listener para el botón de aplicar filtros
        binding.btnFiltrar.setOnClickListener {

            // --- CÓDIGO FALTANTE AÑADIDO AQUÍ ---
            // Obtenemos los valores de los filtros desde los componentes de la UI

            // 1. Obtener fechas (si el campo está vacío, se convierte en null)
            val fechaInicio = binding.etFechaInicio.text.toString().ifEmpty { null }
            val fechaFin = binding.etFechaFin.text.toString().ifEmpty { null }

            // 2. Obtener turno seleccionado (si es "Todos", se convierte en null)
            val turnoSeleccionado = when (binding.rgTurno.checkedRadioButtonId) {
                binding.rbManana.id -> "AM"
                binding.rbTarde.id -> "PM"
                else -> null
            }
            // --- FIN DEL CÓDIGO AÑADIDO ---

            // Ahora sí, estas variables existen y se pueden usar en la llamada al ViewModel
            viewModel.cargarCitasDisponibles(selectedEspecialidadId, fechaInicio, fechaFin, turnoSeleccionado)
        }

        // Listener para el botón de limpiar filtros
        binding.btnLimpiarFiltros.setOnClickListener {
            // Limpiamos los componentes de la UI
            binding.spinnerEspecialidad.setSelection(0)
            binding.etFechaInicio.text = null
            binding.etFechaFin.text = null
            binding.rgTurno.check(binding.rbTodos.id)

            // Actualizamos la variable del ID de especialidad seleccionado
            selectedEspecialidadId = 0

            // Volvemos a cargar la lista sin filtros (todos los parámetros en null)
            viewModel.cargarCitasDisponibles(null, null, null, null)
        }
    }

    private fun mostrarDatePicker(editText: TextInputEditText) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                editText.setText(dateFormat.format(selectedDate.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }
    private fun mostrarDialogoReserva(cita: CitaDisponible) {
        // Este es el modal/dialogo que pediste
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmar Reserva")
        builder.setMessage("Para reservar la cita con el Dr. ${cita.nombreDoctor}, por favor, ingresa tu ID de Paciente.")

        // Creamos un EditText para el dialogo
        val input = EditText(this)
        input.hint = "ID de Paciente"
        input.inputType = android.text.InputType.TYPE_CLASS_NUMBER
        builder.setView(input)

        builder.setPositiveButton("Reservar") { dialog, _ ->
            val pacienteId = input.text.toString().toIntOrNull()
            if (pacienteId != null) {
                viewModel.reservarCita(cita.citaId, pacienteId)
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Por favor, ingresa un ID válido.", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.cancel()
        }
        builder.show()
    }
}
