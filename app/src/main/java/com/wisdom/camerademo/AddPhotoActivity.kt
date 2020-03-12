package com.wisdom.camerademo

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Dialog
import android.app.ProgressDialog
import android.content.ContentUris
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import util.CameraUtil
import util.SharedPreferencesUtils
import util.UploadFile
import util.UploadUtil
import java.io.*
import java.util.HashMap
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

/**
 *@description: 添加照片Activity
 *@author: Felix J
 *@time: 2019/11/7 11:34
 */
class AddPhotoActivity : AppCompatActivity(), UploadUtil.OnUploadProcessListener {

    private lateinit var img1: ImageView
    private lateinit var img2: ImageView
    private lateinit var img3: ImageView
    private lateinit var img4: ImageView
    private lateinit var img5: ImageView
    private lateinit var img6: ImageView
    private lateinit var bitmap: Bitmap
    private lateinit var mFilePath: String
    private lateinit var progressBar: ProgressBar
    private lateinit var btnSubmit: Button
    private val imageList: ArrayList<ImageView> = arrayListOf()
    private lateinit var cameraDialog: Dialog
    private var photoIndex = 0
    private lateinit var uploadImageResult: TextView
    private lateinit var progressDialog: ProgressDialog
    private var picPath: String? = null
    private val handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                TO_UPLOAD_FILE -> toUploadFile()
                UPLOAD_INIT_PROCESS -> progressBar.max = msg.arg1
                UPLOAD_IN_PROCESS -> progressBar.progress = msg.arg1
                UPLOAD_FILE_DONE -> {
                    val result =
                        "响应码：" + msg.arg1 + "\n响应信息：" + msg.obj + "\n耗时：" + UploadUtil.getRequestTime() + "秒"
                    uploadImageResult.text = result
                }
                else -> {
                }
            }
            super.handleMessage(msg)
        }
    }

    companion object {
        /**
         * 打开相册
         */
        private const val REQUEST_ALBUM = 0
        /**
         * 调用相机
         */
        private const val REQUEST_CAMERA = 1
        /**
         * 照片详情
         */
        private const val REQUEST_PHOTO_DETAIL = 2

        /**
         * 去上传文件
         */
        protected const val TO_UPLOAD_FILE = 1
        /**
         * 上传文件响应
         */
        protected const val UPLOAD_FILE_DONE = 2  //
        /**
         * 选择文件
         */
        val TO_SELECT_PHOTO = 3
        /**
         * 上传初始化
         */
        private const val UPLOAD_INIT_PROCESS = 4
        /**
         * 上传中
         */
        private const val UPLOAD_IN_PROCESS = 5
        /***
         * 这里的这个URL是我服务器的javaEE环境URL
         */
        private const val requestURL =
            "http://192.168.1.108:8081/wms/services/PdaRestService/uploadFile"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)

        val index = intent.getIntExtra("index", 0)
        val totalCount = intent.getStringExtra("totalCount")
        //val totalCount = intent.getIntExtra("totalCount", 0)
        img1 = findViewById(R.id.img1)
        img2 = findViewById(R.id.img2)
        img3 = findViewById(R.id.img3)
        img4 = findViewById(R.id.img4)
        img5 = findViewById(R.id.img5)
        img6 = findViewById(R.id.img6)
        uploadImageResult = findViewById(R.id.uploadImageResult)
        progressBar = findViewById(R.id.progressBar)
        btnSubmit = findViewById(R.id.btnSubmit)
        progressDialog = ProgressDialog(this)

        val mLeftIv = findViewById<ImageView>(R.id.mLeftIv)
        mLeftIv.setOnClickListener {
            startActivity(Intent(this@AddPhotoActivity, MainActivity::class.java))
            finish()
        }
        //1、使用Dialog、设置style
        cameraDialog = Dialog(this, R.style.AlertDialogWindow)
        //2、设置布局
        val view = View.inflate(this, R.layout.alertdialog_choose_resources, null)
        cameraDialog.setContentView(view)
        val cameraWindow = cameraDialog.window
        cameraWindow!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        cameraWindow.setGravity(Gravity.BOTTOM)
        //设置对话框大小
        cameraWindow.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val imgList: ArrayList<ImageView> = arrayListOf(img1, img2, img3, img4, img5, img6)
        for (i in 0 until imgList.size step 1) {
            imageList.add(imgList[i])
        }

        if (index != 0) {
            delPicture(index, totalCount)
        }

        for (i in 0 until imageList.size step 1) {
            imageList[i].setOnClickListener {
                if (imageList[i].drawable != null) {
                    if (imageList[i].drawable.current.constantState == resources.getDrawable(R.drawable.icon_add).constantState) {
                        cameraDialog.show()
                        val btnTakePhoto = cameraDialog.findViewById<Button>(R.id.btnTakePhoto)
                        val btnFromAlbum = cameraDialog.findViewById<Button>(R.id.btnFromAlbum)
                        val btnCancel = cameraDialog.findViewById<Button>(R.id.btnCancel)
                        btnTakePhoto.setOnClickListener {
                            //Environment.getExternalStorageDirectory().path // 获取SD卡路径
                            val file =
                                File(Environment.getExternalStorageDirectory().path + "/Sany")
                            if (!file.exists()) {
                                file.mkdir()
                            }
                            mFilePath = file.path
                            mFilePath = "$mFilePath/temp_$photoIndex.jpg"// 指定路径
                            val photoUri = Uri.fromFile(File(mFilePath)) // 传递路径 
                            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)// 更改系统默认存储路径  
                            startActivityForResult(intent, REQUEST_CAMERA)
                            cameraDialog.dismiss()
                        }
                        btnFromAlbum.setOnClickListener {
                            //打开相册
                            val intent = Intent(Intent.ACTION_GET_CONTENT)
                            //Intent.ACTION_GET_CONTENT = "android.intent.action.GET_CONTENT"
                            intent.type = "image/*"
                            startActivityForResult(intent, REQUEST_ALBUM)
                            cameraDialog.dismiss()
                        }
                        btnCancel.setOnClickListener {
                            cameraDialog.dismiss()
                        }
                    } else {
                        toEveryActivity(index = i + 1)
                    }
                }
            }
        }

        btnSubmit.setOnClickListener {
            if (photoIndex != 0) {
                handler.sendEmptyMessage(TO_UPLOAD_FILE)
                //toUploadFile2()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) { // 如果返回数据 
            when (requestCode) {
                REQUEST_CAMERA -> {
                    bitmap = CameraUtil.getImageThumbnail(mFilePath, 80, 80)
                    add(bitmap)
                }
                REQUEST_ALBUM -> {
                    if (Build.VERSION.SDK_INT >= 19) {
                        // 4.4及以上系统使用这个方法处理图片
                        handleImageOnKitKat(data!!)
                    } else {
                        // 4.4以下系统使用这个方法处理图片
                        handleImageBeforeKitKat(data!!)
                    }
                }
            }
        }
    }

    override fun onUploadDone(responseCode: Int, message: String?) {
        progressDialog.dismiss()
        val msg = Message.obtain()
        msg.what = UPLOAD_FILE_DONE
        msg.arg1 = responseCode
        msg.obj = message
        handler.sendMessage(msg)
    }

    override fun onUploadProcess(uploadSize: Int) {
        val msg = Message.obtain()
        msg.what = UPLOAD_IN_PROCESS
        msg.arg1 = uploadSize
        handler.sendMessage(msg)
    }

    override fun initUpload(fileSize: Int) {
        val msg = Message.obtain()
        msg.what = UPLOAD_INIT_PROCESS
        msg.arg1 = fileSize
        handler.sendMessage(msg)
    }

    private fun toUploadFile() {
        uploadImageResult.text = "正在上传中..."
        progressDialog.setMessage("正在上传文件...")
        //progressDialog.show()
        val fileKey = "pic"
        val uploadUtil = UploadUtil.getInstance()
        uploadUtil.setOnUploadProcessListener(this)  //设置监听器监听上传状态
        val params = HashMap<String, String>()
        params["orderId"] = "11111"
        //val uploadParam = UploadParam(orderNum = "000")

        for (i in 0 until photoIndex step 1) {

            picPath = Environment.getExternalStorageDirectory().path + "/Sany/temp_" + i + ".jpg"
            /*uploadUtil.uploadFile(
                picPath, fileKey,
                requestURL, params,
                this
            )*/
            val file = File(picPath)
            UploadFile.toUploadFile(file)
        }
    }

    /**
     * @description 跳转照片详情页面
     * @author Felix J
     * @time 2019/11/8 10:12
     */
    private fun toEveryActivity(index: Int) {
        val intent = Intent(this@AddPhotoActivity, EveryPictureActivity::class.java)
        intent.putExtra("index", index)
        //intent.putExtra("totalCount", photoIndex)
        var filePath = Environment.getExternalStorageDirectory().path + "/Sany"
        filePath = filePath + "/temp_" + (index - 1) + ".jpg"
        intent.putExtra("filePath", filePath)
        //startActivity(intent)
        startActivityForResult(intent, REQUEST_PHOTO_DETAIL)
    }

    @TargetApi(19)
    private fun handleImageOnKitKat(data: Intent) {
        var imagePath: String? = null
        val uri = data.data
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            val docId = DocumentsContract.getDocumentId(uri)
            if ("com.android.providers.media.documents" == uri!!.authority) {
                val id =
                    docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
                // 解析出数字格式的id
                val selection = MediaStore.Images.Media._ID + "=" + id
                imagePath =
                    getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection)
            } else if ("com.android.providers.downloads.documents" == uri.authority) {
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content: //downloads/public_downloads"),
                    java.lang.Long.valueOf(docId)
                )
                imagePath = getImagePath(contentUri, null)
            }
        } else if ("content".equals(uri!!.scheme, ignoreCase = true)) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.path
        }
        // 根据图片路径显示图片
        displayImage(imagePath)
    }

    /**
     * android 4.4以前的处理方式
     * @param data
     */
    private fun handleImageBeforeKitKat(data: Intent) {
        val uri = data.data
        val imagePath = getImagePath(uri!!, null)
        displayImage(imagePath)
    }

    private fun getImagePath(uri: Uri, selection: String?): String? {
        var path: String? = null
        // 通过Uri和selection来获取真实的图片路径
        val cursor = contentResolver.query(uri, null, selection, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            }
            cursor.close()
        }
        return path
    }

    private fun displayImage(imagePath: String?) {
        if (imagePath != null) {
            //val bitmap = BitmapFactory.decodeFile(imagePath)
            val bitmap = CameraUtil.getImageThumbnail(imagePath, 80, 80)
            //imageView!!.setImageBitmap(bitmap)
            photoIndex = 0
            for (i in 0 until imageList.size step 1) {
                photoIndex++
                if (imageList[i].drawable.current.constantState == resources.getDrawable(R.drawable.icon_add).constantState) {
                    photoIndex -= 1
                    break
                }
            }
            val destFilePath =
                Environment.getExternalStorageDirectory().path + "/Sany/temp_" + photoIndex + ".jpg"
            CameraUtil.copyFile(imagePath, destFilePath)
            add(bitmap)
        } else {
            Toast.makeText(this, "获取相册图片失败", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * @description 添加照片
     * @author Felix J
     * @time 2019/11/8 10:11
     */
    private fun add(bitmap: Bitmap) {
        photoIndex = 0
        for (i in 0 until imageList.size step 1) {
            photoIndex++
            if (imageList[i].drawable.current.constantState == resources.getDrawable(R.drawable.icon_add).constantState) {
                imageList[i].setImageBitmap(bitmap)
                if (i != imageList.size - 1) {
                    imageList[i + 1].setImageResource(R.drawable.icon_add)
                    break
                }
            }
        }
        SharedPreferencesUtils.put(this, "totalCount", photoIndex.toString())
    }

    /**
     * @description 删除照片
     * @author Felix J
     * @time 2019/11/14 14:49
     */
    private fun delPicture(index: Int, totalCount: String) {
        val filePath = Environment.getExternalStorageDirectory().path + "/Sany"
        for (i in 0 until index step 1) {
            val mFilePath = "$filePath/temp_$i.jpg"
            CameraUtil.setPicture(mFilePath, imageList[i], 2)
        }
        for (i in index - 1 until totalCount.toInt() step 1) {
            val srcFilePath = filePath + "/temp_" + (i + 1) + ".jpg"
            val destFilePath = "$filePath/temp_$i.jpg"
            CameraUtil.copyFile(srcFilePath, destFilePath)
            CameraUtil.setPicture(destFilePath, imageList[i], 2)
        }
        imageList[totalCount.toInt() - 1].setImageResource(R.drawable.icon_add)
        photoIndex = SharedPreferencesUtils.getString(this, "totalCount", "0").toInt()
        photoIndex--
        SharedPreferencesUtils.put(this, "totalCount", photoIndex.toString())
        /*if (index != imageList.size) {
            imageList[index].setImageDrawable(null)
        }*/
    }
}
