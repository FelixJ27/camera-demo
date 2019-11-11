package com.wisdom.myapplication

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.provider.MediaStore
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Base64
import android.view.View
import java.io.*

class MainActivity : AppCompatActivity() {
    private lateinit var btnPhotograph: Button
    private lateinit var btnVideo: Button
    private lateinit var btnPhotoAlbum: Button
    private lateinit var btnCancel: Button
    private val REQUEST_CAMERA = 1
    private lateinit var mFilePath: String
    private var bitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mFilePath = Environment.getExternalStorageDirectory().path // 获取SD卡路径
        mFilePath = "$mFilePath/temp.png"// 指定路径

        btnPhotograph = findViewById(R.id.btnPhotograph)
        btnVideo = findViewById(R.id.btnVideo)
        btnPhotoAlbum = findViewById(R.id.btnPhotoAlbum)
        btnCancel = findViewById(R.id.btnCancel)

        btnPhotograph.setOnClickListener {
            //open(btnPhotograph)
            toAddPhotoActivity()
        }

        btnVideo.setOnClickListener {
            toAddVideoActivity()
        }

        btnPhotoAlbum.setOnClickListener {

        }
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
        if (resultCode == RESULT_OK) { // 如果返回数据 
            if (requestCode == REQUEST_CAMERA) {
                var fis: FileInputStream? = null
                try {
                    fis = FileInputStream(mFilePath) // 根据路径获取数据
                    bitmap = BitmapFactory.decodeStream(fis)    //获取图片
                    //bitmap = ImageThumbnail().getImageThumbnail(mFilePath, 50, 50)
                    Thread(Runnable { intentBitmap() }).start()
                    val intent = Intent(this@MainActivity, AddPhotoActivity::class.java)
                    startActivity(intent)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                } finally {
                    try {
                        fis!!.close()// 关闭流
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
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
}
