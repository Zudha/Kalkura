package com.rpl1.kalkura

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rpl1.kalkura.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Tombol Hitung
        binding.btnHitung.setOnClickListener {
            startActivity(Intent(this, HitungActivity::class.java))
        }

        // Tombol Simulasi Target UAS
        binding.btnSimulasi.setOnClickListener {
            startActivity(Intent(this, SimulasiActivity::class.java))
        }

        // Tombol & Card ke Pengaturan Bobot & KKM
        binding.btnPengaturan.setOnClickListener {
            startActivity(Intent(this, PengaturanActivity::class.java))
        }
        binding.cardBobot.setOnClickListener {
            startActivity(Intent(this, PengaturanActivity::class.java))
        }

        // Tombol Riwayat
        binding.btnRiwayat.setOnClickListener {
            startActivity(Intent(this, RiwayatActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        muatPengaturanAktif()
    }

    private fun muatPengaturanAktif() {
        val sharedPref = getSharedPreferences("KalkuraPref", Context.MODE_PRIVATE)

        // Default standar: Tugas 40%, UTS 25%, UAS 35%, KKM 75
        val bTugas = sharedPref.getInt("bobot_tugas", 40)
        val bUTS = sharedPref.getInt("bobot_uts", 25)
        val bUAS = sharedPref.getInt("bobot_uas", 35)
        val kkm = sharedPref.getInt("kkm_minimum", 75)

        binding.tvBobotAktif.text = "Tugas $bTugas% | UTS $bUTS% | UAS $bUAS% (KKM: $kkm)"
    }
}