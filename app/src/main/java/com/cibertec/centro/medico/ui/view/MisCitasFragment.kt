package com.cibertec.centro.medico.ui.view

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.cibertec.centro.medico.data.model.MiCita
import com.cibertec.centro.medico.databinding.FragmentMisCitasBinding
import com.cibertec.centro.medico.ui.adapter.MisCitasAdapter
import com.cibertec.centro.medico.ui.viewmodel.MisCitasViewModel

class MisCitasFragment : Fragment() {

    private var _binding: FragmentMisCitasBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MisCitasViewModel by viewModels()
    private lateinit var adapter: MisCitasAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMisCitasBinding.inflate(inflater, container, false)

        // Configurar SwipeRefreshLayout ANTES del return
        setupSwipeRefresh()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        setupListeners()

        // Cargar automáticamente las citas del usuario logueado
        cargarCitasAutomaticamente()
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.apply {
            setOnRefreshListener {
                recargarDatos()
            }
            // Personalizar colores del indicador (opcional)
            setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
            )
        }
    }

    private fun cargarCitasAutomaticamente() {
        val prefs = requireActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE)

        val usuarioId = prefs.getInt("usuarioId", 1)
        Log.d("MisCitasFragment", "SharedPreferences usuarioId: $usuarioId")

        Log.d("MisCitasFragment", "Usuario ID obtenido: $usuarioId")

        if (usuarioId > 0) {
            // Ocultar el campo de búsqueda manual ya que cargamos automáticamente
            binding.layoutBusqueda.visibility = View.GONE
            binding.etPacienteId.setText(usuarioId.toString())
            viewModel.cargarMisCitas(usuarioId)
        } else {
            Log.e("MisCitasFragment", "No se encontró ID de usuario en SharedPreferences")
            Toast.makeText(requireContext(), "Error: No se pudo obtener el ID del usuario", Toast.LENGTH_LONG).show()
            // Mostrar el campo de búsqueda manual como fallback
            binding.layoutBusqueda.visibility = View.VISIBLE
        }
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
                Toast.makeText(requireContext(), "Por favor, ingresa un ID de paciente válido.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupObservers() {
        viewModel.misCitas.observe(viewLifecycleOwner) { citas ->
            Log.d("MisCitasFragment", "Citas recibidas: ${citas.size}")
            adapter.submitList(citas)

            // Mostrar mensaje si no hay citas
            if (citas.isEmpty()) {
                binding.txtSinCitas.visibility = View.VISIBLE
                binding.rvMisCitas.visibility = View.GONE
            } else {
                binding.txtSinCitas.visibility = View.GONE
                binding.rvMisCitas.visibility = View.VISIBLE
            }

            // Detener el refresh cuando se actualicen los datos
            binding.swipeRefreshLayout.isRefreshing = false
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            Log.d("MisCitasFragment", "Loading state: $isLoading")

            // Sincronizar el SwipeRefreshLayout con el estado de carga del ViewModel
            if (!isLoading) {
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }

        viewModel.toastMessage.observe(viewLifecycleOwner) { message ->
            if (message.isNotEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                Log.d("MisCitasFragment", "Toast message: $message")
            }
        }
    }

    private fun mostrarDialogoCancelacion(cita: MiCita) {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirmar Cancelación")
            .setMessage("¿Estás seguro de que deseas cancelar tu cita con el Dr. ${cita.nombreDoctor} ${cita.apellidoDoctor}?")
            .setPositiveButton("Sí, Cancelar") { _, _ ->
                viewModel.cancelarCita(cita.citaId)
            }
            .setNegativeButton("No", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun recargarDatos() {
        Log.d("MisCitasFragment", "Recargando datos...")
        cargarCitasAutomaticamente()
        // Ya no necesitamos el Handler aquí, el refresh se detiene automáticamente
        // cuando se actualicen los datos en los observers
    }
}