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

class SignUp : AppCompatActivity() {
    private lateinit var  auth: FirebaseAuth
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        auth=FirebaseAuth.getInstance()
        val etEmail=findViewById<EditText>(R.id.emailS)
        val etPassword=findViewById<EditText>(R.id.passwordS)
        val btnSignUp = findViewById<Button>(R.id.SignUpBtn)
        val tvLogIn=findViewById<TextView>(R.id.LogIn)

        tvLogIn.setOnClickListener {
            val intent= Intent(this,Login::class.java)
            startActivity(intent)
        }

        btnSignUp.setOnClickListener {
            var email: String = etEmail.text.toString()
            var pass: String = etPassword.text.toString()
            register(email,pass)
        }


    }
    fun register(email:String,pwd:String){
        val email:String = email
        val password:String=pwd
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener{ task->
            if(task.isSuccessful){
                val intent = Intent(this,Login::class.java)
                Toast.makeText(this, "Sign Up Successful", Toast.LENGTH_LONG).show()
                startActivity(intent)
                finish()
            }
        }.addOnFailureListener{ exception ->
            Toast.makeText(applicationContext,exception.localizedMessage, Toast.LENGTH_LONG).show()
        }
    }
}