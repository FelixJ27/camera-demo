package com.wisdom.myapplication

import android.content.Context
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.widget.ImageView
import java.io.ByteArrayInputStream

class EveryoneActivity : AppCompatActivity() {

    private lateinit var img: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_everyone)
        img = findViewById(R.id.img)
        val delete = findViewById<ImageView>(R.id.mRightIv)
        delete.setOnClickListener {

        }
        getPicture()
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
        img.setImageBitmap(bitmap)
    }
}
