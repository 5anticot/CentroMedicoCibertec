package com.cibertec.centro.medico.data.repository // O el paquete donde lo tengas

import com.cibertec.centro.medico.data.model.CrearCitaRequest
import com.cibertec.centro.medico.data.network.RetrofitClient

class CitaRepository {
    private val apiService = RetrofitClient.instance

    // --- Métodos para Especialidades ---
    // Este nombre estaba bien, pero lo incluyo para que tengas el archivo completo.
    suspend fun getEspecialidades() = apiService.getEspecialidades()

    // --- Métodos para la vista "Citas Disponibles" ---
    // CORRECCIÓN: El método ahora se llama getCitasPaciente
    suspend fun getCitasPaciente(
        especialidadId: Int?,
        fechaInicio: String?,
        fechaFin: String?,
        turno: String?
    ) = apiService.getCitasPaciente(especialidadId, fechaInicio, fechaFin, turno)

    // CORRECCIÓN: El método para reservar cita
    suspend fun reservarCita(citaId: Int, pacienteId: Int) =
        apiService.reservarCita(citaId, pacienteId)


    // --- Métodos para la vista "Mis Citas" ---
    // Este nombre estaba bien
    suspend fun getMisCitas(pacienteId: Int) = apiService.getMisCitas(pacienteId)

    // CORRECCIÓN: El método para cancelar cita
    suspend fun cancelarCita(citaId: Int) = apiService.cancelarCita(citaId)


    // --- Métodos para la vista de "Doctor" ---
    // Método para obtener las citas de un doctor
    suspend fun getCitasDoctor(doctorId: Int, estado: String?) =
        apiService.getCitasDoctor(doctorId, estado)

    // Método para crear una cita
    suspend fun crearCita(request: CrearCitaRequest) =
        apiService.crearCita(request)

}