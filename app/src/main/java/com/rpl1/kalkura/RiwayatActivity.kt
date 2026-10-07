package com.rpl1.kalkura

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rpl1.kalkura.databinding.ActivityRiwayatBinding
import com.rpl1.kalkura.databinding.ItemRiwayatBinding
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale

class RiwayatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRiwayatBinding
    private val studentList = mutableListOf<JSONObject>()
    private lateinit var adapter: RiwayatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRiwayatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = RiwayatAdapter(studentList)
        binding.rvRiwayat.layoutManager = LinearLayoutManager(this)
        binding.rvRiwayat.adapter = adapter

        muatRiwayat()

        // Tombol Bagikan Teks Rekap (Implicit Intent)
        binding.btnBagikan.setOnClickListener {
            if (studentList.isEmpty()) {
                Toast.makeText(this, "Tidak ada data riwayat untuk dibagikan!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val textBuilder = StringBuilder()
            textBuilder.append("REKAPITULASI NILAI SISWA (KALKURA)\n")
            textBuilder.append("==================================\n\n")

            for (i in studentList.indices) {
                val obj = studentList[i]
                val nama = obj.optString("nama", "Siswa")
                val nilaiAkhir = obj.optDouble("nilai_akhir", 0.0)
                val predikat = obj.optString("predikat", "-")
                val statusKKM = obj.optString("status_kkm", "-")

                textBuilder.append(String.format(Locale.getDefault(), "%d. %s - %.2f (%s) [%s]\n", i + 1, nama, nilaiAkhir, predikat, statusKKM))
            }

            textBuilder.append("\n==================================")
            textBuilder.append("\nTotal Siswa: ${studentList.size}")

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Rekapitulasi Nilai Siswa - Kalkura")
                putExtra(Intent.EXTRA_TEXT, textBuilder.toString())
            }
            startActivity(Intent.createChooser(shareIntent, "Bagikan Teks Rekap via"))
        }

        // Tombol Hapus Semua Riwayat
        binding.btnHapusRiwayat.setOnClickListener {
            if (studentList.isEmpty()) {
                Toast.makeText(this, "Riwayat sudah kosong!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val sharedPref = getSharedPreferences("KalkuraPref", Context.MODE_PRIVATE)
            sharedPref.edit().remove("riwayat_json").apply()

            studentList.clear()
            adapter.notifyDataSetChanged()
            updateTampilanKosong()

            Toast.makeText(this, "Seluruh riwayat berhasil dihapus!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun muatRiwayat() {
        studentList.clear()
        val sharedPref = getSharedPreferences("KalkuraPref", Context.MODE_PRIVATE)
        val jsonString = sharedPref.getString("riwayat_json", "[]") ?: "[]"

        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                studentList.add(jsonArray.getJSONObject(i))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        adapter.notifyDataSetChanged()
        updateTampilanKosong()
    }

    private fun updateTampilanKosong() {
        binding.tvTotalSiswa.text = "Total Data: ${studentList.size} Siswa"
        if (studentList.isEmpty()) {
            binding.tvKosong.visibility = View.VISIBLE
            binding.rvRiwayat.visibility = View.GONE
        } else {
            binding.tvKosong.visibility = View.GONE
            binding.rvRiwayat.visibility = View.VISIBLE
        }
    }

    class RiwayatAdapter(private val list: List<JSONObject>) :
        RecyclerView.Adapter<RiwayatAdapter.ViewHolder>() {

        class ViewHolder(val binding: ItemRiwayatBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemRiwayatBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val obj = list[position]
            val nama = obj.optString("nama", "Siswa")
            val tugas = obj.optDouble("tugas", 0.0)
            val uts = obj.optDouble("uts", 0.0)
            val uas = obj.optDouble("uas", 0.0)
            val nilaiAkhir = obj.optDouble("nilai_akhir", 0.0)
            val predikat = obj.optString("predikat", "-")
            val statusKKM = obj.optString("status_kkm", "-")

            val context = holder.itemView.context

            holder.binding.tvNomor.text = "${position + 1}."
            holder.binding.tvNamaSiswa.text = nama
            holder.binding.tvNilaiDanPredikat.text = String.format(
                Locale.getDefault(),
                "Nilai: %.2f (%s) | T:%.0f U:%.0f A:%.0f",
                nilaiAkhir, predikat, tugas, uts, uas
            )
            holder.binding.tvStatusKKM.text = statusKKM

            if (statusKKM.equals("TUNTAS", ignoreCase = true)) {
                holder.binding.tvStatusKKM.setBackgroundColor(ContextCompat.getColor(context, R.color.status_tuntas_bg))
                holder.binding.tvStatusKKM.setTextColor(ContextCompat.getColor(context, R.color.status_tuntas_text))
            } else {
                holder.binding.tvStatusKKM.setBackgroundColor(ContextCompat.getColor(context, R.color.status_remedial_bg))
                holder.binding.tvStatusKKM.setTextColor(ContextCompat.getColor(context, R.color.status_remedial_text))
            }
        }

        override fun getItemCount(): Int = list.size
    }
}