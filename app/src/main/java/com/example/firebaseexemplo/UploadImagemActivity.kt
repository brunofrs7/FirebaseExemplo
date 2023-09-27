package com.example.firebaseexemplo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.firebaseexemplo.databinding.ActivityUploadImagemBinding
import com.google.firebase.storage.FirebaseStorage

class UploadImagemActivity : AppCompatActivity() {

    private val binding by lazy { ActivityUploadImagemBinding.inflate(layoutInflater) }
    private val storage by lazy { FirebaseStorage.getInstance() }
    /*private val abrirGaleria = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) {
        if (it != null) {
            Toast.makeText(this, "Imagem selecionada", Toast.LENGTH_SHORT).show()
            binding.imageSelecionada.setImageURI(it)
        } else {
            Toast.makeText(this, "Nenhuma imagem selecionada", Toast.LENGTH_SHORT).show()
        }
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.buttonUpload.setOnClickListener {

        }
        binding.buttonCamera.setOnClickListener {

        }
        binding.buttonGaleria.setOnClickListener {
            registerForActivityResult(
                ActivityResultContracts.GetContent()
            ) {
                if (it != null) {
                    Toast.makeText(this, "Imagem selecionada", Toast.LENGTH_SHORT).show()
                    binding.imageSelecionada.setImageURI(it)

                } else {
                    Toast.makeText(this, "Nenhuma imagem selecionada", Toast.LENGTH_SHORT).show()
                }
            }.launch("image/*") //Mime Type - https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types
        }
        binding.buttonRecuperar.setOnClickListener {

        }
    }
}