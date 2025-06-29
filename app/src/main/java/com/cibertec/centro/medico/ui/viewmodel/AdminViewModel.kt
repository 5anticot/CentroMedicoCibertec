package com.cibertec.centro.medico.ui.viewmodel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cibertec.centro.medico.data.model.RegisterRequest
import com.cibertec.centro.medico.data.model.UsuarioResponse
import com.cibertec.centro.medico.data.model.UsuarioUpdate
import com.cibertec.centro.medico.data.repository.AdminRepository
import kotlinx.coroutines.launch

class AdminViewModel(
    private val adminRepository: AdminRepository = AdminRepository(),
) : ViewModel() {

    private val _usuarios = MutableLiveData<List<UsuarioResponse>>()
    val usuarios: LiveData<List<UsuarioResponse>> = _usuarios

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _toastMessage

    private val _usuarioActualizado = MutableLiveData<Boolean>()
    val usuarioActualizado: LiveData<Boolean> = _usuarioActualizado

    private val _usuarioEliminado = MutableLiveData<Boolean>()
    val usuarioEliminado: LiveData<Boolean> = _usuarioEliminado

    init {
        // Inicializar valores por defecto
        _usuarios.value = emptyList()
        _isLoading.value = false
        _toastMessage.value = ""
        _usuarioActualizado.value = false
        _usuarioEliminado.value = false
    }

    /**
     * Carga la lista de usuarios
     */
    fun cargarUsuarios() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = adminRepository.getUsuarios()
                if (response.isSuccessful) {
                    val resultado = response.body() ?: emptyList()
                    _usuarios.value = resultado
                } else {
                    _toastMessage.value = "Error al cargar usuarios: Código ${response.code()}"
                }
            } catch (e: Exception) {
                _toastMessage.value = "Error al cargar usuarios: ${e.localizedMessage ?: "Error desconocido"}"
                _usuarios.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

//    fun registrarUsuario(email: String, password: String, rol: String, name: String, lastName: String, telf: String, especialidad: String? = null) {
//        viewModelScope.launch {
//            _isLoading.value = true
//            try {
//                val request = RegisterRequest(email, password, rol, name, lastName, telf, especialidad)
//                val response = adminRepository.(request) // Asegúrate de que este método esté implementado en tu repositorio
//                if (response.isSuccessful) {
//                    // Aquí puedes manejar la respuesta si es necesario
//                    _toastMessage.value = "Usuario registrado exitosamente"
//                } else {
//                    _toastMessage.value = "Error al registrar usuario: Código ${response.code()}"
//                }
//            } catch (e: Exception) {
//                _toastMessage.value = "Error al registrar usuario: ${e.localizedMessage ?: "Error desconocido"}"
//            } finally {
//                _isLoading.value = false
//            }
//        }
//    }


    /**
     * Actualiza un usuario existente
     */
    fun actualizarUsuario(usuario: UsuarioUpdate) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val resultado = adminRepository.actualizarUsuario(usuario)
                if (resultado.isSuccessful) {
                    Log.d("AdminViewModel", "Usuario actualizado: ${resultado.body()}")
                    _usuarioActualizado.value = true
                } else {
                    _toastMessage.value = "Error al actualizar el usuario: Código ${resultado.code()}"
                }
            } catch (e: Exception) {
                _toastMessage.value = "Error al actualizar usuario: ${e.localizedMessage ?: "Error desconocido"}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Elimina un usuario por ID
     */
    fun eliminarUsuario(usuarioId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val resultado = adminRepository.eliminarUsuario(usuarioId)
                if (resultado.isSuccessful) {
                    _usuarioEliminado.value = true
                } else {
                    _toastMessage.value = "Error al eliminar el usuario: Código ${resultado.code()}"
                }
            } catch (e: Exception) {
                _toastMessage.value = "Error al eliminar usuario: ${e.localizedMessage ?: "Error desconocido"}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Marca como manejado el evento de usuario actualizado
     */
    fun onUsuarioActualizadoHandled() {
        _usuarioActualizado.value = false
    }

    /**
     * Marca como manejado el evento de usuario eliminado
     */
    fun onUsuarioEliminadoHandled() {
        _usuarioEliminado.value = false
    }

    /**
     * Limpia el mensaje de toast después de mostrarlo
     */
    fun clearToastMessage() {
        _toastMessage.value = ""
    }
}