package com.cibertec.centro.medico.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Usuario(
    @Json(name = "usuarioId")
    val usuarioId: Int,
    @Json(name = "correoElectronico")
    val correoElectronico: String? = null,
    @Json(name = "contrasena")
    val contrasena: String? = null,
    @Json(name = "rol")
    val rol: String? = null,
    @Json(name = "nombre")
    val nombre: String? = "Usuario",
    @Json(name = "apellido")
    val apellido: String? = null,
    @Json(name = "telefono")
    val telefono: String? = null,
    @Json(name = "especialidad")
    val especialidad: String? = null,
    @Json(name = "especialidadId")
    val especialidadId: Int? = null
)

@JsonClass(generateAdapter = true)
data class LoginRequest(
    @Json(name = "correoElectronico") val correoElectronico: String,
    @Json(name = "contrasena") val contrasena: String
)

@JsonClass(generateAdapter = true)
data class RegisterRequest(
    @Json(name = "correoElectronico") val correoElectronico: String,
    @Json(name = "contrasena") val contrasena: String,
    @Json(name = "rol") val rol: String? = "PACIENTE",
    @Json(name = "nombre") val nombre: String,
    @Json(name = "apellido") val apellido: String,
    @Json(name = "telefono") val telefono: String,
    @Json(name = "especialidadId") val especialidadId: Int? = 0,
)

@JsonClass(generateAdapter = true)
data class UsuarioResponse(
    @Json(name = "usuarioId") val usuarioId: Int,
    @Json(name = "correoElectronico") val correoElectronico: String? = null,
    @Json(name = "rol") val rol: String? = null,
    @Json(name = "nombreCompleto") val nombreCompleto: String? = "Usuario",
    @Json(name = "telefono") val telefono: String? = null,
    @Json(name = "especialidad") val especialidad: String? = null
)

@JsonClass(generateAdapter = true)
data class UsuarioUpdate(
    @Json(name = "usuarioId")val usuarioId: Int,
    @Json(name = "correoElectronico")val correoElectronico: String,
    @Json(name ="nombre") val nombre: String,
    @Json(name = "apellido") val apellido: String,
    @Json(name = "telefono") val telefono: String,
    @Json(name = "especialidadId")val especialidadId : Int? = null
)
