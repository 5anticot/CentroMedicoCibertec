package com.cibertec.centro.medico.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cibertec.centro.medico.data.model.MiCita
import com.cibertec.centro.medico.data.repository.CitaRepository
import kotlinx.coroutines.launch

class MisCitasViewModel : ViewModel() {
    private val repository = CitaRepository()

    private val _misCitas = MutableLiveData<List<MiCita>>()
    val misCitas: LiveData<List<MiCita>> = _misCitas

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _toastMessage

    private var currentPacienteId: Int? = null

    fun cargarMisCitas(pacienteId: Int) {
        currentPacienteId = pacienteId
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getMisCitas(pacienteId)
                if (response.isSuccessful) {
                    _misCitas.value = response.body() ?: emptyList()
                    if (response.body().isNullOrEmpty()) {
                        _toastMessage.value = "No se encontraron citas para este ID."
                    }
                } else {
                    _misCitas.value = emptyList()
                    _toastMessage.value = "Error al cargar citas: ${response.message()}"
                    Log.e("MisCitasVM", "Error en respuesta: ${response.code()} - ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _misCitas.value = emptyList()
                _toastMessage.value = "Error de conexión: ${e.message}"
                Log.e("MisCitasVM", "Excepción: ", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun cancelarCita(citaId: Int) {
        viewModelScope.launch {
            try {
                val response = repository.cancelarCita(citaId)
                if (response.isSuccessful) {
                    _toastMessage.value = "Cita cancelada correctamente."
                    // Recargar la lista para que se actualice el estado
                    currentPacienteId?.let { cargarMisCitas(it) }
                } else {
                    _toastMessage.value = "Error al cancelar la cita: ${response.message()}"
                }
            } catch (e: Exception) {
                _toastMessage.value = "Error de conexión al cancelar: ${e.message}"
            }
        }
    }
}
