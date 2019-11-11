package com.wisdom.myapplication

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.widget.ImageView
import android.widget.TextView
import java.io.ByteArrayInputStream

/**
 *@description:
 *@author: Felix J
 *@time: 2019/11/7 11:34
 */
class EveryoneActivity : AppCompatActivity() {

    private lateinit var img: ImageView
    private lateinit var imgDel: ImageView
    private lateinit var txtIndex: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_everyone)

        val index = intent.getIntExtra("index", 0)
        val totalCount = intent.getIntExtra("totalCount", 0)
        //val bitmap:Bitmap = intent.getParcelableExtra("bitmap")
        imgDel = findViewById(R.id.mRightIv)
        imgDel.setImageResource(R.drawable.icon_rubbish)

        txtIndex = findViewById(R.id.mTitleTv)
        txtIndex.text = "$index/$totalCount"
        img = findViewById(R.id.img)
        val delete = findViewById<ImageView>(R.id.mRightIv)
        delete.setOnClickListener {
            //toAddPhotoActivity()
        }
        getPicture()
        //img.setImageBitmap(bitmap)
    }

    private fun getPicture() {
        //获取字符串
        val sPreferences = getSharedPreferences("everyPicture", Context.MODE_PRIVATE)
        val imageBase64 = sPreferences.getString("everyImage", "")
        //把字符串解码成Bitmap对象
        val byte64 = Base64.decode(imageBase64, 0)
        val bais = ByteArrayInputStream(byte64)
        val bitmap = BitmapFactory.decodeStream(bais)
        //显示图片
        img.setImageBitmap(bitmap)
    }

    private fun returnToAddPhotoActivity() {
        val intent = Intent(this@EveryoneActivity, AddPhotoActivity::class.java)
        startActivity(intent)
    }
}
