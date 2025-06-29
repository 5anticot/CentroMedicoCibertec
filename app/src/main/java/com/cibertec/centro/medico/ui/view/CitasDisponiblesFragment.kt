package com.cibertec.centro.medico.ui.view

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.cibertec.centro.medico.data.model.CitaDisponible
import com.cibertec.centro.medico.data.model.Especialidad
import com.cibertec.centro.medico.databinding.FragmentCitasDisponiblesBinding
import com.cibertec.centro.medico.ui.adapter.CitasDisponiblesAdapter
import com.cibertec.centro.medico.ui.viewmodel.CitasDisponiblesViewModel
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*

class CitasDisponiblesFragment : Fragment() {

    private var _binding: FragmentCitasDisponiblesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CitasDisponiblesViewModel by viewModels()
    private lateinit var adapter: CitasDisponiblesAdapter

    private var selectedEspecialidadId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCitasDisponiblesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        setupListeners()

        // Cargar especialidades al iniciar el fragmento
        viewModel.cargarEspecialidades()

        Log.d("CitasDisponiblesFragment", "Fragment creado y configurado")
    }

    private fun setupRecyclerView() {
        adapter = CitasDisponiblesAdapter { cita ->
            mostrarDialogoReserva(cita)
        }
        binding.rvCitasDisponibles.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.citasDisponibles.observe(viewLifecycleOwner) { citas ->
            Log.d("CitasDisponiblesFragment", "Citas disponibles recibidas: ${citas.size}")
            adapter.submitList(citas)

            // Mostrar mensaje si no hay citas
            if (citas.isEmpty()) {
                binding.txtSinCitas.visibility = View.VISIBLE
                binding.rvCitasDisponibles.visibility = View.GONE
            } else {
                binding.txtSinCitas.visibility = View.GONE
                binding.rvCitasDisponibles.visibility = View.VISIBLE
            }
        }

        viewModel.especialidades.observe(viewLifecycleOwner) { especialidades ->
            setupSpinner(especialidades)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            Log.d("CitasDisponiblesFragment", "Loading state: $isLoading")
        }

        viewModel.toastMessage.observe(viewLifecycleOwner) { message ->
            if (message.isNotEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                Log.d("CitasDisponiblesFragment", "Toast message: $message")
            }
        }
    }

    private fun setupSpinner(especialidades: List<Especialidad>) {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            especialidades.map { it.nombre }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerEspecialidad.adapter = adapter

        binding.spinnerEspecialidad.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (especialidades.isNotEmpty()) {
                    selectedEspecialidadId = especialidades[position].especialidadId
                    Log.d("CitasDisponiblesFragment", "Especialidad seleccionada: ${especialidades[position].nombre} (ID: ${selectedEspecialidadId})")
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedEspecialidadId = null
            }
        }
    }

    private fun setupListeners() {
        // Listeners para los campos de fecha
        binding.etFechaInicio.setOnClickListener {
            mostrarDatePicker(it as TextInputEditText)
        }
        binding.etFechaFin.setOnClickListener {
            mostrarDatePicker(it as TextInputEditText)
        }

        // Listener para el botón de aplicar filtros
        binding.btnFiltrar.setOnClickListener {
            // Obtener fechas (si el campo está vacío, se convierte en null)
            val fechaInicio = binding.etFechaInicio.text.toString().ifEmpty { null }
            val fechaFin = binding.etFechaFin.text.toString().ifEmpty { null }

            // Obtener turno seleccionado (si es "Todos", se convierte en null)
            val turnoSeleccionado = when (binding.rgTurno.checkedRadioButtonId) {
                binding.rbManana.id -> "AM"
                binding.rbTarde.id -> "PM"
                else -> null
            }

            Log.d("CitasDisponiblesFragment", "Aplicando filtros - Especialidad: $selectedEspecialidadId, Fecha inicio: $fechaInicio, Fecha fin: $fechaFin, Turno: $turnoSeleccionado")

            viewModel.cargarCitasDisponibles(selectedEspecialidadId, fechaInicio, fechaFin, turnoSeleccionado)
        }

        // Listener para el botón de limpiar filtros
        binding.btnLimpiarFiltros.setOnClickListener {
            // Limpiamos los componentes de la UI
            binding.spinnerEspecialidad.setSelection(0)
            binding.etFechaInicio.setText("")
            binding.etFechaFin.setText("")
            binding.rgTurno.check(binding.rbTodos.id)

            // Actualizamos la variable del ID de especialidad seleccionado
            selectedEspecialidadId = 0

            Log.d("CitasDisponiblesFragment", "Filtros limpiados")

            // Volvemos a cargar la lista sin filtros
            viewModel.cargarCitasDisponibles(null, null, null, null)
        }
    }

    private fun mostrarDatePicker(editText: TextInputEditText) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
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
        // Obtenemos el ID del paciente desde SharedPreferences
        val prefs = requireActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE)
        val usuarioId = prefs.getInt("usuarioId", 0)

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Confirmar Reserva")

        if (usuarioId > 0) {
            // Si tenemos el ID del usuario, lo usamos automáticamente
            builder.setMessage("¿Confirmas la reserva de la cita con el Dr. ${cita.nombreDoctor} ${cita.apellidoDoctor} el ${cita.fechaCita} a las ${cita.horaCita}?")

            builder.setPositiveButton("Confirmar Reserva") { dialog, _ ->
                Log.d("CitasDisponiblesFragment", "Reservando cita ${cita.citaId} para usuario $usuarioId")
                viewModel.reservarCita(cita.citaId, usuarioId)
                dialog.dismiss()
            }
        } else {
            // Fallback: pedir ID manualmente si no se encuentra en SharedPreferences
            builder.setMessage("Para reservar la cita con el Dr. ${cita.nombreDoctor} ${cita.apellidoDoctor}, por favor, ingresa tu ID de Paciente.")

            val input = EditText(requireContext())
            input.hint = "ID de Paciente"
            input.inputType = android.text.InputType.TYPE_CLASS_NUMBER
            builder.setView(input)

            builder.setPositiveButton("Reservar") { dialog, _ ->
                val pacienteId = input.text.toString().toIntOrNull()
                if (pacienteId != null) {
                    Log.d("CitasDisponiblesFragment", "Reservando cita ${cita.citaId} para paciente $pacienteId")
                    viewModel.reservarCita(cita.citaId, pacienteId)
                    dialog.dismiss()
                } else {
                    Toast.makeText(requireContext(), "Por favor, ingresa un ID válido.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.cancel()
        }
        builder.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}