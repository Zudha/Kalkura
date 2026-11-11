package com.rpl1.kalkura

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class LoginActivity : AppCompatActivity() {

    private var selectedSapaan: String = "Ibu"

    private lateinit var btnBapak: MaterialButton
    private lateinit var btnIbu: MaterialButton
    private lateinit var btnSiswa: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etNama = findViewById<EditText>(R.id.etNama)
        val etSekolah = findViewById<EditText>(R.id.etSekolah)
        val etNip = findViewById<EditText>(R.id.etNip)

        btnBapak = findViewById(R.id.btnBapak)
        btnIbu = findViewById(R.id.btnIbu)
        btnSiswa = findViewById(R.id.btnSiswa)
        val btnMasuk = findViewById<Button>(R.id.btnMasuk)

        // Set 'Ibu' as initial selected sapaan
        updateSapaanButtons("Ibu")

        btnBapak.setOnClickListener { updateSapaanButtons("Bapak") }
        btnIbu.setOnClickListener { updateSapaanButtons("Ibu") }
        btnSiswa.setOnClickListener { updateSapaanButtons("Siswa/i") }

        btnMasuk.setOnClickListener {
            val namaInput = etNama.text.toString().trim()
            val sekolahInput = etSekolah.text.toString().trim()

            if (namaInput.isEmpty()) {
                etNama.error = "Nama wajib diisi!"
                return@setOnClickListener
            }

            val intent = Intent(this, HomeActivity::class.java).apply {
                putExtra("EXTRA_NAME", namaInput)
                putExtra("EXTRA_SAPAAN", selectedSapaan)
                putExtra("EXTRA_SEKOLAH", if (sekolahInput.isNotEmpty()) sekolahInput else "SMKN 4 BANDUNG")
            }
            startActivity(intent)
            finish()
        }
    }

    private fun updateSapaanButtons(sapaan: String) {
        selectedSapaan = sapaan
        val tealColor = Color.parseColor("#2B585C")

        setButtonStyle(btnBapak, sapaan == "Bapak", tealColor)
        setButtonStyle(btnIbu, sapaan == "Ibu", tealColor)
        setButtonStyle(btnSiswa, sapaan == "Siswa/i", tealColor)
    }

    private fun setButtonStyle(button: MaterialButton, isSelected: Boolean, tealColor: Int) {
        if (isSelected) {
            button.setBackgroundColor(tealColor)
            button.setTextColor(Color.WHITE)
            button.strokeWidth = 0
        } else {
            button.setBackgroundColor(Color.TRANSPARENT)
            button.setTextColor(tealColor)
            button.setStrokeColorResource(R.color.kalkura_primary)
            button.strokeWidth = 2
        }
    }
}