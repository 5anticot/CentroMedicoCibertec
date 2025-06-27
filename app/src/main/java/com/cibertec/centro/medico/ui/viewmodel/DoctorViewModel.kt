package com.cibertec.centro.medico.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cibertec.centro.medico.data.model.CitaDoctor
import com.cibertec.centro.medico.data.model.CrearCitaRequest
import com.cibertec.centro.medico.data.repository.CitaRepository
import kotlinx.coroutines.launch


class DoctorViewModel : ViewModel() {
    private val repository = CitaRepository()

    private val _citasDoctor = MutableLiveData<List<CitaDoctor>>()
    val citasDoctor: LiveData<List<CitaDoctor>> = _citasDoctor

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _toastMessage

    private val _citaCreada = MutableLiveData<Boolean>(false)
    val citaCreada: LiveData<Boolean> = _citaCreada

    fun cargarCitasDoctor(doctorId: Int, estado: String?) {
        _isLoading.value = true
        // Si el estado es "TODOS", lo enviamos como null a la API
        val estadoApi = if (estado.equals("TODOS", ignoreCase = true)) null else estado

        viewModelScope.launch {
            try {
                val response = repository.getCitasDoctor(doctorId, estadoApi)
                if (response.isSuccessful) {
                    _citasDoctor.value = response.body() ?: emptyList()
                    if (response.body().isNullOrEmpty()) {
                        _toastMessage.value = "No se encontraron citas con los filtros seleccionados."
                    }
                } else {
                    _citasDoctor.value = emptyList()
                    _toastMessage.value = "Error al cargar citas: ${response.message()}"
                    Log.e("DoctorVM", "Error en respuesta: ${response.code()} - ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _citasDoctor.value = emptyList()
                _toastMessage.value = "Error de conexión: ${e.message}"
                Log.e("DoctorVM", "Excepción: ", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun crearCita(doctorId: Int, fecha: String, hora: String) {
        viewModelScope.launch {
            try {
                val request = CrearCitaRequest(
                    doctorId = doctorId,
                    fechaCita = fecha,
                    horaCita = hora
                )
                val response = repository.crearCita(request)
                if (response.isSuccessful) {
                    _toastMessage.value = "Cita creada con éxito."
                    _citaCreada.value = true
                } else {
                    // Ahora podemos dar un error más específico si queremos
                    val errorBody = response.errorBody()?.string()
                    _toastMessage.value = "Error al crear la cita: ${response.message()} ($errorBody)"
                    _citaCreada.value = false
                }
            } catch (e: Exception) {
                _toastMessage.value = "Error de conexión al crear: ${e.message}"
                _citaCreada.value = false
            }
        }
    }


    fun onCitaCreadaHandled() {
        _citaCreada.value = false
    }
}
