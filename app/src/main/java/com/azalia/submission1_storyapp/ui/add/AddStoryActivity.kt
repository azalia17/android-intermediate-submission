package com.azalia.submission1_storyapp.ui.add

import android.Manifest
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.azalia.submission1_storyapp.R
import com.azalia.submission1_storyapp.data.Resource
import com.azalia.submission1_storyapp.databinding.ActivityAddStoryBinding
import com.azalia.submission1_storyapp.getAddressName
import com.azalia.submission1_storyapp.reduceFileSize
import com.azalia.submission1_storyapp.response.UploadResponse
import com.azalia.submission1_storyapp.rotateFile
import com.azalia.submission1_storyapp.ui.ViewModelFactory
import com.azalia.submission1_storyapp.ui.camera.CameraActivity
import com.azalia.submission1_storyapp.ui.main.MainActivity
import com.azalia.submission1_storyapp.uriToFile
import com.azalia.submission1_storyapp.util.Constanta.EXTRA_TOKEN
import com.azalia.submission1_storyapp.util.ViewStateCallback
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddStoryActivity : AppCompatActivity(), ViewStateCallback<UploadResponse> {

    private lateinit var addStoryBinding: ActivityAddStoryBinding
    private val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
    private val addStoryViewModel: AddStoryViewModel by viewModels {
        factory
    }

    private var myFile: File? = null
    private lateinit var token: String

    private lateinit var progressDialog: ProgressDialog

    private var location: Location? = null
    private var locLatitude: RequestBody? = null
    private var locLongitude: RequestBody? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addStoryBinding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(addStoryBinding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getMyLocation()

        if (!allPermissionGranted()) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            elevation = 0f
        }

        addStoryViewModel.getUser().observe(this) {
            if (it != null)
                token = it
        }

        addStoryBinding.btnCamera.setOnClickListener { startCameraX() }
        addStoryBinding.btnGallery.setOnClickListener { startGallery() }

        addStoryBinding.scLocation.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            if (isChecked) {
                addStoryBinding.scLocation.text = location?.let {
                    getAddressName(this@AddStoryActivity, it.latitude, location!!.longitude)
                }
                locLatitude = location?.latitude?.toString()?.toRequestBody("text/plain".toMediaType())!!
                locLongitude = location?.longitude?.toString()?.toRequestBody("text/plain".toMediaType())!!
            } else {
                addStoryBinding.scLocation.text = getString(R.string.save_my_current_location)
            }
        }

        addStoryBinding.btnDetailSubmit.setOnClickListener {
            uploadStory()
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener {
                if (it != null) {
                    this.location = it
                } else {
                    Toast.makeText(this, getString(R.string.try_again), Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun uploadStory() {
        val desc = addStoryBinding.etDesc.text.toString()
        val description = desc.toRequestBody("text/plain".toMediaType())

        if (desc.isEmpty()) {
            Toast.makeText(this, getString(R.string.required_desc), Toast.LENGTH_SHORT).show()
        } else {
            if (myFile == null) {
                Toast.makeText(this, getString(R.string.required_pic), Toast.LENGTH_SHORT).show()
            } else {
                val uploadImage = reduceFileSize(myFile as File)
                val requestImage = uploadImage.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val reqImageFile = MultipartBody.Part.createFormData("photo", uploadImage.name, requestImage)

                addStoryViewModel.addStory(token, reqImageFile, description, locLatitude, locLongitude).observe(this) {
                    if (it != null) {
                        when (it) {
                            is Resource.Success -> it.data?.let { it1 -> onSuccess(it1) }
                            is Resource.Failure -> onFailed(it.message)
                            is Resource.Loading -> onLoading()
                        }
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionGranted()) {
                Toast.makeText(this, getString(R.string.no_permission), Toast.LENGTH_SHORT).show()
            }
            finish()
        }
    }

    private fun allPermissionGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri
            selectedImg.let { uri ->
                val getFile = uriToFile(uri, this@AddStoryActivity)
                myFile = getFile
                addStoryBinding.ivImgDesc.setImageURI(uri)
            }
        }
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            myFile = (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.data?.getSerializableExtra("picture", File::class.java)
            } else {
                it.data?.getSerializableExtra("picture")
            } as? File)!!

            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            myFile.let { file ->
                if (file != null) {
                    rotateFile(file, isBackCamera)
                }
                intent.putExtra(EXTRA_RESULT, myFile)
                if (file != null) {
                    addStoryBinding.ivImgDesc.setImageBitmap(BitmapFactory.decodeFile(file.path))
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onSuccess(data: UploadResponse) {
        Log.e("ADD STORY Success", "onSuccess: Add Story $data")
        Toast.makeText(this, getString(R.string.story_uploaded), Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(EXTRA_TOKEN, token)
        startActivity(intent)
        finish()
    }

    override fun onLoading() {
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setMessage("Loading ...")
        progressDialog.setCancelable(false) // blocks UI interaction
        progressDialog.show()
    }

    override fun onFailed(message: String?) {
        Log.e(TAG, "onFailed Add: $message")
        Toast.makeText(this, getString(R.string.try_again), Toast.LENGTH_SHORT)
            .show()
    }

    companion object {
        const val CAMERA_X_RESULT = 200

        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10

        const val EXTRA_RESULT = "EXTRA_RESULT"
    }
}