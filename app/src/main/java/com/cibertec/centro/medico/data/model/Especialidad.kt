package com.cibertec.centro.medico.data.model

import androidx.lifecycle.GeneratedAdapter
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Especialidad (
    @Json(name = "value") val especialidadId : Int,
    @Json(name = "name") val nombre : String
)