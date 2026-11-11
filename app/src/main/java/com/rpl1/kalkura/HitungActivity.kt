package com.rpl1.kalkura

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.rpl1.kalkura.databinding.ActivityHitungBinding

class HitungActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHitungBinding

    private val detailLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            kosongkanForm()
            Snackbar.make(binding.root, "Form dikosongkan untuk siswa berikutnya!", Snackbar.LENGTH_SHORT)
                .setAction("OKE") {}
                .show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHitungBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnHitungDetail.setOnClickListener {
            val nama = binding.etNamaSiswa.text.toString().trim()
            val strTugas = binding.etNilaiTugas.text.toString().trim()
            val strUTS = binding.etNilaiUTS.text.toString().trim()
            val strUAS = binding.etNilaiUAS.text.toString().trim()

            val gender = if (binding.rbLakiLaki.isChecked) "Laki-Laki" else "Perempuan"

            if (strTugas.isEmpty() || strUTS.isEmpty() || strUAS.isEmpty()) {
                Toast.makeText(this, "Nilai Tugas, UTS, dan UAS wajib diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val tugas = strTugas.toDoubleOrNull()
            val uts = strUTS.toDoubleOrNull()
            val uas = strUAS.toDoubleOrNull()

            if (tugas == null || uts == null || uas == null) {
                Toast.makeText(this, "Format nilai tidak valid!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (tugas !in 0.0..100.0 || uts !in 0.0..100.0 || uas !in 0.0..100.0) {
                Toast.makeText(this, "Rentang nilai harus 0 - 100!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Explicit Intent pengiriman data ke DetailActivity
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra(EXTRA_NAMA, if (nama.isEmpty()) "Siswa" else nama)
                putExtra(EXTRA_GENDER, gender)
                putExtra(EXTRA_TUGAS, tugas)
                putExtra(EXTRA_UTS, uts)
                putExtra(EXTRA_UAS, uas)
            }

            detailLauncher.launch(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        muatRingkasanBobot()
    }

    private fun muatRingkasanBobot() {
        val sharedPref = getSharedPreferences("KalkuraPref", Context.MODE_PRIVATE)
        val bTugas = sharedPref.getInt("bobot_tugas", 40)
        val bUTS = sharedPref.getInt("bobot_uts", 25)
        val bUAS = sharedPref.getInt("bobot_uas", 35)
        val kkm = sharedPref.getInt("kkm_minimum", 75)

        binding.tvSummaryTugas.text = "Tugas  $bTugas %"
        binding.tvSummaryUTS.text = "UTS  $bUTS %"
        binding.tvSummaryUAS.text = "UAS  $bUAS %"
        binding.tvSummaryKKM.text = "$kkm"
    }

    private fun kosongkanForm() {
        binding.etNamaSiswa.text?.clear()
        binding.etNilaiTugas.text?.clear()
        binding.etNilaiUTS.text?.clear()
        binding.etNilaiUAS.text?.clear()
        binding.rbLakiLaki.isChecked = true
        binding.etNamaSiswa.requestFocus()
    }

    companion object {
        const val EXTRA_NAMA = "extra_nama"
        const val EXTRA_GENDER = "extra_gender"
        const val EXTRA_TUGAS = "extra_tugas"
        const val EXTRA_UTS = "extra_uts"
        const val EXTRA_UAS = "extra_uas"
    }
}