package com.cibertec.centro.medico.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cibertec.centro.medico.data.model.CitaDisponible
import com.cibertec.centro.medico.databinding.ItemCitaDisponibleBinding

class CitasDisponiblesAdapter(
    private val onReservarClicked: (CitaDisponible) -> Unit
) : ListAdapter<CitaDisponible, CitasDisponiblesAdapter.CitaViewHolder>(DiffCallback) {

    // El ViewHolder contiene las referencias a las vistas de cada item
    class CitaViewHolder(private val binding: ItemCitaDisponibleBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cita: CitaDisponible) {
            binding.tvEspecialidad.text = cita.especialidad
            binding.tvDoctorNombre.text = "Dr. ${cita.nombreDoctor} ${cita.apellidoDoctor}"
            // Formateamos la fecha y hora para que se vean bien
            binding.tvFecha.text = cita.fechaCita.substring(0, 10) // Ajustar si el formato es distinto
            binding.tvHora.text = cita.horaCita.substring(0, 5)
        }
    }

    // Crea un nuevo ViewHolder cuando el RecyclerView lo necesita
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitaViewHolder {
        val binding = ItemCitaDisponibleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val viewHolder = CitaViewHolder(binding)

        // Configuramos el click listener del botón aquí
        binding.btnReservar.setOnClickListener {
            val position = viewHolder.adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                onReservarClicked(getItem(position))
            }
        }
        return viewHolder
    }

    // Actualiza el contenido de un ViewHolder
    override fun onBindViewHolder(holder: CitaViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    // DiffCallback para que el adapter sepa qué items cambiaron
    companion object DiffCallback : DiffUtil.ItemCallback<CitaDisponible>() {
        override fun areItemsTheSame(oldItem: CitaDisponible, newItem: CitaDisponible): Boolean {
            return oldItem.citaId == newItem.citaId
        }
        override fun areContentsTheSame(oldItem: CitaDisponible, newItem: CitaDisponible): Boolean {
            return oldItem == newItem
        }
    }
}
