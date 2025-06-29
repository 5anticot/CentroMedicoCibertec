package com.cibertec.centro.medico.data.network

import com.cibertec.centro.medico.data.model.LoginRequest
import com.cibertec.centro.medico.data.model.RegisterRequest
import com.cibertec.centro.medico.data.model.Usuario
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AuthApiService {

    @POST("api/Usuarios/LoginUsuario")
    fun loginUsuario(
        @Body request : LoginRequest
    ): Call<Usuario>

    @POST("api/Usuarios/RegistrarUsuario")
    fun registrarUsuario(
        @Body request: RegisterRequest
    ): Call<Unit>


}