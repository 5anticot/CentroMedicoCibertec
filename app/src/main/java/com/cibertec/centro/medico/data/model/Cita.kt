package com.cibertec.centro.medico.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


// Para USP_LISTAR_CITAS_PARA_PACIENTE
@JsonClass(generateAdapter = true)
data class CitaDisponible (
    @Json(name = "citaId") val citaId : Int,
    @Json(name = "nombreDoctor") val nombreDoctor : String,
    @Json(name = "apellidoDoctor") val apellidoDoctor : String,
    @Json(name = "especialidad") val especialidad : String,
    @Json(name = "fechaCita") val fechaCita : String,
    @Json(name = "horaCita") val horaCita : String,
    @Json(name = "estado") val estado : String,
)


// Para USP_LISTAR_CITAS (Mis Citas)

@JsonClass(generateAdapter = true)
data class MiCita(
    @Json(name = "citaId") val citaId: Int,
    @Json(name = "nombreDoctor") val nombreDoctor: String,
    @Json(name = "apellidoDoctor") val apellidoDoctor: String,
    @Json(name = "especialidad") val especialidad: String,
    @Json(name = "fechaCita") val fechaCita: String,
    @Json(name = "horaCita") val horaCita: String,
    @Json(name = "estado") val estado: String
)


// Para USP_LISTAR_CITAS_PARA_DOCTOR
@JsonClass(generateAdapter = true)
data class CitaDoctor(
    @Json(name = "citaId") val citaId: Int,
    @Json(name = "nombrePaciente") val nombrePaciente: String?,
    @Json(name = "apellidoPaciente") val apellidoPaciente: String?,
    @Json(name = "fechaCita") val fechaCita: String,
    @Json(name = "horaCita") val horaCita: String,
    @Json(name = "estado") val estado: String
)

@JsonClass(generateAdapter = true)
data class CrearCitaRequest(
    val doctorId: Int,
    val fechaCita: String,
    val horaCita: String
)