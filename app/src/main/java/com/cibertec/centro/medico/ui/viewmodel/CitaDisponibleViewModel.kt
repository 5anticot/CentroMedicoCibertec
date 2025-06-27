package com.cibertec.centro.medico.ui.viewmodel

import androidx.lifecycle.*
import com.cibertec.centro.medico.data.model.CitaDisponible
import com.cibertec.centro.medico.data.model.Especialidad
import com.cibertec.centro.medico.data.repository.CitaRepository
import kotlinx.coroutines.launch

class CitasDisponiblesViewModel : ViewModel() {

    private var lastEspecialidadId: Int? = null
    private var lastFechaInicio: String? = null
    private var lastFechaFin: String? = null
    private var lastTurno: String? = null

    private val repository = CitaRepository()

    // LiveData para la lista de citas disponibles
    private val _citasDisponibles = MutableLiveData<List<CitaDisponible>>()
    val citasDisponibles: LiveData<List<CitaDisponible>> = _citasDisponibles

    // LiveData para las especialidades del Spinner
    private val _especialidades = MutableLiveData<List<Especialidad>>()
    val especialidades: LiveData<List<Especialidad>> = _especialidades

    // LiveData para el estado de carga (mostrar/ocultar ProgressBar)
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // LiveData para mostrar mensajes (ej. errores o éxito)
    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _toastMessage

    // Se llama al crear el ViewModel para cargar las especialidades
    init {
        cargarEspecialidades()
        // Cargar todas las citas al inicio
        cargarCitasDisponibles(null, null, null, null)
    }


    fun cargarEspecialidades() {
        viewModelScope.launch {
            try {
                val response = repository.getEspecialidades()
                if (response.isSuccessful) {
                    // Añadimos una opción "Todas" al principio
                    val listaConTodos = mutableListOf(Especialidad(0, "Todas las especialidades"))
                    listaConTodos.addAll(response.body()!!)
                    _especialidades.value = listaConTodos
                } else {
                    _toastMessage.value = "Error al cargar especialidades"
                }
            } catch (e: Exception) {
                _toastMessage.value = "Error de conexión: ${e.message}"
            }
        }
    }

    fun cargarCitasDisponibles(especialidadId: Int?, fechaInicio: String?, fechaFin: String?, turno: String?) {
        lastEspecialidadId = especialidadId
        lastFechaInicio = fechaInicio
        lastFechaFin = fechaFin
        lastTurno = turno

        _isLoading.value = true
        viewModelScope.launch {
            try {
                // --- CORRECCIÓN AQUÍ ---
                // Si el especialidadId que recibimos es 0, lo convertimos a null para la llamada a la API.
                // Si es cualquier otro número, lo usamos tal cual.
                val idParaApi = if (especialidadId == 0) null else especialidadId

                val response = repository.getCitasPaciente(idParaApi, fechaInicio, fechaFin, turno)
                if (response.isSuccessful) {
                    _citasDisponibles.value = response.body() ?: emptyList()
                } else {
                    _toastMessage.value = "Error al cargar citas: ${response.message()}"
                    _citasDisponibles.value = emptyList()
                }
            } catch (e: Exception) {
                _toastMessage.value = "Error de conexión: ${e.message}"
                _citasDisponibles.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun reservarCita(citaId: Int, pacienteId: Int) {
        viewModelScope.launch {
            try {
                val response = repository.reservarCita(citaId, pacienteId)
                if (response.isSuccessful) {
                    _toastMessage.value = "¡Cita reservada con éxito!"
                    // Volver a cargar la lista CON LOS FILTROS ACTUALES
                    cargarCitasDisponibles(lastEspecialidadId, lastFechaInicio, lastFechaFin, lastTurno)
                } else {
                    _toastMessage.value = "Error al reservar la cita: ${response.message()}"
                }
            } catch (e: Exception) {
                _toastMessage.value = "Error de conexión al reservar: ${e.message}"
            }
        }
    }

}
