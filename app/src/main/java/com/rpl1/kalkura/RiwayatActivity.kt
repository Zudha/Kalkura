package com.rpl1.kalkura

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
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

        // Tombol Kembali
        binding.btnBack.setOnClickListener {
            finish()
        }

        adapter = RiwayatAdapter(studentList)
        binding.rvRiwayat.layoutManager = LinearLayoutManager(this)
        binding.rvRiwayat.adapter = adapter

        muatRiwayat()

        // Tombol Ekspor Teks (Implicit Intent - Bagikan ke WhatsApp / aplikasi lain)
        binding.btnEksporTeks.setOnClickListener {
            if (studentList.isEmpty()) {
                Toast.makeText(this, "Tidak ada data riwayat untuk diekspor!", Toast.LENGTH_SHORT).show()
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
                val tugas = obj.optDouble("tugas", 0.0)
                val uts = obj.optDouble("uts", 0.0)
                val uas = obj.optDouble("uas", 0.0)

                textBuilder.append(
                    String.format(
                        Locale.getDefault(),
                        "%d. %s - Nilai: %.2f (%s) [%s] (T:%.0f, U:%.0f, A:%.0f)\n",
                        i + 1, nama, nilaiAkhir, predikat, statusKKM, tugas, uts, uas
                    )
                )
            }

            textBuilder.append("\n==================================")
            textBuilder.append("\nTotal Tersimpan: ${studentList.size} Data")

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Rekapitulasi Nilai Siswa - Kalkura")
                putExtra(Intent.EXTRA_TEXT, textBuilder.toString())
            }
            startActivity(Intent.createChooser(shareIntent, "Ekspor Teks Rekap via"))
        }

        // Tombol Hapus Data
        binding.btnHapusData.setOnClickListener {
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
        binding.tvDataTersimpan.text = "${studentList.size} Data Tersimpan"
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
            val predikat = obj.optString("predikat", "-")

            val context = holder.itemView.context

            holder.binding.tvNomor.text = "${position + 1}."
            holder.binding.tvNamaSiswa.text = nama
            holder.binding.tvNilaiRincian.text = String.format(
                Locale.getDefault(),
                "Tugas: %.0f | UTS: %.0f | UAS: %.0f",
                tugas, uts, uas
            )

            // Format Badge Grade: "— A", "— B", "— C"
            val badgeText = if (predikat.length == 1) "— $predikat" else predikat
            holder.binding.tvBadgePredikat.text = badgeText

            val colorRes = when (predikat.uppercase(Locale.getDefault())) {
                "A" -> R.color.grade_a
                "B" -> R.color.grade_b
                "C" -> R.color.grade_c
                else -> R.color.grade_d
            }

            holder.binding.tvBadgePredikat.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(context, colorRes)
            )
        }

        override fun getItemCount(): Int = list.size
    }
}
