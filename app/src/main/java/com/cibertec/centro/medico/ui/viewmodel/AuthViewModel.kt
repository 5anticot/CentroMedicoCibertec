package com.cibertec.centro.medico.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cibertec.centro.medico.data.model.LoginRequest
import com.cibertec.centro.medico.data.model.RegisterRequest
import com.cibertec.centro.medico.data.model.Usuario
import com.cibertec.centro.medico.data.network.RetrofitClient
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    fun registerUser(registerRequest: RegisterRequest, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.registrarUsuario(registerRequest)
                if (response.isSuccessful) {
                    Log.d("AuthViewModel", "Registro exitoso ${response.body()}" )
                    onResult(true, null)
                } else {
                    onResult(false, "Error: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                onResult(false, "Exception: ${e.message}")
            }
        }
    }

    fun loginUser(loginRequest: LoginRequest, onResult: (Usuario?, String?) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.loginUsuario(loginRequest)
                if (response.isSuccessful) {
                    onResult(response.body(), null)
                } else {
                    onResult(null, "Error: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                onResult(null, "Exception: ${e.message}")
            }
        }
    }

    fun getUsuarioById(usuarioId: Int, onResult: (Usuario?) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.getUsuarioById(usuarioId)
                if (response.isSuccessful) {
                    onResult(response.body())
                    Log.d("AuthViewModel", "Usuario obtenido exitosamente: ${response.body()}")
                } else {
                    Log.e("AuthViewModel", "Error al obtener usuario por ID: ${response.code()} - ${response.message()}")
                    onResult(null)
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Exception al obtener usuario por ID: ${e.message}")
                onResult(null)
            }
        }
    }

}