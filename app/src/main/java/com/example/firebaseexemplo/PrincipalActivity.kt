package com.example.firebaseexemplo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.firebaseexemplo.databinding.ActivityPrincipalBinding
import com.google.firebase.auth.FirebaseAuth

class PrincipalActivity : AppCompatActivity() {

    private val binding by lazy { ActivityPrincipalBinding.inflate(layoutInflater) }
    private val auth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.buttonLogout.setOnClickListener {
            auth.signOut()
            finish()
        }
    }
}