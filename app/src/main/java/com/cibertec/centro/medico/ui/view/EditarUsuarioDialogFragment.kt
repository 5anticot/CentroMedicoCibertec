package com.cibertec.centro.medico.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.cibertec.centro.medico.data.model.UsuarioResponse
import com.cibertec.centro.medico.data.model.UsuarioUpdate
import com.cibertec.centro.medico.databinding.DialogEditarUsuarioBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class EditarUsuarioDialogFragment(
    private val usuario: UsuarioResponse,
    private val onUsuarioActualizado: (UsuarioUpdate) -> Unit, // Cambiado a UsuarioUpdate
    private val onUsuarioEliminado: ((UsuarioResponse) -> Unit)? = null
) : DialogFragment() {

    private var _binding: DialogEditarUsuarioBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogEditarUsuarioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()

        // Configurar el diálogo como modal
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private fun setupViews() {
        // Configurar dropdown de especialidades con sus IDs
        val especialidades = mapOf(
            "Neurología" to 1,
            "Gastroenterología" to 2,
            "Oftalmología" to 3,
            "Cardiología" to 4,
            "Dermatología" to 5,
            "Pediatría" to 6
        )

        val especialidadAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            especialidades.keys.toTypedArray()
        )
        binding.spinnerEspecialidad.setAdapter(especialidadAdapter)

        // Llenar campos con datos actuales
        binding.apply {
            // Separar el nombre completo en nombre y apellido
            val nombreCompleto = usuario.nombreCompleto ?: ""
            val partesNombre = nombreCompleto.split(" ", limit = 2)

            etNombre.setText(partesNombre.getOrNull(0) ?: "")
            etApellido.setText(partesNombre.getOrNull(1) ?: "")

            etCorreoElectronico.setText(usuario.correoElectronico ?: "")
            etTelefono.setText(usuario.telefono ?: "")

            // Mostrar especialidad solo si es doctor
            if (usuario.rol?.lowercase() == "doctor") {
                layoutEspecialidad.visibility = View.VISIBLE
                // Si el usuario tiene especialidad, seleccionarla
                usuario.especialidad?.let { especialidad ->
                    spinnerEspecialidad.setText(especialidad, false)
                }
            } else {
                layoutEspecialidad.visibility = View.GONE
            }

            // Configurar botones
            btnCancelar.setOnClickListener {
                dismiss()
            }

            btnGuardar.setOnClickListener {
                guardarCambios()
            }

            // Solo mostrar botón eliminar si se proporcionó el callback
            if (onUsuarioEliminado != null) {
                btnEliminar.visibility = View.VISIBLE
                btnEliminar.setOnClickListener {
                    mostrarDialogoConfirmacionEliminacion()
                }
            } else {
                btnEliminar.visibility = View.GONE
            }
        }
    }

    private fun guardarCambios() {
        if (!validarCampos()) {
            return
        }

        // Combinar nombre y apellido para crear el nombre completo
        val nombre = binding.etNombre.text.toString().trim()
        val apellido = binding.etApellido.text.toString().trim()

        // Obtener especialidad ID si es doctor
        val especialidadId = if (usuario.rol?.lowercase() == "doctor") {
            val especialidadSeleccionada = binding.spinnerEspecialidad.text.toString().trim()
            getEspecialidadId(especialidadSeleccionada)
        } else null

        // Crear UsuarioUpdate en lugar de UsuarioResponse
        val usuarioUpdate = UsuarioUpdate(
            usuarioId = usuario.usuarioId,
            nombre = nombre,
            apellido = apellido,
            correoElectronico = binding.etCorreoElectronico.text.toString().trim(),
            telefono = binding.etTelefono.text.toString().trim(),
            especialidadId = especialidadId
        )

        onUsuarioActualizado(usuarioUpdate)
        dismiss()
    }

    private fun getEspecialidadId(especialidad: String): Int? {
        val especialidades = mapOf(
            "Neurología" to 1,
            "Gastroenterología" to 2,
            "Oftalmología" to 3,
            "Cardiología" to 4,
            "Dermatología" to 5,
            "Pediatría" to 6
        )
        return especialidades[especialidad]
    }

    private fun validarCampos(): Boolean {
        binding.apply {
            // Limpiar errores previos
            etNombre.error = null
            etApellido.error = null
            etCorreoElectronico.error = null
            etTelefono.error = null

            // Validar nombre
            if (etNombre.text.toString().trim().isEmpty()) {
                etNombre.error = "El nombre es requerido"
                etNombre.requestFocus()
                return false
            }

            // Validar apellido
            if (etApellido.text.toString().trim().isEmpty()) {
                etApellido.error = "El apellido es requerido"
                etApellido.requestFocus()
                return false
            }

            // Validar especialidad para doctores
            if (usuario.rol?.lowercase() == "doctor" && layoutEspecialidad.visibility == View.VISIBLE) {
                if (spinnerEspecialidad.text.toString().trim().isEmpty()) {
                    spinnerEspecialidad.error = "La especialidad es requerida para doctores"
                    spinnerEspecialidad.requestFocus()
                    return false
                }
            }

            // Validar email
            val email = etCorreoElectronico.text.toString().trim()
            if (email.isEmpty()) {
                etCorreoElectronico.error = "El correo electrónico es requerido"
                etCorreoElectronico.requestFocus()
                return false
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etCorreoElectronico.error = "Formato de correo electrónico inválido"
                etCorreoElectronico.requestFocus()
                return false
            }

            // Validar teléfono (corregido: debe ser diferente de 9, no igual a 7)
            val telefono = etTelefono.text.toString().trim()
            if (telefono.isNotEmpty() && telefono.length != 9) {
                etTelefono.error = "El teléfono debe tener 9 dígitos"
                etTelefono.requestFocus()
                return false
            }
        }
        return true
    }

    private fun mostrarDialogoConfirmacionEliminacion() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Confirmar eliminación")
            .setMessage("¿Estás seguro de que deseas eliminar este usuario? Esta acción no se puede deshacer.")
            .setPositiveButton("Eliminar") { _, _ ->
                onUsuarioEliminado?.invoke(usuario)
                dismiss()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(
            usuario: UsuarioResponse,
            onUsuarioActualizado: (UsuarioUpdate) -> Unit, // Cambiado a UsuarioUpdate
            onUsuarioEliminado: ((UsuarioResponse) -> Unit)? = null
        ) = EditarUsuarioDialogFragment(usuario, onUsuarioActualizado, onUsuarioEliminado)
    }
}