package com.cibertec.centro.medico.data.network

import com.cibertec.centro.medico.data.model.*
import retrofit2.http.*
import retrofit2.*
interface ApiService
{

    // --- SELECTS antes especialidades ---

    @GET("api/Selects/GetEspecialidades")
    suspend fun getEspecialidades(): Response<List<Especialidad>>


    // VISTA: Citas Disponibles para Paciente
    @GET("api/Citas/GetCitasPaciente")
    suspend fun getCitasPaciente(
        @Query("EspedialidadId") especialidadId: Int?,
        @Query("FechaInicio") fechaInicio: String?,
        @Query("FechaFin") fechaFin: String?,
        @Query("Turno") turno: String?
    ): Response<List<CitaDisponible>>

    // VISTA: Mis Citas (citas de un paciente específico)
    // CORREGIDO: Endpoint y método de pasar el ID.
    // ¡OJO! {PacienteId} en la URL significa que usamos @Path, no @Query.
    @GET("api/Citas/CitaPacienteReservada/{PacienteId}")
    suspend fun getMisCitas(@Path("PacienteId") pacienteId: Int): Response<List<MiCita>>

    // VISTA: Citas para el Doctor
    // CORREGIDO: El endpoint es /api/Citas/GetCitasDoctor
    // NOTA: Asumimos que los filtros del SP (DoctorId, Estado) se pasan como @Query.
    @GET("api/Citas/GetCitasDoctor")
    suspend fun getCitasDoctor(
        @Query("DoctorId") doctorId: Int,
        @Query("Estado") estado: String?
    ): Response<List<CitaDoctor>>

    // ACCIÓN: Crear una nueva cita (disponible) por un Doctor
    // CORREGIDO: El endpoint es /api/Citas/CrearCita. El método es POST.
    @POST("api/Citas/CrearCita")
    suspend fun crearCita(
        @Body request: CrearCitaRequest
    ): Response<Unit>

    // ACCIÓN: Reservar una cita disponible
    // CORREGIDO: El método es PUT, no POST.
    @PUT("api/Citas/ReservarCita")
    suspend fun reservarCita(
        @Query("CitaId") citaId: Int,
        @Query("PacienteId") pacienteId: Int
    ): Response<Unit>

    // ACCIÓN: Cancelar una cita
    // CORREGIDO: El método es PUT, no POST.
    @PUT("api/Citas/CancelarCita")
    suspend fun cancelarCita(@Query("CitaId") citaId: Int): Response<Unit>

    // ACCIÓN: Marcar cita como atendida (Extra, para futuro)
    // Este no lo pediste, pero lo añado por si lo necesitas.
    @PUT("api/Citas/MarcarCitaAtendida")
    suspend fun marcarCitaAtendida(@Query("CitaId") citaId: Int): Response<Unit>







    // --- Usuarios ---




    @GET("api/Admins/GetUsuarios")
    suspend fun getUsuarios(): Response<List<UsuarioResponse>>

    @GET("api/Admins/GetUsuarioById/{usuarioId}")
    suspend fun getUsuarioById(
        @Path("usuarioId") usuarioId: Int
    ): Response<Usuario>

    @PUT("api/Admins/ActualizarUsuario")
    suspend fun actualizarUsuario(
        @Body usuario: UsuarioUpdate
    ): Response<Unit>

    @DELETE("api/Admins/EliminarUsuario/{usuarioId}")
    suspend fun eliminarUsuario(
        @Path("usuarioId") usuarioId: Int
    ): Response<Unit>


    // -- Autenticación --

    @POST("api/Usuarios/LoginUsuario")
    suspend fun loginUsuario(
        @Body request : LoginRequest
    ): Response<Usuario>

    @POST("api/Usuarios/RegistrarUsuario")
    suspend fun registrarUsuario(
        @Body request: RegisterRequest
    ): Response<Unit>



}