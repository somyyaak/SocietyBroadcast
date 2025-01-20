package com.example.societybroadcast


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager2.widget.ViewPager2
import com.example.societybroadcast.daos.UserDao
import com.example.societybroadcast.models.User

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class SignInActivity : AppCompatActivity() {
    private var loginB: Boolean = false
    private var signupB: Boolean = false
    private var google: Boolean = false
    private var  isLoading : Boolean=false;
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var viewPager2: ViewPager2
    private val RC_SIGN_IN = 1234
    private lateinit var googleSignIn: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private val handler = CoroutineExceptionHandler { _, exception ->
        Toast.makeText(
            this@SignInActivity,
            "Invalid email or password",
            Toast.LENGTH_LONG
        ).show()
    }
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        val client = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        googleSignIn = GoogleSignIn.getClient(this, client)
        val googlebutton = findViewById<FloatingActionButton>(R.id.googlebutton)
        auth = Firebase.auth
        googlebutton.setOnClickListener {
            signIn()
        }
        categoryAdapter = CategoryAdapter(this)
        viewPager2 = findViewById(R.id.viewpager)
        viewPager2.adapter = categoryAdapter
        val label = arrayOf("LOGIN", "SIGNUP")
        val tabs = findViewById<TabLayout>(R.id.tablayout)
        TabLayoutMediator(tabs, viewPager2) { tab, position ->
            tab.text = label[position]
        }.attach()


    }

    fun loginMethod(view: View) {
        Log.v("Somiya", "loginMethod")
        loginB = true
        val emaill = findViewById<EditText>(R.id.emaill)
        val passwordl = findViewById<EditText>(R.id.passwordl)
        if (emaill.text.isEmpty() && passwordl.text.isEmpty()) {
            Toast.makeText(this, "Enter email and password", Toast.LENGTH_LONG).show()
        } else if (emaill.text.isEmpty()) {
            Toast.makeText(this, "Enter email", Toast.LENGTH_LONG).show()
        } else if (passwordl.text.isEmpty()) {
            Toast.makeText(this, "Enter password", Toast.LENGTH_LONG).show()
        } else if (passwordl.text.toString().length < 6) {
            Toast.makeText(this, "Password length must be greater than 6", Toast.LENGTH_LONG).show()
        } else if (!emaill.text.toString().contains("@")) {
            Toast.makeText(this, "Invalid email", Toast.LENGTH_LONG).show()
        } else if (!emaill.text.toString().contains(".com")) {
            Toast.makeText(this, "Invalid email", Toast.LENGTH_LONG).show()
        } else {

            auth.signInWithEmailAndPassword(
                emaill.text.toString().trim(),
                passwordl.text.toString()
            ).addOnCompleteListener(this) {
                    task ->
                if(task.isSuccessful){
                    val user = auth.currentUser
                    update(user)
                }
                else{
                    Toast.makeText(
                        this@SignInActivity,
                        "Invalid email or password",
                        Toast.LENGTH_LONG
                    ).show()
                    update(null)
                }
            }

        }
    }







    private fun signIn() {
        val signInIntent = googleSignIn.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(firebaseUser: FirebaseUser?) {
        if(firebaseUser != null) {


            Log.v("Somiya","signup")
            var Uname = firebaseUser.displayName.toString()
            val name = findViewById<EditText>(R.id.name)
            if (signupB) {
                Uname = name.text.toString()
            }
            val user = User(
                firebaseUser.uid,
                Uname,
                firebaseUser.photoUrl.toString()
            )
            val usersDao = UserDao()
            usersDao.addUser(user)

            val mainActivityIntent = Intent(this, MainActivity::class.java)
            startActivity(mainActivityIntent)
            finish()

        }
        else
        {
            val googlebutton = findViewById<FloatingActionButton>(R.id.googlebutton)
            val progressBar = findViewById<ProgressBar>(R.id.progressBar)
            googlebutton.visibility = View.VISIBLE
            progressBar.visibility = View.GONE

        }

    }

    private fun update(firebaseUser: FirebaseUser?){
        if(firebaseUser != null) {
            if (loginB) {
                Log.v("Somiya", "login")
                GlobalScope.launch {
                    val userDao = UserDao()
                    val user =
                        userDao.getUserById(firebaseUser.uid).await().toObject(User::class.java)!!
                    Log.v("Somiya", "login")
                    userDao.loginUser(user)
                }
                val mainActivityIntent = Intent(this@SignInActivity, MainActivity::class.java)
                startActivity(mainActivityIntent)
                finish()
            }
        }
    }
    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(task:Task<GoogleSignInAccount>?) {
        try {
            val account =
                task?.getResult(ApiException::class.java)!!
            firebaseAuthWithGoogle(account.idToken!!)
        }
        catch (e:ApiException){

        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun firebaseAuthWithGoogle(idToken: String) {

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val googlebutton = findViewById<FloatingActionButton>(R.id.googlebutton)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val welcome = findViewById<TextView>(R.id.welcome)
        val constraintLayout = findViewById<ConstraintLayout>(R.id.constraintLayout)
        val background = findViewById<ImageView>(R.id.background)
        googlebutton.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        welcome.visibility = View.GONE
        constraintLayout.visibility = View.GONE
        background.visibility=View.GONE
        GlobalScope.launch(Dispatchers.IO) {
            val auth = auth.signInWithCredential(credential).await()
            val firebaseUser = auth.user
            withContext(Dispatchers.Main) {
                google = true
                updateUI(firebaseUser)
            }
        }

    }

    fun signMethod(view: View) {
        val emails = findViewById<EditText>(R.id.emails)
        val passwords = findViewById<EditText>(R.id.passwords)
        val passwordsc = findViewById<EditText>(R.id.passwordsc)
        val name = findViewById<EditText>(R.id.name)

        signupB=true
        if(name.text.isEmpty()){
            Toast.makeText(this,"Enter name",Toast.LENGTH_LONG).show()
        }
        else if(emails.text.isEmpty() && (passwords.text.isEmpty() || passwordsc.text.isEmpty())){
            Toast.makeText(this,"Enter email and password",Toast.LENGTH_LONG).show()
        }
        else if(emails.text.isEmpty() ){
            Toast.makeText(this,"Enter email",Toast.LENGTH_LONG).show()
        }
        else if( passwords.text.isEmpty() || passwordsc.text.isEmpty()){
            Toast.makeText(this,"Enter password",Toast.LENGTH_LONG).show()
        }
        else if(passwords.text.toString() != passwordsc.text.toString()){
            Toast.makeText(this,"Passwords dont match",Toast.LENGTH_LONG).show()
        }
        else if(passwords.text.toString().length < 6 ){
            Toast.makeText(this,"Password length must be greater than 6",Toast.LENGTH_LONG).show()
        }
        else if(!(emails.text.toString().contains("@") ||emails.text.toString().contains(".com")) ){
            Toast.makeText(this,"Invalid email",Toast.LENGTH_LONG).show()
        }
        else{
            auth.createUserWithEmailAndPassword(emails.text.toString().trim(), passwords.text.toString()).addOnCompleteListener(this) {
                    task ->
                if(task.isSuccessful){
                    val user = auth.currentUser
                    updateUI(user)
                }
                else{
                    Log.v("Somiya","${task.exception}")
                    if(task.exception.toString() == "com.google.firebase.auth.FirebaseAuthUserCollisionException: The email address is already in use by another account."){
                        Toast.makeText(
                            this@SignInActivity,
                            "The email address is already in use by another account",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else {
                        Toast.makeText(
                            this@SignInActivity,
                            "Invalid email or password",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    update(null)
                }

            }
        }

    }
}