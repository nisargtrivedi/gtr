package com.app.smartaccounting

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import arte.programar.materialfile.MaterialFilePicker
import arte.programar.materialfile.ui.FilePickerActivity
import arte.programar.materialfile.utils.FileUtils
import com.app.smartaccounting.adapter.CategoryAdapterTwo
import com.app.smartaccounting.adapter.DetailImageAdapter
import com.app.smartaccounting.databinding.SendDocumentBinding
import com.app.smartaccounting.listeners.onImageDelete
import com.app.smartaccounting.model.categoryModel
import com.app.smartaccounting.network.RetrofitBuilder
import com.app.smartaccounting.util.AppPreferences
import com.app.smartaccounting.util.Status
import com.app.smartaccounting.util.URIPathHelper
import com.app.smartaccounting.viewmodels.ViewModelFactory
import com.app.smartaccounting.viewmodels.userViewModel
import com.scanlibrary.ScanActivity
import com.scanlibrary.ScanConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import kotlin.coroutines.CoroutineContext


class SendDocumentActivity : BaseActivity() , CoroutineScope, onImageDelete {
    private lateinit var binding: SendDocumentBinding

    private lateinit var job: Job
    lateinit var viewModel: userViewModel
    private var filePath:String = ""
    var categoryList = mutableListOf<categoryModel>()
    var imageList = mutableListOf<String>()
    var encodeimageList = mutableListOf<String>()
    var catID:String="0"
    lateinit var arrayAdapter: CategoryAdapterTwo
    lateinit var detailImageAdapter: DetailImageAdapter

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()

        viewModel = ViewModelProvider(this, ViewModelFactory(RetrofitBuilder.apiService)).get(
            userViewModel::class.java
        )
        appPreferences= AppPreferences(this)
        binding = DataBindingUtil.setContentView(this,R.layout.send_document)
        arrayAdapter = CategoryAdapterTwo(
            this, categoryList
        )
        binding.spCategory.adapter = arrayAdapter

        binding.spCategory?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val data =  parent?.getItemAtPosition(position) as categoryModel
                catID = data.ID
            }

        }
        detailImageAdapter= DetailImageAdapter(this,imageList)
        binding.rvImages.layoutManager=LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        binding.rvImages.adapter=detailImageAdapter
        detailImageAdapter.ondeleteImage(this)

        loadCategory()
        binding.imgBack.setOnClickListener {
            finish()
        }

        binding.btnSend.setOnClickListener {

//            if(TextUtils.isEmpty(binding.edtTitle.text)){
//                showToast("Please enter title")
//            }else if(TextUtils.isEmpty(binding.edtBody.text)){
//            showToast("Please enter body")
//            }
            if(categoryList.size==1){
                showToast("Please select any category")
            }else if(imageList.size==0)showToast("Please select any image")
            else{
                /*for(i in imageList){
                    val imagefile = File(i.toString())
                    var fis: FileInputStream? = null
                    try {
                        fis = FileInputStream(imagefile)
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    }
                    val selectedImage = BitmapFactory.decodeStream(fis)
                    val encodedImage = encodeImage(selectedImage)
                    encodeimageList.add(encodedImage.toString())
                }


                val map = HashMap<String,String>()
                map.put("user_id",appPreferences.user.userID)
                map.put("title",binding.edtTitle.text.toString())
                map.put("body",binding.edtBody.text.toString())
                map.put("files[]",encodeimageList.toString())
                map.put("category_id",catID)
*/

                val list = ArrayList<MultipartBody.Part>()
                for (i in imageList) {
                    i.let {
                        if (!TextUtils.isEmpty(it)) {
                            val file: File = File(it.toString())
                            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                            list.add(MultipartBody.Part.createFormData("files[]", file.name,requestFile ))
                        }
                    }
                }
                viewModel.sendImages("Bearer "+appPreferences.user.auth_key,
                         appPreferences.user.userID.toRequestBody("text/plain".toMediaTypeOrNull()),
                        binding.edtTitle.text.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                            binding.edtBody.text.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                                catID.toRequestBody("text/plain".toMediaTypeOrNull()),
                        list).observe(this, androidx.lifecycle.Observer {
                    it.let {
                        when (it.status) {
                            Status.LOADING -> {
                                showLoading(this@SendDocumentActivity)
                            }
                            Status.SUCCESS -> {
                                hideLoading()
                                showToast(it?.data!!.message)
                                finish()
                            }
                            Status.ERROR -> {
                                hideLoading()
                                showToast(it?.data!!.message.toString())
                            }
                            else -> {
                                hideLoading()
                            }
                        }

                    }
                })

            }
            }


        getContentFile =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                    result -> onActivityResult(99, result)
            }
      //  documentScanner!!.startScan()
    }

    override fun onResume() {
        super.onResume()
        binding.rlImageUpload.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED){
                ActivityCompat.requestPermissions(this,  arrayOf(Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE), 101)
            }else openPickerDialog()
          //  documentScanner?.startScan()
        }
        binding.btnAddMore.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED){
                ActivityCompat.requestPermissions(this,  arrayOf(Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE), 101)
            }else openPickerDialog()
        }
    }


    private fun encodeImage(bm: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 50, baos)
        val b = baos.toByteArray()
        return android.util.Base64.encodeToString(b, android.util.Base64.DEFAULT)
    }


    //Image Related Stuff
    private val openGalleryForImageRequest =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                Log.d("CHECK FILE -->", "CALLEDD")

                if (it.data?.clipData != null) {
                    var count = it.data!!.clipData?.itemCount

                    for (i in 0 until count!!) {
                        var imageUri: Uri = it.data!!.clipData!!.getItemAt(i).uri

                        var bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                        filePath = saveImage(bitmap).toString()
                        imageList.add(filePath.toString())
                        detailImageAdapter.notifyDataSetChanged()
                        binding.rvImages.visibility= View.VISIBLE
                        binding.rlImageUpload.visibility=View.GONE
                        binding.btnAddMore.visibility=View.VISIBLE
                        //     iv_image.setImageURI(imageUri) Here you can assign your Image URI to the ImageViews
                    }

                }
else {
                    it.data?.data?.let { path ->
                        val uriPath = URIPathHelper()
                        Log.d("CHECK FILE -->", uriPath.getPath(this, path).toString())
                        // filePath = uriPath.getPath(requireActivity(), path).toString()
                        var bitmap =
                            MediaStore.Images.Media.getBitmap(this.getContentResolver(), path);
                        filePath = saveImage(bitmap).toString()
                        imageList.add(path.toString())
                        detailImageAdapter.notifyDataSetChanged()
                        binding.rvImages.visibility= View.VISIBLE
                        binding.rlImageUpload.visibility=View.GONE
                        binding.btnAddMore.visibility=View.VISIBLE
                    }
                        //        binding.imgUser.setImageBitmap(bitmap)
                }
            }
        }

    //Camera Picker
    private val openCameraImageRequest =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {

                it.data?.extras?.let { path ->
                    /*val uriPath = URIPathHelper()
                    path.get("data")
                 */

                    filePath = saveImage(path.get("data") as Bitmap).toString()

                    imageList.add(filePath.toString())
                    detailImageAdapter.notifyDataSetChanged()
                    binding.rvImages.visibility= View.VISIBLE
                    binding.rlImageUpload.visibility=View.GONE
                    binding.btnAddMore.visibility=View.VISIBLE


           //    Thread().run {
                     //   performCrop(FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider.profile", File(filePath)))


               //     }


                    //     binding.imgUser.setImageBitmap(path.get("data") as Bitmap)

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

    private lateinit var getContentFile: ActivityResultLauncher<Intent>
    private fun startScan(preference: Int) {
        val intent = Intent(this, ScanActivity::class.java)
        intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference)
        getContentFile.launch(intent)
    }


    private fun openPickerDialog() {
        val builder = AlertDialog.Builder(this);
        builder.setTitle("GTR");
        //builder.setCancelable(false);
   0     // add a list
        var animals = arrayOf("CAMERA", "GALLERY","FILES","OCR")
        builder.setItems(animals, object : DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p1: Int) {
                when (p1) {
                    0 -> {
                       // capturePhoto()

                        startScan(ScanConstants.OPEN_CAMERA)


                    /*lifecycleScope.launchWhenResumed {
                           documentScanner.startScan()
                       }*/
                    //documentScanner?.startScan()
                    }
                    1 -> {
                        startScan(ScanConstants.OPEN_MEDIA)
                       // openGalleryForImage()
                    }
                    2 -> {
                      //  startScan(ScanConstants.)
                        selectPdf()
                    }
                    3->{
                        startActivity(Intent(this@SendDocumentActivity,OCRActivity::class.java))
                    }
                }
            }

        })

        // create and show the alert dialog
        val dialog = builder.create()
        dialog.show();
    }


    private fun onActivityResult(requestCode: Int, result: ActivityResult) {
        if (requestCode == 99 && result.resultCode == RESULT_OK) {
            val uri = result.data?.extras?.getParcelable<Uri>(ScanConstants.SCANNED_RESULT)
            var bitmap: Bitmap? = null
            try {
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                contentResolver.delete(uri!!, null, null)
                //scannedImage.setImageBitmap(bitmap)
                filePath = saveImage(bitmap).toString()

                imageList.add(filePath.toString())
                detailImageAdapter.notifyDataSetChanged()
                binding.rvImages.visibility= View.VISIBLE
                binding.rlImageUpload.visibility=View.GONE
                binding.btnAddMore.visibility=View.VISIBLE


            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }




    //Pick Image
    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        openGalleryForImageRequest.launch(intent)
    }



    //Camera
    fun capturePhoto() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        openCameraImageRequest.launch(cameraIntent)
    }

    private fun selectPdf() {
        // External Storage Path
        val externalStorage = FileUtils.getFile(applicationContext, null)

        MaterialFilePicker()
            // Pass a source of context. Can be:
            //    .withActivity(Activity activity)
            //    .withFragment(Fragment fragment)
            //    .withSupportFragment(androidx.fragment.app.Fragment fragment)
            .withActivity(this)
            // With cross icon on the right side of toolbar for closing picker straight away
            .withCloseMenu(true)
            // Entry point path (user will start from it)
            //.withPath(alarmsFolder.absolutePath)
            // Root path (user won't be able to come higher than it)
            .withRootPath(externalStorage.absolutePath)
            // Showing hidden files
            .withHiddenFiles(true)
            // Want to choose only jpg images
            //.withFilter(Pattern.compile(".*\\\\..*\\\\.pdf"))
            // Don't apply filter to directories names
            .withFilterDirectories(true)
            .withTitle("GTR")
            // Require "Activity Result" API
            .withActivityResultApi(openPDFRequest)
            .start()
        //startActivityForResult(intent, 1)
       /// openPDFRequest.launch(intent)
    }

    private val openPDFRequest =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                Log.d("PDF FILE PATH IS ---",  it?.data!!.getStringExtra(FilePickerActivity.RESULT_FILE_PATH).toString())

                imageList.add( it?.data!!.getStringExtra(FilePickerActivity.RESULT_FILE_PATH).toString())
                detailImageAdapter.notifyDataSetChanged()
                binding.rvImages.visibility= View.VISIBLE
                binding.rlImageUpload.visibility=View.GONE
                binding.btnAddMore.visibility=View.VISIBLE

            }
        }

   /* private val documentScanner = DocumentScanner(
        this@SendDocumentActivity,
        { croppedImageResults ->
            // display the first cropped image
           *//* croppedImageView.setImageBitmap(
                BitmapFactory.decodeFile(croppedImageResults.first())
            )*//*


            filePath = saveImage(BitmapFactory.decodeFile(croppedImageResults.first())).toString()

            imageList.add(filePath.toString())
            detailImageAdapter.notifyDataSetChanged()
            binding.rvImages.visibility= View.VISIBLE
            binding.rlImageUpload.visibility=View.GONE
            binding.btnAddMore.visibility=View.VISIBLE


        },
        {
            // an error happened
                errorMessage -> Log.v("documentscannerlogs", errorMessage)
        },
        {
            // user canceled document scan
            Log.v("documentscannerlogs", "User canceled document scan")
        }
    )*/

    fun loadCategory(){
        //load Chat
        CoroutineScope(Dispatchers.Main).launch {

            viewModel.loadCategory("Bearer " + appPreferences.user.auth_key,appPreferences.user.userID).observe(this@SendDocumentActivity,
                androidx.lifecycle.Observer {
                    it.let {
                        when (it.status) {
                            Status.LOADING -> {
                                showLoading(this@SendDocumentActivity)
                            }
                            Status.SUCCESS -> {
                                hideLoading()
                                categoryList.clear()
                                categoryList.addAll(it.data?.chatMessage!!.messageList)
                                val def = categoryModel()
                                def.ID="0"
                                def.Name = "Select Category"
                                def.icon = ""
                                def.unread_messages = "0"
                                categoryList.add(0,def)
                                arrayAdapter.notifyDataSetChanged()
                            }
                            Status.ERROR -> {
                                hideLoading()
                                showToast(it.message)
                            }
                            else -> {
                                hideLoading()
                            }
                        }
                    }
                })
        }
    }

    override fun onClick(model: String,pos:Int) {
                if(imageList.size==1){
                    imageList.clear()
                    binding.rvImages.visibility= View.GONE
                    binding.rlImageUpload.visibility=View.VISIBLE
                    binding.btnAddMore.visibility=View.GONE
                }
                else if(imageList.size>0) {
                    imageList.removeAt(pos)
                }
        detailImageAdapter.notifyDataSetChanged()
    }


}