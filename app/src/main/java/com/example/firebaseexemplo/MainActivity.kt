package com.example.firebaseexemplo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.firebaseexemplo.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }

    override fun onStart() {
        super.onStart()
        //verificarAutenticado()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.buttonExecutar.setOnClickListener {
            //registoUtilizador()
            //loginUtilizador()
            //gravarDados()
            //atualizarDados()
            //removerDados()
            //gravarDadosGeraID()
            //listarDados()
            pesquisarDados()
        }

        binding.buttonIrParaUpload.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    UploadImagemActivity::class.java
                )
            )
        }
    }

    private fun pesquisarDados() {
        db.collection("utilizadores")
            //.whereEqualTo("nome","bruno")
            //.whereNotEqualTo("nome","bruno")
            //.whereIn("nome",listOf("bruno","ana"))
            //.whereNotIn("nome",listOf("bruno","ana"))
            //.whereArrayContains("skills","java")
            //.whereGreaterThan("idade",20)
            //.whereLessThan("idade",20)
            //.whereGreaterThanOrEqualTo("idade",20)
            //.whereLessThanOrEqualTo("idade",20)
            //.orderBy("idade", Query.Direction.ASCENDING)
            //.orderBy("idade", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                var listaResultado = ""
                value?.documents?.forEach {
                    val dados = it?.data
                    if (dados != null) {
                        val nome = dados["nome"]
                        val idade = dados["idade"]
                        listaResultado += "Nome: $nome | Idade: $idade\n"
                    }
                }
                binding.textResultado.text = listaResultado
            }
    }

    private fun salvarDadosUtilizador(nome: String, idade: Int) {
        val id = auth.currentUser?.uid
        if (id != null) {

            val dados = mapOf(
                "nome" to nome,
                "idade" to idade,
            )
            db.collection("utilizadores")
                .document(id)
                .set(dados)
            /*.addOnSuccessListener {
                binding.textResultado.text = "OK"
            }
            .addOnFailureListener {
                binding.textResultado.text = "Erro"
            }*/
        }
    }

    private fun listarDados() {
        //salvarDadosUtilizador("Ana", 20)

        val id = auth.currentUser?.uid
        if (id != null) {
            /*db.collection("utilizadores").document(id)
                .get()//recupera apenas uma vez e não atualiza automaticamente quando carrega
                .addOnSuccessListener {
                    val dados = it.data
                    if (dados != null) {
                        val nome = dados["nome"]
                        val idade = dados["idade"]
                        binding.textResultado.text = "Nome: $nome | Idade: $idade"
                    }
                }
                .addOnFailureListener {
                    binding.textResultado.text = "Erro"
                }*/

            //addSnapshotListener fica a escuta de alterações - update automático (1 utilizador)
            /*db.collection("utilizadores").document(id)
                .addSnapshotListener { value, error ->
                    val dados = value?.data
                    if (dados != null) {
                        val nome = dados["nome"]
                        val idade = dados["idade"]
                        binding.textResultado.text = "Nome: $nome | Idade: $idade"
                    }
                }*/

            //addSnapshotListener fica a escuta de alterações - update automático (todos utilizadores)
            db.collection("utilizadores")
                .addSnapshotListener { value, error ->
                    var listaResultado = ""
                    value?.documents?.forEach {
                        val dados = it?.data
                        if (dados != null) {
                            val nome = dados["nome"]
                            val idade = dados["idade"]
                            listaResultado += "Nome: $nome | Idade: $idade\n"
                        }
                    }
                    binding.textResultado.text = listaResultado
                }
        }
    }

    private fun removerDados() {
        val id = "1"
        db.collection("utilizadores").document(id)
            .delete()
            .addOnSuccessListener {
                binding.textResultado.text = "Utilizador removido com sucesso"
            }
            .addOnFailureListener {
                binding.textResultado.text = "Erro ao remover: ${it.message}"
            }
    }

    private fun atualizarDados() {
        val id = "1"
        db.collection("utilizadores").document(id)
            .update("nome", "Bruno Santos")
            .addOnSuccessListener {
                binding.textResultado.text = "Utilizador atualizado com sucesso"
            }
            .addOnFailureListener {
                binding.textResultado.text = "Erro ao atualizar: ${it.message}"
            }
    }

    private fun gravarDados() {
        val dados = mapOf(
            "nome" to "bruno",
            "idade" to 35,
            "cidade" to "Porto"
        )
        val id = "1"
        db.collection("utilizadores").document(id)
            .set(dados)
            .addOnSuccessListener {
                binding.textResultado.text = "Utilizador gravado com sucesso"
            }
            .addOnFailureListener {
                binding.textResultado.text = "Erro ao gravar: ${it.message}"
            }
    }

    private fun gravarDadosGeraID() {
        val dados = mapOf(
            "nome" to "bruno",
            "idade" to 35,
            "cidade" to "Porto"
        )
        db.collection("utilizadores")
            .add(dados) //add gera o ID de forma automática, não é passado o document, fica no collection
            .addOnSuccessListener {
                binding.textResultado.text = "Utilizador gravado com sucesso"
            }
            .addOnFailureListener {
                binding.textResultado.text = "Erro ao gravar: ${it.message}"
            }
    }

    private fun verificarAutenticado() {
        //Logout
        //auth.signOut()

        val utilizador = auth.currentUser
        if (utilizador != null) {
            /*val id = utilizador.uid
            binding.textResultado.text = "Bem-vindo $id"*/
            startActivity(Intent(this, PrincipalActivity::class.java))
        } else {
            binding.textResultado.text = "Sem login"
        }
    }

    private fun loginUtilizador() {
        val email = "brunofrs7@gmail.com"
        val passw = "P4ssw0rd+"

        auth.signInWithEmailAndPassword(email, passw)
            .addOnSuccessListener {
                binding.textResultado.text = "Login OK"
                startActivity(Intent(this, PrincipalActivity::class.java))
            }
            .addOnFailureListener {
                binding.textResultado.text = "Login Erro: ${it.message}"
            }
    }

    private fun registoUtilizador() {
        val email = "brunofrs7@gmail.com"
        val passw = "P4ssw0rd+"

        auth.createUserWithEmailAndPassword(email, passw)
            .addOnSuccessListener {
                val mail = it.user?.email
                val uid = it.user?.uid
                val nome = "bruno"
                val idade = 35
                //it.user?.sendEmailVerification()

                salvarDadosUtilizador(nome, idade)
                binding.textResultado.text = "OK: $uid - $mail"
            }
            .addOnFailureListener {
                val mensagem = it.message
                binding.textResultado.text = "Erro: $mensagem"
            }
    }
}