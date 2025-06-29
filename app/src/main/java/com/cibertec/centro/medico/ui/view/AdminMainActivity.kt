package com.cibertec.centro.medico.ui.view

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.cibertec.centro.medico.R

import com.cibertec.centro.medico.data.model.UsuarioResponse
import com.cibertec.centro.medico.data.model.UsuarioUpdate
import com.cibertec.centro.medico.databinding.ActivityAdminMainBinding
import com.cibertec.centro.medico.ui.adapter.UsuariosAdapter
import com.cibertec.centro.medico.ui.viewmodel.AdminViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class AdminMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminMainBinding
    private val viewModel: AdminViewModel by viewModels()
    private lateinit var adapter: UsuariosAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupObservers()

        // Cargar usuarios al iniciar
        viewModel.cargarUsuarios()
        configurarMenuOpciones()


    }

    private fun configurarMenuOpciones() {
        val btnMenu = findViewById<ImageButton>(R.id.btnMenu)

        btnMenu.setOnClickListener { view ->
            mostrarMenuOpciones(view)
        }
    }

    private fun mostrarMenuOpciones(anchorView: View) {
        val popup = PopupMenu(this, anchorView)

        // Inflar el menú
        popup.menuInflater.inflate(R.menu.menu_opciones, popup.menu)

        // Configurar el listener para los clicks
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_cerrar_sesion -> {
                    confirmarCerrarSesion()
                    true
                }
                else -> false
            }
        }

        // Mostrar el menú
        popup.show()
    }

    private fun confirmarCerrarSesion() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Cerrar Sesión")
            .setMessage("¿Estás seguro de que quieres cerrar la sesión?")
            .setPositiveButton("Cerrar Sesión") { _, _ ->
                cerrarSesion()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun cerrarSesion() {
        // Aquí implementas tu lógica de cerrar sesión

        // 1. Limpiar SharedPreferences/datos de sesión
        val sharedPrefs = getSharedPreferences("user_session", Context.MODE_PRIVATE)
        sharedPrefs.edit().clear().apply()

        finish()

        // 3. Opcional: Mostrar mensaje
        Toast.makeText(this, "Sesión cerrada exitosamente", Toast.LENGTH_SHORT).show()
    }


    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowHomeEnabled(false)
        supportActionBar?.title = "Portal de administrador"
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        adapter = UsuariosAdapter(
            fragmentManager = supportFragmentManager,
            onUsuarioActualizado = { usuarioUpdate ->
                // Ahora recibe UsuarioUpdate directamente
                actualizarUsuario(usuarioUpdate)
            },
            onUsuarioEliminado = { usuarioEliminado ->
                eliminarUsuario(usuarioEliminado)
            }
        )
        binding.rvUsuarios.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.usuarios.observe(this) { usuarios ->
            adapter.submitList(usuarios)

            // Mostrar mensaje si no hay usuarios
            if (usuarios.isEmpty()) {
                binding.tvEmptyState.visibility = View.VISIBLE
                binding.rvUsuarios.visibility = View.GONE
            } else {
                binding.tvEmptyState.visibility = View.GONE
                binding.rvUsuarios.visibility = View.VISIBLE
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.toastMessage.observe(this) { message ->
            if (message.isNotEmpty()) {
                mostrarMensajeError(message)
                viewModel.clearToastMessage() // Limpiar mensaje después de mostrarlo
            }
        }

        viewModel.usuarioActualizado.observe(this) { actualizado ->
            if (actualizado) {
                mostrarMensajeExito("Usuario actualizado correctamente")
                viewModel.onUsuarioActualizadoHandled()
                // Recargar la lista de usuarios para reflejar los cambios
                viewModel.cargarUsuarios()
            }
        }

        viewModel.usuarioEliminado.observe(this) { eliminado ->
            if (eliminado) {
                mostrarMensajeExito("Usuario eliminado correctamente")
                viewModel.onUsuarioEliminadoHandled()
                // Recargar la lista de usuarios para reflejar los cambios
                viewModel.cargarUsuarios()
            }
        }
    }

    /**
     * Actualiza un usuario usando el ViewModel
     */
    private fun actualizarUsuario(usuarioUpdate: UsuarioUpdate) {
        viewModel.actualizarUsuario(usuarioUpdate)
    }

    /**
     * Elimina un usuario usando el ViewModel
     */
    private fun eliminarUsuario(usuario: UsuarioResponse) {
        viewModel.eliminarUsuario(usuario.usuarioId)
    }

    /**
     * Muestra un mensaje de éxito usando Snackbar
     */
    private fun mostrarMensajeExito(mensaje: String) {
        Snackbar.make(binding.root, mensaje, Snackbar.LENGTH_LONG)
            .setBackgroundTint(getColor(android.R.color.holo_green_dark))
            .setTextColor(getColor(android.R.color.white))
            .setAction("OK") { }
            .show()
    }

    /**
     * Muestra un mensaje de error usando Snackbar
     */
    private fun mostrarMensajeError(mensaje: String) {
        Snackbar.make(binding.root, mensaje, Snackbar.LENGTH_LONG)
            .setBackgroundTint(getColor(android.R.color.holo_red_dark))
            .setTextColor(getColor(android.R.color.white))
            .show()
    }
}