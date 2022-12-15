package com.app.smartaccounting

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Base64.DEFAULT
import android.util.Base64.encodeToString
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.app.smartaccounting.databinding.UpdateProfileBinding
import com.app.smartaccounting.network.RetrofitBuilder
import com.app.smartaccounting.util.AppPreferences
import com.app.smartaccounting.util.Status
import com.app.smartaccounting.util.URIPathHelper
import com.app.smartaccounting.util.Util
import com.app.smartaccounting.viewmodels.ViewModelFactory
import com.app.smartaccounting.viewmodels.userViewModel
import com.bumptech.glide.Glide
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.*
import java.util.*
import kotlin.coroutines.CoroutineContext

class UpdateProfileActivity : BaseActivity() , CoroutineScope{
    private lateinit var binding: UpdateProfileBinding
    private lateinit var job: Job
    lateinit var viewModel: userViewModel
    private var filePath:String = ""

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()
        viewModel = ViewModelProvider(this, ViewModelFactory(RetrofitBuilder.apiService)).get(
            userViewModel::class.java
        )
        appPreferences= AppPreferences(this)
        binding = DataBindingUtil.setContentView(this,R.layout.update_profile)

        binding.edtEmail.setText(appPreferences.user.email.toString())
        binding.edtPhone.setText (appPreferences.user.phone.toString())
        binding.edtUsername.setText(appPreferences.user.userName)

        binding.btnEditProfile.setOnClickListener {
            if (askForPermissions()) openPickerDialog()
        }
        try {
            if(!appPreferences.user.profile_image.isEmpty()) Glide.with(this).load(RetrofitBuilder.BASE_URL_IMAGE+ appPreferences.user.profile_image).into(binding.imgUser)
        }catch (e:Exception){
        }
        binding.btnLogin.setOnClickListener {

            if(TextUtils.isEmpty(binding.edtUsername.text.toString().trim())){
                Util.showDialog(this, "Please enter username")
            }else if(TextUtils.isEmpty(binding.edtEmail.text.toString().trim())){
                Util.showDialog(this, "Please enter a email address")
            } else if(!Util.isValidEmail(binding.edtEmail.text.toString().trim())){
                Util.showDialog(this, " Please enter valid email address")
            }else if(TextUtils.isEmpty(binding.edtPhone.text.toString().trim())){
                Util.showDialog(this, "Please enter a mobile number")
            } else if(binding.edtPhone.text.toString().length<10){
                Util.showDialog(this, "Please enter valid mobile number")
            } else{
                var userID = appPreferences.user.userID
                var email = binding.edtEmail.text.toString()
                var username = binding.edtUsername.text.toString()
                var phone = binding.edtPhone.text.toString()


                val id: String = userID
                val pEmail: String = email
                val pUsername: String = username
                val pPhone: String = phone

           // val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                //signature =
                    //MultipartBody.Part.createFormData("profile_image", file.name, requestFile)
                var encodedString: String = ""
                if(filePath.isNotEmpty()) {
                val file: File = File(filePath.toString())
                val inputStream: InputStream =
                    FileInputStream(filePath) // You can get an inputStream using any I/O API

                val bytes: ByteArray
                val buffer = ByteArray(8192)
                var bytesRead: Int
                val output = ByteArrayOutputStream()

                try {
                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                bytes = output.toByteArray()

                encodedString = android.util.Base64.encodeToString(bytes,android.util.Base64.DEFAULT)
                }

            //    CoroutineScope(Dispatchers.Main).launch {


                        viewModel.updateProfile(
                            session = "Bearer " + appPreferences.user.auth_key,
                            id = id.toString(),
                            username = pUsername.toString(),
                            phone = pPhone.toString(),
                            device_type = "android",
                            device_model = "android",
                            os_version = "12",
                            app_version = "1.0",
                            device_token = "test",
                            encodedString
                        ).observe(/* owner = */ this@UpdateProfileActivity) /* observer = */ {
                            when (it.status) {
                                Status.LOADING -> {
                                    showLoading(this@UpdateProfileActivity)
                                }
                                Status.SUCCESS -> {
                                    hideLoading()
                                 //   showToast(it.message)
                                    appPreferences.set(
                                        "TOKEN",
                                        it.data?.userData?.auth_key.toString()
                                    )
                                    val gson = Gson()
                                    val json = gson.toJson(it.data?.userData)
                                    appPreferences.set("userObj", json)


                                    val builder1 = AlertDialog.Builder(this)
                                    builder1.setMessage(it.data?.message ?: "")
                                    builder1.setTitle("Alert")
                                    builder1.setCancelable(true)

                                    builder1.setPositiveButton(
                                        "Ok"
                                    ) { dialog, id ->
                                        dialog.cancel()
                                        dialog.dismiss()
                                        finish()
                                    }
                                    builder1.setNegativeButton(
                                        "Cancel"
                                    ) { dialog, id ->
                                        dialog.cancel()
                                        dialog.dismiss()
                                    }
                                    val alert11 = builder1.create()
                                    alert11.show()

                                   // Util.showDialog(this, )

                                }
                                Status.ERROR -> {
                                    hideLoading()
                                    Util.showDialog(this,it.message)
                                  //  showToast(it.message)
                                }
                                else -> {
                                    hideLoading()
                                    Util.showDialog(this,it.message)
                           //         showToast(it.message)
                                }
                            }

                        }
                }

            }
    }


    //Image Related Stuff
    private val openGalleryForImageRequest =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                it.data?.data?.let { path ->
                    val uriPath = URIPathHelper()
                    Log.d("CHECK FILE -->", uriPath.getPath(this, path).toString())
                    // filePath = uriPath.getPath(requireActivity(), path).toString()
                    var bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), path);
                    filePath = saveImage(bitmap).toString()
                    binding.imgUser.setImageBitmap(bitmap)

                }

            }
        }

    //Camera Picker
    private val openCameraImageRequest =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                it.data?.extras?.let { path ->
                    val uriPath = URIPathHelper()
                    path.get("data")
                    filePath = saveImage(path.get("data") as Bitmap).toString()
                    binding.imgUser.setImageBitmap(path.get("data") as Bitmap)

                }
            }
        }


    fun saveImage(myBitmap: Bitmap): String? {
        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 70, bytes)

        val obj: File
        when {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.Q -> {
                obj = File(
                    Environment.getExternalStorageDirectory().absolutePath + "/saccounting/"
                )
            }
            else -> {
                obj = File(this.getExternalFilesDir("saccounting").toString())
            }
        }
        if (!obj.exists()) {
            obj.mkdirs()
        }
        try {
            val f = File(
                obj, Calendar.getInstance()
                    .timeInMillis.toString() + ".jpg"
            )
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(
                this,
                arrayOf(f.path),
                arrayOf("image/jpeg"),
                null
            )
            fo.close()
            Log.d("TAG", "File Saved::--->" + f.absolutePath)
            return f.absolutePath
        } catch (e1: IOException) {
            e1.printStackTrace()
        }
        return ""
    }


    private fun openPickerDialog() {
        val builder = AlertDialog.Builder(this);
        builder.setTitle(this.getString(R.string.app_name));
        //builder.setCancelable(false);
        // add a list
        var animals = arrayOf("CAMERA", "GALLERY")
        builder.setItems(animals, object : DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p1: Int) {
                when (p1) {
                    0 -> {
                        capturePhoto()
                    }
                    1 -> {
                        openGalleryForImage()
                    }
//                    2 -> {
//                        openDocument()
//                    }
                }
            }

        })

        // create and show the alert dialog
        val dialog = builder.create()
        dialog.show();
    }

    //Pick Image
    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        openGalleryForImageRequest.launch(intent)
    }


    //Camera
    fun capturePhoto() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        openCameraImageRequest.launch(cameraIntent)
    }
}