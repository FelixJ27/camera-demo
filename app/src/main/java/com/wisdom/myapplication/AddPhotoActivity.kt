package com.wisdom.myapplication

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.widget.ImageView
import java.io.*
import java.lang.RuntimeException

/**
 *@description: 添加照片Activity
 *@author: Felix J
 *@time: 2019/11/7 11:34
 */
class AddPhotoActivity : AppCompatActivity() {

    private lateinit var img1: ImageView
    private lateinit var img2: ImageView
    private lateinit var img3: ImageView
    private lateinit var img4: ImageView
    private lateinit var img5: ImageView
    private lateinit var img6: ImageView
    private lateinit var bitmap: Bitmap
    private lateinit var mFilePath: String
    val imageList: ArrayList<ImageView> = arrayListOf()
    private val REQUEST_CAMERA = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)

        mFilePath = Environment.getExternalStorageDirectory().path // 获取SD卡路径
        mFilePath = "$mFilePath/temp.png"// 指定路径

        img1 = findViewById(R.id.img1)
        img2 = findViewById(R.id.img2)
        img3 = findViewById(R.id.img3)
        img4 = findViewById(R.id.img4)
        img5 = findViewById(R.id.img5)
        img6 = findViewById(R.id.img6)

        //getPicture()

        val photoUri = Uri.fromFile(File(mFilePath)) // 传递路径  
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)// 更改系统默认存储路径  

        val imgList: ArrayList<ImageView> = arrayListOf(img1, img2, img3, img4, img5, img6)
        for (i in 0 until imgList.size step 1) {
            imageList.add(imgList[i])
        }
        for (i in 0 until imageList.size step 1) {
            imageList[i].setOnClickListener {
                if (imageList[i].drawable != null) {
                    if (imageList[i].drawable.current.constantState == resources.getDrawable(R.drawable.icon_add).constantState) {
                        startActivityForResult(intent, REQUEST_CAMERA)
                    } else {
                        toEveryActivity(index = i + 1)
                    }
                }
            }
        }
    }

    private fun getPicture() {
        //获取字符串
        val sPreferences = getSharedPreferences("Picture", Context.MODE_PRIVATE)
        val imageBase64 = sPreferences.getString("cameraImage", "")
        //把字符串解码成Bitmap对象
        val byte64 = Base64.decode(imageBase64, 0)
        val bais = ByteArrayInputStream(byte64)
        val bitmap = BitmapFactory.decodeStream(bais)
        //显示图片
        img1.setImageBitmap(bitmap)
    }

    /**
     * @description 跳转照片详情页面
     * @author Felix J
     * @time 2019/11/8 10:12
     */
    private fun toEveryActivity(index: Int) {
        val intent = Intent(this@AddPhotoActivity, EveryoneActivity::class.java)
        intentBitmap()
        //BitmapFactory.decodeResource(resources, R.drawable)
        intent.putExtra("index", index)
        var totalCount = 0
        for (i in 0 until imageList.size step 1) {
            if (imageList[i].drawable != null
                && imageList[i].drawable.current.constantState != resources.getDrawable(R.drawable.icon_add).constantState
            ) {
                totalCount++
            }
        }
        intent.putExtra("totalCount", totalCount)
        startActivity(intent)
    }

    //传递bitmap
    fun intentBitmap() {
        //把Bitmap转码成字符串
        val baos = ByteArrayOutputStream()
        //压缩图片大小
        bitmap?.compress(Bitmap.CompressFormat.PNG, 0, baos)
        val imageBase64 = String(Base64.encode(baos.toByteArray(), 0))
        //把字符串存到SharedPreferences里面
        val prePicture = getSharedPreferences("everyPicture", Context.MODE_PRIVATE)
        val editor = prePicture.edit()
        editor.putString("everyImage", imageBase64)
        editor.commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) { // 如果返回数据 
            if (requestCode == REQUEST_CAMERA) {
                var fis: FileInputStream? = null
                try {
                    fis = FileInputStream(mFilePath) // 根据路径获取数据
                    bitmap = BitmapFactory.decodeStream(fis)    //获取图片
                    //生成缩略图
                    //bitmap = ImageThumbnail().getImageThumbnail(mFilePath, 50, 50)
                    add(bitmap)
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

    /**
     * @description 添加照片
     * @author Felix J
     * @time 2019/11/8 10:11
     */
    private fun add(bitmap: Bitmap) {
        for (i in 0 until imageList.size step 1) {
            if (imageList[i].drawable.current.constantState == resources.getDrawable(R.drawable.icon_add).constantState) {
                imageList[i].setImageBitmap(bitmap)
                if (i != imageList.size - 1) {
                    imageList[i + 1].setImageResource(R.drawable.icon_add)
                    break
                }
            }
        }
    }
}
