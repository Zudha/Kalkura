package com.rpl1.kalkura

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.rpl1.kalkura.databinding.ActivitySimulasiBinding
import java.util.Locale

class SimulasiActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySimulasiBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySimulasiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref = getSharedPreferences("KalkuraPref", Context.MODE_PRIVATE)
        val bTugas = sharedPref.getInt("bobot_tugas", 40)
        val bUTS = sharedPref.getInt("bobot_uts", 25)
        val bUAS = sharedPref.getInt("bobot_uas", 35)

        binding.tvInfoBobot.text = "Bobot Penilaian Aktif: Tugas $bTugas% | UTS $bUTS% | UAS $bUAS%"

        binding.btnHitungSimulasi.setOnClickListener {
            val strTugas = binding.etNilaiTugas.text.toString().trim()
            val strUTS = binding.etNilaiUTS.text.toString().trim()
            val strTarget = binding.etTargetNilai.text.toString().trim()

            if (strTugas.isEmpty() || strUTS.isEmpty() || strTarget.isEmpty()) {
                Toast.makeText(this, "Semua inputan wajib diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val tugas = strTugas.toDoubleOrNull()
            val uts = strUTS.toDoubleOrNull()
            val target = strTarget.toDoubleOrNull()

            if (tugas == null || uts == null || target == null) {
                Toast.makeText(this, "Format angka tidak valid!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (tugas !in 0.0..100.0 || uts !in 0.0..100.0 || target !in 0.0..100.0) {
                Toast.makeText(this, "Rentang nilai harus 0 - 100!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Rumus: (Target - (Tugas * BobotTugas%) - (UTS * BobotUTS%)) / BobotUAS%
            val pTugas = tugas * (bTugas / 100.0)
            val pUTS = uts * (bUTS / 100.0)
            val uasDibutuhkan = (target - pTugas - pUTS) / (bUAS / 100.0)

            binding.cardHasilSimulasi.visibility = View.VISIBLE
            binding.tvHasilUAS.text = String.format(Locale.getDefault(), "%.2f", uasDibutuhkan)

            when {
                uasDibutuhkan > 100.0 -> {
                    val maxPossible = pTugas + pUTS + bUAS
                    binding.cardHasilSimulasi.setCardBackgroundColor(ContextCompat.getColor(this, R.color.status_remedial_bg))
                    binding.tvHasilUAS.setTextColor(ContextCompat.getColor(this, R.color.status_remedial_text))
                    binding.tvKeteranganSimulasi.setTextColor(ContextCompat.getColor(this, R.color.status_remedial_text))
                    binding.tvKeteranganSimulasi.text = String.format(
                        Locale.getDefault(),
                        "Target terlalu tinggi! Bahkan jika nilai UAS dapat 100, nilai akhir maksimal hanya %.2f.",
                        maxPossible
                    )
                }
                uasDibutuhkan <= 0.0 -> {
                    binding.cardHasilSimulasi.setCardBackgroundColor(ContextCompat.getColor(this, R.color.status_tuntas_bg))
                    binding.tvHasilUAS.setTextColor(ContextCompat.getColor(this, R.color.status_tuntas_text))
                    binding.tvKeteranganSimulasi.setTextColor(ContextCompat.getColor(this, R.color.status_tuntas_text))
                    binding.tvKeteranganSimulasi.text = "Target sudah pasti tercapai! Nilai Tugas & UTS Anda sudah memenuhi target tanpa memerlukan nilai UAS tambahan."
                }
                else -> {
                    binding.cardHasilSimulasi.setCardBackgroundColor(ContextCompat.getColor(this, R.color.primary_light))
                    binding.tvHasilUAS.setTextColor(ContextCompat.getColor(this, R.color.primary_dark))
                    binding.tvKeteranganSimulasi.setTextColor(ContextCompat.getColor(this, R.color.primary_dark))
                    binding.tvKeteranganSimulasi.text = String.format(
                        Locale.getDefault(),
                        "Untuk mencapai target nilai akhir %.1f, Anda membutuhkan nilai UAS minimal %.2f.",
                        target,
                        uasDibutuhkan
                    )
                }
            }
        }
    }
}