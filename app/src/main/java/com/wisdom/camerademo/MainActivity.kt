package com.wisdom.camerademo

import android.Manifest
import android.annotation.TargetApi
import android.content.ContentUris
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.provider.MediaStore
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.util.Base64
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.*


class MainActivity : AppCompatActivity() {
    private lateinit var btnPhotograph: Button
    private lateinit var btnVideo: Button
    //private lateinit var btnPhotoAlbum: Button
    private lateinit var btnCancel: Button
    private val REQUEST_CAMERA = 1
    private val SELECT_PHOTO = 0
    private lateinit var mFilePath: String
    private var bitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mFilePath = Environment.getExternalStorageDirectory().path // 获取SD卡路径
        mFilePath = "$mFilePath/temp.png"// 指定路径

        btnPhotograph = findViewById(R.id.btnPhotograph)
        btnVideo = findViewById(R.id.btnVideo)
        //btnPhotoAlbum = findViewById(R.id.btnPhotoAlbum)
        btnCancel = findViewById(R.id.btnCancel)

        btnPhotograph.setOnClickListener {
            //open(btnPhotograph)
            toAddPhotoActivity()
        }

        btnVideo.setOnClickListener {
            toAddVideoActivity()
        }

        /*btnPhotoAlbum.setOnClickListener {
            selectFromAlbum()
        }*/
    }

    private fun toAddPhotoActivity() {
        startActivity(Intent(this@MainActivity, AddPhotoActivity::class.java))
    }

    private fun toAddVideoActivity() {
        startActivity(Intent(this@MainActivity, AddVideoActivity::class.java))
    }

    private fun open(view: View) {
        var intent = Intent()
        when (view.id) {
            R.id.btnPhotograph -> {
                intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)// 启动系统相机  
            }

            R.id.btnVideo -> {
                intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)// 启动系统相机  
            }
        }
        val photoUri = Uri.fromFile(File(mFilePath)) // 传递路径  
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)// 更改系统默认存储路径  
        startActivityForResult(intent, REQUEST_CAMERA)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            SELECT_PHOTO -> if (resultCode == RESULT_OK) { // 判断手机系统版本号
                if (Build.VERSION.SDK_INT >= 19) {
                    // 4.4及以上系统使用这个方法处理图片
                    handleImageOnKitKat(data!!)
                } else {
                    // 4.4以下系统使用这个方法处理图片
                    handleImageBeforeKitKat(data!!)
                }
            }
            else -> {
            }
        }
    }

    @TargetApi(19)
    private fun handleImageOnKitKat(data: Intent) {
        var imagePath: String? = null
        val uri = data.getData()
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            val docId = DocumentsContract.getDocumentId(uri)
            if ("com.android.providers.media.documents" == uri!!.getAuthority()) {
                val id =
                    docId.split(":".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()[1]
                // 解析出数字格式的id
                val selection = MediaStore.Images.Media._ID + "=" + id
                imagePath =
                    getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection)
            } else if ("com.android.providers.downloads.documents" == uri!!.getAuthority()) {
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content: //downloads/public_downloads"),
                    java.lang.Long.valueOf(docId)
                )
                imagePath = getImagePath(contentUri, null)
            }
        } else if ("content".equals(uri!!.getScheme(), ignoreCase = true)) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null)
        } else if ("file".equals(uri.getScheme(), ignoreCase = true)) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath()
        }
        // 根据图片路径显示图片
        displayImage(imagePath)
    }

    /**
     * android 4.4以前的处理方式
     * @param data
     */
    private fun handleImageBeforeKitKat(data: Intent) {
        val uri = data.getData()
        val imagePath = getImagePath(uri!!, null)
        displayImage(imagePath)
    }

    private fun getImagePath(uri: Uri, selection: String?): String? {
        var path: String? = null
        // 通过Uri和selection来获取真实的图片路径
        val cursor = getContentResolver().query(uri, null, selection, null, null)
        if (cursor != null) {
            if (cursor!!.moveToFirst()) {
                path = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Images.Media.DATA))
            }
            cursor!!.close()
        }
        return path
    }

    private fun displayImage(imagePath: String?) {
        if (imagePath != null) {
            val bitmap = BitmapFactory.decodeFile(imagePath)
            //imageView!!.setImageBitmap(bitmap)
        } else {
            Toast.makeText(this, "获取相册图片失败", Toast.LENGTH_SHORT).show()
        }
    }


    //传递bitmap
    private fun intentBitmap() {
        //把Bitmap转码成字符串
        val baos = ByteArrayOutputStream()
        //压缩图片大小
        bitmap?.compress(Bitmap.CompressFormat.PNG, 50, baos)
        val imageBase64 = String(Base64.encode(baos.toByteArray(), 0))
        //把字符串存到SharedPreferences里面
        val prePicture = getSharedPreferences("Picture", Context.MODE_PRIVATE)
        val editor = prePicture.edit()
        editor.putString("cameraImage", imageBase64)
        editor.commit()
    }

    /**
     * @description 从相册中获取图片
     * @author Felix J
     * @time 2019/11/12 16:02
     */
    private fun selectFromAlbum() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                1
            )
        } else {
            openAlbum()
        }
    }

    /**
     * @description 打开相册
     * @author Felix J
     * @time 2019/11/12 16:02
     */
    private fun openAlbum() {
        val intent = Intent("android.intent.action.GET_CONTENT")
        intent.type = "image/*"
        startActivityForResult(intent,SELECT_PHOTO)
    }
}
