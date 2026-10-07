package com.rpl1.kalkura

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.rpl1.kalkura.databinding.ActivityDetailBinding
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref = getSharedPreferences("KalkuraPref", Context.MODE_PRIVATE)

        // Tangkap Intent Extra
        val nama = intent.getStringExtra(HitungActivity.EXTRA_NAMA) ?: "Siswa"
        val tugas = intent.getDoubleExtra(HitungActivity.EXTRA_TUGAS, 0.0)
        val uts = intent.getDoubleExtra(HitungActivity.EXTRA_UTS, 0.0)
        val uas = intent.getDoubleExtra(HitungActivity.EXTRA_UAS, 0.0)

        // Ambil Bobot dan KKM
        val bTugas = sharedPref.getInt("bobot_tugas", 40)
        val bUTS = sharedPref.getInt("bobot_uts", 25)
        val bUAS = sharedPref.getInt("bobot_uas", 35)
        val kkm = sharedPref.getInt("kkm_minimum", 75)

        // Kalkulasi Komponen
        val pTugas = tugas * (bTugas / 100.0)
        val pUTS = uts * (bUTS / 100.0)
        val pUAS = uas * (bUAS / 100.0)
        val nilaiAkhir = pTugas + pUTS + pUAS

        // Predikat Huruf
        val predikat = when {
            nilaiAkhir >= 90.0 -> "A"
            nilaiAkhir >= 80.0 -> "B"
            nilaiAkhir >= 70.0 -> "C"
            else -> "D"
        }

        // Status KKM
        val isTuntas = nilaiAkhir >= kkm
        val statusKKM = if (isTuntas) "TUNTAS" else "REMEDIAL"

        // Tampilkan ke UI
        binding.tvNamaSiswa.text = nama
        binding.tvRincianTugas.text = String.format(Locale.getDefault(), "Tugas (%d%%): %.1f × %d%% = %.2f", bTugas, tugas, bTugas, pTugas)
        binding.tvRincianUTS.text = String.format(Locale.getDefault(), "UTS (%d%%): %.1f × %d%% = %.2f", bUTS, uts, bUTS, pUTS)
        binding.tvRincianUAS.text = String.format(Locale.getDefault(), "UAS (%d%%): %.1f × %d%% = %.2f", bUAS, uas, bUAS, pUAS)

        binding.tvNilaiAkhir.text = String.format(Locale.getDefault(), "%.2f", nilaiAkhir)
        binding.tvPredikat.text = "Predikat: $predikat"
        binding.tvStatusKKM.text = "$statusKKM (KKM: $kkm)"

        if (isTuntas) {
            binding.tvStatusKKM.setBackgroundColor(ContextCompat.getColor(this, R.color.status_tuntas_bg))
            binding.tvStatusKKM.setTextColor(ContextCompat.getColor(this, R.color.status_tuntas_text))
        } else {
            binding.tvStatusKKM.setBackgroundColor(ContextCompat.getColor(this, R.color.status_remedial_bg))
            binding.tvStatusKKM.setTextColor(ContextCompat.getColor(this, R.color.status_remedial_text))
        }

        // Tombol Simpan & Hitung Siswa Selanjutnya
        binding.btnSimpanDanLanjut.setOnClickListener {
            simpanKeRiwayat(sharedPref, nama, tugas, uts, uas, nilaiAkhir, predikat, statusKKM)
            setResult(RESULT_OK)
            Toast.makeText(this, "Data $nama berhasil disimpan!", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Tombol Kembali
        binding.btnKembali.setOnClickListener {
            finish()
        }
    }

    private fun simpanKeRiwayat(
        sharedPref: android.content.SharedPreferences,
        nama: String,
        tugas: Double,
        uts: Double,
        uas: Double,
        nilaiAkhir: Double,
        predikat: String,
        statusKKM: String
    ) {
        val jsonString = sharedPref.getString("riwayat_json", "[]") ?: "[]"
        val jsonArray = try {
            JSONArray(jsonString)
        } catch (e: Exception) {
            JSONArray()
        }

        val studentObject = JSONObject().apply {
            put("nama", nama)
            put("tugas", tugas)
            put("uts", uts)
            put("uas", uas)
            put("nilai_akhir", nilaiAkhir)
            put("predikat", predikat)
            put("status_kkm", statusKKM)
        }

        jsonArray.put(studentObject)

        sharedPref.edit().putString("riwayat_json", jsonArray.toString()).apply()
    }
}