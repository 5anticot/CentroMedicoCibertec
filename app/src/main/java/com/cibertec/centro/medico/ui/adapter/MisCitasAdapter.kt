package com.cibertec.centro.medico.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cibertec.centro.medico.R
import com.cibertec.centro.medico.data.model.MiCita
import com.cibertec.centro.medico.databinding.ItemMiCitaBinding


class MisCitasAdapter(
    private val onCancelClicked: (MiCita) -> Unit
) : ListAdapter<MiCita, MisCitasAdapter.CitaViewHolder>(DiffCallback) {

    inner class CitaViewHolder(private val binding: ItemMiCitaBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cita: MiCita) {
            binding.tvEspecialidad.text = cita.especialidad
            binding.tvDoctorNombre.text = "Dr. ${cita.nombreDoctor} ${cita.apellidoDoctor}"
            binding.tvFecha.text = cita.fechaCita.substring(0, 10)
            binding.tvHora.text = cita.horaCita.substring(0, 5)

            val estadoCita = cita.estado.uppercase()
            binding.tvEstado.text = estadoCita

            // Lógica para el estado visual del botón y la etiqueta
            if (estadoCita == "RESERVADA") {
                // Estado normal para una cita que se puede cancelar
                binding.tvEstado.background.setTint(ContextCompat.getColor(binding.root.context, R.color.primary_blue))
                binding.btnCancelar.isEnabled = true
                binding.btnCancelar.alpha = 1.0f // Opacidad completa
                binding.btnCancelar.setOnClickListener {
                    onCancelClicked(cita)
                }
            } else {
                // Estado para citas CANCELADAS, ATENDIDAS, etc.
                // Cambiamos el color de la etiqueta a rojo si es cancelada
                val statusColor = if (estadoCita == "CANCELADA") {
                    android.R.color.holo_red_light
                } else {
                    // Otro color para otros estados como "ATENDIDA"
                    android.R.color.darker_gray
                }
                binding.tvEstado.background.setTint(ContextCompat.getColor(binding.root.context, statusColor))

                // Deshabilitamos el botón
                binding.btnCancelar.isEnabled = false
                binding.btnCancelar.alpha = 0.5f // Hacemos que se vea "apagado"
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitaViewHolder {
        val binding = ItemMiCitaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CitaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CitaViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object DiffCallback : DiffUtil.ItemCallback<MiCita>() {
        override fun areItemsTheSame(oldItem: MiCita, newItem: MiCita): Boolean {
            return oldItem.citaId == newItem.citaId
        }
        override fun areContentsTheSame(oldItem: MiCita, newItem: MiCita): Boolean {
            return oldItem == newItem
        }
    }
}


