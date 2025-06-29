package com.cibertec.centro.medico.ui.view

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.cibertec.centro.medico.data.model.Usuario
import com.cibertec.centro.medico.data.network.RetrofitClient
import com.cibertec.centro.medico.databinding.ActivityPacienteMainBinding
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PacienteMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPacienteMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPacienteMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        setupToolbar()

        setupViewPager()

        val prefs = getSharedPreferences("sesion", MODE_PRIVATE)
        val usuarioId = prefs.getInt("usuarioId", 0)

        obtenerUsuarioPorId(usuarioId)
        Log.d("PacienteMainActivity", "UsuarioId: $usuarioId")
    }

    private fun setupViewPager() {
        val tabLayout = binding.tabLayout
        val viewPager = binding.viewPager

        val adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 2

            override fun createFragment(position: Int): Fragment {
                Log.d("PacienteMainActivity", "Creando fragment en posición: $position")
                return when (position) {
                    0 -> {
                        Log.d("PacienteMainActivity", "Creando MisCitasFragment")
                        MisCitasFragment()
                    }
                    1 -> {
                        Log.d("PacienteMainActivity", "Creando CitasDisponiblesFragment")
                        CitasDisponiblesFragment()
                    }
                    else -> {
                        Log.e("PacienteMainActivity", "Posición inválida: $position")
                        throw IllegalArgumentException("Invalid position: $position")
                    }
                }
            }
        }

        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Mis Citas"
                1 -> "Citas Disponibles"
                else -> "Tab $position"
            }
        }.attach()
    }

    private fun obtenerUsuarioPorId(usuarioId: Int) {
        lifecycleScope.launch {
            try {
                Log.d("PacienteMainActivity", "Obteniendo usuario con ID: $usuarioId")

                val response = RetrofitClient.instance.getUsuarioById(usuarioId)

                if (response.isSuccessful) {
                    val usuario = response.body()
                    Log.d("PacienteMainActivity", "Usuario recibido: $usuario")

                    // Actualizar la UI con los datos del usuario
                    usuario?.let {
                        binding.txtNombrePaciente.text = "${it.nombre} ${it.apellido ?: ""}"
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = "Error ${response.code()}: ${response.message()}" +
                            if (errorBody != null) " - $errorBody" else ""
                    Log.e("PacienteMainActivity", "Error al obtener el usuario: $errorMessage")

                    // Opcional: mostrar mensaje de error al usuario
                    Toast.makeText(this@PacienteMainActivity,
                        "Error al cargar datos del usuario",
                        Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("PacienteMainActivity", "Error de red: ${e.message}", e)

                // Opcional: mostrar mensaje de error al usuario
                Toast.makeText(this@PacienteMainActivity,
                    "Error de conexión",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }
}