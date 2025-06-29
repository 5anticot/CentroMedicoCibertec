package com.cibertec.centro.medico.ui.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cibertec.centro.medico.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    // --- CORRECCIÓN AQUÍ ---
    private fun setupListeners() {

        binding.btnPortalDoctor.setOnClickListener {
            startActivity(Intent(this, DoctorCitasActivity::class.java))
        }
    }
}
