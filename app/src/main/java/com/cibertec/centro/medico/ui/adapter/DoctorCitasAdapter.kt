package com.cibertec.centro.medico.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cibertec.centro.medico.R
import com.cibertec.centro.medico.data.model.CitaDoctor
import com.cibertec.centro.medico.databinding.ItemDoctorCitaBinding

class DoctorCitasAdapter : ListAdapter<CitaDoctor, DoctorCitasAdapter.CitaViewHolder>(DiffCallback) {

    class CitaViewHolder(private val binding: ItemDoctorCitaBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cita: CitaDoctor) {
            binding.tvPacienteNombre.text = if (cita.nombrePaciente.isNullOrEmpty()) {
                "Cita Disponible"
            } else {
                "${cita.nombrePaciente} ${cita.apellidoPaciente}"
            }
            binding.tvFecha.text = cita.fechaCita.substring(0, 10)
            binding.tvHora.text = cita.horaCita.substring(0, 5)

            binding.tvEstado.text = cita.estado.uppercase()
            val statusColor = when (cita.estado.uppercase()) {
                "DISPONIBLE" -> R.color.primary_blue
                "RESERVADA" -> android.R.color.holo_orange_dark
                "CANCELADA" -> android.R.color.holo_red_light
                "ATENDIDA" -> android.R.color.holo_green_dark
                else -> com.google.android.material.R.color.material_on_surface_disabled
            }
            binding.tvEstado.background.setTint(ContextCompat.getColor(binding.root.context, statusColor))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitaViewHolder {
        val binding = ItemDoctorCitaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CitaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CitaViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object DiffCallback : DiffUtil.ItemCallback<CitaDoctor>() {
        override fun areItemsTheSame(oldItem: CitaDoctor, newItem: CitaDoctor): Boolean {
            return oldItem.citaId == newItem.citaId
        }
        override fun areContentsTheSame(oldItem: CitaDoctor, newItem: CitaDoctor): Boolean {
            return oldItem == newItem
        }
    }
}
