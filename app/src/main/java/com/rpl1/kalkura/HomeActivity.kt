package com.rpl1.kalkura

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class HomeActivity : AppCompatActivity() {

    private lateinit var tvWelcomeName: TextView
    private lateinit var tvSchoolInfo: TextView
    private lateinit var tvBobotTugas: TextView
    private lateinit var tvBobotUTS: TextView
    private lateinit var tvBobotUAS: TextView
    private lateinit var tvBadgeKKM: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        tvWelcomeName = findViewById(R.id.tvWelcomeName)
        tvSchoolInfo = findViewById(R.id.tvSchoolInfo)
        tvBobotTugas = findViewById(R.id.tvBobotTugas)
        tvBobotUTS = findViewById(R.id.tvBobotUTS)
        tvBobotUAS = findViewById(R.id.tvBobotUAS)
        tvBadgeKKM = findViewById(R.id.tvBadgeKKM)

        // Menerima data nama, sapaan & sekolah dari LoginActivity
        val nama = intent.getStringExtra("EXTRA_NAME") ?: "Arne"
        val sapaan = intent.getStringExtra("EXTRA_SAPAAN") ?: "Bu"
        val sekolah = intent.getStringExtra("EXTRA_SEKOLAH") ?: "SMKN 4 BANDUNG"

        // Mengatur teks dinamis
        tvWelcomeName.text = "Selamat Datang, $sapaan $nama!"
        tvSchoolInfo.text = "💼 WALI KELAS RPL XI - $sekolah"

        // Tombol interaktif menu aktivitas
        findViewById<CardView>(R.id.cardMulaiHitung).setOnClickListener {
            startActivity(Intent(this, HitungActivity::class.java))
        }

        findViewById<CardView>(R.id.cardPengaturan).setOnClickListener {
            startActivity(Intent(this, PengaturanActivity::class.java))
        }

        findViewById<CardView>(R.id.cardRiwayat).setOnClickListener {
            startActivity(Intent(this, RiwayatActivity::class.java))
        }

        findViewById<CardView>(R.id.cardSimulasi).setOnClickListener {
            startActivity(Intent(this, SimulasiActivity::class.java))
        }

        loadBobotDanKKM()
    }

    override fun onResume() {
        super.onResume()
        loadBobotDanKKM()
    }

    private fun loadBobotDanKKM() {
        val sharedPref = getSharedPreferences("KalkuraPref", Context.MODE_PRIVATE)
        val bTugas = sharedPref.getInt("bobot_tugas", 40)
        val bUTS = sharedPref.getInt("bobot_uts", 30)
        val bUAS = sharedPref.getInt("bobot_uas", 30)
        val kkm = sharedPref.getInt("kkm_minimum", 75)

        if (::tvBobotTugas.isInitialized) tvBobotTugas.text = "$bTugas%"
        if (::tvBobotUTS.isInitialized) tvBobotUTS.text = "$bUTS%"
        if (::tvBobotUAS.isInitialized) tvBobotUAS.text = "$bUAS%"
        if (::tvBadgeKKM.isInitialized) tvBadgeKKM.text = "KKM: $kkm"
    }
}