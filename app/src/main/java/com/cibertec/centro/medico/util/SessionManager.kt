package com.cibertec.centro.medico.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.ui.input.key.Key

class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("sesion", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = prefs.edit()

    companion object {
        const val KEY_USUARIO_ID = "usuarioId"
        const val KEY_USUARIO_NOMBRE = "usuarioNombre"
        const val KEY_USUARIO_APELLIDO = "usuarioApellido"
        const val KEY_USUARIO_EMAIL = "usuarioEmail"
        const val KEY_USUARIO_ROL = "usuarioRol"
        const val KEY_USUARIO_TELEFONO = "usuarioTelefono"
        const val KEY_DOCTOR_ESPECIALIDAD_ID = "especialidadId"
        const val KEY_DOCTOR_ESPECIALIDAD = "especialidad"
        const val KEY_IS_LOGGED_IN = "isLoggedIn"

    }

    /**
     * Guardar datos de sesión al hacer login
     */
    fun saveSession(
        usuarioId: Int,
        nombre: String,
        apellido: String,
        email: String,
        rol: String,
        telefono: String,
        especialidadId: Int? = null,
        especialidad: String?

    ) {
        editor.apply {
            putInt(KEY_USUARIO_ID, usuarioId)
            putString(KEY_USUARIO_NOMBRE, nombre)
            putString(KEY_USUARIO_APELLIDO, apellido)
            putString(KEY_USUARIO_EMAIL, email)
            putString(KEY_USUARIO_ROL, rol)
            putBoolean(KEY_IS_LOGGED_IN, true)
            putString(KEY_USUARIO_TELEFONO, telefono)
            putString(KEY_DOCTOR_ESPECIALIDAD, especialidadId?.toString())
            especialidadId?.let { putInt(KEY_DOCTOR_ESPECIALIDAD_ID, it) }
            apply()
        }
    }

    fun clearSession() {
        editor.clear().apply()
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }


    fun getUsuarioId(): Int {
        return prefs.getInt(KEY_USUARIO_ID, 0)
    }


    fun getUsuarioNombre(): String? {
        return prefs.getString(KEY_USUARIO_NOMBRE, null)
    }

    fun getUsuarioApellido(): String? {
        return prefs.getString(KEY_USUARIO_APELLIDO, null)
    }

    fun getUsuarioEmail(): String? {
        return prefs.getString(KEY_USUARIO_EMAIL, null)
    }

    fun getUsuarioRol(): String? {
        return prefs.getString(KEY_USUARIO_ROL, null)
    }

    fun getUsuarioTelefono(): String? {
        return prefs.getString(KEY_USUARIO_TELEFONO, null)
    }

    fun getEspecialidadId(): Int? {
        return if (prefs.contains(KEY_DOCTOR_ESPECIALIDAD_ID)) {
            prefs.getInt(KEY_DOCTOR_ESPECIALIDAD_ID, 0)
        } else {
            null
        }
    }

    fun getEspecialidad(): String? {
        return prefs.getString(KEY_DOCTOR_ESPECIALIDAD, null)
    }
    fun updateSessionData(key: String, value: Any) {
        when (value) {
            is String -> editor.putString(key, value)
            is Int -> editor.putInt(key, value)
            is Boolean -> editor.putBoolean(key, value)
            is Float -> editor.putFloat(key, value)
            is Long -> editor.putLong(key, value)
        }
        editor.apply()
    }


}