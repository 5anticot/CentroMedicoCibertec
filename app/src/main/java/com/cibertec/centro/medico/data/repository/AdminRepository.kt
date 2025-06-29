package com.cibertec.centro.medico.data.repository

import com.cibertec.centro.medico.data.model.UsuarioUpdate
import com.cibertec.centro.medico.data.network.RetrofitClient

class AdminRepository {
    private val authApiService = RetrofitClient.instance

    suspend fun getUsuarios() = authApiService.getUsuarios()

    suspend fun actualizarUsuario(usuario: UsuarioUpdate) = authApiService.actualizarUsuario(usuario)

    suspend fun eliminarUsuario(usuarioId: Int) = authApiService.eliminarUsuario(usuarioId)

    suspend fun getUsuarioById(usuarioId: Int) =
        authApiService.getUsuarioById(usuarioId)

}