package com.azalia.submission1_storyapp.ui.register

import android.animation.ObjectAnimator
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import com.azalia.submission1_storyapp.R
import com.azalia.submission1_storyapp.data.Resource
import com.azalia.submission1_storyapp.databinding.ActivityRegisterBinding
import com.azalia.submission1_storyapp.response.RegisterResponse
import com.azalia.submission1_storyapp.ui.ViewModelFactory
import com.azalia.submission1_storyapp.ui.login.LoginActivity
import com.azalia.submission1_storyapp.ui.main.MainActivity
import com.azalia.submission1_storyapp.util.Constanta.EXTRA_TOKEN
import com.azalia.submission1_storyapp.util.ViewStateCallback

class RegisterActivity : AppCompatActivity(), ViewStateCallback<RegisterResponse?> {

    private lateinit var registerBinding: ActivityRegisterBinding
    private var name: Editable? = null
    private  var email: Editable? = null
    private  var pass: Editable? = null

    private val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
    private val registerViewModel: RegisterViewModel by viewModels {
        factory
    }
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(registerBinding.root)

        setupView()
        setMyButtonEnable()

        ObjectAnimator.ofFloat(registerBinding.imgRegister, View.TRANSLATION_X, 0f, 50f).apply {
            duration = 1500
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        registerBinding.btnLogin.setOnClickListener {
            val optionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this,
                    Pair(registerBinding.btnRegist, "submit"),
                    Pair(registerBinding.btnLogin, "login"),
                    Pair(registerBinding.tfEmailRegist, "email"),
                    Pair(registerBinding.tfPassRegist, "password"),
                    Pair(registerBinding.tvRegister, "register"),
                )

            val view = registerBinding.root
            val intent = Intent(this, LoginActivity::class.java)
            view.animate()
                .alpha(0f)
                .setDuration(5000)
                .withEndAction { supportFinishAfterTransition() }
                .start()

            startActivity(intent, optionsCompat.toBundle())
        }

        registerBinding.etNameRegist.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().isNotEmpty()) setMyButtonEnable() else registerBinding.btnRegist.isEnabled = false
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().isNotEmpty()) setMyButtonEnable() else registerBinding.btnRegist.isEnabled = false
            }

            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString().isNotEmpty()) setMyButtonEnable() else registerBinding.btnRegist.isEnabled = false
            }

        })

        registerBinding.etPassRegist.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val s = p0.toString()

                if (s.length >= 8) setMyButtonEnable() else registerBinding.btnRegist.isEnabled = false
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val s = p0.toString()

                if (s.length >= 8) setMyButtonEnable() else registerBinding.btnRegist.isEnabled = false
            }

            override fun afterTextChanged(p0: Editable?) {
                val s = p0.toString()

                if (s.length >= 8) setMyButtonEnable() else registerBinding.btnRegist.isEnabled = false

            }

        })

        registerBinding.etEmailRegist.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val s = p0.toString()

                if (Patterns.EMAIL_ADDRESS.matcher(s).matches()) setMyButtonEnable() else registerBinding.btnRegist.isEnabled = false
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val s = p0.toString()

                if (Patterns.EMAIL_ADDRESS.matcher(s).matches()) setMyButtonEnable() else registerBinding.btnRegist.isEnabled = false
            }

            override fun afterTextChanged(p0: Editable?) {
                val s = p0.toString()

                if (Patterns.EMAIL_ADDRESS.matcher(s).matches()) setMyButtonEnable() else registerBinding.btnRegist.isEnabled = false
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
        name = registerBinding.etNameRegist.text
        pass = registerBinding.etPassRegist.text
        email = registerBinding.etEmailRegist.text
        registerBinding.btnRegist.isEnabled = name != null && name.toString().isNotEmpty() && pass != null && pass.toString().isNotEmpty() && email != null && email.toString().isNotEmpty()
        registerBinding.btnRegist.setOnClickListener {
            registerViewModel.userRegister(name = name.toString(), email = email.toString(), password = pass.toString()).observe(this@RegisterActivity) {
                if (it != null) {
                    when(it) {
                        is Resource.Success -> onSuccess(it.data)
                        is Resource.Loading -> onLoading()
                        is Resource.Failure -> onFailed(it.message)
                    }
                }
            }
        }
    }

    override fun onSuccess(data: RegisterResponse?) {
        Log.e(TAG, "onSuccess register: $data")
        registerViewModel.userLogin(email.toString(), pass.toString()).observe(this@RegisterActivity) {
            Log.e(TAG, "regist login: $email, $pass")
            when (it) {
                is Resource.Success -> {
                    Log.e(TAG, "onSuccess: ${it.data?.loginResult}")
                    Toast.makeText(this, "${getString(R.string.welcome)} $name!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                    intent.putExtra(EXTRA_TOKEN, it.data?.loginResult?.token)
                    startActivity(intent)
                    finish()
                }
                is Resource.Failure -> {
                    Log.e(TAG, "onFailur login regist: ${it.message}")
                    Toast.makeText(this, getString(R.string.manually_login), Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                    finish()
                }
                is Resource.Loading -> {}
            }
        }
    }

    override fun onLoading() {
        Log.e(TAG, "onLoading register")
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setMessage("Loading ...")
        progressDialog.setCancelable(false) // blocks UI interaction
        progressDialog.show()

    }

    override fun onFailed(message: String?) {
        progressDialog.hide()
        Log.e(TAG, "onFailure register: $message")
        Toast.makeText(this, getString(R.string.try_again), Toast.LENGTH_SHORT).show()
    }
}