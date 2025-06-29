package com.cibertec.centro.medico.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cibertec.centro.medico.R
import com.cibertec.centro.medico.data.model.UsuarioResponse
import com.cibertec.centro.medico.data.model.UsuarioUpdate
import com.cibertec.centro.medico.databinding.ItemUsuarioBinding
import com.cibertec.centro.medico.ui.dialog.EditarUsuarioDialogFragment

class UsuariosAdapter(
    private val fragmentManager: FragmentManager,
    private val onUsuarioActualizado: (UsuarioUpdate) -> Unit, // Cambiado a UsuarioUpdate
    private val onUsuarioEliminado: ((UsuarioResponse) -> Unit)? = null
) : ListAdapter<UsuarioResponse, UsuariosAdapter.UsuarioViewHolder>(UsuarioDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        val binding = ItemUsuarioBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UsuarioViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class UsuarioViewHolder(
        private val binding: ItemUsuarioBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(usuario: UsuarioResponse) {
            binding.apply {
                // Mostrar nombre completo
                val nombreCompleto = usuario.nombreCompleto?.takeIf { it.isNotEmpty() } ?: "Usuario sin nombre"
                tvNombre.text = nombreCompleto

                // Mostrar correo electrónico
                tvEmail.text = usuario.correoElectronico?.takeIf { it.isNotEmpty() } ?: "Sin email"

                // Mostrar rol con formato capitalizado
                val rolTexto = usuario.rol?.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase() else it.toString()
                } ?: "Desconocido"
                tvRol.text = rolTexto

                // Aplicar colores según el rol
                when (usuario.rol?.lowercase()) {
                    "paciente" -> {
                        tvRol.setTextColor(ContextCompat.getColor(itemView.context, R.color.text_light))
                    }
                    "doctor" -> {
                        tvRol.setTextColor(ContextCompat.getColor(itemView.context, R.color.primary_dark_blue))
                    }
                    "administrador" -> {
                        tvRol.setTextColor(ContextCompat.getColor(itemView.context, R.color.crimson_red))
                    }
                    else -> {
                        tvRol.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.darker_gray))
                    }
                }

                // Configurar click del botón "Ver más"
                btnVerMas.setOnClickListener {
                    val dialog = EditarUsuarioDialogFragment.newInstance(
                        usuario = usuario,
                        onUsuarioActualizado = { usuarioUpdate ->
                            // Ahora recibe UsuarioUpdate y lo pasa al callback
                            onUsuarioActualizado(usuarioUpdate)
                        },
                        onUsuarioEliminado = onUsuarioEliminado
                    )
                    dialog.show(fragmentManager, "EditarUsuarioDialog")
                }
            }
        }
    }

    class UsuarioDiffCallback : DiffUtil.ItemCallback<UsuarioResponse>() {
        override fun areItemsTheSame(oldItem: UsuarioResponse, newItem: UsuarioResponse): Boolean {
            return oldItem.usuarioId == newItem.usuarioId
        }

        override fun areContentsTheSame(oldItem: UsuarioResponse, newItem: UsuarioResponse): Boolean {
            return oldItem == newItem
        }
    }
}