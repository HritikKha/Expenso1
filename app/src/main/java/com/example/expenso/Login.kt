package com.example.expenso

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {
    private lateinit var  auth: FirebaseAuth
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth=FirebaseAuth.getInstance()
        val etEmail=findViewById<EditText>(R.id.email)
        val etPassword=findViewById<EditText>(R.id.password)
        val btnLogin=findViewById<Button>(R.id.SignInBtn)
        val tvSignUp=findViewById<TextView>(R.id.tvSign)

        tvSignUp.setOnClickListener {
            val intent= Intent(this,SignUp::class.java)
            startActivity(intent)
        }

        btnLogin.setOnClickListener {
            var email: String = etEmail.text.toString()
            var pass: String = etPassword.text.toString()
            login(email,pass)
        }


    }
    fun login(email1:String,pwd1:String){
        val email:String = email1
        val password:String=pwd1
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener{ task->
            if(task.isSuccessful){
                val intent = Intent(this,MainActivity::class.java)
                Toast.makeText(this,"Login Successful", Toast.LENGTH_LONG).show()
                startActivity(intent)
                finish()
            }
        }.addOnFailureListener{ exception ->
            Toast.makeText(applicationContext,exception.localizedMessage, Toast.LENGTH_LONG).show()
        }
    }
}