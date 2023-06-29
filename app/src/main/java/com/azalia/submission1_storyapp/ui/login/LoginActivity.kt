package com.azalia.submission1_storyapp.ui.login

import android.animation.ObjectAnimator
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.util.Pair
import androidx.core.app.ActivityOptionsCompat
import com.azalia.submission1_storyapp.R
import com.azalia.submission1_storyapp.data.Resource
import com.azalia.submission1_storyapp.databinding.ActivityLoginBinding
import com.azalia.submission1_storyapp.response.LoginResponse
import com.azalia.submission1_storyapp.ui.ViewModelFactory
import com.azalia.submission1_storyapp.ui.main.MainActivity
import com.azalia.submission1_storyapp.ui.register.RegisterActivity
import com.azalia.submission1_storyapp.util.Constanta.EXTRA_TOKEN
import com.azalia.submission1_storyapp.util.ViewStateCallback


class LoginActivity : AppCompatActivity(), ViewStateCallback<LoginResponse> {

    private lateinit var loginBinding: ActivityLoginBinding
    private val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
    private val loginViewModel: LoginViewModel by viewModels {
        factory
    }
    private  var email: Editable? = null
    private  var pass: Editable? = null

    private lateinit var progressDialog: ProgressDialog

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)

        setupView()

        setMyButtonEnable()

        ObjectAnimator.ofFloat(loginBinding.imgLogin, View.TRANSLATION_X, 0f, 40f).apply {
            duration = 1500
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        loginBinding.btnRegister.setOnClickListener {
            val optionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this,
                    Pair(loginBinding.tvLogin, "login"),
                    Pair(loginBinding.btnLogin, "submit"),
                    Pair(loginBinding.tfEmailLogin, "email"),
                    Pair(loginBinding.tfPassLogin, "password"),
                    Pair(loginBinding.btnRegister, "register"),
                )

            val view = loginBinding.root
            val intent = Intent(this, RegisterActivity::class.java)
            view.animate()
                .alpha(0f)
                .setDuration(3000)
                .withEndAction { supportFinishAfterTransition() }
                .start()

            startActivity(intent, optionsCompat.toBundle())
        }

        loginBinding.etPassLogin.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val s = p0.toString()

                if (s.length >= 8) setMyButtonEnable() else loginBinding.btnLogin.isEnabled = false
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val s = p0.toString()

                if (s.length >= 8) setMyButtonEnable() else loginBinding.btnLogin.isEnabled = false
            }

            override fun afterTextChanged(p0: Editable?) {
                val s = p0.toString()

                if (s.length >= 8) setMyButtonEnable() else loginBinding.btnLogin.isEnabled = false
            }
        })
        loginBinding.etEmailLogin.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val s = p0.toString()
//                if (loginBinding.etEmailLogin.error.isEmpty()) setMyButtonEnable() else loginBinding.btnLogin.isEnabled = false
                if (Patterns.EMAIL_ADDRESS.matcher(s).matches()) setMyButtonEnable() else loginBinding.btnLogin.isEnabled = false

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val s = p0.toString()
//                if (loginBinding.etEmailLogin.error.isEmpty()) setMyButtonEnable() else loginBinding.btnLogin.isEnabled = false

                if (Patterns.EMAIL_ADDRESS.matcher(s).matches()) setMyButtonEnable() else loginBinding.btnLogin.isEnabled = false
            }

            override fun afterTextChanged(p0: Editable?) {
                val s = p0.toString()
//                if (loginBinding.etEmailLogin.error.isEmpty()) setMyButtonEnable() else loginBinding.btnLogin.isEnabled = false

                if (Patterns.EMAIL_ADDRESS.matcher(s).matches()) setMyButtonEnable() else loginBinding.btnLogin.isEnabled = false

            }
        })
    }

    private fun setupView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setMyButtonEnable() {
        pass = loginBinding.etPassLogin.text
        email = loginBinding.etEmailLogin.text
        loginBinding.btnLogin.isEnabled = pass != null && pass.toString().isNotEmpty() && email != null && email.toString().isNotEmpty()
        loginBinding.btnLogin.setOnClickListener {
            loginViewModel.userLogin(email = email.toString(), password = pass.toString()).observe(this) {
                Log.e(TAG, "${email.toString()}, ${pass.toString()}")
                Log.e(TAG, "${it.data}")
                if (it != null) {
                    when (it) {
                        is Resource.Success -> it.data?.let { it1 -> onSuccess(it1) }
                        is Resource.Loading -> onLoading()
                        is Resource.Failure -> onFailed(it.message)
                        else -> {}
                    }
                }
            }
            }

        }

    override fun onSuccess(data: LoginResponse) {
        Log.e(TAG, "onSuccess login: $data")
        Toast.makeText(this, "${getString(R.string.welcome)} ${data.loginResult.name}!", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(EXTRA_TOKEN, data.loginResult.token)
        startActivity(intent)
        finish()
    }

    override fun onLoading() {
        Log.e(TAG, "onLoading login")
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setMessage("Loading ...")
        progressDialog.setCancelable(false) // blocks UI interaction
        progressDialog.show()
    }

    override fun onFailed(message: String?) {
        progressDialog.hide()
        Log.e(TAG, "onFailed login: $message")
        Toast.makeText(this, getString(R.string.try_again), Toast.LENGTH_SHORT).show()
    }
}