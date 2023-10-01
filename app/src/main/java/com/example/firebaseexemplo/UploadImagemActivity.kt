package com.example.firebaseexemplo

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.firebaseexemplo.databinding.ActivityUploadImagemBinding
import com.example.firebaseexemplo.helper.Permissao
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream
import java.util.UUID

class UploadImagemActivity : AppCompatActivity() {

    private val binding by lazy { ActivityUploadImagemBinding.inflate(layoutInflater) }
    private val storage by lazy { FirebaseStorage.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }
    private var uriSelecionada: Uri? = null
    private var bitmapSelecionada: Bitmap? = null

    private val abrirGaleria = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            Toast.makeText(this, "Imagem selecionada", Toast.LENGTH_SHORT).show()
            binding.imageSelecionada.setImageURI(uri)
            uriSelecionada = uri
        } else {
            Toast.makeText(this, "Nenhuma imagem selecionada", Toast.LENGTH_SHORT).show()
        }
    }

    private val abrirCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        bitmapSelecionada = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            it.data?.extras?.getParcelable("data", Bitmap::class.java)
        } else {
            it.data?.extras?.getParcelable("data")
        }
        binding.imageSelecionada.setImageBitmap(bitmapSelecionada)
    }

    private val permissoes = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        listOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_MEDIA_IMAGES,
        )
    } else {
        listOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
        )
    }

    private var temPermissaoCamera = false
    private var temPermissaoGaleria = false

    /*override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.i("permissao_app", "requestCode: $requestCode")
        permissions.forEachIndexed { indice, valor ->
            Log.i("permissao_app", "permission: $indice - $valor")
        }
        grantResults.forEachIndexed { indice, valor ->
            Log.i("permissao_app", "concedida: $indice - $valor")
        }
    }*/
    private fun solicitarPermissoes() {
        //verificar permissoes
        val permissoesNegadas = mutableListOf<String>()
        temPermissaoCamera =
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED

        temPermissaoGaleria = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }

        if (!temPermissaoCamera) {
            permissoesNegadas.add(android.Manifest.permission.CAMERA)
        }
        if (!temPermissaoGaleria) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissoesNegadas.add(android.Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                permissoesNegadas.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        //solicitar permissoes
        val gerenciadorPermissoes = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissoes: Map<String, Boolean> ->
            Log.i("novas_permissoes", "permissoes: $permissoes")
        }
        gerenciadorPermissoes.launch(permissoes.toTypedArray())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //permissões
        //Permissao.requisitarPermissoes(this, permissoes, 100)

        //permissões alternativa
        solicitarPermissoes()

        binding.buttonGaleria.setOnClickListener {
            if (temPermissaoGaleria) {
                abrirGaleria.launch("image/*") //Mime Type - https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types
            } else {
                Toast.makeText(this, "Não tem permissão", Toast.LENGTH_SHORT).show()
            }
        }
        binding.buttonCamera.setOnClickListener {
            if (temPermissaoCamera) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                abrirCamera.launch(intent)
            } else {
                Toast.makeText(this, "Não tem permissão", Toast.LENGTH_SHORT).show()
            }
        }
        binding.buttonUpload.setOnClickListener {
            //uploadGaleria()
            uploadCamera()
        }
        binding.buttonRecuperar.setOnClickListener {
            val id = auth.currentUser?.uid
            if (id != null) {
                storage.getReference("fotos")
                    .child(id)
                    .child("foto.jpg")
                    .downloadUrl
                    .addOnSuccessListener { urlFirebase ->
                        Picasso.get().load(urlFirebase).into(binding.imageRecuperada)
                    }
            }
        }

    }

    private fun uploadGaleria() {
        val id = auth.currentUser?.uid
        //val nomeImagem = UUID.randomUUID().toString()
        if (uriSelecionada != null && id != null) {
            storage.getReference("fotos")
                .child(id)
                .child("foto.jpg")
                .putFile(uriSelecionada!!)
                .addOnSuccessListener {
                    Toast.makeText(this, "Sucesso no upload", Toast.LENGTH_SHORT).show()
                    it.metadata?.reference?.downloadUrl
                        ?.addOnSuccessListener { uriFirebase ->
                            Toast.makeText(this, uriFirebase.toString(), Toast.LENGTH_SHORT)
                                .show()
                        }

                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro no upload", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun uploadCamera() {
        val id = auth.currentUser?.uid

        val outputStream = ByteArrayOutputStream()
        bitmapSelecionada?.compress(
            Bitmap.CompressFormat.JPEG,
            100,
            outputStream
        )

        if (bitmapSelecionada != null && id != null) {
            storage.getReference("fotos")
                .child(id)
                .child("foto.jpg")
                .putBytes(outputStream.toByteArray())
                .addOnSuccessListener {
                    Toast.makeText(this, "Sucesso no upload", Toast.LENGTH_SHORT).show()
                    it.metadata?.reference?.downloadUrl
                        ?.addOnSuccessListener { uriFirebase ->
                            Toast.makeText(this, uriFirebase.toString(), Toast.LENGTH_SHORT)
                                .show()
                        }

                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro no upload", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
