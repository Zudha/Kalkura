package com.rpl1.kalkura

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.rpl1.kalkura.databinding.ActivityPengaturanBinding

class PengaturanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPengaturanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPengaturanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref = getSharedPreferences("KalkuraPref", Context.MODE_PRIVATE)

        // Tampilkan bobot tersimpan (Default: 40, 25, 35, KKM 75)
        binding.etBobotTugas.setText(sharedPref.getInt("bobot_tugas", 40).toString())
        binding.etBobotUTS.setText(sharedPref.getInt("bobot_uts", 25).toString())
        binding.etBobotUAS.setText(sharedPref.getInt("bobot_uas", 35).toString())
        binding.etKKM.setText(sharedPref.getInt("kkm_minimum", 75).toString())

        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateTotalCounter()
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        binding.etBobotTugas.addTextChangedListener(watcher)
        binding.etBobotUTS.addTextChangedListener(watcher)
        binding.etBobotUAS.addTextChangedListener(watcher)

        updateTotalCounter()

        binding.btnSimpanPengaturan.setOnClickListener {
            val strTugas = binding.etBobotTugas.text.toString().trim()
            val strUTS = binding.etBobotUTS.text.toString().trim()
            val strUAS = binding.etBobotUAS.text.toString().trim()
            val strKKM = binding.etKKM.text.toString().trim()

            if (strTugas.isEmpty() || strUTS.isEmpty() || strUAS.isEmpty() || strKKM.isEmpty()) {
                Toast.makeText(this, "Semua inputan wajib diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val bTugas = strTugas.toIntOrNull() ?: 0
            val bUTS = strUTS.toIntOrNull() ?: 0
            val bUAS = strUAS.toIntOrNull() ?: 0
            val kkm = strKKM.toIntOrNull() ?: 75

            val total = bTugas + bUTS + bUAS

            if (total != 100) {
                Toast.makeText(this, "Total persentase bobot harus TEPAT 100%! Saat ini: $total%", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // Simpan ke SharedPreferences
            sharedPref.edit().apply {
                putInt("bobot_tugas", bTugas)
                putInt("bobot_uts", bUTS)
                putInt("bobot_uas", bUAS)
                putInt("kkm_minimum", kkm)
                apply()
            }

            Toast.makeText(this, "Pengaturan berhasil disimpan!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun updateTotalCounter() {
        val bTugas = binding.etBobotTugas.text.toString().trim().toIntOrNull() ?: 0
        val bUTS = binding.etBobotUTS.text.toString().trim().toIntOrNull() ?: 0
        val bUAS = binding.etBobotUAS.text.toString().trim().toIntOrNull() ?: 0
        val total = bTugas + bUTS + bUAS

        if (total == 100) {
            binding.tvTotalBobot.text = "100% (Sesuai)"
            binding.tvTotalBobot.setTextColor(Color.parseColor("#15803D")) // Dark green
        } else {
            binding.tvTotalBobot.text = "$total% (Harus 100%)"
            binding.tvTotalBobot.setTextColor(Color.parseColor("#B91C1C")) // Dark red
        }
    }
}