package com.example.talk_in

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class SignUp : AppCompatActivity() {

  private lateinit var edtName: com.google.android.material.textfield.TextInputEditText
  private lateinit var edtEmail: com.google.android.material.textfield.TextInputEditText
  private lateinit var edtPassword: com.google.android.material.textfield.TextInputEditText
  private lateinit var btnSignUp: Button
  private lateinit var mAuth: FirebaseAuth
  private lateinit var mDbref: DatabaseReference

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_sign_up)
    supportActionBar?.hide()

    mAuth = FirebaseAuth.getInstance()

    edtName = findViewById(R.id.edt_name)
    edtEmail = findViewById(R.id.edt_email)
    edtPassword = findViewById(R.id.edt_password)
    btnSignUp = findViewById(R.id.btnSignup)
    val backbtn = findViewById<ImageView>(R.id.btnBack)

    backbtn.setOnClickListener {
      startActivity(Intent(this@SignUp, EntryActivity::class.java))
      finish()
    }

    btnSignUp.setOnClickListener {
      val email = edtEmail.text.toString().trim()
      val password = edtPassword.text.toString().trim()
      val username = edtName.text.toString().trim()

      if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
        Toast.makeText(this@SignUp, "Enter Details", Toast.LENGTH_SHORT).show()
      } else {
        signUp(username, email, password)
      }
    }
  }

  private fun signUp(name:String, email: String, password: String){
    // Creating user
    mAuth.createUserWithEmailAndPassword(email, password)
      .addOnCompleteListener(this) { task ->
        if (task.isSuccessful) {
          //code for jumping home activity
          addUserToDatabase(name,email,mAuth.currentUser?.uid!!)
          sendVerificationEmail()
          val intent= Intent(this@SignUp,LogIn::class.java)
          finish()
          startActivity(intent)

        } else {
          Toast.makeText(this@SignUp,"Please Try Again,Some Error Occurred",Toast.LENGTH_SHORT).show()
        }
      }
  }

  private fun addUserToDatabase(name: String, email: String, uid: String){
    mDbref = FirebaseDatabase.getInstance().getReference()

    mDbref.child("user").child(uid).setValue(User(name,email,uid))
  }

  fun sendVerificationEmail() {
    mAuth.currentUser?.sendEmailVerification()?.addOnCompleteListener { task ->
      if (task.isSuccessful) {
        Toast.makeText(this, "Verification email sent to your email id.", Toast.LENGTH_SHORT).show()
      } else {
        Toast.makeText(this, "Failed to send verification email.", Toast.LENGTH_SHORT).show()
      }
    }
  }
}
